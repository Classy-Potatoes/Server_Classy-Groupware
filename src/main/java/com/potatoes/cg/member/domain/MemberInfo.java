package com.potatoes.cg.member.domain;

import com.potatoes.cg.member.domain.type.MemberInfoStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import java.util.List;

import static com.potatoes.cg.member.domain.type.MemberInfoStatus.NONREGIST;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name="tbl_member_info")
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString
public class MemberInfo {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long infoCode;

    @Column(nullable = false)
    private String infoName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deptCode")
    private Dept dept;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobCode")
    private Job job;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private MemberInfoStatus infoStatus = NONREGIST;

    @Column(nullable = false)
    private String infoEmail;

    private String infoPhone;

    private Long infoZipcode;

    private String infoAddress;

    private String infoAddressAdd;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "infoCode")
    private List<History> history;


    public MemberInfo(String infoName, Dept dept, Job job, String infoEmail, String infoPhone,
                      Long infoZipcode, String infoAddress, String infoAddressAdd, List<History> history) {
        this.infoName = infoName;
        this.dept = dept;
        this.job = job;
        this.infoEmail = infoEmail;
        this.infoPhone = infoPhone;
        this.infoZipcode = infoZipcode;
        this.infoAddress = infoAddress;
        this.infoAddressAdd = infoAddressAdd;
        this.history = history;
    }


    public static MemberInfo of(String infoName, Dept dept, Job job, String infoEmail, String infoPhone,
                                Long infoZipcode, String infoAddress, String infoAddressAdd, List<History> history) {

        return new MemberInfo(
                infoName,
                dept,
                job,
                infoEmail,
                infoPhone,
                infoZipcode,
                infoAddress,
                infoAddressAdd,
                history
        );

    }

    public void update(MemberInfoStatus infoStatus, String infoEmail, String infoPhone, Long infoZipcode,
                       String infoAddress, String infoAddressAdd) {

        this.infoStatus = infoStatus;
        this.infoEmail = infoEmail;
        this.infoPhone = infoPhone;
        this.infoZipcode = infoZipcode;
        this.infoAddress = infoAddress;
        this.infoAddressAdd = infoAddressAdd;
    }

    public void update2(String infoEmail, String infoPhone, String infoName, Dept dept, Job job, Long infoZipcode,
                       String infoAddress, String infoAddressAdd) {

        this.infoEmail = infoEmail;
        this.infoPhone = infoPhone;
        this.infoName = infoName;
        this.dept = dept;
        this.job = job;
        this.infoZipcode = infoZipcode;
        this.infoAddress = infoAddress;
        this.infoAddressAdd = infoAddressAdd;
    }

}
