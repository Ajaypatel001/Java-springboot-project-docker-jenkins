package com.datastore.person.controller;

import com.datastore.person.pojo.Student;
import com.datastore.person.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentRepository studentRepository;

    // CREATE STUDENT
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student) {

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Student name cannot be empty");
        }

        if (studentRepository.findByName(student.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Student already exists");
        }

        studentRepository.save(student);
        logger.info("Created student : {}", student.getName());

        return ResponseEntity.ok(student);
    }

    // GET STUDENT BY NAME
    @GetMapping("/{name}")
    public ResponseEntity<?> getStudent(@PathVariable String name) {

        logger.info("Fetching student : {}", name);

        return studentRepository.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET ALL STUDENTS
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {

        logger.info("Fetching all students");

        return ResponseEntity.ok(studentRepository.findAll());
    }

    // UPDATE STUDENT
    @PutMapping("/{name}")
    public ResponseEntity<?> updateStudent(@PathVariable String name,
                                           @RequestBody Student updatedStudent) {

        return studentRepository.findByName(name)
                .map(student -> {
                    student.setName(updatedStudent.getName());
                    student.setAge(updatedStudent.getAge());
                    studentRepository.save(student);

                    logger.info("Updated student {} -> {}", name, updatedStudent.getName());

                    return ResponseEntity.ok(student);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE STUDENT
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteStudent(@PathVariable String name) {

        return studentRepository.findByName(name)
                .map(student -> {
                    studentRepository.delete(student);
                    logger.info("Deleted student : {}", name);
                    return ResponseEntity.ok("Student deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
