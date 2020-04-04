package Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import DataProvider.AssessmentStatus;
import DataProvider.AssessmentStatusConverter;
import DataProvider.DateTimeConverter;

@Entity(tableName ="assessment_table",
        foreignKeys = @ForeignKey(entity = Course.class,
        parentColumns = "id",
        childColumns = "courseId",
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE),
        indices = {@Index("courseId")})

public class Assessment {

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

    @ColumnInfo(name = "courseId")
    private long mCourseId;

    @TypeConverters(AssessmentStatusConverter.class)
    @ColumnInfo(name = "assessment_status")
    private AssessmentStatus mStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getMCourseId() {
        return mCourseId;
    }

    public void setMCourseId(long mCourseId) {
        this.mCourseId = mCourseId;
    }

    public AssessmentStatus getMStatus() {
        return mStatus;
    }

    public void setMStatus(AssessmentStatus mStatus) {
        this.mStatus = mStatus;
    }
}
