package com.mynetpcb.gerber.attribute.aperture;

import com.mynetpcb.gerber.attribute.AbstractAttribute;


public class ViaPadAttribute extends AbstractAttribute {
    public ViaPadAttribute() {
        super(Type.ViaPad,"TA.AperFunction",Type.ViaPad.toString(),"");
    }
    
    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
