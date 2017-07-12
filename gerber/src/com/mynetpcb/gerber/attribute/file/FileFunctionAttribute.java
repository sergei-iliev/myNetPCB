package com.mynetpcb.gerber.attribute.file;

import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.attribute.AbstractAttribute;


public class FileFunctionAttribute extends AbstractAttribute {
    public FileFunctionAttribute(int layermask) {
        super("TF.FileFunction",null,"");
        this.name=produce(layermask);
    }

    @Override
    public String print() {
        return  ("%"+String.format("%s,%s",command,name)+"*%");
    }
    
    /*
     * From PCB internal layer name to external gerber layer name
     */
    private String produce(int layermask){
        
        if(layermask==Layer.SILKSCREEN_LAYER_FRONT){
            return "Legend,Top";
        }            
        if(layermask==Layer.SOLDERMASK_LAYER_FRONT){
            return "Soldermask,Top";
        } 
        if(layermask==Layer.LAYER_FRONT){
            return "Copper,L1,Top";
        }
        //*************************
        if(layermask==Layer.LAYER_BACK){
            return "Copper,L2,Bot";
        }
        if(layermask==Layer.SOLDERMASK_LAYER_BACK){
            return "Soldermask,Bot";
        } 
        if(layermask==Layer.SILKSCREEN_LAYER_BACK){
            return "Legend,Bot";
        }                                
        throw new IllegalStateException("Unsupported layer: "+layermask);
    }
}
