package tests.mynetpcb.gerber;

import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.gerber.Gerber;
import com.mynetpcb.gerber.capi.GerberServiceContext;

import java.io.BufferedReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

public class GerberTest {
        //private String demo="c:\\sergei\\java\\myNetPCB\\deploy\\workspace\\boards\\BlueTooth\\BlueTemp.xml";
        private String demo="c:\\sergei\\java\\myNetPCB\\deploy\\workspace\\boards\\SolarLight\\SolarLight_MPPT.xml";
        @Test
        public void testTrackRender()throws Exception{
            Board board=new Board((int)Grid.MM_TO_COORD(100),(int)Grid.MM_TO_COORD(100));   
            PCBTrack track=new PCBTrack((int)Grid.MM_TO_COORD(0.611111), Layer.LAYER_FRONT);
            track.add(new LinePoint(Grid.MM_TO_COORD(3.23),Grid.MM_TO_COORD(3.23)));
            track.add(new LinePoint(Grid.MM_TO_COORD(30.23),Grid.MM_TO_COORD(30.23)));
            board.add(track);
            
            //same width
            track=new PCBTrack((int)Grid.MM_TO_COORD(0.611111), Layer.LAYER_FRONT);
            track.add(new LinePoint(Grid.MM_TO_COORD(13.2377777777777),Grid.MM_TO_COORD(3.2377777777777)));
            track.add(new LinePoint(Grid.MM_TO_COORD(30.23),Grid.MM_TO_COORD(30.23)));
            board.add(track);
            
            GerberServiceContext context=new GerberServiceContext();  
            Gerber gerber=new Gerber(board);
            gerber.build(context,"d:\\top.gbr",Layer.LAYER_FRONT);   
            
        }
        
    @Test
    public void testBoardRender()throws Exception{
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
            
            //second board
            container.parse(xml.toString(),1);
            GerberServiceContext gerberServiceContext=new GerberServiceContext();
            //gerberServiceContext.setParameter(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN,true);
            //gerberServiceContext.setParameter(GerberServiceContext.FOOTPRINT_REFERENCE_ON_SILKSCREEN,true);
            //gerberServiceContext.setParameter(GerberServiceContext.FOOTPRINT_VALUE_ON_SILKSCREEN,true);
        
            Gerber gerber=new Gerber(container.getUnit());              
            //gerber.build(gerberServiceContext,"d:\\sergei\\top.gbr",Layer.LAYER_FRONT);
            gerber.build(gerberServiceContext,"d:\\sergei\\bottom.gbr",Layer.LAYER_BACK);
            //gerber.build(gerberServiceContext,"d:\\sergei\\top_silk.gbr",Layer.SILKSCREEN_LAYER_FRONT);
            gerber.build(gerberServiceContext,"d:\\sergei\\bottom_silk.gbr",Layer.SILKSCREEN_LAYER_BACK);
        
        //    Excelon drill=new Excelon(container.getUnit());
        //    drill.build(gerberServiceContext,"d:\\drill_npth.gbr", Layer.NPTH_LAYER_DRILL);
        //    drill.build(gerberServiceContext,"d:\\drill_pth.gbr", Layer.PTH_LAYER_DRILL);
        
    }
}
