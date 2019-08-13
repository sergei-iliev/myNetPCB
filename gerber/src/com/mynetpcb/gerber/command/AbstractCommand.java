package com.mynetpcb.gerber.command;

import com.mynetpcb.gerber.capi.Printable;


public abstract class AbstractCommand implements Printable{
    public enum Type{
        INTERPOLATE_OPERATION("D01"),
        MOVE_OPERATION("D02"), 
        FLASH_OPERATION("D03"), 
        SET_CURRENT_APERTURE("Dnn"),
        LENEAR_MODE_INTERPOLATION("G01"), 
        CLOCKWISE_CICULAR_INTERPOLATION("G02"),        
        COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION("G03"),        
        COMMENTS("G04"), 
        REGION_MODE_ON("G36"), 
        REGION_MODE_OFF("G37"), 
        SINGLE_QUADRENT_MODE("G74"), 
        MULTI_QUADRENT_MODE("G75"),
        END_FILE("M02"),
        COORDINAT_FORMAT("FS"),
        SET_UNITS("MO"),
        DEFINE_APERTURE("AD"),
        DEFINE_MACRO("AM"),
        STEP_REPEAT_MODE("SR"),
        LEVEL_POLARITY("LP");
        
        private String code;
        
        Type(String code){
           this.code=code; 
        }
        public String getCode(){
            return code;
        }
    }
    protected final Type type;
    
    public AbstractCommand(Type type) {
        this.type=type;
    }
    
    public AbstractCommand.Type getType(){
        return type;
    }
    
    public String print() {
         return "%"+type.getCode()+"*%";
    }
}
