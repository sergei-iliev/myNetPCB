package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.Circle;

import java.util.List;

public class CommandCircleProcessor implements Processor {

    private final GraphicsStateContext context;

    public CommandCircleProcessor(GraphicsStateContext context) {
        this.context = context;
    }


    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
   


        List<Circle> circles = board.getShapes(Circle.class, layermask);
        for (Circle circle : circles) {
            if(circle.getFill()==Shape.Fill.EMPTY){  
               processCircle(circle,board.getHeight());
            }
        }
        //do circles in footprints
       if(serviceContext.getParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, Boolean.class)){              
          for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Circle.class){
                    if(shape.getFill()==Shape.Fill.EMPTY){ 
                       processCircle((Circle)shape,board.getHeight());  
                    }
                }
            }
          }
       }
    }
    protected void processCircle(Circle circle,int height){
        processCircle(circle,circle.getThickness(), height,null);
    }
    
    protected void processCircle(ArcGerberable circle,double thickness,int height,AbstractAttribute.Type type){        
        ApertureDefinition aperture;
        if(type==null){ 
           aperture = context.getApertureDictionary().findCircle(thickness);
        }else{
           aperture = context.getApertureDictionary().findCircle(type,thickness);  
        }
        //set aperture if not same
        context.resetAperture(aperture);
        //set multi quadrant mode if not set
        context.resetCommand(AbstractCommand.Type.MULTI_QUADRENT_MODE);
                
        //set start point
        StringBuffer buffer = new StringBuffer();
        Point point = circle.getStartPoint();
        buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x) * 100000));
        buffer.append("Y" + context.getFormatter().format(Grid.COORD_TO_MM(height - ( point.y)) * 100000));
        buffer.append("D02*");
        context.getOutput().append(buffer);

        //set clock wise interpolation
        context.getOutput().append(context.getCommandDictionary()
                                                  .get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)
                                                  .print());

        //set end point and radious
        buffer = new StringBuffer();
        point = circle.getStartPoint();
        buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x ) * 100000));
        buffer.append("Y" +context.getFormatter().format(Grid.COORD_TO_MM(height - (point.y)) * 100000));
                //radius
        buffer.append("I" + context.getFormatter().format(Grid.COORD_TO_MM(circle.getI()) * 100000));
        buffer.append("J" + context.getFormatter().format(Grid.COORD_TO_MM(circle.getJ()) * 100000));

        buffer.append("D01*");
        context.getOutput().append(buffer);
    }
}
