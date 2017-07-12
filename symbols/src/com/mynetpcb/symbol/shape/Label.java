package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.shape.LabelShape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.unit.Symbol;

import org.w3c.dom.Node;


public class Label extends LabelShape implements Externalizable{
    
    public Label(){
       super(0,0,0); 
    }
    public String toXML() {
        if(texture!=null)
          return "<label>"+texture.toXML()+"</label>\r\n";
        else
          return "";  
    }

    public void fromXML(Node node){              
            this.texture.fromXML(node);
    }
    
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }
    
    
    public static class Memento extends AbstractMemento<Symbol,Label>{
        Texture.Memento textureMemento;
        
        public Memento(MementoType mementoType){
          super(mementoType);  
          textureMemento=new FontTexture.Memento();
        }
        @Override
        public void loadStateTo(Label shape) {
          super.loadStateTo(shape);  
          textureMemento.loadStateTo(shape.texture);  
        }
        @Override
        public void saveStateFrom(Label shape){
            super.saveStateFrom(shape);
            textureMemento.saveStateFrom(shape.texture);
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

            return(this.getUUID().equals(other.getUUID()) &&
                   getMementoType()==other.getMementoType()&&
                   textureMemento.equals(other.textureMemento)
                );            
          
        }
        
        @Override
        public int hashCode(){
          int hash=getUUID().hashCode();
          hash+=getMementoType().hashCode();
          hash+=textureMemento.hashCode();
          return hash;
        }     
        
        public boolean isSameState(Symbol unit) {
            Label label=(Label)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this)); 
        }
    }
}
