package se.chalmers.datx02_15_36.studeraeffektivt;

import java.util.List;

/**
 * Created by jesper on 2015-02-24.
 */
public class Course {
    public List<StudySession> studySessionList;
    public String name;
    public String courseCode;
    public int id;

    public Course(String name) {
        this.name = name;
    }

    public Course(String name, String courseCode){
        this.name = name;
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
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


}
