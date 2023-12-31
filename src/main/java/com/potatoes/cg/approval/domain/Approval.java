package com.potatoes.cg.approval.domain;

import com.potatoes.cg.approval.domain.type.approvalType.ApprovalStatusType;
import com.potatoes.cg.approval.domain.type.approvalType.DocumentType;
import com.potatoes.cg.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static com.potatoes.cg.approval.domain.type.approvalType.ApprovalStatusType.WAITING;
import static com.potatoes.cg.approval.domain.type.approvalType.DocumentType.LETTER;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "tbl_approval")
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalCode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime approvalRegistDate;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private ApprovalStatusType approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "memberCode")
    private Member member;

    @Column
    private String approvalTurnbackReason;


    @Column(updatable = false)
    private LocalDateTime approvalApproveDate;


    @Column(updatable = false)
    private LocalDateTime approvalTurnbackDate;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private DocumentType documentType = LETTER;

    @Column(nullable = false)
    private String documentTitle;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "approvalCode")
    private List<ApprovalLine> approvalLine;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "approvalCode")
    private List<Reference> referenceLine;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "approvalCode")
    private List<ApprovalFile> attachment;


    /* 상세 페이지 출력용 */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "approvalCode")
    private List<ApprovalLine_GET> approvalLine_GET;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "approvalCode")
    private List<ReferenceLine_GET> referenceLine_GET;




    public Approval(String documentTitle, List<ApprovalLine> approvalLine, List<Reference> referenceLine,
                    DocumentType documentType, Member member, List<ApprovalFile> attachment) {

        this.documentTitle = documentTitle;
        this.approvalLine = approvalLine;
        this.referenceLine = referenceLine;
        this.documentType = documentType;
        this.member = member;
        this.attachment = attachment;
        this.approvalStatus = WAITING;
    }

    public Approval(String documentTitle, List<Reference> referenceLine,
                    List<ApprovalLine> approvalLine, DocumentType documentType,
                    Member findByLoginmember) {
        this.documentTitle = documentTitle;
        this.referenceLine = referenceLine;
        this.approvalLine = approvalLine;
        this.documentType = documentType;
        this.member = findByLoginmember;
        this.approvalStatus = WAITING;
    }




    public static Approval of(final String documentTitle, final List<Reference> referenceLine, final List<ApprovalLine> approvalLine
            , final DocumentType documentType, final Member member, final List<ApprovalFile> attachment) {

        return new Approval(
                documentTitle,
                approvalLine,
                referenceLine,
                documentType,
                member,
                attachment

        );
    }

    public static Approval from(String documentTitle, List<Reference> referenceLine,
                                List<ApprovalLine> approvalLine, DocumentType documentType,
                                Member findByLoginmember) {
        return new Approval(
                documentTitle,
                referenceLine,
                approvalLine,
                documentType,
                findByLoginmember
        );
    }

    /* test */
    public void signUpdate(String approvalTurnbackReason , List<ApprovalLine> approvalLine) {
        this.approvalTurnbackReason = approvalTurnbackReason;
        this.approvalLine = approvalLine;
    }



    public void setApprovalApproveDate(LocalDateTime now) {
        this.approvalApproveDate = now;
    }


    public void setApprovalStatus(ApprovalStatusType approvalStatusType) {
        this.approvalStatus = approvalStatusType;
    }

    public void setApprovalTurnbackDate(LocalDateTime now) {
        this.approvalTurnbackDate = now;
    }


    public void setApprovalTurnbackReason(String approvalTurnbackReason) {
        this.approvalTurnbackReason = approvalTurnbackReason;
    }


    public void update(String approvalTurnbackReason, LocalDateTime approvalApproveDate,
                       LocalDateTime approvalTurnbackDate, ApprovalStatusType approvalStatusType,
                       List<ApprovalLine> approvalLine) {
        this.approvalTurnbackReason = approvalTurnbackReason;
        this.approvalApproveDate = approvalApproveDate;
        this.approvalTurnbackDate = approvalTurnbackDate;
        this.approvalStatus = approvalStatusType;
        this.approvalLine = approvalLine;
    };





}



