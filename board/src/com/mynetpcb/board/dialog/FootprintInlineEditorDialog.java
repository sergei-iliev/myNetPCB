package com.mynetpcb.board.dialog;

import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.dialog.FootprintEditorDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;

public class FootprintInlineEditorDialog extends FootprintEditorDialog {
    
    private FootprintContainer result;
    
    public FootprintInlineEditorDialog(Window window, String caption, FootprintContainer footprintContainer) {
        super(window, caption, footprintContainer);
        LoadButton.setEnabled(false);
        AddFootprintButton.setEnabled(false);
    }
    
    public FootprintContainer getResult(){
       return result;    
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Save")) {
          result=super.footprintComponent.getModel();
          this.setVisible(false);
          return;
        }
       super.actionPerformed(e);
    }
    
    
}
