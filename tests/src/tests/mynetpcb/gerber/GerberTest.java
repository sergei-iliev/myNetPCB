package tests.mynetpcb.gerber;

import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.shape.PCBCircle;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBRoundRect;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.CutOuts;
import com.mynetpcb.gerber.Excelon;
import com.mynetpcb.gerber.Gerber;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.capi.StringBufferEx;
import com.mynetpcb.gerber.command.CommandDictionary;
import com.mynetpcb.gerber.processor.aperture.ApertureCutOutProcessor;
import com.mynetpcb.gerber.processor.command.CommandCutOutProcessor;
import com.mynetpcb.gerber.processor.command.GraphicsStateContext;
import com.mynetpcb.pad.shape.Pad;

import java.io.BufferedReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;


public class GerberTest {
private String demo="C:\\sergei\\java\\myNetPCB\\deploy\\workspace\\boards\\demo\\gerber.xml";

    @Test
    public void testCutOutsFile()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));   
        PCBRoundRect rect=new PCBRoundRect(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(1.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(1),Grid.MM_TO_COORD(0.5),Layer.BOARD_EDGE_CUTS);     
        board.Add(rect);
                

       
        CutOuts cutouts=new CutOuts(board);
        cutouts.build("c:\\sergei\\cutout.gbr", Layer.BOARD_EDGE_CUTS);       
    }
    
    @Test
    public void testDrillFile()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));   
        
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);        
        Pad circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.THROUGH_HOLE);
        circle.setCopper(Layer.Copper.Cu);
        footprint.Add(circle);
        
        circle=new Pad(Grid.MM_TO_COORD(7.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.THROUGH_HOLE);
        circle.setCopper(Layer.Copper.BCu);
        circle.getDrill().setWidth(Grid.MM_TO_COORD(0.8));        
        footprint.Add(circle);
                
        board.Add(footprint);
       
        Excelon drill=new Excelon(board);
       
        //drill.build("c:\\sergei\\drill_pth.gbr", Layer.PTH_LAYER_DRILL);
        drill.build("c:\\sergei\\drill_npth.gbr", Layer.NPTH_LAYER_DRILL);       
    }
    @Test
    public void testBoardCutOut()throws Exception{
       Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100)); 
       PCBRoundRect rect=new PCBRoundRect(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(1.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(1),Grid.MM_TO_COORD(0.5),Layer.BOARD_EDGE_CUTS);     
       board.Add(rect);
       
        ApertureDictionary dictionary =new ApertureDictionary();        
        ApertureCutOutProcessor processor=new ApertureCutOutProcessor(dictionary);        
        processor.process(board, Layer.BOARD_EDGE_CUTS);
        
        GraphicsStateContext context=new GraphicsStateContext(dictionary, new CommandDictionary(), new StringBufferEx());
        
        CommandCutOutProcessor command=new CommandCutOutProcessor(context);
        command.process(board, Layer.BOARD_EDGE_CUTS);
        
        System.out.println(context.getOutput());
        
    }
    @Test
    public void testTrackCommand()throws Exception{
       Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100)); 
       
       PCBTrack track=new PCBTrack(Grid.MM_TO_COORD(0.25), Layer.LAYER_FRONT);
       track.add(Grid.MM_TO_COORD(0.25),Grid.MM_TO_COORD(0.25));
       track.add(Grid.MM_TO_COORD(10.25),Grid.MM_TO_COORD(10.25));
       track.add(Grid.MM_TO_COORD(10.2),Grid.MM_TO_COORD(0.2));
       board.Add(track);
       
       track=new PCBTrack(Grid.MM_TO_COORD(0.4), Layer.LAYER_FRONT);
       track.add(Grid.MM_TO_COORD(20.25),Grid.MM_TO_COORD(0.25));
       track.add(Grid.MM_TO_COORD(20.25),Grid.MM_TO_COORD(10.25));
       board.Add(track);
        
       track=new PCBTrack(Grid.MM_TO_COORD(0.8), Layer.LAYER_FRONT);
       track.add(Grid.MM_TO_COORD(30.25),Grid.MM_TO_COORD(0.25));
       track.add(Grid.MM_TO_COORD(30.25),Grid.MM_TO_COORD(10.25));
       board.Add(track); 
        
       Gerber gerber=new Gerber(board);              
       gerber.build("c:\\sergei\\top.gbr",Layer.LAYER_FRONT);       
    
    }
    @Test
    public void testPadsCommand()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));   
        
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);        
        Pad circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.THROUGH_HOLE);
        circle.setCopper(Layer.Copper.All);
        footprint.Add(circle);
        
        circle=new Pad(Grid.MM_TO_COORD(7.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.THROUGH_HOLE);
        circle.setCopper(Layer.Copper.BCu);
        footprint.Add(circle);
        
        circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(8.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.SMD);
        circle.setCopper(Layer.Copper.FCu);
        footprint.Add(circle);
        
        Pad oval=new Pad(Grid.MM_TO_COORD(7.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(3),Pad.Shape.OVAL);        
        oval.setType(Pad.Type.SMD);
        oval.setCopper(Layer.Copper.FCu);
        footprint.Add(oval);
                
        Pad rect=new Pad(Grid.MM_TO_COORD(11.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3.17), Grid.MM_TO_COORD(2.47),Pad.Shape.RECTANGULAR);        
        rect.setType(Pad.Type.SMD);
        rect.setCopper(Layer.Copper.FCu);
        footprint.Add(rect);
        
        board.Add(footprint);
        
        Gerber gerber=new Gerber(board);              
        gerber.build("c:\\sergei\\bottom.gbr",Layer.LAYER_BACK);    
    }
    
    @Test
    public void testPadsTrackesCommand()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));   
        
        PCBTrack track=new PCBTrack(Grid.MM_TO_COORD(0.25), Layer.LAYER_FRONT);
        track.add(Grid.MM_TO_COORD(0.25),Grid.MM_TO_COORD(0.25));
        track.add(Grid.MM_TO_COORD(10.25),Grid.MM_TO_COORD(10.25));
        track.add(Grid.MM_TO_COORD(10.2),Grid.MM_TO_COORD(0.2));
        board.Add(track);
        
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);        
        Pad circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.THROUGH_HOLE);
        circle.getDrill().setWidth(Grid.MM_TO_COORD(2));
        circle.setCopper(Layer.Copper.All);
        footprint.Add(circle);
        
        circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(8.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.SMD);
        circle.setCopper(Layer.Copper.FCu);
        footprint.Add(circle);
        
        Pad oval=new Pad(Grid.MM_TO_COORD(7.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(2.47),Pad.Shape.OVAL);        
        oval.setType(Pad.Type.SMD);
        oval.setCopper(Layer.Copper.FCu);
        footprint.Add(oval);
                
        Pad rect=new Pad(Grid.MM_TO_COORD(11.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3.17), Grid.MM_TO_COORD(2.47),Pad.Shape.RECTANGULAR);        
        rect.setType(Pad.Type.SMD);
        rect.setCopper(Layer.Copper.FCu);
        footprint.Add(rect);
        
        board.Add(footprint);
        
        Gerber gerber=new Gerber(board);              
        gerber.build("c:\\sergei\\bottom.gbr",Layer.LAYER_BACK);      
    }

    @Test
    public void testPadsTrackesViasCommand()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));   
        
        PCBTrack track=new PCBTrack(Grid.MM_TO_COORD(0.25), Layer.LAYER_FRONT);
        track.add(Grid.MM_TO_COORD(0.25),Grid.MM_TO_COORD(0.25));
        track.add(Grid.MM_TO_COORD(10.25),Grid.MM_TO_COORD(10.25));
        track.add(Grid.MM_TO_COORD(10.2),Grid.MM_TO_COORD(0.2));
        board.Add(track);
        
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);        
        Pad circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.SMD);
        circle.setCopper(Layer.Copper.FCu);
        footprint.Add(circle);
        
        circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(8.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.SMD);
        circle.setCopper(Layer.Copper.FCu);
        footprint.Add(circle);
        
        Pad oval=new Pad(Grid.MM_TO_COORD(7.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(2.47),Pad.Shape.OVAL);        
        oval.setType(Pad.Type.SMD);
        oval.setCopper(Layer.Copper.FCu);
        footprint.Add(oval);
                
        Pad rect=new Pad(Grid.MM_TO_COORD(11.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3.17), Grid.MM_TO_COORD(2.47),Pad.Shape.RECTANGULAR);        
        rect.setType(Pad.Type.SMD);
        rect.setCopper(Layer.Copper.FCu);        
        footprint.Add(rect);
        board.Add(footprint);
        
        PCBVia via=new PCBVia();
        via.setThickness(Grid.MM_TO_COORD(0.8));
        via.setWidth(Grid.MM_TO_COORD(2));
        via.setLocation(Grid.MM_TO_COORD(90.2),Grid.MM_TO_COORD(0.2));
        board.Add(via);
        
        Gerber gerber=new Gerber(board);        
        gerber.build("c:\\sergei\\bottom.gbr",Layer.LAYER_BACK);      
    }
    
    @Test
    public void testCircleCommand()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));   
        
        PCBCircle circle=new PCBCircle(Grid.MM_TO_COORD(13.25), Grid.MM_TO_COORD(18.25),Grid.MM_TO_COORD(10),Grid.MM_TO_COORD(0.3),Layer.LAYER_FRONT);
        board.Add(circle);
        
        Gerber gerber=new Gerber(board);        
        gerber.build("c:\\sergei\\top.gbr",Layer.LAYER_FRONT);      
    }
    
    @Test
    public void testBoardFromFile()throws Exception{
    Charset charset = Charset.forName("UTF-8");
    StringBuffer xml = new StringBuffer();
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(demo), charset)) {
        String line=null;
        while ((line = reader.readLine()) != null) {
            xml.append(line);
        }
    } 
        BoardContainer container=new BoardContainer();
        container.Add(new Board(1, 1));
        
        //second board
        container.Parse(xml.toString(),1);
//        Gerber gerber=new Gerber(container.getUnit());              
//        gerber.build("c:\\sergei\\top.gbr",Layer.LAYER_FRONT);   
//        gerber.build("c:\\sergei\\bottom.gbr",Layer.LAYER_BACK);  
//        gerber.build("c:\\sergei\\top_silk.gbr",Layer.SILKSCREEN_LAYER_FRONT);
//        gerber.build("c:\\sergei\\bottom_silk.gbr",Layer.SILKSCREEN_LAYER_BACK);
//        
//        Excelon drill=new Excelon(container.getUnit());
//        drill.build("c:\\sergei\\drill_npth.gbr", Layer.NPTH_LAYER_DRILL); 
//        drill.build("c:\\sergei\\drill_pth.gbr", Layer.PTH_LAYER_DRILL);
         
        CutOuts cutouts=new CutOuts(container.getUnit());
        cutouts.build("c:\\sergei\\cutout.gbr", Layer.BOARD_EDGE_CUTS);   
    }
    
    
}
