package com.mynetpcb.gerber.command.extended;


public class UnitCommand extends ExtendedCommand {
    public UnitCommand(String unit) {
        super(Type.SET_UNITS,"MO"+unit); 
    }
}
