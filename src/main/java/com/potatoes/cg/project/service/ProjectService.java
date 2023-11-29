package com.potatoes.cg.project.service;


import com.potatoes.cg.common.exception.NotFoundException;
import com.potatoes.cg.member.domain.Dept;
import com.potatoes.cg.member.domain.Member;
import com.potatoes.cg.project.domain.Project;
import com.potatoes.cg.project.domain.ProjectParticipant;
import com.potatoes.cg.project.domain.repository.DeptRepository;
import com.potatoes.cg.project.domain.repository.ProjectParticipantRepository;
import com.potatoes.cg.project.domain.repository.memberRepository;
import com.potatoes.cg.project.domain.repository.ProjectRepository;
import com.potatoes.cg.project.dto.request.ProjectCreateRequest;
import com.potatoes.cg.project.dto.response.ProjectResponse;
import com.potatoes.cg.project.dto.response.ProjectsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.potatoes.cg.common.exception.type.ExceptionCode.NOT_FOUND_DEPT_CODE;
import static com.potatoes.cg.common.exception.type.ExceptionCode.NOT_FOUND_MEMBER_CODE;
import static com.potatoes.cg.project.domain.type.ProjectStatusType.USABLE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DeptRepository deptRepository;
    private final ProjectParticipantRepository projectParticipantRepository;


    /* 뉴 프로젝트 생성 */
    public Long save(ProjectCreateRequest projectRequest, int memberCode) {


        Dept dept = deptRepository.findById(projectRequest.getDeptCode())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_DEPT_CODE));

        final Project newProject = Project.of(
                projectRequest.getProjectTitle(),
                projectRequest.getProjectBody(),
                projectRequest.getProjectStartDate(),
                projectRequest.getProjectEndDate(),
                dept,
                memberCode
        );

        final Project project = projectRepository.save(newProject);

        return project.getProjectCode();
    }

    /* 내 부서 프로젝트 조회 */

    private Pageable getPageable(final Integer page) {
        return PageRequest.of(page - 1, 5, Sort.by("projectCode").descending());
    }

    @Transactional(readOnly = true)
    public Page<ProjectsResponse> getMyDeptProjects(Integer page, Long deptCode) {

        Page<Project> projects = projectRepository.findByDeptDeptCodeAndProjectStatus(getPageable(page), deptCode, USABLE);

        return projects.map(project -> ProjectsResponse.from(project));
    }


    /* 내가 참여 중인 프로젝트 조회 */
    @Transactional(readOnly = true)
    public Page<ProjectsResponse> getMyProjects(Integer page, Long memberCode) {


        return projectRepository.findMyProjects(getPageable(page), memberCode);
    }

    /* 프로젝트에 참여인원 조회 */
    public long countParticipantsByProjectCode(Long projectCode) {

        return projectParticipantRepository.countParticipantsByProjectCode(projectCode);
    }
}


