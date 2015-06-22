/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.model;

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

    public String getName(){
        return courseName;
    }

    public void setName(String courseName){
        this.courseName = courseName;
    }

    public String toString(){
        return courseCode + " " + courseName;
    }

    public String getCourseCode(){
        return courseCode;
    }

    public String getCourseName(){ return courseName; }
}
