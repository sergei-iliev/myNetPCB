package com.mynetpcb.symbol.component;

import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.LineEventHandle;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.impex.XMLImportTask;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.FutureCommand;
import com.mynetpcb.core.capi.io.ReadUnitLocal;
import com.mynetpcb.core.capi.line.DefaultBendingProcessorFactory;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.dialog.SymbolLoadDialog;
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
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

public class SymbolComponent extends UnitComponent<Symbol, Shape, SymbolContainer> implements CommandListener{
    
    private final SymbolPopupMenu popup;
    
    public SymbolComponent(DialogFrame dialogFrame) {
        super(dialogFrame);
        this.setModel(new SymbolContainer());
        this.eventMgr=new SymbolEventMgr(this);
        this.setMode(Mode.COMPONENT_MODE);
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
        case Mode.RECT_MODE:
            shape=new RoundRect();
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
                                                                                             "images/dragopen.png").getImage(),
                                                                       new java.awt.Point(16,
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
                new MouseScaledEvent(event, getModel().getUnit().getScalableTransformation().getInversePoint(getViewportWindow().getX() +
                                                                                                                       event.getX(),
                                                                                                                       getViewportWindow().getY() +
                                                                                                                       event.getY()));
          

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
                
                Shape shape = getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY(),getViewportWindow());
                //***is control rect clicked
                if (shape != null) {
                    if(shape instanceof Arc){
                    	Point pt=((Arc) shape).isControlRectClicked(scaledEvent.getX() , scaledEvent.getY(),getViewportWindow());
                        if(pt.equals(((Arc)shape).getShape().getStart())){ 
                        	this.getEventMgr().setEventHandle("arc.start.angle",shape);                    
                        }else if(pt.equals(((Arc)shape).getShape().getEnd())){
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
                                                                              1 :
                                                                              -1) *
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
        if(getModel().getFileName()==null){
            return;   
          }
        Command reader =new ReadUnitLocal(this,Configuration.get().getSymbolsRoot(),
                                                    getModel().getLibraryName(),
                                                    getModel().getCategoryName(),
                                                    getModel().getFileName(),
                                                    Symbol.class);
       CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);  
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
        if(receiver==Symbol.class){         
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
                SymbolContainer source = (SymbolContainer) task.get();                
                    for (Symbol symbol : source.getUnits()) {
                        try {
                            Symbol copy = symbol.clone();
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
}
