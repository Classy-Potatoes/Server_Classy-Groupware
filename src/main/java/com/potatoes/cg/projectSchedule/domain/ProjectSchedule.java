package com.potatoes.cg.projectSchedule.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.potatoes.cg.calendar.domain.type.StatusType;
import com.potatoes.cg.member.domain.Member;
import com.potatoes.cg.project.domain.Project;
import com.potatoes.cg.project.domain.ProjectReply;
import com.potatoes.cg.projectManagers.domain.ProjectManagersSchedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.potatoes.cg.calendar.domain.type.StatusType.PROGRESS;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "tbl_project_schedule")
@NoArgsConstructor(access = PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE tbl_project_schedule SET schedule_status = 'DELETED' WHERE schedule_code = ?")
public class ProjectSchedule {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long scheduleCode;

    @Column(nullable = false)
    private String scheduleTitle;

    @Column(nullable = false)
    private String scheduleBody;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime scheduleStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime scheduleEndDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime scheduleCreatedDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime scheduleModifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberCode")
    private Member member;
//    @Column
//    private int memberCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectCode")
    private Project project;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private StatusType scheduleStatus = PROGRESS;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleCode")
    private List<ProjectManagersSchedule> projectManagerList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "scheduleCode")
    private List<ProjectReply> replies;

    public ProjectSchedule(String scheduleTitle, String scheduleBody, LocalDateTime scheduleStartDate, LocalDateTime scheduleEndDate, Member member, Project project, List<ProjectManagersSchedule> projectManagerList) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleBody = scheduleBody;
        this.scheduleStartDate = scheduleStartDate;
        this.scheduleEndDate = scheduleEndDate;
        this.member = member;
        this.project = project;
        this.projectManagerList = projectManagerList;
    }

    public static ProjectSchedule of(String scheduleTitle, String scheduleContent, LocalDateTime scheduleStartedDate, LocalDateTime scheduleEndDate, Member member, Project project, List<ProjectManagersSchedule> projectManagerList) {
        return new ProjectSchedule(
                scheduleTitle,
                scheduleContent,
                scheduleStartedDate,
                scheduleEndDate,
                member,
                project,
                projectManagerList
                );
    }

    public void update(String scheduleTitle, String scheduleContent, LocalDateTime scheduleStartedDate, LocalDateTime scheduleEndDate, Project project, List<ProjectManagersSchedule> attendants) {
        this.scheduleTitle = scheduleTitle;
        this.scheduleBody = scheduleContent;
        this.scheduleStartDate = scheduleStartedDate;
        this.scheduleEndDate = scheduleEndDate;
        this.project = project;
        this.projectManagerList = attendants;
    }
}

