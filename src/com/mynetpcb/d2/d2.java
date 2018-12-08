package com.mynetpcb.d2;

import com.mynetpcb.d2.shapes.FontText;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Shape;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.Graphics;

import java.awt.Graphics2D;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class d2 extends  JFrame {
    private D2Component d2Component=new D2Component();
    JSlider fontSize = new JSlider(JSlider.HORIZONTAL,
                                          8, 100, 8);
    JSlider angle = new JSlider(JSlider.HORIZONTAL,
                                          0,360, 0);
    public d2(){
          setTitle("D2");
          setSize(1200, 800);
          buildUI();
          addShapes();
          setLocationRelativeTo(null);
          setDefaultCloseOperation(EXIT_ON_CLOSE);
       }
       private void buildUI(){
           Container pane = getContentPane();
           JPanel panel=new JPanel(new BorderLayout());
           JPanel header=new JPanel(new FlowLayout());
           panel.add(header, BorderLayout.PAGE_START);
           JButton one=new JButton("ONE");
           header.add(one);
           
           JButton two=new JButton("TWO");
           header.add(two);
            
           header.add(fontSize);
           header.add(angle);
           
           panel.add(d2Component, BorderLayout.CENTER);
           pane.add(panel);
       }
       private void addShapes(){
           FontText t1=new FontText(new Point(900,400),"Sergio Leonq",8);
           d2Component.add(t1);
           fontSize.addChangeListener((e) -> {t1.setSize(fontSize.getValue());
                                              d2Component.repaint(); }); 
           
           angle.addChangeListener((e) -> {t1.rotate(angle.getValue(),null);
                                              d2Component.repaint(); }); 
           
           Point p1=new Point(100,100);                    
           d2Component.add(p1);
           Point p2=p1.clone();
           p2.rotate(90, new Point(10,100));
           d2Component.add(p2);
       }
       public static void main(String[] args) {
          d2 ex = new d2();
          ex.setVisible(true);
       }
       
       private static class D2Component extends JComponent{
           Collection<Shape> shapes=new ArrayList<>();
           
           D2Component(){
            
           }
           void add(Shape shape){
               shapes.add(shape);
           }
           @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g;
                        
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
 
            for(Shape shape:shapes){
                shape.paint(g2);
            }
            
        }
//           @Override
//           public Dimension getPreferredSize( ) {
//               return new Dimension(100, 100);
//             }           
       }
}
