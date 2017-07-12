package com.mynetpcb.core.capi.event;


import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;

import java.util.HashMap;
import java.util.Map;

/**
 *Event manager keeps track of all available Event Handlers in memory and keeps a single one as current to respond to mouse and key events
 * @param <S>
 * @param <C>
 * @author Sergey Iliev
 */
public abstract class EventMgr<C extends UnitComponent,S extends Shape>{

    protected  final Map<String,EventHandle<C,S>> hash;    

    protected EventHandle<C,S> targetEventHandle;

    public EventMgr(C component) {
      hash=new HashMap<String,EventHandle<C,S>>();
      Initialize(component);
    }
    
    protected  abstract void Initialize(C component);
    
    public void setEventHandle(String eventKey,S target){
        resetEventHandle();
        targetEventHandle=getEventHandle(eventKey,target);
    }
 
    public void resetEventHandle(){
        if (this.targetEventHandle != null) {
            this.targetEventHandle.Detach();
        }
        this.targetEventHandle = null;                
    }
    
    public EventHandle<C, S> getTargetEventHandle() {
        return targetEventHandle;
    }
    
    protected abstract  EventHandle<C,S> getEventHandle(String eventKey,S target);
    /**
     *remove event handle when events are in restricted mode like inline editing
     * @param eventKey
     */
    public void nullifyEventHandle(String eventKey){
        hash.put(eventKey, null);
    }
}


