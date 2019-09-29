package com.mynetpcb.core.capi.undo;


import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class Memento<U extends Unit,S extends Shape> extends AbstractMemento<U,S> implements MementoUnit<U>{

        private final List<AbstractMemento> mementoList;
        
        private int width,height;
        
        public Memento(MementoType mementoType){
          super(mementoType);        
          mementoList=new ArrayList<AbstractMemento>();
        }
        
        public boolean isSameState(Unit unit) {
            return unit.getState(getMementoType()).equals(this);
        }
        
        @Override
        public void loadStateTo(Shape shape) {
        
        }
        
        
        public void clear() {
            super.clear();
            for(AbstractMemento memento:mementoList){
              memento.clear();  
            }
            mementoList.clear();
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
            return(getUUID().equals(other.getUUID())&&
                   getMementoType().equals(other.getMementoType())&&
                   mementoList.equals(other.mementoList)&&
                   (width==other.width)&&(height==other.height)
                 );     
          
        }
        
        @Override
        public int hashCode(){
          int hash=getUUID().hashCode();
          hash+=width;
          hash+=height;
          hash+=this.mementoList.hashCode();          
          return hash;
        }
        
        @Override
        public String toString(){
          return "Unit Memento";  
        } 
        
        public void loadStateTo(U unit) {
          //Is this nesessary?
          //unit.uuid = UUID.fromString(uuid.toString());  
          unit.setSize(width,height);
          for(AbstractMemento memento:mementoList){
               Shape shape=unit.getShape(memento.getUUID());               
               shape.setState(memento);
          }
        }

        public void saveStateFrom(U unit) {
           //uuid = UUID.fromString(unit.getUUID().toString());
            
           this.width=unit.getWidth();
           this.height=unit.getHeight();
           
           for(Shape shape:(Collection<Shape>)unit.getShapes()){
              mementoList.add(shape.getState(MementoType.MOVE_MEMENTO));     
           }
           
            throw new RuntimeException("WHO IS USING UUID");
        }

}

