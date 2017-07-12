package com.mynetpcb.circuit.dialog;


import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.dialog.SymbolEditorDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;

public class SymbolInlineEditorDialog extends SymbolEditorDialog{
    
    private SymbolContainer result;
    
    public SymbolInlineEditorDialog(Window window, String caption, SymbolContainer symbolContainer) {
        super(window, caption, symbolContainer);
        LoadButton.setEnabled(false);
        AddSymbolButton.setEnabled(false);
        //symbolComponent.getEventMgr().nullifyEventHandle("symbol");
    }
    public SymbolContainer getResult(){
       return result;    
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Save")) {
          result=super.symbolComponent.getModel();
          this.setVisible(false);
          return;
        }
       super.actionPerformed(e);
    }    
}
