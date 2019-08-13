package com.mynetpcb.gerber.processor.command;


import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
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
        int lastX=-1,lastY=-1;
         

        List<ViaShape> vias= board.getShapes(ViaShape.class, layermask);     
        for(ViaShape via:vias){
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
            
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.ViaPad,via.getWidth());
           
            //set aperture if not same        
            context.resetAperture(aperture);
            
            //flash the via!!!
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
