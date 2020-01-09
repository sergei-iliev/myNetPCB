package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Hexagon;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Rectangle;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.Pad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommandPadProcessor implements Processor{
    
    private final GraphicsStateContext context;
    
    public CommandPadProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape>  board, int layermask) {    
        //set dark polarity
        context.resetPolarity(LevelPolarityCommand.Polarity.DARK);
        
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);                     
        for(FootprintShape footprint:footprints){
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){
                if(!pad.isVisibleOnLayers(layermask)){  //a footprint may have pads on different layers
                   continue;
                }
                
                switch(pad.getShapeType()){
                case CIRCULAR:
                    processCircle(pad,board.getHeight());  
                    break;
                case RECTANGULAR:
                    processRectangle(pad,board.getHeight());           
                    break;
                case OVAL:
                    processOval(pad,board.getHeight());
                    break;
                case POLYGON:
                    processPolygon(pad,board.getHeight());  
                    break;
                }
            }
            
        }
    }
    private void processCircle(Pad pad,int height){
        context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
        
        ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.resolvePad(pad.getType()),pad.getWidth());
        //set aperture if not same
        context.resetAperture(aperture);
        
        StringBuffer commandLine=new StringBuffer();
        //flash the pad!!!
        commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(pad.getCenter().x)*100000));
        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-pad.getCenter().y)*100000));
          
        commandLine.append("D03*");                               
        context.getOutput().append(commandLine);   
        
    }
    /*
     * Process rect as region /contour
     */
    private void processPolygon(Pad pad,int height){
        Hexagon hexagon= (Hexagon)pad.getPadDrawing().getGeometricFigure();
        //set region on
        AbstractCommand command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_ON, FunctionCommand.class);
        context.getOutput().append(command.print());
                
        //close rect 
        Collection<Point> points=new ArrayList<>(hexagon.points);
        points.add(hexagon.points.get(0));
        //rect is 4 point line
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
        lineProcessor.processLine(points,Grid.MM_TO_COORD(1), height,null); 
        
        //set region off
        command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_OFF, FunctionCommand.class);
        context.getOutput().append(command.print());  
    }
    /*
     * Process rect as region /contour
     */
    private void processRectangle(Pad pad,int height){
        Rectangle rect= (Rectangle)pad.getPadDrawing().getGeometricFigure();

        //set region on
        AbstractCommand command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_ON, FunctionCommand.class);
        context.getOutput().append(command.print());
                
        //close rect 
        Collection<Point> points=new ArrayList<>(rect.points);
        points.add(rect.points.get(0));
        //rect is 4 point line
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
        lineProcessor.processLine(points,Grid.MM_TO_COORD(1), height,null); 
        
        //set region off
        command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_OFF, FunctionCommand.class);
        context.getOutput().append(command.print());  
    }
    private void processOval(Pad pad,int height){
      
        Obround obround= (Obround)pad.getPadDrawing().getGeometricFigure();
        double diameter=((Obround)pad.getPadDrawing().getGeometricFigure()).getDiameter();
        
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);
        lineProcessor.processLine(Arrays.asList(obround.ps,obround.pe),diameter, height,AbstractAttribute.Type.resolvePad(pad.getType())); 
       
          
        
    }
}
