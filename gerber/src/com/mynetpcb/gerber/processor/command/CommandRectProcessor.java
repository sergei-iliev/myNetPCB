package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.gerber.ArcGerberableAdaptor;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.RoundRect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommandRectProcessor implements Processor {
    private final GraphicsStateContext context;

    public CommandRectProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {                
        //board shapes
        for(RoundRect rect:board.<RoundRect>getShapes(RoundRect.class,layermask)){
               processRect(rect,board.getHeight());                               
        }
        //footprint shapes
        if(serviceContext.getParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN, Boolean.class)){        
         List<FootprintShape> footprints= board.getShapes(FootprintShape.class);                     
         for(FootprintShape footrpint:footprints){
            Collection<? extends Shape> shapes=footrpint.getShapes();
            for(Shape shape:shapes){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()==RoundRect.class){
                    processRect((RoundRect)shape,board.getHeight());   
                }
            }
         }
        }
    }
    
    private void processRect(RoundRect rect,int height){    
        if(rect.getFill()==Shape.Fill.FILLED){
            return;
        }
        if(rect.getRounding()==0){
           //close rect 
           Collection<Point> points=new ArrayList<>(rect.getShape().points);
           points.add(rect.getShape().points.get(0));
           //rect is 4 point line
           CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
           lineProcessor.processLine(points,rect.getThickness(), height,null); 
        }else{
           CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
           CommandArcProcessor arcProcessor=new CommandArcProcessor(context);           
            
           arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[0]),rect.getThickness(), height,null); 
           lineProcessor.processLine(Arrays.asList(rect.getShape().segments[0].ps,rect.getShape().segments[0].pe),rect.getThickness(),height,null); 
           arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[1]),rect.getThickness(), height,null);  
           lineProcessor.processLine(Arrays.asList(rect.getShape().segments[1].ps,rect.getShape().segments[1].pe),rect.getThickness(),height,null);  
           arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[2]),rect.getThickness(), height,null);   
           lineProcessor.processLine(Arrays.asList(rect.getShape().segments[2].ps,rect.getShape().segments[2].pe),rect.getThickness(),height,null);  
           arcProcessor.processArc(new ArcGerberableAdaptor(rect.getShape().arcs[3]),rect.getThickness(), height,null);   
           lineProcessor.processLine(Arrays.asList(rect.getShape().segments[3].ps,rect.getShape().segments[3].pe),rect.getThickness(),height,null);   
            
        }
        
        
        
        
    }
    
    
    
}
