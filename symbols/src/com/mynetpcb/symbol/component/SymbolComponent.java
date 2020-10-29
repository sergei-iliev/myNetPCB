package com.mynetpcb.symbol.component;

import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.LineEventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.line.DefaultBendingProcessorFactory;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.event.SymbolEventMgr;
import com.mynetpcb.symbol.popup.SymbolPopupMenu;
import com.mynetpcb.symbol.shape.Arc;
import com.mynetpcb.symbol.shape.ArrowLine;
import com.mynetpcb.symbol.shape.Ellipse;
import com.mynetpcb.symbol.shape.FontLabel;
import com.mynetpcb.symbol.shape.Line;
import com.mynetpcb.symbol.shape.Pin;
import com.mynetpcb.symbol.shape.RoundRect;
import com.mynetpcb.symbol.shape.Triangle;
import com.mynetpcb.symbol.unit.Symbol;
import com.mynetpcb.symbol.unit.SymbolMgr;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import java.util.Collection;

public class SymbolComponent extends UnitComponent<Symbol, Shape, SymbolContainer> implements CommandListener{
    
    private final SymbolPopupMenu popup;
    
    public SymbolComponent(DialogFrame dialogFrame) {
        super(dialogFrame);
        this.setModel(new SymbolContainer());
        this.eventMgr=new SymbolEventMgr(this);
        this.setMode(Mode.COMPONENT_MODE);
        this.setBackground(Color.WHITE);
        //this.loadDialogBuilder=new FootprintLoadDialog.Builder();
        this.popup=new SymbolPopupMenu(this);
        bendingProcessorFactory=new DefaultBendingProcessorFactory();
        setLineBendingProcessor(bendingProcessorFactory.resolve("defaultbend",null));
    }
    public void setMode(int mode) {
        super.setMode(mode);
        Shape shape = null;

        this.requestFocusInWindow(); //***for the cancel button
        switch (getMode()) {
        case Mode.RECT_MODE:
            shape=new RoundRect(1);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break;
        case Mode.ARROW_MODE:
            shape=new ArrowLine(1);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape); 
            break;
        case Mode.ARC_MODE:
            shape=new Arc(1);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break;
        case Mode.ELLIPSE_MODE:
            shape=new Ellipse(1);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break; 
        case Mode.TRIANGLE_MODE:
            shape=new Triangle(1);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);
            break;
        case Mode.PIN_MODE:
            shape=new Pin();
            shape.setSelected(true);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break;
        case Mode.LABEL_MODE:
            shape=new FontLabel();
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);  
            break;
        case Mode.ORIGIN_SHIFT_MODE:  
             getEventMgr().setEventHandle("origin",null);   
             break;
        case Mode.DRAGHEAND_MODE:
                Cursor cursor =
                        getToolkit().getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame() ,
                                                                                             "/com/mynetpcb/core/images/dragopen.png").getImage(),
                                                                       new Point(16,
                                                                                 16),
                                                                       "DragHeandOpen");
                this.setCursor(cursor);
                break;
        default:
                this.setCursor(Cursor.getDefaultCursor());
                this.Repaint();
        }
    }
    @Override
    public void mousePressed(MouseEvent event) {
        if (getModel().getUnit() == null) {
            getEventMgr().resetEventHandle();
        } else {
            MouseScaledEvent scaledEvent =
                new MouseScaledEvent(event, getModel().getUnit().getScalableTransformation().getInversePoint(new Point((int)getViewportWindow().getX() +
                                                                                                                       event.getX(),
                                                                                                                       (int)getViewportWindow().getY() +
                                                                                                                       event.getY())));
          

            switch (getMode()) {
            case Mode.COMPONENT_MODE:
                /*
                      * 1.Coordinate origin
                      * 2.Control rect/reshape point
                      * 3.Symbol(Text) clicked
                      * 4.Module
                      */
                if(this.getModel().getUnit().getCoordinateSystem()!=null){ 
                 if(getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())){
                    getEventMgr().setEventHandle("origin",null); 
                    break;
                 }
                }                                
                
                Shape shape = getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY());
                //***is control rect clicked
                if (shape != null) {
                    if(shape instanceof Arc){
                        if(((Arc)shape).isStartAnglePointClicked(scaledEvent.getX() , scaledEvent.getY())){ 
                            this.getEventMgr().setEventHandle("arc.start.angle",shape);                    
                        }else if(((Arc)shape).isExtendAnglePointClicked(scaledEvent.getX(), scaledEvent.getY() )){
                            this.getEventMgr().setEventHandle("arc.extend.angle",shape);                                              
                        }else{
                             this.getEventMgr().setEventHandle("resize",shape);    
                        }                    
                    }else{
                        getEventMgr().setEventHandle("resize", shape);
                    }
                } else if ((shape =
                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(), true)) !=
                           null) {
                    //***block operation
                    if (SymbolMgr.getInstance().isBlockSelected(getModel().getUnit()) && shape.isSelected())
                         getEventMgr().setEventHandle("block", shape);
                    else if((shape instanceof Textable)&&( ((Textable)shape).getClickedTexture(scaledEvent.getX(), scaledEvent.getY())!=null)) 
                         getEventMgr().setEventHandle("texture", shape);
                    else
                         getEventMgr().setEventHandle("move",shape);
                } else {
                    getEventMgr().setEventHandle("component", null);
                }

                break;
            case Mode.LINE_MODE:
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof LineEventHandle)) {
                    //***handle popup when no active wire
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape = getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(), true);

                    if ((shape == null) || (!(shape instanceof Line))) {
                        shape = new Line(1);
                        getModel().getUnit().add(shape);
                    }else {
                        /*Click on a line
                                    *1.Click at begin or end point - resume
                                    *2.Click in between - new Wire
                                    */
                        Trackable line = (Trackable)shape;
                        if (line.isEndPoint(scaledEvent.getX(),
                                            scaledEvent.getY())) {
                            this.resumeLine(line,"line", scaledEvent.getX(), scaledEvent.getY());
                            return;
                        } else {
                            shape = new Line(1);                        
                            getModel().getUnit().add(shape);
                        }
                    }
                    getEventMgr().setEventHandle("line", shape);
                }
                break;
            case Mode.ARROW_MODE:
                break;
            case Mode.PIN_MODE:
                break;
            case Mode.ARC_MODE:
                break;
            case Mode.ELLIPSE_MODE:
                break;
            case Mode.TRIANGLE_MODE:
                break;
            case Mode.LABEL_MODE:
                break;
            case Mode.DRAGHEAND_MODE:
                getEventMgr().setEventHandle("dragheand", null);
                break;
            }


        }
        super.mousePressed(event);
    }
    @Override
    protected boolean defaultKeyPress(KeyEvent e) {
        if (super.defaultKeyPress(e)) {
            return true;
        }
        if (e.getModifiersEx() != 0) {
            if (e.getModifiers() == ActionEvent.CTRL_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_Q ||
                    e.getKeyCode() == KeyEvent.VK_A) {
                    
                    Collection<Shape> shapes= getModel().getUnit().getSelectedShapes();
                    if(shapes.size()==0){
                       return true; 
                    }   
                    //***notify undo manager                    
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Box r=getModel().getUnit().getShapesRect(shapes);  
                    com.mynetpcb.d2.shapes.Point center=r.getCenter();

                    SymbolMgr.getInstance().rotateBlock(shapes,
                                           ((e.getKeyCode() ==KeyEvent.VK_A) ?
                                                                              -1 :
                                                                              1) *
                                                                             90,
                                                                             center); 
                    SymbolMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);                     

                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
                    Repaint();
                    return true;       
                    
                }
            }
            if (e.getModifiers() == ActionEvent.SHIFT_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_Q ||
                    e.getKeyCode() == KeyEvent.VK_A) {
                    Collection<Shape> shapes= getModel().getUnit().getSelectedShapes();
                    if(shapes.size()==0){
                       return true; 
                    } 
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Box r=getModel().getUnit().getShapesRect(shapes);
                    com.mynetpcb.d2.shapes.Point center=r.getCenter();
                    com.mynetpcb.d2.shapes.Point p=getModel().getUnit().getGrid().positionOnGrid(center); 
                    if(e.getKeyCode() == KeyEvent.VK_Q){
                        SymbolMgr.getInstance().mirrorBlock(getModel().getUnit().getSelectedShapes(),new com.mynetpcb.d2.shapes.Line(new com.mynetpcb.d2.shapes.Point(p.x - 10, p.y),
                                                              new com.mynetpcb.d2.shapes.Point(p.x + 10, p.y)));
                                           
                    }else{
                        SymbolMgr.getInstance().mirrorBlock(getModel().getUnit().getSelectedShapes(),new com.mynetpcb.d2.shapes.Line(
                                new com.mynetpcb.d2.shapes.Point(p.x, p.y - 10),
                                          new com.mynetpcb.d2.shapes.Point(p.x, p.y + 10)));
                    }
                    
                    SymbolMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
                    Repaint();
                    return true;                                     
                }
            }
        }
        //***single CTRL press for the mouse menu
        if (e.getModifiers() == ActionEvent.CTRL_MASK) {
            return true;
        }
        return false;
    }    
    @Override
    public SymbolPopupMenu getPopupMenu() {        
        return this.popup;
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
    public void reload() {
        // TODO Implement this method
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
}
