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
    private String taName;
    private String taEmail;
    private ObservableList<TeachingAssistant> taList;
    
    public AddingTA_Transaction(String taName, String taEmail, ObservableList<TeachingAssistant> taList){
        this.taName = taName;
        this.taEmail = taEmail;
        this.taList = taList;
    }
    
    @Override
    public void doTransaction() {
        int indexOfOldTA = -1;
        for (int i=0; i<taList.size(); i++){
            if (taList.get(i).getName().equals(taName)){
                indexOfOldTA = i;
                break;
            }
        }
        if (indexOfOldTA != -1){
            taList.remove(indexOfOldTA);
        }
        
        taList.add(new TeachingAssistant(taName, taEmail));
        Collections.sort(taList);
    }

    @Override
    public void undoTransaction() {
        int indexOfOldTA = -1;
        for (int i=0; i<taList.size(); i++){
            if (taList.get(i).getName().equals(taName)){
                indexOfOldTA = i;
                break;
            }
        }
        if (indexOfOldTA != -1){
            taList.remove(indexOfOldTA);
        }
    }
       
}
