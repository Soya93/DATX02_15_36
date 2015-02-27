package se.chalmers.datx02_15_36.studeraeffektivt;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.model.StudySession;

/**
 * Created by jesper on 2015-02-24.
 */
public class Course {
    public List<StudySession> studySessionList;
    public String courseName;
    public String courseCode;
    public int id;

    public Course(String courseName) {
        this.courseName = courseName;
    }

    public Course(String courseName, String courseCode){
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    public void addClass(StudySession c) {
        if (!studySessionList.contains(c)) {
            studySessionList.add(c);
        }
    }

    public void deleteClass(StudySession c) {
        studySessionList.remove(c);
    }

    public String geName(){
        return courseName;
    }

    public void setName(String courseName){
        this.courseName = courseName;
    }

    public String toString(){
        return courseCode + " - " + courseName;
    }


}
