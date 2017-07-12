package com.mynetpcb.gerber.aperture.type;

import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.Printable;


public abstract class ApertureDefinition implements Printable{
    public enum ApertureShape
    {
      CIRCLE,                        
      RECTANGLE,                     
      OBROUND,
      POLYGON                       
    };
    
    protected int code;                        
    protected ApertureShape shape;
    protected AbstractAttribute attribute;
    
    public ApertureDefinition(ApertureShape shape){
      this.shape=shape;
    }
    
    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setShape(ApertureDefinition.ApertureShape shape) {
        this.shape = shape;
    }

    public ApertureDefinition.ApertureShape getShape() {
        return shape;
    }


    public void setAttribute(AbstractAttribute attribute) {
        this.attribute = attribute;
    }

    public AbstractAttribute getAttribute() {
        return attribute;
    }

}
