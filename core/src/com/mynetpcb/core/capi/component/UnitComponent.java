package com.mynetpcb.core.capi.component;


import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Scrollable;
import com.mynetpcb.core.capi.UnitComponentKeyboardListener;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.Zoomable;
import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerEventDispatcher;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.EventMgr;
import com.mynetpcb.core.capi.line.AbstractBendingProcessorFactory;
import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.popup.AbstractPopupItemsContainer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;

import java.lang.ref.WeakReference;

import java.security.AccessControlException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;


public abstract class UnitComponent<U extends Unit, S extends Shape, M extends UnitContainer> extends JComponent implements Scrollable,
                                                                                                                   Zoomable,
                                                                                                                   ChangeListener,
                                                                                                                   ComponentListener,
                                                                                                                   MouseListener,
                                                                                                                   MouseMotionListener,
                                                                                                                   MouseWheelListener,
                                                                                                                   KeyListener,
                                                                                                                   ContainerEventDispatcher {

    public static final int COMPONENT_MODE = 0x00;
    
    public static final int ORIGIN_SHIFT_MODE=0x08;
    
    public static final int MEASUMENT_MODE=0x0C;
    
    private static final int MIN_UNIT_INCREAMEN=0x10; 
    
    public static final int DRAGHEAND_MODE=0x11; 
    
    private M model;

    private ViewportWindow viewportWindow;

    private Canvas canvas;

    //***keep a strong reference
    private S cursor;

    private final WeakReference<DialogFrame> dialogFrame;

    protected EventMgr<? extends UnitComponent, S> eventMgr;

    private int mode;

    private EventListenerList containerListeners;

    //***keep current processor for wiring event
    private LineBendingProcessor lineBendingProcessor;

    protected AbstractBendingProcessorFactory bendingProcessorFactory;
    
    //parameter bag
    private final Map<String, Object> parameters;
    
    protected AbstractLoadDialog.Builder loadDialogBuilder;

    public UnitComponent(DialogFrame dialogFrame) {
        this.dialogFrame = new WeakReference<DialogFrame>(dialogFrame);
        dialogFrame.getHorizontalScrollBar().getModel().addChangeListener(this);
        dialogFrame.getVerticalScrollBar().getModel().addChangeListener(this);
        this.addMouseWheelListener(this);
        this.addComponentListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.setFocusable(true);
        viewportWindow = new ViewportWindow();
        canvas = new Canvas(1, 1);
        parameters = new HashMap<String, Object>(5);
        this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        containerListeners = new EventListenerList();
    }
    public AbstractLoadDialog.Builder getLoadDialogBuilder(){
        return loadDialogBuilder;
    }
    public <T> T getParameter(String name, Class<T> clazz) {
        return clazz.cast(parameters.get(name));
    }

    public <T> T getParameter(String name, Class<T> clazz, T defaultValue) {
        T value = clazz.cast(parameters.get(name));
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }
    
    public <P extends AbstractPopupItemsContainer> P getPopupMenu() {
       return null;
    }
    
    public void setModel(M model) {
        this.model = model;
    }

    public M getModel() {
        return model;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (cursor != null) {
            cursor.Clear();
            cursor = null;
        }
        eventMgr.resetEventHandle();
    }

    public int getMode() {
        return this.mode;
    }

    public void setContainerCursor(S cursor) {
        this.setCursor(Cursor.getDefaultCursor());
        this.cursor = cursor;
    }

    public S getContainerCursor() {
        return this.cursor;
    }

    public LineBendingProcessor getLineBendingProcessor() {
        return lineBendingProcessor;
    }
    
    public void setLineBendingProcessor(LineBendingProcessor lineBendingProcessor) {
       this.lineBendingProcessor = lineBendingProcessor;
    }
    
    public AbstractBendingProcessorFactory getBendingProcessorFactory(){
        return bendingProcessorFactory;
    }
    
    public void resumeLine(Trackable line,String handleKey, int x, int y) {
        getLineBendingProcessor().Initialize(line);
        line.Reset(x,y);
        //***do we need to reorder
        line.Reverse(x,y);
        line.setSelected(true);
        getEventMgr().setEventHandle(handleKey,(S)line);
    }
    
    public ViewportWindow getViewportWindow() {
        return viewportWindow;
    }

    public void Clear() {
        setSize(1, 1);
        revalidate();
        eventMgr.resetEventHandle();
        model.Clear();
        System.gc();
    }


    //***Remove resources

    public void Release() {
        this.Clear();
        parameters.clear();
        model.Release();

        //***clear listeners list
        for (int i = 0; i < containerListeners.getListenerList().length; i++) {
            containerListeners.getListenerList()[i] = null;
        }
        containerListeners = null;

        this.removeMouseListener(this);
        this.removeMouseMotionListener(this);
        this.removeKeyListener(this);
        this.removeMouseWheelListener(this);
        this.removeComponentListener(this);
    }

    public void Repaint() {
        if (model.getUnit() != null && canvas != null) {
            Graphics2D g2 = (Graphics2D)canvas.getGraphics();
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            model.getUnit().Paint(g2, viewportWindow);
            if (cursor != null) {
                cursor.Paint(g2, viewportWindow,
                             getModel().getUnit().getScalableTransformation().getCurrentTransformation(),Layer.Copper.All.getLayerMaskID());

            }
        }
        repaint();
    }

    public void Export(String fileName,PrintContext context){
        this.model.getUnit().prepare(context);
        try {
            this.model.getUnit().export(fileName,context);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        //finish callback
        this.model.getUnit().finish();
    }
    public void Print(PrintContext context) {
        PrinterJob printJob = PrinterJob.getPrinterJob();

        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(OrientationRequested.LANDSCAPE);
        aset.add(PrintQuality.HIGH);
        aset.add(new MediaPrintableArea(10, 10, (MediaSize.ISO.A4.getX(Size2DSyntax.MM) - 20),
                                        (MediaSize.ISO.A4.getY(Size2DSyntax.MM) - 20), Size2DSyntax.MM));
        //set number of pages
        aset.add(new PageRanges(1,this.model.getUnit().getNumberOfPages()));
        
        aset.add(new JobName("myNetPCB - "+context.getTag(), null));

        printJob.setPrintable(this.model.getUnit());


        //--- Show a print dialog to the user. If the user
        //--- click the print button, then print otherwise
        //--- cancel the print job
        if (printJob.printDialog(aset)) {
            //prapare callback
            this.model.getUnit().prepare(context);
            try {
                printJob.print(aset);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            //finish callback
            this.model.getUnit().finish();
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        if (model.getUnit() != null) {
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(canvas, 0, 0, null);
        }
        super.paintComponent(g);
    }

    @Override
    public void setSize(int width, int height) {
        canvas = new Canvas(width < 1 ? 1 : width, height < 1 ? 1 : height);
        viewportWindow.setSize(width < 1 ? 1 : width, height < 1 ? 1 : height);
    }

    @Override
    public void ScrollX(int x) {
        viewportWindow.x = x;
    }

    @Override
    public void ScrollY(int y) {
        viewportWindow.y = y;
    }

    public void setScrollPosition(int x, int y) {
        final Point2D position = new Point2D.Double(x, y);
        this.getModel().getUnit().getScalableTransformation().getCurrentTransformation().transform(position, position);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getDialogFrame().getHorizontalScrollBar().setValue((int)position.getX() - getWidth() / 2);
                getDialogFrame().getVerticalScrollBar().setValue((int)position.getY() - getHeight() / 2);
            }
        });
    }

    @Override
    public void Rotate(int rotationType, Point p) {
        Point tmp = new Point();
        if (Utilities.ROTATION_RIGHT == rotationType) {

            //***rotate clock wize at point (0,0)
            AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI / 2, 0, 0);

            rotation.transform(p, tmp);

            //***translate the symbols on X axes
            AffineTransform translate = AffineTransform.getTranslateInstance(this.getModel().getUnit().getWidth(), 0);
            translate.transform(tmp, p);

        }
        if (Utilities.ROTATION_LEFT == rotationType) {

            //***translate the symbols on X axes
            AffineTransform translate =
                AffineTransform.getTranslateInstance(-this.getModel().getUnit().getHeight(), 0);
            translate.transform(p, tmp);


            //***rotate clock wize at point (0,0)
            AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
            rotation.transform(tmp, p);
        }

        setScrollPosition(p.x, p.y);


    }

    @Override
    public boolean ZoomOut(Point windowPoint) {
        if (model.getUnit().getScalableTransformation().ScaleOut()) {
            this.getViewportWindow().scaleout(windowPoint.x, windowPoint.y,
                                              this.model.getUnit().getScalableTransformation());
            this.Repaint();
        } else {
            return false;
        }

        //***notify event handle
        if (eventMgr.getTargetEventHandle() != null) {
            eventMgr.getTargetEventHandle().resizingEvent();
        }

        JScrollBar hbar = dialogFrame.get().getHorizontalScrollBar();
        JScrollBar vbar = dialogFrame.get().getVerticalScrollBar();
        hbar.getModel().removeChangeListener(this);
        vbar.getModel().removeChangeListener(this);

        //set new maximum
        hbar.setMaximum((int)(this.model.getUnit().getWidth() *
                              this.model.getUnit().getScalableTransformation().getCurrentTransformation().getScaleX()));
        vbar.setMaximum((int)(this.model.getUnit().getHeight() *
                              this.model.getUnit().getScalableTransformation().getCurrentTransformation().getScaleY()));
        //set visible amount
        hbar.setVisibleAmount(this.getWidth());
        vbar.setVisibleAmount(this.getHeight());
        //set new initial value
        hbar.setValue(this.getViewportWindow().x);
        vbar.setValue(this.getViewportWindow().y);

        hbar.getModel().addChangeListener(this);
        vbar.getModel().addChangeListener(this);
        return true;
    }

    @Override
    public boolean ZoomIn(Point windowPoint) {
        if (model.getUnit().getScalableTransformation().ScaleIn()) {
            this.getViewportWindow().scalein(windowPoint.x, windowPoint.y,
                                             this.model.getUnit().getScalableTransformation());
            this.Repaint();
        } else {
            return false;
        }

        //***notify event handle
        if (eventMgr.getTargetEventHandle() != null) {
            eventMgr.getTargetEventHandle().resizingEvent();
        }

        JScrollBar hbar = dialogFrame.get().getHorizontalScrollBar();
        JScrollBar vbar = dialogFrame.get().getVerticalScrollBar();
        hbar.getModel().removeChangeListener(this);
        vbar.getModel().removeChangeListener(this);

        //set new maximum
        hbar.setMaximum((int)(this.model.getUnit().getWidth() *
                              this.model.getUnit().getScalableTransformation().getCurrentTransformation().getScaleX()));
        vbar.setMaximum((int)(this.model.getUnit().getHeight() *
                              this.model.getUnit().getScalableTransformation().getCurrentTransformation().getScaleY()));
        //set visible amount
        hbar.setVisibleAmount(this.getWidth());
        vbar.setVisibleAmount(this.getHeight());
        //set new initial value
        hbar.setValue(this.getViewportWindow().x);
        vbar.setValue(this.getViewportWindow().y);

        hbar.getModel().addChangeListener(this);
        vbar.getModel().addChangeListener(this);
        return true;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == dialogFrame.get().getHorizontalScrollBar().getModel()) {
            this.ScrollX(dialogFrame.get().getHorizontalScrollBar().getValue());
        }

        if (e.getSource() == dialogFrame.get().getVerticalScrollBar().getModel()) {
            this.ScrollY(dialogFrame.get().getVerticalScrollBar().getValue());
        }
        
        this.Repaint();
    }
    
    @Override
    public void componentResized(ComponentEvent event) {
        if (this.model.getUnit() == null) {
            this.setSize(1, 1);
            JScrollBar hbar = dialogFrame.get().getHorizontalScrollBar();
            JScrollBar vbar = dialogFrame.get().getVerticalScrollBar();
            hbar.setValues(0, 0, 0, 0);
            vbar.setValues(0, 0, 0, 0);
        } else {
            this.setSize(this.getWidth(), this.getHeight());
            JScrollBar hbar = dialogFrame.get().getHorizontalScrollBar();
            JScrollBar vbar = dialogFrame.get().getVerticalScrollBar();
            
            
            //current value may exceed width/height then reset->flipping from one unit to smaller one.
            int hCurrentValue = event==null?0:hbar.getValue();
            int vCurrentValue = event==null?0:vbar.getValue();
            
            
            hbar.setValues(hCurrentValue, (this.getWidth()), 0,
                           (int)(this.model.getUnit().getWidth() * this.model.getUnit().getScalableTransformation().getCurrentTransformation().getScaleX()));
            int unitIncrement=(int)(this.model.getUnit().getGrid().getGridPointToPoint() *
                                        this.model.getUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
            hbar.setUnitIncrement(unitIncrement<MIN_UNIT_INCREAMEN?MIN_UNIT_INCREAMEN:unitIncrement);
            
            vbar.setValues(vCurrentValue, (this.getHeight()), 0,
                           (int)(model.getUnit().getHeight() * this.model.getUnit().getScalableTransformation().getCurrentTransformation().getScaleX()));            
            vbar.setUnitIncrement(unitIncrement<MIN_UNIT_INCREAMEN?MIN_UNIT_INCREAMEN:unitIncrement);
            
            hbar.setBlockIncrement(this.getWidth());
            vbar.setBlockIncrement(this.getHeight());
            
            
        }
       
        //is it OS resizing
        if(event!=null){
           this.Repaint(); 
        }
    }

    public DialogFrame getDialogFrame() {
        return dialogFrame.get();
    }
    
    public abstract void Reload();
    
    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }
    
    protected boolean isMouseWheelRederect(MouseWheelEvent event){
        //***MOVE LEFT-RIGHT
        if (event.getModifiersEx() != 0 && (event.getModifiers() == ActionEvent.CTRL_MASK)) {
            int x = getDialogFrame().getHorizontalScrollBar().getValue();
            int maxx = getDialogFrame().getHorizontalScrollBar().getMaximum();
            int deltax =
                (int)Math.round(getModel().getUnit().getGrid().getGridPointToPoint() * getModel().getUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
            deltax =deltax<MIN_UNIT_INCREAMEN?MIN_UNIT_INCREAMEN:deltax;
            
            if (event.getWheelRotation() > 0) {
                if ((x + deltax) > maxx) {
                    getDialogFrame().getHorizontalScrollBar().setValue(maxx);
                } else {
                    getDialogFrame().getHorizontalScrollBar().setValue(x + deltax);
                }
            } else {
                if ((x - deltax) < 0) {
                    getDialogFrame().getHorizontalScrollBar().setValue(0);
                } else {
                    getDialogFrame().getHorizontalScrollBar().setValue(x - deltax);
                }
            }
            return true;
        }
        //***MOVE TOP-BOTTOM
        if (event.getModifiersEx() != 0 && (event.getModifiers() == ActionEvent.SHIFT_MASK)) {
            int y = getDialogFrame().getVerticalScrollBar().getValue();
            int maxy = getDialogFrame().getVerticalScrollBar().getMaximum();
            int deltay =
                (int)Math.round(getModel().getUnit().getGrid().getGridPointToPoint() * getModel().getUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
            deltay =deltay<MIN_UNIT_INCREAMEN?MIN_UNIT_INCREAMEN:deltay;
            
            if (event.getWheelRotation() > 0) {
                if ((y + deltay) > maxy) {
                    getDialogFrame().getVerticalScrollBar().setValue(maxy);
                } else {
                    getDialogFrame().getVerticalScrollBar().setValue(y + deltay);
                }
            } else {
                if ((y - deltay) < 0) {
                    getDialogFrame().getVerticalScrollBar().setValue(0);
                } else {
                    getDialogFrame().getVerticalScrollBar().setValue(y - deltay);
                }
            }
            return true;
        }        
        return false;
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(this.getModel().getUnit()==null)
            return;
        if(this.isMouseWheelRederect(e)){
            return;
        }
        if (e.getWheelRotation() > 0) {
            ZoomIn(e.getPoint());
        } else {
            ZoomOut(e.getPoint());
        }
    }



    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            if (getEventMgr().getTargetEventHandle() != null) {
                getEventMgr().getTargetEventHandle().doubleClick(e);
            }
        }
    }
    
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (eventMgr.getTargetEventHandle() != null) {
            eventMgr.getTargetEventHandle().mousePressed(mouseEvent);
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (eventMgr.getTargetEventHandle() != null) {
            eventMgr.getTargetEventHandle().mouseReleased(mouseEvent);
        }
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (eventMgr.getTargetEventHandle() != null) {
            eventMgr.getTargetEventHandle().mouseDragged(mouseEvent);
        }
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        if (eventMgr.getTargetEventHandle() != null && getModel().getUnit() != null) {
            eventMgr.getTargetEventHandle().mouseMove(mouseEvent);
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    public EventMgr<? extends UnitComponent, S> getEventMgr() {
        return eventMgr;
    }

    public void keyTyped(KeyEvent e) {
    }


    public void keyPressed(KeyEvent e) {
        if (eventMgr.getTargetEventHandle() != null && getModel().getUnit() != null) {
            eventMgr.getTargetEventHandle().keyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void fireContainerEvent(ContainerEvent e) {
        Object[] listeners = containerListeners.getListenerList();
        int numListeners = listeners.length;
        for (int i = 0; i < numListeners; i += 2) {
            if (listeners[i] == ContainerListener.class) {
                switch (e.getEventType()) {
                case ContainerEvent.SELECT_CONTAINER:
                    ((ContainerListener)listeners[i + 1]).selectContainerEvent(e);
                    break;
                case ContainerEvent.RENAME_CONTAINER:
                    ((ContainerListener)listeners[i + 1]).renameContainerEvent(e);
                    break;
                case ContainerEvent.DELETE_CONTAINER:
                    ((ContainerListener)listeners[i + 1]).deleteContainerEvent(e);
                    break;
                }
            }
        }
    }

    public void addContainerListener(ContainerListener listener) {
        containerListeners.add(ContainerListener.class, listener);
    }

    public void removeContainerListener(ContainerListener listener) {
        containerListeners.remove(ContainerListener.class, listener);
    }

    private static final UnitComponentKeyboardListener unitKeyboardListener;

    public static UnitComponentKeyboardListener getUnitKeyboardListener() {
        return unitKeyboardListener;
    }
    static {
        unitKeyboardListener = new UnitComponentKeyboardListener();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(unitKeyboardListener);
    }

    public boolean processKeyPress(KeyEvent e) {
        //***skip inspector panel edit boxes
        if (e.getComponent() instanceof JTextComponent) {
            return false;
        }
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (getEventMgr().getTargetEventHandle() == null ||
                !getEventMgr().getTargetEventHandle().forwardKeyPress(e)) {
                return defaultKeyPress(e);
            }
        }

        return false;
    }

    protected boolean defaultKeyPress(KeyEvent e) {
                if (getModel().getUnit() != null) {
                    if (e.getModifiersEx() != 0 && (e.getModifiers() == ActionEvent.CTRL_MASK)) {
                        if (e.getKeyCode() == KeyEvent.VK_Z) {
                            if (getModel().getUnit().Undo(getEventMgr().getTargetEventHandle())) {
                                Repaint();
                                revalidate();
                            } else {
                                Repaint();
                            }
                            return true;
                        }
                        if (e.getKeyCode() == KeyEvent.VK_Y) {
                            if (getModel().getUnit().Redo()) {
                                Repaint();
                                revalidate();
                            } else {
                                Repaint();
                            }
                            return true;
                        }
                    if (e.getKeyCode() == KeyEvent.VK_S) {
                        getModel().getUnit().setSelected(true);
                        Repaint();
                        return true;
                    }
                    //***copy
                    if (e.getKeyCode() == KeyEvent.VK_C) {
                        Copy();
                        return true;
                    }
                    //***paste
                    if (e.getKeyCode() == KeyEvent.VK_V) {
                        Paste();
                        return true;
                    }
                }
                    //***ESCAPE==cancel for Wiring mode
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    //1.kill popup
                    if(this.getPopupMenu().isVisible()){
                        this.getPopupMenu().setVisible(false);                
                    }else{
                    //2.kill current mode
                        this.getDialogFrame().setButtonGroup(COMPONENT_MODE);
                        this.setMode(COMPONENT_MODE);                        
                    }
                    Repaint();
                    return true;
                }    
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    UnitMgr unitMgr = new UnitMgr();
                    getModel().getUnit().registerMemento(new CompositeMemento(MementoType.MOVE_MEMENTO).Add(getModel().getUnit().getSelectedShapes(false)));
                    getModel().getUnit().registerMemento(new CompositeMemento(MementoType.DELETE_MEMENTO).Add(getModel().getUnit().getSelectedShapes(false)));
                    //reset event handle
                    getEventMgr().resetEventHandle();
                    this.getPopupMenu().setVisible(false);
                    unitMgr.deleteBlock(getModel().getUnit(), getModel().getUnit().getSelectedShapes(true));
                    Repaint();
                    return true;
                }
                }
        return false;
    }

    public void Copy(){
        try {
            ClipboardMgr.getInstance().setClipboardContent(Clipboardable.Clipboard.LOCAL,
                                                           getModel().getUnit().createClipboardContent());
        } catch (AccessControlException ace) {
            JOptionPane.showMessageDialog(getDialogFrame().getParentFrame(),
                                          "You need to use the signed applet version.",
                                          "Security exception", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void Paste(){
        try {
            getModel().getUnit().setSelected(false);
            getModel().getUnit().realizeClipboardContent(ClipboardMgr.getInstance().getClipboardContent(Clipboardable.Clipboard.LOCAL ));
            
            //position onto screen center                            
            Point point=getModel().getUnit().getScalableTransformation().getInversePoint(new Point(getViewportWindow().x +
                                                                                                                   getWidth()/2,
                                                                                                                   getViewportWindow().y +
                                                                                                                   getHeight()/2));
            UnitMgr unitMgr = new UnitMgr();
            Collection<Shape> shapes=this.getModel().getUnit().getSelectedShapes(false);
            Rectangle r=this.getModel().getUnit().getShapesRect(shapes);                                                      
            //move to screen center
            unitMgr.moveBlock(shapes,point.x-r.x,point.y-r.y);

            //register with Do/Undo Mgr
            getModel().getUnit().registerMemento(new CompositeMemento(MementoType.CREATE_MEMENTO).Add(getModel().getUnit().getSelectedShapes(false)));
            
            Repaint();
            //***emit property event change
        //                            if (getModel().getUnit().getSelectedShapes(false).size() == 1) {
        //                                getModel().getUnit().fireShapeEvent(new ShapeEvent((Moveable)getModel().getUnit().getSelectedShapes(false).iterator().next(),
        //                                                                               ShapeEvent.SELECT_SHAPE));
        //                            }
        } catch (AccessControlException ace) {
            JOptionPane.showMessageDialog(getDialogFrame().getParentFrame(),
                                          "You need to use the signed applet version.",
                                          "Security exception", JOptionPane.ERROR_MESSAGE);
        }
    }
    public final class Canvas extends BufferedImage {

        public Canvas(int width, int height) {
            super(width, height, TYPE_BYTE_INDEXED);
        }

    }
}

