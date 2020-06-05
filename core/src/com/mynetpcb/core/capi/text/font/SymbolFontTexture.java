package com.mynetpcb.core.capi.text.font;

import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Color;

public class SymbolFontTexture extends FontTexture {

    public SymbolFontTexture(String text, String tag, double x, double y, int fontSize, double rotation) {
        super(text,tag, x, y, fontSize, rotation);
        //this.selectionRectWidth=4;
        this.setFillColor(Color.BLACK);
    }

    @Override
    public SymbolFontTexture clone() throws CloneNotSupportedException {
        return (SymbolFontTexture) super.clone();
    }

    public void rotate(double angle, Point pt) {
        //redesign!!!!!!!!
        this.shape
            .anchorPoint
            .rotate(angle, pt);
        this.shape
            .metrics
            .calculateMetrics(this.shape.text, this.shape.fontStyle, this.shape.fontSize, this.shape.rotate);
        if (this.shape.rotate == 90) {
            this.shape.rotate = 0;
        } else {
            this.shape.rotate = 90;
        }
    }

    public void setOrientation(Orientation orientation) {
        switch (orientation) {
        case HORIZONTAL:
            if (this.shape.rotate == 90) {
                this.rotate(-90, this.shape.anchorPoint);
            }
            break;
        case VERTICAL:
            if (this.shape.rotate == 0) {
                this.rotate(90, this.shape.anchorPoint);
            }
        }
    }

    public Texture.Orientation getOrientation() {
        if (this.shape.rotate == 90) {
            return Orientation.VERTICAL;
        } else {
            return Orientation.HORIZONTAL;
        }
    }
}
