package com.lwj.springmvc;

import com.lwj.springmvc.dao.StudentDao;
import com.lwj.springmvc.models.CollegeStudent;
import com.lwj.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentAndGradeService studentservice;

    @Autowired
    private StudentDao studentDao;

    @BeforeEach
    public void setUpDatabase() {
        jdbcTemplate.execute("insert into student(id, firstname, lastname, email_Address) " +
                "values (1, 'Eric', 'Roby', 'lwj.com')");
    }

    @Test
    public void createStudentService() {

        studentservice.createStudent("Chad", "Darby", "lwj.com");

        CollegeStudent student = studentDao.findByEmailAddress("lwj.com");

        assertEquals("lwj.com", student.getEmailAddress(), "find by email address");
    }

    @Test
    public void isStudentNullCheck() {
        assertTrue(studentservice.checkIfStudentIsNull(1));

        assertFalse(studentservice.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deletedStudent = studentDao.findById(1);

        assertTrue(deletedStudent.isPresent(), "Return True");

        studentservice.deleteStudent(1);

        deletedStudent = studentDao.findById(1);

        assertFalse(deletedStudent.isPresent(), "Return False");

    }

    @Test
    @Sql("/insertData.sql")
    public void getGradebookService() {

        Iterable<CollegeStudent> iterableCollegeStudents = studentservice.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size(), "Should be 1");
    }
    
    @AfterEach
    public void setUpAfterTransaction() {
        jdbcTemplate.execute("DELETE FROM student");
    }
}