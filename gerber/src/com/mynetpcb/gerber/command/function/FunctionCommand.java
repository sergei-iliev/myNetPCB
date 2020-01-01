package com.mynetpcb.gerber.command.function;

import com.mynetpcb.gerber.command.AbstractCommand;

public class FunctionCommand extends AbstractCommand {
    
    public FunctionCommand(Type type) {
        super(type);
    }
 
    public String print() {
         return type.getCode()+"*";
    }   
}
