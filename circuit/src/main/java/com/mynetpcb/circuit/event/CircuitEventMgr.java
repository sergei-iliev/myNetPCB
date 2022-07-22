package com.mynetpcb.circuit.event;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.core.capi.event.BlockEventHandle;
import com.mynetpcb.core.capi.event.CursorEventHandle;
import com.mynetpcb.core.capi.event.DragingEventHandle;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.EventMgr;
import com.mynetpcb.core.capi.event.MoveEventHandle;
import com.mynetpcb.core.capi.event.OriginEventHandle;
import com.mynetpcb.core.capi.event.ResizeEventHandle;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.TextureEventHandle;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitEventHandle;
import com.mynetpcb.core.capi.shape.Shape;

public class CircuitEventMgr extends EventMgr<CircuitComponent,Shape> {
    public CircuitEventMgr(CircuitComponent component) {
        super(component);
    }


    @Override
        public EventHandle<CircuitComponent,Shape> getEventHandle(String eventKey,Shape target){
          EventHandle<CircuitComponent,Shape> handle=hash.get(eventKey);  
          if(handle!=null){
             handle.setTarget(target); 
          //****generate event
             if(eventKey.equals("move")||eventKey.equals("buspining")||eventKey.equals("symbol")||eventKey.equals("buspin")||eventKey.equals("texture")||eventKey.equals("resize")||eventKey.equals("connector")){
                 handle.getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(target, ShapeEvent.SELECT_SHAPE));
             }else
               {    //***default handling - circuit event
                 handle.getComponent().getModel().fireUnitEvent(new UnitEvent(handle.getComponent().getModel().getUnit(), UnitEvent.SELECT_UNIT));               
               }                      
         
            handle.attach();
          } 
          
          return handle;
        }


    @Override
        protected void initialize(CircuitComponent component) {
         hash.put("origin",new OriginEventHandle<CircuitComponent,Shape>(component));
         hash.put("move",new MoveEventHandle<CircuitComponent,Shape>(component));
         hash.put("texture",new TextureEventHandle<CircuitComponent,Shape>(component));
         hash.put("component",new UnitEventHandle<CircuitComponent,Shape>(component));
         hash.put("block",new BlockEventHandle<CircuitComponent,Shape>(component,false));
         hash.put("wire",new WireEventHandle(component));                   
         hash.put("resize",new ResizeEventHandle<CircuitComponent,Shape>(component));
         hash.put("symbol",new SymbolEventHandle(component)); 
         hash.put("cursor",new CursorEventHandle<CircuitComponent,Shape>(component));        
         hash.put("dragheand",new DragingEventHandle<CircuitComponent,Shape>(component)); 
         hash.put("move.segment",new MoveLineSegmentHandle(component));
        }
}
