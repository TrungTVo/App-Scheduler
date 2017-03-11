/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tam.jtps;

import java.util.HashMap;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import tam.data.TAData;
import tam.data.TeachingAssistant;

/**
 *
 * @author trungvo
 */
public class ToggleCell_Transaction implements jTPS_Transaction {
    private TeachingAssistant ta;
    private HashMap<String, StringProperty> officeHours;
    private Pane pane;
    private TAData taData;
    
    public ToggleCell_Transaction(TeachingAssistant ta, HashMap<String, StringProperty> officeHours,
                                                    Pane pane, TAData taData){
        this.ta = ta;
        this.officeHours = officeHours;
        this.pane = pane;
        this.taData = taData;
    }
    
    @Override
    public void doTransaction() {
        // TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
        taData.toggleTAOfficeHours(pane.getId(), ta.getName());
    }

    @Override
    public void undoTransaction() {
        // TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
        taData.toggleTAOfficeHours(pane.getId(), ta.getName());
    }
}
