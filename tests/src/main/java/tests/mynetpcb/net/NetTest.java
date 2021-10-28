package tests.mynetpcb.net;

import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.Net;
import com.mynetpcb.core.capi.shape.Shape;

import java.io.BufferedReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class NetTest {
    private String demo="c:\\sergei\\java\\myNetPCB\\deploy\\workspace\\boards\\demo\\first.xml";
    
    @Test
    public void testSelectNetAt()throws Exception{
        Charset charset = Charset.forName("UTF-8");
        StringBuffer xml = new StringBuffer();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(demo), charset)) {
            String line=null;
            while ((line = reader.readLine()) != null) {
                xml.append(line);
            }
        } 
            BoardContainer container=new BoardContainer();
            container.add(new Board(1, 1));
                        
            container.parse(xml.toString(),0);
            Shape track=null;
            for(Shape shape:container.getUnit().getShapes()){
                if(shape instanceof PCBTrack){
                    track=shape;
                    container.getUnit().selectNetAt((Net)track);   
                }
            }            
            
            
    }
}
