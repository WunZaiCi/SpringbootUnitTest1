package com.lwj.springmvc;

import com.lwj.springmvc.dao.HistoryGradeDao;
import com.lwj.springmvc.dao.MathGradeDao;
import com.lwj.springmvc.dao.ScienceGradeDao;
import com.lwj.springmvc.dao.StudentDao;
import com.lwj.springmvc.models.*;
import com.lwj.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@TestPropertySource("/application-test.properties")
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

    @Value("${sql.scripts.create.student}")
    private String sqlAddStudent;

    @Value("${sql.scripts.create.math,grade}")
    private String sqlAddMathGrade;

    @Value("${sql.scripts.create.science,grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.scripts.create.history,grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.scripts.create.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.scripts.create.delete.math,grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.scripts.create.delete.science,grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.scripts.create.delete.history,grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setUpDatabase() {
        jdbcTemplate.execute(sqlAddStudent);
        jdbcTemplate.execute(sqlAddMathGrade);
        jdbcTemplate.execute(sqlAddScienceGrade);
        jdbcTemplate.execute(sqlAddHistoryGrade);
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
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2, "Student has math grades");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2, "Student has science grades");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2, "Student has history grades");

    }

    @Test
    public void creatGradeServiceReturnFalse() {

        assertFalse(studentservice.creatGrade(105, 1, "math"));
        assertFalse(studentservice.creatGrade(-5, 1, "science"));
        assertFalse(studentservice.creatGrade(80.50, 2, "history"));
        assertFalse(studentservice.creatGrade(80.50, 1, "English"));
    }

    @Test
    public void deleteGradeService() {
        assertEquals(1, studentservice.deleteGrade(1, "math"), "Return student id after delete");
        assertEquals(1, studentservice.deleteGrade(1, "science"), "Return student id after delete");
        assertEquals(1, studentservice.deleteGrade(1, "history"), "Return student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero() {
        assertEquals(0, studentservice.deleteGrade(0, "science"), "No student should have 0 id");
        assertEquals(0, studentservice.deleteGrade(1, "English"), "No student should have English class");

    }

    @Test
    public void studentInformation() {

        GradebookCollegeStudent gradebookCollegeStudent = studentservice.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Eric", gradebookCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegeStudent.getLastname());
        assertEquals("lwj.com", gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
    }

    @Test
    public void studentInformationServiceReturnNull() {

        GradebookCollegeStudent gradebookCollegeStudent = studentservice.studentInformation(0);

        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void setUpAfterTransaction() {
        jdbcTemplate.execute(sqlDeleteStudent);
        jdbcTemplate.execute(sqlDeleteMathGrade);
        jdbcTemplate.execute(sqlDeleteScienceGrade);
        jdbcTemplate.execute(sqlDeleteHistoryGrade);
    }
}