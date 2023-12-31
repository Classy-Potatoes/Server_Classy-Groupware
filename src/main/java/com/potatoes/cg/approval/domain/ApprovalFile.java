package com.potatoes.cg.approval.domain;

import com.potatoes.cg.approval.domain.type.ApprovalFileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import static com.potatoes.cg.approval.domain.type.ApprovalFileType.APPROVAL;
import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "tbl_file")
@NoArgsConstructor
@Getter
public class ApprovalFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileCode;

    @Column
    private String fileName;
    @Column
    private String filePathName;
    @Column
    private String fileSaveName;
    @Column
    private String fileExtName;

    @Column(name = "fileType")
    @Enumerated(value = STRING)
    private ApprovalFileType approvalFileType;


    public ApprovalFile(String replaceFileName, String filePathName, String randomName, String fileExtension) {
        this.fileName = replaceFileName;
        this.filePathName = filePathName;
        this.fileSaveName = randomName;
        this.fileExtName = fileExtension;
        this.approvalFileType = APPROVAL;

    }
}
