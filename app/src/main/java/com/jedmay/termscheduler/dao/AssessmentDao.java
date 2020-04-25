package com.jedmay.termscheduler.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jedmay.termscheduler.model.Assessment;

import java.util.List;

@Dao
public interface AssessmentDao {

    @Query("SELECT * FROM assessment_table WHERE id = :assessmentId")
    Assessment getAssessment(long assessmentId);

    @Query("SELECT * FROM assessment_table ORDER BY id")
    List<Assessment> getAllAssessments();

    @Query("SELECT * FROM assessment_table WHERE courseId = :courseId")
    List<Assessment> getAssessmentsForCourse(long courseId);

    @Query("SELECT COUNT(*) from assessment_table WHERE assessment_status = :status")
    int getCountOfAssessmentType(String status);

    @Insert
    long insert(Assessment assessment);

    @Insert
    void insertAllAssessments(Assessment... assessments);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("DELETE FROM assessment_table")
    void deleteAllAssessments();

}
