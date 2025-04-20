package ru.tbank.myservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.tbank.myservice.dto.GetStudentResponseDTO;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService {

    public byte[] generateCourseworkDoc(GetStudentResponseDTO student, String title) throws IOException {
        InputStream templateStream = new ClassPathResource("coursework_template.docx").getInputStream();
        XWPFDocument document = new XWPFDocument(templateStream);

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceParagraphPlaceholders(paragraph, student, title);
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceParagraphPlaceholders(paragraph, student, title);
                    }
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        return outputStream.toByteArray();
    }

    private void replaceParagraphPlaceholders(XWPFParagraph paragraph, GetStudentResponseDTO student, String title) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("${title}", title);
        placeholders.put("${first_name}", student.getFirstName());
        placeholders.put("${last_name}", student.getLastName());
        placeholders.put("${middle_name}", student.getMiddleName());
        placeholders.put("${phone}", student.getPhone());
        placeholders.put("${email}", student.getEmail());
        placeholders.put("${course}", String.valueOf(student.getCourse()));
        placeholders.put("${fraction}", student.getFraction());
        placeholders.put("${faculty}", student.getFaculty());
        placeholders.put("${department}", student.getDepartment());

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) {
                fullText.append(text);
            }
        }

        String replaced = fullText.toString();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }

        for (int i = runs.size() - 1; i >= 1; i--) {
            paragraph.removeRun(i);
        }
        runs.get(0).setText(replaced, 0);
    }
}
