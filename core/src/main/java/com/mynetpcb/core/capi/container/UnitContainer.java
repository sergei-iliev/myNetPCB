package com.mynetpcb.core.capi.container;


import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeEventDispatcher;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitEventDispatcher;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.Changeable;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.event.EventListenerList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;


public abstract class UnitContainer<T extends Unit, S extends Shape> implements UnitEventDispatcher,
                                                                                ShapeEventDispatcher,Changeable,Clipboardable {
    private T unit;

    private Map<UUID, T> unitsMap;

    private final Map<UUID,AbstractMemento> statesMap;
    //keep both unit and symbol listeners
    private EventListenerList unitListeners;

    //***where on the hard drive this module,package,circuit is located->fileName without '_xxx_cir'
    private String formatedFileName = "";
    //***file Name with _xxx to uniqly identify file name
    private String fileName;

    //***file folder Library or Project name
    private String categoryName;

    private String libraryName;

    private String designerName = "Unknown author";

    public UnitContainer() {
        unitsMap = new LinkedHashMap<UUID, T>();
        unitListeners = new EventListenerList();
        statesMap=new HashMap<UUID,AbstractMemento>();
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setLibraryName(String labraryName) {
        this.libraryName = labraryName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        if (!this.fileName.toLowerCase().endsWith(".xml")) {
            this.fileName += ".xml";
        }
        formatedFileName = this.fileName.substring(0, this.fileName.lastIndexOf("."));
    }

    public String getFileName() {
        return fileName;
    }

    public String getFormatedFileName() {
        return formatedFileName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    public String getDesignerName() {
        return designerName;
    }

    public T getUnit() {
        return unit;
    }

    public Collection<T> getUnits() {
        return unitsMap.values();
    }

    public void add(T unit) {
        unitsMap.put(unit.getUUID(), unit);
        statesMap.put(unit.getUUID(),new CompositeMemento(MementoType.MEMENTO).add(unit.getShapes()));
        attachShapeListeners(unit);
        this.fireUnitEvent(new UnitEvent(unit, UnitEvent.ADD_UNIT));
        //set to default if first one
        if(unitsMap.size()==1){
         this.setActiveUnit(unit.getUUID());   
        }
    }

    public void delete(UUID uuid) {
        T _unit = unitsMap.get(uuid);
        if (_unit == null) {
            return;
        }
        _unit.release();
        this.fireUnitEvent(new UnitEvent(unitsMap.get(uuid), UnitEvent.DELETE_UNIT));
        if (_unit == unit) {
            unit = null;
        }
        _unit = null;
        unitsMap.remove(uuid);
        statesMap.remove(uuid);
    }

    public void clear() {
        List<UUID> keys = new ArrayList<UUID>(unitsMap.keySet());
        for (int i = 0; i < keys.size(); i++) {
            delete(keys.get(i));
        }
        this.fileName=null;
        this.categoryName=null;
        this.libraryName=null;
        unitsMap.clear();
        statesMap.clear();
    }

    public void release() {
        this.clear();
        //***clear listeners list
        if (unitListeners != null){
        for (int i = 0; i < unitListeners.getListenerList().length; i++) {
            unitListeners.getListenerList()[i] = null;
        }
        }
        unitListeners = null;
    }

    public void setActiveUnit(int index) {
        List<T> list = new LinkedList<T>(unitsMap.values());
        unit = list.get(index);
    }
    
    public void setActiveUnitByName(String name){
        for (Unit aunit : unitsMap.values()) {            
            if (aunit.getUnitName()!=null&&aunit.getUnitName().equals(name)) {
                setActiveUnit(aunit.getUUID());
                return;
            }
        }          
    }
    public void setActiveUnit(UUID key) {
        unit = unitsMap.get(key);
    }
    public int getActiveUnitIndex() {
        int index = -1;
        if (unit == null) {
            return index;
        }
        for (Unit aunit : unitsMap.values()) {
            ++index;
            if (aunit == unit) {
                return index;
            }
        }
        return index;
    }

    /*
     * Check if unit state has changed
     */
    public boolean isChanged(UUID uuid){
       for(T unit:getUnits()){
           if(unit.getUUID().equals(uuid)){
               AbstractMemento initMemento=statesMap.get(unit.getUUID());
               AbstractMemento currentMemento=new CompositeMemento(MementoType.MEMENTO).add(unit.getShapes());
               //2.is unit changed
               if(!initMemento.equals(currentMemento)){
                   return true;
               }                 
           }
        }
        
        return false;        
    }
    public boolean isChanged(){
           //1.is unit deleted
    
            if(!unitsMap.keySet().equals(statesMap.keySet())){
              return true;
            }
    
            for(T unit:getUnits()){
              AbstractMemento initMemento=statesMap.get(unit.getUUID());
              AbstractMemento currentMemento=new CompositeMemento(MementoType.MEMENTO).add(unit.getShapes());
          //2.is unit changed
                if(!initMemento.equals(currentMemento)){
                  return true;
                }
            }
    
           return false;
     }

      public void registerInitialState(){
            statesMap.clear();
            for(T unit:getUnits()) {
              statesMap.put(unit.getUUID(),new CompositeMemento(MementoType.MEMENTO).add(unit.getShapes()));
            }
       }

       public void registerInitialState(UUID uuid){
           T unit=unitsMap.get(uuid);
           if(unit==null){
              throw new IllegalArgumentException("uuid is unknown");
           }
           statesMap.put(uuid,new CompositeMemento(MementoType.MEMENTO).add(unit.getShapes()));
       }


    public void fireUnitEvent(UnitEvent e) {
        Object[] listeners = unitListeners.getListenerList();
        int numListeners = listeners.length;
        for (int i = 0; i < numListeners; i += 2) {
            if (listeners[i] == UnitListener.class) {
                switch (e.getEventType()) {
                case UnitEvent.ADD_UNIT:
                    ((UnitListener)listeners[i + 1]).addUnitEvent(e);
                    break;
                case UnitEvent.DELETE_UNIT:
                    ((UnitListener)listeners[i + 1]).deleteUnitEvent(e);
                    break;
                case UnitEvent.SELECT_UNIT:
                    ((UnitListener)listeners[i + 1]).selectUnitEvent(e);
                    break;
                case UnitEvent.RENAME_UNIT:
                    ((UnitListener)listeners[i + 1]).renameUnitEvent(e);
                    break;
                case UnitEvent.PROPERTY_CHANGE:
                    ((UnitListener)listeners[i + 1]).propertyChangeEvent(e);
                    break;
                }

            }
        }
    }


    public void addUnitListener(UnitListener listener) {
        unitListeners.add(UnitListener.class, listener);
    }


    public void removeUnitListener(UnitListener listener) {
    }


    public abstract StringBuffer format();

    public abstract void parse(String xml) throws XPathExpressionException, ParserConfigurationException, SAXException,
                                                  IOException;

    public abstract void parse(String xml, int index) throws XPathExpressionException, ParserConfigurationException,
                                                             SAXException, IOException;


    private void attachShapeListeners(Unit unit) {
        for (int i = 0; i < unitListeners.getListeners(ShapeListener.class).length; i++) {
            unit.addShapeListener(unitListeners.getListeners(ShapeListener.class)[i]);
        }
    }

    public void fireShapeEvent(ShapeEvent e) {
        //****
    }

    public void addShapeListener(ShapeListener listener) {
        unitListeners.add(ShapeListener.class, listener);
    }

    public void removeShapeListener(ShapeListener listener) {
        unitListeners.remove(ShapeListener.class, listener);
    }
    /*
     * D&D on tree fires reorder of underlying units
     */
    public void reorder(int index,UUID uuid){
        int i=0;
        for (UUID key : unitsMap.keySet()) {
           if (key.equals(uuid)) {
             break;
           }
          i++;           
        }  
        if(index>i){
          index--;  
        }
        
        Map.Entry<UUID,T> target=null;
        List<Map.Entry<UUID, T>> rest = new ArrayList<>();
        for (Map.Entry<UUID, T> entry : unitsMap.entrySet()) {
              if (entry.getKey().equals(uuid)) {
                target=entry;
                continue;
              }
            rest.add(entry);
        }
        
        unitsMap.clear();
        for(int j=0;j<rest.size();j++){
            if(index==j){
                unitsMap.put(target.getKey(), target.getValue());
            }
            unitsMap.put(rest.get(j).getKey(), rest.get(j).getValue()); 
            
        }
        //if last one
        if(!unitsMap.containsKey(target.getKey())){
            unitsMap.put(target.getKey(), target.getValue()); 
        }
        
    }
    @Override
    public  Transferable createClipboardContent(){        
        return new StringSelection(this.format().toString());
    }
}

