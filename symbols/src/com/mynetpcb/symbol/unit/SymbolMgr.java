package com.mynetpcb.symbol.unit;


import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.UnitMgr;


public final class SymbolMgr extends UnitMgr<Symbol,Shape> {
    private static SymbolMgr symbolMgr;
    
    private SymbolMgr() {
    
    }
    
    public static synchronized SymbolMgr getInstance() {
      if(symbolMgr==null)
        symbolMgr=new SymbolMgr();
      return symbolMgr;
    }   
        
}

