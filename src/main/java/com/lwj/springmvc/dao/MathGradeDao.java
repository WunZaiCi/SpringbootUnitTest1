package com.lwj.springmvc.dao;

import com.lwj.springmvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

public interface MathGradeDao extends CrudRepository<MathGrade, Integer> {

        Iterable<MathGrade> findGradesByStudentId(int studentId);

        void deleteByStudentId(int id);

}
