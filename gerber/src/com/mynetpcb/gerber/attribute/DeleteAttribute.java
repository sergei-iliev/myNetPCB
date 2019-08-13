package com.mynetpcb.gerber.attribute;


public class DeleteAttribute extends AbstractAttribute{
    public DeleteAttribute() {
        super("TD.AperFunction","","");
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s",command)+"*%");
    }
}
