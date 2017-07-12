package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBLabel;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.aperture.type.ObroundAperture;
import com.mynetpcb.gerber.aperture.type.PolygonAperture;
import com.mynetpcb.gerber.aperture.type.RectangleAperture;
import com.mynetpcb.gerber.attribute.aperture.ComponentPadAttribute;
import com.mynetpcb.gerber.attribute.aperture.ConductorAttribute;
import com.mynetpcb.gerber.attribute.aperture.SMDPadAttribute;
import com.mynetpcb.gerber.attribute.aperture.ViaPadAttribute;
import com.mynetpcb.gerber.attribute.drill.ComponentDrillAttribute;
import com.mynetpcb.gerber.capi.Processor;

import com.mynetpcb.pad.shape.Pad;

import java.awt.Rectangle;

import java.awt.geom.Rectangle2D;

import java.util.Collection;
import java.util.List;

public class ApertureRegionProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureRegionProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Board board, int layermask) {        
        //add D10 if not there
        Collection<PCBCopperArea> regions=board.getShapes(PCBCopperArea.class,layermask);                       
        if(regions.size()>0){
           ApertureDefinition aperture= dictionary.get(10);
           if(aperture==null){
               //add default
               CircleAperture circle=new CircleAperture();
               circle.setDiameter(Grid.MM_TO_COORD(1));
               dictionary.add(circle);               
           }
        }
        
        for(PCBCopperArea region:regions){
          processVias(board,region);    
          processTracks(board, region);
          processPads(board, region); 
        }
    }
        
    private void processPads(Board board,PCBCopperArea source){
        List<PCBFootprint> footprints= board.getShapes(PCBFootprint.class);              
        for(PCBFootprint footrpint:footprints){
            Collection<Pad> pads=footrpint.getPins();
            for(Pad pad:pads){
                if(pad.isVisibleOnLayers(source.getCopper().getLayerMaskID())){
                    ApertureDefinition apperture=null;
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
                    

                    dictionary.add(apperture);                    
                }
            }    
    }
    }
    private void processTracks(Board board,PCBCopperArea source){
        for(PCBTrack track:board.<PCBTrack>getShapes(PCBTrack.class,source.getCopper().getLayerMaskID())){
            
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
    private void processVias(Board board,PCBCopperArea source){
        //select vias of the region layer
        for(PCBVia via:board.<PCBVia>getShapes(PCBVia.class,source.getCopper().getLayerMaskID())){
            Rectangle inner=via.getBoundingShape().getBounds();             
            inner.grow(source.getClearance(), source.getClearance());
            if(!source.getBoundingShape().intersects(inner)){
               continue; 
            }
            CircleAperture circle=new CircleAperture();
            circle.setDiameter((int)inner.getWidth());            
            dictionary.add(circle);                         
        }            
    }
    
}
