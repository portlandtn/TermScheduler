package Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import DataProvider.DateTimeConverter;

@Entity(tableName = "course_table", foreignKeys = {
        @ForeignKey(entity = Term.class,
                parentColumns = "id",
                childColumns = "term_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Mentor.class,
                parentColumns = "id",
                childColumns = "mentor_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index("term_id"), @Index("mentor_id")}
)
public class Course {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String mTitle;

    @TypeConverters(DateTimeConverter.class)
    @ColumnInfo(name = "start_date")
    private Date mStartDate;

    @TypeConverters(DateTimeConverter.class)
    @ColumnInfo(name = "end_date")
    private Date mEndDate;

    //@TypeConverters(CourseStatusConverter.class)
    @ColumnInfo(name = "course_status")
    private String mStatus;

    @ColumnInfo(name = "term_id")
    private long mTermId;

    @ColumnInfo(name = "mentor_id")
    private long mMentorId;


    public long getId() {
        return id;
    }

    public String getMTitle() {
        return mTitle;
    }

    public void setMTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getMStartDate() {
        return mStartDate;
    }

    public void setMStartDate(Date mStartDate) {
        this.mStartDate = mStartDate;
    }

    public Date getMEndDate() {
        return mEndDate;
    }

    public void setMEndDate(Date mEndDate) {
        this.mEndDate = mEndDate;
    }

    public String getMStatus() {
        return mStatus;
    }

    public void setMStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public long getMTermId() {
        return mTermId;
    }

    public void setMTermId(long mTermId) {
        this.mTermId = mTermId;
    }

    public long getMMentorId() {
        return mMentorId;
    }

    public void setMMentorId(long mMentorId) {
        this.mMentorId = mMentorId;
    }

    public void setId(long id) {
        this.id = id;
    }

}
