package com.mynetpcb.core.capi.gui;


import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.ImageIcon;


public class UnitSelectionGrid {
    
    private UnitContainer model;
    
    private int width,height;
    
    private BufferedImage bufferedImage;
    
    private final Font textFont;
    
    private final Map<UUID, UnitSelectionCell> map;
    
    private Color backgroundColor,textColor;
    
    double scaleRatio;
    
    int scaleFactor;
    
    int minScaleFactor;
    
    int maxScaleFactor;
    
    boolean enabled;
                                                                 
    public UnitSelectionGrid(UnitContainer model) {
      this.textFont=new Font("Palatino Linotype", Font.ITALIC+Font.BOLD, 12);
      this.model=model;
      this.backgroundColor=Color.BLACK;
      this.textColor=Color.WHITE;
      this.scaleRatio=0.5;this.scaleFactor=10;minScaleFactor=4;maxScaleFactor=13;
      this.map =new LinkedHashMap<UUID, UnitSelectionCell>();
    }
    
    public UnitContainer getModel(){
       return model;  
    }
    public void setModel(UnitContainer model){
       this.model=model;  
    }
    public void paint(Graphics2D g2){
        if(bufferedImage!=null){
          g2.drawImage(bufferedImage,null,0,0);    
        }        
    }
    public int getWidth(){
      return width;  
    }
    
    public int getHeight(){
       return height; 
    }
    public boolean processClick(MouseEvent e){
        UnitSelectionCell tmpRenderElement = null;
        for (Map.Entry<UUID, UnitSelectionCell> entry : map.entrySet()) {
            if (entry.getValue().checkboxRect.contains(e.getPoint()) ||
                entry.getValue().unitRect.contains(e.getPoint())) 
              //if(!entry.getValue().selected)
              {
                
                if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                    entry.getValue().selected = true;
                    getModel().setActiveUnit(entry.getKey());
                }
                if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                    entry.getValue().selected = false;
                    getModel().setActiveUnit(null);
                }
                tmpRenderElement = entry.getValue();
                break;
            }
        }

        if (tmpRenderElement == null)
            return false;
        //set the rest to uncheck
        for (UnitSelectionCell renderElement : map.values()) {
            if (tmpRenderElement == renderElement)
                continue;
            renderElement.selected = false;
        }  
        this.paint();
        return true;
    }
    public void clear() {
        if(model!=null)
          model.clear();
        width=0;
        height=0;
        map.clear(); 
        bufferedImage=null;
    }
    public void release() {
        this.clear();
        if(model!=null){
          model.release();
          model=null;
        }  
    }
    public void build(){
        int x=0,y=0;
        Collection<Unit> units=getModel().getUnits();
        //1.set scale factor
        for(Unit unit:units){
            unit.getScalableTransformation().reset(scaleRatio,scaleFactor,minScaleFactor,maxScaleFactor);  
            width=Math.max(width,(int)Math.round(UnitSelectionCell.CHECKBOX_WIDTH+3 * UnitSelectionCell.OFFSET_X+unit.getBoundingRect().getWidth()*unit.getScalableTransformation().getCurrentTransformation().getScaleX()));

        }
        //2.find the biggest width, calculate height
        for(Unit unit:units){          
            int h=(int)Math.round(unit.getBoundingRect().getHeight()*unit.getScalableTransformation().getCurrentTransformation().getScaleY()) + UnitSelectionCell.CHECKBOX_HEIDTH + 2 * UnitSelectionCell.OFFSET_Y;             
            
            UnitSelectionCell element =
                new UnitSelectionCell(x, 
                                         height, 
                                         width, 
                                         h, 
                                         (int)(unit.getBoundingRect().getWidth()*unit.getScalableTransformation().getCurrentTransformation().getScaleX()),
                                         (int)(unit.getBoundingRect().getHeight()*unit.getScalableTransformation().getCurrentTransformation().getScaleY()), 
                                         (getModel().getUnit()==unit?true:false));
            
            map.put(unit.getUUID(), element);        
            height+=h; 
        }
        this.paint();
    }
    /**
     * Draw the grid on internal buffer canvas
     */
    private void paint(){
        if(bufferedImage==null||bufferedImage.getWidth()!=this.getWidth()||bufferedImage.getHeight()!=this.getHeight()){
            bufferedImage=new BufferedImage(this.getWidth(),this.getHeight(), BufferedImage.TYPE_BYTE_INDEXED);
        } 
        ImageIcon icon;
        Graphics2D g2=(Graphics2D)bufferedImage.getGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);  
      
        g2.setPaint(backgroundColor);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        for (Unit unit : (Collection<Unit>)getModel().getUnits()) {
              unit.setSelected(false);           
              UnitSelectionCell element = map.get(unit.getUUID());
              g2.setFont(textFont);
              g2.setPaint(textColor);
              g2.drawString(unit.getUnitName(),
                          element.nameRect.x,
                          element.nameRect.y +
                          element.nameRect.height);
          

            if (unit.getShapes().isEmpty()) {
                continue;
            }
            Box box=unit.getBoundingRect();
            
            box.scale(unit.getScalableTransformation().getCurrentTransformation().getScaleX());
            box.grow(UnitSelectionCell.OFFSET_X);
            
            //Rectangle2D rect = Utilities.getScaleRect(unit.getBoundingRect(), unit.getScalableTransformation().getCurrentTransformation());
            //Utilities.IncrementRect(rect, UnitSelectionCell.OFFSET_X, UnitSelectionCell.OFFSET_Y);

            BufferedImage image = unit.getImage(box,unit.getScalableTransformation().getCurrentTransformation(),backgroundColor);
            g2.drawImage(image, null, element.unitRect.x,
                         element.unitRect.y);

            if (this.enabled) {
             icon=(element.selected == true ? Utilities.loadImageIcon(this,
                                                                  "/com/mynetpcb/core/images/checkbox_checked.png") : Utilities.loadImageIcon(this,
                               "/com/mynetpcb/core/images/checkbox_unchecked.png"));
            if (element.selected) {  
               RoundRectangle2D selectionRect= new RoundRectangle2D.Double(element.unitRect.x,
                                                                    element.unitRect.y,
                                                                    element.unitRect.getWidth(),
                                                                    element.unitRect.getHeight(),
                                                                    8, 8);
                AlphaComposite composite =
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
                Composite originalComposite = g2.getComposite();
                g2.setPaint(Color.GRAY);
                g2.setComposite(composite);
                g2.fill(selectionRect);
                g2.setComposite(originalComposite);
                g2.setStroke(new BasicStroke(1));
                g2.draw(selectionRect);
            }
        }else {
            icon = Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/checkbox_checked_dis.png");
        }
        g2.drawImage(icon.getImage(),
                    element.checkboxRect.x,
                    (element.checkboxRect.y + element.checkboxRect.height - icon.getIconHeight()), null);
        }
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setScaleRatio(double scaleRatio) {
        this.scaleRatio = scaleRatio;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public void setMinScaleFactor(int minScaleFactor) {
        this.minScaleFactor = minScaleFactor;
    }

    public void setMaxScaleFactor(int maxScaleFactor) {
        this.maxScaleFactor = maxScaleFactor;
    }
    
    void setEnabled(boolean enabled){
      this.enabled=enabled;
    }
}
