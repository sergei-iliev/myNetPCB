package com.mynetpcb.gerber.attribute.file;

import com.mynetpcb.gerber.attribute.AbstractAttribute;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CreationDateAttribute extends AbstractAttribute {
    public CreationDateAttribute() {
        super("TF.CreationDate",ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")),"");
    }

    @Override
    public String print() {        
        return ("%"+String.format("%s,%s",command,name)+"*%");
    }
}
