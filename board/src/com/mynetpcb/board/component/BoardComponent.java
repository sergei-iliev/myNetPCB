package com.mynetpcb.board.component;

import com.mynetpcb.board.line.BoardBendingProcessorFactory;
import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.container.BoardContainerFactory;
import com.mynetpcb.board.dialog.BoardLoadDialog;
import com.mynetpcb.board.event.BoardEventMgr;
import com.mynetpcb.board.event.CopperAreaEventHandle;
import com.mynetpcb.board.event.TrackEventHandle;
import com.mynetpcb.board.popup.BoardPopupMenu;
import com.mynetpcb.board.shape.PCBArc;
import com.mynetpcb.board.shape.PCBCircle;
import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBHole;
import com.mynetpcb.board.shape.PCBLabel;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBRoundRect;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.board.unit.BoardMgr;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ScalableTransformation;
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
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;

import com.mynetpcb.pad.shape.Arc;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import java.util.Collection;

import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

/**
 * The Board Component GUI
 * @author Sergey Iliev
 */
public class BoardComponent extends UnitComponent<Board, Shape, BoardContainer> implements CommandListener {

    public static final int TRACK_MODE = 0x01;

    public static final int RECT_MODE = 0x02;

    public static final int LINE_MODE = 0x03;

    public static final int FOOTPRINT_MODE = 0x07;

    public static final int VIA_MODE = 0x04;

    public static final int LABEL_MODE = 0x05;

    public static final int ARC_MODE = 0x09;

    public static final int ELLIPSE_MODE = 0x0A;

    public static final int COPPERAREA_MODE = 0x0B;

    public static final int HOLE_MODE = 0x10;

    private final BoardPopupMenu popup;

    public BoardComponent(DialogFrame dialog) {
        super(dialog);
        this.setModel(new BoardContainer());
        this.eventMgr = new BoardEventMgr(this);
        this.setBackground(Color.BLACK);

        this.loadDialogBuilder = new BoardLoadDialog.Builder();
        popup = new BoardPopupMenu(this);
        bendingProcessorFactory = new BoardBendingProcessorFactory();
        setLineBendingProcessor(bendingProcessorFactory.resolve("defaultbend", null));
    }

    public void setMode(int mode) {
        super.setMode(mode);
        Shape shape = null;
        Cursor cursor = null;
        this.requestFocusInWindow(); //***for the cancel button
        switch (getMode()) {
        case ELLIPSE_MODE:
            shape =
                new PCBCircle(0, 0, Grid.MM_TO_COORD(4), Grid.MM_TO_COORD(0.2),
                               getModel().getUnit().getActiveSide() == Layer.Side.BOTTOM ? Layer.SILKSCREEN_LAYER_BACK :
                               Layer.SILKSCREEN_LAYER_FRONT);
            setContainerCursor(shape);
            getEventMgr().setEventHandle("cursor", shape);
            break;
        case ARC_MODE:
            shape =
                new PCBArc(0, 0, Grid.MM_TO_COORD(7), Grid.MM_TO_COORD(0.2),
                           getModel().getUnit().getActiveSide() == Layer.Side.BOTTOM ? Layer.SILKSCREEN_LAYER_BACK :
                           Layer.SILKSCREEN_LAYER_FRONT);
            setContainerCursor(shape);
            getEventMgr().setEventHandle("cursor", shape);
            break;
        case LABEL_MODE:
            shape =
                new PCBLabel(getModel().getUnit().getActiveSide() == Layer.Side.BOTTOM ? Layer.SILKSCREEN_LAYER_BACK :
                             Layer.SILKSCREEN_LAYER_FRONT);
            setContainerCursor(shape);
            getEventMgr().setEventHandle("cursor", shape);
            break;
        case RECT_MODE:
                shape =
                    new PCBRoundRect(0,0,Grid.MM_TO_COORD(4),Grid.MM_TO_COORD(4),Grid.MM_TO_COORD(1),Grid.MM_TO_COORD(0.2),
                               getModel().getUnit().getActiveSide() == Layer.Side.BOTTOM ? Layer.SILKSCREEN_LAYER_BACK :
                               Layer.SILKSCREEN_LAYER_FRONT);
                setContainerCursor(shape);
                getEventMgr().setEventHandle("cursor", shape);
                break;
        case VIA_MODE:
            this.setCursor(Cursor.getDefaultCursor());
            shape = new PCBVia();
            setContainerCursor(shape);
            getEventMgr().setEventHandle("cursor", shape);
            break;
        case HOLE_MODE:
            this.setCursor(Cursor.getDefaultCursor());
            shape = new PCBHole();
            setContainerCursor(shape);
            getEventMgr().setEventHandle("cursor", shape);
            break;
        case ORIGIN_SHIFT_MODE:
            getEventMgr().setEventHandle("origin", null);
            break;
        case DRAGHEAND_MODE:
            cursor =
                Toolkit.getDefaultToolkit().createCustomCursor(Utilities.loadImageIcon(getDialogFrame(),
                                                                                       "/com/mynetpcb/core/images/dragopen.png").getImage(),
                                                               new Point(16, 16), "DragHeandOpen");
            this.setCursor(cursor);
            break;
        default:
            this.setCursor(Cursor.getDefaultCursor());
            this.Repaint();
        }
        //        if(shape!=null){
        //            //orient in regard to side
        //            int mask=Layer.Side.convert(getModel().getUnit().getActiveSide() , shape.getCopper().getLayerMaskID());
        //            if(mask!=shape.getCopper().getLayerMaskID()){
        //              shape.setCopper(Layer.Copper.resolve(mask));
        //            }
        //        }
    }

    public void mousePressed(MouseEvent event) {
        if (getModel().getUnit() == null) {
            getEventMgr().resetEventHandle();
        } else {
            MouseScaledEvent scaledEvent =
                new MouseScaledEvent(event,
                                     getModel().getUnit().getScalableTransformation().getInversePoint(new Point(getViewportWindow().x +
                                                                                                                event.getX(),
                                                                                                                getViewportWindow().y +
                                                                                                                event.getY())));

            switch (getMode()) {
            case COMPONENT_MODE:
                /*
                * 1.Coordinate origin
                * 2.Control rect/reshape point
                * 3.selected shapes comes before control points
                */
                if (getModel().getUnit().getCoordinateSystem().isClicked(scaledEvent.getX(), scaledEvent.getY())) {
                    getEventMgr().setEventHandle("origin", null);
                    break;
                }

                Shape shape = getModel().getUnit().isControlRectClicked(scaledEvent.getX(), scaledEvent.getY());

                if (shape != null) {                    
                    if(shape instanceof Arc){
                        if(((Arc)shape).isStartAnglePointClicked(scaledEvent.getX() , scaledEvent.getY())){ 
                          getEventMgr().setEventHandle("arc.start.angle",shape);                    
                        }else if(((Arc)shape).isExtendAnglePointClicked(scaledEvent.getX() , scaledEvent.getY())){
                          getEventMgr().setEventHandle("arc.extend.angle",shape);                      
                        }else{
                          getEventMgr().setEventHandle("resize",shape);    
                        }
                    }else{
                      getEventMgr().setEventHandle("resize",shape);  
                    }
                } else if ((shape =
                            getModel().getUnit().getClickedShape(scaledEvent.getX(), scaledEvent.getY(), true)) !=
                           null) {
                    if ((BoardMgr.getInstance().isBlockSelected(getModel().getUnit()) && shape.isSelected())|| (event.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
                        getEventMgr().setEventHandle("block", shape);
                    } else if (!(shape instanceof PCBLabel) && (shape instanceof Textable) &&
                               (((Textable) shape).getChipText().getClickedTexture(scaledEvent.getX(),
                                                                                   scaledEvent.getY()) != null)) {
                        getEventMgr().setEventHandle("texture", shape);
                    } else if (shape instanceof PCBFootprint) {
                        getEventMgr().setEventHandle("symbol", shape);
                    } else
                        getEventMgr().setEventHandle("move", shape);
                } else {
                    getEventMgr().setEventHandle("component", null);
                }

                break;
            case TRACK_MODE:
                //***is this a new wire
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof TrackEventHandle)) {
                    //***handle popup when no active wire
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape =
                        new PCBTrack(Grid.MM_TO_COORD(0.4),
                                     getModel().getUnit().getActiveSide() == Layer.Side.BOTTOM ? Layer.LAYER_BACK :
                                     Layer.LAYER_FRONT);
                    getModel().getUnit().Add(shape);
                    getEventMgr().setEventHandle("track", shape);
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
                        new PCBLine(Grid.MM_TO_COORD(0.4),
                                    getModel().getUnit().getActiveSide() == Layer.Side.BOTTOM ?
                                    Layer.SILKSCREEN_LAYER_BACK : Layer.SILKSCREEN_LAYER_FRONT);
                    getModel().getUnit().Add(shape);
                    getEventMgr().setEventHandle("line", shape);
                }

                break;
            case COPPERAREA_MODE:
                //is this a new copper area
                if ((getEventMgr().getTargetEventHandle() == null) ||
                    !(getEventMgr().getTargetEventHandle() instanceof CopperAreaEventHandle)) {
                    if (event.getModifiers() == InputEvent.BUTTON3_MASK) {
                        return; //***right button click
                    }
                    shape =
                        new PCBCopperArea(getModel().getUnit().getActiveSide() == Layer.Side.BOTTOM ? Layer.LAYER_BACK :
                                          Layer.LAYER_FRONT);
                    getModel().getUnit().Add(shape);
                    getEventMgr().setEventHandle("copperarea", shape);
                }
                break;
            case DRAGHEAND_MODE:
                getEventMgr().setEventHandle("dragheand", null);
                break;
            case MEASUMENT_MODE:
                if ((getEventMgr().getTargetEventHandle() != null) ||
                    (getEventMgr().getTargetEventHandle() instanceof MeasureEventHandle)) {
                    getEventMgr().resetEventHandle();
                    this.Repaint();
                } else {
                    getEventMgr().setEventHandle("measure", this.getModel().getUnit().getRuler());
                    this.getModel().getUnit().getRuler().setLocation(scaledEvent.getX(), scaledEvent.getY());
                }
                break;
            }
        }

        super.mousePressed(event);
    }

    @Override
    public BoardPopupMenu getPopupMenu() {
        return popup;
    }

    @Override
    protected boolean defaultKeyPress(KeyEvent e) {
        if (super.defaultKeyPress(e)) {
            return true;
        }
        if (e.getModifiersEx() != 0) {
            if (e.getModifiers() == ActionEvent.CTRL_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_Q || e.getKeyCode() == KeyEvent.VK_A) {

                    Collection<Shape> shapes = getModel().getUnit().getSelectedShapes(false);
                    if (shapes.size() == 0) {
                        return true;
                    }
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size() > 1 ?
                                                         new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes) :
                                                         shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Rectangle r = getModel().getUnit().getShapesRect(shapes);


                    BoardMgr.getInstance().rotateBlock(shapes,
                                                       AffineTransform.getRotateInstance(((e.getKeyCode() ==
                                                                                           KeyEvent.VK_A) ? -1 : 1) *
                                                                                         Math.PI / 2, r.getCenterX(),
                                                                                         r.getCenterY()));
                    BoardMgr.getInstance().alignBlock(getModel().getUnit().getGrid(), shapes);

                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size() > 1 ?
                                                         new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes) :
                                                         shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Repaint();
                    return true;

                }
            }
            if (e.getModifiers() == ActionEvent.SHIFT_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_Q || e.getKeyCode() == KeyEvent.VK_A) {
                    Collection<Shape> shapes = getModel().getUnit().getSelectedShapes(false);
                    if (shapes.size() == 0) {
                        return true;
                    }
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size() > 1 ?
                                                         new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes) :
                                                         shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
                    Rectangle r = getModel().getUnit().getShapesRect(shapes);
                    Point p = getModel().getUnit().getGrid().positionOnGrid((int) r.getCenterX(), (int) r.getCenterY());
                    if (e.getKeyCode() == KeyEvent.VK_Q) {
                        BoardMgr.getInstance().mirrorBlock(getModel().getUnit(), new Point(p.x - 10, p.y),
                                                           new Point(p.x + 10, p.y));

                    } else {
                        BoardMgr.getInstance().mirrorBlock(getModel().getUnit(), new Point(p.x, p.y - 10),
                                                           new Point(p.x, p.y + 10));
                    }
                    BoardMgr.getInstance().alignBlock(getModel().getUnit().getGrid(), shapes);
                    //***notify undo manager
                    getModel().getUnit().registerMemento(shapes.size() > 1 ?
                                                         new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes) :
                                                         shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
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
    public void Import(String targetFile) {
        UnitContainerProducer unitContainerProducer=new UnitContainerProducer().withFactory("boards", new BoardContainerFactory());
        CommandExecutor.INSTANCE.addTask("import",
                                         new XMLImportTask(this,
                                                           unitContainerProducer,
                                                           targetFile, XMLImportTask.class));
    }
    @Override
    public void Reload() {

        if (getModel().getFileName() == null || getModel().getLibraryName() == null) {
            return;
        }
        if (!Configuration.get().isIsApplet()) {
            Command reader =
                new ReadUnitLocal(this, Configuration.get().getBoardsRoot(), getModel().getLibraryName(), null,
                                  getModel().getFileName(), Board.class);
            CommandExecutor.INSTANCE.addTask("ReadUnitLocal", reader);
        } else {
            Command reader =
                new ReadConnector(this,
                                  new RestParameterMap.ParameterBuilder("/boards").addURI(getModel().getLibraryName()).addURI(getModel().getFormatedFileName()).build(),
                                  Board.class);
            CommandExecutor.INSTANCE.addTask("ReadBoard", reader);
        }
    }

    public void OnStart(Class<?> reciever) {
        DisabledGlassPane.block(this.getDialogFrame().getRootPane(), "Loading...");
    }

    public void OnRecive(String result, Class reciever) {
        if (reciever == Board.class) {
            getModel().getUnit().Clear();
            try {
                getModel().Parse(result, getModel().getActiveUnitIndex());
                getModel().getUnit().setSelected(false);
                getModel().registerInitialState();
            } catch (Exception ioe) {
                ioe.printStackTrace(System.out);
            }
            this.componentResized(null);
            Rectangle r = this.getModel().getUnit().getBoundingRect();
            this.setScrollPosition((int) r.getCenterX(), (int) r.getCenterY());
            this.Repaint();
            this.revalidate();
        }
    }

    public void OnFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getDialogFrame().getRootPane());
        if (receiver == XMLImportTask.class) {
            FutureCommand task = CommandExecutor.INSTANCE.getTaskByName("import");
            try{
                BoardContainer source = (BoardContainer) task.get();
                    for (Board board : source.getUnits()) {
                        try {
                            Board copy = board.clone();
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
        JOptionPane.showMessageDialog(getDialogFrame().getParentFrame(), error, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
