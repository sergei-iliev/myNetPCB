package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.GlyphLabel;

import java.util.Collection;

public class ApertureTextProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureTextProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }
        
    @Override
    public void process(Unit<? extends Shape> board, int layermask) {
        //board text
        for(GlyphLabel label:board.<GlyphLabel>getShapes(GlyphLabel.class,layermask)){
               processTexture(label.getTexture());                               
        }
        //text in footprints
        for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            //grab text
            for(Texture text:((Textable)footprint).getChipText().getChildren()){
                if(!text.isEmpty()&&((text.getLayermaskId()&layermask)!=0)){
                    processTexture((GlyphTexture)text);
                }
            }
            
                for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== GlyphLabel.class){
                    processTexture(((GlyphLabel)shape).getTexture());
                }
            }
        }
        
    }
    
    private void processTexture(GlyphTexture texture){
        CircleAperture circle=new CircleAperture();
        circle.setDiameter(texture.getThickness());
        dictionary.add(circle); 
    }    
}
