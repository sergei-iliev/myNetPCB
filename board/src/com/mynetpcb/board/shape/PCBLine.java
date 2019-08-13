package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Line;

import java.awt.Point;

import java.util.Arrays;

public class PCBLine extends Line implements PCBShape{
        
    public PCBLine(int thickness,int layermaskId){
        super(thickness,layermaskId);
    } 
    
    @Override
    public Point alignToGrid(boolean isRequired) {        
        return null;
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);  
    }
    
    public static class Memento extends AbstractMemento<Board, PCBLine> {

        private int Ax[];

        private int Ay[];         
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }
        
        @Override
        public void loadStateTo(PCBLine shape) {
            super.loadStateTo(shape);
            shape.getLinePoints().clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.addPoint(new Point(Ax[i], Ay[i])); 
            }
            //***reset floating start point
            if (shape.getLinePoints().size() > 0) {
                shape.floatingStartPoint.setLocation(shape.getLinePoints().get(shape.getLinePoints().size() -
                                                                 1));
                shape.Reset();
            }
        }
        @Override
        public void saveStateFrom(PCBLine shape) {
            super.saveStateFrom(shape);
            
            Ax = new int[shape.getLinePoints().size()];
            Ay = new int[shape.getLinePoints().size()];
            for (int i = 0; i < shape.getLinePoints().size(); i++) {
                Ax[i] = shape.getLinePoints().get(i).x;
                Ay[i] = shape.getLinePoints().get(i).y;
            }
        }

        @Override
        public void Clear() {
            super.Clear();
            Ax = null;
            Ay = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }
            Memento other = (Memento)obj;
            return (getUUID().equals(other.getUUID()) &&
                    getMementoType().equals(other.getMementoType()) &&
                    thickness==other.thickness&&
                    layerindex==other.layerindex&&
                    Arrays.equals(Ax, other.Ax) &&
                    Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash += this.getMementoType().hashCode();
            hash += thickness+layerindex;
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }

        public boolean isSameState(Board unit) {
            PCBLine line = (PCBLine)unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }
}
