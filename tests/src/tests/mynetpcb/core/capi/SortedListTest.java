package tests.mynetpcb.core.capi;

import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.SortedList;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Pad;

import java.awt.Point;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SortedListTest {

    @Test
    public void testSortedListForFootprintAdd(){
       
       List<Shape> list=new SortedList<>();
       
       Pad pad=new Pad();
       pad.setCopper(Layer.Copper.FCu);
       list.add(pad);
       Assert.assertTrue(list.size()==1);
       
       Arc arc=new Arc();
       list.add(arc);
        
       arc=new Arc();
       list.add(arc);
       
       pad=new Pad();
       pad.setCopper(Layer.Copper.BCu);
       list.add(pad);

       pad=new Pad();
       pad.setCopper(Layer.Copper.All);
       list.add(pad);

       pad=new Pad();
       pad.setCopper(Layer.Copper.FCu);
       list.add(pad);
       
       Assert.assertTrue(list.get(0).getCopper().getLayerMaskID()==Layer.LAYER_BACK);
       Assert.assertTrue(list.get(1).getCopper().getLayerMaskID()==Layer.LAYER_FRONT);
       Assert.assertTrue(list.get(2).getCopper().getLayerMaskID()==Layer.LAYER_FRONT);
       Assert.assertTrue((list.get(3).getCopper().getLayerMaskID()&(Layer.LAYER_FRONT|Layer.LAYER_BACK))>0);
       Assert.assertTrue(list.get(4).getCopper().getLayerMaskID()==Layer.SILKSCREEN_LAYER_FRONT); 
       Assert.assertTrue(list.get(5).getCopper().getLayerMaskID()==Layer.SILKSCREEN_LAYER_FRONT); 
    }
    
    @Test
    public void testSortedListForFootprintReorder(){
        List<Shape> list=new SortedList<>();
        
        Pad pad=new Pad();
        pad.setCopper(Layer.Copper.FCu);
        list.add(pad);
        Assert.assertTrue(list.size()==1);
        
        Arc arc=new Arc();
        list.add(arc);
         
        arc=new Arc();
        arc.setCopper(Layer.Copper.BCu);
        list.add(arc);
        
        pad=new Pad();
        pad.setCopper(Layer.Copper.BCu);
        list.add(pad);

        Pad pad1=new Pad();
        pad1.setCopper(Layer.Copper.All);
        list.add(pad1);

        pad=new Pad();
        pad.setCopper(Layer.Copper.FCu);
        list.add(pad);
       
        pad1.setCopper(Layer.Copper.BCu);
        ((SortedList)list).reorder();
        
        Assert.assertTrue(list.get(0).getCopper().getLayerMaskID()==Layer.LAYER_BACK);
        Assert.assertTrue(list.get(1).getCopper().getLayerMaskID()==Layer.LAYER_BACK);
        Assert.assertTrue(list.get(2).getCopper().getLayerMaskID()==Layer.LAYER_FRONT);
        Assert.assertTrue(list.get(3).getCopper().getLayerMaskID()==Layer.LAYER_FRONT);
        Assert.assertTrue(list.get(4).getCopper().getLayerMaskID()==Layer.LAYER_BACK); 
        Assert.assertTrue(list.get(5).getCopper().getLayerMaskID()==Layer.SILKSCREEN_LAYER_FRONT); 
        //list.stream().forEach(e->System.out.println(e.getDisplayName()+ "::"+e.getCopper())); 
        
    }
    
    @Test
    public void testSortedListForBoard(){
      Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));
      PCBTrack tracktop=new PCBTrack(Grid.MM_TO_COORD(0.4),Layer.Copper.FCu.getLayerMaskID());
      tracktop.addPoint(new Point(Grid.MM_TO_COORD(2),Grid.MM_TO_COORD(2)));
      tracktop.addPoint(new Point(Grid.MM_TO_COORD(4),Grid.MM_TO_COORD(2)));
      tracktop.addPoint(new Point(Grid.MM_TO_COORD(4),Grid.MM_TO_COORD(3)));      
      board.Add(tracktop);
      
      PCBTrack trackbottom=new PCBTrack(Grid.MM_TO_COORD(0.4),Layer.Copper.BCu.getLayerMaskID());
      trackbottom.addPoint(new Point(Grid.MM_TO_COORD(2),Grid.MM_TO_COORD(2)));
      trackbottom.addPoint(new Point(Grid.MM_TO_COORD(4),Grid.MM_TO_COORD(4)));
      trackbottom.addPoint(new Point(Grid.MM_TO_COORD(4),Grid.MM_TO_COORD(5)));
      trackbottom.addPoint(new Point(Grid.MM_TO_COORD(7),Grid.MM_TO_COORD(7)));
      board.Add(trackbottom);  
    
      PCBCopperArea topcoper=new PCBCopperArea(Layer.Copper.FCu.getLayerMaskID());
      topcoper.addPoint(new Point(Grid.MM_TO_COORD(3),Grid.MM_TO_COORD(1)));
      topcoper.addPoint(new Point(Grid.MM_TO_COORD(3),Grid.MM_TO_COORD(9)));
      topcoper.addPoint(new Point(Grid.MM_TO_COORD(9),Grid.MM_TO_COORD(9)));
      topcoper.addPoint(new Point(Grid.MM_TO_COORD(9),Grid.MM_TO_COORD(1)));
      board.Add(topcoper);
      
      PCBCopperArea bottomcoper=new PCBCopperArea(Layer.Copper.BCu.getLayerMaskID());
      bottomcoper.addPoint(new Point(Grid.MM_TO_COORD(3),Grid.MM_TO_COORD(1)));
      bottomcoper.addPoint(new Point(Grid.MM_TO_COORD(3),Grid.MM_TO_COORD(9)));
      bottomcoper.addPoint(new Point(Grid.MM_TO_COORD(9),Grid.MM_TO_COORD(9)));
      bottomcoper.addPoint(new Point(Grid.MM_TO_COORD(9),Grid.MM_TO_COORD(1)));
      board.Add(bottomcoper);
      
      board.getShapes().stream().forEach(e->System.out.println(e.getDisplayName()+ "::"+e.getCopper())); 
     
      board.setActiveSide(Layer.Side.BOTTOM);
      board.getShapes().stream().forEach(e->System.out.println(e.getDisplayName()+ "::"+e.getCopper())); 
      
    }
}
