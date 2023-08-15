package com.mynetpcb.gerber.processor.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.d2.shapes.Circle;
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

/*
 * Same as Pad command 
 */
public class CommandSolderMaskProcessor implements Processor{
    
    private final GraphicsStateContext context;
    
    public CommandSolderMaskProcessor(GraphicsStateContext context) {
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
                //a footprint may have pads on different layers                
                if((((pad.getCopper().getLayerMaskID()&Layer.LAYER_FRONT)!=0)&&((layermask&Layer.SOLDERMASK_LAYER_FRONT)!=0))||
                    	(((pad.getCopper().getLayerMaskID()&Layer.LAYER_BACK)!=0)&&((layermask&Layer.SOLDERMASK_LAYER_BACK)!=0))) {                
                
                switch(pad.getShapeType()){
                case CIRCULAR:
                    processCircle((Circle)pad.getPadDrawing().getGeometricFigure(),board.getHeight(),pad.getSolderMaskExpansion());  
                    break;
                case RECTANGULAR:
                	var r=(Rectangle)pad.getPadDrawing().getGeometricFigure().clone();
                	r.grow(pad.getSolderMaskExpansion());
                    processRectangle(r,board.getHeight());           
                    break;
                case OVAL:
                    processOval((Obround)pad.getPadDrawing().getGeometricFigure(),board.getHeight(),pad.getSolderMaskExpansion(),pad.getRotate());
                    break;
                case POLYGON:
                    var h=(Hexagon)pad.getPadDrawing().getGeometricFigure().clone();
                    h.grow(pad.getSolderMaskExpansion());
                	processPolygon( h,board.getHeight());  
                    break;
                }
                }
            }
            
        }
    }
    protected void processCircle(Circle shape,int height,double solderMaskExtension){
        context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
        ApertureDefinition aperture=context.getApertureDictionary().findCircle(shape.r*2+2*solderMaskExtension); 
        
        //set aperture if not same
        context.resetAperture(aperture);
        
        StringBuffer commandLine=new StringBuffer();
        //flash the pad!!!
        commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(shape.getCenter().x)*100000));
        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-shape.getCenter().y)*100000));
          
        commandLine.append("D03*");                               
        context.getOutput().append(commandLine);   
        
    }
    /*
     * Process rect as region /contour
     */
    protected void processPolygon(Hexagon hexagon,int height){
        //set region on
        AbstractCommand command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_ON, FunctionCommand.class);
        context.getOutput().append(command.print());
                
        //close rect 
        Collection<Point> points=new ArrayList<>(hexagon.points);
        points.add(hexagon.points.get(0));
        //rect is 4 point line
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
        lineProcessor.processLine(points,Grid.MM_TO_COORD(1), height,null,true); 
        
        //set region off
        command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_OFF, FunctionCommand.class);
        context.getOutput().append(command.print());  
    }
    /*
     * Process rect as region /contour
     */
    protected void processRectangle(Rectangle rect,int height){
        //set region on
        AbstractCommand command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_ON, FunctionCommand.class);
        context.getOutput().append(command.print());
                
        //close rect 
        Collection<Point> points=new ArrayList<>(rect.points);
        points.add(rect.points.get(0));
        //rect is 4 point line
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);          
        lineProcessor.processLine(points,Grid.MM_TO_COORD(1), height,null,true); 
        
        //set region off
        command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_OFF, FunctionCommand.class);
        context.getOutput().append(command.print());  
    }
    
    protected void processOval(Obround obround,int height,double solderMaskExtension,double rotation){        
        var o=obround.clone();
        o.grow(solderMaskExtension,rotation);
        
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);
        lineProcessor.processLine(Arrays.asList(obround.ps,obround.pe),o.getDiameter(), height,null,false); 
    }
}
