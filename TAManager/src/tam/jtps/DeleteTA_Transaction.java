/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tam.jtps;

import java.util.Collections;
import java.util.HashMap;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import tam.data.TAData;
import tam.data.TeachingAssistant;

/**
 *
 * @author trungvo
 */
public class DeleteTA_Transaction implements jTPS_Transaction {
    private TeachingAssistant ta;
    private HashMap<String, StringProperty> officeHours;
    private TAData taData;
    private HashMap<String, StringProperty> clonedOfficeHours = new HashMap<>();
    
    public DeleteTA_Transaction(TeachingAssistant ta, HashMap<String, StringProperty> officeHours, TAData taData){
        this.ta = ta;
        this.officeHours = officeHours;
        this.taData = taData;
        clonedOfficeHours = clone(officeHours);
    }

    @Override
    public void doTransaction() {
        String taName = ta.getName();
        for (String cellKey:taData.getOfficeHours().keySet()){
            StringProperty cellText = taData.getOfficeHours().get(cellKey);
            if (cellText != null) {
                if (taData.isCellPaneHasTAName(cellText.getValue(), taName)) {
                    taData.removeTAFromCell(cellText, taName, null, false, cellKey);
                }
            }
        }
        taData.getTeachingAssistants().remove(ta);
    }

    @Override
    public void undoTransaction() {
        // add TA back to table
        taData.getTeachingAssistants().add(ta);
        Collections.sort(taData.getTeachingAssistants());
        
        // put deleted TA back to cell
        for (String item:clonedOfficeHours.keySet()){
            taData.getOfficeHours().get(item).setValue(clonedOfficeHours.get(item).getValue());
        }
    }
    
    public HashMap<String, StringProperty> clone(HashMap<String,StringProperty> officeHours){
        clonedOfficeHours.clear();
        for (String item:officeHours.keySet()){
            clonedOfficeHours.put(item, new Label().textProperty());
            clonedOfficeHours.get(item).setValue(new String(officeHours.get(item).getValue()));
        }
        return clonedOfficeHours;
    }
}
