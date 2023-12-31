package com.potatoes.cg.jwt.service;

import com.potatoes.cg.common.exception.NotFoundException;
import com.potatoes.cg.jwt.CustomUser;
import com.potatoes.cg.member.domain.Member;
import com.potatoes.cg.member.domain.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static com.potatoes.cg.common.exception.type.ExceptionCode.NOT_FOUND_MEMBER_ID;


@Slf4j
@Service
public class JwtService {

    // yml에서 불어온다.
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    private final Key key;

    private final MemberRepository memberRepository;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER = "Bearer ";     // jwt 고유형식. 뒤쪽에 띄어쓰기도 중요

    public JwtService(@Value("${jwt.secret}") String secretKey, MemberRepository memberRepository) {
        // 디코딩해서 Bytes 값으로 변환해서
        byte[] keyBytes = Decoders.BASE64.decode( secretKey );
        this.key = Keys.hmacShaKeyFor( keyBytes );
        this.memberRepository = memberRepository;
    }

    public String createAccessToken(Map<String, String> memberInfo) {

        // Claims 쪽에 키, 밸류 값으로 얼마든지 저장하면 된다.
        // Claims 이란 객체는 페이로드? 라고 생각하면 된다. 원하는 정보를 넣어서 보낼수 있다.
        Claims claims = Jwts.claims().setSubject( ACCESS_TOKEN_SUBJECT );
        claims.putAll( memberInfo );


        return Jwts.builder()
                .setClaims( claims )        // 몸체에 데이터 담고
                // 만료시간 설정 (야물에 넣어두고 불러오기)
                .setExpiration( new Date( System.currentTimeMillis() + accessTokenExpirationPeriod) )
                .signWith( key, SignatureAlgorithm.HS512 )      // 서명을 넣었다.
                .compact();                 // 모아서 access 토큰을 만든다.

    }


    public String createRefreshToken() {

        // access 토큰과 맥락은 비슷하다.
        return Jwts.builder()
                .setSubject( REFRESH_TOKEN_SUBJECT )        // 이쪽에서는 그냥 바로 이름 적어줘도 된다.
                .setExpiration( new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod) )
                .signWith( key, SignatureAlgorithm.HS512 )
                .compact();

    }

    @Transactional
    public void updateRefreshToken( String memberId, String refreshToken ) {

        memberRepository.findByMemberId( memberId )
                .ifPresentOrElse(       // 2가지 옵셔널 처리방법
                        // 만약 있으면 이렇게 해라
                        // Transactional 로 인해 엔티티에 변화가 생기면 자동 업데이트 된다.
                        member -> member.updateRefreshToken( refreshToken ),
                        // 없으면 이렇게 해라
                        () -> new NotFoundException( NOT_FOUND_MEMBER_ID )
                );
    }


    /* 넘어온 refreshToken을 꺼낸다. */
    public Optional<String> getRefreshToken( HttpServletRequest request ) {

        return Optional.ofNullable( request.getHeader( "Refresh-Token" ) )
                // refresh 토큰이 없다면 null 반환, 있다면 문자열 반환. (가공처리)
                // jwt의 앞쪽 형태인 BEARER 인지 필터해봄 (아닐경우 false로 null값을 반환 맞다면 true 문자열 반환)
                .filter( refreshToken -> refreshToken.startsWith( BEARER ) )
                // 들어온 문자열에서 BEARER 을 ""으로 가공
                .map( refreshToken -> refreshToken.replace(BEARER, "") );

    }


    public Optional<String> getAccessToken( HttpServletRequest request ) {


        return Optional.ofNullable( request.getHeader( "Access-Token" ) )
                // Access 토큰이 없다면 null 반환, 있다면 문자열 반환. (가공처리)
                .filter( accessToken -> accessToken.startsWith( BEARER ) )
                .map( accessToken -> accessToken.replace( BEARER, "") );
    }


    // 여기서 String token은 추출된 문자열
    // 토큰의 유효성 검사 하는 메소드
    public boolean isvalidToken( String token ) {

        try {
            // 유효한 토큰인지 확인, 유효하면 true, 아니면 false. exception 발생
            Jwts.parserBuilder().setSigningKey( key ).build().parseClaimsJws( token );
            return true;

        } catch ( Exception e ) {
            // exception 발생시키면서 false를 반환.
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }


    }


    public void checkRefreshTokenAndReIssueAccessToken( HttpServletResponse response, String refreshToken ) {

        memberRepository.findByMemberToken( refreshToken )
                .ifPresent( member -> {
                    // 일치한다면 리프레쉬 토큰을 생성하고 DB에 저장한다.
                    String reIssuedRefreshToken = reIssuedRefreshToken( member );

                    // 일치한다면 새로운 액세스 토큰을 발급
                    String accessToken = createAccessToken(
                            Map.of("memberId", member.getMemberId(), "memberRole", member.getMemberRole().name() )
                    );

                    // 사용자 응답쪽으로 넘긴다.
                    response.setHeader("Access-Token", accessToken );
                    response.setHeader("Refresh-Token", reIssuedRefreshToken );

                });

    }


    private String reIssuedRefreshToken( Member member ) {

        // 새로운 리프래쉬 토큰을 만들었다.
        String reIssuedRefreshToken = createRefreshToken();
        //
        member.updateRefreshToken( reIssuedRefreshToken );
        // 저장해라. Flush 구문 붙은 메소드 사용시 쌓아놓는게 아니라 바로 이 순간 DB로 내보내기 된다.
        memberRepository.saveAndFlush( member );

        return reIssuedRefreshToken;

    }


    public void checkAccessTokenAndAuthentication( HttpServletRequest request,
                                                   HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {

        getAccessToken( request )
                .filter( this::isvalidToken )
                // 유효한지 검사 // 유효하다면,
                .ifPresent( accessToken -> getMemberId( accessToken )
                        .ifPresent( memberId -> memberRepository.findByMemberId( memberId )
                                // ifPresent 있다면? 없다면? 쓸때
                                .ifPresent( this::saveAuthentication ) ) );

        // 다음 필터로 넘어가라
        filterChain.doFilter( request, response );

    }


    // 액세스 토큰을 전달하여 memberId를 꺼내오는 기능
    private Optional<String> getMemberId( String accessToken ) {

        try {
            return Optional.ofNullable(
                    Jwts.parserBuilder()
                            // 파싱처리할수 있는 객체 먼저 소환
                            .setSigningKey( key )
                            // 파싱할때 서명키를 전달
                            .build()
                            // 파싱을 할수 있도록
                            .parseClaimsJws( accessToken )
                            // 파싱해오는 동작
                            .getBody()
                            .get("memberId").toString()
                            // memberId를 꺼내오겠다.
            );

        } catch ( Exception e ) {

            log.info("Access Token이 유효하지 않습니다.");
            return Optional.empty();            // 유효하지 않을 경우 비워서 보낸다.
        }

    }


    public void saveAuthentication( Member member ) {

        UserDetails userDetails = User.builder()
                .username( member.getMemberId() )
                .password( member.getMemberPassword())
                .roles( member.getMemberRole().name())
                .build();


        CustomUser customUser = CustomUser.of( member.getMemberCode(), member.getMemberInfo().getInfoCode(), userDetails);

        // userDetails.getAuthorities() User,ADMIN인지 확인
        // 1. principal(인증주체)
        // 2. credntials(비밀번호)- 로그인처리를 하는게 아니기 때문에 굳이 여기선 필요없다.
        // 3. 인증처리후 인가처리 해야하는데 user인지 admin인지 셋팅
        Authentication authentication
                = new UsernamePasswordAuthenticationToken( customUser, null, userDetails.getAuthorities() );

        // 인증객체를 저장해줘야 하는 형태가 있다.(Authentication - UsernamePasswordAuthenticationToken)
        SecurityContextHolder.getContext().setAuthentication( authentication );

    }



}
