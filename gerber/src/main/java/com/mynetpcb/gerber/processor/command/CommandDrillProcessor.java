package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.board.shape.HoleShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
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
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
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
        double lastX=-1,lastY=-1;

        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        for(FootprintShape footprint:footprints){
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){
                if(pad.getType()==Pad.Type.THROUGH_HOLE || pad.getType()==Pad.Type.CONNECTOR){
                    ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.ComponentDrill,pad.getDrill().getWidth());
                    //set aperture if not same
                    context.resetAperture(aperture);
                    
                    //flash the drill hole!!!
                    StringBuffer commandLine=new StringBuffer();
                    if (!Utils.EQ(pad.getCenter().x ,lastX)){                   
                        lastX = pad.getCenter().x;
                        commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(pad.getCenter().x)*100000));
                    }
                    if (!Utils.EQ(pad.getCenter().y ,lastY))
                      {                   
                        lastY = pad.getCenter().y;
                        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-pad.getCenter().y)*100000));
                      }
                    commandLine.append("D03*");                               
                    context.getOutput().append(commandLine);
                }
            }
        }
    }    
    
    private void processHoles(Unit<? extends Shape>  board,int height){
        double lastX=-1,lastY=-1;

        List<HoleShape> holes= board.getShapes(HoleShape.class);              
        for(HoleShape hole:holes){
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.MechanicalDrill,hole.getInner().r*2);
                    //set aperture if not same
            context.resetAperture(aperture);
                    
                    //flash the drill hole!!!
            StringBuffer commandLine=new StringBuffer();
            if (!Utils.EQ(hole.getCenter().x,lastX)){                   
                   lastX = hole.getCenter().x;
                   commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(hole.getCenter().x)*100000));
            }
            if (!Utils.EQ(hole.getCenter().y ,lastY))
            {                   
                        lastY = hole.getCenter().y;
                        commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-hole.getCenter().y)*100000));
            }
            commandLine.append("D03*");                               
            context.getOutput().append(commandLine);
        }
            
        
    }  
    
    public void processVias(Unit<? extends Shape>  board,int height){
        double lastX=-1,lastY=-1;
        
        List<ViaShape> vias= board.getShapes(ViaShape.class);              
        for(ViaShape via:vias){
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.ViaDrill,via.getInner().r*2);
            //set aperture if not same
            context.resetAperture(aperture);
            //flash the via hole!!!
            StringBuffer commandLine=new StringBuffer();
            if (!Utils.EQ(via.getCenter().x ,lastX)){
            lastX = via.getCenter().x;
            commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(via.getCenter().x)*100000));
            }
            if (!Utils.EQ(via.getCenter().y ,lastY))
            {
                lastY = via.getCenter().y;
                commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-via.getCenter().y)*100000));
            }
            commandLine.append("D03*");
            context.getOutput().append(commandLine);
            
        }
    }
}

