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
import com.mynetpcb.pad.shape.Arc;

import java.util.List;

public class CommandArcProcessor implements Processor {
    private final GraphicsStateContext context;

    public CommandArcProcessor(GraphicsStateContext context) {
        this.context = context;
    }


    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {

        List<Arc> arcs = board.getShapes(Arc.class, layermask);
        for (Arc arc : arcs) {
            if(arc.getFill()==Shape.Fill.EMPTY){ 
              processArc(arc,board.getHeight());
            }
        }
        
        //do arcs in footprints
        if(serviceContext.getParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, Boolean.class)){
         List<FootprintShape> footprints = board.getShapes(FootprintShape.class, layermask);
         for (FootprintShape footprint : footprints) {

            for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Arc.class){
                    if(shape.getFill()==Shape.Fill.EMPTY){
                        processArc((Arc)shape,board.getHeight());                        
                    }
                    
                }
            }
         }
        }
    }

    protected void processArc(Arc arc,int height){
        processArc(arc,arc.getThickness(),height,null);
    }
    
    protected void processArc(ArcGerberable arc,double thickness,int height,AbstractAttribute.Type attributeType){
              
        ApertureDefinition aperture;
        if(attributeType==null){
          aperture = context.getApertureDictionary().findCircle(thickness);
        }else{
          aperture = context.getApertureDictionary().findCircle(attributeType,thickness);  
        }
        //set aperture if not same
        context.resetAperture(aperture);
        
        if (arc.isSingleQuadrant()) {
            singleQuadrentMode(arc,height);
        } else {
            multiQuadrantMode(arc,height);
        }        
    }
    
    protected void singleQuadrentMode(ArcGerberable arc,int height) {
        //set single quadrant mode if not set
        context.resetCommand(AbstractCommand.Type.SINGLE_QUADRENT_MODE);
        //set start point
        Point point=arc.getStartPoint();
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x) * 100000));
        buffer.append("Y" +
                      context.getFormatter().format(Grid.COORD_TO_MM(height - (point.y)) * 100000));
        buffer.append("D02*");
        context.getOutput().append(buffer);
        
        if (arc.isClockwise()) {
            //clockwize
            context.resetCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION);
        } else {
            //counterclockwize
            context.resetCommand(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION);
        }
        
        //set end point and radious
        buffer = new StringBuffer();
        point = arc.getEndPoint();
        buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x) * 100000));
        buffer.append("Y" +
                      context.getFormatter().format(Grid.COORD_TO_MM(height - ( point.y)) * 100000));
        //radius
        buffer.append("I" + context.getFormatter().format(Grid.COORD_TO_MM(Math.abs(arc.getI())) * 100000));
        buffer.append("J" + context.getFormatter().format(Grid.COORD_TO_MM(Math.abs(arc.getJ())) * 100000));

        buffer.append("D01*");
        context.getOutput().append(buffer);

        

    }

    protected void multiQuadrantMode(ArcGerberable arc,int height) {
        //set multi quadrant mode if not set
        context.resetCommand(AbstractCommand.Type.MULTI_QUADRENT_MODE);
        //set start point
        Point point=arc.getStartPoint();
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x) * 100000));
        buffer.append("Y" +
                      context.getFormatter().format(Grid.COORD_TO_MM(height - (point.y)) * 100000));
        buffer.append("D02*");
        context.getOutput().append(buffer);
        
        if (arc.isClockwise()) {
            //clockwize
            context.resetCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION);
        } else {
            //counterclockwize
            context.resetCommand(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION);
        }
        
        //set end point and radious
        buffer = new StringBuffer();
        point = arc.getEndPoint();
        buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x ) * 100000));
        buffer.append("Y" +
                      context.getFormatter().format(Grid.COORD_TO_MM(height - (point.y)) * 100000));
        //radius
        buffer.append("I" + context.getFormatter().format(Grid.COORD_TO_MM((arc.getI())) * 100000));
        //due to the fact that y is inverted, j needs to be inverted by sign too
        buffer.append("J" + context.getFormatter().format(Grid.COORD_TO_MM((-1*(arc.getJ()))) * 100000));

        buffer.append("D01*");
        context.getOutput().append(buffer);
    }
}

