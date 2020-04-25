package com.jedmay.termscheduler.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jedmay.termscheduler.dao.AssessmentDao;
import com.jedmay.termscheduler.dao.CourseDao;
import com.jedmay.termscheduler.dao.MentorDao;
import com.jedmay.termscheduler.dao.NoteDao;
import com.jedmay.termscheduler.dao.TermDao;
import com.jedmay.termscheduler.model.Assessment;
import com.jedmay.termscheduler.model.Course;
import com.jedmay.termscheduler.model.Mentor;
import com.jedmay.termscheduler.model.Note;
import com.jedmay.termscheduler.model.Term;

@Database(entities = {Assessment.class, Course.class, Mentor.class, Note.class, Term.class}, version = 3, exportSchema = false)
public abstract class WGUTermRoomDatabase extends RoomDatabase {

    public abstract AssessmentDao assessmentDao();
    public abstract CourseDao courseDao();
    public abstract MentorDao mentorDao();
    public abstract NoteDao noteDao();
    public abstract TermDao termDao();

    private static WGUTermRoomDatabase instance;
    private static final String DB_NAME = "WGU_term_database";

    public static synchronized WGUTermRoomDatabase getDatabase(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), WGUTermRoomDatabase.class, DB_NAME).fallbackToDestructiveMigrationFrom(2, 3).allowMainThreadQueries().build();
        }
        return instance;
    }


}
