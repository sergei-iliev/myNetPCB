package com.mynetpcb.board.dialog.panel;


import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.dialog.panel.inspector.BoardPanelBuilder;
import com.mynetpcb.board.dialog.panel.inspector.ComponentPanelBuilder;
import com.mynetpcb.board.dialog.panel.inspector.CopperAreaPanelBuilder;
import com.mynetpcb.board.dialog.panel.inspector.FootprintPanelBuilder;
import com.mynetpcb.board.dialog.panel.inspector.HolePanelBuilder;
import com.mynetpcb.board.dialog.panel.inspector.LabelPanelBuilder;
import com.mynetpcb.board.dialog.panel.inspector.TrackPanelBuilder;
import com.mynetpcb.board.dialog.panel.inspector.ViaPanelBuilder;
import com.mynetpcb.board.shape.PCBArc;
import com.mynetpcb.board.shape.PCBCircle;
import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.shape.PCBHole;
import com.mynetpcb.board.shape.PCBLabel;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.board.shape.PCBRoundRect;
import com.mynetpcb.board.shape.PCBSolidRegion;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilderFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.pad.dialog.panel.inspector.ArcPanelBuilder;
import com.mynetpcb.pad.dialog.panel.inspector.CirclePanelBuilder;
import com.mynetpcb.pad.dialog.panel.inspector.LinePanelBuilder;
import com.mynetpcb.pad.dialog.panel.inspector.RectPanelBuilder;
import com.mynetpcb.pad.dialog.panel.inspector.SolidRegionPanelBuilder;

public class BoardBuilderFactory extends AbstractPanelBuilderFactory<Shape>{
        public BoardBuilderFactory(BoardComponent component) {
          panelsMap.put(PCBTrack.class,new TrackPanelBuilder(component));
          panelsMap.put(PCBCopperArea.class,new CopperAreaPanelBuilder(component));
          panelsMap.put(PCBLine.class,new LinePanelBuilder(component));
          panelsMap.put(PCBCircle.class,new CirclePanelBuilder(component));
          panelsMap.put(PCBSolidRegion.class,new SolidRegionPanelBuilder(component));
          panelsMap.put(PCBArc.class,new ArcPanelBuilder(component));
          panelsMap.put(Board.class,new BoardPanelBuilder(component));
          panelsMap.put(PCBVia.class,new ViaPanelBuilder(component));
          panelsMap.put(PCBHole.class,new HolePanelBuilder(component));
          panelsMap.put(PCBFootprint.class,new FootprintPanelBuilder(component));
          panelsMap.put(PCBLabel.class,new LabelPanelBuilder(component)); 
          panelsMap.put(BoardComponent.class,new ComponentPanelBuilder(component)); 
          panelsMap.put(PCBRoundRect.class,new RectPanelBuilder(component)); 
        }

    }
