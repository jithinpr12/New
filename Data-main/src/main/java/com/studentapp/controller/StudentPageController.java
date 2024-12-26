package com.studentapp.controller;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.studentapp.model.Student;
import com.studentapp.service.StudentService;

@Controller
public class StudentPageController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/students/import")
    public String uploadPage() {
        return "student"; // Refers to student.html
    }

    @PostMapping("/student/upload")
    public String uploadCsvFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            studentService.importFromCsv(file);
            model.addAttribute("message", "File uploaded successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Error uploading file: " + e.getMessage());
        }
        return "student";
    }

    @GetMapping("/student")
    public String showStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "student_form"; // Refers to student_form.html
    }

    @PostMapping("/student")
    public String saveStudent(@ModelAttribute Student student, Model model) {
        try {
            studentService.saveStudent(student);
            model.addAttribute("message", "Student saved successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Error saving student: " + e.getMessage());
        }
        return "student_form";
    }
    
    @GetMapping("/students/id")
    public String showStudentForm() {
        return "studentForm"; // Refers to studentForm.html
    }

    @PostMapping("/students/get")
    public String getStudentById(@RequestParam("id") Long id, Model model) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            model.addAttribute("student", student.get());
        } else {
            model.addAttribute("error", "Student not found with ID: " + id);
        }
        return "studentDetails";

}    
 @GetMapping("/students/export")
    public ResponseEntity<?> exportStudentData() {
        try {
            List<Student> students = studentService.getAllStudents();

            StringBuilder csvContent = new StringBuilder();
            csvContent.append("ID,Name,Email\n");
            for (Student student : students) {
                csvContent.append(student.getId())
                          .append(",")
                          .append(student.getName())
                          .append(",")
                          .append(student.getEmail())
                          .append("\n");
            }

            byte[] csvBytes = csvContent.toString().getBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csvBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error exporting student data: " + e.getMessage());
        }
    }
}

