package com.mynetpcb.pad.component;


import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.container.UnitContainerProducer;
import com.mynetpcb.core.capi.event.LineEventHandle;
import com.mynetpcb.core.capi.event.MeasureEventHandle;
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
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.DefaultBendingProcessorFactory;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.container.FootprintContainerFactory;
import com.mynetpcb.pad.dialog.FootprintLoadDialog;
import com.mynetpcb.pad.event.FootprintEventMgr;
import com.mynetpcb.pad.event.SolidRegionEventHandle;
import com.mynetpcb.pad.popup.FootprintPopupMenu;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Circle;
import com.mynetpcb.pad.shape.GlyphLabel;
import com.mynetpcb.pad.shape.Line;
import com.mynetpcb.pad.shape.RoundRect;
import com.mynetpcb.pad.shape.SolidRegion;
import com.mynetpcb.pad.unit.Footprint;
import com.mynetpcb.pad.unit.FootprintMgr;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;


public class FootprintComponent extends UnitComponent<Footprint, Shape, FootprintContainer> implements CommandListener{    


    
    private final FootprintPopupMenu popup;
    
    public FootprintComponent(DialogFrame dialogFrame) {
        super(dialogFrame);
        this.setModel(new FootprintContainer());
        this.eventMgr=new FootprintEventMgr(this);
        this.setMode(Mode.COMPONENT_MODE);
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
            case Mode.SOLID_REGION:
            break;
            case Mode.RECT_MODE:
             shape=new RoundRect(0,0,Grid.MM_TO_COORD(10),Grid.MM_TO_COORD(7),(int)Grid.MM_TO_COORD(0.8),(int)Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;
            case Mode.PAD_MODE:
             shape = FootprintMgr.getInstance().createPad(this.getModel().getUnit());
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;
            case Mode.ARC_MODE:
             shape=new Arc(0,0,Grid.MM_TO_COORD(3.4),60,60,(int)Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;                           
            case Mode.ELLIPSE_MODE:
             shape=new Circle(0,0,Grid.MM_TO_COORD(3.4),(int)Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;             
            case Mode.LABEL_MODE:             
             shape=new GlyphLabel("Label",(int)Grid.MM_TO_COORD(0.3),Layer.SILKSCREEN_LAYER_FRONT);
             setContainerCursor(shape);               
             getEventMgr().setEventHandle("cursor",shape);   
             break;
            case Mode.ORIGIN_SHIFT_MODE:  
             
             getEventMgr().setEventHandle("origin",null);   
             break;      
            case Mode.DRAGHEAND_MODE:
//             Cursor cursor =
//                    getToolkit().getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame() ,
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

    @Override
    public void mousePressed(MouseEvent event){  
        if(getModel().getUnit()==null){
          getEventMgr().resetEventHandle();
        }else{
            //transform event into a real footprint size one
            MouseScaledEvent scaledEvent =new MouseScaledEvent(event,getModel().getUnit().getScalableTransformation().getInversePoint(new java.awt.Point((int)getViewportWindow().getX()+event.getX(),(int)getViewportWindow().getY()+event.getY())));

            //find the right handler to handle the event
            switch (getMode()){
                case Mode.COMPONENT_MODE:
                /*
                 * 1.Coordinate system 
                 * 2.Control rect/reshape point
                 * 3.Shape(Text) clicked                      
                 * 4.Footprint
                 */
                if(this.getModel().getUnit().getCoordinateSystem()!=null){ 
                 if(getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())){
                    getEventMgr().setEventHandle("origin",null); 
                    break;
                 }
                }
                Shape shape=getModel().getUnit().isControlRectClicked(scaledEvent.getX() , scaledEvent.getY());
                
                if(shape!=null){
                    if(shape instanceof Arc){
                        if(((Arc)shape).isStartAnglePointClicked(scaledEvent.getX() , scaledEvent.getY())){ 
                          getEventMgr().setEventHandle("arc.start.angle",shape);                    
                        }else if(((Arc)shape).isExtendAnglePointClicked(scaledEvent.getX() , scaledEvent.getY())){
                          getEventMgr().setEventHandle("arc.extend.angle",shape);      
                        }else if(((Arc)shape).isMidPointClicked(scaledEvent.getX() , scaledEvent.getY())){
                          getEventMgr().setEventHandle("arc.mid.point",shape);
                        }else{
                          getEventMgr().setEventHandle("resize",shape);    
                        }
                    }else{
                      getEventMgr().setEventHandle("resize",shape);  
                    }
                }else if((shape = getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(), true))!=null){
                    //***block operation
                    if (FootprintMgr.getInstance().isBlockSelected(getModel().getUnit()) && shape.isSelected())
                         getEventMgr().setEventHandle("block", shape);
                    else if((shape instanceof Textable)&&( ((Textable)shape).getClickedTexture(scaledEvent.getX(), scaledEvent.getY())!=null)) 
                         getEventMgr().setEventHandle("texture", shape);
                    else
                         getEventMgr().setEventHandle("move",shape);
                }else{
                     getEventMgr().setEventHandle("component",null); 
                }
                break;
                
                case Mode.LINE_MODE:
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
                        shape = new Line((int)Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);
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
                            shape = new Line((int)Grid.MM_TO_COORD(0.2),Layer.SILKSCREEN_LAYER_FRONT);                        
                            getModel().getUnit().add(shape);
                        }
                    } 
                    getEventMgr().setEventHandle("line", shape);
                }
                
                break;
                case Mode.SOLID_REGION:
                    //is this a new copper area
                    if ((this.getEventMgr().getTargetEventHandle() == null) ||
                        !(this.getEventMgr().getTargetEventHandle() instanceof SolidRegionEventHandle)) {
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                        shape =new SolidRegion(Layer.LAYER_FRONT);
                        this.getModel().getUnit().add(shape);
                        this.getEventMgr().setEventHandle("solidregion", shape);
                    }                   
                    break;                
                case Mode.PAD_MODE:
                break;
                case Mode.DRAGHEAND_MODE:
                    getEventMgr().setEventHandle("dragheand", null);
                    break;
            case Mode.MEASUMENT_MODE:
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
                    
                    Collection<Shape> shapes= getModel().getUnit().getSelectedShapes();
                    if(shapes.size()==0){
                       return true; 
                    }   
                    //***notify undo manager                    
                    getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Box r=getModel().getUnit().getShapesRect(shapes);  
                    Point center=r.getCenter();

                    FootprintMgr.getInstance().rotateBlock(shapes,
                                           ((e.getKeyCode() ==KeyEvent.VK_A) ?
                                                                              -1 :
                                                                              1) *
                                                                             90,
                                                                             center); 
                    FootprintMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);                     

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
                    Point center=r.getCenter();
                    Point p=getModel().getUnit().getGrid().positionOnGrid(center); 
                    if(e.getKeyCode() == KeyEvent.VK_Q){
                        FootprintMgr.getInstance().mirrorBlock(getModel().getUnit().getSelectedShapes(),new com.mynetpcb.d2.shapes.Line(new Point(p.x - 10, p.y),
                                                              new Point(p.x + 10, p.y)));
                                           
                    }else{
                        FootprintMgr.getInstance().mirrorBlock(getModel().getUnit().getSelectedShapes(),new com.mynetpcb.d2.shapes.Line(
                                new Point(p.x, p.y - 10),
                                          new Point(p.x, p.y + 10)));
                    }
                    
                    FootprintMgr.getInstance().alignBlock(getModel().getUnit().getGrid(),shapes);
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
    public void _import(String targetFile) {
        UnitContainerProducer unitContainerProducer=new UnitContainerProducer().withFactory("footprints", new FootprintContainerFactory());
        CommandExecutor.INSTANCE.addTask("import",
                                         new XMLImportTask(this,
                                                           unitContainerProducer,
                                                           targetFile, XMLImportTask.class));
    }
    @Override
    public void reload() {
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
        DisabledGlassPane.block( this.getDialogFrame().getRootPane(),"Loading...");     
    }

    public void OnRecive(String result,  Class reciever) {
        if(reciever==Footprint.class){         
                getModel().getUnit().clear();             
             try{  
                getModel().parse(result,getModel().getActiveUnitIndex());                             
                getModel().getUnit().setSelected(false); 
                getModel().registerInitialState(); 
             }catch(Exception ioe){ioe.printStackTrace(System.out);}                          
                this.componentResized(null);
                Box r=this.getModel().getUnit().getBoundingRect();
                Point center=r.getCenter();
                this.setScrollPosition((int)center.x,(int)center.y);
                this.Repaint();
                this.revalidate();     
        }
    }

    public void OnFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getDialogFrame().getRootPane());       
        if (receiver == XMLImportTask.class) {
            FutureCommand task = CommandExecutor.INSTANCE.getTaskByName("import");
            try{
                FootprintContainer source = (FootprintContainer) task.get();
                    for (Footprint footprint : source.getUnits()) {
                        try {
                            Footprint copy = footprint.clone();
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

    public void OnError(String error) {
        DisabledGlassPane.unblock(getDialogFrame().getRootPane());  
        JOptionPane.showMessageDialog(getDialogFrame().getParentFrame(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE);      
    }
}

