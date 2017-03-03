package tam.workspace;

import static tam.TAManagerProp.*;
import djf.ui.AppMessageDialogSingleton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import properties_manager.PropertiesManager;
import tam.TAManagerApp;
import tam.data.TAData;
import tam.data.TeachingAssistant;
import tam.workspace.TAWorkspace;
import javafx.beans.property.StringProperty;

/**
 * This class provides responses to all workspace interactions, meaning
 * interactions with the application controls not including the file
 * toolbar.
 * 
 * @author Richard McKenna - Trung Vo
 * @version 1.0
 */
public class TAController {
    // THE APP PROVIDES ACCESS TO OTHER COMPONENTS AS NEEDED
    TAManagerApp app;

    /**
     * Constructor, note that the app must already be constructed.
     */
    public TAController(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;
    }
    
    /**
     * This method responds to when the user requests to add
     * a new TA via the UI. Note that it must first do some
     * validation to make sure a unique name and email address
     * has been provided.
     */
    public boolean handleAddTA() {
        // WE'LL NEED THE WORKSPACE TO RETRIEVE THE USER INPUT VALUES
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TextField nameTextField = workspace.getNameTextField();
        TextField emailTextField = workspace.getEmailTextField();
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        
        // WE'LL NEED TO ASK THE DATA SOME QUESTIONS TOO
        TAData data = (TAData)app.getDataComponent();
        
        // WE'LL NEED THIS IN CASE WE NEED TO DISPLAY ANY ERROR MESSAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // DID THE USER NEGLECT TO PROVIDE A TA NAME?
        if (name.isEmpty()) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(MISSING_TA_NAME_TITLE), props.getProperty(MISSING_TA_NAME_MESSAGE));            
            return false;
        } else if (email.isEmpty()) {
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
            dialog.show(props.getProperty(MISSING_TA_EMAIL_TITLE), props.getProperty(MISSING_TA_EMAIL_MESSAGE));
            return false;
        }
        // DOES A TA ALREADY HAVE THE SAME NAME OR EMAIL?
        else if (data.containsTA(name)) {
	    AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_TITLE), props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_MESSAGE));                                    
            return false;
        } else if (data.containsTAEmail(email)){
            AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
	    dialog.show(props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_TITLE), props.getProperty(TA_NAME_AND_EMAIL_NOT_UNIQUE_MESSAGE));
            return false;
        }
        // EVERYTHING IS FINE, ADD A NEW TA
        else {
            // ADD THE NEW TA TO THE DATA
            data.addTA(name, email);
            
            // CLEAR THE TEXT FIELDS
            nameTextField.setText("");
            emailTextField.setText("");
            
            // AND SEND THE CARET BACK TO THE NAME TEXT FIELD FOR EASY DATA ENTRY
            nameTextField.requestFocus();
            emailTextField.requestFocus();
            return true;
        }
    }

    /**
     * This function provides a response for when the user clicks
     * on the office hours grid to add or remove a TA to a time slot.
     * 
     * @param pane The pane that was toggled.
     */
    public boolean handleCellToggle(Pane pane) {
        // GET THE TABLE
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TableView taTable = workspace.getTATable();
        
        // IS A TA SELECTED IN THE TABLE?
        Object selectedItem = taTable.getSelectionModel().getSelectedItem();
        // GET THE TA
        TeachingAssistant ta = (TeachingAssistant)selectedItem;
        if (ta != null){
            String taName = ta.getName();
            TAData data = (TAData)app.getDataComponent();
            String cellKey = pane.getId();

            // AND TOGGLE THE OFFICE HOURS IN THE CLICKED CELL
            data.toggleTAOfficeHours(cellKey, taName);
            return true;
        } else {
            return false;
        }
    }
    
    public void handleDeleteTAfromTable(TeachingAssistant ta) {
        String taName = ta.getName();
        TAData data = (TAData)app.getDataComponent();
        for (String cellKey:data.getOfficeHours().keySet()){
            StringProperty cellText = data.getOfficeHours().get(cellKey);
            if (data.isCellPaneHasTAName(cellText.getValue(), taName)){
                data.removeTAFromCell(cellText, taName, cellKey);
            }
        }
        data.getTeachingAssistants().remove(ta);
    }
}