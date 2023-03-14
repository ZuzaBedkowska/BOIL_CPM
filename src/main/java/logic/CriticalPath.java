package logic;

import java.util.ArrayList;
import java.util.Comparator;

public class CriticalPath {
    ArrayList<Activity> allActivities = new ArrayList<Activity>();
    ArrayList<Event> allEvents = new ArrayList<Event>();
    public CriticalPath(ArrayList<Activity> allActivities, ArrayList<Event> allEvents) {
        this.allActivities = allActivities;
        this.allEvents = allEvents;
    }
    public void calc(){
        criticalPathStepForward();
        System.out.println("lol it eventually ended");
    }
    private Event findFistEvent(){
        ArrayList<Event> firstEvent = new ArrayList<>();
        try {
            for (Event e: allEvents)
                if (e.inActivites.size()==0)
                    firstEvent.add(e);
            if (firstEvent.size()>1)
                throw(new Exception());
        }
        catch (Exception e){
            System.err.println("error in findFistEvent(): there is more than one first event");
        }
        return firstEvent.get(0);
    }
    private void criticalPathStepForward(){
        Event startFrom = findFistEvent();
        recursiveStepForward(startFrom);

    }
    private void recursiveStepForward(Event e){
        ArrayList<Event> nextEvents = new ArrayList<>();
        if (e.inActivites.size() == 0) { //no activity enters event
            for (Activity a: e.outActivites){
                a.ES = 0.;
                a.EF = a.ES + a.time;
                nextEvents.addAll(a.eventTo);
            }
        }
        else if (e.inActivites.size() == 1) { //only one activity enters event
            for (Activity a: e.outActivites){
                a.ES = e.inActivites.get(0).EF;
                a.EF = a.ES + a.time;
                nextEvents.addAll(a.eventTo);
            }
        }
        else { //more than one activity enters event
            for (Activity a: e.outActivites){
                a.ES = e.inActivites.stream().max(Comparator.comparing(Activity::getEF)).get().EF;
                a.EF = a.ES + a.time;
                nextEvents.addAll(a.eventTo);
            }
        }
        //can be added: calc T (max of EF of last Event)
        for (Event nextE: nextEvents)
            recursiveStepForward(nextE);
    }
}
