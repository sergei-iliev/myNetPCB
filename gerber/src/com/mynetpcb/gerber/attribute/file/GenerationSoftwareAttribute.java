package com.mynetpcb.gerber.attribute.file;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

public class GenerationSoftwareAttribute extends AbstractAttribute {
    public GenerationSoftwareAttribute(String applicationName,String applicationVersion) {
        super("TF.GenerationSoftware",applicationName,applicationVersion);
    }

    @Override
    public String print() {
        return ("%"+String.format("%s,%s,%s",command,name,value)+"*%");        
    }
}
