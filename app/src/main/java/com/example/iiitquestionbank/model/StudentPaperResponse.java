package com.example.iiitquestionbank.model;

import java.io.Serializable;

public class StudentPaperResponse implements Serializable {
    private Long id;
    private String title;
    private Integer semester;
    private ExaminationType examinationType;
    private Integer academicYear;
    private String branch;
    private String uploadedAt;

    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public Integer getSemester() {
        return semester;
    }
    public ExaminationType getExaminationType() {
        return examinationType;
    }
    public Integer getAcademicYear() {
        return academicYear;
    }
    public String getBranch() {
        return branch;
    }
    public String getUploadedAt() {
        return uploadedAt;
    }
}
