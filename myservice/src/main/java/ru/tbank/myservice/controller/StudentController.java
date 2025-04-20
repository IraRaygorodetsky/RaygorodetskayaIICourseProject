package ru.tbank.myservice.controller;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.tbank.myservice.dto.CreateStudentRequestDTO;
import ru.tbank.myservice.dto.GetStudentResponseDTO;
import ru.tbank.myservice.dto.UpdateStudentRequestDTO;
import ru.tbank.myservice.service.DocumentService;
import ru.tbank.myservice.service.StudentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final DocumentService documentService;

    @GetMapping
    public List<GetStudentResponseDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping
    public GetStudentResponseDTO createStudent(CreateStudentRequestDTO student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/{id}")
    public GetStudentResponseDTO getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public GetStudentResponseDTO updateStudent(@PathVariable Long id, UpdateStudentRequestDTO student) {
        return studentService.updateStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudentById(@PathVariable Long id) {
        studentService.deleteStudentById(id);
    }

    @GetMapping(value = "/{id}/document", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> downloadCourseworkDocument(@PathVariable("id") Long id,
                                                             @RequestParam String title) throws IOException {
        GetStudentResponseDTO student = studentService.getStudentById(id);
        byte[] docBytes = documentService.generateCourseworkDoc(student, title);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("coursework.docx").build());

        return new ResponseEntity<>(docBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/menu")
    public String showMenu() {
        return "menu";
    }

    @GetMapping("/statistics")
    public String getStudentStatistics(Model model) {
        List<GetStudentResponseDTO> students = studentService.getAllStudents();

        int total = students.size();
        double averageCourse = students.stream()
                .mapToInt(GetStudentResponseDTO::getCourse)
                .average().orElse(0);

        Optional<GetStudentResponseDTO> youngest = students.stream()
                .min(Comparator.comparing(GetStudentResponseDTO::getBirthDate));

        Optional<GetStudentResponseDTO> oldest = students.stream()
                .max(Comparator.comparing(GetStudentResponseDTO::getBirthDate));

        Map<String, Long> facultyCount = students.stream()
                .collect(Collectors.groupingBy(GetStudentResponseDTO::getFaculty, Collectors.counting()));

        model.addAttribute("total", total);
        model.addAttribute("averageCourse", averageCourse);
        model.addAttribute("youngest", youngest.orElse(null));
        model.addAttribute("oldest", oldest.orElse(null));
        model.addAttribute("facultyCount", facultyCount);

        return "statistics";
    }

}