package com.mynetpcb.gerber.processor.aperture;

import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.attribute.aperture.ViaPadAttribute;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;

public class ApertureViaProcessor implements Processor{
    
    private final ApertureDictionary dictionary;
    
    public ApertureViaProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
            
            for(ViaShape via:board.<ViaShape>getShapes(ViaShape.class,layermask)){
                CircleAperture circle=new CircleAperture();
                circle.setDiameter(via.getOuter().r*2);
                circle.setAttribute(new ViaPadAttribute());
                dictionary.add(circle);                         
            }
        }
    
}