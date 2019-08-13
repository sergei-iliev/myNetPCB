package com.mynetpcb.gerber.attribute.aperture;

import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.attribute.AbstractAttribute.Type;

@Deprecated
public class ThermalReliefPadAttribute extends AbstractAttribute{
    public ThermalReliefPadAttribute() {
        super(Type.ThermalReliefPad,"TA.AperFunction",Type.ThermalReliefPad.toString(),"");
    }
    
    @Override
    public String print() {        
        return ("%"+String.format("%s,%s",command,name)+"*%");
    }    
}
