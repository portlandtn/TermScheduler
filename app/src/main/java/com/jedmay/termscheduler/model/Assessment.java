package com.jedmay.termscheduler.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.jedmay.termscheduler.dataProvider.DateTimeConverter;

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
    @ColumnInfo(name = "planned_date")
    private Date mPlannedDate;

    @ColumnInfo(name = "courseId")
    private long mCourseId;

    //@TypeConverters(AssessmentStatusConverter.class)
    @ColumnInfo(name = "assessment_status")
    private String mStatus;

    @ColumnInfo(name = "assessment_type")
    private String mType;

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

    public Date getMPlannedDate() {return mPlannedDate; }

    public void setMPlannedDate(Date mPlannedDate) {
        this.mPlannedDate = mPlannedDate;
    }

    public long getMCourseId() {
        return mCourseId;
    }

    public void setMCourseId(long mCourseId) {
        this.mCourseId = mCourseId;
    }

    public String getMStatus() {
        return mStatus;
    }

    public void setMStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public void setMType(String mType) {this.mType = mType;}

    public String getMType() {return this.mType;
    }
}
