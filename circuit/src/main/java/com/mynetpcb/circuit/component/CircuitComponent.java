package com.mynetpcb.circuit.component;


import com.mynetpcb.circuit.container.CircuitContainer;
import com.mynetpcb.circuit.dialog.CircuitLoadDialog;
import com.mynetpcb.circuit.event.CircuitEventMgr;
import com.mynetpcb.circuit.event.WireEventHandle;
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
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.impex.XMLImportTask;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.FutureCommand;
import com.mynetpcb.core.capi.io.ReadUnitLocal;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

/**
 * The Board Component GUI
 * @author Sergey Iliev
 */
public class CircuitComponent extends UnitComponent<Circuit, Shape, CircuitContainer> implements CommandListener {


    private final CircuitPopupMenu popup;

    public CircuitComponent(DialogFrame dialog) {
        super(dialog);
        this.setModel(new CircuitContainer());
        this.eventMgr = new CircuitEventMgr(this);
        this.setBackground(Color.WHITE);
        this.setParameter("snaptogrid", true);
        this.loadDialogBuilder = new CircuitLoadDialog.Builder();
        popup = new CircuitPopupMenu(this);
        bendingProcessorFactory = new CircuitBendingProcessorFactory();
        setLineBendingProcessor(bendingProcessorFactory.resolve("vhbend", null));
        getLineBendingProcessor().setGridAlignable(true);
    }

    public void setMode(int mode) {
        super.setMode(mode);
        Shape shape = null;

        this.requestFocusInWindow(); //***for the cancel button
        switch (getMode()) {
        case Mode.WIRE_MODE:
            Cursor cursor =
                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
                                                                                         "images/cursor_cross.png").getImage(),
                                                                   new java.awt.Point(16,
                                                                             16),
                                                                   "Wire");
            this.setCursor(cursor);
            break;
        case Mode.NETLABEL_MODE:
            shape=new SCHNetLabel();
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape); 
            break;         
        case Mode.BUS_MODE:
            cursor =
                    Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
                                                                                         "images/cursor_cross_bus.png").getImage(),
                                                                   new java.awt.Point(16,
                                                                             16),
                                                                   "Bus");
            this.setCursor(cursor);
            //this.requestFocusInWindow(); //***for the cancel button
            break;
        case Mode.BUSPIN_MODE:
            SCHBusPin buspin = new SCHBusPin();        
            setContainerCursor(buspin);
            getEventMgr().setEventHandle("cursor", buspin);
            break;
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
        case Mode.JUNCTION_MODE:
            shape = new SCHJunction();
            setContainerCursor(shape);
            getEventMgr().setEventHandle("cursor", shape);
            break;
        case Mode.CONNECTOR_MODE:
            shape = new SCHConnector();            
            setContainerCursor(shape);               
            getEventMgr().setEventHandle("cursor",shape); 
            break;
        case Mode.NOCONNECTOR_MODE:
                this.setCursor(Cursor.getDefaultCursor());
                shape = new SCHNoConnector();
                setContainerCursor(shape);               
                getEventMgr().setEventHandle("cursor",shape); 
                break;
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
                MouseScaledEvent scaledEvent =new MouseScaledEvent(event,getModel().getUnit().getScalableTransformation().getInversePoint(getViewportWindow().getX()+event.getX(),getViewportWindow().getY()+event.getY()));

    
            switch (getMode()) {
            case Mode.COMPONENT_MODE:
                //***is this a symbol click - this could be eighter wire,chip,junction,buss or empty(circuit)
                if(getModel().getUnit().getCoordinateSystem()!=null){
                if (getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())) {
                    getEventMgr().setEventHandle("origin", null);
                    break;
                }
                }
                Shape shape = getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY(),getViewportWindow());
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
                            getEventMgr().setEventHandle("symbol",shape);  
				   		else if(shape instanceof SCHWire) {
				   			if(((SCHWire)shape).isSegmentClicked(scaledEvent.getPoint(),getViewportWindow())) 
				   			 if(((SCHWire)shape).isSingleSegment()) {
				   				this.getEventMgr().setEventHandle("move",shape);	
				   			}else {
				   				this.getEventMgr().setEventHandle("move.segment",shape);
				   			}				   		  
				   		}else    
                             getEventMgr().setEventHandle("move",shape);
                    }else{                    
                         getEventMgr().setEventHandle("component",null); 
                    }
                }
                    break;
            case Mode.WIRE_MODE:
                //getModel().getUnit().setSelected(false);
                //***is this a new wire
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof WireEventHandle)) {
                    //***handle popup when no active wire
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape =
                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
                                                  true);
                    if ((shape == null)||(!(shape instanceof SCHWire))) {
                        shape = new SCHWire();
                        getModel().getUnit().add(shape);
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
                        //    wire.reverse(scaledEvent.getX(),scaledEvent.getY());
                        } else {
                            shape = new SCHWire();                        
                            getModel().getUnit().add(shape);
                        }
                    }
                    getEventMgr().setEventHandle("wire", shape);
                }
                //****KEEP THE HANDLE between clicks, if wiring
                break;
            case Mode.BUS_MODE:
                getModel().getUnit().setSelected(false);
                //***is this a new wire
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof WireEventHandle)) {
                    //***handle popup when no active wire
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape =
                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(),
                                                  true);
                    if ((shape == null) ||(!(shape instanceof SCHBus))) {
                        shape = new SCHBus();
                        getModel().getUnit().add(shape);
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
                            //bus.reverse(scaledEvent.getX(),scaledEvent.getY());
                        } else {
                            shape = new SCHBus();                        
                            getModel().getUnit().add(shape);
                        }
                    }
                    getEventMgr().setEventHandle("wire", shape);
                }
                //****KEEP THE HANDLE between clicks, if wiring
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
                if (e.getKeyCode() == KeyEvent.VK_Q ||e.getKeyCode() == KeyEvent.VK_A) {
                    
                    Collection<Shape> shapes= getModel().getUnit().getSelectedShapes();
                    if(shapes.size()==0){
                       return true; 
                    }   
                    //***notify undo manager                    
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Box r = getModel().getUnit().getShapesRect(shapes);
                    Point center=r.getCenter();
                    
                    CircuitMgr.getInstance().rotateBlock(shapes,
                                           ((e.getKeyCode() ==KeyEvent.VK_A) ?
                                                                              1 :
                                                                              -1) *
                                                                             90,
                                                                             center); 
                    CircuitMgr.getInstance().alignBlock(getModel().getUnit().getGrid(), shapes);

                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size() > 1 ?
                                                         new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes) :
                                                         shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
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
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size() > 1 ?
                                                         new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes) :
                                                         shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Box r = getModel().getUnit().getShapesRect(shapes);
                    Point center=r.getCenter();
                    Point p=getModel().getUnit().getGrid().positionOnGrid(center); 
                    
                    if(e.getKeyCode() == KeyEvent.VK_Q){
                        CircuitMgr.getInstance().mirrorBlock(getModel().getUnit().getSelectedShapes(),new com.mynetpcb.d2.shapes.Line(new Point(p.x - 10, p.y),
                                                              new Point(p.x + 10, p.y)));
                                           
                    }else{
                        CircuitMgr.getInstance().mirrorBlock(getModel().getUnit().getSelectedShapes(),new com.mynetpcb.d2.shapes.Line(
                                new Point(p.x, p.y - 10),
                                          new Point(p.x, p.y + 10)));
                    }
                    CircuitMgr.getInstance().alignBlock(getModel().getUnit().getGrid(), shapes);
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size() > 1 ?
                                                         new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes) :
                                                         shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Repaint();
                    return true;                
                }
            }
        }
        return false;
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
    public CircuitPopupMenu getPopupMenu() {        
        return this.popup;
    }
    
    @Override
    public void _import(String string) {
        // TODO Implement this method
    }

    @Override
    public void onStart(Class<?> receiver) {
    	DisabledGlassPane.block( this.getDialogFrame().getRootPane(),"Loading...");   
    }

    @Override
    public void onRecive(String result, Class<?> receiver) {
        if(receiver==Circuit.class){      
            getModel().getUnit().clear();             
         try{  
            getModel().parse(result,getModel().getActiveUnitIndex());                             
            getModel().getUnit().setSelected(false); 
            getModel().registerInitialState(); 
         }catch(Exception ioe){ioe.printStackTrace(System.out);} 
            this.componentResized(null);
            this.Repaint();
            this.revalidate();    
    }

    }

    @Override
    public void onFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getDialogFrame().getRootPane());      
        if (receiver == XMLImportTask.class) {
            FutureCommand task = CommandExecutor.INSTANCE.getTaskByName("import");
            try{
                    CircuitContainer source = (CircuitContainer) task.get();
                    for (Circuit circuit : source.getUnits()) {
                        try {
                            Circuit copy = circuit.clone();                            
                            this.getModel().add(copy);
                            copy.notifyListeners(ShapeEvent.ADD_SHAPE);
                        } catch (CloneNotSupportedException f) {
                            f.printStackTrace(System.out);
                        }
                    }
                    source.clear();
                
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void onError(String error) {
        DisabledGlassPane.unblock(getDialogFrame().getRootPane());  
        JOptionPane.showMessageDialog(getDialogFrame().getParentFrame(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }
    @Override
    public void reload() {
        if(getModel().getFileName()==null||getModel().getLibraryName()==null){
            return;   
        }        
               
        Command reader =new ReadUnitLocal(this,Configuration.get().getCircuitsRoot(),
                                                    getModel().getLibraryName(),
                                                    null,
                                                    getModel().getFileName(),
                                                     Circuit.class);
        CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
        
    }
}
