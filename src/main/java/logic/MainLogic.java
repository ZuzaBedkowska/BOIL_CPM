package logic;

import java.lang.reflect.Array;
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
        System.out.println();
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
        simplifySameOuts();
        testPrint();
        apparentActivityCase1();
        testPrint();
        apparentActivityCase2();
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
    private void simplifySameOuts(){
        ArrayList<Event> toRemove = new ArrayList<Event>();
        for (int i = 0; i < allEvents.size(); i++) {
            Event e1 = allEvents.get(i);
            for (int j = i+1; j < allEvents.size(); j++) {
                Event e2 = allEvents.get(j);
                if (e1 == e2)
                    break;
                if (e1.outActivites.size() != e2.outActivites.size())
                    break;
                boolean eventIsRemovable = true;
                for (Activity a : e1.outActivites)
                    if (!e2.outActivites.contains(a)) {
                        eventIsRemovable = false;
                        break;
                    }
                if (eventIsRemovable) {
                    e1.inActivites.addAll(e2.inActivites); //move all inActivities from 2nd to 1st
                    toRemove.add(e2); //and remove the duplicate (actually add to remove queue)
                }
            }
        }
        allEvents.removeAll(toRemove); //remove all from remove queue
        //clear Activities from removed Events
        for (Activity a: allActivities)
            a.eventFrom.removeAll(toRemove);
    }
    private void apparentActivityCase1(){
        for (Activity a: allActivities)
            if (a.eventFrom.size() >= 2) {
                System.out.println("activity "+a.name+" needs to be rebuild");
            }
    }
    //TOFIX: activity after simplifySameOuts breaks lol, still points to old event
    private void apparentActivityCase2(){
        for (Event e: allEvents){
            if (e.outActivites.size() < 2)
                continue;
            ArrayList<Event> toEvents = new ArrayList<Event>();
            System.out.println(e.name);
            for (Activity a : e.outActivites) {
                System.out.println(toEvents);
                if (toEvents.contains(a.eventTo.get(0))){
                    System.out.println("activity "+a.name+" needs to be rebuild");
                }
                toEvents.add(a.eventTo.get(0));
                System.out.println(" added "+a.eventTo.get(0).name);
            }
            System.out.println("  break");
        }
    }
}
