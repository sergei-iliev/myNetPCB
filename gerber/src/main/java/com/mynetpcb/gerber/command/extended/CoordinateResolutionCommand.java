package com.mynetpcb.gerber.command.extended;


public class CoordinateResolutionCommand extends ExtendedCommand {
    public CoordinateResolutionCommand(String resolution) {
        super(Type.COORDINAT_FORMAT,"FSLAX"+resolution+"Y"+resolution);
    }
}
