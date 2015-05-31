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

/**
 * Created by emmawestman on 15-04-04.
 */
public class HomeEventItem{

    private String titleS;
    private String timeS;
    private String locationS;
    private String timeToStartS;
    private long id;
    private long startTime;
    private long endTime;
    private int color;
    private long calId;



    public HomeEventItem() {

    }


    public String getTitleS() {
        return titleS;
    }

    public void setTitleS(String title) {

        if(title.length() > 20) {
            this.titleS = title.substring(0,20) + "...";
        } else {
            this.titleS = title;
        }
    }

    public String getTimeS() {
        return timeS;
    }

    public void setTimeS(String timeS) {
        this.timeS = timeS;
    }

    public String getLocationS() {
        return locationS;
    }

    public void setLocationS(String locationS) {
        this.locationS = locationS;
    }

    public String getTimeToStartS() {
        return timeToStartS;
    }

    public void setTimeToStartS(String timeToStartS) {
        this.timeToStartS = timeToStartS;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getCalId() {
        return calId;
    }

    public void setCalId(long calId) {
        this.calId = calId;
    }



}
