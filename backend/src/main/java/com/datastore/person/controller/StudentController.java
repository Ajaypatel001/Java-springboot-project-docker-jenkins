package com.datastore.person.controller;

import com.datastore.person.pojo.Student;
import com.datastore.person.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    StudentRepository studentRepository;

    // CREATE STUDENT
    @RequestMapping(method = RequestMethod.POST, path = "/student/post")
    private ResponseEntity<String> postStudent(@RequestBody Student student, HttpServletRequest request) {
        studentRepository.save(student);
        logger.info("Posted student to DB : {}", student.getName());
        return ResponseEntity.status(HttpStatus.OK).body("Student successfully posted.");
    }

    // GET STUDENT BY NAME
    @RequestMapping(method = RequestMethod.GET, path = "/student/get/{name}")
    private ResponseEntity<Student> getStudent(@PathVariable("name") String name) {
        logger.info("Getting student by name : {}", name);
        return studentRepository.findByName(name)
                .map(student -> ResponseEntity.ok().body(student))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET ALL STUDENTS
    @RequestMapping(method = RequestMethod.GET, path = "/student/all")
    private ResponseEntity<List<Student>> getAllStudents() {
        logger.info("Getting all students");
        List<Student> stuList = studentRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(stuList);
    }

    // DELETE STUDENT
    @RequestMapping(method = RequestMethod.DELETE, path = "/student/delete/{name}")
    private ResponseEntity<String> deleteStudent(@PathVariable("name") String name) {

        logger.info("Deleting student : {}", name);

        return studentRepository.findByName(name)
                .map(student -> {
                    studentRepository.delete(student);
                    return ResponseEntity.ok("Student deleted successfully");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found"));
    }

    // UPDATE STUDENT
    @RequestMapping(method = RequestMethod.PUT, path = "/student/update/{name}")
    private ResponseEntity<String> updateStudent(@PathVariable("name") String name,
                                                 @RequestBody Student updatedStudent) {

        logger.info("Updating student : {}", name);

        return studentRepository.findByName(name)
                .map(student -> {
                    student.setName(updatedStudent.getName());
                    student.setAge(updatedStudent.getAge());
                    studentRepository.save(student);
                    return ResponseEntity.ok("Student updated successfully");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found"));
    }
}
