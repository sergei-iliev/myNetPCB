package com.mynetpcb.gerber.processor.command;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.pad.shape.Line;

import java.awt.Point;

import java.util.List;

public class CommandLineProcessor implements Processor {
    private final GraphicsStateContext context;

    public CommandLineProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape>  board, int layermask) {
        //process board lines
        List<Line> lines= board.getShapes(Line.class, layermask);              
        for(Line line:lines){
            processLine(line,board.getHeight());
        }
        
        //process board lines
        if(serviceContext.getParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, Boolean.class)){        
         List<FootprintShape> footprints= board.getShapes(FootprintShape.class, layermask);              
         for(FootprintShape footprint:footprints){            
            for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Line.class){
                    processLine((Line)shape,board.getHeight());
                }
            }
         }
        }

    }
    protected void processLine(Line line,int height){
      this.processLine(line,height,null);
    }
    protected void processLine(Line line,int height,AbstractAttribute.Type attributeType){
        
            int size=line.getThickness();
            int lastX=-1,lastY=-1;
            boolean firstPoint=true;

            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
            ApertureDefinition aperture;
            if(attributeType==null){            
               aperture=context.getApertureDictionary().findCircle(size);
            }else{
               aperture=context.getApertureDictionary().findCircle(attributeType,size);  
            }
            //set aperture if not same
            context.resetAperture(aperture);
            
            for(Point point:line.getLinePoints()){
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
