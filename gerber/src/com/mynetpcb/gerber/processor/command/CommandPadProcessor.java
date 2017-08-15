package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.pad.shape.Pad;

import java.util.Collection;
import java.util.List;

public class CommandPadProcessor  implements Processor{
    
    private final GraphicsStateContext context;
    
    public CommandPadProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(Unit<? extends Shape>  board, int layermask) {                                                                       
                      
            int height=board.getHeight();       
            int lastX=-1,lastY=-1;
            StringBuffer commandLine=new StringBuffer();
            
            List<FootprintShape> footprints= board.getShapes(FootprintShape.class);                     
            for(FootprintShape footrpint:footprints){
                //set linear mode if not set
                context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);

                Collection<Pad> pads=((Pinaware)footrpint).getPins();
                for(Pad pad:pads){
                    if(!pad.isVisibleOnLayers(layermask)){  //a footprint may have pads on different layers
                       continue;
                    }
                    ApertureDefinition aperture=null;
                    switch(pad.getShape()){
                        case CIRCULAR:
                         aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.resolvePad(pad.getType()),pad.getWidth());
                            break;
                        case OVAL:
                         aperture=context.getApertureDictionary().findObround(AbstractAttribute.Type.resolvePad(pad.getType()),pad.getWidth(),pad.getHeight());
                            break;
                        case RECTANGULAR:
                         aperture=context.getApertureDictionary().findRectangle(AbstractAttribute.Type.resolvePad(pad.getType()),pad.getWidth(),pad.getHeight());
                         break;
                        case POLYGON:
                        aperture=context.getApertureDictionary().findPolygon(AbstractAttribute.Type.resolvePad(pad.getType()),pad.getWidth(),6);
                        break;                         
                    default:
                        throw new IllegalStateException("Unknown shape - "+pad.getShape());
                    }
                    //set aperture if not same
                    context.resetAperture(aperture);

                    //flash the pad!!!
                    commandLine.setLength(0); 
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
//            if(footprints.size()>0){
//             processHoles(board, layermask);
//            }
    }
    
    /**
     * Process dark polarity drill holes
     */
    /*
    private void processHoles(Board board, int layermask){
        LevelPolarityCommand polarity= context.getLevelPolarityCommand();
        if(polarity.isDark()){
          polarity.setClear();
          context.getOutput().append(polarity.print());
        }
                  
        int height=board.getHeight();       
        int lastX=-1,lastY=-1;
        

        //set linear mode if not set
        context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);

        List<PCBFootprint> footprints= board.getShapes(PCBFootprint.class, layermask);                     
        for(PCBFootprint footrpint:footprints){
            Collection<Pad> pads=footrpint.getPins();
            for(Pad pad:pads){
                if(!pad.isVisibleOnLayers(layermask)){  //a footprint may have pads on different layers
                   continue;
                }                
                //inspect drill holes
                if(pad.getDrill()==null){
                    continue;
                }                
                ApertureDefinition aperture=context.getApertureDictionary().findCircle(AbstractAttribute.Type.ComponentDrill,pad.getDrill().getWidth());
                
                //set aperture if not same
                context.resetAperture(aperture);
                
                //flash the pad!!!
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

        polarity.setDark();
        context.getOutput().append(polarity.print());
        
    }
    */
}
