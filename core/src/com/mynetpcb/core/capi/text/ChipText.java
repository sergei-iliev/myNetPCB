package com.mynetpcb.core.capi.text;


import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.ViewportWindow;

import com.mynetpcb.d2.shapes.Line;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ChipText  implements Moveable{    

//    private final List<Texture> composite;
//    
//    public ChipText(){
//      composite=new ArrayList<Texture>(2);
//    }
//    
//    public List<Texture> getChildren(){
//      return composite;   
//    }
//    
//    public boolean Add(Texture texture){
//      texture.setID(composite.size()+1);
//      return composite.add(texture);      
//    }
//    public Texture get(int index){
//      return composite.get(index);  
//    }
//    
//    public boolean isEmpty(){
//          for(Texture texture:composite){
//              if(!texture.isEmpty()){
//                 return false; 
//              }
//          }    
//          return true;
//    }
//    
//    @Override
//    public void Move(int xOffset, int yOffset) {
//        for(Texture texture:composite){
//            texture.Move(xOffset,yOffset); 
//        }         
//    }
//    @Override
//    public void Rotate(AffineTransform rotation) {
//        for(Texture texture:composite){
//           texture.Rotate(rotation);  
//        }
//    }
//    @Override
//    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
//        
//        for(Texture texture:composite){
//           texture.Paint(g2,viewportWindow,scale,layermask);   
//        }
//    }
//
//    public void Mirror(Point A,Point B) {
//        for(Texture texture:composite){
//           texture.Mirror(A,B);
//        }
//    }
//
//    @Override
//    public void Translate(AffineTransform transform) {
//        for(Texture texture:composite){
//           texture.Translate(transform); 
//        }
//    }
//    @Override
//    public void setLocation(int x, int y) {
//        throw new RuntimeException("NO WAY");
////        for(Texture text:composite)
////           text.setLocation(x,y);     
//    }
//
//    @Override
//    public long getOrderWeight() {
//         return 0;
//    }
//
//    public Texture getTextureByTag(String tag){
//        for(Texture texture:composite){
//            if(texture.getTag().equals(tag))
//             return texture;                                   
//        }
//        return null;         
//    }
//    
//    public Texture getClickedTexture(int x, int y) {
//        for(Texture texture:composite){
//          if ((texture.getBoundingShape() != null) && (texture.getBoundingShape().contains(x, y)))
//            return texture;        
//        }
//        return null;    
//    }
//    
//    @Override
//    public boolean isClicked(int x, int y) {
//        for(Texture texture:composite){
//            if(texture.isClicked(x, y))
//                return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean isInRect(Rectangle r) {
//        return false;
//    }
//
//    @Override
//    public Rectangle getBoundingShape() {
//        //***sum up rectangles
//        Rectangle r=null;
//        int x1=Integer.MAX_VALUE,y1=Integer.MAX_VALUE,x2=0,y2=0;
//        
//            for(Texture text:composite){
//                Rectangle tmp=text.getBoundingShape();
//                if(tmp!=null){
//                    x1=Math.min(x1,tmp.x);
//                    y1=Math.min(y1,tmp.y);
//                    x2=Math.max(x2,tmp.x+tmp.width);
//                    y2=Math.max(y2,tmp.y+tmp.height);                   
//                    if(r==null){
//                      r=new Rectangle();  
//                    }
//                } 
//            }
//        if(r!=null){
//          r.setRect(x1,y1,x2-x1,y2-y1);
//        }
//        return r;
//    }
//
//    @Override
//    public void setSelected(boolean isSelected) {
//        for(Texture text:composite)
//           text.setSelected(isSelected); 
//    }
//    @Override
//    public boolean isSelected() {
//        return composite.get(0).isSelected();
//    }
//    public boolean isTextLayoutVisible() {
//        return composite.get(0).isTextLayoutVisible();
//    }
//
//    public void setTextLayoutVisible(boolean visible) {
//        for(Texture texture:composite)
//           texture.setTextLayoutVisible(visible); 
//    }
//    public void clear() {
//        for(Texture text:composite)
//           text.Clear();
//        
//        composite.clear();
//    }
//    
//    public ChipText clone(){             
//        ChipText clone =new ChipText();      
//        try{                                                           
//            for(Texture text:composite){
//               clone.Add(text.clone()); 
//            }       
//        }catch(CloneNotSupportedException cne){
//            cne.printStackTrace(System.out);
//                                               }
//        return clone;
//    }
//    @Override
//    public Color getFillColor(){
//        return null;
//    }
//    @Override
//    public void setFillColor(Color color){
//        for(Texture text:composite){
//           text.setFillColor(color);
//        }
//    }
//    public Point getCenter() {
//
//            return null;
//    }


    @Override
    public void move(double xoffset, double yoffset) {
        // TODO Implement this method

    }

    @Override
    public void mirror(Line line) {
        
    }
    @Override
    public void translate(AffineTransform translate) {
        // TODO Implement this method
    }

    @Override
    public void rotate(double angle,com.mynetpcb.d2.shapes.Point origin) {
        // TODO Implement this method
    }

    @Override
    public void setLocation(double x, double y) {
        // TODO Implement this method

    }

    @Override
    public com.mynetpcb.d2.shapes.Point getCenter() {
        // TODO Implement this method
        return null;
    }

    @Override
    public long getOrderWeight() {
        // TODO Implement this method
        return 0L;
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        // TODO Implement this method

    }

    @Override
    public boolean isClicked(int x, int y) {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean isInRect(com.mynetpcb.d2.shapes.Rectangle r) {
        // TODO Implement this method
        return false;
    }

    @Override
    public com.mynetpcb.d2.shapes.Box getBoundingShape() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void setSelected(boolean isSelected) {
        // TODO Implement this method
    }

    @Override
    public boolean isSelected() {
        // TODO Implement this method
        return false;
    }

    @Override
    public Color getFillColor() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void setFillColor(Color color) {
        // TODO Implement this method
    }

//    public static class Memento{
//       
//        private final List<Texture.Memento> textureMementos;
//        
//        public Memento(){
//            textureMementos=new ArrayList<Texture.Memento>(2);
//        }
//        
//        public void saveStateFrom(ChipText symbol){    
//           for(Texture text:symbol.getChildren()){
//               Texture.Memento memento=text.createMemento();
//               memento.saveStateFrom(text);
//               textureMementos.add(memento);
//           }
//        }
//        
//        public void loadStateTo(ChipText symbol) {
//            for(Texture text:symbol.getChildren()){
//               Texture.Memento memento=getMementoByID(text.getID());            
//               memento.loadStateTo(text); 
//            }
//        }
//
//        public void Clear() {
//            textureMementos.clear();
//        }
//        
//        @Override
//        public boolean equals(Object obj){
//            ChipText.Memento that=(ChipText.Memento)obj;
//            
//            for(Texture.Memento memento:textureMementos){
//                Texture.Memento thatMemento=that.getMementoByID(memento.getID());
//                if(thatMemento==null){
//                  return false;   
//                }    
//                if(!thatMemento.equals(memento)){
//                  return false;
//                }                                     
//            }
//            return true;   
//        }
//        
//        @Override
//        public int hashCode(){
//          int hash=1;
//          for(Texture.Memento memento:textureMementos){
//              hash = hash * 31+memento.hashCode();
//          }
//          return hash;  
//        }
//        
//        private Texture.Memento getMementoByID(int ID){
//           for(Texture.Memento memento:textureMementos){
//                if(memento.getID()==ID){
//                  return memento;  
//                }
//           }
//           throw new IllegalStateException("Unable to find texture by id="+ID); 
//        }
//    
//
//    }    
}


