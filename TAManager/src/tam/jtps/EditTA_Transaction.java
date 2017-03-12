/**
 * This class serves as a transaction object for Edit TA. Used
 * in UNDO/REDO mode. This transaction object will be used to be pushed
 * into the stack using jTPS framework.
 * 
 * @author Trung Vo - CSE219
 */
package tam.jtps;

import java.util.Collections;
import java.util.HashMap;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import tam.data.TAData;
import tam.data.TeachingAssistant;
import tam.workspace.TAWorkspace;

public class EditTA_Transaction implements jTPS_Transaction {
    private TAData taData;
    private String oldName;
    private String oldEmail;
    private String nameToUpdate;
    private String emailToUpdate;
    private boolean edited;
    private HashMap<String, StringProperty> clonedOfficeHours = new HashMap<>();
    private TAWorkspace workspace;
    
    public EditTA_Transaction(TAWorkspace workspace, TAData taData, String oldName, String oldEmail, String nameToUpdate, String emailToUpdate, boolean edited){
        this.taData = taData;
        this.workspace = workspace;
        this.oldName = oldName;
        this.oldEmail = oldEmail;
        this.nameToUpdate = nameToUpdate;
        this.emailToUpdate = emailToUpdate;
        this.edited = edited;
        clonedOfficeHours = clone(taData.getOfficeHours());
    }

    @Override
    public void doTransaction() {
        StringBuilder cellTextStr;
        for (String cellKey : taData.getOfficeHours().keySet()) {
            StringProperty cellText = taData.getOfficeHours().get(cellKey);
            if (cellText != null) {
                cellTextStr = new StringBuilder(cellText.getValue());
                if (taData.isCellPaneHasTAName(cellTextStr.toString(), oldName)) {
                    taData.removeTAFromCell(cellText, oldName, nameToUpdate, edited, cellKey);
                }
            }
        }
        
        // remove old TA
        int indexOldTA = -1;
        for (int i=0; i<taData.getTeachingAssistants().size(); i++){
            if (((TeachingAssistant)taData.getTeachingAssistants().get(i)).getName().equals(oldName)){
                indexOldTA = i;
                break;
            }
        }
        if (indexOldTA != -1){
            taData.getTeachingAssistants().remove(indexOldTA);
        }
        
        // add new edited TA
        taData.getTeachingAssistants().add(new TeachingAssistant(nameToUpdate,emailToUpdate));
        Collections.sort(taData.getTeachingAssistants());
        
        workspace.getNameTextField().clear();
        workspace.getEmailTextField().clear();
        workspace.getAddButton().setText("Add TA");
        workspace.getTATable().getSelectionModel().clearSelection();
    }

    @Override
    public void undoTransaction() {
        // remove new TA
        int indexNewTA = -1;
        for (int i=0; i<taData.getTeachingAssistants().size(); i++){
            if (((TeachingAssistant)taData.getTeachingAssistants().get(i)).getName().equals(nameToUpdate)){
                indexNewTA = i;
                break;
            }
        }
        if (indexNewTA != -1){
            taData.getTeachingAssistants().remove(indexNewTA);
        }
        // retrieve back old TA
        taData.getTeachingAssistants().add(new TeachingAssistant(oldName,oldEmail));
        Collections.sort(taData.getTeachingAssistants());
        
        // put deleted TA back to cell
        for (String item:clonedOfficeHours.keySet()){
            if (taData.getOfficeHours().containsKey(item)){
                if (clonedOfficeHours.get(item) != null){
                    if (taData.getOfficeHours().get(item) != null)
                        taData.getOfficeHours().get(item).setValue(clonedOfficeHours.get(item).getValue());
                }
            }
        }
        
        workspace.getNameTextField().clear();
        workspace.getEmailTextField().clear();
        workspace.getAddButton().setText("Add TA");
        workspace.getTATable().getSelectionModel().clearSelection();
    }
    
    public HashMap<String, StringProperty> clone(HashMap<String,StringProperty> officeHours){
        clonedOfficeHours.clear();
        for (String item:officeHours.keySet()){
            clonedOfficeHours.put(item, new Label().textProperty());
            if (officeHours.get(item) != null)
                clonedOfficeHours.get(item).setValue(new String(officeHours.get(item).getValue()));
        }
        return clonedOfficeHours;
    }
    
}
