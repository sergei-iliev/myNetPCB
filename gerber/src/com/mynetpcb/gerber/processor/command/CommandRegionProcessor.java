package com.mynetpcb.gerber.processor.command;

import com.mynetpcb.core.board.shape.CopperAreaShape;
import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.board.shape.TrackShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Hexagon;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Rectangle;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.Pad;

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
          processRegion(region.getLinePoints(),board.getHeight());
        
         //draw in clean polarity stuff in it
          context.resetPolarity(LevelPolarityCommand.Polarity.CLEAR);  
          
          processVias(board,region);    
          processTracks(board, region);
          processPads(board, region); 
          //processText(board,region); 
            
          context.resetPolarity(LevelPolarityCommand.Polarity.DARK);
          
          //process THERMAL pads   
          //processThermalPads(board,region);   
        }

    }
    
    private void processPads(Unit<? extends Shape> board,CopperAreaShape source){
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        CommandPadProcessor commandPadProcessor=new CommandPadProcessor(context);
        
        for(FootprintShape footprint:footprints){
            //check if footprint in copper area
            if(!source.getBoundingShape().intersects(footprint.getBoundingShape())){
               continue; 
            }
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){ 
                if(pad.isSameNet(source)&&source.getPadConnection()==PadShape.PadConnection.DIRECT){
                    continue;
                }
                // is pad  within copper area
                Box rect = pad.getBoundingShape();
                rect.grow(source.getClearance());
                
                if(!(source).getBoundingShape().intersects(rect)){
                   continue; 
                }
            
                switch(pad.getShapeType()){
                case CIRCULAR:
                    Circle circle=(Circle)pad.getPadDrawing().getGeometricFigure();
                    circle=circle.clone();
                    circle.grow(source.getClearance());
                    commandPadProcessor.processCircle(circle,null,board.getHeight());  
                    break;
                case RECTANGULAR:
                    Rectangle rectangle=(Rectangle)pad.getPadDrawing().getGeometricFigure();
                    rectangle=rectangle.clone();
                    rectangle.grow(source.getClearance());
                    commandPadProcessor.processRectangle(rectangle,board.getHeight());           
                    break;
                case OVAL:
                    Obround obround=(Obround)pad.getPadDrawing().getGeometricFigure();
                    obround=obround.clone();
                    obround.grow(source.getClearance());
                    commandPadProcessor.processOval(obround,null,board.getHeight());
                    break;
                case POLYGON:
                    Hexagon hexagon= (Hexagon)pad.getPadDrawing().getGeometricFigure();
                    hexagon=hexagon.clone();
                    hexagon.grow(source.getClearance());
                    commandPadProcessor.processPolygon(hexagon,board.getHeight());  
                    break;
                }                
                        
            }
        }
    }    

    /*
     * draw cross hair to represent THERMAL pads connections
     */
    
//    private void processThermalPads(Unit<? extends Shape> board,CopperAreaShape source){
//        int height=board.getHeight();       
//        CommandLineProcessor lineProcessor=new CommandLineProcessor(context); 
//        
//        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);                     
//        for(FootprintShape footprint:footprints){  
//            //check if footprint in copper area
//            if(!source.getBoundingShape().intersects(footprint.getBoundingShape().getBounds())){
//               continue; 
//            }
//            Collection<PadShape> pads=footprint.getPins();
//            for(PadShape pad:pads){ 
//                // is pad  within copper area
//                Rectangle rect = pad.getBoundingShape().getBounds();
//                rect.grow(source.getClearance(), source.getClearance());
//                
//                if(!(source).getBoundingShape().intersects(rect)){
//                   continue; 
//                }
//                
//                /*
//                 * Both pad and copper must be on the same layer and copper pad connection
//                 * registered as THERMAL                 
//                 */
//                if(Utilities.isSameNet(source,pad)&&source.getPadConnection()==PadShape.PadConnection.THERMAL){
//                    //THERMAL pad -> draw crossing                     
//                    switch(pad.getShape()){
//                        case CIRCULAR:                            
//                              
//                            Line line =new Line(pad.getWidth()/2,0);
//                            
//                            line.add((int)(rect.getMinX()+(rect.getHeight()/2)), (int)rect.getMinY());
//                            line.add((int)(rect.getMinX()+(rect.getHeight()/2)), (int)rect.getMaxY());            
//                            lineProcessor.processLine(line, height); 
//
//                            
//                            line.Clear();
//                            line.add((int)rect.getMinX(),(int)(rect.getMinY()+(rect.getWidth()/2)));
//                            line.add((int)rect.getMaxX(), (int)(rect.getMinY()+(rect.getWidth()/2)));            
//                            lineProcessor.processLine(line, height);                         
//                        break;
//                    case OVAL:case RECTANGULAR:
//                            line =new Line(pad.getWidth()/2,0);
//                            line.add((int)(rect.getMinX()+(rect.getWidth()/2)), (int)rect.getMinY());
//                            line.add((int)(rect.getMinX()+(rect.getWidth()/2)), (int)rect.getMaxY());            
//                            lineProcessor.processLine(line, height);
//                                                   
//                            line.Clear();
//                            line.setThickness(pad.getHeight()/2); 
//                            line.add((int)rect.getMinX(), (int)(rect.getMinY()+(rect.getHeight()/2)));
//                            line.add((int)rect.getMaxX(), (int)(rect.getMinY()+(rect.getHeight()/2)));                                    
//                            lineProcessor.processLine(line, height);
//                        break;
//
//                    }
//                      
//                    
//                                    
//               }
//            }
//        }
//    }
    
    private void processRegion(List<? extends Point> region,int height){
        
        double lastX=-1,lastY=-1;
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
            if (!Utils.EQ(point.x ,lastX)){                    
                lastX = point.x;
                commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
            }
            if (!Utils.EQ(point.y,lastY))
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
        if (!Utils.EQ(point.x ,lastX)){                  
            lastX = point.x;
            commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
        }
        if (!Utils.EQ(point.y,lastY))
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
    
//    private void processText(Unit<? extends Shape> board,CopperAreaShape source){
//            List<Point> region=new ArrayList<>(4);
//            for(GlyphLabel label:board.<GlyphLabel>getShapes(GlyphLabel.class,source.getCopper().getLayerMaskID())){      
//               Rectangle rect=label.getTexture().getBoundingShape();
//               rect.grow(( ((ClearanceTarget)label).getClearance()!=0?((ClearanceTarget)label).getClearance():source.getClearance()), ((ClearanceTarget)label).getClearance()!=0?((ClearanceTarget)label).getClearance():source.getClearance());  
//               //is this in region
//               if(!(source).getBoundingShape().intersects(rect)){
//                   continue; 
//               }
//               region.clear();
//               region.add(new Point(rect.x,rect.y));
//               region.add(new Point(rect.x+rect.width,rect.y));
//               region.add(new Point(rect.x+rect.width,rect.y+rect.height));
//               region.add(new Point(rect.x,rect.y+rect.height));
//                
//               processRegion(region, board.getHeight());
//            }                                      
//        }        
    
    private void processTracks(Unit<? extends Shape> board,CopperAreaShape source){
        int height=board.getHeight();
        
        List<TrackShape> tracks= board.getShapes(TrackShape.class, source.getCopper().getLayerMaskID());              
        for(TrackShape track:tracks){
            if(track.isSameNet(source)){
                continue;
            }
            
            double lastX=-1,lastY=-1;
            boolean firstPoint=true;
            
            int lineThickness=(track.getThickness()+2*(track.getClearance()!=0?track.getClearance():source.getClearance()));
            
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
                        
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(lineThickness);
            //set aperture if not same
            context.resetAperture(aperture);
            
            for(Point point:track.getLinePoints()){
                StringBuffer commandLine=new StringBuffer();
                if (!Utils.EQ(point.x ,lastX)){                  
                    lastX = point.x;
                    commandLine.append("X"+context.getFormatter().format(Grid.COORD_TO_MM(point.x)*100000));
                }
                if (!Utils.EQ(point.y ,lastY)){                                     
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
        double lastX=-1,lastY=-1;
        int height=board.getHeight(); 
        //select vias of the region layer
        for(ViaShape via:board.<ViaShape>getShapes(ViaShape.class,source.getCopper().getLayerMaskID())){                        
            if(via.isSameNet(source)){
                continue;
            }
            Box rect=via.getBoundingShape();             
            rect.grow(via.getClearance()!=0?via.getClearance():source.getClearance());

            if(!source.getBoundingShape().intersects(rect)){
               continue; 
            }

            
            //set linear mode if not set
            context.resetCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION);
            
            ApertureDefinition aperture=context.getApertureDictionary().findCircle(rect.getWidth());
            
            //set aperture if not same        
            context.resetAperture(aperture);
            
            //flash the via!!!
            StringBuffer commandLine=new StringBuffer(); 
            if (!Utils.EQ(via.getCenter().x ,lastX)){                 
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
