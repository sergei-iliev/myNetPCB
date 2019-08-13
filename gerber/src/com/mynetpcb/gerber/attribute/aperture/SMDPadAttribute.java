package com.mynetpcb.gerber.attribute.aperture;

import com.mynetpcb.gerber.attribute.AbstractAttribute;


public class SMDPadAttribute extends AbstractAttribute {
    public SMDPadAttribute() {
        super(Type.SMDPad,"TA.AperFunction",Type.SMDPad.toString(),"CuDef");
    }

    @Override
    public String print() {
        return ("%"+String.format("%s,%s,%s",command,name,value)+"*%");  
    }
}
