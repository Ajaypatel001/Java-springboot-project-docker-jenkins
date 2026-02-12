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
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentRepository studentRepository;

    // CREATE STUDENT
    @PostMapping("/student/post")
    public ResponseEntity<String> postStudent(@RequestBody Student student) {
        studentRepository.save(student);
        logger.info("Posted student to DB : {}", student.getName());
        return ResponseEntity.ok("Student successfully posted.");
    }

    // GET STUDENT BY NAME
    @GetMapping("/student/get/{name}")
    public ResponseEntity<Student> getStudent(@PathVariable String name) {

        logger.info("Getting student by name : {}", name);

        return studentRepository.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET ALL STUDENTS
    @GetMapping("/student/all")
    public ResponseEntity<List<Student>> getAllStudents() {

        logger.info("Getting all students");

        return ResponseEntity.ok(studentRepository.findAll());
    }

    // DELETE STUDENT
    @DeleteMapping("/student/delete/{name}")
    public ResponseEntity<String> deleteStudent(@PathVariable String name) {

        return studentRepository.findByName(name)
                .map(student -> {
                    studentRepository.delete(student);
                    logger.info("Deleted student : {}", name);
                    return ResponseEntity.ok("Student deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE STUDENT
    @PutMapping("/student/update/{name}")
    public ResponseEntity<String> updateStudent(@PathVariable String name,
                                                @RequestBody Student updatedStudent) {

        return studentRepository.findByName(name)
                .map(student -> {
                    student.setName(updatedStudent.getName());
                    student.setAge(updatedStudent.getAge());
                    studentRepository.save(student);

                    logger.info("Updated student : {}", name);
                    return ResponseEntity.ok("Student updated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
