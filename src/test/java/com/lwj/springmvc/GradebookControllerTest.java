package com.lwj.springmvc;

import com.lwj.springmvc.dao.StudentDao;
import com.lwj.springmvc.models.CollegeStudent;
import com.lwj.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentservice;

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private StudentAndGradeService studentAndGradeService;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Chad");
        request.setParameter("lastname", "Darby");
        request.setParameter("emailAddress", "fakeEmail@gmail.com");
    }

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("insert into student(id, firstname, lastname, email_Address) " +
                "values (1, 'Eric', 'Roby', 'lwj.com')");
    }

    @Test
    public void getStudentHttpRequest() throws Exception {
        CollegeStudent studentOne = new CollegeStudent("Eric", "Roby", "lwj1.com");
        CollegeStudent studentTwo = new CollegeStudent("david", "Luis", "lwj2.com");

        List<CollegeStudent> students = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        when(studentservice.getGradebook()).thenReturn(students);

        assertIterableEquals(students, studentservice.getGradebook());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

    }

    @Test
    public void createStudentHttpRequest() throws Exception {

        CollegeStudent studentOne = new CollegeStudent("Eric", "Roby", "lwj1.com");

        ArrayList<CollegeStudent> collegeStudents = new ArrayList<>(Arrays.asList(studentOne));

        when(studentservice.getGradebook()).thenReturn(collegeStudents);

        assertIterableEquals(collegeStudents, studentservice.getGradebook());

        MvcResult mvcResult = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname", request.getParameterValues("firstname"))
                .param("lastname", request.getParameterValues("lastname"))
                .param("emailAddress", request.getParameterValues("emailAddress"))
        ).andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        CollegeStudent byEmailAddress = studentDao.findByEmailAddress("fakeEmail@gmail.com");

        assertNotNull(byEmailAddress, "Student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {

        assertTrue (studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", 1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "index");

        assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", 0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(modelAndView, "error");
    }


    @AfterEach
    public void setUpAfterTransaction() {
        jdbcTemplate.execute("DELETE FROM student");
    }

}
