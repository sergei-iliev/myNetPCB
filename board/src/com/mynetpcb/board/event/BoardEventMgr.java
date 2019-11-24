package com.mynetpcb.board.event;


import com.mynetpcb.board.component.BoardComponent;
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
import com.mynetpcb.pad.event.ArcExtendAngleEventHandler;
import com.mynetpcb.pad.event.ArcMidPointEventHandle;
import com.mynetpcb.pad.event.ArcStartAngleEventHandle;

public class BoardEventMgr extends EventMgr<BoardComponent,Shape> {
    public BoardEventMgr(BoardComponent component) {
        super(component);
    }
    
    @Override
    public EventHandle<BoardComponent,Shape> getEventHandle(String eventKey,Shape target){
      EventHandle<BoardComponent,Shape> handle=hash.get(eventKey);  
      if(handle!=null){
         handle.setTarget(target); 
      //****generate event
         if(eventKey.equals("move")||eventKey.equals("copperarea")||eventKey.equals("track")||eventKey.equals("line")||eventKey.equals("texture")||eventKey.equals("symbol")||eventKey.equals("resize")){
             handle.getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(target, ShapeEvent.SELECT_SHAPE));
         }
         if(eventKey.equals("component")||eventKey.equals("origin")){        
             handle.getComponent().getModel().fireUnitEvent(new UnitEvent(handle.getComponent().getModel().getUnit(), UnitEvent.SELECT_UNIT));               
         }                      
     
        handle.attach();
      } 
      
      return handle;
    }


@Override
    protected void Initialize(BoardComponent component) {
     hash.put("arc.start.angle",new ArcStartAngleEventHandle<BoardComponent,Shape>(component));
     hash.put("arc.extend.angle",new ArcExtendAngleEventHandler<BoardComponent,Shape>(component));
     hash.put("arc.mid.point",new ArcMidPointEventHandle<BoardComponent,Shape>(component));
     hash.put("origin",new OriginEventHandle<BoardComponent,Shape>(component));
     hash.put("texture",new TextureEventHandle<BoardComponent,Shape>(component));
     hash.put("move",new MoveEventHandle<BoardComponent,Shape>(component));
     hash.put("component",new UnitEventHandle<BoardComponent,Shape>(component));
     hash.put("block",new BlockEventHandle<BoardComponent,Shape>(component,true));
     hash.put("line",new LineEventHandle<BoardComponent,Shape>(component));     
     hash.put("track",new TrackEventHandle(component));  
     hash.put("copperarea",new CopperAreaEventHandle(component));
     hash.put("resize",new ResizeEventHandle<BoardComponent,Shape>(component));
     hash.put("symbol",new FootprintEventHandle(component)); 
     hash.put("cursor",new CursorEventHandle<BoardComponent,Shape>(component));        
     hash.put("dragheand",new DragingEventHandle<BoardComponent,Shape>(component)); 
     hash.put("measure",new MeasureEventHandle<BoardComponent,Shape>(component));
    }
}
