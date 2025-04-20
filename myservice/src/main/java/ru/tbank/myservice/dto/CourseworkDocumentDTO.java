package ru.tbank.myservice.dto;

import lombok.Data;

@Data
public class CourseworkDocumentDTO {
    private String department;
    private Integer course;
    private String fraction;
    private String title;
    private String faculty;
    private String last_name;
    private String first_name;
    private String middle_name;
    private String phone;
    private String email;
}
