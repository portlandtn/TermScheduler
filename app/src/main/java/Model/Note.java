package Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table",
        foreignKeys = @ForeignKey(entity = Assessment.class,
        parentColumns = "id",
        childColumns = "course_id",
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE),
        indices = @Index("course_id"))

public class Note {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "note")
    private String mNote;

    @ColumnInfo(name = "course_id")
    private long mCourseId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMNote() {
        return mNote;
    }

    public void setMNote(String mNote) {
        this.mNote = mNote;
    }

    public long getMCourseId() {
        return mCourseId;
    }

    public void setMCourseId(long mCourseId) {
        this.mCourseId = mCourseId;
    }
}
