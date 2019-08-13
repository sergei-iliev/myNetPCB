package tests.mynetpcb.gerber;

import com.mynetpcb.board.shape.PCBArc;
import com.mynetpcb.board.shape.PCBCircle;
import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBRoundRect;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.aperture.type.ObroundAperture;
import com.mynetpcb.gerber.aperture.type.RectangleAperture;
import com.mynetpcb.gerber.attribute.AbstractAttribute;
import com.mynetpcb.gerber.capi.GraphicsStateContext;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.gerber.capi.StringBufferEx;
import com.mynetpcb.gerber.command.AbstractCommand;
import com.mynetpcb.gerber.command.CommandDictionary;
import com.mynetpcb.gerber.command.function.FunctionCommand;
import com.mynetpcb.gerber.command.function.SetApertureCodeCommand;
import com.mynetpcb.gerber.processor.ApertureProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureCutOutProcessor;
import com.mynetpcb.gerber.processor.aperture.ApertureRegionProcessor;
import com.mynetpcb.gerber.processor.command.CommandArcProcessor;
import com.mynetpcb.gerber.processor.command.CommandCircleProcessor;
import com.mynetpcb.gerber.processor.command.CommandPadProcessor;
import com.mynetpcb.gerber.processor.command.CommandRectProcessor;
import com.mynetpcb.gerber.processor.command.CommandRegionProcessor;
import com.mynetpcb.gerber.processor.command.CommandTrackProcessor;
import com.mynetpcb.gerber.processor.command.CommandViaProcessor;
import com.mynetpcb.pad.shape.Pad;

import com.mynetpcb.pad.shape.RoundRect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;


public class ProcessorTest {

    @Test
    public void testRangeViaApertureProcessor(){
        PCBCopperArea copperArea=new PCBCopperArea( Layer.LAYER_FRONT);
        copperArea.add(Grid.MM_TO_COORD(1), Grid.MM_TO_COORD(1));
        copperArea.add(Grid.MM_TO_COORD(100), Grid.MM_TO_COORD(1));
        copperArea.add(Grid.MM_TO_COORD(100), Grid.MM_TO_COORD(100));
        copperArea.add(Grid.MM_TO_COORD(1), Grid.MM_TO_COORD(100));
        
        PCBVia via=new PCBVia();
        via.setX(Grid.MM_TO_COORD(50));
        via.setY(Grid.MM_TO_COORD(50));
        via.setWidth((Grid.MM_TO_COORD(0.4)));
        
        ApertureDictionary dictionary=new ApertureDictionary();
        Processor processor=new ApertureRegionProcessor(dictionary);
        
        Board board =Mockito.mock(Board.class);
        Mockito.when(board.getShapes(PCBCopperArea.class, Layer.LAYER_FRONT)).thenReturn(Collections.singletonList(copperArea));
        Mockito.when(board.getShapes(PCBVia.class, Layer.LAYER_FRONT)).thenReturn(Collections.singletonList(via));
        
        int width=via.getWidth()+2*copperArea.getClearance();
        
        //processor.process(board, Layer.LAYER_FRONT);
        Assert.assertTrue(dictionary.findCircle(width)!=null);
        
        //position outside
        dictionary.Reset();
        
        via.setX(Grid.MM_TO_COORD(150));    
        //processor.process(board, Layer.LAYER_FRONT);
        //no match
        Assert.assertTrue(dictionary.findCircle(width)==null);
        
        
    }
    
    @Test
    public void testCirclePadApertureProcessor(){
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);
        Pad pad=new Pad(Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(2.47));
        pad.setShape(Pad.Shape.CIRCULAR);
        pad.setType(Pad.Type.SMD);
        pad.setCopper(Layer.Copper.FCu);
        footprint.Add(pad);
        
        List footprints=new ArrayList<>();
        footprints.add(footprint);
        
        ApertureDictionary dictionary=new ApertureDictionary();
        ApertureProcessor processor=new ApertureProcessor(dictionary);
        
        Board board =Mockito.mock(Board.class);
        Mockito.when(board.getShapes(PCBFootprint.class, Layer.LAYER_FRONT)).thenReturn(footprints);
        //processor.process(board, Layer.LAYER_FRONT);
        
        Assert.assertNotNull(dictionary.findCircle(AbstractAttribute.Type.SMDPad,pad.getWidth()));
            
    }
    
    @Test
    public void testPadDrillApertureProcessor(){
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);
        Pad pad=new Pad(Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(2), Grid.MM_TO_COORD(2));
        pad.setShape(Pad.Shape.CIRCULAR);
        pad.setType(Pad.Type.SMD);
        pad.setCopper(Layer.Copper.FCu);
        footprint.Add(pad);

        Pad hole=new Pad(Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(2.47), Grid.MM_TO_COORD(2.47));
        hole.setShape(Pad.Shape.CIRCULAR);
        hole.setType(Pad.Type.THROUGH_HOLE);
        hole.setCopper(Layer.Copper.All);
        hole.getDrill().setWidth(Grid.MM_TO_COORD(2));
        footprint.Add(hole);
        
        List footprints=new ArrayList<>();
        footprints.add(footprint);
        
        ApertureDictionary dictionary=new ApertureDictionary();
        ApertureProcessor processor=new ApertureProcessor(dictionary);
        
        Board board =Mockito.mock(Board.class);
        Mockito.when(board.getShapes(PCBFootprint.class, Layer.LAYER_FRONT)).thenReturn(footprints);
        //processor.process(board, Layer.LAYER_FRONT);
        
        Assert.assertNotNull(dictionary.findCircle(AbstractAttribute.Type.SMDPad,pad.getWidth()));
        Assert.assertNotNull(dictionary.findCircle(hole.getDrill().getWidth()));    
    }
    
    @Test
    public void testCutOutApertureProcessor(){
                
            
        PCBLine outline=new PCBLine(Grid.MM_TO_COORD(0.5),Layer.BOARD_EDGE_CUTS);
        outline.add(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(0.2));
        outline.add(Grid.MM_TO_COORD(99.2), Grid.MM_TO_COORD(0.2));
        outline.add(Grid.MM_TO_COORD(99.2), Grid.MM_TO_COORD(99.2));
        outline.add(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(99.2));
        outline.add(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(0.2));
        
        List lines=new ArrayList<>();
        lines.add(outline);
                
        
        ApertureDictionary dictionary =new ApertureDictionary();        
        ApertureCutOutProcessor processor=new ApertureCutOutProcessor(dictionary);
        
        Board board =Mockito.mock(Board.class);
        Mockito.when(board.getShapes(PCBLine.class, Layer.BOARD_EDGE_CUTS)).thenReturn(lines);
        
        //processor.process(board, Layer.BOARD_EDGE_CUTS);
        
        Assert.assertNotNull(dictionary.findCircle(AbstractAttribute.Type.CutOut,outline.getThickness()));
       
        lines.clear();
        PCBArc arc=new PCBArc(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(1.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(0.6),Layer.BOARD_EDGE_CUTS);     
        lines.add(arc);
        Mockito.reset(board);
        Mockito.when(board.getShapes(PCBArc.class, Layer.BOARD_EDGE_CUTS)).thenReturn(lines); 
        //processor.process(board, Layer.BOARD_EDGE_CUTS);
        
        Assert.assertNotNull(dictionary.findCircle(AbstractAttribute.Type.CutOut,arc.getThickness()));
    
        lines.clear();
        PCBRoundRect rect=new PCBRoundRect(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(1.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(0.2),Grid.MM_TO_COORD(0.7),Layer.BOARD_EDGE_CUTS);     
        lines.add(rect);
        Mockito.reset(board);
        Mockito.when(board.getShapes(PCBRoundRect.class, Layer.BOARD_EDGE_CUTS)).thenReturn(lines); 
        //processor.process(board, Layer.BOARD_EDGE_CUTS);
        
        Assert.assertNotNull(dictionary.findCircle(AbstractAttribute.Type.CutOut,rect.getThickness()));
    
        lines.clear();
        PCBCircle circle=new PCBCircle(Grid.MM_TO_COORD(0.2), Grid.MM_TO_COORD(1.2),Grid.MM_TO_COORD(30.2),Grid.MM_TO_COORD(0.8),Layer.BOARD_EDGE_CUTS);     
        lines.add(circle);
        Mockito.reset(board);
        Mockito.when(board.getShapes(PCBCircle.class, Layer.BOARD_EDGE_CUTS)).thenReturn(lines); 
        //processor.process(board, Layer.BOARD_EDGE_CUTS);
        
        Assert.assertNotNull(dictionary.findCircle(AbstractAttribute.Type.CutOut,circle.getThickness()));

    }
    
    @Test
    public void testRectanglePadApertureProcessor(){
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);
        Pad pad=new Pad(Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(2.47));
        pad.setShape(Pad.Shape.RECTANGULAR);
        pad.setType(Pad.Type.SMD);
        pad.setCopper(Layer.Copper.FCu);
        footprint.Add(pad);
        
        List footprints=new ArrayList<>();
        footprints.add(footprint);
        
        ApertureDictionary dictionary=new ApertureDictionary();
        ApertureProcessor processor=new ApertureProcessor(dictionary);
        
        Board board =Mockito.mock(Board.class);
        Mockito.when(board.getShapes(PCBFootprint.class, Layer.LAYER_FRONT)).thenReturn(footprints);
        //processor.process(board, Layer.LAYER_FRONT);
        
        Assert.assertNotNull(dictionary.findRectangle(AbstractAttribute.Type.SMDPad,pad.getWidth(),pad.getHeight()));
            
    }    
    
    @Test
    public void testOvalPadApertureProcessor(){
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);
        Pad oval=new Pad(Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(2.47));
        oval.setShape(Pad.Shape.OVAL);
        oval.setType(Pad.Type.SMD);
        oval.setCopper(Layer.Copper.FCu);
        footprint.Add(oval);
        
        Pad rect=new Pad(Grid.MM_TO_COORD(2.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(2.47), Grid.MM_TO_COORD(2.47));
        rect.setShape(Pad.Shape.RECTANGULAR);
        rect.setType(Pad.Type.SMD);
        rect.setCopper(Layer.Copper.FCu);
        footprint.Add(rect);
        
        Pad circle=new Pad(Grid.MM_TO_COORD(3.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(3), Grid.MM_TO_COORD(3));
        circle.setShape(Pad.Shape.CIRCULAR);
        circle.setType(Pad.Type.SMD);
        circle.setCopper(Layer.Copper.FCu);
        footprint.Add(circle);
        
        List footprints=new ArrayList<>();
        footprints.add(footprint);
        
        ApertureDictionary dictionary=new ApertureDictionary();
        ApertureProcessor processor=new ApertureProcessor(dictionary);
        
        Board board =Mockito.mock(Board.class);
        Mockito.when(board.getShapes(PCBFootprint.class, Layer.LAYER_FRONT)).thenReturn(footprints);
        //processor.process(board, Layer.LAYER_FRONT);
        
        Assert.assertNotNull(dictionary.findRectangle(AbstractAttribute.Type.SMDPad,rect.getWidth(),rect.getHeight()));
        Assert.assertNotNull(dictionary.findCircle(AbstractAttribute.Type.SMDPad,circle.getWidth()));            
        Assert.assertNotNull(dictionary.findObround(AbstractAttribute.Type.SMDPad,oval.getWidth(),oval.getHeight()));  
        
    } 
    @Test
    public void testCommandRegionProcessor()throws Exception{        
        
        PCBCopperArea copperArea=new PCBCopperArea( Layer.LAYER_FRONT);
        copperArea.add(Grid.MM_TO_COORD(1), Grid.MM_TO_COORD(1));
        copperArea.add(Grid.MM_TO_COORD(100), Grid.MM_TO_COORD(1));
        copperArea.add(Grid.MM_TO_COORD(100), Grid.MM_TO_COORD(100));
        copperArea.add(Grid.MM_TO_COORD(1), Grid.MM_TO_COORD(100));
        
        
        
        Board board =Mockito.mock(Board.class);
        Mockito.when(board.getHeight()).thenReturn(Grid.MM_TO_COORD(110)); 
        Mockito.when(board.getShapes(PCBCopperArea.class, Layer.LAYER_FRONT)).thenReturn(Collections.singletonList(copperArea));
        
        
        
        CommandDictionary commandDictionary =Mockito.mock(CommandDictionary.class);
        Mockito.when(commandDictionary.get(AbstractCommand.Type.COMMENTS, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.COMMENTS));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class)).thenReturn(new SetApertureCodeCommand());
        Mockito.when(commandDictionary.get(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.REGION_MODE_ON, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.REGION_MODE_ON));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.REGION_MODE_OFF, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.REGION_MODE_OFF));

        
        ApertureDictionary apertureDictionary=Mockito.mock(ApertureDictionary.class);
        Mockito.when(apertureDictionary.get(10)).thenReturn(new CircleAperture(10,Grid.MM_TO_COORD(0.25),0));
        
        GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
        
        CommandRegionProcessor processor=new CommandRegionProcessor(context);
        
        //processor.process(board, Layer.LAYER_FRONT);
        System.out.println(context.getOutput());
        
    }    
    @Test
    public void testCommandTrackProcessor()throws Exception{
        
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));         
        
        PCBTrack track=new PCBTrack(Grid.MM_TO_COORD(0.25), Layer.LAYER_FRONT);
        track.add(Grid.MM_TO_COORD(0.25),Grid.MM_TO_COORD(0.25));
        track.add(Grid.MM_TO_COORD(10.25),Grid.MM_TO_COORD(10.25));
        track.add(Grid.MM_TO_COORD(10.2),Grid.MM_TO_COORD(0.2));
        board.Add(track);
        
        CommandDictionary commandDictionary =Mockito.mock(CommandDictionary.class);
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class)).thenReturn(new SetApertureCodeCommand());
        Mockito.when(commandDictionary.get(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION));

        
        ApertureDictionary apertureDictionary=Mockito.mock(ApertureDictionary.class);
        Mockito.when(apertureDictionary.findCircle(AbstractAttribute.Type.Conductor,track.getThickness())).thenReturn(new CircleAperture(10,Grid.MM_TO_COORD(0.25),0));
        
        GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
        
        CommandTrackProcessor processor=new CommandTrackProcessor(context);
        
        //processor.process(board, Layer.LAYER_FRONT);
        System.out.println(context.getOutput());
        
    }
    
    @Test
    public void testCommandCircleProcessor()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));  
        PCBCircle ellipse=new PCBCircle(Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(5.47),Grid.MM_TO_COORD(0.3),Layer.LAYER_FRONT);
        board.Add(ellipse);
        
        CommandDictionary commandDictionary =Mockito.mock(CommandDictionary.class);
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class)).thenReturn(new SetApertureCodeCommand());
        Mockito.when(commandDictionary.get(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.MULTI_QUADRENT_MODE, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.MULTI_QUADRENT_MODE));

        
        ApertureDictionary apertureDictionary=Mockito.mock(ApertureDictionary.class);
        Mockito.when(apertureDictionary.findCircle(ellipse.getThickness())).thenReturn(new CircleAperture(10,Grid.MM_TO_COORD(0.3),0));

        GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
        
        CommandCircleProcessor processor=new CommandCircleProcessor(context);
        //processor.process(board, Layer.LAYER_FRONT);
        System.out.println(context.getOutput());
        
    }
    
    @Test
    public void testCommandRectProcessor()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));  
        
        
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);
        RoundRect rect=new RoundRect(Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(4.47),Grid.MM_TO_COORD(4.47),0, Grid.MM_TO_COORD(0.4),Layer.LAYER_FRONT);        
        footprint.Add(rect);
        board.Add(footprint);
        
        CommandDictionary commandDictionary =Mockito.mock(CommandDictionary.class);
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class)).thenReturn(new SetApertureCodeCommand());
        Mockito.when(commandDictionary.get(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.MULTI_QUADRENT_MODE, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.MULTI_QUADRENT_MODE));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SINGLE_QUADRENT_MODE, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.SINGLE_QUADRENT_MODE));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.COMMENTS, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.COMMENTS));
        
        
        ApertureDictionary apertureDictionary=Mockito.mock(ApertureDictionary.class);
        Mockito.when(apertureDictionary.findCircle(rect.getThickness())).thenReturn(new CircleAperture(10,rect.getThickness(),0));

        GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
        
        CommandRectProcessor processor=new CommandRectProcessor(context);
        //processor.process(board, Layer.LAYER_FRONT);
        System.out.println(context.getOutput());
        
    }
    
    @Test
    public void testCommandArcProcessor()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));  
        PCBArc arc=new PCBArc(Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(10), Grid.MM_TO_COORD(5.47),Grid.MM_TO_COORD(0.3),Layer.LAYER_FRONT);
        arc.setStartAngle(90);
        arc.setExtendAngle(90);
        board.Add(arc);
        
        CommandDictionary commandDictionary =Mockito.mock(CommandDictionary.class);
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class)).thenReturn(new SetApertureCodeCommand());
        Mockito.when(commandDictionary.get(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.MULTI_QUADRENT_MODE, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.MULTI_QUADRENT_MODE));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.COUNTER_CLOCKWISE_CIRCULAR_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SINGLE_QUADRENT_MODE, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.SINGLE_QUADRENT_MODE));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.COMMENTS, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.COMMENTS));
        
        ApertureDictionary apertureDictionary=Mockito.mock(ApertureDictionary.class);
        Mockito.when(apertureDictionary.findCircle(arc.getThickness())).thenReturn(new CircleAperture(10,Grid.MM_TO_COORD(0.3),0));

        GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
        
        CommandArcProcessor processor=new CommandArcProcessor(context);
        //processor.process(board, Layer.LAYER_FRONT);
        System.out.println(context.getOutput());
        
    }
    
    @Test
    public void testCommandPadProcessor()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));   
        
        PCBFootprint footprint=new PCBFootprint( Layer.LAYER_FRONT);
        Pad oval=new Pad(Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(2.47));
        oval.setShape(Pad.Shape.OVAL);
        oval.setType(Pad.Type.SMD);
        oval.setCopper(Layer.Copper.FCu);
        footprint.Add(oval);
        
        Pad rect=new Pad(Grid.MM_TO_COORD(2.25), Grid.MM_TO_COORD(1.25), Grid.MM_TO_COORD(2.47), Grid.MM_TO_COORD(2.47));
        rect.setShape(Pad.Shape.RECTANGULAR);
        rect.setType(Pad.Type.SMD);
        rect.setCopper(Layer.Copper.FCu);
        footprint.Add(rect);
        
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
        
        board.Add(footprint);
        
        CommandDictionary commandDictionary =Mockito.mock(CommandDictionary.class);
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class)).thenReturn(new SetApertureCodeCommand());
        Mockito.when(commandDictionary.get(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION));


        ApertureDictionary apertureDictionary=Mockito.mock(ApertureDictionary.class);
        
        Mockito.when(apertureDictionary.findCircle(AbstractAttribute.Type.SMDPad,circle.getWidth())).thenReturn(new CircleAperture(10,Grid.MM_TO_COORD(3),0));
        Mockito.when(apertureDictionary.findRectangle(AbstractAttribute.Type.SMDPad,rect.getWidth(),rect.getHeight())).thenReturn(new RectangleAperture(11,Grid.MM_TO_COORD(2.47), Grid.MM_TO_COORD(2.47)));
        Mockito.when(apertureDictionary.findObround(AbstractAttribute.Type.SMDPad,oval.getWidth(),oval.getHeight())).thenReturn(new ObroundAperture(12,Grid.MM_TO_COORD(1.47), Grid.MM_TO_COORD(2.47)));
        
        GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
        
        CommandPadProcessor processor=new CommandPadProcessor(context);

       //processor.process(board, Layer.LAYER_FRONT);
        System.out.println(context.getOutput());
    }
        
    @Test
    public void testCommandViaProcessor()throws Exception{
        Board board=new Board(Grid.MM_TO_COORD(100),Grid.MM_TO_COORD(100));  
        
        PCBVia via=new PCBVia();
        via.setX(Grid.MM_TO_COORD(1.25));
        via.setY(Grid.MM_TO_COORD(1.25));
        via.setWidth(Grid.MM_TO_COORD(2));
        via.setThickness(Grid.MM_TO_COORD(0.8));
        
        board.Add(via);
        
        PCBVia via1=new PCBVia();
        via1.setX(Grid.MM_TO_COORD(1.25));
        via1.setY(Grid.MM_TO_COORD(3.25));
        via1.setWidth(Grid.MM_TO_COORD(2));
        via1.setThickness(Grid.MM_TO_COORD(0.8));
        
        board.Add(via1);
        
        
        CommandDictionary commandDictionary =Mockito.mock(CommandDictionary.class);
        Mockito.when(commandDictionary.get(AbstractCommand.Type.SET_CURRENT_APERTURE, SetApertureCodeCommand.class)).thenReturn(new SetApertureCodeCommand());
        Mockito.when(commandDictionary.get(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.LENEAR_MODE_INTERPOLATION));
        Mockito.when(commandDictionary.get(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION, FunctionCommand.class)).thenReturn(new FunctionCommand(AbstractCommand.Type.CLOCKWISE_CICULAR_INTERPOLATION));

        ApertureDictionary apertureDictionary=Mockito.mock(ApertureDictionary.class);
        
        Mockito.when(apertureDictionary.findCircle(AbstractAttribute.Type.ViaPad,via.getWidth())).thenReturn(new CircleAperture(10,Grid.MM_TO_COORD(2),0));

        GraphicsStateContext context=new GraphicsStateContext(apertureDictionary, commandDictionary, new StringBufferEx());
        CommandViaProcessor processor=new CommandViaProcessor(context);

        //processor.process(board, Layer.LAYER_BACK);
        System.out.println(context.getOutput());
    }
}
