package com.mynetpcb.gerber.attribute.drill;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

public class MechanicalDrillAttribute extends AbstractAttribute {
    public MechanicalDrillAttribute() {
        super(Type.MechanicalDrill,"TA.AperFunction",Type.MechanicalDrill.toString(),"");
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
