package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.board.shape.CopperAreaShape;
import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.board.shape.TrackShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.aperture.type.ObroundAperture;
import com.mynetpcb.gerber.aperture.type.PolygonAperture;
import com.mynetpcb.gerber.aperture.type.RectangleAperture;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;

import java.awt.Rectangle;

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
            if(!source.getBoundingShape().intersects(footprint.getBoundingShape().getBounds())){
               continue; 
            }
            Collection<PadShape> pads=footprint.getPins();
            for(PadShape pad:pads){            
                // is pad  within copper area
                Rectangle rect = pad.getBoundingShape().getBounds();
                rect.grow(source.getClearance(), source.getClearance());
                
                if(!(source).getBoundingShape().intersects(rect)){
                   continue; 
                }

                if(Utilities.isSameNet(source,pad)){
                    //is this THERMAL pad
                    if(source.getPadConnection()==PadShape.PadConnection.THERMAL){
                      //add drawing line    
                      switch(pad.getShape()){
                      case CIRCULAR: 
                         CircleAperture apperture=new CircleAperture();
                         apperture.setDiameter(pad.getWidth()/2);
                         dictionary.add(apperture);  
                      break;
                      case OVAL: case RECTANGULAR:
                         apperture=new CircleAperture();
                         apperture.setDiameter(pad.getWidth()/2);
                         dictionary.add(apperture); 
                          
                         apperture=new CircleAperture();
                         apperture.setDiameter(pad.getHeight()/2);
                         dictionary.add(apperture);                           
                       break;
                      }
                    }else{
                       //there is no clearence when both Copper area and Pad have the same Net value
                       continue;
                    }
                }
                ApertureDefinition apperture=null;
                if(pad.isVisibleOnLayers(source.getCopper().getLayerMaskID())){                    
                    switch(pad.getShape()){
                    case CIRCULAR:                                                
                        apperture=new CircleAperture();
                        ((CircleAperture)apperture).setDiameter(pad.getWidth()+(2*source.getClearance()));
                        break;
                    case OVAL:                        
                        apperture=new ObroundAperture();
                        ((ObroundAperture)apperture).setX(pad.getWidth()+(2*source.getClearance()));
                        ((ObroundAperture)apperture).setY(pad.getHeight()+(2*source.getClearance())); 
                        break;
                    case RECTANGULAR:
                        apperture=new RectangleAperture();
                        ((RectangleAperture)apperture).setX(pad.getWidth()+(2*source.getClearance()));
                        ((RectangleAperture)apperture).setY(pad.getHeight()+(2*source.getClearance())); 
                        break;
                    case POLYGON:
                        apperture= new PolygonAperture();
                        ((PolygonAperture)apperture).setDiameter(pad.getWidth()+(2*source.getClearance()));
                        ((PolygonAperture)apperture).setVerticesNumber(6);
                    }                    
                                 
                }else{
                  //in case of DRILL hole and pad has no part in this layer, still clearance has to be provided  
                    if(pad.getType()==PadShape.Type.THROUGH_HOLE){
                        apperture=new CircleAperture();
                        ((CircleAperture)apperture).setDiameter(pad.getWidth()+(2*source.getClearance()));                     
                    }
                }
                
                
                if(apperture!=null){
                   dictionary.add(apperture);
                }
            }    
    }
    }
    private void processTracks(Unit<? extends Shape> board,CopperAreaShape source){
        for(TrackShape track:board.<TrackShape>getShapes(TrackShape.class,source.getCopper().getLayerMaskID())){
            
            if(Utilities.isSameNet(source,track)){
                continue;
            }
            
            int lineThickness;
            if(track.getClearance()!=0){
              lineThickness=(track.getThickness()+2*track.getClearance());
            }else{
              lineThickness=(track.getThickness()+2*source.getClearance());
            }
            
            CircleAperture circle=new CircleAperture();
            circle.setDiameter(lineThickness);            
            dictionary.add(circle);                           
        }
    }
    private  void  processVias(Unit<? extends Shape> board,CopperAreaShape source){
        //select vias of the region layer
        for(ViaShape via:board.<ViaShape>getShapes(ViaShape.class,source.getCopper().getLayerMaskID())){          
            if(Utilities.isSameNet(source,via)){
                continue;
            }
            Rectangle rect=via.getBoundingShape().getBounds();             
            rect.grow(via.getClearance()!=0?via.getClearance():source.getClearance(),via.getClearance()!=0?via.getClearance():source.getClearance());

            if(!source.getBoundingShape().intersects(rect)){
               continue; 
            }
            
            CircleAperture circle=new CircleAperture();
            circle.setDiameter((int)rect.getWidth());            
            dictionary.add(circle);                         
        }            
    }
    
}
