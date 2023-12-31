package com.potatoes.cg.approval.domain;

import com.potatoes.cg.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_reference")
@Getter
@NoArgsConstructor
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long referenceCode;

    private Long approvalCode;

    private Long memberCode;


//    public Reference(Member member) {
//        this.member = member;
//    }

    public Reference(Long memberCode) {
        this.memberCode = memberCode;
    }
    public static Reference of(Long memberCode) {

        return new Reference(memberCode);

    }
}
