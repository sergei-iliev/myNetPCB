package com.mynetpcb.circuit.component;


import com.mynetpcb.board.container.BoardContainerFactory;
import com.mynetpcb.circuit.container.CircuitContainer;
import com.mynetpcb.circuit.container.CircuitContainerFactory;
import com.mynetpcb.circuit.dialog.CircuitLoadDialog;
import com.mynetpcb.circuit.event.CircuitEventMgr;
import com.mynetpcb.circuit.event.LineEventHandle;
import com.mynetpcb.circuit.line.CircuitBendingProcessorFactory;
import com.mynetpcb.circuit.popup.CircuitPopupMenu;
import com.mynetpcb.circuit.shape.SCHBus;
import com.mynetpcb.circuit.shape.SCHBusPin;
import com.mynetpcb.circuit.shape.SCHConnector;
import com.mynetpcb.circuit.shape.SCHJunction;
import com.mynetpcb.circuit.shape.SCHLabel;
import com.mynetpcb.circuit.shape.SCHNetLabel;
import com.mynetpcb.circuit.shape.SCHNoConnector;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.shape.SCHWire;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.container.UnitContainerProducer;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.impex.XMLImportTask;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.FutureCommand;
import com.mynetpcb.core.capi.io.ReadUnitLocal;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.container.FootprintContainerFactory;
import com.mynetpcb.symbol.container.SymbolContainerFactory;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;

import java.util.Collection;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;


public class CircuitComponent extends UnitComponent<Circuit, Shape,CircuitContainer> implements CommandListener{

    public static final int WIRE_MODE = 0x01;

    public static final int BUS_MODE = 0x02;

    public static final int SYMBOL_MODE = 0x03;

    public static final int JUNCTION_MODE = 0x04;

    public static final int BUSPIN_MODE = 0x05;

    public static final int LABEL_MODE = 0x06;

    public static final int CONNECTOR_MODE = 0x07;       
    
    public static final int NOCONNECTION_MODE=0x09;   
    
    public static final int NETLABEL_MODE=0x0A;       
    
    private final CircuitPopupMenu popup;
    
    public CircuitComponent(DialogFrame dialog) {
        super(dialog);
        this.setModel(new CircuitContainer());
        this.eventMgr = new CircuitEventMgr(this);
        
        this.loadDialogBuilder= new CircuitLoadDialog.Builder(); 
        this.setParameter("snaptogrid", true);
        popup = new CircuitPopupMenu(this);
        bendingProcessorFactory=new CircuitBendingProcessorFactory();
        setLineBendingProcessor(bendingProcessorFactory.resolve("defaultbend",null));
    }

    public void setMode(int mode) {
        super.setMode(mode);
        Shape shape = null;

        this.requestFocusInWindow(); //***for the cancel button
        switch (getMode()) {
        case WIRE_MODE:
            Cursor cursor =
                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
                                                                                         "/com/mynetpcb/core/images/cursor_cross.png").getImage(),
                                                                   new Point(16,
                                                                             16),
                                                                   "Wire");
            this.setCursor(cursor);
            break;
        case BUS_MODE:
            cursor =
                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
                                                                                         "/com/mynetpcb/core/images/cursor_cross_bus.png").getImage(),
                                                                   new Point(16,
                                                                             16),
                                                                   "Bus");
            this.setCursor(cursor);
            //this.requestFocusInWindow(); //***for the cancel button
            break;
        case BUSPIN_MODE:
             SCHBusPin buspin = new SCHBusPin();        
            setContainerCursor(buspin);
            getEventMgr().setEventHandle("cursor", buspin);
            break;
        case LABEL_MODE:
            shape=new SCHLabel();
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape); 
            break;
        case NETLABEL_MODE:
                shape=new SCHNetLabel();
                setContainerCursor(shape);               
                getEventMgr().setEventHandle("cursor",shape); 
                break;        
        case JUNCTION_MODE:
            this.setCursor(Cursor.getDefaultCursor());
            shape = new SCHJunction();
            setContainerCursor(shape);
            getEventMgr().setEventHandle("cursor", shape);
            break;
        case CONNECTOR_MODE:
            this.setCursor(Cursor.getDefaultCursor());
            shape = new SCHConnector();
            //shape.Move(-1 * (int)shape.getBoundingShape().getBounds().getCenterX(), -1 * (int)shape.getBoundingShape().getBounds().getCenterY());
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape); 
            break;
        case NOCONNECTION_MODE:
                this.setCursor(Cursor.getDefaultCursor());
                shape = new SCHNoConnector();
                setContainerCursor(shape);               
                getEventMgr().setEventHandle("cursor",shape); 
                break;
        case ORIGIN_SHIFT_MODE:  
                 getEventMgr().setEventHandle("origin",null);   
                 break;        
        case DRAGHEAND_MODE:
            cursor =
                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
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
                //***is this a symbol click - this could be eighter wire,chip,junction,buss or empty(circuit)
                if(getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())){
                    getEventMgr().setEventHandle("origin",null); 
                    break;
                }
                Shape shape = getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY());
                if(shape instanceof Resizeable){
                    getEventMgr().setEventHandle("resize", shape);   
                }else{
                   shape =getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
                                              true);
                                
                   if(shape!=null){
                        //***block operation
                        if (CircuitMgr.getInstance().isBlockSelected(getModel().getUnit()) && shape.isSelected())
                             getEventMgr().setEventHandle("block", shape);
                        else if(!(shape instanceof SCHLabel)&&(shape instanceof Textable)&&( ((Textable)shape).getChipText().getClickedTexture(scaledEvent.getX(), scaledEvent.getY())!=null)) 
                             getEventMgr().setEventHandle("texture", shape);
                        else if(shape instanceof SCHSymbol)
                            getEventMgr().setEventHandle("symbol",shape);
                        else    
                             getEventMgr().setEventHandle("move",shape);
                    }else{
                    
                         getEventMgr().setEventHandle("component",null); 
                    }
                }
                    break;
            case WIRE_MODE:
                getModel().getUnit().setSelected(false);
                //***is this a new wire
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof LineEventHandle)) {
                    //***handle popup when no active wire
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape =
                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
                                                  true);
                    if ((shape == null)||(shape instanceof SCHBusPin)||(!(shape instanceof SCHWire))) {
                        shape = new SCHWire();
                        getModel().getUnit().Add(shape);
                    } 
                    else {
                        /*Click on a wire
                                    *1.Click at begin or end point - resume
                                    *2.Click in between - new Wire
                                    */
                        Trackable wire = (Trackable)shape;
                        if (wire.isEndPoint(scaledEvent.getX(),
                                            scaledEvent.getY())) {
                            //***do we need to reorder
                            wire.Reverse(scaledEvent.getX(),scaledEvent.getY());
                        } else {
                            shape = new SCHWire();                        
                            getModel().getUnit().Add(shape);
                        }
                    }
                    getEventMgr().setEventHandle("line", shape);
                }
                //****KEEP THE HANDLE between clicks, if wiring
                break;
            case BUS_MODE:
                getModel().getUnit().setSelected(false);
                //***is this a new wire
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof LineEventHandle)) {
                    //***handle popup when no active wire
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape =
                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
                                                  true);
                    if ((shape == null) ||(!(shape instanceof SCHBus))) {
                        shape = new SCHBus();
                        getModel().getUnit().Add(shape);
                    } 
                    else {
                        /*Click on a wire
                                    *1.Click at begin or end point - resume
                                    *2.Click in between - new Wire
                                    */
                        Trackable bus = (Trackable)shape;
                        if (bus.isEndPoint(scaledEvent.getX(),
                                            scaledEvent.getY())) {
                            //***do we need to reorder
                            bus.Reverse(scaledEvent.getX(),scaledEvent.getY());
                        } else {
                            shape = new SCHWire();                        
                            getModel().getUnit().Add(shape);
                        }
                    }
                    getEventMgr().setEventHandle("line", shape);
                }
                //****KEEP THE HANDLE between clicks, if wiring
                break;
            case BUSPIN_MODE:
                break;
            case JUNCTION_MODE:
                break;
            case CONNECTOR_MODE:
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
                    
                    
                    CircuitMgr.getInstance().rotateBlock(shapes,
                                           AffineTransform.getRotateInstance(((e.getKeyCode() ==
                                                                               KeyEvent.VK_A) ?
                                                                              -1 :
                                                                              1) *
                                                                             Math.PI /
                                                                             2,
                                                                             r.getCenterX(),
                                                                             r.getCenterY())); 
                    CircuitMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);                                         
                    CircuitMgr.getInstance().normalizePinText(shapes);  
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
                    if (shapes.size() == 1) {
                        getModel().getUnit().fireShapeEvent(new ShapeEvent(shapes.iterator().next(), ShapeEvent.PROPERTY_CHANGE));
                    }
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
                    CircuitMgr.getInstance().mirrorBlock(getModel().getUnit(),                                           
                                            new Point(p.x - 10, p.y),
                                                              new Point(p.x + 10, p.y));
                                            
                    }else{
                    CircuitMgr.getInstance().mirrorBlock(getModel().getUnit(),                                           
                        new Point(p.x, p.y - 10),
                                          new Point(p.x, p.y + 10));
                    }
                    CircuitMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);                    
                    CircuitMgr.getInstance().normalizePinText(shapes);
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
                    if (shapes.size() == 1) {
                        getModel().getUnit().fireShapeEvent(new ShapeEvent(shapes.iterator().next(), ShapeEvent.PROPERTY_CHANGE));
                    }
                    Repaint();
                    return true;                
                }
            }
        }
        return false;
    }
    
    @Override
    public CircuitPopupMenu getPopupMenu() {
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
    public  void Import(String targetFile){
        UnitContainerProducer unitContainerProducer=new UnitContainerProducer().withFactory("circuits", new CircuitContainerFactory());
        CommandExecutor.INSTANCE.addTask("import",
                                         new XMLImportTask(this,
                                                           unitContainerProducer,
                                                           targetFile, XMLImportTask.class));
        
        
    }
    @Override
    public void Reload(){
 
        if(getModel().getFileName()==null||getModel().getLibraryName()==null){
          return;   
        }        
        if (!Configuration.get().isIsApplet()) {                
            Command reader =
                new ReadUnitLocal(this,Configuration.get().getCircuitsRoot(),
                                                  getModel().getLibraryName(),
                                                  null,
                                                  getModel().getFileName(),
                                                   Circuit.class);
            CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
        } else {
            Command reader=new ReadConnector(this,new RestParameterMap.ParameterBuilder("/circuits").addURI(getModel().getLibraryName()).addURI(getModel().getFormatedFileName()).build(),Circuit.class);
            CommandExecutor.INSTANCE.addTask("ReadCircuit",reader);              
        }        
    }

    public void OnStart(Class<?> reciever) {
        DisabledGlassPane.block( this.getDialogFrame().getRootPane(),"Loading...");    
    }

    public void OnRecive(String result,   Class reciever) {
        if(reciever==Circuit.class){      
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
        if (receiver == XMLImportTask.class) {
            FutureCommand task = CommandExecutor.INSTANCE.getTaskByName("import");
            try{
                    CircuitContainer source = (CircuitContainer) task.get();
                    for (Circuit circuit : source.getUnits()) {
                        try {
                            Circuit copy = circuit.clone();                            
                            this.getModel().Add(copy);
                            copy.notifyListeners(ShapeEvent.ADD_SHAPE);
                        } catch (CloneNotSupportedException f) {
                            f.printStackTrace(System.out);
                        }
                    }
                    source.Clear();
                
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    public void OnError(String error) {
        DisabledGlassPane.unblock(getDialogFrame().getRootPane());  
        JOptionPane.showMessageDialog(getDialogFrame().getParentFrame(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE);      
    }

}
