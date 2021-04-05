package com.mynetpcb.d2;

import com.mynetpcb.d2.shapes.BaseFontText;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.FontText;
import com.mynetpcb.d2.shapes.GeometricFigure;
import com.mynetpcb.d2.shapes.Obround;
import com.mynetpcb.d2.shapes.Point;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
           FontText t1=new FontText(900,400,"Sergio Leonq",0,10);
           d2Component.add(t1);
           fontSize.addChangeListener((e) -> {t1.setSize(fontSize.getValue());
                                              d2Component.repaint(); }); 
           
           angle.addChangeListener((e) -> {t1.rotate(angle.getValue(),t1.anchorPoint);
                                              d2Component.repaint(); }); 
           
           BaseFontText bfr=new BaseFontText(500,400,"Basic Sergio Leonq",0,24,Font.BOLD);
           d2Component.add(bfr);
           
           BaseFontText bfl=new BaseFontText(500,400,"Basic Sergio Leonq",2,24,Font.BOLD);
           d2Component.add(bfl);
           
           BaseFontText bft=new BaseFontText(150,400,"Basic Sergio Leonq",1,24,Font.BOLD);
           d2Component.add(bft);

           BaseFontText bfb=new BaseFontText(150,400,"Basic Sergio Leonq",3,24,Font.BOLD);
           d2Component.add(bfb);           
//           
//           Circle circle=new Circle(new Point(100,100),20);
//           d2Component.add(circle);
//           
//           Circle copy=circle.clone();
//           copy.rotate(10,new Point(0,0));
//           d2Component.add(copy);
//           
//           Arc arc=new Arc(new Point(150,100),20,0,80);
//           d2Component.add(arc);
//           
//           Arc arc1=arc.clone();
//           arc1.rotate(10);
//           d2Component.add(arc1);
//           
//           Polygon polygon=new Polygon();
//           polygon.points.add(new Point(200,100));
//           polygon.points.add(new Point(250,100));
//           polygon.points.add(new Point(250,50));           
//           d2Component.add(polygon);
//           
//           Polygon polygon1=polygon.clone();
//           polygon1.rotate(10);
//           d2Component.add(polygon1);
//           
//           Hexagon hexagon=new Hexagon(300,100,70);
//           hexagon.rotate(30,hexagon.pc);
//           d2Component.add(hexagon);
//           Hexagon hexagon1=hexagon.clone();
//           hexagon1.rotate(10);
//           d2Component.add(hexagon1);
//           
//           Line line=new Line(new Point(380,100),new Point(430,100));        
//           d2Component.add(line);
//           Line line1=line.clone();
//           line1.rotate(2);
//           d2Component.add(line1);
//           
           Obround obround=new Obround(new Point(500,100),50,20);        
           d2Component.add(obround);
           Obround obround1=obround.clone();
           obround1.rotate(10,new Point(0,0));
           d2Component.add(obround1);
           
           Box b=obround.box();
           d2Component.add(b);
//           
//           RoundRectangle roundRect=new RoundRectangle(600, 100, 80, 40, 9);                      
//           d2Component.add(roundRect);
//           
//           RoundRectangle roundRect1=roundRect.clone();
//           roundRect1.rotate(10);
//           d2Component.add(roundRect1);
//       
//           
//           Ellipse e=new Ellipse(130, 200, 80, 40);   
//           e.rotate(30);
//           d2Component.add(e);
//           
//           Ellipse e1=e.clone();           
//           e1.rotate(0);
//           d2Component.add(e1);
//           
//           
//           Arcellipse a=new Arcellipse(320, 200, 80, 40);
//           a.rotate(46);
//           d2Component.add(a);
//           
//           Arcellipse a1=a.clone();           
//           a1.rotate(0);
//           d2Component.add(a1);
           
       
       }
       public static void main(String[] args) {
          d2 ex = new d2();
          ex.setVisible(true);
       }
       
       private static class D2Component extends JComponent{
           Collection<GeometricFigure> shapes=new ArrayList<>();
           
           D2Component(){
               addMouseListener(new MouseListener(){

                @Override
                public void mouseClicked(MouseEvent e) {
                    shapes.forEach(s->{
                        if(s.contains(new Point(e.getPoint().x,e.getPoint().y)))
                        System.out.println(s);        
                    });
                
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    // TODO Implement this method
                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                    // TODO Implement this method
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    // TODO Implement this method
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    // TODO Implement this method
                }
            });
           }
           void add(GeometricFigure shape){
               shapes.add(shape);
           }
           @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g;
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            
            g2.setColor(Color.BLUE);
            for(GeometricFigure shape:shapes){
                shape.paint(g2,false);
            }
            
        }
          
       }
}
