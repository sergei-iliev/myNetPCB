package com.mynetpcb.circuit.dialog;

import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.dialog.SymbolEditorDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SymbolInlineEditorDialog extends SymbolEditorDialog{
    
    private SymbolContainer result;
    
    public SymbolInlineEditorDialog(Window window, String caption, SymbolContainer symbolContainer) {
        super(window, caption, symbolContainer);
        addWindowListener(new WindowAdapter() {

            @Override
          public void windowOpened(WindowEvent e) {
                //position symbol to center
                symbolComponent.setViewportPosition(symbolComponent.getModel().getUnit().getWidth() / 2,
                		symbolComponent.getModel().getUnit().getHeight() / 2);  
                symbolComponent.Repaint();
          }
          });        
    }
    public SymbolContainer getResult(){
       return result;    
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==SaveButton) {
          result=super.symbolComponent.getModel();
          this.setVisible(false);
          return;
        }
       super.actionPerformed(e);
    }    
}