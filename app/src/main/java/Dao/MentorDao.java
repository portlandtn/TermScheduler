package Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import Model.Mentor;

@Dao
public interface MentorDao {

    @Query("SELECT * FROM mentor_table WHERE id = :courseId ORDER BY id")
    Mentor getMentor(long courseId);

    @Insert
    long insert(Mentor mentor);

    @Update
    void update(Mentor mentor);

    @Delete
    void delete(Mentor mentor);

    @Query("DELETE FROM mentor_table")
    void deleteAllMentors();
}
