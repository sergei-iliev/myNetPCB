package com.mynetpcb.gerber.processor.command;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.board.shape.HoleShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Pad;

import java.util.Collection;
import java.util.List;

public class CommandDrillProcessor implements Processor {

    private final GraphicsStateContext context;

    public CommandDrillProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(Unit<? extends Shape> board, int layermask) {
        if(layermask==Layer.NPTH_LAYER_DRILL){
           //non plated
           processPads(board,board.getHeight());
           processHoles(board,board.getHeight());
        }else{
            //plated
           processVias(board,board.getHeight()); 
            
        }

    }
    
    private void processPads(Unit<? extends Shape> board,int height){
        int lastX=-1,lastY=-1;

        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        for(FootprintShape footrpint:footprints){
            Collection<Pad> pads=((Pinaware)footrpint).getPins();
            for(Pad pad:pads){
                if(pad.getType()==Pad.Type.THROUGH_HOLE){
                    ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.ComponentDrill,pad.getDrill().getWidth());
                    //set aperture if not same
                    context.resetAperture(aperture);
                    
                    //flash the drill hole!!!
                    StringBuffer commandLine=new StringBuffer();
                    if (pad.getX() != lastX){                   
                        lastX = pad.getX();
                        commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(pad.getX())*100000));
                    }
                    if (pad.getY() != lastY)
                      {                   
                        lastY = pad.getY();
                        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-pad.getY())*100000));
                      }
                    commandLine.append("D03*");                               
                    context.getOutput().append(commandLine);
                }
            }
        }
    }    
    
    private void processHoles(Unit<? extends Shape>  board,int height){
        int lastX=-1,lastY=-1;

        List<HoleShape> holes= board.getShapes(HoleShape.class);              
        for(HoleShape hole:holes){
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.MechanicalDrill,hole.getWidth());
                    //set aperture if not same
            context.resetAperture(aperture);
                    
                    //flash the drill hole!!!
            StringBuffer commandLine=new StringBuffer();
            if (hole.getX() != lastX){                   
                   lastX = hole.getX();
                   commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(hole.getX())*100000));
            }
            if (hole.getY() != lastY)
            {                   
                        lastY = hole.getY();
                        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-hole.getY())*100000));
            }
            commandLine.append("D03*");                               
            context.getOutput().append(commandLine);
        }
            
        
    }  
    
    public void processVias(Unit<? extends Shape>  board,int height){
        int lastX=-1,lastY=-1;
        
        List<ViaShape> vias= board.getShapes(ViaShape.class);              
        for(ViaShape via:vias){
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.ViaDrill,via.getThickness());
            //set aperture if not same
            context.resetAperture(aperture);
            //flash the via hole!!!
            StringBuffer commandLine=new StringBuffer();
            if (via.getX() != lastX){
            lastX = via.getX();
            commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(via.getX())*100000));
            }
            if (via.getY() != lastY)
            {
                lastY = via.getY();
                commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-via.getY())*100000));
            }
            commandLine.append("D03*");
            context.getOutput().append(commandLine);
            
        }
    }
}
