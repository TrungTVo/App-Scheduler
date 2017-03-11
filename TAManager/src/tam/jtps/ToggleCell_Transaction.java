/**
 * This class serves as a transaction object for Cell Toggling mode. Used
 * in UNDO/REDO mode. This transaction object will be used to be pushed
 * into the stack using jTPS framework.
 * 
 * @author Trung Vo - CSE219
 */
package tam.jtps;

import java.util.HashMap;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import tam.data.TAData;
import tam.data.TeachingAssistant;

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
