package tam.workspace;

import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import java.util.ArrayList;
import java.util.HashMap;
import tam.TAManagerApp;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import properties_manager.PropertiesManager;
import tam.TAManagerProp;
import tam.style.TAStyle;
import tam.data.TAData;
import tam.data.TeachingAssistant;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.*;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
/**
 * This class serves as the workspace component for the TA Manager
 * application. It provides all the user interface controls in 
 * the workspace area.
 * 
 * @author Richard McKenna - Trung Vo
 */
public class TAWorkspace extends AppWorkspaceComponent {
    // THIS PROVIDES US WITH ACCESS TO THE APP COMPONENTS
    TAManagerApp app;

    // THIS PROVIDES RESPONSES TO INTERACTIONS WITH THIS WORKSPACE
    TAController controller;

    // NOTE THAT EVERY CONTROL IS PUT IN A BOX TO HELP WITH ALIGNMENT
    
    // FOR THE HEADER ON THE LEFT
    HBox tasHeaderBox;
    Label tasHeaderLabel;
    
    // FOR THE TA TABLE
    TableView<TeachingAssistant> taTable;
    TableColumn<TeachingAssistant, String> nameColumn;
    TableColumn<TeachingAssistant, String> emailColumn;

    // THE TA INPUT
    HBox addBox;
    TextField nameTextField;
    TextField emailTextField;
    Button addButton;
    Button clearButton;

    // THE HEADER ON THE RIGHT
    HBox officeHoursHeaderBox;
    Label officeHoursHeaderLabel;
    Label startTimeLabel;
    Label endTimeLabel;
    ComboBox startBox;
    ComboBox endBox;
    HBox startWrap;
    HBox endWrap;
    
    // THE OFFICE HOURS GRID
    GridPane officeHoursGridPane;
    HashMap<String, Pane> officeHoursGridTimeHeaderPanes;
    HashMap<String, Label> officeHoursGridTimeHeaderLabels;
    HashMap<String, Pane> officeHoursGridDayHeaderPanes;
    HashMap<String, Label> officeHoursGridDayHeaderLabels;
    HashMap<String, Pane> officeHoursGridTimeCellPanes;
    HashMap<String, Label> officeHoursGridTimeCellLabels;
    HashMap<String, Pane> officeHoursGridTACellPanes;
    HashMap<String, Label> officeHoursGridTACellLabels;

    /**
     * The contstructor initializes the user interface, except for
     * the full office hours grid, since it doesn't yet know what
     * the hours will be until a file is loaded or a new one is created.
     */
    public TAWorkspace(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;

        // WE'LL NEED THIS TO GET LANGUAGE PROPERTIES FOR OUR UI
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // INIT THE HEADER ON THE LEFT
        tasHeaderBox = new HBox();
        String tasHeaderText = props.getProperty(TAManagerProp.TAS_HEADER_TEXT.toString());
        tasHeaderLabel = new Label(tasHeaderText);
        tasHeaderBox.getChildren().add(tasHeaderLabel);

        // MAKE THE TABLE AND SETUP THE DATA MODEL
        taTable = new TableView();
        taTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TAData data = (TAData) app.getDataComponent();
        ObservableList<TeachingAssistant> tableData = data.getTeachingAssistants();
        taTable.setItems(tableData);
        String nameColumnText = props.getProperty(TAManagerProp.NAME_COLUMN_TEXT.toString());
        String emailColumnText = props.getProperty(TAManagerProp.EMAIL_COLUMN_TEXT.toString());
        nameColumn = new TableColumn(nameColumnText);
        emailColumn = new TableColumn(emailColumnText);
        nameColumn.setCellValueFactory(
                new PropertyValueFactory<TeachingAssistant, String>("name")
        );
        emailColumn.setCellValueFactory(
                new PropertyValueFactory<TeachingAssistant, String>("email")
        );
        taTable.getColumns().add(nameColumn);
        taTable.getColumns().add(emailColumn);

        // ADD BOX FOR ADDING A TA (name and email box)
        String namePromptText = props.getProperty(TAManagerProp.NAME_PROMPT_TEXT.toString());
        String emailPrompText = props.getProperty(TAManagerProp.EMAIL_PROMPT_TEXT.toString());
        String addButtonText = props.getProperty(TAManagerProp.ADD_BUTTON_TEXT.toString());
        nameTextField = new TextField();
        nameTextField.setPromptText(namePromptText);
        emailTextField = new TextField();
        emailTextField.setPromptText(emailPrompText);
        addButton = new Button(addButtonText);
        clearButton = new Button("Clear");
        addBox = new HBox();
        nameTextField.prefWidthProperty().bind(addBox.widthProperty().multiply(.4));
        emailTextField.prefWidthProperty().bind(addBox.widthProperty().multiply(.4));
        nameTextField.prefHeightProperty().bind(addBox.heightProperty().multiply(1));
        emailTextField.prefHeightProperty().bind(addBox.heightProperty().multiply(1));
        addButton.prefWidthProperty().bind(addBox.widthProperty().multiply(.2));
        clearButton.prefWidthProperty().bind(addBox.widthProperty().multiply(.2));
        addBox.getChildren().add(nameTextField);
        addBox.getChildren().add(emailTextField);
        addBox.getChildren().add(addButton);
        addBox.getChildren().add(clearButton);

        // INIT THE HEADER ON THE RIGHT
        officeHoursHeaderBox = new HBox();
        String officeHoursGridText = props.getProperty(TAManagerProp.OFFICE_HOURS_SUBHEADER.toString());
        officeHoursHeaderLabel = new Label(officeHoursGridText);
        officeHoursHeaderBox.getChildren().add(officeHoursHeaderLabel);
        
        // THESE WILL STORE PANES AND LABELS FOR OUR OFFICE HOURS GRID
        officeHoursGridPane = new GridPane();
        officeHoursGridTimeHeaderPanes = new HashMap();
        officeHoursGridTimeHeaderLabels = new HashMap();
        officeHoursGridDayHeaderPanes = new HashMap();
        officeHoursGridDayHeaderLabels = new HashMap();
        officeHoursGridTimeCellPanes = new HashMap();
        officeHoursGridTimeCellLabels = new HashMap();
        officeHoursGridTACellPanes = new HashMap();
        officeHoursGridTACellLabels = new HashMap();

        // ORGANIZE THE LEFT AND RIGHT PANES
        VBox leftPane = new VBox();
        leftPane.getChildren().add(tasHeaderBox);        
        leftPane.getChildren().add(taTable);        
        leftPane.getChildren().add(addBox);
        VBox rightPane = new VBox();
        rightPane.getChildren().add(officeHoursHeaderBox);
        rightPane.getChildren().add(officeHoursGridPane);
        
        // ADD COMBO BOXES
        TAData taData = (TAData)app.getDataComponent();
        ObservableList<String> hoursList = taData.generateStartEndTimeList(0, true);
        startBox = new ComboBox(hoursList);
        endBox = new ComboBox(hoursList);
        startTimeLabel = new Label("Start time:");
        endTimeLabel = new Label("End time:");
        startWrap = new HBox(startTimeLabel);
        startWrap.getChildren().add(startBox);
        startWrap.setAlignment(Pos.CENTER);
        endWrap = new HBox(endTimeLabel);
        endWrap.getChildren().add(endBox);
        endWrap.setAlignment(Pos.CENTER);
        officeHoursHeaderBox.getChildren().add(startWrap);
        officeHoursHeaderBox.getChildren().add(endWrap);
        officeHoursHeaderBox.setAlignment(Pos.CENTER_LEFT);
        
        // BOTH PANES WILL NOW GO IN A SPLIT PANE
        SplitPane sPane = new SplitPane(leftPane, new ScrollPane(rightPane));
        workspace = new BorderPane();
        
        // AND PUT EVERYTHING IN THE WORKSPACE
        ((BorderPane) workspace).setCenter(sPane);

        // MAKE SURE THE TABLE EXTENDS DOWN FAR ENOUGH
        taTable.prefHeightProperty().bind(workspace.heightProperty().multiply(1.9));

        // NOW LET'S SETUP THE EVENT HANDLING
        controller = new TAController(app);
        
        // CONTROLS FOR ADDING & EDITING TAs
        addButton.setOnAction(e -> {
            boolean added = false;
            boolean edited = false;
            if (addButton.getText().equals("Add TA")){
                added = false;
                added = controller.handleAddTA();
            } else {
                // Get the table
                TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
                TableView taTable = workspace.getTATable();
                // IS A TA SELECTED IN THE TABLE?
                Object selectedItem = taTable.getSelectionModel().getSelectedItem();
                TeachingAssistant ta = (TeachingAssistant) selectedItem;
                if (ta != null){
                    String oldName = ta.getName();
                    String oldEmail = ta.getEmail();
                    ta.setName(nameTextField.getText());
                    ta.setEmail(emailTextField.getText());
                    edited = false;
                    edited = controller.handleEditTA(ta,oldName,oldEmail);
                }
            }
            if (added || edited)
                app.getGUI().getAppFileController().markAsEdited(app.getGUI());     // flag as file as been modified
        });
        
        // CONTROL FOR CLEAR BUTTON
        clearButton.setOnAction(e -> {
            if (addButton.getText().equals("Edit TA")){
                nameTextField.clear();
                emailTextField.clear();
                addButton.setText("Add TA");
                // Get the table
                TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
                TableView taTable = workspace.getTATable();
                taTable.getSelectionModel().clearSelection();
                // AND SEND THE CARET BACK TO THE NAME TEXT FIELD FOR EASY DATA ENTRY
                nameTextField.requestFocus();
            }
        });
    }
    
    
    // WE'LL PROVIDE AN ACCESSOR METHOD FOR EACH VISIBLE COMPONENT
    // IN CASE A CONTROLLER OR STYLE CLASS NEEDS TO CHANGE IT
    
    
    public HBox getTAsHeaderBox() {
        return tasHeaderBox;
    }

    public Label getTAsHeaderLabel() {
        return tasHeaderLabel;
    }

    public TableView getTATable() {
        return taTable;
    }

    public HBox getAddBox() {
        return addBox;
    }

    public TextField getNameTextField() {
        return nameTextField;
    }
    
    public TextField getEmailTextField(){
        return emailTextField;
    }

    public Button getAddButton() {
        return addButton;
    }
    
    public Button getClearButton() {
        return clearButton;
    }

    public HBox getOfficeHoursSubheaderBox() {
        return officeHoursHeaderBox;
    }

    public Label getOfficeHoursSubheaderLabel() {
        return officeHoursHeaderLabel;
    }
    
    public Label getStartTimeLabel(){
        return startTimeLabel;
    }
    
    public Label getEndTimeLabel(){
        return endTimeLabel;
    }
    
    public ComboBox getStartBox(){
        return startBox;
    }
    
    public ComboBox getEndBox(){
        return endBox;
    }
    
    public HBox getStartWrapBox(){
        return startWrap;
    }
    
    public HBox getEndWrapBox(){
        return endWrap;
    }

    public GridPane getOfficeHoursGridPane() {
        return officeHoursGridPane;
    }

    public HashMap<String, Pane> getOfficeHoursGridTimeHeaderPanes() {
        return officeHoursGridTimeHeaderPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridTimeHeaderLabels() {
        return officeHoursGridTimeHeaderLabels;
    }

    public HashMap<String, Pane> getOfficeHoursGridDayHeaderPanes() {
        return officeHoursGridDayHeaderPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridDayHeaderLabels() {
        return officeHoursGridDayHeaderLabels;
    }

    public HashMap<String, Pane> getOfficeHoursGridTimeCellPanes() {
        return officeHoursGridTimeCellPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridTimeCellLabels() {
        return officeHoursGridTimeCellLabels;
    }

    public HashMap<String, Pane> getOfficeHoursGridTACellPanes() {
        return officeHoursGridTACellPanes;
    }

    public HashMap<String, Label> getOfficeHoursGridTACellLabels() {
        return officeHoursGridTACellLabels;
    }
    
    public String getCellKey(Pane testPane) {
        for (String key : officeHoursGridTACellLabels.keySet()) {
            if (officeHoursGridTACellPanes.get(key) == testPane) {
                return key;
            }
        }
        return null;
    }

    public Label getTACellLabel(String cellKey) {
        return officeHoursGridTACellLabels.get(cellKey);
    }

    public Pane getTACellPane(String cellPane) {
        return officeHoursGridTACellPanes.get(cellPane);
    }

    public String buildCellKey(int col, int row) {
        return "" + col + "_" + row;
    }

    public String buildCellText(int militaryHour, String minutes) {
        // FIRST THE START AND END CELLS
        int hour = militaryHour;
        if (hour > 12) {
            hour -= 12;
        }
        String cellText = "" + hour + ":" + minutes;
        if (militaryHour < 12) {
            cellText += "am";
        } else {
            cellText += "pm";
        }
        return cellText;
    }

    @Override
    public void resetWorkspace() {
        // CLEAR OUT THE GRID PANE
        officeHoursGridPane.getChildren().clear();
        
        // AND THEN ALL THE GRID PANES AND LABELS
        officeHoursGridTimeHeaderPanes.clear();
        officeHoursGridTimeHeaderLabels.clear();
        officeHoursGridDayHeaderPanes.clear();
        officeHoursGridDayHeaderLabels.clear();
        officeHoursGridTimeCellPanes.clear();
        officeHoursGridTimeCellLabels.clear();
        officeHoursGridTACellPanes.clear();
        officeHoursGridTACellLabels.clear();
    }
    
    @Override
    public void reloadWorkspace(AppDataComponent dataComponent) {
        TAData taData = (TAData)dataComponent;
        reloadOfficeHoursGrid(taData);
    }

    public void reloadOfficeHoursGrid(TAData dataComponent) {        
        ArrayList<String> gridHeaders = dataComponent.getGridHeaders();

        // ADD THE TIME HEADERS
        for (int i = 0; i < 2; i++) {
            addCellToGrid(dataComponent, officeHoursGridTimeHeaderPanes, officeHoursGridTimeHeaderLabels, i, 0);
            dataComponent.getCellTextProperty(i, 0).set(gridHeaders.get(i));
        }
        
        // THEN THE DAY OF WEEK HEADERS
        for (int i = 2; i < 7; i++) {
            addCellToGrid(dataComponent, officeHoursGridDayHeaderPanes, officeHoursGridDayHeaderLabels, i, 0);
            dataComponent.getCellTextProperty(i, 0).set(gridHeaders.get(i));            
        }
        
        // THEN THE TIME AND TA CELLS
        int row = 1;
        for (int i = dataComponent.getStartHour(); i < dataComponent.getEndHour(); i++) {
            // START TIME COLUMN
            int col = 0;
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row);
            dataComponent.getCellTextProperty(col, row).set(buildCellText(i, "00"));
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row+1);
            dataComponent.getCellTextProperty(col, row+1).set(buildCellText(i, "30"));

            // END TIME COLUMN
            col++;
            int endHour = i;
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row);
            dataComponent.getCellTextProperty(col, row).set(buildCellText(endHour, "30"));
            addCellToGrid(dataComponent, officeHoursGridTimeCellPanes, officeHoursGridTimeCellLabels, col, row+1);
            dataComponent.getCellTextProperty(col, row+1).set(buildCellText(endHour+1, "00"));
            col++;

            // AND NOW ALL THE TA TOGGLE CELLS
            while (col < 7) {
                addCellToGrid(dataComponent, officeHoursGridTACellPanes, officeHoursGridTACellLabels, col, row);
                addCellToGrid(dataComponent, officeHoursGridTACellPanes, officeHoursGridTACellLabels, col, row+1);
                col++;
            }
            row += 2;
        }
        
        // CONTROLS FOR TOGGLING TA OFFICE HOURS
        for (Pane p : officeHoursGridTACellPanes.values()) {
            p.setOnMouseClicked(e -> {
                boolean toggled = false;
                toggled = controller.handleCellToggle((Pane) e.getSource());
                if (toggled)
                    app.getGUI().getAppFileController().markAsEdited(app.getGUI());     // flag as file has been modified
            });
        }
        
        // Handle Delete TA from table by pressing "Delete" Key
        // Get the table
        TAWorkspace workspace = (TAWorkspace)app.getWorkspaceComponent();
        TableView taTable = workspace.getTATable();
        
        taTable.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            // IS A TA SELECTED IN THE TABLE?
            Object selectedItem = taTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null){
                TeachingAssistant ta = (TeachingAssistant)selectedItem;
                if (ev.getCode() == KeyCode.BACK_SPACE || ev.getCode() == KeyCode.DELETE){
                    controller.handleDeleteTAfromTable(ta);
                    app.getGUI().getAppFileController().markAsEdited(app.getGUI());         // flag as file has been modified
                    taTable.getSelectionModel().clearSelection();                           // clear selected item
                } else if (ev.getCode() == KeyCode.UP || ev.getCode() == KeyCode.DOWN) {
                    int indexOfOldTA = ((TAData)app.getDataComponent()).getTeachingAssistants().indexOf(ta);
                    int indexOfNewTA;
                    TeachingAssistant newSelectedTA = null;
                    
                    if (ev.getCode() == KeyCode.UP){
                        if (indexOfOldTA == 0){                 // if current TA is the first one, so can't go UP anymore
                            nameTextField.clear();
                            emailTextField.clear();
                            addButton.setText("Add TA");
                        } else {
                            indexOfNewTA = indexOfOldTA - 1;
                            newSelectedTA = (TeachingAssistant)((TAData)app.getDataComponent()).getTeachingAssistants().get(indexOfNewTA);
                        }
                    } else if (ev.getCode() == KeyCode.DOWN) {
                        if (indexOfOldTA == ((TAData)app.getDataComponent()).getTeachingAssistants().size()-1){
                            nameTextField.clear();
                            emailTextField.clear();
                            addButton.setText("Add TA");
                        } else {
                            indexOfNewTA = indexOfOldTA + 1;
                            newSelectedTA = (TeachingAssistant)((TAData)app.getDataComponent()).getTeachingAssistants().get(indexOfNewTA);
                        }
                    }
                    
                    if (newSelectedTA != null) {
                        nameTextField.setText(newSelectedTA.getName());                     // put text and email into textfield for editing
                        emailTextField.setText(newSelectedTA.getEmail());
                        addButton.setText("Edit TA");
                    }
                }
            }
        });
        
        // HANDLE EDIT TA AT TEXTFIELD
        taTable.setOnMouseClicked(e -> {
            // IS A TA SELECTED IN THE TABLE?
            Object selectedItem = taTable.getSelectionModel().getSelectedItem();
            TeachingAssistant ta = (TeachingAssistant) selectedItem;
            if (ta != null) {               // if TA selected
                nameTextField.setText(ta.getName());                    // put text and email into textfield for editing
                emailTextField.setText(ta.getEmail());
                addButton.setText("Edit TA");
            } 
        });
        
        nameTextField.clear();
        emailTextField.clear();
        addButton.setText("Add TA");
        
        // AND MAKE SURE ALL THE COMPONENTS HAVE THE PROPER STYLE
        TAStyle taStyle = (TAStyle)app.getStyleComponent();
        taStyle.initOfficeHoursGridStyle();
    }
    
    public void addCellToGrid(TAData dataComponent, HashMap<String, Pane> panes, HashMap<String, Label> labels, int col, int row) {       
        // MAKE THE LABEL IN A PANE
        Label cellLabel = new Label("");
        HBox cellPane = new HBox();
        cellPane.setAlignment(Pos.CENTER);
        cellPane.getChildren().add(cellLabel);

        // BUILD A KEY TO EASILY UNIQUELY IDENTIFY THE CELL
        String cellKey = dataComponent.getCellKey(col, row);
        cellPane.setId(cellKey);
        cellLabel.setId(cellKey);
        
        // NOW PUT THE CELL IN THE WORKSPACE GRID
        officeHoursGridPane.add(cellPane, col, row);
        
        // AND ALSO KEEP IN IN CASE WE NEED TO STYLIZE IT
        panes.put(cellKey, cellPane);
        labels.put(cellKey, cellLabel);
        
        // AND FINALLY, GIVE THE TEXT PROPERTY TO THE DATA MANAGER
        // SO IT CAN MANAGE ALL CHANGES
        dataComponent.setCellProperty(col, row, cellLabel.textProperty());        
    }
}
