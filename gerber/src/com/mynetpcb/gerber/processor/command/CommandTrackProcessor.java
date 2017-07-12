package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.capi.StringBufferEx;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.gerber.command.function.SetApertureCodeCommand;

import java.awt.Point;

import java.util.List;

public class CommandTrackProcessor  implements Processor{
    private final GraphicsStateContext context;
    public CommandTrackProcessor(GraphicsStateContext context) {
       this.context=context;
    }

    @Override
    public void process(Board board, int layermask) {
                      
        int height=board.getHeight();
        
        List<PCBTrack> tracks= board.getShapes(PCBTrack.class, layermask);              
        for(PCBTrack track:tracks){
            int size=track.getThickness();
            int lastX=-1,lastY=-1;
            boolean firstPoint=true;

            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
                        
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.Conductor,size);
            //set aperture if not same
            context.resetAperture(aperture);
            
            for(Point point:track.getLinePoints()){
                StringBuffer commandLine=new StringBuffer();
                if (point.x != lastX){                   
                    lastX = point.x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
                }
                if (point.y != lastY)
                  {                   
                    lastY = point.y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-point.y)*100000));
                  }
                
                if (firstPoint){
                   commandLine.append("D02*");
                }else{
                   commandLine.append("D01*"); 
                }
                
                context.getOutput().append(commandLine);
                                
                firstPoint = false;
            }
            
        }
    }
}
