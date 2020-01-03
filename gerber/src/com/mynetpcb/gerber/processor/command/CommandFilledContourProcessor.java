package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.gerber.ArcGerberableAdaptor;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Circle;
import com.mynetpcb.pad.shape.RoundRect;
import com.mynetpcb.pad.shape.SolidRegion;

import java.util.Arrays;
import java.util.List;

public class CommandFilledContourProcessor implements Processor{
    private final GraphicsStateContext context;

    public CommandFilledContourProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(GerberServiceContext serviceContext, Unit<? extends Shape> board, int layermask) {        
            boolean filledContour=serviceContext.getParameter(GerberServiceContext.FILLED_CONTOUR,Boolean.class);
            if(!filledContour){
                return;
            }
            
            //set dark polarity
            context.resetPolarity(LevelPolarityCommand.Polarity.DARK);
            //set region on
            AbstractCommand command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_ON, FunctionCommand.class);
            context.getOutput().append(command.print());
        
            ApertureDefinition aperture=context.getApertureDictionary().get(10);        
            //set aperture if not same
            context.resetAperture(aperture);
        
            //process arcs
            List<Arc> arcs = board.getShapes(Arc.class, layermask);
            for (Arc arc : arcs) {
                if(arc.getFill()==Shape.Fill.FILLED){                
                   processArc(arc,board.getHeight());
                }
            }
        //process circles
            List<Circle> circles = board.getShapes(Circle.class, layermask);
            for (Circle circle : circles) {
                if(circle.getFill()==Shape.Fill.FILLED){                
                    //process(arc,board.getHeight());
                }
            }            
            //process rect
            List<RoundRect> rects = board.getShapes(RoundRect.class, layermask);
            for (RoundRect rect : rects){
                if(rect.getFill()==Shape.Fill.FILLED){
                   processRect(rect,board.getHeight());
                }
            }
            //process filled areas
            List<SolidRegion> solidRegions = board.getShapes(SolidRegion.class, layermask);
            for (SolidRegion solidRegion : solidRegions) {                
                processSolidRegion(solidRegion,board.getHeight());                                
            }
            
            
            //set region off
            command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_OFF, FunctionCommand.class);
            context.getOutput().append(command.print());  
    }
    
    private void processArc(Arc arc,int height){        
        //draw arc
            CommandArcProcessor commandArcProcessor=new CommandArcProcessor(context);        
            if (arc.isSingleQuadrant()) {
                commandArcProcessor.singleQuadrentMode(arc,height);
            } else {
                commandArcProcessor.multiQuadrantMode(arc,height);
            } 
       //draw close line        
       
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
       
            StringBuffer commandLine=new StringBuffer();      
            commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(arc.getStartPoint().x)*100000));
            commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-arc.getStartPoint().y)*100000));
            commandLine.append("D01*");                 
            context.getOutput().append(commandLine);
        
        
    }
    
    private void processRect(RoundRect rect,int height){
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
        CommandArcProcessor arcProcessor=new CommandArcProcessor(context);           
         
        arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[0]),Grid.MM_TO_COORD(1), height,null); 
        lineTo(rect.getShape().segments[0].pe,height);
        //arcTo(new ArcGerberableAdaptor(rect.getShape().arcs[1]),height);
        
        arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[1]),Grid.MM_TO_COORD(1), height,null);  
        lineTo(rect.getShape().segments[1].pe, height);
        //lineProcessor.processLine(Arrays.asList(rect.getShape().segments[1].ps,rect.getShape().segments[1].pe),rect.getThickness(),height,null);  
        arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[2]),Grid.MM_TO_COORD(1), height,null);   
        lineTo(rect.getShape().segments[2].pe, height);
        
        //lineProcessor.processLine(Arrays.asList(rect.getShape().segments[2].ps,rect.getShape().segments[2].pe),rect.getThickness(),height,null);  
        arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[3]),Grid.MM_TO_COORD(1), height,null);   
        lineTo(rect.getShape().arcs[0].getEnd(), height);
                        
        //lineProcessor.processLine(Arrays.asList(rect.getShape().segments[3].ps,rect.getShape().segments[3].pe),rect.getThickness(),height,null);    
        
        lineProcessor.processLine(Arrays.asList(rect.getShape().segments[0].ps ,rect.getShape().segments[0].pe,
                                                rect.getShape().segments[1].pe,rect.getShape().segments[2].pe,rect.getShape().segments[3].ps),Grid.MM_TO_COORD(1),height,null);  
    }    
    
    private void lineTo(Point pt,int height){
        context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);        
        StringBuffer commandLine=new StringBuffer();      
        commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM( pt.x)*100000));
        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-pt.y)*100000));
        commandLine.append("D01*");                 
        context.getOutput().append(commandLine);        
    }
    
    private void arcTo(ArcGerberable arc,int height){
        //set single quadrant mode if not set
        context.resetCommand(AbstractCommand.Type.SINGLE_QUADRENT_MODE);
        //set start point
        //Point point=arc.getStartPoint();
        //StringBuffer buffer = new StringBuffer();
        
        //buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x) * 100000));
        //buffer.append("Y" +
        //              context.getFormatter().format(Grid.COORD_TO_MM(height - (point.y)) * 100000));
        //buffer.append("D02*");
        //context.getOutput().append(buffer);
        
        if (arc.isClockwise()) {
            //clockwize
            context.resetCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION);
        } else {
            //counterclockwize
            context.resetCommand(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION);
        }
        
        //set end point and radious
        StringBuffer buffer = new StringBuffer();
        Point point = arc.getEndPoint();
        buffer.append("X" + context.getFormatter().format(Grid.COORD_TO_MM(point.x) * 100000));
        buffer.append("Y" +
                      context.getFormatter().format(Grid.COORD_TO_MM(height - ( point.y)) * 100000));
        //radius
        buffer.append("I" + context.getFormatter().format(Grid.COORD_TO_MM(Math.abs(arc.getI())) * 100000));
        buffer.append("J" + context.getFormatter().format(Grid.COORD_TO_MM(Math.abs(arc.getJ())) * 100000));

        buffer.append("D01*");
        context.getOutput().append(buffer);        
    }
    
    private void processSolidRegion(SolidRegion solidRegion,int height){
        double lastX=-1,lastY=-1;
        boolean firstPoint=true;
        
        //set linear mode if not set
        context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
        
        ApertureDefinition aperture=context.getApertureDictionary().get(10);        
        //set aperture if not same
        context.resetAperture(aperture);
        
        for(Point point:solidRegion.getLinePoints()){
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
        
        //close region
        Point point=solidRegion.getLinePoints().get(0);
        StringBuffer commandLine=new StringBuffer();
        if (!Utils.EQ(point.x ,lastX)){                   
            lastX = point.x;
            commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
        }
        if (!Utils.EQ(point.y,lastY))
          {                   
            lastY = point.y;
            commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-point.y)*100000));
          }
        
        commandLine.append("D01*");                 
        context.getOutput().append(commandLine);
                
    }
}
