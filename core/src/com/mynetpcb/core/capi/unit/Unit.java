package com.mynetpcb.core.capi.unit;


import com.mynetpcb.core.capi.CoordinateSystem;
import com.mynetpcb.core.capi.Drawable;
import com.mynetpcb.core.capi.Frameable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.pin.PinLineable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.Ruler;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.event.Event;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeEventDispatcher;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.layer.CompositeLayer;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.DefaultOrderedList;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.layer.OrderedList;
import com.mynetpcb.core.capi.line.Sublineable;
import com.mynetpcb.core.capi.print.PrintCallable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.undo.Stateable;
import com.mynetpcb.core.capi.undo.UndoCallback;
import com.mynetpcb.core.capi.undo.UndoProvider;
import com.mynetpcb.core.capi.undo.Undoable;

import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Rectangle;

import com.sun.jmx.remote.util.OrderClassLoaders;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.swing.event.EventListenerList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import org.xml.sax.SAXException;


public abstract class Unit<S extends Shape> implements ShapeEventDispatcher, PrintCallable,Undoable, Cloneable,Clipboardable {
    private UUID uuid;

    private int width, height;

    protected Grid grid;
    
    protected OrderedList<S> shapes;

    protected String unitName;

    protected ScalableTransformation scalableTransformation;

    private EventListenerList shapeListeners;

    private int scrollPositionXValue, scrollPositionYValue;

    protected AbstractShapeFactory shapeFactory;
    
    protected UndoProvider undoProvider;
    
    protected CoordinateSystem coordinateSystem;
    
    protected Ruler ruler;
    
    protected Frameable frame;
    
    public Unit(int width, int height) {
        this(width,height,new DefaultOrderedList<>());
    }
    
    public Unit(int width, int height,OrderedList<S> list) {
        uuid = UUID.randomUUID();
        shapes = list;
        scalableTransformation = new ScalableTransformation();
        shapeListeners = new EventListenerList();
        this.grid = new Grid(0.8, Grid.Units.MM);
        this.width = width;
        this.height = height;
        this.unitName = "Uknown";
        undoProvider = new UndoProvider();
        coordinateSystem=new CoordinateSystem(this);
        this.frame=new UnitFrame(width,height);
        this.ruler=new Ruler();
    }
    
    public Object clone() throws CloneNotSupportedException { 
      Unit copy=(Unit)super.clone();
      copy.frame=new UnitFrame(this.width,this.height);
      copy.frame.setOffset(this.frame.getOffset());
      copy.frame.setFillColor(this.frame.getFillColor());
      copy.uuid=UUID.randomUUID();
      copy.shapeListeners = new EventListenerList();
      copy.undoProvider = new UndoProvider();
      copy.grid=this.grid.clone();
      copy.ruler=new Ruler();
      copy.coordinateSystem =new CoordinateSystem(copy);
      copy.shapes=this.shapes.clone();         
            for (S shape : shapes) {
                    try {
                        copy.add(shape.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace(System.out);
                    }      
            }
      copy.scalableTransformation=this.scalableTransformation.clone();
      return copy;
    }
    
    public void setScrollPositionValue(int scrollPositionXValue, int scrollPositionYValue) {
        this.scrollPositionXValue = scrollPositionXValue;
        this.scrollPositionYValue = scrollPositionYValue;
    }

    public int getScrollPositionXValue() {
        return scrollPositionXValue;
    }

    public int getScrollPositionYValue() {
        return scrollPositionYValue;
    }

    public String getUnitName() {
        return unitName;
    }

    //***for access outside Unit

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    
    public CoordinateSystem getCoordinateSystem(){
        return coordinateSystem;
    }
    
    public UUID getUUID() {
        return uuid;
    }

    public void add(S shape) {
        if (shape == null)
            return;
        
        shape.setOwningUnit(this);        
        
        shapes.add(shape);

        //***notify listeners
        this.fireShapeEvent(new ShapeEvent(shape, Event.ADD_SHAPE));
    }

    public void clear() {
        //***go through all lists and delete them
        for (; shapes.size() > 0; ) {
            this.delete(shapes.get(0).getUUID());
        }
        shapes.clear();
        //undoProvider.Clear();
    }

    public void release() {
        this.clear();
        //***clear listeners list
        for (int i = 0; i < shapeListeners.getListenerList().length; i++) {
            shapeListeners.getListenerList()[i] = null;
        }
        shapeListeners = null;
    }
    //***delete drawable element

    public void delete(UUID uuid) {
        Iterator<S> i = shapes.iterator();
        while (i.hasNext()) {
            Shape shape = i.next();
            if (shape.getUUID().compareTo(uuid) == 0) {
                //***delete from list
                shapes.remove(shape);
                this.fireShapeEvent(new ShapeEvent(shape, ShapeEvent.DELETE_SHAPE));
                //***kill strong references
                shape.setOwningUnit(null);
                //***kill strong references
                if (shape instanceof Ownerable) {
                    ((Ownerable)shape).setOwner(null);
                }
                shape.clear();
                shape = null;
                return;
            }
        }
    }

    public void setSelected(boolean flag) {
        for (S shape : shapes) {
            shape.setSelected(flag);
        }
    }
    /*
    * notify all listeners of event on all elements.Sometimes listeners are not registered and notification is postponed
    */

    public void notifyListeners(int eventType) {
        for (S shape : shapes) {
            this.fireShapeEvent(new ShapeEvent(shape, eventType));
        }
    }
    /*
     * exclude those that are owned by others,count parents only
     */

    public Collection<S> getSelectedShapes(boolean parentsOnly) {
        Collection<S> v = new ArrayList<S>(20);
        for (S shape : shapes) {
            if (parentsOnly && shape instanceof Ownerable && ((Ownerable)shape).getOwner() != null) {
                continue;
            }
            if (shape.isSelected()) {
                v.add(shape);
            }
        }
        return v;
    }

    public UUID getSelected() {
        for (S shape : shapes) {
            if (shape.isSelected()) {
                return shape.getUUID();
            }
        }
        return null;
    }

    public void setSelected(UUID uuid, boolean selected) {

        for (S shape : shapes) {
            if (shape.getUUID().compareTo(uuid) == 0) {
                shape.setSelected(selected);
                break;
            }
        }
    }
    private boolean isShapeVisibleOnLayers(Shape shape){
        if(this instanceof CompositeLayerable){
          if(shape.isVisibleOnLayers(((CompositeLayerable)this).getLayerMaskID())){
            return true;
          }else{
            return false;  
          }
        }
        //default
        return true;
    }
    public void setSelected(Box rect) {

        for (S shape : shapes) {
            if(!isShapeVisibleOnLayers(shape)){
                continue;
            }
            if (shape.isInRect(rect)) {
                shape.setSelected(true);
            }else{
                //if(shape instanceof Sublineable&& ((Sublineable)shape).isSublineInRect(rect)){
                //    ((Sublineable)shape).setSublineSelected(rect, true);  
                //}
            }
        }
    }

    public OrderedList<S> getShapes() {
        return shapes;
    }
    public <M extends Drawable> List<M> getShapes(Class<?> clazz,int layermask) {
        ArrayList<M> list = new ArrayList<M>();
        for (S s : shapes) {
            
            if (clazz.isAssignableFrom(s.getClass())&&s.isVisibleOnLayers(layermask)) {                
                list.add((M)s);
            }
        }

        return list;       
    }
    public <M extends Drawable> List<M> getShapes(Class<?> clazz) {
        ArrayList<M> list = new ArrayList<M>();
        for (S s : shapes) {
            
            if (clazz.isAssignableFrom(s.getClass())) {
                list.add((M)s);
            }
        }
        return list;
    }

    public S getShape(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        for (S shape : shapes) {
            if ((shape).getUUID().compareTo(uuid) == 0)
                return shape;
        }
        return null;
    }

    public S isControlRectClicked(int x, int y) {
        /*
         * if two symbols overlap and one is selected
         * then the selected should be checked for control rect click first
         */
        {
            S shape = getShape(getSelected());
            if (shape != null && shape instanceof Resizeable && ((Resizeable)shape).isControlRectClicked(x, y) != null)
                return shape;
        }
        //for (S shape : getShapes()) {
        //    if (shape instanceof Resizeable && ((Resizeable)shape).isControlRectClicked(x, y) != null) {
        //        return shape;
        //    }
       // }
        return null;
    }
    /*
    * The Bounding rectangle based on all elements in the Unit
    */

    public Box getBoundingRect() {
        return getShapesRect(this.shapes);
    }

    public Box getShapesRect(Collection<S> shapes) {
        Box r = new Box();
        double x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;

        //***empty schematic,element,package
        if (shapes.size() == 0) {
            return r;
        }

        for (Shape shape : shapes) {
            Box tmp = shape.getBoundingShape();
            
            if (tmp != null) {
                x1 = Math.min(x1, tmp.min.x);
                y1 = Math.min(y1, tmp.min.y);
                x2 = Math.max(x2, tmp.max.x );
                y2 = Math.max(y2, tmp.max.y);
            }

            //isolate simple pin text/SIMPLE_TEXT
            if(shape instanceof PinLineable){
                 continue;
            }
           
//            if (shape instanceof Textable) {
//                tmp = ((Textable)shape).getChipText().getBoundingShape();
//                
//                if (tmp != null) {
//                    x1 = Math.min(x1, tmp.x);
//                    y1 = Math.min(y1, tmp.y);
//                    x2 = Math.max(x2, tmp.x+ tmp.width);
//                    y2 = Math.max(y2, tmp.y+tmp.height);
//                }
//            }


        }
        r.setRect(x1, y1, x2 - x1, y2 - y1);
        return r;
    }
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.frame.setSize(width, height);
    }

    public abstract StringBuffer format();

    public abstract void parse(Node node) throws XPathExpressionException, ParserConfigurationException;

    /*
     * is junshion click
     */
    public Optional<S> getShapeAt(int x,int y,Class<?> clazz){
        S s=getClickedShape(x, y, false);
    
                
        if (clazz.isAssignableFrom(s.getClass())) {
            return Optional.of(s);
        }else{
            return Optional.empty();
        }                
    }
    protected List<Shape> buildClickedShapesList(int x, int y, boolean isTextIncluded) {
        List<Shape> orderElements = new ArrayList<>();
                for (Shape shape : this.<Shape>getShapes()) {
                    if (isTextIncluded && shape instanceof Textable) {                   
                        if(((Textable)shape).isClickedTexture(x, y)){ 
                          orderElements.add(0,shape);
                          continue;
                        }
                    }
                    if(shape.isClicked(x, y)){
                       orderElements.add(shape);
                       continue; 
                    }                    
                    
                }        
                return orderElements;
        
    }
    
    private Comparator<Shape> clickedShapesComparator=new Comparator<Shape>(){
        @Override
        public int compare(Shape o1, Shape o2) {
                    if(o1.getOwningUnit() instanceof CompositeLayerable){
                         //both on same side
                          Layer.Side s1=Layer.Side.resolve(o1.getCopper().getLayerMaskID());
                          Layer.Side s2=Layer.Side.resolve(o2.getCopper().getLayerMaskID());
                          Layer.Side active=((CompositeLayerable)o1.getOwningUnit()).getActiveSide();
                          //active layer has presedense
                          if(s1!=s2){
                             if(s1==active){
                                  return -1;
                              }else{
                                  return 1;
                              }
                           }
                    }
                                
                    if ((o1.getOrderWeight() - o2.getOrderWeight()) == 0)
                        return 0;
                    if ((o1.getOrderWeight() - o2.getOrderWeight()) > 0)
                        return 1;
                    else
                        return -1;
        }
    };
    
    public S getClickedShape(int x, int y, boolean isTextIncluded) {
        List<Shape> clickedShapes = buildClickedShapesList(x,y,isTextIncluded);
        if(clickedShapes.size()==0){
            return null;
        }
        //Text?
        if (clickedShapes.get(0) instanceof Textable) {   
            if(isShapeVisibleOnLayers(clickedShapes.get(0))){ 
             //if(((Textable)clickedShapes.get(0)).getChipText().isClicked(x, y)){              
              return (S)clickedShapes.get(0);
             //}
            }
        }

        Collections.sort(clickedShapes,clickedShapesComparator);
        for(Shape shape:clickedShapes){
            if(!isShapeVisibleOnLayers(shape)){             
               continue;              
            }
            
            return (S)shape;
        }
        return null;  
    }
//    protected List<ClickableOrderItem> buildClickableOrderItem(int x, int y, boolean isTextIncluded) {
//        List<ClickableOrderItem> orderElements = new ArrayList<ClickableOrderItem>();
//        int index = 0;
//        for (Shape shape : getShapes()) {
//            if (isTextIncluded && shape instanceof Textable) {                   
//                if(((Textable)shape).getChipText().isClicked(x, y)){ 
//                  orderElements.add(new ClickableOrderItem(index, 0,shape.getCopper().getLayerMaskID()));
//                }
//            }
//            if(!shape.isClicked(x, y)){
//               index++;
//               continue; 
//            }
//            //***give selected a higher priority
//            orderElements.add(new ClickableOrderItem(index,
//                                                     (shape.isSelected() && shape.getOrderWeight() > 1 ? 2 : shape.getOrderWeight()),shape.getCopper().getLayerMaskID()));
//
//            index++;
//        }
//
//        return orderElements;
//    }
      
    
//    public S getClickedShape(int x, int y, boolean isTextIncluded) {
//        List<ClickableOrderItem> orderedElements = buildClickableOrderItem(x,y,isTextIncluded);
//        Collections.sort(orderedElements, new Comparator<ClickableOrderItem>() {
//                public int compare(ClickableOrderItem o1, ClickableOrderItem o2) {
//                    if(Unit.this instanceof CompositeLayerable){
//                       //both on same side
//                        Layer.Side s1=Layer.Side.resolve(o1.getLayerMaskID());
//                        Layer.Side s2=Layer.Side.resolve(o2.getLayerMaskID());
//                        Layer.Side active=((CompositeLayerable)Unit.this).getActiveSide();
//                        //active layer has presedense
//                        if(s1!=s2){
//                            if(s1==active){
//                               return -1;
//                            }else{
//                               return 1;
//                            }
//                        }
//                    }
//                    
//                    if ((o1.getOrderWeight() - o2.getOrderWeight()) == 0)
//                        return 0;
//                    if ((o1.getOrderWeight() - o2.getOrderWeight()) > 0)
//                        return 1;
//                    else
//                        return -1;
//                }
//            });
//
//        for (ClickableOrderItem orderedElement : orderedElements) {
//            S shape = shapes.get(orderedElement.getElementIndex());
//            if(!isShapeVisibleOnLayers(shape)){             
//                continue;              
//            }
//            //***could be textable
//            if ((shape instanceof Textable) && (orderedElement.getOrderWeight() == 0)) {
//                Texture texture = ((Textable)shape).getChipText().getClickedTexture(x, y);
//                if (texture != null) {
//                    return shape;
//                }
//            } else {
//                /*
//                buildClickableOrderItem garantees that shape is clicked
//                */
//                //if (shape.isClicked(x, y)) {
//                    return shape;
//                //}
//            }
//        }
//
//        return null;
//    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ScalableTransformation getScalableTransformation() {
        return scalableTransformation;
    }

    public Grid getGrid() {
        return grid;
    }
    
    public Ruler getRuler(){
        return ruler;
    }
    /*
     * Prepare printing
     */
    @Override
    public void prepare(PrintContext context) {     
    }
    @Override
    public int getNumberOfPages(){
        return 1;
    }
    /*
     * Finish printing
     */
    @Override
    public void finish() {     
    }
    
    public void export(String fileName,PrintContext context)throws IOException{
        
    }
    
    public BufferedImage getImage(Box clipRect, AffineTransform transformation,Color background) {

        int width = (int)(getWidth() * transformation.getScaleX());
        int height = (int)(getHeight() * transformation.getScaleX());

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
        //***draw background
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fillRect(0, 0, width, height);
        ViewportWindow vw = new ViewportWindow(0, 0, width, height);
        for (S shape : shapes) {
            shape.paint(g2, vw, transformation,-1);
        }
        try{
        bi =bi.getSubimage((Double.compare(clipRect.getX(),0) < 0 ? 0 : (int)clipRect.getX()), 
                           (Double.compare(clipRect.getY(),0) < 0 ? 0 : (int)clipRect.getY()),
                           (Double.compare(width,clipRect.getWidth()) < 0?width:(int)clipRect.getWidth()),
                           (Double.compare(height,clipRect.getHeight()) < 0?height:(int)clipRect.getHeight()));
        }catch(Exception e){            
            e.printStackTrace();
        }

        g2.dispose();
        return bi;

    }
    /*
    @Override
    public AbstractMemento getState(MementoType operationType){
//        switch(operationType){
//          case ROTATE_UNIT_MEMENTO:
//            Memento memento= new Memento(operationType); 
//            memento.saveStateFrom(this);
//            return memento; 
//        default:
           return new CompositeMemento(operationType).add(shapes);
//        }        
    }
    @Override
    public void setState(AbstractMemento memento){        
//        switch(memento.getMementoType()){
//         case ROTATE_UNIT_MEMENTO:
//            ((Memento)memento).loadStateTo(this);       
//            break;
//        default:  
          CompositeMemento compositeMemento=(CompositeMemento)memento;
          List<AbstractMemento> list=compositeMemento.getMementoList();
          for(AbstractMemento amemento:list){
              Shape shape = this.getShape(amemento.getUUID());
              shape.setState(amemento);           
          }
//        }
    }
*/
    public void paint(Graphics2D g2, ViewportWindow viewportWindow) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (S shape : shapes) {
            shape.paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation(),Layer.LAYER_ALL);
        }
        grid.Paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation());
        //coordinate system
        coordinateSystem.paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation(),Layer.LAYER_ALL);
        //ruler
        ruler.paint(g2, viewportWindow,  scalableTransformation.getCurrentTransformation(),Layer.LAYER_ALL);
        //frame
        frame.paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation(),Layer.LAYER_ALL);
                
    }

    @Override
    public void fireShapeEvent(ShapeEvent e) {
        Object[] listeners = shapeListeners.getListenerList();
        int numListeners = listeners.length;
        for (int i = 0; i < numListeners; i += 2) {
            if (listeners[i] == ShapeListener.class) {
                switch (e.getEventType()) {
                case ShapeEvent.ADD_SHAPE:
                    ((ShapeListener)listeners[i + 1]).addShapeEvent(e);
                    break;
                case ShapeEvent.SELECT_SHAPE:
                    ((ShapeListener)listeners[i + 1]).selectShapeEvent(e);
                    break;
                case ShapeEvent.DELETE_SHAPE:
                    ((ShapeListener)listeners[i + 1]).deleteShapeEvent(e);
                    break;
                case ShapeEvent.PROPERTY_CHANGE:
                    ((ShapeListener)listeners[i + 1]).propertyChangeEvent(e);
                    break;
                case ShapeEvent.RENAME_SHAPE:
                    ((ShapeListener)listeners[i + 1]).renameShapeEvent(e);
                    break;

                }
            }
        }
    }

    @Override
    public void addShapeListener(ShapeListener listener) {
        shapeListeners.add(ShapeListener.class, listener);
    }

    @Override
    public void removeShapeListener(ShapeListener listener) {
        shapeListeners.remove(ShapeListener.class, listener);
    }
    
    protected abstract void parseClipboardSelection(String xml) throws XPathExpressionException,ParserConfigurationException,
                                                   SAXException, IOException;
    
    public void realizeClipboardContent(Transferable transferable) {
        if(transferable==null){
           return; 
        }
        try {
            parseClipboardSelection((String)transferable.getTransferData(DataFlavor.stringFlavor));
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    
    protected abstract StringBuffer format(Collection<S> shapes);
    
    public Transferable createClipboardContent() {         
            StringBuffer xml=new StringBuffer();        
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
            Collection<S> list=this.getSelectedShapes(false);
            if(list.size()>0){
               xml.append("<clipboard>");
               xml.append(format(list));
                xml.append("</clipboard>");
               return new StringSelection(xml.toString());
            }else{
               return null;
            }            
    }
    
    public UndoProvider getUndoProvider(){
        return undoProvider;
    }
    public void registerMemento(AbstractMemento memento) {        
        undoProvider.registerMemento(memento);
    }
    @Override
    public boolean undo(UndoCallback undocallback) {
        AbstractMemento memento=null;
        //***skip duplicates
        while(true){
          memento = undoProvider.Undo();  
        //***CHECK the validity of a memento   
          if (memento == null) {
            return false;
          }else{
              if(memento.getMementoType().equals(MementoType.CREATE_MEMENTO)||memento.getMementoType().equals(MementoType.DELETE_MEMENTO)){
                  break;
              }
              //***eigther composite or single memento
              if(memento.isSameState(this)){                
                 continue; 
              }else{
                 break;  
              }                  
          }
        }      
           
        switch (memento.getMementoType()) {
        case CREATE_MEMENTO:
            //***delete it
            {
            Collection<AbstractMemento> mementos;
            
            if(memento instanceof CompositeMemento){
              mementos=((CompositeMemento)memento).getMementoList();     
            }else{
              mementos=Collections.singletonList(memento);     
            }
                
            for(AbstractMemento state:mementos){
                this.delete(state.getUUID());                                 
            }
            }
            if(undocallback!=null){
               undocallback.onUndo();
            }
            break;
        case DELETE_MEMENTO:
            //***create it
            {
            Collection<AbstractMemento> mementos;
            if(memento instanceof CompositeMemento){
              mementos=((CompositeMemento)memento).getMementoList();     
            }else{
              mementos=Collections.singletonList(memento);     
            }
                
            for(AbstractMemento state:mementos){
                S element=(S)shapeFactory.createShape(state);
                this.add(element);                 
                fireShapeEvent(new ShapeEvent(element, ShapeEvent.SELECT_SHAPE));
            }   
            }
            break;
//        case  ROTATE_UNIT_MEMENTO:
//            this.setState(memento);             
//            return true;
        default:
            if(memento instanceof CompositeMemento){
               //this.setState(memento); 
                ((CompositeMemento)memento).loadStateTo(this);
            }else{
               Shape element = this.getShape(memento.getUUID());
               element.setState(memento);
               fireShapeEvent(new ShapeEvent(element, ShapeEvent.PROPERTY_CHANGE));
            
            }
        }
        
        return false;
    }
    
    //Is resizing required
    public boolean redo() {
        AbstractMemento memento=null;
        //***skip duplicates
        while(true){
            memento = undoProvider.Redo();
            if (memento == null) {
              return false;
            }else{                
                if(memento.getMementoType().equals(MementoType.CREATE_MEMENTO)||memento.getMementoType().equals(MementoType.DELETE_MEMENTO)){
                    break;
                }
                //***eigther composite or single memento
                if(memento.isSameState(this)){
                   continue; 
                }else{
                   break;  
                }                 
            }            
        }    
            
        switch (memento.getMementoType()) {
        case CREATE_MEMENTO:
            //***create it
            {
            Collection<AbstractMemento> mementos;
            if(memento instanceof CompositeMemento){
              mementos=((CompositeMemento)memento).getMementoList();     
            }else{
              mementos=Collections.singletonList(memento);     
            }
                
            for(AbstractMemento state:mementos){
                S shape=(S)shapeFactory.createShape(state);
                this.add(shape);                 
                fireShapeEvent(new ShapeEvent(shape, ShapeEvent.SELECT_SHAPE));
            }   
            }
            break;
        case DELETE_MEMENTO:
            //***delete it
            {
            Collection<AbstractMemento> mementos;
            
            if(memento instanceof CompositeMemento){
              mementos=((CompositeMemento)memento).getMementoList();     
            }else{
              mementos=Collections.singletonList(memento);     
            }
                
            for(AbstractMemento state:mementos){
                this.delete(state.getUUID());                                 
            }
            }
            break;
//        case  ROTATE_UNIT_MEMENTO:
//            this.setState(memento); 
//            return true;
        default:            
            if(memento instanceof CompositeMemento){
               //this.setState(memento); 
               ((CompositeMemento)memento).loadStateTo(this);
            }else{
                Shape element = this.getShape(memento.getUUID());
                element.setState(memento);
                fireShapeEvent(new ShapeEvent(element, ShapeEvent.PROPERTY_CHANGE));
            }
            
        }
       return false;
    } 

}
