package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.unit.Unit;

public class UnitEvent implements Event<Unit>{
        
        private final Unit o;
        
        private final int eventType;
        
        public UnitEvent(Unit o,int eventType) {
         this.o=o;
         this.eventType=eventType;
        }

        public Unit getObject() {
            return o;
        }

        public int getEventType() {
            return eventType;
        }
    }
