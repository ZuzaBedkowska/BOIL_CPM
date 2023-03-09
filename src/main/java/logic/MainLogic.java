package logic;

import javax.swing.*;
import java.util.ArrayList;

public class MainLogic {
    ArrayList<Activity> allActivities = new ArrayList<Activity>();
    ArrayList<Event> allEvents = new ArrayList<Event>();
    private void testPrint(){
        for (Event e: allEvents){
            System.out.print(e.name+":");
            for (Activity a: e.inActivites)
                System.out.print(a.name);
            System.out.print(":");
            for (Activity a: e.outActivites)
                System.out.print(a.name);
            System.out.println();
        }
    }
    public void test(){
        ArrayList<ActivityInput> testingData = new ArrayList<ActivityInput>();

        testingData.add(new ActivityInput("A","-",6.));
        testingData.add(new ActivityInput("B","-",8.));
        testingData.add(new ActivityInput("C","A,B",12.));
        testingData.add(new ActivityInput("D","C",4.));
        testingData.add(new ActivityInput("E","C",6.));
        testingData.add(new ActivityInput("F","D,   E",15.));
        testingData.add(new ActivityInput("G","E",12.));
        testingData.add(new ActivityInput("H","F, G",8.));

        if (calc(testingData) != 0)
            System.err.println("there was some error in calc()!");
    }
    public Integer calc(ArrayList<ActivityInput> activityInputs){
        //convert ActivityInput to Activity and save in allActivities
        for (ActivityInput a : activityInputs)
            allActivities.add(new Activity(a, allActivities));

        basicDumbAlgorithm();
        testPrint();
        return 0x0;
    }
    private void basicDumbAlgorithm() {
        //most suboptimal algorithm ever
        //step1: add every Activity with empty directlyPrecedingActivities to out of first Event
        ArrayList<Activity> outActivitiesToAdd = new ArrayList<Activity>();
        for (Activity a : allActivities)
            if (a.directlyPrecedingActivities.size() <= 0)
                outActivitiesToAdd.add(a); //add to Event
        allEvents.add(new Event("", new ArrayList<Activity>(), outActivitiesToAdd));
        //step2: some fancy loop
        while (true) {
            boolean waitIHaveWorkToDo = false;
            //step2a: for each "out" without "in" create new event
            for(Activity a: allActivities){
                //if there is from but no to
                if (a.eventFrom.size() > 0 && a.eventTo.size() <= 0){
                    ArrayList<Activity> inActivitiesToAdd = new ArrayList<Activity>();
                    inActivitiesToAdd.add(a);
                    Event e = new Event("",inActivitiesToAdd, new ArrayList<Activity>());
                    allEvents.add(e);
                    a.eventTo.add(e);
                    waitIHaveWorkToDo = true;
                }
            }
            //step2b: if pre is in "in" put in "out"
            for(Activity a: allActivities){
                for (Activity pre: a.directlyPrecedingActivities){
                    for (Event e: allEvents){
                        for (Activity in: e.inActivites){
                            if (pre == in && !e.outActivites.contains(a)) {
                                e.outActivites.add(a);
                                a.eventFrom.add(e);
                                waitIHaveWorkToDo = true;
                            }
                        }
                    }
                }
            }
            if (!waitIHaveWorkToDo)
                break;
        }
    }
}
