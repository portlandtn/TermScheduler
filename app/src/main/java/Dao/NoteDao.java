package Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Model.Note;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id")
    List<Note> getAlNotes();

    @Query("SELECT * FROM note_table WHERE id = :noteId")
    Note getNote(long noteId);

    @Query("SELECT * FROM note_table WHERE course_id = :courseId ORDER BY id")
    List<Note> getNotesForCourse(long courseId);

    @Insert
    long insert(Note note);

    @Insert
    void insertAllNotes(Note... notes);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

}
