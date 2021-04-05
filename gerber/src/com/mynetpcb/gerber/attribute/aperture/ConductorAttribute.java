package com.mynetpcb.gerber.attribute.aperture;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

public class ConductorAttribute  extends AbstractAttribute {
    public ConductorAttribute() {
        super(Type.Conductor,"TA.AperFunction",Type.Conductor.toString(),"");
    }


    @Override
    public String print() {        
        return ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
