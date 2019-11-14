package com.mynetpcb.board.dialog.save;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.ReadRepositoryLocal;
import com.mynetpcb.core.capi.io.WriteUnitLocal;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.WriteConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.dialog.save.AbstractSaveDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class BoardSaveDialog extends AbstractSaveDialog{
    
    public BoardSaveDialog(Window owner,UnitComponent component,boolean isonline) {
        super(owner,component,"Save", isonline);
    }
    
    @Override
    protected void doBody(){
      super.doBody();
      libraryName.setText("Project");
      categoryName.setVisible(false);
      categoryCombo.setVisible(false);
    }
    
    @Override
    protected void prepareData() {
      super.prepareData();     
      //***Read Libraries
      if (!isonline) {
          Command reader =
              new ReadRepositoryLocal(this, Configuration.get().getBoardsRoot(),
                                      JComboBox.class);
          CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal", reader);
      } else {
              Command reader =
                  new ReadConnector(this,
                                    new RestParameterMap.ParameterBuilder("/boards").addURI("projects").build(),
                                    JComboBox.class);
              CommandExecutor.INSTANCE.addTask("ReadProjects", reader);
      }
    }

    public void actionPerformed(ActionEvent e) { 
        super.actionPerformed(e);

        if (e.getSource() == SaveButton) {
        if (fileNameText.getText() == null ||
            fileNameText.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Name must not be empty", "Error",
                                          JOptionPane.ERROR_MESSAGE);             
            return;
        }
        if(libraryCombo.getSelectedItem()==null){
            JOptionPane.showMessageDialog(this, "Project name must not be empty", "Error",
                                          JOptionPane.ERROR_MESSAGE);   
            return;
        }
        if (!isonline) {

            Command writer =
                new WriteUnitLocal(this, getComponent().getModel().Format(), Configuration.get().getBoardsRoot(),
                                   (String)libraryCombo.getSelectedItem(), null,
                                   fileNameText.getText(),
                                   overrideCheck.isSelected(),
                                   WriteUnitLocal.class);
            CommandExecutor.INSTANCE.addTask("WriteUnitLocal", writer);
        } else {

            Command writer =
                new WriteConnector(this, getComponent().getModel().Format(), new RestParameterMap.ParameterBuilder("/boards").addURI("projects").addURI((String)libraryCombo.getSelectedItem()).addAttribute("boardName",fileNameText.getText()).addAttribute("overwrite",String.valueOf(overrideCheck.isSelected())).build(),
                                   WriteConnector.class);
            CommandExecutor.INSTANCE.addTask("WriteUnit", writer);
        }
      }
    }
    
}
