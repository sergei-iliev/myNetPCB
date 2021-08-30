package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.gerber.Fillable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;

public class ApertureFilledContourProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureFilledContourProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext, Unit<? extends Shape> board, int layermask) {
        //process arc fills
        for(Shape shape:board.getShapes()){
            if(!shape.isVisibleOnLayers(layermask)){
                continue;
            }
            if(shape instanceof Fillable){ 
                //add default
                CircleAperture circle=new CircleAperture();
                circle.setDiameter(Grid.MM_TO_COORD(1));
                dictionary.add(circle);  
                serviceContext.setParameter(GerberServiceContext.FILLED_CONTOUR,true);
                break;
            }
        }        
        
    }
}
