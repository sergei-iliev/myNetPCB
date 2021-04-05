package com.mynetpcb.gerber.attribute.drill;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

public class ComponentDrillAttribute extends AbstractAttribute {
    public ComponentDrillAttribute() {
        super(Type.ComponentDrill,"TA.AperFunction",Type.ComponentDrill.toString(),"");
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
