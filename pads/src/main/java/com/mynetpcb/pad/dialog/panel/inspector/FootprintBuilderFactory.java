package com.mynetpcb.pad.dialog.panel.inspector;


import com.mynetpcb.core.capi.panel.AbstractPanelBuilderFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.shape.Arc;
import com.mynetpcb.pad.shape.Circle;
import com.mynetpcb.pad.shape.GlyphLabel;
import com.mynetpcb.pad.shape.Hole;
import com.mynetpcb.pad.shape.Line;
import com.mynetpcb.pad.shape.Pad;
import com.mynetpcb.pad.shape.RoundRect;
import com.mynetpcb.pad.shape.SolidRegion;
import com.mynetpcb.pad.unit.Footprint;

public class FootprintBuilderFactory extends AbstractPanelBuilderFactory<Shape>{
    public FootprintBuilderFactory(FootprintComponent component) {
        panelsMap.put(FootprintComponent.class,new ComponentPanelBuilder(component));
        panelsMap.put(Footprint.class,new FootprintPanelBuilder(component)); 
        panelsMap.put(RoundRect.class,new RectPanelBuilder(component)); 
        panelsMap.put(Pad.class,new PadPanelBuilder(component));
        panelsMap.put(SolidRegion.class,new SolidRegionPanelBuilder(component));
        panelsMap.put(Circle.class,new CirclePanelBuilder(component));
        panelsMap.put(Line.class,new LinePanelBuilder(component));
        panelsMap.put(Hole.class,new HolePanelBuilder(component));
        panelsMap.put(GlyphLabel.class,new LabelPanelBuilder(component));
        panelsMap.put(Arc.class,new ArcPanelBuilder(component));
    }
}
