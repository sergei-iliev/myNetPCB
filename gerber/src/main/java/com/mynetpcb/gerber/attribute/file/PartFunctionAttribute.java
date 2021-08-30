package com.mynetpcb.gerber.attribute.file;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

/*
 * Single part only
 */
public class PartFunctionAttribute extends AbstractAttribute {
    private enum Type{
        Single,
        CustomerPanel,
        ProductionPanel,
        Coupon,
        Other
    }
    public PartFunctionAttribute() {
        super("TF.Part", Type.Single.toString(),"");
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
