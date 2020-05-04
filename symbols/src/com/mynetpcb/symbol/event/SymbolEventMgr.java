package com.mynetpcb.symbol.event;

import com.mynetpcb.core.capi.event.BlockEventHandle;
import com.mynetpcb.core.capi.event.CursorEventHandle;
import com.mynetpcb.core.capi.event.DragingEventHandle;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.EventMgr;
import com.mynetpcb.core.capi.event.MoveEventHandle;
import com.mynetpcb.core.capi.event.OriginEventHandle;
import com.mynetpcb.core.capi.event.ResizeEventHandle;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitEventHandle;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.component.SymbolComponent;

public class SymbolEventMgr extends EventMgr<SymbolComponent,Shape> {
  
    public SymbolEventMgr(SymbolComponent component) {
        super(component);
    }  
    @Override
    protected void Initialize(SymbolComponent component) {
        hash.put("origin",new OriginEventHandle<SymbolComponent,Shape>(component));
        hash.put("move",new MoveEventHandle<SymbolComponent,Shape>(component));
        hash.put("component", new UnitEventHandle<SymbolComponent,Shape>(component));
//        hash.put("line",new LineEventHandle(component));
        hash.put("block",new BlockEventHandle<SymbolComponent,Shape>(component,false));
        hash.put("cursor",new CursorEventHandle<SymbolComponent,Shape>(component));
        hash.put("resize",new ResizeEventHandle<SymbolComponent,Shape>(component));
//        hash.put("texture",new TextureEventHandle<SymbolComponent,Shape>(component));
//        hash.put("reshape",new ReshapeEventHandle(component));
        hash.put("dragheand",new DragingEventHandle<SymbolComponent,Shape>(component)); 
        
    }

    @Override
    protected EventHandle<SymbolComponent,Shape> getEventHandle(String eventKey,Shape target) {
        EventHandle<SymbolComponent,Shape> handle=hash.get(eventKey); 
        if(handle!=null){
           handle.setTarget(target);
            //****generate event
            if(eventKey.equals("line")||eventKey.equals("texture")||eventKey.equals("move")||(eventKey.equals("resize"))||eventKey.equals("reshape")){             
               handle.getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(target, ShapeEvent.SELECT_SHAPE));
            }              
            if(eventKey.equals("component")||eventKey.equals("origin")){
                handle.getComponent().getModel().fireUnitEvent(new UnitEvent(handle.getComponent().getModel().getUnit(), UnitEvent.SELECT_UNIT));           
            } 
           handle.attach();
        }
        return handle;
    }   
}
