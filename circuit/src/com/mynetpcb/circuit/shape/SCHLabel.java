package com.mynetpcb.circuit.shape;


import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.shape.FontLabel;

import java.lang.ref.WeakReference;


public class SCHLabel extends FontLabel implements Ownerable<Shape>, Externalizable {

    

    private WeakReference<Shape> weakParentRef;
    
    public SCHLabel clone() throws CloneNotSupportedException {
        SCHLabel copy = (SCHLabel)super.clone();
        copy.weakParentRef=null;       
        return copy;
    }
    
    @Override
    public void Clear() {
        super.Clear();
        setOwner(null);
    }

    public Shape getOwner() {
        if (weakParentRef != null && weakParentRef.get() != null) {
            return weakParentRef.get();
        }
        return null;
    }

    public void setOwner(Shape parent) {
        if (parent == null) {
            /*
          * nulify
          */
            if (this.weakParentRef != null && this.weakParentRef.get() != null) {
                this.weakParentRef.clear();
                this.weakParentRef = null;
            }
        } else {
            /*
           * assign
           */
            if (this.weakParentRef != null && this.weakParentRef.get() != null) {
                this.weakParentRef.clear();
            }
            this.weakParentRef = new WeakReference<Shape>(parent);
        }
    }

    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }


    static class Memento extends AbstractMemento<Circuit, SCHLabel> {

        Texture.Memento textureMemento;

        public Memento(MementoType mementoType) {
            super(mementoType);
            textureMemento = new FontTexture.Memento();
        }

        @Override
        public void loadStateTo(SCHLabel shape) {
            super.loadStateTo(shape);
            textureMemento.loadStateTo(shape.texture);
        }

        @Override
        public void saveStateFrom(SCHLabel shape) {
            super.saveStateFrom(shape);
            textureMemento.saveStateFrom(shape.texture);
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

            return (this.getUUID().equals(other.getUUID()) && getMementoType() == other.getMementoType() &&
                    textureMemento.equals(other.textureMemento));

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash += getMementoType().hashCode();
            hash += textureMemento.hashCode();
            return hash;
        }

        public boolean isSameState(Circuit unit) {
            SCHLabel label = (SCHLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this));
        }
    }
}

