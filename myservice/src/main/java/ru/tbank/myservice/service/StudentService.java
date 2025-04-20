package ru.tbank.myservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.myservice.dto.CreateStudentRequestDTO;
import ru.tbank.myservice.dto.GetStudentResponseDTO;
import ru.tbank.myservice.dto.UpdateStudentRequestDTO;
import ru.tbank.myservice.entity.Student;
import ru.tbank.myservice.mapper.StudentMapper;
import ru.tbank.myservice.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public List<GetStudentResponseDTO> getAllStudents() {
        List<Student> entities = studentRepository.findAll();
        return entities.stream().map(studentMapper::getStudent).toList();
    }

    public GetStudentResponseDTO createStudent(CreateStudentRequestDTO student) {
        Student entity = studentMapper.createStudent(student);
        entity = studentRepository.save(entity);
        return studentMapper.getStudent(entity);
    }

    public GetStudentResponseDTO getStudentById(Long id) {
        Student entity = studentRepository.getReferenceById(id);
        return studentMapper.getStudent(entity);
    }

    public GetStudentResponseDTO updateStudent(Long id, UpdateStudentRequestDTO student) {
        Student entity = studentMapper.updateStudent(student);
        entity.setId(id);
        entity = studentRepository.save(entity);
        return studentMapper.getStudent(entity);
    }

    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }
}