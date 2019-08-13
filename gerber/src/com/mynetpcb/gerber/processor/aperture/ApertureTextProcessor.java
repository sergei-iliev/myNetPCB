package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.glyph.GlyphTexture;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.GerberServiceContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.GlyphLabel;

public class ApertureTextProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureTextProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }
        
    @Override
    public void process(GerberServiceContext serviceContext,Unit<? extends Shape> board, int layermask) {
        //board text
        for(GlyphLabel label:board.<GlyphLabel>getShapes(GlyphLabel.class,layermask)){
               processTexture(label.getTexture());                               
        }
        //text in footprints(reference,value)
        for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            //grab text
            for(Texture text:footprint.getChipText().getChildren()){
                boolean isRefPrintable=serviceContext.getParameter(GerberServiceContext.FOOTPRINT_REFERENCE_ON_SILKSCREEN, Boolean.class);
                if(text.getTag().equals("reference")&&isRefPrintable){
                    if(!text.isEmpty()&&((text.getLayermaskId()&layermask)!=0)){
                        processTexture((GlyphTexture)text);
                    }   
                }
                boolean isValPrintable=serviceContext.getParameter(GerberServiceContext.FOOTPRINT_VALUE_ON_SILKSCREEN, Boolean.class);
                if(text.getTag().equals("value")&&isValPrintable){
                    if(!text.isEmpty()&&((text.getLayermaskId()&layermask)!=0)){
                        processTexture((GlyphTexture)text);
                    }   
                }
                
                
            }
            //footprint labels
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
