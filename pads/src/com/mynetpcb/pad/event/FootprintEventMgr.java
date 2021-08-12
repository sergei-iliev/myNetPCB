package com.mynetpcb.pad.event;

import com.mynetpcb.core.capi.event.BlockEventHandle;
import com.mynetpcb.core.capi.event.CursorEventHandle;
import com.mynetpcb.core.capi.event.DragingEventHandle;
import com.mynetpcb.core.capi.event.EventHandle;
import com.mynetpcb.core.capi.event.EventMgr;
import com.mynetpcb.core.capi.event.LineEventHandle;
import com.mynetpcb.core.capi.event.MeasureEventHandle;
import com.mynetpcb.core.capi.event.MoveEventHandle;
import com.mynetpcb.core.capi.event.OriginEventHandle;
import com.mynetpcb.core.capi.event.ResizeEventHandle;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.TextureEventHandle;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitEventHandle;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.pad.component.FootprintComponent;


public class FootprintEventMgr extends EventMgr<FootprintComponent,Shape> {
    public FootprintEventMgr(FootprintComponent component) {
        super(component);
    }

    @Override
    protected void initialize(FootprintComponent component) {
        hash.put("arc.start.angle",new ArcStartAngleEventHandle<FootprintComponent,Shape>(component));
        hash.put("arc.extend.angle",new ArcExtendAngleEventHandler<FootprintComponent,Shape>(component));
        hash.put("arc.mid.point",new ArcMidPointEventHandle<FootprintComponent,Shape>(component));
        hash.put("move",new MoveEventHandle<FootprintComponent,Shape>(component));
        hash.put("component", new UnitEventHandle<FootprintComponent,Shape>(component));
        hash.put("line",new LineEventHandle<FootprintComponent,Shape>(component));
        hash.put("block",new BlockEventHandle<FootprintComponent,Shape>(component,false));
        hash.put("cursor",new CursorEventHandle<FootprintComponent,Shape>(component));
        hash.put("arc.resize",new ArcResizeEventHandle<FootprintComponent,Shape>(component));
        hash.put("resize",new ResizeEventHandle<FootprintComponent,Shape>(component));
        hash.put("texture",new TextureEventHandle<FootprintComponent,Shape>(component));
        hash.put("dragheand",new DragingEventHandle<FootprintComponent,Shape>(component)); 
        hash.put("origin",new OriginEventHandle<FootprintComponent,Shape>(component));
        hash.put("measure",new MeasureEventHandle<FootprintComponent,Shape>(component));
        hash.put("solidregion",new SolidRegionEventHandle<FootprintComponent,Shape>(component));
    }

    @Override
    protected EventHandle<FootprintComponent,Shape> getEventHandle(String eventKey, Shape target) {
        EventHandle<FootprintComponent,Shape> handle=hash.get(eventKey); 
        if(handle!=null){
           handle.setTarget(target);
            //****generate event
            if(eventKey.equals("line")||eventKey.equals("texture")||eventKey.equals("move")||(eventKey.equals("resize"))||eventKey.equals("solidregion")){             
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

