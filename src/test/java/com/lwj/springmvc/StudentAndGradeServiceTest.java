package com.lwj.springmvc;

import com.lwj.springmvc.dao.HistoryGradeDao;
import com.lwj.springmvc.dao.MathGradeDao;
import com.lwj.springmvc.dao.ScienceGradeDao;
import com.lwj.springmvc.dao.StudentDao;
import com.lwj.springmvc.models.CollegeStudent;
import com.lwj.springmvc.models.HistoryGrade;
import com.lwj.springmvc.models.MathGrade;
import com.lwj.springmvc.models.ScienceGrade;
import com.lwj.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

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

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @BeforeEach
    public void setUpDatabase() {
        jdbcTemplate.execute("insert into student(id, firstname, lastname, email_Address) " +
                "values (1, 'Eric', 'Roby', 'lwj.com')");

        jdbcTemplate.execute("insert into math_grade(id, student_id, grade) " +
                "values (1, 1, 100.0)");

        jdbcTemplate.execute("insert into science_grade(id, student_id, grade) " +
                "values (1, 1, 100.0)");

        jdbcTemplate.execute("insert into history_grade(id, student_id, grade) " +
                "values (1, 1, 100.0)");
    }

    @Test
    public void createStudentService() {

        studentservice.createStudent("Chad", "Darby", "lwj234.com");

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
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);

        assertTrue(deletedStudent.isPresent(), "Return True");
        assertTrue(deletedMathGrade.isPresent(), "Return True");
        assertTrue(deletedScienceGrade.isPresent(), "Return True");
        assertTrue(deletedHistoryGrade.isPresent(), "Return True");

        studentservice.deleteStudent(1);

        deletedStudent = studentDao.findById(1);
        deletedMathGrade = mathGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);

        assertFalse(deletedStudent.isPresent(), "Return False");
        assertFalse(deletedMathGrade.isPresent(), "Return False");
        assertFalse(deletedScienceGrade.isPresent(), "Return False");
        assertFalse(deletedHistoryGrade.isPresent(), "Return False");

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


    @Test
    public void creatGradeService() {

        //create the grade
        assertTrue(studentservice.creatGrade(80.50, 1, "math"));
        assertTrue(studentservice.creatGrade(80.50, 1, "science"));
        assertTrue(studentservice.creatGrade(80.50, 1, "history"));

        //get all grades with studentID
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradesByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradesByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradesByStudentId(1);

        //Verify there is grades
        assertTrue(((Collection<MathGrade>)mathGrades).size() == 2, "Student has math grades");
        assertTrue(((Collection<ScienceGrade>)scienceGrades).size() == 2, "Student has science grades");
        assertTrue(((Collection<HistoryGrade>)historyGrades).size() == 2, "Student has history grades");

    }

    @Test
    public void creatGradeServiceReturnFalse() {

        assertFalse(studentservice.creatGrade(105, 1, "math"));
        assertFalse(studentservice.creatGrade(-5, 1, "science"));
        assertFalse(studentservice.creatGrade(80.50, 2, "history"));
        assertFalse(studentservice.creatGrade(80.50, 1, "English"));
    }

    @Test
    public void deleteGradeService(){
        assertEquals(1, studentservice.deleteGrade(1, "math"), "Return student id after delete");
        assertEquals(1, studentservice.deleteGrade(1, "science"), "Return student id after delete");
        assertEquals(1, studentservice.deleteGrade(1, "history"), "Return student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){
        assertEquals(0, studentservice.deleteGrade(0, "science"), "No student should have 0 id");
        assertEquals(0, studentservice.deleteGrade(1, "English"), "No student should have English class");

    }
    
    @AfterEach
    public void setUpAfterTransaction() {
        jdbcTemplate.execute("DELETE FROM student");
        jdbcTemplate.execute("DELETE FROM math_grade");
        jdbcTemplate.execute("DELETE FROM science_grade");
        jdbcTemplate.execute("DELETE FROM history_grade");
    }
}