package com.mynetpcb.gerber.processor.command;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Rectangle;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.d2.shapes.Vector;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.extended.LevelPolarityCommand;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.pad.shape.Pad;


public class CommandRegionProcessor implements Processor {
    private final GraphicsStateContext context;
    private final CommandLineProcessor lineProcessor;  
    
    public CommandRegionProcessor(GraphicsStateContext context) {
        this.context = context;
        this.lineProcessor=new CommandLineProcessor(context);
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
          processPads(board, region,layermask); 
          //processText(board,region); 
            
          context.resetPolarity(LevelPolarityCommand.Polarity.DARK);
          
          //process THERMAL pads   
          processThermalPads(board,region);   
        }

    }
    
    private void processPads(Unit<? extends Shape> board,CopperAreaShape source, int layermask){
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        CommandPadProcessor commandPadProcessor=new CommandPadProcessor(context);
        
        for(FootprintShape footprint:footprints){
            //check if footprint in copper area
            if(!source.getBoundingShape().intersects(footprint.getBoundingShape())){
               continue; 
            }
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){
                // is pad  within copper area
                Box rect = pad.getBoundingShape();
                rect.grow(source.getClearance());
                
                if(!(source).getBoundingShape().intersects(rect)){
                   continue; 
                }                
                //if on the same net and DIRECT connection do nothing	
                if(pad.isSameNet(source)&&source.getPadConnection()==PadShape.PadConnection.DIRECT){
                    continue;
                }
                if(pad.isVisibleOnLayers(source.getCopper().getLayerMaskID())){                                            
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
                    obround.grow(source.getClearance(),pad.getRotate());
                    commandPadProcessor.processOval(obround,null,board.getHeight());
                    break;
                case POLYGON:
                    Hexagon hexagon= (Hexagon)pad.getPadDrawing().getGeometricFigure();
                    hexagon=hexagon.clone();
                    hexagon.grow(source.getClearance());
                    commandPadProcessor.processPolygon(hexagon,board.getHeight());  
                    break;
                }                
              }else {
                  //in case of DRILL hole and pad has no part in this layer, still clearance has to be provided  
                  if(pad.getType()==PadShape.Type.THROUGH_HOLE){                      
                      Circle c=pad.getDrill().getGeometricFigure().clone();
                      c.grow(source.getClearance());                      
                      commandPadProcessor.processCircle(c,null,board.getHeight()); 
                  }            	  
              }
                
                
                
            }
        }
    }    

    /*
     * draw cross hair to represent THERMAL pads connections
     */
    
    private void processThermalPads(Unit<? extends Shape> board,CopperAreaShape source){    	 
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        for(FootprintShape footprint:footprints){
            //check if footprint in copper area
            if(!source.getBoundingShape().intersects(footprint.getBoundingShape())){
               continue; 
            }
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){            
                // is pad  within copper area
                Box rect = pad.getBoundingShape();
                rect.grow(source.getClearance());
                
                if(!(source).getBoundingShape().intersects(rect)){
                   continue; 
                }
                                
                if(pad.isVisibleOnLayers(source.getCopper().getLayerMaskID())){      //same layer                                 
                    /*
                     * GROUND and VCC net
                     * Use region contour
                     */
                    //1. THERMAL makes sense if pad has copper on source layer                    
                    if(source.isSameNet(pad) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){                  
                      switch(pad.getShapeType()){
                       case CIRCULAR:
                    	   double r=((Circle)pad.getPadDrawing().getGeometricFigure()).r/2;
                    	   var list=new ArrayList<Point>();
                    	   rect.grow(Grid.MM_TO_COORD(0.1));
                    	   list.add(new Point(rect.min.x+rect.getWidth()/2-r,rect.min.y));                    	   
                    	   list.add(new Point(rect.min.x+rect.getWidth()/2+r,rect.min.y));                    	   
                    	   list.add(new Point(rect.min.x+rect.getWidth()/2+r,rect.max.y));                    	   
                    	   list.add(new Point(rect.min.x+rect.getWidth()/2-r,rect.max.y));                    	  
                    	   processRegion(list,board.getHeight());
                    	   list.clear();                    	   
                    	   list.add(new Point(rect.min.x,rect.min.y+rect.getWidth()/2-r));
                    	   list.add(new Point(rect.min.x,rect.min.y+rect.getWidth()/2+r));
                    	   list.add(new Point(rect.max.x,rect.min.y+rect.getWidth()/2+r));                    	   
                    	   list.add(new Point(rect.max.x,rect.min.y+rect.getWidth()/2-r));                    	  
                    	   processRegion(list,board.getHeight());
                    	                             
                           break;
                       case OVAL:
                    	   list=new ArrayList<Point>();
                           var oo=(Obround)pad.getPadDrawing().getGeometricFigure();
                           var o=oo.clone();
                           o.grow(source.getClearance()+Grid.MM_TO_COORD(0.1),pad.getRotate());
                           //***horizontal
                           r=o.getDiameter()/2;
                           //first point on line
                           Vector v=new Vector(o.pe,o.ps);
                           Vector n=v.normalize();
                           double a=o.ps.x +r*n.x;
                           double b=o.ps.y +r*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();
                           a=a +((oo.getDiameter() /4))*n.x;
                           b=b +((oo.getDiameter() /4))*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();
                           a=a +(o.ps.distanceTo(o.pe)+2*r)*n.x;
                           b=b +(o.ps.distanceTo(o.pe)+2*r)*n.y;  
                           list.add(new Point(a,b));

                           n.rotate90CCW();
                           a=a +((oo.getDiameter() /2))*n.x;
                           b=b +((oo.getDiameter() /2))*n.y;                             
                           list.add(new Point(a,b));

                           n.rotate90CCW();
                           a=a +(o.ps.distanceTo(o.pe)+2*r)*n.x;
                           b=b +(o.ps.distanceTo(o.pe)+2*r)*n.y;  
                           list.add(new Point(a,b));
                           
                           processRegion(list,board.getHeight());
 
      
                           //***vertical
                           list.clear();
                           v.rotate90CW();
                           n=v.normalize();
                           a=o.pc.x +r*n.x;
                           b=o.pc.y +r*n.y;
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();
                           double d=(((oo.ps.distanceTo(oo.pe)+oo.getDiameter())/2));
                           a=a +(d/2)*n.x;
                           b=b +(d/2)*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();
                           a=a +2*r*n.x;
                           b=b +2*r*n.y;
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();                    
                           a=a +(d)*n.x;
                           b=b +(d)*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();                    
                           a=a +2*r*n.x;
                           b=b +2*r*n.y;                             
                           list.add(new Point(a,b));
                           
                           
                           processRegion(list,board.getHeight());
                           break;
                       case RECTANGULAR:
                    	   list=new ArrayList<Point>();
                    	   var rr=((Rectangle)pad.getPadDrawing().getGeometricFigure());                    	   
                           var rrr=rr.clone();
                           rrr.grow(source.getClearance()+Grid.MM_TO_COORD(0.1));
                           //first line	
                           d=rrr.points.get(0).distanceTo(rrr.points.get(1));
                           double w=rr.points.get(0).distanceTo(rr.points.get(1));
                           
                           v=new Vector(rrr.points.get(0),rrr.points.get(1));
                           n=v.normalize();
                           var p=rrr.points.get(0).middleOf(rrr.points.get(1));
                           
                           a=p.x +w/4*n.x;
                           b=p.y +w/4*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();  
                           a=a +rrr.points.get(1).distanceTo(rrr.points.get(2))*n.x;
                           b=b +rrr.points.get(1).distanceTo(rrr.points.get(2))*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();
                           a=a +w/2*n.x;
                           b=b +w/2*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();  
                           a=a +rrr.points.get(1).distanceTo(rrr.points.get(2))*n.x;
                           b=b +rrr.points.get(1).distanceTo(rrr.points.get(2))*n.y;                             
                           list.add(new Point(a,b));
                                                      
                           processRegion(list,board.getHeight());
                           
                           //second line
                           list.clear();
                           d=rrr.points.get(1).distanceTo(rrr.points.get(2));
                           double hh=rr.points.get(1).distanceTo(rr.points.get(2));
                           v=new Vector(rrr.points.get(1),rrr.points.get(2));
                           n=v.normalize();
                           p=rrr.points.get(1).middleOf(rrr.points.get(2));
                           
                           a=p.x +hh/4*n.x;
                           b=p.y +hh/4*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();  
                           a=a +rrr.points.get(2).distanceTo(rrr.points.get(3))*n.x;
                           b=b +rrr.points.get(2).distanceTo(rrr.points.get(3))*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();
                           a=a +hh/2*n.x;
                           b=b +hh/2*n.y;                             
                           list.add(new Point(a,b));
                           
                           n.rotate90CCW();  
                           a=a +rrr.points.get(2).distanceTo(rrr.points.get(3))*n.x;
                           b=b +rrr.points.get(2).distanceTo(rrr.points.get(3))*n.y;                             
                           list.add(new Point(a,b));
                           
                           processRegion(list,board.getHeight());
                          break;                           
                       case POLYGON:
                    	   var h=((Hexagon)pad.getPadDrawing().getGeometricFigure()).clone();
                    	   d=h.width/3;
                    	   
                    	   h.grow(source.getClearance());
                    	   drawLine(h, 0, d, board.getHeight());
                    	   drawLine(h, 1, d, board.getHeight());
                    	   drawLine(h, 2, d, board.getHeight());                    	                       	                       	   
                    	   break;

                       }                                    
                    }                                 
                }                                                   
            }    
     }
    }
    /**
     * Utility to avoid repeat
     */
    private void drawLine(Hexagon refHegagone,int refIndex,double thickness,int height){    	
    	var list=new ArrayList<Point>();
        double r=refHegagone.width/2;
        Vector v=new Vector(refHegagone.pc,refHegagone.points.get(refIndex));
        
        v.rotate90CW();
        Vector n=v.normalize();            
        double a=refHegagone.pc.x +r*n.x;
        double b=refHegagone.pc.y +r*n.y;       
        
        n.rotate90CW();
        a=a +thickness/2*n.x;
        b=b +thickness/2*n.y;
        list.add(new Point(a,b));
        
        n.rotate90CW();
        a=a +2*r*n.x;
        b=b +2*r*n.y;
        list.add(new Point(a,b));

        n.rotate90CW();
        a=a +thickness*n.x;
        b=b +thickness*n.y;
        list.add(new Point(a,b));

        n.rotate90CW();
        a=a +2*r*n.x;
        b=b +2*r*n.y;
        list.add(new Point(a,b));

        processRegion(list,height);

        
    }    	    	    
    
    
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
        //No need to set apperture in REGION mode
        //context.resetAperture(aperture);
        
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
