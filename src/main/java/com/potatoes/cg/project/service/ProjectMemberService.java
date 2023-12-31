package com.potatoes.cg.project.service;

import com.potatoes.cg.jwt.CustomUser;
import com.potatoes.cg.member.domain.Member;

import com.potatoes.cg.member.domain.MemberInfo;
import com.potatoes.cg.member.domain.repository.InfoRepository;
import com.potatoes.cg.project.domain.Project;
import com.potatoes.cg.project.domain.ProjectParticipant;
import com.potatoes.cg.project.domain.ProjectParticipantId;
import com.potatoes.cg.project.domain.repository.ProjectMemberRepository;
import com.potatoes.cg.project.domain.repository.ProjectParticipantRepository;
import com.potatoes.cg.project.domain.repository.ProjectRepository;
import com.potatoes.cg.project.dto.request.ProjectInviteMemberRequest;
import com.potatoes.cg.project.dto.response.LoginJobInfoResponse;
import com.potatoes.cg.project.dto.response.MemberDeptResponse;
import com.potatoes.cg.project.dto.response.ProjectMemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.potatoes.cg.member.domain.type.MemberStatus.ACTIVE;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final InfoRepository infoRepository;
    private final ProjectParticipantRepository projectParticipantRepository;
    private final ProjectRepository projectRepository;

    private Pageable getPageable(final Integer page) {
//        return PageRequest.of(page - 1, 5, Sort.by("memberInfo").descending());
        return PageRequest.of(page - 1, 5);
    }

    /* 부서별 회원 조회 */
//    @Transactional(readOnly = true)
//    public List<MemberDeptResponse> getDeptMember(final Long deptCode) {
//
//        List<Member> members = projectMemberRepository.findByMemberInfoDeptDeptCodeAndMemberStatus(deptCode, ACTIVE);
//
//        List<MemberDeptResponse> projectMemberResponseList = members.stream()
//                .map(member -> MemberDeptResponse.from(
//                        member.getMemberInfo(),
//                        member.getMemberInfo().getInfoName(),
//                        member.getMemberInfo().getDept().getDeptCode(),
//                        member.getMemberInfo().getDept().getDeptName()
//
//                )).collect(Collectors.toList());
//
//        return projectMemberResponseList;
//    }

    @Transactional(readOnly = true)
    public List<MemberDeptResponse> getDeptMember(final Long deptCode, final Long projectCode) {

        // 해당 프로젝트의 참석자들을 조회
//        List<ProjectParticipant> projectParticipants = projectParticipantRepository.findAllByProjectProjectCode(projectCode);
      List<ProjectParticipant> projectParticipants = projectParticipantRepository.findParticipantsByProjectProjectCode(projectCode);

        // 부서별 회원 조회
        List<Member> members = projectMemberRepository.findByMemberInfoDeptDeptCodeAndMemberStatus(deptCode, ACTIVE);

        // 해당 프로젝트에 참석자로 등록되지 않은 회원만 필터링
        List<MemberDeptResponse> projectMemberResponseList = members.stream()
                .filter(member -> projectParticipants.stream()
                        .noneMatch(participant -> participant.getMember().getInfoCode().equals(member.getMemberInfo().getInfoCode())))
                .map(member -> MemberDeptResponse.from(
                        member.getMemberInfo(),
                        member.getMemberInfo().getInfoName(),
                        member.getMemberInfo().getDept().getDeptCode(),
                        member.getMemberInfo().getDept().getDeptName()
                ))
                .collect(Collectors.toList());

        return projectMemberResponseList;
    }

    /* 부서별 회원 검색*/
    public List<MemberDeptResponse> getDeptSearch(Long deptCode, String infoName, Long projectCode) {

        List<ProjectParticipant> projectParticipants = projectParticipantRepository.findParticipantsByProjectProjectCode(projectCode);

        List<Member> members = projectMemberRepository.findByMemberInfoDeptDeptCodeAndMemberInfoInfoNameContainsAndMemberStatus(deptCode, infoName, ACTIVE);

        List<MemberDeptResponse> projectMemberResponseList = members.stream()
                .filter(member -> projectParticipants.stream()
                        .noneMatch(participant -> participant.getMember().getInfoCode().equals(member.getMemberInfo().getInfoCode())))
                .map(member -> MemberDeptResponse.from(
                        member.getMemberInfo(),
                        member.getMemberInfo().getInfoName(),
                        member.getMemberInfo().getDept().getDeptCode(),
                        member.getMemberInfo().getDept().getDeptName()
                ))
                .collect(Collectors.toList());

        return projectMemberResponseList;
    }

    /* 프로젝트에 회원 초대 */
    public List<ProjectParticipantId> inviteMembers(List<ProjectInviteMemberRequest> projectInviteMemberRequests) {

        List<ProjectParticipantId> invitedMembers = new ArrayList<>();

        for (ProjectInviteMemberRequest request : projectInviteMemberRequests) {
            MemberInfo member = infoRepository.getReferenceById(request.getMemberCode());

            Project project = projectRepository.getReferenceById(request.getProjectCode());

            final ProjectParticipant newProjectParticipant = ProjectParticipant.of(
                    project,
                    member
            );

            final ProjectParticipant projectParticipant = projectParticipantRepository.save(newProjectParticipant);
            invitedMembers.add(projectParticipant.getId());
        }

        return invitedMembers;
    }

    //    public ProjectParticipantId inviteMember(ProjectInviteMemberRequest projectInviteMemberRequest) {
//
//        MemberInfo member = infoRepository.findById(projectInviteMemberRequest.getMemberCode())
//                .orElseThrow(() -> new NotFoundException(NOT_FOUND_INFO_CODE));
//
//        Project project = projectRepository.findById(projectInviteMemberRequest.getProjectCode())
//                .orElseThrow(() -> new NotFoundException(NOT_PROJECT_CODE));
//
//        final ProjectParticipant newProjectParticipant = ProjectParticipant.of(
//                project,
//                member
//        );
//
//        final ProjectParticipant projectParticipant = projectParticipantRepository.save(newProjectParticipant);
//
//        return projectParticipant.getId();
//    }

    /* 프로젝트 번호로 인원 조회 */
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMemberList(Long projectCode) {

        List<ProjectParticipant> projectParticipantList = projectParticipantRepository.findAllByProjectProjectCode(projectCode);

        List<ProjectMemberResponse> projectMemberResponseList = projectParticipantList.stream()
                .map(projectParticipant -> ProjectMemberResponse.from(
                        projectParticipant.getMember().getInfoCode(),
                        projectParticipant.getMember().getInfoName()
                )).collect(Collectors.toList());

        return projectMemberResponseList;
    }

    @Transactional(readOnly = true)
    /* 회원 검색 (초대) */
    public Page<ProjectMemberResponse> getInviteMemberSearch(Integer page, String infoName) {

        Page<Member> members = projectMemberRepository.findByMemberInfoInfoNameContainsAndMemberStatus(getPageable(page), infoName, ACTIVE);

        return members.map(Member -> ProjectMemberResponse.fromMember(Member));

    }

    /* 로그인 정보 */
    @Transactional(readOnly = true)
    public LoginJobInfoResponse loginInfo(CustomUser customUser) {

        Member member = projectMemberRepository.findByMemberInfoInfoCode(customUser.getInfoCode());

        return LoginJobInfoResponse.from(member);
    }
}
