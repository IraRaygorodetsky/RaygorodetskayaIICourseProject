package ru.tbank.myservice.mapper;

import org.mapstruct.Mapper;
import ru.tbank.myservice.dto.CreateStudentRequestDTO;
import ru.tbank.myservice.dto.GetStudentResponseDTO;
import ru.tbank.myservice.dto.UpdateStudentRequestDTO;
import ru.tbank.myservice.entity.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student createStudent(CreateStudentRequestDTO student);
    GetStudentResponseDTO getStudent(Student student);
    Student updateStudent(UpdateStudentRequestDTO student);
}