package com.studentapp.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.studentapp.model.Student;
import com.studentapp.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Save a single student
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    // Get a student by ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Update student details
    public Student updateStudent(String email, Student updatedStudent) {
        Optional<Student> existingStudent = studentRepository.findByEmail(email);
        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();
            student.setName(updatedStudent.getName());
            return studentRepository.save(student);
        }
        throw new RuntimeException("Student with email " + email + " not found.");
    }

    // Save a list of students (bulk save)
    public void saveAll(List<Student> students) {
        studentRepository.saveAll(students);
    }
       public void importFromCsv(MultipartFile file) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 3) {
                    Student student = new Student();
                    student.setId(Long.parseLong(fields[0].trim()));
                    student.setName(fields[1].trim());
                    student.setEmail(fields[2].trim());
                    students.add(student);
                }
            }
            studentRepository.saveAll(students);
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage());
        }
    }
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    void deleteStudentById(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
