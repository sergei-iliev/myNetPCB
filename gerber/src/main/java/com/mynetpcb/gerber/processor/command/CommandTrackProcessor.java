package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.TrackShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;

import java.util.List;

public class CommandTrackProcessor  implements Processor{
    private final GraphicsStateContext context;
    public CommandTrackProcessor(GraphicsStateContext context) {
       this.context=context;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape>  board, int layermask) {
                      
        int height=board.getHeight();
        
        List<TrackShape> tracks= board.getShapes(TrackShape.class, layermask);              
        for(TrackShape track:tracks){
            int thickness=track.getThickness();
            double lastX=-1,lastY=-1;
            boolean firstPoint=true;

            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
                        
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.Conductor,thickness);
            //set aperture if not same
            context.resetAperture(aperture);
            
            for(Point point:track.getLinePoints()){
                StringBuffer commandLine=new StringBuffer();
                if (!Utils.EQ(point.x,lastX)){                   
                    lastX = point.x;                    
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
                }
                if (!Utils.EQ(point.y,lastY))
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
