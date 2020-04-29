package com.mynetpcb.symbol.component;

import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.line.DefaultBendingProcessorFactory;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.SymbolEventMgr;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Color;

public class SymbolComponent extends UnitComponent<Symbol, Shape, SymbolContainer> implements CommandListener{
    
    public SymbolComponent(DialogFrame dialogFrame) {
        super(dialogFrame);
        this.setModel(new SymbolContainer());
        this.eventMgr=new SymbolEventMgr(this);
        this.setMode(Mode.COMPONENT_MODE);
        this.setBackground(Color.WHITE);
        //this.loadDialogBuilder=new FootprintLoadDialog.Builder();
        //this.popup=new FootprintPopupMenu(this);
        bendingProcessorFactory=new DefaultBendingProcessorFactory();
        setLineBendingProcessor(bendingProcessorFactory.resolve("defaultbend",null));
    }

    @Override
    public void reload() {
        // TODO Implement this method
    }

    @Override
    public void Import(String string) {
        // TODO Implement this method
    }

    @Override
    public void OnStart(Class<?> c) {
        // TODO Implement this method
    }

    @Override
    public void OnRecive(String string, Class<?> c) {
        // TODO Implement this method

    }

    @Override
    public void OnFinish(Class<?> c) {
        // TODO Implement this method
    }

    @Override
    public void OnError(String string) {
        // TODO Implement this method
    }
}
