package com.mynetpcb.gerber.command.extended;


public class LevelPolarityCommand extends ExtendedCommand{
    public enum Polarity{
        DARK("D"),CLEAR("C");
        String code;
        Polarity(String code){
         this.code=code;   
        }
        
        
        @Override
        public String toString() {        
            return code;
        }
    }
    private Polarity polarity;
    
    public LevelPolarityCommand(){
        super(Type.LEVEL_POLARITY,null);
        this.polarity=Polarity.DARK;
    }
    public void setPolarity(Polarity polarity){
        this.polarity=polarity;
    }
    
    public Polarity getPolarity(){
        return polarity;
    }
    
    
    @Override
    public String print() {            
        return "%LP"+this.polarity+"*%";
    }
}
