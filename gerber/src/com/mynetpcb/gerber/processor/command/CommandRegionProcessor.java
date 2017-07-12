package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBLabel;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.Pad;

import java.awt.Point;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandRegionProcessor implements Processor {
    private final GraphicsStateContext context;

    public CommandRegionProcessor(GraphicsStateContext context) {
        this.context = context;
    }

    @Override
    public void process(Board board, int layermask) {
        Collection<PCBCopperArea> regions=board.getShapes(PCBCopperArea.class,layermask);  
        
        for(PCBCopperArea region:regions){
          //draw in dark polarity region
          processRegion(region.getLinePoints(),board.getHeight());
        
         //draw in clean polarity stuff in it
          context.resetPolarity(LevelPolarityCommand.Polarity.CLEAR);  
          
          processVias(board,region);    
          processTracks(board, region);
          processPads(board, region); 
          processText(board,region); 
            
          context.resetPolarity(LevelPolarityCommand.Polarity.DARK);    
        }

    }
    private void processText(Board board,PCBCopperArea source){
        List<PCBLabel> labels= board.getShapes(PCBLabel.class,source.getCopper().getLayerMaskID());
        List<Point> region=new ArrayList<>(4);
        for(PCBLabel label:labels){
            Rectangle rect=label.getTexture().getBoundingShape();        
            rect.grow(label.getClearance()!=0?label.getClearance():source.getClearance(), label.getClearance()!=0?label.getClearance():source.getClearance());
            region.clear();
            region.add(new Point(rect.x,rect.y));
            region.add(new Point(rect.x+rect.width,rect.y));
            region.add(new Point(rect.x+rect.width,rect.y+rect.height));
            region.add(new Point(rect.x,rect.y+rect.height));
            
            processRegion(region, board.getHeight());
        }
    }

    private void processRegion(List<? extends Point> region,int height){
        
        int lastX=-1,lastY=-1;
        boolean firstPoint=true;
        //set region on
        AbstractCommand command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_ON, FunctionCommand.class);
        context.getOutput().append(command.print());
        //set linear mode if not set
        context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
        
        ApertureDefinition aperture=context.getApertureDictionary().get(10);        
        //set aperture if not same
        context.resetAperture(aperture);
        
        for(Point point:region){
            StringBuffer commandLine=new StringBuffer();
            if (point.x != lastX){                   
                lastX = point.x;
                commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
            }
            if (point.y != lastY)
              {                   
                lastY = point.y;
                commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-point.y)*100000));
              }
            
            if (firstPoint){
               commandLine.append("D02*");
            }else{
               commandLine.append("D01*"); 
            }
            
            context.getOutput().append(commandLine);
                            
            firstPoint = false;            
        }
        
        //close region
        Point point=region.get(0);
        StringBuffer commandLine=new StringBuffer();
        if (point.x != lastX){                   
            lastX = point.x;
            commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
        }
        if (point.y != lastY)
          {                   
            lastY = point.y;
            commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-point.y)*100000));
          }
        
        commandLine.append("D01*");                 
        context.getOutput().append(commandLine);
        
        //set region off
        command=context.getCommandDictionary().get(AbstractCommand.Type.REGION_MODE_OFF, FunctionCommand.class);
        context.getOutput().append(command.print());        
    }
    
    private void processPads(Board board,PCBCopperArea source){
        int height=board.getHeight();       
        int lastX=-1,lastY=-1;
        StringBuffer commandLine=new StringBuffer();
        
        List<PCBFootprint> footprints= board.getShapes(PCBFootprint.class);                     
        for(PCBFootprint footrpint:footprints){
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);

            Collection<Pad> pads=footrpint.getPins();
            for(Pad pad:pads){
                if(!pad.isVisibleOnLayers(source.getCopper().getLayerMaskID())){  //a footprint may have pads on different layers
                   continue;
                }
                ApertureDefinition aperture=null;
                switch(pad.getShape()){
                    case CIRCULAR:
                     aperture=context.getApertureDictionary().findCircle(pad.getWidth()+(2*source.getClearance()));
                        break;
                    case OVAL:
                     aperture=context.getApertureDictionary().findObround(pad.getWidth()+(2*source.getClearance()),pad.getHeight()+(2*source.getClearance()));
                        break;
                    case RECTANGULAR:
                     aperture=context.getApertureDictionary().findRectangle(pad.getWidth()+(2*source.getClearance()),pad.getHeight()+(2*source.getClearance()));
                     break;
                    case POLYGON:
                    aperture=context.getApertureDictionary().findPolygon(pad.getWidth()+(2*source.getClearance()),6);
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
    }
    
    private void processTracks(Board board,PCBCopperArea source){
        int height=board.getHeight();
        
        List<PCBTrack> tracks= board.getShapes(PCBTrack.class, source.getCopper().getLayerMaskID());              
        for(PCBTrack track:tracks){
            int size=track.getThickness();
            int lastX=-1,lastY=-1;
            boolean firstPoint=true;

            int lineThickness;
            if(track.getClearance()!=0){
              lineThickness=(track.getThickness()+2*track.getClearance());
            }else{
              lineThickness=(track.getThickness()+2*source.getClearance());
            }
            
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
                        
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(lineThickness);
            //set aperture if not same
            context.resetAperture(aperture);
            
            for(Point point:track.getLinePoints()){
                StringBuffer commandLine=new StringBuffer();
                if (point.x != lastX){                   
                    lastX = point.x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
                }
                if (point.y != lastY)
                  {                   
                    lastY = point.y;
                    commandLine.append("Y"+context.getFormatter().format(Grid.COORD_TO_MM(height-point.y)*100000));
                  }
                
                if (firstPoint){
                   commandLine.append("D02*");
                }else{
                   commandLine.append("D01*"); 
                }
                
                context.getOutput().append(commandLine);
                                
                firstPoint = false;
            }
            
        }          
    }
    private void processVias(Board board,PCBCopperArea source){
        int lastX=-1,lastY=-1;
        int height=board.getHeight(); 
        //select vias of the region layer
        for(PCBVia via:board.<PCBVia>getShapes(PCBVia.class,source.getCopper().getLayerMaskID())){
            Rectangle inner=via.getBoundingShape().getBounds();             
            inner.grow(source.getClearance(), source.getClearance());
            if(!source.getBoundingShape().intersects(inner)){
               continue; 
            }
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
            
            ApertureDefinition aperture=context.getApertureDictionary().findCircle((int)inner.getWidth());
            
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
