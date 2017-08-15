package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.Circle;

import java.awt.geom.Point2D;

import java.util.Collection;
import java.util.List;

public class CommandCircleProcessor implements Processor {

    private final GraphicsStateContext context;

    public CommandCircleProcessor(GraphicsStateContext context) {
        this.context = context;
    }


    @Override
    public void process(Unit<? extends Shape> board, int layermask) {
   


        List<Circle> circles = board.getShapes(Circle.class, layermask);
        for (Circle circle : circles) {
           processCircle(circle,board.getHeight());
        }
        //do circles in footprints
        List<FootprintShape> footprints = board.getShapes(FootprintShape.class, layermask);
        for (FootprintShape footprint : footprints) {
            Collection<Shape> shapes=footprint.<Shape>getShapes();
            for(Shape shape:shapes){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Circle.class){
                    processCircle((Circle)shape,board.getHeight());  
                }
            }
        }
    }
    private void processCircle(Circle circle,int height){
        processCircle(circle,height,null);
    }
    protected void processCircle(Circle circle,int height,AbstractAttribute.Type type){
        
        int lastX = -1, lastY = -1;
        
            if (circle.getFill() == Shape.Fill.EMPTY) {
                ApertureDefinition aperture;
                if(type==null){ 
                    aperture = context.getApertureDictionary().findCircle(circle.getThickness());
                }else{
                    aperture = context.getApertureDictionary().findCircle(type,circle.getThickness());  
                }
                //set aperture if not same
                context.resetAperture(aperture);
                //set multi quadrant mode if not set
                context.resetCommand(AbstractCommand.Type.MULTI_QUADRENT_MODE);
                
                //set start point
                StringBuffer buffer = new StringBuffer();
                Point2D point = circle.getStartPoint();
                buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM((int) point.getX()) * 100000));
                buffer.append("Y" +
                              context.getFormatter().format(Grid.COORD_TO_MM(height - ((int) point.getY())) * 100000));
                buffer.append("D02*");
                context.getOutput().append(buffer);

                //set clock wise interpolation
                context.getOutput().append(context.getCommandDictionary()
                                                  .get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)
                                                  .print());

                //set end point and radious
                buffer = new StringBuffer();
                point = circle.getStartPoint();
                buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM((int) point.getX()) * 100000));
                buffer.append("Y" +
                              context.getFormatter().format(Grid.COORD_TO_MM(height - ((int) point.getY())) * 100000));
                //radius
                buffer.append("I" + context.getFormatter().format(Grid.COORD_TO_MM(circle.getI()) * 100000));
                buffer.append("J" + context.getFormatter().format(Grid.COORD_TO_MM(circle.getJ()) * 100000));

                buffer.append("D01*");
                context.getOutput().append(buffer);
            } else {
                //flash it
                ApertureDefinition aperture=context.getApertureDictionary().findCircle(2*circle.getWidth());                                
                //set aperture if not same
                context.resetAperture(aperture);
                
                //flash the filled circle!!!
                StringBuffer commandLine=new StringBuffer();
                if (circle.getX() != lastX){                   
                    lastX = circle.getX();
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(circle.getX())*100000));
                }
                if (circle.getY() != lastY)
                  {                   
                    lastY = circle.getY();
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-circle.getY())*100000));
                  }
                commandLine.append("D03*");                               
                context.getOutput().append(commandLine);

            }
        
        
    }
}
