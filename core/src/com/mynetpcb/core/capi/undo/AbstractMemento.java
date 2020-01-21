package com.mynetpcb.core.capi.undo;

import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Utils;

import java.util.UUID;


public abstract class AbstractMemento<U extends Unit,S extends Shape> {


    //***could be the Moveable target uuid or the owner of the text or Unit
    protected UUID uuid;

    private UUID parentUUID;

    protected final MementoType mementoType;
    
    protected int layerindex;
    
    protected int thickness;
    
    protected int fill;
    
    protected double rotate;

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
    
    public void clear(){
       uuid=null;
       parentUUID=null;
    }

    public  void loadStateTo(S shape){
        shape.setUUID(UUID.fromString(uuid.toString()));
        shape.setCopper(Layer.Copper.values()[layerindex]);
        shape.setThickness(this.thickness);
        shape.setFill(Fill.values()[this.fill]);
        shape.setRotate(rotate);
    }
    
    public void saveStateFrom(S shape) {
        //common fields
        this.layerindex=shape.getCopper().ordinal();
        this.thickness=shape.getThickness();
        this.fill=shape.getFill().ordinal();
        this.rotate=shape.getRotate();
        //***clone!
        if (shape.getUUID() != null) {
            uuid = UUID.fromString(shape.getUUID().toString());
        }

//        if (shape instanceof Ownerable&&((Ownerable)shape).getOwner()!=null){
//            parentUUID =
//                    UUID.fromString(((Ownerable)shape).getOwner().getUUID().toString());
//        }

    }
    
    @Override
    public boolean equals(Object obj){
        if(this==obj){
          return true;  
        }
        if(!(obj instanceof AbstractMemento)){
          return false;  
        }         
        AbstractMemento other=(AbstractMemento)obj;
        return (other.getMementoType().equals(this.getMementoType())&&
                other.getUUID().equals(this.getUUID())&&
                other.thickness==this.thickness&&
                other.fill==this.fill&&
                Utils.EQ(other.rotate,this.rotate)&&
                other.layerindex==this.layerindex
               );
        
      
    }
    
    @Override
    public int hashCode(){            
       int hash=31+getUUID().hashCode()+this.getMementoType().hashCode()+this.fill+this.thickness+this.layerindex+Double.hashCode(this.rotate);
       return hash;
    }
    

    public boolean isSameState(Unit unit) {
        Shape other=unit.getShape(getUUID());              
        return (other.getThickness()==this.thickness&&other.getFill().ordinal()==this.fill&&other.getCopper().ordinal()==this.layerindex&& Utils.EQ(other.getRotate(),this.rotate));                                
    }
}


