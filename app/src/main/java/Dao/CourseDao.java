package Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Model.Course;

@Dao
public interface CourseDao {

    @Query("SELECT * FROM course_table WHERE id = :courseId")
    Course getCourse(long courseId);

    @Query("SELECT * FROM course_table WHERE term_id = :termId")
    List<Course> getCoursesForTerm(long termId);

    @Delete
    void delete(Course course);

    @Update
    void update(Course course);

    @Insert
    long insert(Course course);

    @Query("DELETE FROM course_table")
    void deleteAllCourses();

}
