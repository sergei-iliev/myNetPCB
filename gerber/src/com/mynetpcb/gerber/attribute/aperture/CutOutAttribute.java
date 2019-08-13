package com.mynetpcb.gerber.attribute.aperture;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

public class CutOutAttribute extends AbstractAttribute {
    public CutOutAttribute() {
        super(Type.CutOut,"TA.AperFunction",Type.CutOut.toString(),"");
    }
    
    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
