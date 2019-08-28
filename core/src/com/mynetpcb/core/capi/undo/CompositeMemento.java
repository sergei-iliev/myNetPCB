package com.mynetpcb.core.capi.undo;


import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public  class CompositeMemento<U extends Unit,S extends Shape> extends AbstractMemento<U,S> implements MementoContainer<CompositeMemento,S>{
      
      private final List<AbstractMemento> mementoList;
        
      public CompositeMemento(MementoType mementoType){
            super(mementoType);
            mementoList=new LinkedList<AbstractMemento>();  
      } 
      @Override  
      public CompositeMemento Add(Collection<S> shapes){
        for(S shape:shapes){
            mementoList.add(shape.getState(mementoType));  
        } 
        return this;  
      }
          
      public void loadStateTo(S shape) {
           
      }      
      
      public List<AbstractMemento> getMementoList(){
         return  mementoList;
      }
      
      @Override
      public void Clear() {
            super.Clear();
            for(AbstractMemento memento:mementoList){
              memento.Clear();  
            }
            mementoList.clear();            
      }
      @Override
      public boolean equals(Object obj){
            if(this==obj){
              return true;  
            }
            if(!(obj instanceof CompositeMemento)){
              return false;  
            }
            
            CompositeMemento other=(CompositeMemento)obj;
            
            if(!other.getMementoType().equals(this.getMementoType()))
              return false;
            
            return this.mementoList.equals(other.mementoList);
          
      }
      
      @Override
      public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("Composite Memento[");
          for(AbstractMemento memento:mementoList){
            sb.append(memento);                
          }
        sb.append("]");
        return sb.toString();  
      }
      
      @Override
      public int hashCode(){
            int hash=31; 
            hash+=this.getMementoType().hashCode();
            hash+=mementoList.hashCode();     
            return hash;  
      }        
      public boolean isSameState(U unit) {
            for(AbstractMemento memento:mementoList){
                if(!memento.isSameState(unit)){
                  return false;  
                }
            }
          return true;  
      }

}

