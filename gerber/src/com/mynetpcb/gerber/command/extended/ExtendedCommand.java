package com.mynetpcb.gerber.command.extended;

import com.mynetpcb.gerber.command.AbstractCommand;


public abstract class ExtendedCommand extends AbstractCommand {
    private final String command;
    
    public ExtendedCommand(Type type,String command) {
        super(type);
        this.command = command;
    }
    
    public String print() {
         return "%"+command+"*%";
    }


}


