package com.lwj.springmvc.dao;

import com.lwj.springmvc.models.HistoryGrade;
import com.lwj.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradeDao extends CrudRepository<HistoryGrade, Integer> {

    Iterable<HistoryGrade> findGradesByStudentId(int studentId);

}
