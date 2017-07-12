package com.mynetpcb.symbol.component;


import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Reshapeable;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.ReadUnitLocal;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.line.DefaultBendingProcessorFactory;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.dialog.SymbolLoadDialog;
import com.mynetpcb.symbol.event.LineEventHandle;
import com.mynetpcb.symbol.event.SymbolEventMgr;
import com.mynetpcb.symbol.popup.SymbolPopupMenu;
import com.mynetpcb.symbol.shape.Arc;
import com.mynetpcb.symbol.shape.Arrow;
import com.mynetpcb.symbol.shape.Ellipse;
import com.mynetpcb.symbol.shape.Label;
import com.mynetpcb.symbol.shape.Line;
import com.mynetpcb.symbol.shape.Pin;
import com.mynetpcb.symbol.shape.RoundRect;
import com.mynetpcb.symbol.shape.Triangle;
import com.mynetpcb.symbol.unit.Symbol;
import com.mynetpcb.symbol.unit.SymbolMgr;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;

import java.util.Collection;

import javax.swing.JOptionPane;


public class SymbolComponent extends UnitComponent<Symbol, Shape, SymbolContainer> implements CommandListener{

    public static final int RECT_MODE = 0x01;

    public static final int ARC_MODE = 0x02;

    public static final int LINE_MODE = 0x03;
    
    public static final int ELLIPSE_MODE = 0x05;

    public static final int ARROW_MODE= 0x06;

    public static final int TRIANGLE_MODE = 0x07;

    public static final int LABEL_MODE = 0x09;
    
    public static final int DRAGHEAND_MODE=0x0A;
    
    public static final int PIN_MODE=0x0B;

    private final SymbolPopupMenu popup;

    public SymbolComponent(DialogFrame dialog) {
        super(dialog);
        this.setModel(new SymbolContainer());
        this.eventMgr = new SymbolEventMgr(this);
        this.setMode(COMPONENT_MODE);
        this.setBackground(Color.WHITE);
        
        this.loadDialogBuilder=new SymbolLoadDialog.Builder(); 
        this.popup=new SymbolPopupMenu(this);
        bendingProcessorFactory=new DefaultBendingProcessorFactory();
        setLineBendingProcessor(bendingProcessorFactory.resolve("defaultbend",null));
    }

    public void setMode(int mode) {
        super.setMode(mode);
        Shape shape = null;

        this.requestFocusInWindow(); //***for the cancel button
        switch (getMode()) {
        case RECT_MODE:
            shape=new RoundRect(0,0,50,50);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break;
        case ARROW_MODE:
            shape=new Arrow(0,0,20,-20);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape); 
            break;
        case ARC_MODE:
            shape=new Arc(0,0,50,50);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break;
        case ELLIPSE_MODE:
            shape=new Ellipse(0,0,50,50);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break; 
        case TRIANGLE_MODE:
            shape=new Triangle( Triangle.DIRECTION_EAST,0,0,20,30);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);
            break;
        case PIN_MODE:
            shape=new Pin(0,0);
            shape.setSelected(true);
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);   
            break;
        case LABEL_MODE:
            shape=new Label();
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape);  
            break;
        case ORIGIN_SHIFT_MODE:  
             getEventMgr().setEventHandle("origin",null);   
             break;
        case DRAGHEAND_MODE:
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

    public void mousePressed(MouseEvent event) {
        if (getModel().getUnit() == null) {
            getEventMgr().resetEventHandle();
        } else {
            MouseScaledEvent scaledEvent =
                new MouseScaledEvent(event, getModel().getUnit().getScalableTransformation().getInversePoint(new Point(getViewportWindow().x +
                                                                                                                       event.getX(),
                                                                                                                       getViewportWindow().y +
                                                                                                                       event.getY())));
          

            switch (getMode()) {
            case COMPONENT_MODE:
                /*
                      * 1.Coordinate origin
                      * 2.Control rect/reshape point
                      * 3.Symbol(Text) clicked
                      * 4.Module
                      */
                if(getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())){
                    getEventMgr().setEventHandle("origin",null); 
                    break;
                }
                Shape shape = getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY());
                //***is control rect clicked
                if (shape != null) {
                    if (shape instanceof Reshapeable &&
                        (((Reshapeable)shape).isReshapeRectClicked(scaledEvent.getX(), scaledEvent.getY()) != null))
                        getEventMgr().setEventHandle("reshape", shape);
                    else
                        getEventMgr().setEventHandle("resize", shape);
                } else if ((shape =
                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(), true)) !=
                           null) {
                    //***block operation
                    if (SymbolMgr.getInstance().isBlockSelected(getModel().getUnit()) && shape.isSelected())
                        getEventMgr().setEventHandle("block", shape);
                    else if (!(shape instanceof Label) && (shape instanceof Textable) &&
                             (((Textable)shape).getChipText().getClickedTexture(scaledEvent.getX(),
                                                                                scaledEvent.getY()) != null))
                        getEventMgr().setEventHandle("texture", shape);
                    //***still this could be control point
                    else if (getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY()) !=
                             null) {
                        getEventMgr().setEventHandle("resize",
                                                     getModel().getUnit().isControlRectClicked(scaledEvent.getX(),
                                                                                               scaledEvent.getY()));
                    } else {
                        getEventMgr().setEventHandle("move", shape);
                    }
                } else {
                    getEventMgr().setEventHandle("component", null);
                }

                break;
            case RECT_MODE:
                break;
            case LINE_MODE:
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof LineEventHandle)) {
                    //***handle popup when no active wire
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape = getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(), true);

                    if ((shape == null) || (!(shape instanceof Line))) {
                        shape = new Line();
                        getModel().getUnit().Add(shape);
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
                            shape = new Line();                        
                            getModel().getUnit().Add(shape);
                        }
                    }
                    getEventMgr().setEventHandle("line", shape);
                }
                break;
            case ARROW_MODE:
                break;
            case PIN_MODE:
                break;
            case ARC_MODE:
                break;
            case ELLIPSE_MODE:
                break;
            case TRIANGLE_MODE:
                break;
            case LABEL_MODE:
                break;
            case DRAGHEAND_MODE:
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
                    
                    Collection<Shape> shapes= getModel().getUnit().getSelectedShapes(false);
                    if(shapes.size()==0){
                       return true; 
                    }   
                    //***notify undo manager                    
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Rectangle r=getModel().getUnit().getShapesRect(shapes);  
                    
                    
                    SymbolMgr.getInstance().rotateBlock(shapes,
                                           AffineTransform.getRotateInstance(((e.getKeyCode() ==
                                                                               KeyEvent.VK_A) ?
                                                                              -1 :
                                                                              1) *
                                                                             Math.PI /
                                                                             2,
                                                                             r.getCenterX(),
                                                                             r.getCenterY())); 
                    SymbolMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);                     
                    SymbolMgr.getInstance().normalizePinText(shapes);  
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
                    Repaint();
                    return true;         
                    
                }
            }
            if (e.getModifiers() == ActionEvent.SHIFT_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_Q ||
                    e.getKeyCode() == KeyEvent.VK_A) {
                    Collection<Shape> shapes= getModel().getUnit().getSelectedShapes(false);
                    if(shapes.size()==0){
                       return true; 
                    } 
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Rectangle r=getModel().getUnit().getShapesRect(shapes);
                    Point p=getModel().getUnit().getGrid().positionOnGrid((int)r.getCenterX(),(int)r.getCenterY()); 
                    if(e.getKeyCode() == KeyEvent.VK_Q){
                    SymbolMgr.getInstance().mirrorBlock(getModel().getUnit(),                                           
                                            new Point(p.x - 10, p.y),
                                                              new Point(p.x + 10, p.y));
                                            
                    }else{
                    SymbolMgr.getInstance().mirrorBlock(getModel().getUnit(), 
                        new Point(p.x, p.y - 10),
                                          new Point(p.x, p.y + 10));                        
                    }
                    SymbolMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);
                    SymbolMgr.getInstance().normalizePinText(shapes);
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
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
        return popup;
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
            ZoomOut(e.getPoint());
        } else {
            ZoomIn(e.getPoint());
        }
    }
    

    @Override
    public void Reload() {
        if(getModel().getFileName()==null){
          return;   
        }
        if (!Configuration.get().isIsApplet()) {
            Command reader =
                new ReadUnitLocal(this,Configuration.get().getSymbolsRoot(),
                                                  getModel().getLibraryName(),
                                                  getModel().getCategoryName(),
                                                  getModel().getFileName(),
                                                  Symbol.class);
            CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
        } else {
            Command reader=new ReadConnector(this,new RestParameterMap.ParameterBuilder("/symbols").addURI(getModel().getLibraryName()).addURI(getModel().getCategoryName()).addURI(getModel().getFormatedFileName()).build(),Symbol.class);
            CommandExecutor.INSTANCE.addTask("ReadModule",reader);              
        }    
    }
    
    public void OnStart(Class<?> reciever) {
        DisabledGlassPane.block( this.getDialogFrame().getRootPane(),"Reloading...");     
    }

    public void OnRecive(String result,  Class reciever) {
        if(reciever==Symbol.class){         
                getModel().getUnit().Clear();             
             try{  
                getModel().Parse(result,getModel().getActiveUnitIndex());                             
                getModel().getUnit().setSelected(false); 
                getModel().registerInitialState(); 
             }catch(Exception ioe){ioe.printStackTrace(System.out);}                          
                this.componentResized(null);
                Rectangle r=this.getModel().getUnit().getBoundingRect();
                this.setScrollPosition((int)r.getCenterX(),(int)r.getCenterY());
                this.Repaint();
                this.revalidate();     
        }
    }

    public void OnFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getDialogFrame().getRootPane());       
    }

    public void OnError(String error) {
        DisabledGlassPane.unblock(getDialogFrame().getRootPane());  
        JOptionPane.showMessageDialog(getDialogFrame().getParentFrame(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE);      
    }
}
