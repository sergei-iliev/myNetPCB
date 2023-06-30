package com.mynetpcb.board.dialog;

import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.dialog.FootprintEditorDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FootprintInlineEditorDialog extends FootprintEditorDialog {
    
    private FootprintContainer result;
    
    public FootprintInlineEditorDialog(Window window, String caption, FootprintContainer footprintContainer) {
        super(window, caption, footprintContainer);
        addWindowListener(new WindowAdapter() {

          @Override
        public void windowOpened(WindowEvent e) {
              //position footprint to center
              footprintComponent.setViewportPosition(footprintComponent.getModel().getUnit().getWidth() / 2,
                      footprintComponent.getModel().getUnit().getHeight() / 2);  
              footprintComponent.Repaint();
        }
        });
    }
    
    public FootprintContainer getResult(){
       return result;    
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==SaveButton) {
          result=super.footprintComponent.getModel();
          this.setVisible(false);
          return;
        }
       super.actionPerformed(e);
    }
    
    
}
