/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tam.jtps;

import javafx.collections.ObservableList;
import tam.data.TeachingAssistant;
/**
 *
 * @author trungvo
 */
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
    }

    @Override
    public void undoTransaction() {
        if (taList.contains(ta))
            taList.remove(ta);
    }
       
}
