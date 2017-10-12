package com.mynetpcb.pad.component;


import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.event.MeasureEventHandle;
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
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.dialog.FootprintLoadDialog;
import com.mynetpcb.pad.event.FootprintEventMgr;
import com.mynetpcb.pad.event.LineEventHandle;
import com.mynetpcb.pad.popup.FootprintPopupMenu;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Circle;
import com.mynetpcb.pad.shape.GlyphLabel;
import com.mynetpcb.pad.shape.Line;
import com.mynetpcb.pad.shape.RoundRect;
import com.mynetpcb.pad.unit.Footprint;
import com.mynetpcb.pad.unit.FootprintMgr;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import java.util.Collection;

import javax.swing.JOptionPane;


public class FootprintComponent extends UnitComponent<Footprint, Shape, FootprintContainer> implements CommandListener{    

    public static final int PAD_MODE = 0x01;

    public static final int RECT_MODE = 0x02;
    
    public static final int LINE_MODE = 0x03;
    
    public static final int ELLIPSE_MODE = 0x04;
    
    public static final int ARC_MODE = 0x05;
    
    public static final int DRAGHEAND_MODE=0x07;
    
    public static final int LABEL_MODE = 0x09;
    
    private final FootprintPopupMenu popup;
    
    public FootprintComponent(DialogFrame dialogFrame) {
        super(dialogFrame);
        this.setModel(new FootprintContainer());
        this.eventMgr=new FootprintEventMgr(this);
        this.setMode(COMPONENT_MODE);
        this.setBackground(Color.BLACK);
        this.loadDialogBuilder=new FootprintLoadDialog.Builder();
        this.popup=new FootprintPopupMenu(this);
        bendingProcessorFactory=new DefaultBendingProcessorFactory();
        setLineBendingProcessor(bendingProcessorFactory.resolve("defaultbend",null));
    }
    
    public void setMode(int mode) {
        super.setMode(mode);
        Shape shape=null;
        
        this.requestFocusInWindow(); //***for the cancel button
        switch (getMode()){
            case RECT_MODE:
             shape=new RoundRect(0,0,Grid.MM_TO_COORD(7),Grid.MM_TO_COORD(7),Grid.MM_TO_COORD(0.8),Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;
            case PAD_MODE:
             shape = FootprintMgr.getInstance().createPad(this.getModel().getUnit());
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;
            case ARC_MODE:
             shape=new Arc(0,0,Grid.MM_TO_COORD(3.4),Grid.MM_TO_COORD(0.4),Layer.SILKSCREEN_LAYER_FRONT);
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;                           
            case ELLIPSE_MODE:
             shape=new Circle(0,0,Grid.MM_TO_COORD(3.4),Grid.MM_TO_COORD(0.4),Layer.SILKSCREEN_LAYER_FRONT);
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;             
            case LABEL_MODE:             
             shape=new GlyphLabel("Label",Grid.MM_TO_COORD(0.3),Layer.SILKSCREEN_LAYER_FRONT);
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

    @Override
    public void mousePressed(MouseEvent event){  
        if(getModel().getUnit()==null){
          getEventMgr().resetEventHandle();
        }else{
            //if(getModel().getUnit().getShapes().size()>0){
            //   Arc s=(Arc)getModel().getUnit().getShapes().get(0);
            //   System.out.println(s.getStartPoint());
            //}
            //System.out.println(event.getX()+"::"+event.getY());
            //transform event into a real footprint size one
            MouseScaledEvent scaledEvent =new MouseScaledEvent(event,getModel().getUnit().getScalableTransformation().getInversePoint(new Point(getViewportWindow().x+event.getX(),getViewportWindow().y+event.getY())));

            //find the right handler to handle the event
            switch (getMode()){
                case COMPONENT_MODE:
                /*
                 * 1.Coordinate system 
                 * 2.Control rect/reshape point
                 * 3.Shape(Text) clicked                      
                 * 4.Footprint
                 */
                
                if(getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())){
                    getEventMgr().setEventHandle("origin",null); 
                    break;
                }
                
                Shape shape=getModel().getUnit().isControlRectClicked(scaledEvent.getX() , scaledEvent.getY());
                
                if(shape!=null){
                      getEventMgr().setEventHandle("resize",shape);                    
                }else if((shape = getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(), true))!=null){
                    //***block operation
                    if (FootprintMgr.getInstance().isBlockSelected(getModel().getUnit()) && shape.isSelected())
                         getEventMgr().setEventHandle("block", shape);
                    else if(!(shape instanceof GlyphLabel)&&(shape instanceof Textable)&&( ((Textable)shape).getChipText().getClickedTexture(scaledEvent.getX(), scaledEvent.getY())!=null)) 
                         getEventMgr().setEventHandle("texture", shape);
                    else
                         getEventMgr().setEventHandle("move",shape);
                }else{
                     getEventMgr().setEventHandle("component",null); 
                }
                break;
                
                case LINE_MODE:
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
                   
                    if ((shape == null) ||(!(shape instanceof Line))) {
                        shape = new Line(Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);
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
                            shape = new Line(Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);                        
                            getModel().getUnit().Add(shape);
                        }
                    } 
                    getEventMgr().setEventHandle("line", shape);
                }
                
                break;
                
                case PAD_MODE:
                break;
                case DRAGHEAND_MODE:
                    getEventMgr().setEventHandle("dragheand", null);
                    break;
            case MEASUMENT_MODE:
                if ((getEventMgr().getTargetEventHandle() != null) ||
                    (getEventMgr().getTargetEventHandle() instanceof MeasureEventHandle)) {
                    getEventMgr().resetEventHandle();
                    this.Repaint();
                }else{
                   getEventMgr().setEventHandle("measure",this.getModel().getUnit().getRuler());   
                   this.getModel().getUnit().getRuler().setLocation(scaledEvent.getX(),scaledEvent.getY());                   
                }
                   break;
              
            }
            
        }
        super.mousePressed(event);
    }
    
    @Override
    public FootprintPopupMenu getPopupMenu() {
        return popup;
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
                    

                    FootprintMgr.getInstance().rotateBlock(shapes,
                                           AffineTransform.getRotateInstance(((e.getKeyCode() ==
                                                                               KeyEvent.VK_A) ?
                                                                              -1 :
                                                                              1) *
                                                                             Math.PI /
                                                                             2,
                                                                             r.getCenterX(),
                                                                             r.getCenterY())); 
                    FootprintMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);                     

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
                        FootprintMgr.getInstance().mirrorBlock(getModel().getUnit(),                                        
                                            new Point(p.x - 10, p.y),
                                                              new Point(p.x + 10, p.y));
                                           
                    }else{
                        FootprintMgr.getInstance().mirrorBlock(getModel().getUnit(),
                                new Point(p.x, p.y - 10),
                                          new Point(p.x, p.y + 10));
                    }
                    
                    FootprintMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);
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
    public void Reload() {
        if(getModel().getFileName()==null){
          return;   
        }
        if (!Configuration.get().isIsApplet()) {
            Command reader =
                new ReadUnitLocal(this,Configuration.get().getFootprintsRoot(),
                                                  getModel().getLibraryName(),
                                                  getModel().getCategoryName(),
                                                  getModel().getFileName(),
                                                  Footprint.class);
            CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
        } else {
            Command reader=new ReadConnector(this,new RestParameterMap.ParameterBuilder("/footprints").addURI(getModel().getLibraryName()).addURI(getModel().getCategoryName()).addURI(getModel().getFormatedFileName()).build(),Footprint.class);
            CommandExecutor.INSTANCE.addTask("ReadModule",reader);              
        }    
    }
    
    public void OnStart(Class<?> reciever) {
        DisabledGlassPane.block( this.getDialogFrame().getRootPane(),"Reloading...");     
    }

    public void OnRecive(String result,  Class reciever) {
        if(reciever==Footprint.class){         
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

