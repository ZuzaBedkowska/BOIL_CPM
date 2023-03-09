package logic;

import java.util.ArrayList;

public class Event {
    public String name; //1,2,3
    public ArrayList<Activity> inActivites;
    public ArrayList<Activity> outActivites;
    private static int eventCounter = 0;
    public Event(String name, ArrayList<Activity> inActivites, ArrayList<Activity> outActivites) {
        this.name = name;
        this.name = Integer.toString(eventCounter++);
        this.inActivites = inActivites;
        this.outActivites = outActivites;
        //add EventFrom to all outActivities
        for (Activity a: outActivites)
            a.addEventFrom(this);
    }
}
