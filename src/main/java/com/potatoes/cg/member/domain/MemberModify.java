package com.potatoes.cg.member.domain;

import com.potatoes.cg.member.domain.type.MemberRole;
import com.potatoes.cg.member.domain.type.MemberStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.potatoes.cg.member.domain.type.MemberRole.USER;
import static com.potatoes.cg.member.domain.type.MemberStatus.ACTIVE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name="tbl_member")
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MemberModify {
    /* 수정용 Member 엔티티 */

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long memberCode;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String memberPassword;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private MemberStatus memberStatus = ACTIVE;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private MemberRole memberRole = USER;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime memberJoinDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime memberUpdateDate;

    private String memberToken;

    @Column(nullable = false)
    private Long infoCode;


    public MemberModify(String memberId, String memberPassword, Long infoCode) {
        this.memberId = memberId;
        this.memberPassword = memberPassword;
        this.infoCode = infoCode;
    }


    // 넘어온 값으로 새로운 엔티티를 만들어서 반환해라
    public static MemberModify of(String memberId, String memberPassword, Long infoCode) {

        return new MemberModify(
                memberId,
                memberPassword,
                infoCode
        );

    }


}
