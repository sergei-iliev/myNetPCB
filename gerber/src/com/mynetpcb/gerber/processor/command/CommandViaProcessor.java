package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;

import java.util.List;

public class CommandViaProcessor  implements Processor{
    private final GraphicsStateContext context;
    
    public CommandViaProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape>  board, int layermask) {
        
        int height=board.getHeight();       
        double lastX=-1,lastY=-1;
         

        List<ViaShape> vias= board.getShapes(ViaShape.class, layermask);     
        for(ViaShape via:vias){
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
            double diameter=via.getOuter().r*2;
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.ViaPad,diameter);
           
            //set aperture if not same        
            context.resetAperture(aperture);
            
            //flash the via!!!
            StringBuffer commandLine=new StringBuffer();  
            if (!Utils.EQ(via.getCenter().x,lastX)){                   
                lastX = via.getCenter().x;
                commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(via.getCenter().x)*100000));
            }
            if (!Utils.EQ(via.getCenter().y,lastY))
              {                   
                lastY = via.getCenter().y;
                commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-via.getCenter().y)*100000));
              }
            commandLine.append("D03*");                               
            context.getOutput().append(commandLine);
            
            
        }
    }
}

