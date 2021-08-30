package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.symbol.shape.FontLabel;

public class SCHLabel extends FontLabel implements  Externalizable {

    public SCHLabel clone() throws CloneNotSupportedException {
        SCHLabel copy = (SCHLabel)super.clone();            
        return copy;
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    
    public static class Memento extends AbstractMemento<Circuit,SCHLabel>{
        Texture.Memento textureMemento;
        
        public Memento(MementoType mementoType){
          super(mementoType);  
          textureMemento=new SymbolFontTexture.Memento();
        }
        @Override
        public void loadStateTo(SCHLabel shape) {
          super.loadStateTo(shape);  
          textureMemento.loadStateTo(shape.getTexture());  
        }
        @Override
        public void saveStateFrom(SCHLabel shape){
            super.saveStateFrom(shape);
            textureMemento.saveStateFrom(shape.getTexture());
        }
        
        @Override
        public boolean equals(Object obj){
            if(this==obj){
              return true;  
            }
            if(!(obj instanceof Memento)){
              return false;  
            }
            
            Memento other=(Memento)obj;

            return super.equals(obj)&&textureMemento.equals(other.textureMemento);                          
        }
        
        @Override
        public int hashCode(){
          int hash = super.hashCode()+textureMemento.hashCode();
          return hash;
        }     
        @Override
        public boolean isSameState(Unit unit) {
            SCHLabel label=(SCHLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this));             
        }
    }
    
}
