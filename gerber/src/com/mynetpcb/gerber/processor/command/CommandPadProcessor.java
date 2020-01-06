package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Pad;

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
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);                     
        for(FootprintShape footprint:footprints){
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){
                if(!pad.isVisibleOnLayers(layermask)){  //a footprint may have pads on different layers
                   continue;
                }
                
                switch(pad.getShapeType()){
                case CIRCULAR:
                    break;
                case OVAL:
                    processOval(pad,board.getHeight());
                    break;
                  
                }
            }
            
        }
    }
    
    private void processOval(Pad pad,int height){
      
        Obround obround= (Obround)pad.getPadDrawing().getGeometricFigure();
        double diameter=((Obround)pad.getPadDrawing().getGeometricFigure()).getDiameter();
        
        CommandLineProcessor lineProcessor=new CommandLineProcessor(context);
        lineProcessor.processLine(Arrays.asList(obround.ps,obround.pe),diameter, height,AbstractAttribute.Type.resolvePad(pad.getType())); 
       
          
        
    }
}
