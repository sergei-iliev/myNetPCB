package com.mynetpcb.circuit.component;


import com.mynetpcb.circuit.container.CircuitContainer;
import com.mynetpcb.circuit.event.CircuitEventMgr;
import com.mynetpcb.circuit.shape.SCHLabel;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * The Board Component GUI
 * @author Sergey Iliev
 */
public class CircuitComponent extends UnitComponent<Circuit, Shape, CircuitContainer> implements CommandListener {


    //private final BoardPopupMenu popup;

    public CircuitComponent(DialogFrame dialog) {
        super(dialog);
        this.setModel(new CircuitContainer());
        this.eventMgr = new CircuitEventMgr(this);
        this.setBackground(Color.WHITE);
//        this.loadDialogBuilder = new BoardLoadDialog.Builder();
//        popup = new BoardPopupMenu(this);
//        bendingProcessorFactory = new BoardBendingProcessorFactory();
//        setLineBendingProcessor(bendingProcessorFactory.resolve("defaultbend", null));
    }

    public void setMode(int mode) {
        super.setMode(mode);
        Shape shape = null;

        this.requestFocusInWindow(); //***for the cancel button
        switch (getMode()) {
//        case Mode.WIRE_MODE:
//            Cursor cursor =
//                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
//                                                                                         "/com/mynetpcb/core/images/cursor_cross.png").getImage(),
//                                                                   new Point(16,
//                                                                             16),
//                                                                   "Wire");
//            this.setCursor(cursor);
//            break;
//        case Mode.BUS_MODE:
//            cursor =
//                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
//                                                                                         "/com/mynetpcb/core/images/cursor_cross_bus.png").getImage(),
//                                                                   new Point(16,
//                                                                             16),
//                                                                   "Bus");
//            this.setCursor(cursor);
//            //this.requestFocusInWindow(); //***for the cancel button
//            break;
//        case Mode.BUSPIN_MODE:
//             SCHBusPin buspin = new SCHBusPin();        
//            setContainerCursor(buspin);
//            getEventMgr().setEventHandle("cursor", buspin);
//            break;
        case Mode.LABEL_MODE:
            shape=new SCHLabel();
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape); 
            break;
//        case Mode.NETLABEL_MODE:
//                shape=new SCHNetLabel();
//                setContainerCursor(shape);               
//                getEventMgr().setEventHandle("cursor",shape); 
//                break;        
//        case Mode.JUNCTION_MODE:
//            this.setCursor(Cursor.getDefaultCursor());
//            shape = new SCHJunction();
//            setContainerCursor(shape);
//            getEventMgr().setEventHandle("cursor", shape);
//            break;
//        case Mode.CONNECTOR_MODE:
//            this.setCursor(Cursor.getDefaultCursor());
//            shape = new SCHConnector();
//            //shape.Move(-1 * (int)shape.getBoundingShape().getBounds().getCenterX(), -1 * (int)shape.getBoundingShape().getBounds().getCenterY());
//            setContainerCursor(shape);               
//            getEventMgr().setEventHandle("cursor",shape); 
//            break;
//        case Mode.NOCONNECTION_MODE:
//                this.setCursor(Cursor.getDefaultCursor());
//                shape = new SCHNoConnector();
//                setContainerCursor(shape);               
//                getEventMgr().setEventHandle("cursor",shape); 
//                break;
        case Mode.ORIGIN_SHIFT_MODE:  
                 getEventMgr().setEventHandle("origin",null);   
                 break;        
        case Mode.DRAGHEAND_MODE:
//            cursor =
//                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
//                                                                                         "/com/mynetpcb/core/images/dragopen.png").getImage(),
//                                                                   new Point(16,
//                                                                             16),
//                                                                   "DragHeandOpen");
//            this.setCursor(cursor);
            break;
        default:
            this.setCursor(Cursor.getDefaultCursor());
            this.Repaint();
        }

    }
    public void mousePressed(MouseEvent event) {
            if (getModel().getUnit() == null) {
                getEventMgr().resetEventHandle();
            } else {
                MouseScaledEvent scaledEvent =new MouseScaledEvent(event,getModel().getUnit().getScalableTransformation().getInversePoint(new java.awt.Point((int)getViewportWindow().getX()+event.getX(),(int)getViewportWindow().getY()+event.getY())));

    
            switch (getMode()) {
            case Mode.COMPONENT_MODE:
                //***is this a symbol click - this could be eighter wire,chip,junction,buss or empty(circuit)
                if(getModel().getUnit().getCoordinateSystem()!=null){
                if (getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())) {
                    getEventMgr().setEventHandle("origin", null);
                    break;
                }
                }
                Shape shape = getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY());
                if(shape !=null){
                    getEventMgr().setEventHandle("resize", shape);   
                }else{
                   shape =getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
                                              true);
                               
                   if(shape!=null){
                        //***block operation
                        if (CircuitMgr.getInstance().isBlockSelected(getModel().getUnit()) && shape.isSelected())
                             getEventMgr().setEventHandle("block", shape);
                        else if(!(shape instanceof SCHLabel)&&(shape instanceof Textable)&&( ((Textable)shape).getClickedTexture(scaledEvent.getX(), scaledEvent.getY())!=null)) 
                             getEventMgr().setEventHandle("texture", shape);
                        else if(shape instanceof SCHSymbol)
                            //getEventMgr().setEventHandle("symbol",shape);
                            getEventMgr().setEventHandle("move",shape);
                        else    
                             getEventMgr().setEventHandle("move",shape);
                    }else{                    
                         getEventMgr().setEventHandle("component",null); 
                    }
                }
                    break;
//            case Mode.WIRE_MODE:
//                getModel().getUnit().setSelected(false);
//                //***is this a new wire
//                if ((getEventMgr().getTargetEventHandle() == null) ||
//                    !(getEventMgr().getTargetEventHandle() instanceof LineEventHandle)) {
//                    //***handle popup when no active wire
//                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
//                        return; //***right button click
//                    }
//                    shape =
//                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
//                                                  true);
//                    if ((shape == null)||(shape instanceof SCHBusPin)||(!(shape instanceof SCHWire))) {
//                        shape = new SCHWire();
//                        getModel().getUnit().Add(shape);
//                    } 
//                    else {
//                        /*Click on a wire
//                                    *1.Click at begin or end point - resume
//                                    *2.Click in between - new Wire
//                                    */
//                        Trackable wire = (Trackable)shape;
//                        if (wire.isEndPoint(scaledEvent.getX(),
//                                            scaledEvent.getY())) {
//                            //***do we need to reorder
//                            wire.Reverse(scaledEvent.getX(),scaledEvent.getY());
//                        } else {
//                            shape = new SCHWire();                        
//                            getModel().getUnit().Add(shape);
//                        }
//                    }
//                    getEventMgr().setEventHandle("line", shape);
//                }
//                //****KEEP THE HANDLE between clicks, if wiring
//                break;
//            case Mode.BUS_MODE:
//                getModel().getUnit().setSelected(false);
//                //***is this a new wire
//                if ((getEventMgr().getTargetEventHandle() == null) ||
//                    !(getEventMgr().getTargetEventHandle() instanceof LineEventHandle)) {
//                    //***handle popup when no active wire
//                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
//                        return; //***right button click
//                    }
//                    shape =
//                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
//                                                  true);
//                    if ((shape == null) ||(!(shape instanceof SCHBus))) {
//                        shape = new SCHBus();
//                        getModel().getUnit().Add(shape);
//                    } 
//                    else {
//                        /*Click on a wire
//                                    *1.Click at begin or end point - resume
//                                    *2.Click in between - new Wire
//                                    */
//                        Trackable bus = (Trackable)shape;
//                        if (bus.isEndPoint(scaledEvent.getX(),
//                                            scaledEvent.getY())) {
//                            //***do we need to reorder
//                            bus.Reverse(scaledEvent.getX(),scaledEvent.getY());
//                        } else {
//                            shape = new SCHWire();                        
//                            getModel().getUnit().Add(shape);
//                        }
//                    }
//                    getEventMgr().setEventHandle("line", shape);
//                }
//                //****KEEP THE HANDLE between clicks, if wiring
//                break;
//            case Mode.DRAGHEAND_MODE:
//                getEventMgr().setEventHandle("dragheand", null);
//                break;
           }
        }

        super.mousePressed(event);
    }    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(this.getModel().getUnit()==null)
            return;
        //could be a horizontal or vertical scrall rederect
        if(this.isMouseWheelRederect(e)){
            return;
        }
        if (e.getWheelRotation() > 0) {
            zoomOut(e.getPoint());
        } else {
            zoomIn(e.getPoint());
        }
    }
    @Override
    public void _import(String string) {
        // TODO Implement this method
    }

    @Override
    public void OnStart(Class<?> c) {
        // TODO Implement this method
    }

    @Override
    public void OnRecive(String string, Class<?> c) {
        // TODO Implement this method

    }

    @Override
    public void OnFinish(Class<?> c) {
        // TODO Implement this method
    }

    @Override
    public void OnError(String string) {
        // TODO Implement this method
    }
    @Override
    public void reload() {
        
    }
}
