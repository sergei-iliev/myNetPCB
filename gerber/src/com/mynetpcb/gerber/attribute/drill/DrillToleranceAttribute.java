package com.mynetpcb.gerber.attribute.drill;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

public class DrillToleranceAttribute extends AbstractAttribute{
    
    public DrillToleranceAttribute(String x,String y) {
        super("TA.DrillTolerance",x,y);
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s,%s,%s",command,name,value)+"*%");
    }
}
