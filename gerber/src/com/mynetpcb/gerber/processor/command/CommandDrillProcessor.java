package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBHole;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.attribute.drill.ComponentDrillAttribute;
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
    public void process(Board board, int layermask) {
        if(layermask==Layer.NPTH_LAYER_DRILL){
           //non plated
           processPads(board,board.getHeight());
           processHoles(board,board.getHeight());
        }else{
            //plated
           processVias(board,board.getHeight()); 
            
        }

    }
    
    private void processPads(Board board,int height){
        int lastX=-1,lastY=-1;

        List<PCBFootprint> footprints= board.getShapes(PCBFootprint.class);              
        for(PCBFootprint footrpint:footprints){
            Collection<Pad> pads=footrpint.getPins();
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
    
    private void processHoles(Board board,int height){
        int lastX=-1,lastY=-1;

        List<PCBHole> holes= board.getShapes(PCBHole.class);              
        for(PCBHole hole:holes){
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
    
    public void processVias(Board board,int height){
        int lastX=-1,lastY=-1;
        
        List<PCBVia> vias= board.getShapes(PCBVia.class);              
        for(PCBVia via:vias){
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
