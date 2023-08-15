package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.board.Net;
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
import com.mynetpcb.d2.shapes.Rectangle;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.shape.pad.CircularShape;

import java.util.Collection;
import java.util.List;

public class ApertureRegionProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureRegionProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {        
        //add D10 if not there
        Collection<CopperAreaShape> regions=board.getShapes(CopperAreaShape.class,layermask);                       
        if(regions.size()>0){
           ApertureDefinition aperture= dictionary.get(10);
           if(aperture==null){
               //add default
               CircleAperture circle=new CircleAperture();
               circle.setDiameter(Grid.MM_TO_COORD(1));
               dictionary.add(circle);               
           }
        }
        
        for(CopperAreaShape region:regions){
          this.processVias(board,region);    
          this.processTracks(board, region);
          this.processPads(board, region); 
          //TODO process text
        }
    }
                
    private void processPads(Unit<? extends Shape> board,CopperAreaShape source){
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
                
                ApertureDefinition apperture=null;
                double diameter;
                if(pad.isVisibleOnLayers(source.getCopper().getLayerMaskID())){      //same layer              
                    switch(pad.getShapeType()){
                    case CIRCULAR:                                                
                        apperture=new CircleAperture();
                        diameter=((CircularShape)pad.getPadDrawing()).getDiameter();
                        ((CircleAperture)apperture).setDiameter(diameter+(2*source.getClearance()));
                        dictionary.add(apperture);
                        break;
                    case OVAL:                         
                    	var o=((Obround)pad.getPadDrawing().getGeometricFigure()).clone();
                    	o.grow(source.getClearance(),pad.getRotate());
                    	apperture=new CircleAperture();                          
                        ((CircleAperture)apperture).setDiameter(o.getDiameter());
                        dictionary.add(apperture);
                        break;
                    case RECTANGULAR:case POLYGON:
                        //add default
                        apperture=new CircleAperture();
                        ((CircleAperture)apperture).setDiameter(Grid.MM_TO_COORD(1));
                        dictionary.add(apperture);  
                    }                    
                    /*
                     * GROUND and VCC net
                     * USE REGION!
                     */
                    
                    //1. THERMAL makes sense if pad has copper on source layer                    
                    /*
                    if(source.isSameNet(pad) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){                  
                      switch(pad.getShapeType()){
                       case CIRCULAR:
                    	   apperture=new CircleAperture();                    	                       	   
                           ((CircleAperture)apperture).setDiameter(pad.getWidth()/2);
                           dictionary.add(apperture);
                    	   break;
                       case OVAL:
                           apperture=new CircleAperture();  
                           ((CircleAperture)apperture).setDiameter(pad.getWidth()/2);                           
                           dictionary.add(apperture);
                           
                           apperture=new CircleAperture();  
                           ((CircleAperture)apperture).setDiameter(pad.getHeight()/2);                           
                           dictionary.add(apperture);
                           
                    	   break;
                       case RECTANGULAR:
                    	   var r=((Rectangle)pad.getPadDrawing().getGeometricFigure());                    	   
                    	   double d=r.points.get(0).distanceTo(r.points.get(1));
                           apperture=new CircleAperture();  
                           ((CircleAperture)apperture).setDiameter(d/2);                           
                           dictionary.add(apperture);
                           
                    	   d=r.points.get(1).distanceTo(r.points.get(2));
                           apperture=new CircleAperture();  
                           ((CircleAperture)apperture).setDiameter(d/2);                           
                           dictionary.add(apperture);
                    	   
                           break;                    	   
                       case POLYGON:                    	   
                    	   var h=((Hexagon)pad.getPadDrawing().getGeometricFigure());
                    	   d=h.width/3;
                           apperture=new CircleAperture();  
                           ((CircleAperture)apperture).setDiameter(d);                           
                           dictionary.add(apperture);                    	   
                    	   break;
                       }                                    
                    } 
                     */                              
                }else{
                  //in case of DRILL hole and pad has no part in this layer, still clearance has to be provided  
                    if(pad.getType()==PadShape.Type.THROUGH_HOLE){                        
                        Circle c=pad.getDrill().getGeometricFigure().clone();
                        c.grow(source.getClearance());  
                        apperture=new CircleAperture();
                        ((CircleAperture)apperture).setDiameter(2*c.r);
                        dictionary.add(apperture);
                    }
                }                                      
            
            }    
    }
    }

    private void processTracks(Unit<? extends Shape> board,CopperAreaShape source){
        for(TrackShape track:board.<TrackShape>getShapes(TrackShape.class,source.getCopper().getLayerMaskID())){
            
            if(track.isSameNet(source)){
                continue;
            }
            
            int lineThickness=(track.getThickness()+2*(track.getClearance()!=0?track.getClearance():source.getClearance()));
          
            
            CircleAperture circle=new CircleAperture();
            circle.setDiameter(lineThickness);            
            dictionary.add(circle);                           
        }
    }

    private  void  processVias(Unit<? extends Shape> board,CopperAreaShape source){
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
            
            CircleAperture circle=new CircleAperture();
            circle.setDiameter(rect.getWidth());            
            dictionary.add(circle);                         
        }            
    }
    
}
