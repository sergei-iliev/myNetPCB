package com.mynetpcb.gerber.processor.command;


import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.board.shape.CopperAreaShape;
import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.board.shape.TrackShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.GlyphLabel;

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
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
        Collection<CopperAreaShape> regions=board.getShapes(CopperAreaShape.class,layermask);  
        
        for(CopperAreaShape region:regions){
          //draw in dark polarity region
          processRegion(((Trackable<LinePoint>)region).getLinePoints(),board.getHeight());
        
         //draw in clean polarity stuff in it
          context.resetPolarity(LevelPolarityCommand.Polarity.CLEAR);  
          
          processVias(board,region);    
          processTracks(board, region);
          processPads(board, region); 
          processText(board,region); 
            
          context.resetPolarity(LevelPolarityCommand.Polarity.DARK);    
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
    
        private void processText(Unit<? extends Shape> board,CopperAreaShape source){
            List<Point> region=new ArrayList<>(4);
            for(GlyphLabel label:board.<GlyphLabel>getShapes(GlyphLabel.class,source.getCopper().getLayerMaskID())){      
               Rectangle rect=label.getTexture().getBoundingShape();
               rect.grow(( ((ClearanceTarget)label).getClearance()!=0?((ClearanceTarget)label).getClearance():source.getClearance()), ((ClearanceTarget)label).getClearance()!=0?((ClearanceTarget)label).getClearance():source.getClearance());  
               region.clear();
               region.add(new Point(rect.x,rect.y));
               region.add(new Point(rect.x+rect.width,rect.y));
               region.add(new Point(rect.x+rect.width,rect.y+rect.height));
               region.add(new Point(rect.x,rect.y+rect.height));
                
               processRegion(region, board.getHeight());
            }                                      
        }
        
    
    private void processPads(Unit<? extends Shape> board,CopperAreaShape source){
        int height=board.getHeight();       
        int lastX=-1,lastY=-1;
        StringBuffer commandLine=new StringBuffer();
        
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);                     
        for(FootprintShape footrpint:footprints){
            
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);

            Collection<PadShape> pads=footrpint.getPins();
            for(PadShape pad:pads){
                if(Utilities.isSameNet(source,pad)){
                    continue;
                }
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
    
    private void processTracks(Unit<? extends Shape> board,CopperAreaShape source){
        int height=board.getHeight();
        
        List<TrackShape> tracks= board.getShapes(TrackShape.class, source.getCopper().getLayerMaskID());              
        for(TrackShape track:tracks){
            if(Utilities.isSameNet(source,track)){
                continue;
            }
            
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
    private void processVias(Unit<? extends Shape> board,CopperAreaShape source){
        int lastX=-1,lastY=-1;
        int height=board.getHeight(); 
        //select vias of the region layer
        for(ViaShape via:board.<ViaShape>getShapes(ViaShape.class,source.getCopper().getLayerMaskID())){
            if(Utilities.isSameNet(source,via)){
                continue;
            }

            Rectangle inner=via.getBoundingShape().getBounds();             
            inner.grow(via.getClearance()!=0?via.getClearance():source.getClearance(),via.getClearance()!=0?via.getClearance():source.getClearance());
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
