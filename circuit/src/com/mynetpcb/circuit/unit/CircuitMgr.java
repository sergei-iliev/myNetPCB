package com.mynetpcb.circuit.unit;

import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.symbol.shape.FontLabel;
import com.mynetpcb.symbol.unit.Symbol;

public class CircuitMgr extends UnitMgr {
    private static CircuitMgr circuitMgr;

    private CircuitMgr() {

    }


    public static synchronized CircuitMgr getInstance() {
        if (circuitMgr == null) {
            circuitMgr = new CircuitMgr();
        }
        return circuitMgr;
    }
    public SCHSymbol createSCHSymbol(Symbol module) {
        SCHSymbol schsymbol = new SCHSymbol();
        for (Shape symbol : module.getShapes()) {
            if (symbol instanceof FontLabel) {
                if (((FontLabel)symbol).getTexture().getTag().equals("unit")) {
                    schsymbol.getTextureByTag("unit").copy(((FontLabel)symbol).getTexture());
                    continue;
                }
                if (((FontLabel)symbol).getTexture().getTag().equals("reference")) {
                    schsymbol.getTextureByTag("reference").copy(((FontLabel)symbol).getTexture());
                    continue;
                }
            }
            try {
                schsymbol.add(symbol.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        schsymbol.setDisplayName(module.getUnitName());
        schsymbol.setType(module.getType());
        return schsymbol;
    }
    
}
