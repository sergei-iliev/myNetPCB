package tests.mynetpcb.gerber;

import com.mynetpcb.board.shape.BoardOutlineShapeFactory;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.gerber.Gerber;
import com.mynetpcb.gerber.capi.GerberServiceContext;

public class BoardOutlineTest {

    @Test
    public void boardOutlineRectTest()throws Exception{
    	Board board=new Board((int)Grid.MM_TO_COORD(100),(int)Grid.MM_TO_COORD(100));  
    	
    	BoardOutlineShapeFactory.createRect(board);
    	
        GerberServiceContext context=new GerberServiceContext();  
        Gerber gerber=new Gerber(board);
        //gerber.build(context,"d:\\top.gbr",Layer.LAYER_FRONT);  
    	
    }
}
