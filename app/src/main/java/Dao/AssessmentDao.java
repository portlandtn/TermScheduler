package Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.List;

import DataProvider.AssessmentStatus;
import DataProvider.AssessmentStatusConverter;
import Model.Assessment;

@Dao
public interface AssessmentDao {

    @Query("SELECT * FROM assessment_table WHERE id = :assessmentId")
    Assessment getAssessment(long assessmentId);

    @Query("SELECT * FROM assessment_table ORDER BY id")
    List<Assessment> getAllAssessments();

    @Query("SELECT * FROM assessment_table WHERE courseId = :courseId")
    List<Assessment> getAssessmentsForCourse(long courseId);

    @TypeConverters(AssessmentStatusConverter.class)
    @Query("SELECT COUNT(*) from assessment_table WHERE assessment_status = :status")
    int getCountOfAssessmentType(AssessmentStatus status);

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
