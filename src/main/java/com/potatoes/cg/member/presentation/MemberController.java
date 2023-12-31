package com.potatoes.cg.member.presentation;

import com.potatoes.cg.common.paging.Pagenation;
import com.potatoes.cg.common.paging.PagingButtonInfo;
import com.potatoes.cg.common.paging.PagingResponse;
import com.potatoes.cg.jwt.CustomUser;
import com.potatoes.cg.member.dto.request.*;
import com.potatoes.cg.member.dto.MailSendDTO;
import com.potatoes.cg.member.dto.response.*;
import com.potatoes.cg.member.service.MemberService;
import com.potatoes.cg.member.service.SendEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping({"/cg-api/v1", "/cg-api/v1/member"})
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final SendEmailService sendEmailService;



    /* 사번 검증 */
    @PostMapping("/non/member/info/search")
    public ResponseEntity<SearchInfoResponse> infoSearch( @RequestBody @Valid final InfoCodeCheckRequest request ) {

        SearchInfoResponse searchInfoResponse = memberService.infoSearch( request );

        return ResponseEntity.ok( searchInfoResponse );
    }

    /* 계정 가입 */
    @PostMapping("/non/member/regist")
    public ResponseEntity<Void> regist( @RequestPart @Valid final MemberSignupRequest memberRequest,
                                       @RequestPart final MultipartFile profileImg ) {

        memberService.regist( memberRequest, profileImg );

        return ResponseEntity.status( HttpStatus.CREATED ).build();
    }

    /* 아이디 찾기 */
    @PostMapping("/non/member/search")
    public ResponseEntity<MemberResponse> searchId( @RequestBody @Valid final MemberIdCheckRequest request ) {

        MemberResponse memberResponse = memberService.searchId( request );

        return ResponseEntity.ok( memberResponse );
    }

    /* 아이디 중복 검사 */
    @PostMapping("/non/member/duplicateId")
    public Boolean duplicateId( @RequestBody @Valid final MemberDuplicateIdRequest request ) {

        return memberService.duplicateId( request );
    }

    /* 비밀번호 찾기(이메일 전송) */
    @PostMapping("/non/member/pwdSearch/sendEmail")
    public @ResponseBody void sendEmail( @RequestBody @Valid final MemberSearchPwdRequest request ) {

        sendEmailService.createMailAndChangePwd( request );
    }

    /* ----------------------- 마이페이지, 로그인 후 -------------------------- */

    /* 비밀번호 변경(마이페이지) */
    @PutMapping("/member/pwdUpdate")
    public Boolean pwdUpdate(@RequestBody final MemberPwdRequest pwdSearchRequest,
                                          @AuthenticationPrincipal CustomUser customUser) {

        return memberService.pwdUpdate( pwdSearchRequest, customUser.getMemberCode() );
    }


    /* 계정 반납 */
    @PutMapping("/member/returnUser")
    public ResponseEntity<Void> returnUser( @AuthenticationPrincipal CustomUser customUser) {

        memberService.returnUser( customUser );

        return ResponseEntity.created( URI.create("/") ).build();
    }


    /* 회원상세 조회(마이페이지) */
    @GetMapping("/member/myProfile")
    public ResponseEntity<ProfileResponse> profile( @AuthenticationPrincipal CustomUser customUser ) {

        ProfileResponse profileResponse = memberService.getProfile( customUser );

        return ResponseEntity.ok( profileResponse );
    }


    /* 회원상세 수정(마이페이지) */
    @PutMapping("/member/myProfileUpdate")
    public ResponseEntity<Void> profileUpdate(@RequestPart @Valid final MemberUpdateRequest memberUpdateRequest,
                                              @RequestPart(required = false) final MultipartFile profileImage,
                                              @AuthenticationPrincipal CustomUser customUser ) {

        memberService.profileUpdate( memberUpdateRequest, profileImage, customUser );

        return ResponseEntity.created( URI.create( "/" ) ).build();
    }


    /* 회원 목록 조회(연락망) */
    @GetMapping("/members/list")
    public ResponseEntity<PagingResponse> memberList( @RequestParam(defaultValue = "1") final Integer page ) {

        final Page<MembersResponse> memberList = memberService.memberList( page );
        final PagingButtonInfo pagingButtonInfo = Pagenation.getPagingButtonInfo( memberList );
        final PagingResponse pagingResponse = PagingResponse.of( memberList.getContent(), pagingButtonInfo );

        return ResponseEntity.ok( pagingResponse );
    }

    /* 회원 목록 조회(연락망, search) */
    @GetMapping("/members/list/search")
    public ResponseEntity<PagingResponse> memberListByInfoName(@RequestParam final String infoName,
                                                               @RequestParam(defaultValue = "1") final Integer page ) {

        final Page<MembersResponse> memberList = memberService.memberListByInfoName( page, infoName);
        final PagingButtonInfo pagingButtonInfo = Pagenation.getPagingButtonInfo( memberList );
        final PagingResponse pagingResponse = PagingResponse.of( memberList.getContent(), pagingButtonInfo );

        return ResponseEntity.ok( pagingResponse );
    }

    /* 회원 상세 조회(연락망) */
    @GetMapping("/members/list/{memberCode}")
    public ResponseEntity<ProfileResponse> getCustomNetworkMember(@PathVariable final Long memberCode ) {

        final ProfileResponse profileResponse = memberService.getCustomNetworkMember( memberCode );

        return ResponseEntity.ok( profileResponse );
    }



    /* ----------------------- 부서, 직급 조회 -------------------------- */


    /* 부서 리스트 조회 */
    @GetMapping("/deptList")
    public ResponseEntity<List<DeptResponse>> deptList() {

        final List<DeptResponse> deptList = memberService.deptList();

        return ResponseEntity.ok( deptList );
    }

    /* 직급 리스트 조회 */
    @GetMapping("/jobList")
    public ResponseEntity<List<JobResponse>> jobList() {

        final List<JobResponse> jobList = memberService.jobList();

        return ResponseEntity.ok( jobList );
    }


}
