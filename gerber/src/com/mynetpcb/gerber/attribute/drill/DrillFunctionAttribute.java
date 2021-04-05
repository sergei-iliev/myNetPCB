package com.mynetpcb.gerber.attribute.drill;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

/*
 * 2 sided board!!!!
 */
public class DrillFunctionAttribute extends AbstractAttribute {
    public enum Type{
        PTH,NPTH,Blind,Buried
    }
    
    public enum Plate{
        Plated,
        NonPlated
    }
    public DrillFunctionAttribute() {
        super("TF.FileFunction", Plate.NonPlated.toString()+",1,2", Type.NPTH.toString());
    }
    
    public DrillFunctionAttribute(Plate plated,Type type) {
        super("TF.FileFunction",plated.toString()+",1,2",type.toString());
    }
    
    @Override
    public String print() {
        return  ("%"+String.format("%s,%s,%s",command,name,value)+",Drill*%");
    }
}
