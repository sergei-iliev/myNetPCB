package com.mynetpcb.gerber.attribute;

import com.mynetpcb.gerber.capi.Printable;
import com.mynetpcb.pad.shape.Pad;

import java.util.Objects;

/*
 * Meta gerber data
 */
public abstract class AbstractAttribute implements Printable{
    public enum Type{
        ViaPad,
        ComponentDrill,
        ViaDrill,
        MechanicalDrill,
        Conductor,
        ComponentPad,
        CutOut,
        ThermalReliefPad,
        SMDPad;
        
        public static Type resolvePad(Pad.Type pad){
            
            if(Pad.Type.SMD==pad){ 
                 return SMDPad; 
            }else if(Pad.Type.THROUGH_HOLE==pad){
                return ComponentPad;  
            }else if(Pad.Type.CONNECTOR==pad){
                return ComponentPad;     
            }else{            
                throw new IllegalStateException("Unknown pad:"+pad);
            }
        }
    }
    
    protected  String command;
    protected  String name;
    protected  String value;   //optional
    private Type type;
    
    public AbstractAttribute(String command,String name,String value) {
        this.command = command;
        this.name=name;
        this.value = value;
    }
    
    public AbstractAttribute(Type type,String command,String name,String value) {
        this.type=type;
        this.command = command;
        this.name=name;
        this.value = value;
    }
    
    public Type getType(){
        return type;
    }
    
    public String getIdentity(){
        StringBuffer sb=new StringBuffer();
        sb.append(command);
        sb.append(name);
        sb.append(value);
        return sb.toString();
    }
    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (command == null ? 0 : command.hashCode());
        hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
        hashCode = 31 * hashCode + (value == null ? 0 : value.hashCode());
        return hashCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractAttribute)) {
            return false;
        }
        AbstractAttribute other = (AbstractAttribute)obj;
        return Objects.equals(this.command, other.command)&&Objects.equals(this.name, other.name)&&Objects.equals(this.value, other.value);        

    }
    
    
}

