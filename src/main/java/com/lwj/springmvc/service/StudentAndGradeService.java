package com.lwj.springmvc.service;

import com.lwj.springmvc.dao.HistoryGradeDao;
import com.lwj.springmvc.dao.MathGradeDao;
import com.lwj.springmvc.dao.ScienceGradeDao;
import com.lwj.springmvc.dao.StudentDao;
import com.lwj.springmvc.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    public void createStudent(String firstname, String lastname, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {
        Optional<CollegeStudent> student = studentDao.findById(id);
        return student.isPresent();
    }

    public void deleteStudent(int id) {
        if (checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
            mathGradeDao.deleteById(id);
            scienceGradeDao.deleteById(id);
            historyGradeDao.deleteById(id);
        }

    }

    public Iterable<CollegeStudent> getGradebook() {
        return studentDao.findAll();
    }

    public boolean creatGrade(double grade, int studentID, String gradeTpye) {

        if (!checkIfStudentIsNull(studentID)) {
            return false;
        }

        if (grade >= 0 && grade <= 100) {
            if ("math".equals(gradeTpye)) {
                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentID);
                mathGradeDao.save(mathGrade);
                return true;
            }
            if ("science".equals(gradeTpye)) {
                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentID);
                scienceGradeDao.save(scienceGrade);
                return true;
            }

            if ("history".equals(gradeTpye)) {
                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentID);
                historyGradeDao.save(historyGrade);
                return true;
            }
        }
        return false;
    }

    public int deleteGrade(int id, String gradeTpye) {

        int studentID = 0;

        if (checkIfStudentIsNull(id)) {
            if ("math".equals(gradeTpye)) {
                Optional<MathGrade> grade = mathGradeDao.findById(id);
                if (!grade.isPresent()) {
                    return studentID;
                }
                studentID = grade.get().getStudentId();
                mathGradeDao.delete(grade.get());
            }

            if ("science".equals(gradeTpye)) {
                Optional<ScienceGrade> grade = scienceGradeDao.findById(id);
                if (!grade.isPresent()) {
                    return studentID;
                }
                studentID = grade.get().getStudentId();
                scienceGradeDao.delete(grade.get());
            }

            if ("history".equals(gradeTpye)) {
                Optional<HistoryGrade> grade = historyGradeDao.findById(id);
                if (!grade.isPresent()) {
                    return studentID;
                }
                studentID = grade.get().getStudentId();
                historyGradeDao.delete(grade.get());
            }
        }

        return studentID;
    }
}
