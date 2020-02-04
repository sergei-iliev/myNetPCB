package com.mynetpcb.board.unit;

import com.mynetpcb.board.shape.BoardShapeFactory;
import com.mynetpcb.core.board.Net;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.CompositeLayer;
import com.mynetpcb.core.capi.layer.CompositeLayerable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.layer.LayerOrderedList;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Line;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import javax.imageio.ImageIO;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;


public class Board extends Unit<Shape> implements CompositeLayerable {

    private CompositeLayerable compositeLayer;

    public Board(int width, int height) {
        super(width, height,new LayerOrderedList<>());
        this.shapeFactory = new BoardShapeFactory();
        this.grid.setGridUnits(0.8, Grid.Units.MM);
        this.grid.setPointsColor(Color.WHITE);
        this.frame.setFillColor(Color.WHITE);
        scalableTransformation.reset(0.5, 10, 3, 13);
       // this.getCoordinateSystem().setSelectionRectWidth(3000);
        this.compositeLayer = new CompositeLayer();

    }

    @Override
    public Board clone() throws CloneNotSupportedException {
        Board copy = (Board) super.clone();
        copy.shapeFactory = new BoardShapeFactory();
        copy.compositeLayer = new CompositeLayer();
        return copy;
    }
    public void selectNetAt(Net target){
        Queue<Net> targets = new LinkedList<>();        
        Collection<UUID> selectedShapes=new HashSet<>();
        targets.add(target);
        selectedShapes.add(((Shape)target).getUUID());
        
        while(!targets.isEmpty()){
            Net shape=targets.remove();
            Collection<Shape> list=shape.getNetShapes(selectedShapes);
            if(!list.isEmpty()){
                for(Shape item:list){
                    if(!selectedShapes.contains(item.getUUID())){
                        selectedShapes.add(item.getUUID());
                        targets.add((Net)item);
                    }
                }
            }
            
        }
        
        for(UUID uuid:selectedShapes){
            getShape(uuid).setSelected(true);
        }
    }
//    private Comparator<Shape> clickedShapesComparator=new Comparator<Shape>(){
//        @Override
//        public int compare(Shape o1, Shape o2) {
//                    if(o1.getOwningUnit() instanceof CompositeLayerable){
//                         //both on same side
//                          Layer.Side s1=Layer.Side.resolve(o1.getCopper().getLayerMaskID());
//                          Layer.Side s2=Layer.Side.resolve(o2.getCopper().getLayerMaskID());
//                          Layer.Side active=((CompositeLayerable)o1.getOwningUnit()).getActiveSide();
//                          //active layer has presedense
//                          if(s1!=s2){
//                             if(s1==active){
//                                  return -1;
//                              }else{
//                                  return 1;
//                              }
//                           }
//                    }
//                                
//                    if ((o1.getOrderWeight() - o2.getOrderWeight()) == 0)
//                        return 0;
//                    if ((o1.getOrderWeight() - o2.getOrderWeight()) > 0)
//                        return 1;
//                    else
//                        return -1;
//        }
//    };
    @Override
    protected List<Shape> buildClickedShapesList(int x, int y, boolean isTextIncluded) {
        List<Shape> orderElements = new ArrayList<>();
        
            for (int i = this.shapes.size(); i-- > 0; ) {            
                    if (isTextIncluded && shapes.get(i) instanceof Textable) {                   
                        if(((Textable)shapes.get(i)).isClickedTexture(x, y)){ 
                          orderElements.add(0,shapes.get(i));
                          continue;
                        }
                    }
                    if(shapes.get(i).isClicked(x, y)){
                        if(isShapeVisibleOnLayers(shapes.get(i))){
                         orderElements.add(shapes.get(i));                         
                        }
                    }                    
                    
                }        
                return orderElements;
        
    }
    @Override
    public Shape getClickedShape(int x, int y, boolean isTextIncluded) {
        List<Shape> clickedShapes = buildClickedShapesList(x,y,isTextIncluded);
        if(clickedShapes.size()==0){
            return null;
        }
        //Text?
        if (clickedShapes.get(0) instanceof Textable) {   
            if(((Textable)clickedShapes.get(0)).isClickedTexture(x, y)){ 
              return clickedShapes.get(0);             
            }
        }
        //Collections.reverse(clickedShapes);
        Shape result=null;
        for(Shape shape:clickedShapes){
            if(result==null){
                result=shape;
            }else if(shape.getDrawingLayerPriority()==result.getDrawingLayerPriority()){
                if(shape.getOrderWeight()<result.getOrderWeight()){
                    result=shape;
                }
            }
        
        }
        return result;  
    }
    @Override
    public StringBuffer format() {
        StringBuffer xml = new StringBuffer();
        xml.append("<board width=\"" + this.getWidth() + "\" height=\"" + this.getHeight() + "\">\r\n");
        xml.append("<units raster=\"" + this.getGrid().getGridValue() + "\">" + this.getGrid().getGridUnits() +
                   "</units>\r\n");
        xml.append("<name>" + this.getUnitName() + "</name>\r\n");
        
        xml.append(format(getShapes()));
        
        xml.append("</board>\r\n");        
        return xml;
    }

    @Override
    protected StringBuffer format(Collection<Shape> shapes) {
        StringBuffer xml = new StringBuffer();
        xml.append("<symbols>\r\n");

        for (Shape shape : shapes) {
            xml.append(((Externalizable) shape).toXML());

        }
        xml.append("</symbols>\r\n");
        return xml;
    }

    @Override
    public void parse(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element e = (Element) node;
        this.setSize(Integer.parseInt(e.getAttribute("width")), Integer.parseInt(e.getAttribute("height")));
        NodeList nlist = ((Element) node).getElementsByTagName("name");
        this.unitName = nlist.item(0).getTextContent();
        
        
        NodeList nodelist = ((Element) node).getElementsByTagName("units");
        this.getGrid().setGridUnits(Double.parseDouble(((Element) nodelist.item(0)).getAttribute("raster")),
                                    Grid.Units.MM);
        
        parseSymbols(node , false);
        this.getShapes().reorder();
    }

    private void parseSymbols(Node node, boolean selection) throws XPathExpressionException,
                                                                     ParserConfigurationException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        NodeList nodelist = (NodeList) xpath.evaluate("./symbols/*", node, XPathConstants.NODESET);
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node item = nodelist.item(i);
            Shape shape = this.shapeFactory.createShape(item);
            if (shape == null)
                continue;
            shape.setSelected(selection);
            this.add(shape);
            //any children
            NodeList subnodelist = (NodeList) xpath.evaluate("./children/*", item, XPathConstants.NODESET);
            for (int j = 0; j < subnodelist.getLength(); j++) {
                Node subitem = subnodelist.item(j);
                Shape child = this.shapeFactory.createShape(subitem);
                child.setSelected(selection);
                //((Ownerable) child).setOwner(shape);
                this.add(child);
            }
        }
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Shape shape : shapes) {
            shape.paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation(),
                        compositeLayer.getLayerMaskID());
        
        }
        for (Shape shape : shapes) {
            if (shape instanceof Resizeable) {
                ((Resizeable) shape).drawControlShape(g2, viewportWindow,
                                                      scalableTransformation.getCurrentTransformation());
            }
        }
        grid.Paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation());
        //coordinate system
        if(coordinateSystem!=null){
           coordinateSystem.paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation(), -1);
        }
        //ruler
        ruler.paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation(),
                    Layer.Copper.All.getLayerMaskID());
        //frame
        frame.paint(g2, viewportWindow, scalableTransformation.getCurrentTransformation(), -1);

    }
    @Override
    public void export(String fileName,PrintContext context)throws IOException{
                  double scale=(context.getCustomSizeRatio() / 254000d);
                  int width = (int)(this.getWidth() *scale);
                  int height = (int)(this.getHeight() * scale);            
                  BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);                    
                  
                  Graphics2D g2 = (Graphics2D)bi.getGraphics();
                 
                  g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                  g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                  g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                  g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                  g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                  g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
                  
                  context.setBackgroundColor(context.isBlackAndWhite()?Color.WHITE:Color.BLACK);
                  
                  g2.setColor(context.getBackgroundColor());
                  g2.fillRect(0, 0, width, height);
                  
                  g2.scale(scale, scale);
                  for (Shape shape : printboard.getShapes()) {
                        shape.print(g2,context,context.getLayermaskId());
                  }
                  String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                  ImageIO.write(bi,ext,new File(fileName));                        
    }
    
    private Board printboard;
    private WeakReference<PrintContext> context;

    @Override
    public void prepare(PrintContext context) {
        this.printboard = new Board(0, 0);
        this.context = new WeakReference<>(context);
        
        //***white sheet of paper
        this.context.get().setBackgroundColor(Color.WHITE);
        for (Shape shape : shapes) {
            try {
                Shape copy = shape.clone();
                copy.setSelected(false);
                printboard.add(copy);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        if (this.context.get().isMirrored()) {
            //mirror
            BoardMgr.getInstance().mirrorBlock(printboard.getShapes(),new Line(0, -10, 0, +10));
            BoardMgr.getInstance().moveBlock(printboard.getShapes(), this.getWidth(), 0);
        }
    }

    @Override
    public int getNumberOfPages() {
        return 1;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) {
        if (page == 0) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            AffineTransform oldTransform = g2d.getTransform();
            g2d.scale((72d / 254000d), (72d / 254000d));
            for (Shape shape : printboard.getShapes()) {
                shape.print(g2d,context.get(),context.get().getLayermaskId());
            }
            //((Printaware) this.frame).print(g2d,context.get(),Layer.Copper.All.getLayerMaskID());
            g2d.setTransform(oldTransform);
            return PAGE_EXISTS;
        }

        //        if(page==0){
        //        Graphics2D g2d = (Graphics2D)g;
        //        g2d.translate(pf.getImageableX(), pf.getImageableY());
        //        AffineTransform oldTransform = g2d.getTransform();
        //        g2d.scale((72d / 254000d), (72d / 254000d));
        //        for (Shape shape : shapes) {
        //            shape.Print(g2d,Layer.LAYER_FRONT|Layer.SILKSCREEN_LAYER_FRONT);
        //        }
        //        ((Printaware)this.frame).Print(g2d,Layer.Copper.All.getLayerMaskID());
        //        g2d.setTransform(oldTransform);
        //
        //        return PAGE_EXISTS;
        //        }
        //        if(page==1){
        //            Graphics2D g2d = (Graphics2D)g;
        //            g2d.translate(pf.getImageableX(), pf.getImageableY());
        //            AffineTransform oldTransform = g2d.getTransform();
        //            g2d.scale((72d / 254000d), (72d / 254000d));
        //            for (Shape shape : mirrorboard.getShapes()) {
        //                shape.Print(g2d,Layer.LAYER_BACK|Layer.SILKSCREEN_LAYER_BACK);
        //            }
        //            //draw frame
        //            ((Printaware)this.frame).Print(g2d,Layer.Copper.All.getLayerMaskID());
        //            g2d.setTransform(oldTransform);
        //
        //            return PAGE_EXISTS;
        //        }

        return NO_SUCH_PAGE;
    }

    @Override
    public void finish() {
        context.clear();
        context = null;
        printboard.clear();
        printboard = null;
    }

    
    protected void parseClipboardSelection(String xml) throws XPathExpressionException, ParserConfigurationException,
                                                              SAXException, IOException {
        Node node =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();
        parseSymbols(node, true);

    }


    @Override
    public boolean isLayerVisible(int i) {
        return compositeLayer.isLayerVisible(i);
    }

    @Override
    public void setLayerVisible(int i, boolean flag) {
        compositeLayer.setLayerVisible(i, flag);
    }

    @Override
    public int getLayerMaskID() {
        return compositeLayer.getLayerMaskID();
    }

    @Override
    public void setLayerMaskID(int i) {

    }

    @Override
    public void setActiveSide(Layer.Side side) {
        this.compositeLayer.setActiveSide(side);
        this.shapes.reorder();
    }

    @Override
    public Layer.Side getActiveSide() {
        return compositeLayer.getActiveSide();
    }

    @Override
    public String toString() {
        return "board";
    }

}
