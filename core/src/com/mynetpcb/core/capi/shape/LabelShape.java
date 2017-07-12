package com.mynetpcb.core.capi.shape;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/*
 * Base label class for all Labels in drawings
*/
public class LabelShape extends Shape{
    
    protected FontTexture texture;
    
    public LabelShape(int x,int y,int layermaskId) {
        super(x,y,0,0, -1,layermaskId);
        texture=new FontTexture("label","Label",x,y,Text.Alignment.LEFT,8);
        texture.setFillColor(Color.BLACK);
    }
    
    
    @Override
    public LabelShape clone()throws CloneNotSupportedException{
        LabelShape copy=(LabelShape)super.clone();
        copy.texture=this.texture.clone();
        return copy;
    }
    
    public Texture getTexture(){
        return this.texture;
    }
    
    @Override
    public void Clear() {
        texture.Clear();
    }
    
    @Override
    public void Move(int xoffset, int yoffset) {
        texture.Move(xoffset, yoffset);
    }

    @Override
    public void Mirror(Point A,Point B) {
        texture.Mirror(A,B);
    }

    @Override
    public void Translate(AffineTransform translate) {
        texture.Translate(translate);
    }

    @Override
    public void Rotate(AffineTransform rotation) {
      texture.Rotate(rotation);
    }

    @Override
    public void setLocation(int x, int y) {
      texture.setLocation(x, y);
    }
    @Override 
    public Rectangle calculateShape(){ 
     return texture.getBoundingShape();
    }
    @Override
    public void setSelected(boolean selected) {
        this.texture.setSelected(selected);
    }
    @Override
    public boolean isSelected() {
        return this.texture.isSelected();
    }
    @Override
    public boolean isClicked(int x, int y) {
        return texture.isClicked(x,y);
    }
    
    @Override
    public long getOrderWeight(){
        return 0;
    } 
    @Override
    public String getDisplayName(){
        return "Label";
    }
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(),scale);
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        texture.Paint(g2, viewportWindow, scale,layermask);
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        texture.Paint(g2, new ViewportWindow(0,0,0,0), AffineTransform.getScaleInstance(1, 1),layermask);
    }
}
