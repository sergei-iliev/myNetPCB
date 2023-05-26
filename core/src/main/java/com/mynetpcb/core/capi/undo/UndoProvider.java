package com.mynetpcb.core.capi.undo;

import java.util.LinkedList;
import java.util.List;


public final class UndoProvider {
    private static final int QUEUE_DEPTH = 17;

    private List<AbstractMemento> queue;


    private int currentIndex;

    public UndoProvider() {
        queue = new LinkedList<AbstractMemento>();
        currentIndex = 0;
    }
    
    private List<AbstractMemento> getQueue(){
        return queue;
    }
    public int getQueueIndex(){
        return currentIndex;
    }
    
    public AbstractMemento Redo() {
        if (currentIndex >=(queue.size()-1)) {
            return null;
        }
        return queue.get(++currentIndex);
    }

    public AbstractMemento Undo() {
        if (currentIndex ==-1) {
            return null;
        }
        if(queue.size()==0){
           return null;  
        }
        return queue.get(currentIndex--);
    }

    public void clear() {
        for (AbstractMemento memento : queue) {
            memento.clear();
        }
        queue.clear();
        currentIndex = 0;
    }

    public void registerMemento(AbstractMemento memento) {
        //***1.Skip add if same memento as last one on the stack
        for(int i=queue.size()-1;i>0;i--){
            AbstractMemento prevMemento=queue.get(i);
                if(prevMemento.equals(memento)){ 
                  memento.clear();
                  return;  
                }              
            break;
        }   
     
        
        if (currentIndex >= QUEUE_DEPTH) {
            AbstractMemento _memento = queue.remove(0);
            _memento.clear();
            currentIndex = queue.size()-1; 
        }        
        
        if (queue.size() == 0 || currentIndex == queue.size() - 1) {
        } else {
             for (int j = currentIndex + 1; currentIndex < queue.size() - 1; ) {            
                AbstractMemento _memento = queue.remove(j);
                _memento.clear();
             }
        }

        queue.add(memento);  
        currentIndex = queue.size()-1; 
    }
    
    public String toString(){
        StringBuilder sb=new StringBuilder();
        for(AbstractMemento memento:queue){
          sb.append(memento);
          sb.append("-");
          sb.append(memento.mementoType);
          sb.append(";");  
        }
        return sb.toString();
    }

}


