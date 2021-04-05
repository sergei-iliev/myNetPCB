package com.mynetpcb.circuit.unit;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.dialog.SymbolInlineEditorDialog;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.shape.FontLabel;
import com.mynetpcb.symbol.unit.Symbol;

import javax.swing.JFrame;

public class CircuitMgr extends UnitMgr {
    private static CircuitMgr circuitMgr;

    private CircuitMgr() {

    }


    public static synchronized CircuitMgr getInstance() {
        if (circuitMgr == null) {
            circuitMgr = new CircuitMgr();
        }
        return circuitMgr;
    }
    public SCHSymbol createSCHSymbol(Symbol module) {
        SCHSymbol schsymbol = new SCHSymbol();
        for (Shape symbol : module.getShapes()) {
            if (symbol instanceof FontLabel) {
                if (((FontLabel)symbol).getTexture().getTag().equals("unit")) {
                    schsymbol.getTextureByTag("unit").copy(((FontLabel)symbol).getTexture());
                    continue;
                }
                if (((FontLabel)symbol).getTexture().getTag().equals("reference")) {
                    schsymbol.getTextureByTag("reference").copy(((FontLabel)symbol).getTexture());
                    continue;
                }
            }
            try {
                schsymbol.add(symbol.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        schsymbol.setDisplayName(module.getUnitName());
        schsymbol.setType(module.getType());
        return schsymbol;
    }
    public Symbol createSymbol(SCHSymbol schsymbol) {
        Symbol symbol = new Symbol(500,500);
        //1.shapes
        for (Shape shape : schsymbol.getShapes()) {
            try {
                Shape copy=shape.clone();        
                symbol.add(copy);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        //2.text
            if (schsymbol.getTextureByTag("reference") != null&&! schsymbol.getTextureByTag("reference").isEmpty() ) {
                FontLabel value=new FontLabel();
                value.getTexture().copy(schsymbol.getTextureByTag("reference"));
                symbol.add(value);
            }
            if (schsymbol.getTextureByTag("unit") != null && ! schsymbol.getTextureByTag("unit").isEmpty()) {
                FontLabel value=new FontLabel();
                value.getTexture().copy(schsymbol.getTextureByTag("unit"));
                symbol.add(value);
            }

        //3.name
        symbol.setUnitName(schsymbol.getDisplayName());
        //4. type
        symbol.setType(schsymbol.getType());
        //symbol.getPackaging().copy(schsymbol.getPackaging());
        return symbol;
    } 
    public void openSymbolInlineEditorDialog(CircuitComponent unitComponent,SCHSymbol schsymbol){
        //clone
        SCHSymbol clone=null;
        try {
            clone = schsymbol.clone();
            clone.setSelected(false);
        } catch (CloneNotSupportedException e) {
          e.printStackTrace(); 
        }
        //create Symbol
        SymbolContainer copy=new SymbolContainer();
        copy.add(createSymbol(clone));

        //center the copy
        int x=(int)copy.getUnit().getBoundingRect().getCenter().x;
        int y=(int)copy.getUnit().getBoundingRect().getCenter().y;
        this.moveBlock(copy.getUnit().getShapes(), (copy.getUnit().getWidth()/2)-x, (copy.getUnit().getHeight()/2)-y);
        this.alignBlock(copy.getUnit().getGrid(),copy.getUnit().getShapes());
        
        SymbolInlineEditorDialog symbolEditorDialog =
            new SymbolInlineEditorDialog((JFrame)unitComponent.getDialogFrame().getParentFrame(), "Symbol Inline Editor",copy);
        symbolEditorDialog.pack();
        symbolEditorDialog.setLocationRelativeTo(null); //centers on screen
        symbolEditorDialog.setFocusable(true);
        symbolEditorDialog.setVisible(true);
        CircuitComponent.getUnitKeyboardListener().setComponent(unitComponent);            
        
        if(symbolEditorDialog.getResult()!=null){
            this.switchSymbol(symbolEditorDialog.getResult().getUnit(),schsymbol);
            symbolEditorDialog.getResult().release();
        } 
        clone.clear();
        copy.release();
        symbolEditorDialog.dispose();        
    }    
    
    /**
     *Equalize inline changed symbol with in circuit positioned schsymbol
     * @param symbol - edited symbol
     * @param schsymbol - sch one that needs to be equalized
     */
    
    public void switchSymbol(Symbol symbol,SCHSymbol schsymbol){
        //1.unselect
        symbol.setSelected(false);
        schsymbol.setSelected(false);
        //2.get current position
        Point rsrc=schsymbol.getPinsRect().getCenter();        
        //3.clear schsymbol
        schsymbol.clear();        
        //schsymbol.getChipText().Add(new FontTexture("reference", "", 0, 0, Text.Alignment.LEFT, 8));
        //schsymbol.getChipText().Add(new FontTexture("unit", "", 8, 8, Text.Alignment.RIGHT, 8)); 
        
        //4.transfer
        createSCHSymbol(symbol, schsymbol);
        //5.get new position
        Point rdst=schsymbol.getPinsRect().getCenter();
        //6.go to new position
        schsymbol.move(rsrc.x-rdst.x,rsrc.y-rdst.y);
    }    
    private void createSCHSymbol(Symbol symbol,SCHSymbol schsymbol) {
        for (Shape shape : symbol.getShapes()) {
            if (shape instanceof FontLabel) {                
                    if (((FontLabel)shape).getTexture().getTag().equals("unit")) {
                    schsymbol.getTextureByTag("unit").copy(((FontLabel)shape).getTexture());
                    continue;
                }
                    if (((FontLabel)shape).getTexture().getTag().equals("reference")) {
                    schsymbol.getTextureByTag("reference").copy(((FontLabel)shape).getTexture());
                    continue;
                }
            }
            try {
                schsymbol.add(shape.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        schsymbol.setDisplayName(symbol.getUnitName());
        //schsymbol.getPackaging().copy(symbol.getPackaging());
    }     
}
