package se.chalmers.datx02_15_36.studeraeffektivt;

import java.util.List;

/**
 * Created by jesper on 2015-02-24.
 */
public class Course {
    public List<Class> classList;
    public String name;
    public String courseCode;

    public Course(String name) {
        this.name = name;
    }

    public Course(String name, String courseCode){
        this.name = name;
        this.courseCode = courseCode;
    }

    public void addClass(Class c) {
        if (!classList.contains(c)) {
            classList.add(c);
        }
    }

    public void deleteClass(Class c) {
        classList.remove(c);
    }

    public String getame(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


}
