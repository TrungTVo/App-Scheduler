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
    private String taName;
    private Pane pane;
    private TAData taData;
    
    public ToggleCell_Transaction(String taName, Pane pane, TAData taData){
        this.taName = taName;
        this.pane = pane;
        this.taData = taData;
    }
    
    @Override
    public void doTransaction() {
        // TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
        taData.toggleTAOfficeHours(pane.getId(), taName);
    }

    @Override
    public void undoTransaction() {
        // TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
        taData.toggleTAOfficeHours(pane.getId(), taName);
    }
}
