package com.mynetpcb.core.capi.undo;

import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.Layer;

import java.util.UUID;


public abstract class AbstractMemento<U extends Unit,S extends Shape> {


    //***could be the Moveable target uuid or the owner of the text or Unit
    protected UUID uuid;

    private UUID parentUUID;

    protected final MementoType mementoType;
    
    protected int layerindex;
    
    protected int thickness;
    
    protected int fill;
    

    public AbstractMemento(MementoType mementoType) {
        this.mementoType = mementoType;
    }
    
    public MementoType getMementoType(){
      return mementoType;  
    }
    
    public UUID getUUID() {
        return uuid;
    }

    public UUID getParentUUID() {
        return parentUUID;
    }

    public abstract boolean isSameState(U unit);
    
    public void Clear(){
       uuid=null;
       parentUUID=null;
    }

    public  void loadStateTo(S shape){
        shape.setUUID(UUID.fromString(uuid.toString()));
        shape.clearCache();
        shape.setCopper(Layer.Copper.values()[layerindex]);
        shape.setThickness(this.thickness);
        shape.setFill(Fill.values()[this.fill]);
    }
    
    public void saveStateFrom(S shape) {
        //common fields
        this.layerindex=shape.getCopper().ordinal();
        this.thickness=shape.getThickness();
        this.fill=shape.getFill().ordinal();
        //***clone!
        if (shape.getUUID() != null) {
            uuid = UUID.fromString(shape.getUUID().toString());
        }

        if (shape instanceof Ownerable&&((Ownerable)shape).getOwner()!=null){
            parentUUID =
                    UUID.fromString(((Ownerable)shape).getOwner().getUUID().toString());
        }

    }
    

}


