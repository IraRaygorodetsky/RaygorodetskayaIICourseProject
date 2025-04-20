package ru.tbank.myservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateStudentRequestDTO {
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private Integer course;
    private String fraction;
    private String faculty;
    private String department;
}