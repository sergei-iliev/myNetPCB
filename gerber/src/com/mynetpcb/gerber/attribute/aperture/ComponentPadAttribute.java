package com.mynetpcb.gerber.attribute.aperture;

import com.mynetpcb.gerber.attribute.AbstractAttribute;


public class ComponentPadAttribute extends AbstractAttribute {
    public ComponentPadAttribute() {
        super(Type.ComponentPad,"TA.AperFunction",Type.ComponentPad.toString(),"");
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
