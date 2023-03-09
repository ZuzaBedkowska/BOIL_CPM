package logic;

import java.util.ArrayList;

public class MainLogic {
    ArrayList<Activity> allActivities = new ArrayList<Activity>();
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
    }
    public Integer calc(ArrayList<ActivityInput> activityInputs){
        return 0x0;
    }


}
