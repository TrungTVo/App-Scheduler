
package tam.jtps;

import java.util.HashMap;
import javafx.beans.property.StringProperty;
import tam.data.TAData;
import tam.workspace.TAWorkspace;

public class TimeFrameChange_Transaction implements jTPS_Transaction {
    private HashMap<String, StringProperty> oldHours;
    private HashMap<String, StringProperty> newHours;
    
    private int newStartHour;
    private int newEndHour;
    private String newStartMin;
    private String newEndMin;
    
    private int oldStartHour;
    private int oldEndHour;
    private String oldStartMin;
    private String oldEndMin;
    
    private TAData taData;
    private TAWorkspace workspace;
    
    public TimeFrameChange_Transaction(HashMap<String, StringProperty> oldHours, HashMap<String, StringProperty> newHours,
                                           int newStartHour, String newStartMin, int newEndHour, String newEndMin,
                                           int oldStartHour, String oldStartMin, int oldEndHour, String oldEndMin, TAData taData, TAWorkspace workspace){
        this.oldHours = oldHours;
        this.newHours = newHours;
        
        // new hours
        this.newStartHour = newStartHour;
        this.newEndHour = newEndHour;
        this.newStartMin = newStartMin;
        this.newEndMin = newEndMin;
        
        // old hours
        this.oldStartHour = oldStartHour;
        this.oldStartMin = oldStartMin;
        this.oldEndHour = oldEndHour;
        this.oldEndMin = oldEndMin;
        
        this.taData = taData;
        this.workspace = workspace;
    }

    @Override
    public void doTransaction() {
        // redo new time frame
        taData.setStartHour(newStartHour);
        taData.setStartMin(newStartMin);
        taData.setEndHour(newEndHour);
        taData.setEndMin(newEndMin);
        
        taData.getOfficeHours().clear();
        for (String cellKey:newHours.keySet()){
            if (cellKey.charAt(0) != '0' && cellKey.charAt(0) != '1' && !cellKey.substring(cellKey.indexOf("_")+1).equals("0"))
                taData.getOfficeHours().put(cellKey, newHours.get(cellKey));
        }
        workspace.setUpdatingTime(true);
        workspace.resetWorkspace();
        workspace.reloadOfficeHoursGrid(taData);
    }

    @Override
    public void undoTransaction() {
        // undo old time frame
        taData.setStartHour(oldStartHour);
        taData.setStartMin(oldStartMin);
        taData.setEndHour(oldEndHour);
        taData.setEndMin(oldEndMin);
        
        taData.getOfficeHours().clear();
        for (String cellKey:oldHours.keySet()){
            if (cellKey.charAt(0) != '0' && cellKey.charAt(0) != '1' && !cellKey.substring(cellKey.indexOf("_")+1).equals("0"))
                taData.getOfficeHours().put(cellKey, oldHours.get(cellKey));
        }
        workspace.setUpdatingTime(true);
        workspace.resetWorkspace();
        workspace.reloadOfficeHoursGrid(taData);
    }
    
}
