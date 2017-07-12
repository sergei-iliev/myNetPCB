package com.mynetpcb.circuit.unit;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.dialog.SymbolInlineEditorDialog;
import com.mynetpcb.circuit.shape.SCHConnector;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.shape.SCHWire;
import com.mynetpcb.core.capi.Pinaware;
import com.mynetpcb.core.capi.Typeable;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.shape.Label;
import com.mynetpcb.symbol.shape.Pin;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Point;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/*
 * Use singleton manager to
 * 1.Name components number ICxx
 * 2.Add bus end when wire ends or starts on a buss
 * 3.Get WirePoints belonging to Buss
 */
public final class CircuitMgr extends UnitMgr<Circuit, Shape> {
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
            if (symbol instanceof Label) {
                if (((Label)symbol).getTexture().getTag().equals("unit")) {
                    schsymbol.getChipText().getTextureByTag("unit").copy(((Label)symbol).getTexture());
                    continue;
                }
                if (((Label)symbol).getTexture().getTag().equals("reference")) {
                    schsymbol.getChipText().getTextureByTag("reference").copy(((Label)symbol).getTexture());
                    continue;
                }
            }
            try {
                schsymbol.Add(symbol.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        schsymbol.setDisplayName(module.getUnitName());
        schsymbol.setType(module.getType());
        schsymbol.getPackaging().copy(module.getPackaging());
        return schsymbol;
    }
    public Symbol createSymbol(SCHSymbol schsymbol) {
        Symbol symbol = new Symbol(500,500);
        //1.shapes
        for (Shape shape : schsymbol.getShapes()) {
            try {
                Shape copy=shape.clone();        
                symbol.Add(copy);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        //2.text
            if (schsymbol.getChipText().getTextureByTag("reference") != null&&! schsymbol.getChipText().getTextureByTag("reference").isEmpty() ) {
                Label value=new Label();
                value.getTexture().copy(schsymbol.getChipText().getTextureByTag("reference"));
                symbol.Add(value);
            }
            if (schsymbol.getChipText().getTextureByTag("unit") != null && ! schsymbol.getChipText().getTextureByTag("unit").isEmpty()) {
                Label value=new Label();
                value.getTexture().copy(schsymbol.getChipText().getTextureByTag("unit"));
                symbol.Add(value);
            }

        //3.name
        symbol.setUnitName(schsymbol.getDisplayName());
        //4. type
        symbol.setType(schsymbol.getType());
        symbol.getPackaging().copy(schsymbol.getPackaging());
        return symbol;
    }     
    private void createSCHSymbol(Symbol symbol,SCHSymbol schsymbol) {
        for (Shape shape : symbol.getShapes()) {
            if (shape instanceof Label) {
                    if (((Label)shape).getTexture().getTag().equals("unit")) {
                    schsymbol.getChipText().getTextureByTag("unit").copy(((Label)shape).getTexture());
                    continue;
                }
                    if (((Label)shape).getTexture().getTag().equals("reference")) {
                    schsymbol.getChipText().getTextureByTag("reference").copy(((Label)shape).getTexture());
                    continue;
                }
            }
            try {
                schsymbol.Add(shape.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace(System.out);
            }
        }
        schsymbol.setDisplayName(symbol.getUnitName());
        schsymbol.getPackaging().copy(symbol.getPackaging());
    } 
   
    public void openSymbolInlineEditorDialog(CircuitComponent unitComponent,SCHSymbol schsymbol){
        
        //create Symbol
        SymbolContainer copy=new SymbolContainer();
        copy.Add(createSymbol(schsymbol));

        //center the copy
        int x=(int)copy.getUnit().getBoundingRect().getCenterX();
        int y=(int)copy.getUnit().getBoundingRect().getCenterY();
        this.moveBlock(copy.getUnit().getShapes(), (copy.getUnit().getWidth()/2)-x, (copy.getUnit().getHeight()/2)-y);
        this.alignBlock(copy.getUnit().getGrid(),copy.getUnit().getShapes());
        
        SymbolInlineEditorDialog symbolEditorDialog =
            new SymbolInlineEditorDialog(unitComponent.getDialogFrame().getParentFrame(), "Symbol Inline Editor",copy);
        symbolEditorDialog.pack();
        symbolEditorDialog.setLocationRelativeTo(null); //centers on screen
        symbolEditorDialog.setFocusable(true);
        symbolEditorDialog.setVisible(true);
        CircuitComponent.getUnitKeyboardListener().setComponent(unitComponent);            
        
        if(symbolEditorDialog.getResult()!=null){
            this.switchSymbol(symbolEditorDialog.getResult().getUnit(),schsymbol);
            symbolEditorDialog.getResult().Release();
        }    
        copy.Release();
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
        //2.get current position
        Rectangle rsrc=schsymbol.getPinsRect();        
        //3.clear schsymbol
        schsymbol.Clear();
        schsymbol.getChipText().Add(new FontTexture("reference", "", 0, 0, Text.Alignment.LEFT, 8));
        schsymbol.getChipText().Add(new FontTexture("unit", "", 8, 8, Text.Alignment.RIGHT, 8)); 
        
        //4.transfer
        createSCHSymbol(symbol, schsymbol);
        //5.get new position
        Rectangle rdst=schsymbol.getPinsRect();
        //6.go to new position
        schsymbol.Move(rsrc.x-rdst.x,rsrc.y-rdst.y);
    }
    
    public void bindChipWirePoints(Circuit circuit, Pinaware chip, boolean bind) {
        //***to bind you need unselected wire ends
        for (SCHWire wire : circuit.<SCHWire>getShapes(SCHWire.class)) {
            for (LinePoint wirePoint : wire.getLinePoints()) {
                if (null != chip.getPin(wirePoint.x, wirePoint.y)) {
                    wirePoint.setSelected(bind);
                }
            }
        }
    }
    /*
     * returns null if no wire is connected to chip pins
     */

    public boolean isWirePointToChip(Circuit circuit, Pinaware chip) {
        for (SCHWire wire : circuit.<SCHWire>getShapes(SCHWire.class)) {
            for (Point wirePoint : wire.getLinePoints()) {
                if (null != chip.getPin(wirePoint.x, wirePoint.y)) {
                    return true;
                }
            }
        }
        return  false;
    }
//
//    public void bindChipConnectors(Circuit circuit, Pinable chip, boolean bind) {
//        for (Connector connector : circuit.<Connector>getSymbols(Connector.class)) {
//            PinElement pin = connector.getPins().iterator().next();
//            Point endPoint = pin.getPinEnd();
//            if (null != chip.getPin(endPoint.x, endPoint.y)) {
//                if (bind) {
//                    connector.setOwner((Symbol)chip);
//                } else {
//                    connector.setOwner(null);
//                }
//            }
//        }
//    }
//
    public boolean isConnectorToChip(Circuit circuit, Pinaware chip) {
        for (SCHConnector connector : circuit.<SCHConnector>getShapes(SCHConnector.class)) {
            Pin pin = connector.getPins().iterator().next();
            if (null != chip.getPin(pin.getX(), pin.getY())) {
                return true;
            }
        }
        return false;
    }
//
//    public void setWireToBussEnd(Circuit circuit, Wire wire) {
//        //***wire is more then 2 points
//        if (wire.getWirePoints().size() < 2)
//            return;
//
//        int distance = circuit.getGrid().getGridPointToPoint();
//
//        for (Symbol moveable : circuit.getSymbols()) {
//            if (!(moveable instanceof Bus))
//                continue;
//
//            Bus buss = (Bus)moveable;
//            BusPin busPin = null;
//            //***first wire point
//            if (buss.isPointOnWire(wire.getWirePoints().get(0).x, wire.getWirePoints().get(0).y)) {
//                WirePoint firstPoint = wire.getWirePoints().get(0);
//                WirePoint secondPoint = wire.getWirePoints().get(1);
//                WirePoint lastPoint = wire.getWirePoints().get(wire.getWirePoints().size() - 1);
//
//                switch (buss.getWireLineOrientation(firstPoint.x, firstPoint.y)) {
//                case Wire.SUBWIRE_ORIENTATION_X:
//                    busPin = new BusPin(circuit, Text.Alignment.LEFT);
//
//                    //***What if points on the same X axis????
//                    if (secondPoint.x != firstPoint.y) {
//                        if (secondPoint.x > firstPoint.x) {
//                            busPin.setLocation(firstPoint.x, firstPoint.y - distance);
//                            firstPoint.setLocation(firstPoint.x + distance, firstPoint.y);
//                        } else {
//                            busPin.setLocation(firstPoint.x, firstPoint.y + distance);
//                            firstPoint.setLocation(firstPoint.x - distance, firstPoint.y);
//                        }
//                        busPin.getWirePoints().get(1).setLocation(firstPoint);
//                        busPin.getChipText().get(0).setLocation(firstPoint.x, firstPoint.y);
//                        circuit.add(busPin);
//                    } else {
//                        WirePoint thirdPoint;
//                        try {
//                            thirdPoint = wire.getWirePoints().get(2);
//                        } catch (IndexOutOfBoundsException iobe) {
//                            thirdPoint = null;
//                        }
//                        //***short 2-point wire!
//                        if (thirdPoint == null) {
//                            //***delete this crap!
//                            busPin = null;
//                            circuit.delete(wire.getUUID());
//                        } else {
//                            if (thirdPoint.x > secondPoint.x) {
//                                busPin.setLocation(firstPoint.x, firstPoint.y);
//                                firstPoint.setLocation(firstPoint.x + distance, firstPoint.y + distance);
//                                secondPoint.setLocation(secondPoint.x + distance, secondPoint.y);
//
//                            } else {
//                                busPin.setLocation(firstPoint.x, firstPoint.y);
//                                firstPoint.setLocation(firstPoint.x - distance, firstPoint.y - distance);
//                                secondPoint.setLocation(secondPoint.x - distance, secondPoint.y);
//                            }
//                            busPin.getWirePoints().get(1).setLocation(firstPoint);
//                            busPin.getChipText().get(0).setLocation(firstPoint.x, firstPoint.y);
//                            circuit.add(busPin);
//                        }
//
//                    }
//
//                    break;
//                case Wire.SUBWIRE_ORIENTATION_Y:
//                    busPin = new BusPin(circuit, Text.Alignment.BOTTOM);
//                    //***What if points on the same Y axis????
//                    if (secondPoint.y != firstPoint.y) {
//                        if (secondPoint.y > firstPoint.y) {
//                            busPin.setLocation(firstPoint.x + distance, firstPoint.y);
//                            firstPoint.setLocation(firstPoint.x, firstPoint.y + distance);
//                        } else {
//                            busPin.setLocation(firstPoint.x - distance, firstPoint.y);
//                            firstPoint.setLocation(firstPoint.x, firstPoint.y - distance);
//                        }
//
//                        busPin.getWirePoints().get(1).setLocation(firstPoint);
//                        busPin.getChipText().get(0).setLocation(firstPoint.x, firstPoint.y);
//                        circuit.add(busPin);
//                    } else {
//                        WirePoint thirdPoint;
//                        try {
//                            thirdPoint = wire.getWirePoints().get(2);
//                        } catch (IndexOutOfBoundsException iobe) {
//                            thirdPoint = null;
//                        }
//                        //***short 2-point wire!
//                        if (thirdPoint == null) {
//                            //***delete this crap!
//                            busPin = null;
//                            circuit.delete(wire.getUUID());
//                        } else {
//
//                            if (thirdPoint.y > secondPoint.y) {
//                                busPin.setLocation(firstPoint.x, firstPoint.y);
//                                firstPoint.setLocation(firstPoint.x - distance, firstPoint.y + distance);
//                                secondPoint.setLocation(secondPoint.x, secondPoint.y + distance);
//                            } else {
//                                busPin.setLocation(firstPoint.x, firstPoint.y);
//                                firstPoint.setLocation(firstPoint.x + distance, firstPoint.y - distance);
//                                secondPoint.setLocation(secondPoint.x, secondPoint.y - distance);
//                            }
//                            busPin.getWirePoints().get(1).setLocation(firstPoint);
//                            busPin.getChipText().get(0).setLocation(firstPoint.x, firstPoint.y);
//                            circuit.add(busPin);
//                        }
//                    }
//                    break;
//                }
//                //***PUT PROPER BUSPIN NAME
//                for (Symbol chip : circuit.getSymbols()) {
//                    if (!(chip instanceof Pinable))
//                        continue;
//                    PinElement pin = ((Pinable)chip).getPin(lastPoint.x, lastPoint.y);
//                    if (pin != null) {
//                        busPin.getChipText().getTextureByTag("name").setText(pin.getPinName());
//                        circuit.fireSymbolEvent(new SymbolEvent(busPin, SymbolEvent.SELECT_SYMBOL));
//                        break;
//                    }
//                }
//                return;
//            }
//
//
//            //***last wire point
//            if (buss.isPointOnWire(wire.getWirePoints().get(wire.getWirePoints().size() - 1).x,
//                                   wire.getWirePoints().get(wire.getWirePoints().size() - 1).y)) {
//                WirePoint firstPoint = wire.getWirePoints().get(0);
//                WirePoint lastPoint = wire.getWirePoints().get(wire.getWirePoints().size() - 1);
//
//                switch (buss.getWireLineOrientation(lastPoint.x, lastPoint.y)) {
//                case Wire.SUBWIRE_ORIENTATION_X:
//                    WirePoint beforeLastPoint = wire.getWirePoints().get(wire.getWirePoints().size() - 2);
//
//                    busPin = new BusPin(circuit, Text.Alignment.LEFT);
//                    busPin.setLocation(lastPoint.x, lastPoint.y + distance);
//
//                    if (beforeLastPoint.x > lastPoint.x) {
//                        lastPoint.setLocation(lastPoint.x + distance, lastPoint.y);
//                    } else {
//                        lastPoint.setLocation(lastPoint.x - distance, lastPoint.y);
//                    }
//
//                    busPin.getWirePoints().get(1).setLocation(lastPoint);
//                    busPin.getChipText().get(0).setLocation(lastPoint.x, lastPoint.y);
//                    circuit.add(busPin);
//                    break;
//                case Wire.SUBWIRE_ORIENTATION_Y:
//                    beforeLastPoint = wire.getWirePoints().get(wire.getWirePoints().size() - 2);
//
//
//                    busPin = new BusPin(circuit, Text.Alignment.BOTTOM);
//                    busPin.setLocation(lastPoint.x + distance, lastPoint.y);
//
//
//                    if (beforeLastPoint.y > lastPoint.y) {
//                        lastPoint.setLocation(lastPoint.x, lastPoint.y + distance);
//                    } else {
//                        lastPoint.setLocation(lastPoint.x, lastPoint.y - distance);
//                    }
//
//                    busPin.getWirePoints().get(1).setLocation(lastPoint);
//                    busPin.getChipText().get(0).setLocation(lastPoint.x, lastPoint.y);
//                    circuit.add(busPin);
//
//                    break;
//                }
//                //***PUT PROPER BUSPIN NAME
//                for (Symbol chip : circuit.getSymbols()) {
//                    if (!(chip instanceof Pinable))
//                        continue;
//                    PinElement pin = ((Pinable)chip).getPin(firstPoint.x, firstPoint.y);
//                    if (pin != null) {
//                        busPin.getChipText().getTextureByTag("name").setText(pin.getPinName());
//                        circuit.fireSymbolEvent(new SymbolEvent(busPin, SymbolEvent.SELECT_SYMBOL));
//                        break;
//                    }
//                }
//                return;
//            }
//
//        }
//
//    }
//    /*
//     * naming rules
//     * 1.Starts with letter - don't touch
//     * 2.Find biggest postfix and increment with one
//     */
//
//    public void symbolNaming(Circuit circuit, Symbol symbol) {
//        int counter = 0;
//
//        if (symbol instanceof Textable) {
//            ChipText text = ((Textable)symbol).getChipText();
//            if (text.getTextureByTag("reference") == null)
//                return;
//            String reference = text.getTextureByTag("reference").getText();
//            if (reference.equals("") || reference.equals(" "))
//                return;
//
//
//            //***strip trailing numbers
//            reference = stripTrailingNumber(reference);
//
//            //***find biggest postfix number
//            for (Symbol moveable : circuit.getSymbols())
//                if (moveable instanceof Textable) {
//                    ChipText t = ((Textable)moveable).getChipText();
//                    if (t.getTextureByTag("reference") == null)
//                        continue;
//
//
//                    String ref = t.getTextureByTag("reference").getText();
//                    if (ref.startsWith(reference)) {
//                        //***extract post number
//
//                        int c = getPostNumber(ref);
//                        if (c > counter)
//                            counter = c;
//
//                    }
//                }
//            if (counter > 0)
//                text.getTextureByTag("reference").setText(reference + (counter + 1));
//            else
//                text.getTextureByTag("reference").setText(reference + 1);
//        }
//    }
//    
    public Collection<SCHSymbol> getChipsByType(Circuit circuit,Typeable.Type type){
        Collection<SCHSymbol> chips = circuit.getShapes(SCHSymbol.class);
        
        for (Iterator<SCHSymbol> it = chips.iterator(); it.hasNext();) {
                SCHSymbol chip = it.next();
                if(chip.getType()!=type){
                   it.remove();    
                }
            }
       
        return chips; 
    }
    
    public void generateShapeNaming(final Circuit circuit) {
        if(circuit==null)
            return;
        //1.reference prefixes
        Collection<SCHSymbol> chips = getChipsByType(circuit,Typeable.Type.SYMBOL);
         
        Set<String> refTexts = new HashSet<String>();
        for (SCHSymbol chip : chips) {
            Texture reference = chip.getChipText().getTextureByTag("reference");
            if (reference != null && reference.getText().length() > 0) {
                String refText = stripTrailingNumber(reference.getText());
                refTexts.add(refText);
            }
        }

        //2.isolate symbols with same prefix
        Comparator<UUID> sorter = new Comparator<UUID>() {
            @Override
            public int compare(UUID u1, UUID u2) {
                Shape s1=circuit.getShape(u1);            
                Shape s2=circuit.getShape(u2);
                if(Utilities.isBefore(s1.getBoundingShape().getBounds(), s2.getBoundingShape().getBounds())){
                  return -1;  
                }else{
                  return 1;
                }  
            }
        };

        for (String refText : refTexts) {
            List<UUID> uuids = new ArrayList<UUID>();
            for (SCHSymbol chip : chips) {
                Texture reference = chip.getChipText().getTextureByTag("reference");
                if (reference != null && reference.getText().length() > 0) {
                    String text = stripTrailingNumber(reference.getText());
                    if (text.startsWith(refText)) {
                        uuids.add(chip.getUUID());
                    }
                }
            }

            Collections.sort(uuids, sorter);
           
         //3.Rename
            int i=1;
            for(UUID uuid:uuids){                
                Texture texture=((Textable)(circuit.getShape(uuid))).getChipText().getTextureByTag("reference");
                texture.setText(refText+i++);
            }
        }

    }


    private String stripTrailingNumber(String reference) {
        char[] stringArray = reference.toCharArray();
        int i;

        for (i = 0; i < stringArray.length; i++)
            if (stringArray[i] <= '9')
                break;
        return reference.substring(0, i);
    }


}

