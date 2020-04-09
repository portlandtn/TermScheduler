package DataProvider;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import Database.WGUTermRoomDatabase;
import Model.Assessment;
import Model.Course;
import Model.Mentor;
import Model.Note;
import Model.Term;

@SuppressLint("Registered")
public class SampleData extends AppCompatActivity {

    Term term1 = new Term();
    Term term2 = new Term();
    Term term3 = new Term();

    Course course1 = new Course();
    Course course2 = new Course();
    Course course3 = new Course();

    Assessment assessment1 = new Assessment();
    Assessment assessment2 = new Assessment();
    Assessment assessment3 = new Assessment();

    Mentor mentor1 = new Mentor();
    Mentor mentor2 = new Mentor();
    Mentor mentor3 = new Mentor();

    Note note1 = new Note();
    Note note2 = new Note();
    Note note3 = new Note();

    WGUTermRoomDatabase db;

    public Object[] populateDatabaseWithSampleData(Context context) {
        db = WGUTermRoomDatabase.getDatabase(context);

        return new Object[]{
                populateTerms(),
                populateMentors(),
                populateCourses(),
                populateAssessments(),
                populateNotes()
        };

    }

    public void deleteAllDataFromDatabase(Context context) {
        db = WGUTermRoomDatabase.getDatabase(context);

        // order is important. Dependencies (foreign keys) Must be deleted with dependencies first, then independent tables
        db.noteDao().deleteAllNotes();
        db.mentorDao().deleteAllMentors();
        db.assessmentDao().deleteAllAssessments();
        db.courseDao().deleteAllCourses();
        db.termDao().deleteAllTerms();
    }

    private Term[] populateTerms() {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        end.add(Calendar.MONTH, 6);
        term1.setMTitle("Spring 2020");
        term1.setMStartDate(start.getTime());
        term1.setMEndDate(end.getTime());
        term1.setId(db.termDao().insert(term1));

        start.add(Calendar.MONTH, 6);
        end.add(Calendar.MONTH, 6);
        term2.setMTitle("Fall 2020");
        term2.setMStartDate(start.getTime());
        term2.setMEndDate(end.getTime());
        term2.setId(db.termDao().insert(term2));

        start.add(Calendar.MONTH, 6);
        end.add(Calendar.MONTH, 6);
        term3.setMTitle("Spring 2021");
        term3.setMStartDate(start.getTime());
        term3.setMEndDate(end.getTime());
        term3.setId(db.termDao().insert(term3));

        return new Term[]{term1, term2, term3};
    }

    private Course[] populateCourses() {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        end.add(Calendar.MONTH, 2);
        course1.setMTitle("Biology 101");
        course1.setMStartDate(start.getTime());
        course1.setMEndDate(end.getTime());
        course1.setMTermId(term1.getId());
        course1.setMMentorId(mentor1.getId());
        course1.setMStatus(CourseStatus.PLAN_TO_TAKE);
        course1.setId(db.courseDao().insert(course1));

        start.add(Calendar.MONTH, 2);
        end.add(Calendar.MONTH, 2);
        course2.setMTitle("Math 518");
        course2.setMStartDate(start.getTime());
        course2.setMEndDate(end.getTime());
        course2.setMTermId(term1.getId());
        course2.setMMentorId(mentor1.getId());
        course2.setMStatus(CourseStatus.IN_PROGRESS);
        course2.setId(db.courseDao().insert(course2));

        start.add(Calendar.MONTH, 2);
        end.add(Calendar.MONTH, 2);
        course3.setMTitle("Computer Science 165");
        course3.setMStartDate(start.getTime());
        course3.setMEndDate(end.getTime());
        course3.setMTermId(term2.getId());
        course3.setMMentorId(mentor2.getId());
        course3.setMStatus(CourseStatus.FAILED);
        course3.setId(db.courseDao().insert(course3));

        return new Course[] {course1, course2, course3};

    }

    private Assessment[] populateAssessments() {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        end.add(Calendar.DATE, 6);
        assessment1.setMTitle("First Attempt");
        assessment1.setMStartDate(start.getTime());
        assessment1.setMEndDate(end.getTime());
        assessment1.setMCourseId(course1.getId());
        assessment1.setMStatus(AssessmentStatus.PASSED);
        assessment1.setId(db.assessmentDao().insert(assessment1));

        start.add(Calendar.DATE, 6);
        end.add(Calendar.DATE, 6);
        assessment2.setMTitle("Second Attempt");
        assessment2.setMStartDate(start.getTime());
        assessment2.setMEndDate(end.getTime());
        assessment2.setMCourseId(course1.getId());
        assessment2.setMStatus(AssessmentStatus.PLANNED);
        assessment2.setId(db.assessmentDao().insert(assessment2));

        start.add(Calendar.DATE, 6);
        end.add(Calendar.DATE, 6);
        assessment3.setMTitle("Another Attempt");
        assessment3.setMStartDate(start.getTime());
        assessment3.setMEndDate(end.getTime());
        assessment3.setMCourseId(course3.getId());
        assessment3.setMStatus(AssessmentStatus.FAILED);
        assessment3.setId(db.assessmentDao().insert(assessment3));

        return new Assessment[]{assessment1, assessment2, assessment3};

    }

    private Mentor[] populateMentors() {
        mentor1.setMName("Amanda Huginkiss");
        mentor1.setMPhone("270-493-8182");
        mentor1.setMEmail("man2hugNkiss@fakeNews.com");
        mentor1.setId(db.mentorDao().insert(mentor1));

        mentor2.setMName("Hugh Jazz");
        mentor2.setMPhone("850-941-9099");
        mentor2.setMEmail("HubertJazz@fakeNews.com");
        mentor2.setId(db.mentorDao().insert(mentor2));

        mentor3.setMName("Mein Utzich");
        mentor3.setMPhone("978-493-4958");
        mentor3.setMEmail("butnotreally@fakeNews.com");
        mentor3.setId(db.mentorDao().insert(mentor3));

        return new Mentor[] {mentor1, mentor2, mentor3};

    }

    private Note[] populateNotes() {
        note1.setMNote("This is a test note. Shouldn't do much more than this.");
        note1.setMCourseId(assessment1.getId());
        note1.setId(db.noteDao().insert(note1));

        note2.setMNote("Another test note for another assessment. This assessment is horrible!");
        note2.setMCourseId(assessment3.getId());
        note2.setId(db.noteDao().insert(note2));

        note3.setMNote("Yet one more note test for the assessments.");
        note3.setMCourseId(assessment3.getId());
        note3.setId(db.noteDao().insert(note3));

        return new Note[] {note1, note2, note3};

    }


}
