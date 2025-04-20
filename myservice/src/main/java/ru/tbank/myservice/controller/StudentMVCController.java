package ru.tbank.myservice.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tbank.myservice.dto.CourseworkDocumentDTO;
import ru.tbank.myservice.dto.CreateStudentRequestDTO;
import ru.tbank.myservice.dto.GetStudentResponseDTO;
import ru.tbank.myservice.dto.UpdateStudentRequestDTO;
import ru.tbank.myservice.service.DocumentService;
import ru.tbank.myservice.service.StudentService;


import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Hidden
@Controller
@RequestMapping("/students-mvc")
@RequiredArgsConstructor
public class StudentMVCController {

    private final StudentService studentService;
    private final DocumentService documentService;

    @GetMapping
    public String getAllStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "list";
    }

    @GetMapping("/new")
    public String createStudentForm(Model model) {
        model.addAttribute("student", new CreateStudentRequestDTO());
        return "create";
    }

    @PostMapping
    public String createStudent(@ModelAttribute CreateStudentRequestDTO student) {
        studentService.createStudent(student);
        return "redirect:/students-mvc";
    }

    @GetMapping("/{id}")
    public String getStudentById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "detail";
    }

    @GetMapping("/{id}/edit")
    public String updateStudentForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "edit";
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable("id") Long id, @ModelAttribute UpdateStudentRequestDTO student) {
        studentService.updateStudent(id, student);
        return "redirect:/students-mvc";
    }

    @PostMapping("/{id}/delete")
    public String deleteStudentById(@PathVariable("id") Long id) {
        studentService.deleteStudentById(id);
        return "redirect:/students-mvc";
    }

    @GetMapping("/{id}/document")
    public void downloadCourseworkDocument(@PathVariable("id") Long id,
                                           @RequestParam String title,
                                           HttpServletResponse response) throws IOException {
        GetStudentResponseDTO student = studentService.getStudentById(id);
        byte[] docBytes = documentService.generateCourseworkDoc(student, title);

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=coursework.docx");
        response.getOutputStream().write(docBytes);
        response.getOutputStream().flush();
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