package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.container.UnitContainer;


public class ContainerEvent implements Event<UnitContainer> {
    
    private final UnitContainer unitContainer;
    private final int eventType;
    
    public ContainerEvent(UnitContainer unitContainer,int eventType) {
      this.unitContainer=unitContainer;
      this.eventType=eventType;
    }

    @Override
    public UnitContainer getObject() {
        return unitContainer;
    }
    
    @Override
    public int getEventType() {
        return eventType;
    }
}


