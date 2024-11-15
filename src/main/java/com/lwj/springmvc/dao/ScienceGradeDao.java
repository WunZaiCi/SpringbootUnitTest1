package com.lwj.springmvc.dao;

import com.lwj.springmvc.models.MathGrade;
import com.lwj.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface ScienceGradeDao extends CrudRepository<ScienceGrade, Integer> {

    Iterable<ScienceGrade> findGradesByStudentId(int studentId);

    void deleteByStudentId(int id);

}
