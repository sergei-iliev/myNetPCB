package tests.mynetpcb.gerber;

import org.junit.Test;

import com.mynetpcb.board.shape.BoardOutlineShapeFactory;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.gerber.Gerber;
import com.mynetpcb.gerber.capi.GerberServiceContext;

public class BoardOutlineTest {

    @Test
    public void boardOutlineRectTest()throws Exception{
    	Board board=new Board((int)Grid.MM_TO_COORD(100),(int)Grid.MM_TO_COORD(100));  
    	
    	BoardOutlineShapeFactory.createCircle(board);
    	
        GerberServiceContext context=new GerberServiceContext();  
        Gerber gerber=new Gerber(board);
        gerber.build(context,"d:\\board_ouline.gbr",Layer.BOARD_OUTLINE_LAYER);  
    	
    }
}
