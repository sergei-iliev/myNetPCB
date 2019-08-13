package com.mynetpcb.gerber.command.function;


public class SetApertureCodeCommand extends FunctionCommand {
        
        private int dcode;
        
        public SetApertureCodeCommand(){
            super(Type.SET_CURRENT_APERTURE);
        }
        
        public void setDCode(int dcode){
            this.dcode=dcode;
        }
        
        @Override
        public String print() {
            return "D"+String.valueOf(this.dcode)+"*";
        }
    
}
