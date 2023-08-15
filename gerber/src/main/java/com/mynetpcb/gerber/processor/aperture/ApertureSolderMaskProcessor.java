package com.mynetpcb.gerber.processor.aperture;

import java.util.Collection;
import java.util.List;

import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.ApertureDefinition;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.shape.pad.CircularShape;

public class ApertureSolderMaskProcessor implements Processor{
    
    private final ApertureDictionary dictionary;
    
    public ApertureSolderMaskProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {          
        List<FootprintShape> footprints= board.getShapes(FootprintShape.class);              
        double diameter;
        for(FootprintShape footprint:footprints){
            Collection<Pad> pads=(Collection<Pad>)footprint.getPads();
            for(Pad pad:pads){
                if((((pad.getCopper().getLayerMaskID()&Layer.LAYER_FRONT)!=0)&&((layermask&Layer.SOLDERMASK_LAYER_FRONT)!=0))||
                    	(((pad.getCopper().getLayerMaskID()&Layer.LAYER_BACK)!=0)&&((layermask&Layer.SOLDERMASK_LAYER_BACK)!=0))) {                
                    ApertureDefinition apperture=null;
                    switch(pad.getShapeType()){
                    case CIRCULAR:
                        apperture=new CircleAperture();
                        diameter=((CircularShape)pad.getPadDrawing()).getDiameter()+2*pad.getSolderMaskExpansion();                        
                        ((CircleAperture)apperture).setDiameter(diameter);
                        break;
                    case OVAL:
                        apperture=new CircleAperture();  
                        Obround obround=((Obround)pad.getPadDrawing().getGeometricFigure());//.getDiameter()+2*pad.getSolderMaskExpansion();
                        var o=obround.clone();
                        o.grow(pad.getSolderMaskExpansion(),pad.getRotate());                        
                        ((CircleAperture)apperture).setDiameter(o.getDiameter());                          
                        break;
                    case RECTANGULAR:case POLYGON:
                            //add default
                            CircleAperture circle=new CircleAperture();
                            circle.setDiameter(Grid.MM_TO_COORD(1));
                            dictionary.add(circle);                                                                        
                    }
                    if(apperture!=null){   //not all pads produce apperture(rect and polygon)
                      //if(pad.getType()==Pad.Type.SMD){
                      //  apperture.setAttribute(new SMDPadAttribute());  
                      //}else{
                      //  apperture.setAttribute(new ComponentPadAttribute());                                                
                      //}                    
                      dictionary.add(apperture);                    
                    }
                }
            }
        
        }
    }
}
