package com.mynetpcb.gerber.attribute.drill;

import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.attribute.AbstractAttribute.Type;

public class ViaDrillAttribute extends AbstractAttribute {
    public ViaDrillAttribute() {
        super(Type.ViaDrill,"TA.AperFunction",Type.ViaDrill.toString(),"");
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
