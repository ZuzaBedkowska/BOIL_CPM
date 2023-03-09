package logic;

import java.util.ArrayList;

public class Activity {
    public String name; //A,B
    public ArrayList<Activity> directlyPrecedingActivities;
    public Double time;

    public Activity(String name, ArrayList<Activity> directlyPrecedingActivities, Double time) {
        this.name = name;
        this.directlyPrecedingActivities = directlyPrecedingActivities;
        this.time = time;
    }
}
