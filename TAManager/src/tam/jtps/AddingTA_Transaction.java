/**
 * This class serves as a transaction object for adding new TA. Used
 * in UNDO/REDO mode. This transaction object will be used to be pushed
 * into the stack using jTPS framework.
 * 
 * @author Trung Vo - CSE219
 */
package tam.jtps;

import java.util.Collections;
import javafx.collections.ObservableList;
import tam.data.TeachingAssistant;

public class AddingTA_Transaction implements jTPS_Transaction {
    private TeachingAssistant ta;
    private ObservableList<TeachingAssistant> taList;
    
    public AddingTA_Transaction(TeachingAssistant ta, ObservableList<TeachingAssistant> taList){
        this.ta = ta;
        this.taList = taList;
    }

    @Override
    public void doTransaction() {
        taList.add(ta);
        Collections.sort(taList);
    }

    @Override
    public void undoTransaction() {
        if (taList.contains(ta))
            taList.remove(ta);
    }
       
}
