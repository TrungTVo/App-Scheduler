package tam.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import djf.components.AppDataComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javafx.beans.property.StringProperty;
import properties_manager.PropertiesManager;
import tam.TAManagerApp;
import tam.TAManagerProp;
import tam.jtps.AddingTA_Transaction;
import tam.jtps.jTPS;
import tam.jtps.jTPS_Transaction;
import tam.workspace.TAWorkspace;

/**
 * This is the data component for TAManagerApp. It has all the data needed
 * to be set by the user via the User Interface and file I/O can set and get
 * all the data from this object
 * 
 * @author Richard McKenna - Trung Vo
 */
public class TAData implements AppDataComponent {

    // WE'LL NEED ACCESS TO THE APP TO NOTIFY THE GUI WHEN DATA CHANGES
    TAManagerApp app;

    // NOTE THAT THIS DATA STRUCTURE WILL DIRECTLY STORE THE
    // DATA IN THE ROWS OF THE TABLE VIEW
    ObservableList<TeachingAssistant> teachingAssistants;

    // THIS WILL STORE ALL THE OFFICE HOURS GRID DATA, WHICH YOU
    // SHOULD NOTE ARE StringProperty OBJECTS THAT ARE CONNECTED
    // TO UI LABELS, WHICH MEANS IF WE CHANGE VALUES IN THESE
    // PROPERTIES IT CHANGES WHAT APPEARS IN THOSE LABELS
    HashMap<String, StringProperty> officeHours;
    
    // THESE ARE THE LANGUAGE-DEPENDENT VALUES FOR
    // THE OFFICE HOURS GRID HEADERS. NOTE THAT WE
    // LOAD THESE ONCE AND THEN HANG ON TO THEM TO
    // INITIALIZE OUR OFFICE HOURS GRID
    ArrayList<String> gridHeaders;

    // THESE ARE THE TIME BOUNDS FOR THE OFFICE HOURS GRID. NOTE
    // THAT THESE VALUES CAN BE DIFFERENT FOR DIFFERENT FILES, BUT
    // THAT OUR APPLICATION USES THE DEFAULT TIME VALUES AND PROVIDES
    // NO MEANS FOR CHANGING THESE VALUES
    int startHour;
    int endHour;
    String startMin;
    String endMin;
    
    // DEFAULT VALUES FOR START AND END HOURS IN MILITARY HOURS
    public static final int MIN_START_HOUR = 0;
    public static final int MAX_END_HOUR = 23;

    /**
     * This constructor will setup the required data structures for
     * use, but will have to wait on the office hours grid, since
     * it receives the StringProperty objects from the Workspace.
     * 
     * @param initApp The application this data manager belongs to. 
     */
    public TAData(TAManagerApp initApp) {
        // KEEP THIS FOR LATER
        app = initApp;

        // CONSTRUCT THE LIST OF TAs FOR THE TABLE
        teachingAssistants = FXCollections.observableArrayList();

        // THESE ARE THE DEFAULT OFFICE HOURS
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        startMin = new String("00");
        endMin = new String("00");
        
        //THIS WILL STORE OUR OFFICE HOURS
        officeHours = new HashMap();
        
        // THESE ARE THE LANGUAGE-DEPENDENT OFFICE HOURS GRID HEADERS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        ArrayList<String> timeHeaders = props.getPropertyOptionsList(TAManagerProp.OFFICE_HOURS_TABLE_HEADERS);
        ArrayList<String> dayHeaders = props.getPropertyOptionsList(TAManagerProp.DAYS_OF_WEEK);
        gridHeaders = new ArrayList();
        gridHeaders.addAll(timeHeaders);
        gridHeaders.addAll(dayHeaders);
    }
    
    /**
     * Called each time new work is created or loaded, it resets all data
     * and data structures such that they can be used for new values.
     */
    @Override
    public void resetData() {
        startHour = MIN_START_HOUR;
        endHour = MAX_END_HOUR;
        startMin = "00";
        endMin = "00";
        teachingAssistants.clear();
        officeHours.clear();
    }
    
    // ACCESSOR METHODS

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
    
    public String getStartMin() {
        return startMin;
    }
    
    public String getEndMin() {
        return endMin;
    }
    
    public void setStartHour(int startHour){
        this.startHour = startHour;
    }
    
    public void setEndHour(int endHour){
        this.endHour = endHour;
    }
    
    public void setStartMin(String startMin){
        this.startMin = startMin;
    }
    
    public void setEndMin(String endMin){
        this.endMin = endMin;
    }
    
    public double differenceRowsBetweenStartAndEnd(){
        double tempStartHr = (startMin.equals("30"))? (startHour+0.5):startHour;
        double tempEndHr = (endMin.equals("30"))? (endHour+0.5):endHour;
        return (int)(2*(tempEndHr-tempStartHr)-1);
    }
    
    public ArrayList<String> getGridHeaders() {
        return gridHeaders;
    }

    public ObservableList getTeachingAssistants() {
        return teachingAssistants;
    }
    
    public String getCellKey(int col, int row) {
        return col + "_" + row;
    }

    public StringProperty getCellTextProperty(int col, int row) {
        String cellKey = getCellKey(col, row);
        return officeHours.get(cellKey);
    }

    public HashMap<String, StringProperty> getOfficeHours() {
        return officeHours;
    }
    
    public void setOfficeHours(HashMap<String, StringProperty> officeHours){
        this.officeHours = officeHours;
    }
    
    public int getNumRows() {
        return ((endHour - startHour) * 2) + 1;
    }
    
    // generate a lsit of start & end time for combo boxes
    public ObservableList<String> generateStartEndTimeList(int startTime){
        ObservableList<String> res = FXCollections.observableArrayList();
        for (int i=1; i<=24; i++){
            res.add(getTimeString(startTime, true));
            startTime++;
        }
        return res;
    }
    
    public String getTimeString(int militaryHour, boolean onHour) {
        String minutesText = "00";
        if (!onHour) {
            minutesText = "30";
        }

        // FIRST THE START AND END CELLS
        int hour = militaryHour;
        if (hour > 12) {
            hour -= 12;
        }
        String cellText = "" + hour + ":" + minutesText;
        if (militaryHour < 12) {
            cellText += "am";
        } else {
            cellText += "pm";
        }
        return cellText;
    }
    
    public String getCellKey(String day, String time) {
        int col = gridHeaders.indexOf(day);
        int row = 1;
        int hour = Integer.parseInt(time.substring(0, time.indexOf("_")));
        int milHour = hour;
        if (hour < startHour)
            milHour += 12;
        row += (milHour - startHour) * 2;
        if (time.contains("_30"))
            row += 1;
        return getCellKey(col, row);
    }
    
    public TeachingAssistant getTA(String testName) {
        for (TeachingAssistant ta : teachingAssistants) {
            if (ta.getName().equals(testName)) {
                return ta;
            }
        }
        return null;
    }
    
    /**
     * This method is for giving this data manager the string property
     * for a given cell.
     */
    public void setCellProperty(int col, int row, StringProperty prop) {
        String cellKey = getCellKey(col, row);
        officeHours.put(cellKey, prop);
    }    
    
    /**
     * This method is for setting the string property for a given cell.
     */
    public void setGridProperty(ArrayList<ArrayList<StringProperty>> grid,
                                int column, int row, StringProperty prop) {
        grid.get(row).set(column, prop);
    }
    
    private void initOfficeHours(int initStartHour, int initEndHour) {
        // NOTE THAT THESE VALUES MUST BE PRE-VERIFIED
        startHour = initStartHour;
        endHour = initEndHour;
        
        // EMPTY THE CURRENT OFFICE HOURS VALUES
        officeHours.clear();
            
        // WE'LL BUILD THE USER INTERFACE COMPONENT FOR THE
        // OFFICE HOURS GRID AND FEED THEM TO OUR DATA
        // STRUCTURE AS WE GO
        TAWorkspace workspaceComponent = (TAWorkspace)app.getWorkspaceComponent();
        workspaceComponent.reloadOfficeHoursGrid(this);
    }
    
    public void initHours(String startHourText, String endHourText) {
        int initStartHour = Integer.parseInt(startHourText);
        int initEndHour = Integer.parseInt(endHourText);
        if ((initStartHour >= MIN_START_HOUR)
                && (initEndHour <= MAX_END_HOUR)
                && (initStartHour <= initEndHour)) {
            // THESE ARE VALID HOURS SO KEEP THEM
            initOfficeHours(initStartHour, initEndHour);
        }
    }

    public boolean containsTA(String testName, ObservableList<TeachingAssistant> listTAs) {
        for (TeachingAssistant ta : listTAs) {
            if (ta.getName().equals(testName)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsTAEmail(String taEmail, ObservableList<TeachingAssistant> listTAs){
        for (TeachingAssistant ta : listTAs) {
            if (ta.getEmail().equals(taEmail)) {
                return true;
            }
        }
        return false;
    }

    public void addTA(String initName, String initEmail) {
        // MAKE THE TA
        TeachingAssistant ta = new TeachingAssistant(initName, initEmail);

        // ADD THE TA
        if (!containsTA(initName, teachingAssistants)) {
            // push current state into stack before transaction
            jTPS_Transaction transaction = new AddingTA_Transaction(ta, teachingAssistants);
            ((TAWorkspace)app.getWorkspaceComponent()).getJTPS().addTransaction(transaction);
            //teachingAssistants.add(ta);
        }

        // SORT THE TAS
        Collections.sort(teachingAssistants);
    }
    
    public ObservableList<TeachingAssistant> clone(ObservableList<TeachingAssistant> oldList){
        ObservableList<TeachingAssistant> newList = FXCollections.observableArrayList();
        for (TeachingAssistant ta:oldList){
            newList.add(ta);
        }
        return newList;
    }

    public void addOfficeHoursReservation(String day, String time, String taName) {
        String cellKey = getCellKey(day, time);
        toggleTAOfficeHours(cellKey, taName);
    }
    
    /**
     * This function toggles the taName in the cell represented
     * by cellKey. Toggle means if it's there it removes it, if
     * it's not there it adds it.
     */
    public void toggleTAOfficeHours(String cellKey, String taName) {
        StringProperty cellProp = officeHours.get(cellKey);
        String cellText = cellProp.getValue();
        if (!isCellPaneHasTAName(cellText, taName)){
            cellProp.setValue(cellText + "\n" + taName);
            officeHours.put(cellKey, cellProp);
        } else {
            removeTAFromCell(cellProp, taName, null, false, cellKey);
        }
    }
    
    // helper method to check if TA's name in the TA cell pane or not
    public boolean isCellPaneHasTAName(String cellText, String taName){
        String[] nameList = cellText.split("\n");
        for (String item:nameList){
            if (item.trim().equals(taName))
                return true;
        }
        return false;
    }
    
    /**
     * This method removes taName from the office grid cell
     * represented by cellProp.
     */
    public void removeTAFromCell(StringProperty cellProp, String taName, String nameToUpdate, boolean edited, String cellKey) {
        // GET THE CELL TEXT
        String cellText = cellProp.getValue();
        // IS IT THE ONLY TA IN THE CELL?
        if (cellText.equals("\n"+taName)) {
            cellProp.setValue("");
            if (edited){
                cellProp.setValue("\n"+nameToUpdate);
            }
        } else {
            int i = 0;
            int startIndex, index;
            do {
                startIndex = cellText.indexOf("\n"+taName, i);
                index = startIndex+1+taName.length();
                if (index < cellText.length() && cellText.charAt(index) != '\n' ){
                    i++;
                }
            } while (index < cellText.length() && cellText.charAt(index) != '\n' );
            
            StringBuilder newText = new StringBuilder();
            if (index < cellText.length()){
                cellText = cellText.substring(0, startIndex) + cellText.substring(index);
                if (edited){
                    newText = new StringBuilder(cellText);
                    newText.insert(startIndex, "\n"+nameToUpdate);
                }
            } else {
                cellText = cellText.substring(0, startIndex);
                if (edited){
                    newText = new StringBuilder(cellText);
                    newText.append("\n"+nameToUpdate);
                }
            }
            if (!edited)
                cellProp.setValue(cellText);
            else
                cellProp.setValue(newText.toString());
        }
        officeHours.put(cellKey, cellProp);
    }
}