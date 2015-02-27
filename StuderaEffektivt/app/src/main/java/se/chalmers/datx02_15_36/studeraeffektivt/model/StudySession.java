package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.text.format.Time;

import java.util.Date;

import se.chalmers.datx02_15_36.studeraeffektivt.Course;

/**
 * Created by jesper on 2015-02-24.
 */
public class StudySession {

    public String name;
    private Course course;
    private String where;
    private Date date;
    private Time time;
    private String type;

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Course getCourse() {
        return course;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getWhere() {
        return where;
    }





    public StudySession(String name, Course course){
        this.name = name;
        this.course = course;
    }
}
