package Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import Dao.AssessmentDao;
import Dao.CourseDao;
import Dao.MentorDao;
import Dao.NoteDao;
import Dao.TermDao;
import Model.Assessment;
import Model.Course;
import Model.Mentor;
import Model.Note;
import Model.Term;

@Database(entities = {Assessment.class, Course.class, Mentor.class, Note.class, Term.class}, version = 1, exportSchema = false)
public abstract class WGUTermRoomDatabase extends RoomDatabase {

    public abstract AssessmentDao assessmentDao();
    public abstract CourseDao courseDao();
    public abstract MentorDao mentorDao();
    public abstract NoteDao noteDao();
    public abstract TermDao termDao();

//    private static final Migration migration = new Migration(1, 2) {
//
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            // no action necessary
//        }
//    };

    private static WGUTermRoomDatabase instance;
    private static final String DB_NAME = "WGU_term_database";

    public static synchronized WGUTermRoomDatabase getDatabase(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), WGUTermRoomDatabase.class, DB_NAME).allowMainThreadQueries().build();
        }
        return instance;
    }


}
