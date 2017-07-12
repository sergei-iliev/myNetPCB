package tests.mynetpcb.board;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Grid;

import java.awt.event.MouseEvent;

import javax.swing.JScrollBar;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

public class LineEventHandleTest {


@Test
public void testLineUndoDefaultLineProcessor(){
   DialogFrame df=Mockito.mock(DialogFrame.class);
   Mockito.when(df.getHorizontalScrollBar()).thenReturn(new JScrollBar());
   Mockito.when(df.getVerticalScrollBar()).thenReturn(new JScrollBar()); 
   
   Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));
   BoardComponent boardComponent=new BoardComponent(df);
   boardComponent.getModel().Add(board);
   boardComponent.setMode(BoardComponent.LINE_MODE);
   MouseEvent mpres=new MouseEvent(boardComponent,MouseEvent.MOUSE_PRESSED,11,0,111,111,1,false);
   MouseEvent mrelease=new MouseEvent(boardComponent,MouseEvent.MOUSE_RELEASED,11,0,111,111,1,false);
   MouseEvent mmove=new MouseEvent(boardComponent,MouseEvent.MOUSE_MOVED,11,0,111,121,1,false);

   
   boardComponent.mousePressed(mpres);
   boardComponent.mouseMoved(mmove);
   boardComponent.mouseReleased(mrelease);
   
   Assert.assertTrue(board.getUndoProvider().getQueue().size()==0);
   
   mpres=new MouseEvent(boardComponent,MouseEvent.MOUSE_PRESSED,11,0,111,121,1,false);
   mrelease=new MouseEvent(boardComponent,MouseEvent.MOUSE_RELEASED,11,0,111,121,1,false);
   mmove=new MouseEvent(boardComponent,MouseEvent.MOUSE_MOVED,11,0,131,131,1,false);
    
   boardComponent.mousePressed(mpres);
   boardComponent.mouseMoved(mmove);
   boardComponent.mouseReleased(mrelease);
    
   //this must be a create and move 
   Assert.assertTrue(board.getUndoProvider().getQueue().size()==2);
    Assert.assertTrue(board.getShapes().size()==1);
   //Ctrl+Z
   board.Undo(boardComponent.getEventMgr().getTargetEventHandle());
   Assert.assertTrue(board.getShapes().size()==0);
   
   //Ctrl+Y
   board.Redo();
   Assert.assertTrue(board.getShapes().size()==1);
   
   

   
   
}
}
