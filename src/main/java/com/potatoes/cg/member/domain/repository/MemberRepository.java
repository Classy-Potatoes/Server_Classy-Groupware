package com.potatoes.cg.member.domain.repository;

import com.potatoes.cg.member.domain.History;
import com.potatoes.cg.member.domain.Member;
import com.potatoes.cg.member.domain.MemberInfo;
import com.potatoes.cg.member.domain.type.MemberInfoStatus;
import com.potatoes.cg.member.domain.type.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findByMemberInfoInfoCodeAndMemberInfoInfoName( Long infoCode, String infoName );
    Optional<Member> findByMemberInfoInfoCodeAndMemberId( Long infoCode, String memberId );

    /* 아이디 중복검사 */
    Boolean existsByMemberId( String memberId );

    /* 아이디찾기(로그인쪽) */
    Optional<Member> findByMemberId( String memberId );

    /* DB에서 refreshToken 조회 */
    Optional<Member> findByMemberToken( String refreshToken );

    /* 회원 목록 조회(연락망) */
    Page<Member> findByMemberStatus( Pageable pageable, MemberStatus memberStatus );

    /* 회원 목록 조회(연락망, search) */
    Page<Member> findByMemberInfoInfoNameContainsAndMemberStatus(Pageable pageable, String infoName, MemberStatus memberStatus);

    /* 미분류 회원 목록 조회(search) */
    Page<Member> findByMemberInfoInfoNameContains(Pageable pageable, String infoName);

    /* 프로젝트 참석자 전달 */
    List<Member> findAllByMemberInfoIn(List<MemberInfo> infos);

    Member findByMemberInfoInfoCode(Long code);

    Member findByMemberInfo(MemberInfo info);
}
