package logic;

import java.util.ArrayList;

public class ActivityInput {
    public String name; //A,B
    public ArrayList<String> directlyPrecedingActivities;
    public Double time;

    public ActivityInput(String name, ArrayList<String> directlyPrecedingActivities, Double time) {
        this.name = name;
        this.directlyPrecedingActivities = directlyPrecedingActivities;
        this.time = time;
    }
}
