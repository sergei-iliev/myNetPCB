package tests.mynetpcb.board;

import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.board.unit.BoardMgr;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.unit.Footprint;
import com.mynetpcb.pad.unit.FootprintMgr;

import java.awt.Rectangle;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class BoardTest {


    private SortedSet<Shape> shapesToPrint=new TreeSet<Shape>(new Comparator<Shape>(){
        
        /*
         * wires must be printed before
         */
        @Override
        public int compare(Shape s1, Shape s2) {
            if(s1 instanceof PCBFootprint){
              return 1;  
            }else if(s2 instanceof PCBFootprint){
                return -1;
            }
            return -1;
        }
    });  
@Test
public void testBoardPrinting(){
      Board board=new Board(1000,1000);
      PCBFootprint footprint =new PCBFootprint(0);
      board.Add(footprint);
      PCBTrack wire=new PCBTrack(0,0);      
      board.Add(wire);
      footprint =new PCBFootprint(0);
      board.Add(footprint);
      wire=new PCBTrack(0,0);      
      board.Add(wire);
      
      shapesToPrint.addAll(board.getShapes());
      
      Object[] a=shapesToPrint.toArray();
      Assert.assertTrue(a[0] instanceof PCBTrack);
      Assert.assertTrue(a[1] instanceof PCBTrack);
      Assert.assertTrue(a[2] instanceof PCBFootprint);
      Assert.assertTrue(a[3] instanceof PCBFootprint);
      
      boolean same=false;
      //assert no copy
      for(Shape shape:shapesToPrint){
          if(shape==a[0]){
              same=true;
          }
      }
      Assert.assertTrue(same);
      
     same=false;
     for(Shape shape:shapesToPrint){
        if(shape==a[1]){
            same=true;
        }
     }
     Assert.assertTrue(same);
     
    same=false;
    for(Shape shape:shapesToPrint){
       if(shape==a[2]){
           same=true;
       }
    }
    Assert.assertTrue(same);
    
    same=false;
    for(Shape shape:shapesToPrint){
       if(shape==a[3]){
           same=true;
       }
    }
    Assert.assertTrue(same);    
      
}
@Test
public void testPCBFootprintToFootprint(){
    BoardMgr mgr=BoardMgr.getInstance();
    PCBFootprint pcbfootprint=new PCBFootprint(Layer.LAYER_FRONT);
    //pcbfootprint.Add(new RoundRect(Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(10),Grid.MM_TO_COORD(7),0, Grid.MM_TO_COORD(8)));
    pcbfootprint.getChipText().getTextureByTag("reference").setText("RIP");
    pcbfootprint.getChipText().getTextureByTag("value").setText("ATV");
    
    Footprint footprint=mgr.createFootprint(pcbfootprint);
    Assert.assertTrue(footprint.getShapes().size()==3);
    
    Texture texture = FootprintMgr.getInstance().getTextureByTag(footprint,"reference"); 
    Assert.assertTrue(texture.getText()=="RIP");
    Assert.assertTrue(texture.getText().equals("RIP"));
    
    texture = FootprintMgr.getInstance().getTextureByTag(footprint,"value"); 
    Assert.assertTrue(texture.getText()=="ATV");  

}
    @Test
    public void testFootprintSwitchPCBFootprint(){
        BoardMgr mgr=BoardMgr.getInstance();
        PCBFootprint pcbfootprint=new PCBFootprint(Layer.LAYER_FRONT);
        //pcbfootprint.Add(new RoundRect(Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(10),Grid.MM_TO_COORD(7),0, Grid.MM_TO_COORD(8)));
        pcbfootprint.Add(new Pad(Grid.MM_TO_COORD(7),Grid.MM_TO_COORD(7),Grid.MM_TO_COORD(14),Grid.MM_TO_COORD(4)));
        pcbfootprint.Add(new Pad(Grid.MM_TO_COORD(17),Grid.MM_TO_COORD(17),Grid.MM_TO_COORD(14),Grid.MM_TO_COORD(4)));
        pcbfootprint.getChipText().getTextureByTag("reference").setText("RIP");
        pcbfootprint.getChipText().getTextureByTag("value").setText("ATV");
    
        Footprint footprint=mgr.createFootprint(pcbfootprint);
        
        //check position
        Rectangle rsrc=pcbfootprint.getPinsRect();
        Rectangle rdest=FootprintMgr.getInstance().getPinsRect(footprint.getShapes());
        Assert.assertTrue(rsrc.equals(rdest));
        
        //change it
        FootprintMgr.getInstance().moveBlock(footprint.getShapes(),200,300);
        //validate
        rdest=FootprintMgr.getInstance().getPinsRect(footprint.getShapes());
        Assert.assertTrue(!rsrc.equals(rdest));
        
        UUID suuid=pcbfootprint.getUUID();
        BoardMgr.getInstance().switchFootprint(footprint,pcbfootprint);
        
        UUID duuid=pcbfootprint.getUUID();
        Assert.assertTrue(suuid.equals(duuid));
        
        Rectangle rcurr=pcbfootprint.getPinsRect();
        Assert.assertTrue(rsrc.equals(rcurr));
        
        
        
        //rename pad
        //Collection<Pad> pads=footprint.getShapes(Pad.class);
        //Assert.assertTrue(pads.size()==2);
        
        
        //pcbfootprint.getPinsRect().equals(footprint.get)       
        
    }


}
