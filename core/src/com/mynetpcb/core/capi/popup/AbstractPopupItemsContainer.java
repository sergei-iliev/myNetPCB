package com.mynetpcb.core.capi.popup;


import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.gui.filter.ImpexFileFilter;
//import com.mynetpcb.core.capi.line.LineBendingProcessor;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.UnitMgr;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;

import com.mynetpcb.d2.shapes.Box;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import java.lang.ref.WeakReference;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

@SuppressWarnings("unchecked")
public abstract class AbstractPopupItemsContainer<T extends UnitComponent> extends JPopupMenu implements ActionListener{
    
    protected int x,y;

    private WeakReference<Shape> weakTargetRef;  
    
    private final WeakReference<T> weakComponentRef;
    
    protected Map<String,Object>  blockMenu;
    
    protected Map<String,Object> basicMenu;

    protected Map<String,Object> unitMenu;
    
    protected Map<String,Object> lineMenu;
    
    protected Map<String,Object> lineSelectMenu;
    
    protected Map<String,Object> chipMenu;
    
    protected Map<String,Object>  shapeMenu; 
    
    public AbstractPopupItemsContainer(T component) {
        this.weakComponentRef=new WeakReference<T>(component);
        this.createBlockMenuItems(); 
        createBasicMenuItems(); 
        lineMenu=new LinkedHashMap<String,Object>();
        chipMenu=new LinkedHashMap<String,Object>(); 
        this.createLineMenuItems();
        this.createChipMenuItems();
        
        this.createUnitMenu();       
        this.createLineSelectMenuItems();
        this.createShapeMenuItems();
    }
    
    public abstract void registerUnitPopup(MouseScaledEvent e,Shape target);
        
    public void registerLinePopup(MouseScaledEvent e, Shape target) {
        initializePopupMenu(e, target, lineMenu);
        Map<String,JMenuItem> submenu=(Map<String,JMenuItem>)lineMenu.get("Bending");
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());
    }

    protected void createLineMenuItems(){
        //***delete last point    
        JMenuItem item=new JMenuItem("Delete last point"); item.setActionCommand("DeleteLastPoint");
        lineMenu.put("DeleteLastPoint",item);
        item=new JMenuItem("Delete line"); item.setActionCommand("deleteline");
        lineMenu.put("DeleteWire",item); 
        item=new JMenuItem("Cancel"); item.setActionCommand("CancelWiring");
        lineMenu.put("CancelWiring",item);  
    }
    
    protected void createBlockMenuItems(){    
        blockMenu=new LinkedHashMap<String,Object>();   
        
        Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 
        JMenuItem item=new JMenuItem("Left"); item.setActionCommand("RotateLeft");item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));        
        submenu.put("RotateLeft",item);  
        item=new JMenuItem("Right"); item.setActionCommand("RotateRight");item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));        
        submenu.put("RotateRight",item);  
        blockMenu.put("Rotate",submenu);


        item=new JMenuItem("Clone"); item.setActionCommand("Clone");                                                                   
        blockMenu.put("Clone",item);
        
        submenu=new LinkedHashMap<String,JMenuItem>(); 
        item = new JMenuItem("Left - Right");item.setActionCommand("LeftRight"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.SHIFT_MASK));
        submenu.put("LeftRight",item);
        item = new JMenuItem("Top - Bottom");item.setActionCommand("TopBottom");item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.SHIFT_MASK));
        submenu.put("TopBottom",item);       
        blockMenu.put("Mirror",submenu);
        
        blockMenu.put("Separator0",null); 
        
        item=new JMenuItem("Delete"); item.setActionCommand("Delete");
        blockMenu.put("Delete",item);           
    }
    
    protected void createChipMenuItems(){  
        Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 
        
        JMenuItem item=new JMenuItem("Edit Symbol"); item.setActionCommand("EditSymbol");
        chipMenu.put("EditSymbol",item);
        
        item=new JMenuItem("Left"); item.setActionCommand("RotateLeft"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        submenu.put("RotateLeft",item);  
        item=new JMenuItem("Right"); item.setActionCommand("RotateRight"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));       
        submenu.put("RotateRight",item);  
        chipMenu.put("Rotate",submenu);

        item=new JMenuItem("Clone"); item.setActionCommand("Clone");
        chipMenu.put("Clone",item);    
        
        submenu=new LinkedHashMap<String,JMenuItem>(); 
        item = new JMenuItem("Left - Right");item.setActionCommand("LeftRight"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.SHIFT_MASK));
        submenu.put("LeftRight",item);
        item = new JMenuItem("Top - Bottom");item.setActionCommand("TopBottom");item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.SHIFT_MASK));
        submenu.put("TopBottom",item);   
        
        chipMenu.put("Mirror",submenu);  
         
        item=new JMenuItem("Delete");item.setActionCommand("Delete");
        chipMenu.put("Delete",item);       
        item=new JMenuItem("Assign Package"); item.setActionCommand("AssignPackage");
        chipMenu.put("SelectPackage",item);      
        //***separator
        chipMenu.put("Separator",null); 
        //connectors
        submenu=new LinkedHashMap<String,JMenuItem>(); 
        item = new JMenuItem("Bind");item.setActionCommand("Bind");
        submenu.put("Bind",item);
        item = new JMenuItem("Unbind");item.setActionCommand("Unbind");
        submenu.put("Unbind",item);         
        chipMenu.put("ChildConnectors",submenu);     
        chipMenu.put("Separator1",null); 
        //wires
        submenu=new LinkedHashMap<String,JMenuItem>(); 
        item=new JMenuItem("Disconnect");item.setActionCommand("DisconnectWires");
        submenu.put("DisconnectWires",item); 
        item=new JMenuItem("Connect");item.setActionCommand("ConnectWires");
        submenu.put("ConnectWires",item);     
        chipMenu.put("Wire ends",submenu);    
         
    }
    private void createBasicMenuItems(){
      basicMenu=new LinkedHashMap<String,Object>();
      JMenuItem item=new JMenuItem("Clone");item.setActionCommand("clone"); 
      basicMenu.put("Clone",item);
      item=new JMenuItem("Delete");item.setActionCommand("delete");
      basicMenu.put("Delete",item); 
    }
    
    private void createLineSelectMenuItems(){
        lineSelectMenu=new LinkedHashMap<String,Object>();
        
        JMenuItem item=new JMenuItem("Clone"); item.setActionCommand("clone");       
        lineSelectMenu.put("Clone",item);  
        
        //***separator
        lineSelectMenu.put("Separator",null); 

        
        item=new JMenuItem("Resume"); item.setActionCommand("Resume");                                                                                    
        lineSelectMenu.put("Resume",item); 
        
        item=new JMenuItem("Add Bending point"); item.setActionCommand("AddBendingPoint");                                                                                    
        lineSelectMenu.put("AddBendingPoint",item);      
        
        item=new JMenuItem("Delete Bending point"); item.setActionCommand("DeleteBendingPoint");                                                                                    
        lineSelectMenu.put("DeleteBendingPoint",item); 
        
        //***separator
        lineSelectMenu.put("Separator1",null);
        
        item=new JMenuItem("Delete"); item.setActionCommand("Delete");       
        lineSelectMenu.put("Delete",item);  
    }
    
    
    public void registerChipPopup(MouseScaledEvent e, Shape target){
       throw new IllegalStateException("Abstract popup menu call is not supported");    
    }
        
    public void registerShapePopup(MouseScaledEvent e,Shape target){  
        initializePopupMenu(e,target,shapeMenu);   
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());             
    }
    public void registerBlockPopup(MouseScaledEvent e,Shape target){
        initializePopupMenu(e,target,blockMenu);              
        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());            
    }  
    
    protected void createShapeMenuItems(){
        shapeMenu=new LinkedHashMap<String,Object>();  
        Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 

        JMenuItem item=new JMenuItem("Left"); item.setActionCommand("RotateLeft"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));        
        submenu.put("RotateLeft",item);  
        item=new JMenuItem("Right"); item.setActionCommand("RotateRight");  item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));        
        submenu.put("RotateRight",item);  
        shapeMenu.put("Rotate",submenu);


        item=new JMenuItem("Clone"); item.setActionCommand("Clone");                                                                   
        shapeMenu.put("Clone",item);
        
        submenu=new LinkedHashMap<String,JMenuItem>(); 
        item = new JMenuItem("Left - Right");item.setActionCommand("LeftRight"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.SHIFT_MASK));
        submenu.put("LeftRight",item);
        item = new JMenuItem("Top - Bottom");item.setActionCommand("TopBottom");item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.SHIFT_MASK));
        submenu.put("TopBottom",item);       
        shapeMenu.put("Mirror",submenu);
        
        //***separator
        shapeMenu.put("Separator2",null);         
        
        item=new JMenuItem("Delete"); item.setActionCommand("Delete");        
        shapeMenu.put("Delete",item);     
        
        
    }
    
    protected void createUnitMenu(){
        unitMenu=new LinkedHashMap<String,Object>();
        //Map<String,JMenuItem> submenu=new LinkedHashMap<String,JMenuItem>(); 
        
        JMenuItem item=new JMenuItem("Select All");item.setActionCommand("selectall"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        unitMenu.put("SelectAll",item);
        //***separator
        unitMenu.put("Separator1",null);

        item=new JMenuItem("Undo");item.setActionCommand("Undo"); item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        unitMenu.put("Undo",item);                                     

        item=new JMenuItem("Redo");item.setActionCommand("Redo");item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        unitMenu.put("Redo",item);                                     
        //***separator
        unitMenu.put("Separator2",null);

        item=new JMenuItem("Import to Project");item.setActionCommand("ImportUnit");                                                 
        unitMenu.put("ImportUnit",item);

        item=new JMenuItem("Load");item.setActionCommand("LoadUnit");                                                 
        unitMenu.put("LoadUnit",item);
        
        item=new JMenuItem("Reload");item.setActionCommand("Reload");
        unitMenu.put("ReloadFromDisk",item);
        
        item=new JMenuItem("Delete");item.setActionCommand("DeleteUnit");
        unitMenu.put("DeleteUnit",item);
        
        item = new JMenuItem("Copy");item.setActionCommand("Copy");
        unitMenu.put("Copy",item);
        item = new JMenuItem("Paste");item.setActionCommand("Paste");
        unitMenu.put("Paste",item);       

        unitMenu.put("Separator3",null);
        item=new JMenuItem("Position drawing to center");item.setActionCommand("positiontocenter");
        unitMenu.put("positiontocenter",item);             
        
    }   
    
    public void registerLineSelectPopup(MouseScaledEvent e, Shape target) {

        initializePopupMenu(e, target, lineSelectMenu);

        Trackable wire = (Trackable)target;
        //***insert logic behind menu options availability
        if (wire.isBendingPointClicked(e.getX(), e.getY())!= null) {
            //***is this an end point
            if (wire.isEndPoint(e.getX(), e.getY())) {
                this.setEnabled(lineSelectMenu, "Resume", true);
            } else {
                this.setEnabled(lineSelectMenu, "Resume", false);
            }
            this.setEnabled(lineSelectMenu, "AddBendingPoint", false);
            this.setEnabled(lineSelectMenu, "DeleteBendingPoint", true);
        } else {
            this.setEnabled(lineSelectMenu, "Resume", false);
            this.setEnabled(lineSelectMenu, "AddBendingPoint", true);
            this.setEnabled(lineSelectMenu, "DeleteBendingPoint", false);
        }

        this.show(e.getComponent(), e.getWindowX(), e.getWindowY());

    }
    
    protected Shape getTarget(){
      return weakTargetRef.get();  
    }
    
    protected T getUnitComponent(){
      return weakComponentRef.get();  
    }
    /*
     * called prior to Menu registration method
     * 1.Clean up Popup Menu container
     * 2.Remember target
     * 3.Remember mouse click position
     * 4.Populate menu items
     */
    protected void initializePopupMenu(MouseScaledEvent e,Shape target,Map<String,Object> itemsMap){
        
        if (this.weakTargetRef != null && this.weakTargetRef.get() != null) {
            if(this.weakTargetRef.get()!=target){
               this.weakTargetRef.clear();
               this.weakTargetRef = new WeakReference<Shape>(target);
            }
             //same   
        }else
           this.weakTargetRef = new WeakReference<Shape>(target);
        //***remove old menu items
        while(this.getSubElements().length>0){  
          this.remove(0);
        }
        x=e.getX();  y=e.getY();

        //****Construct menu dynamically according to the requested action -> itemsMap
        populateMenuItems(null,itemsMap);
    }    
    private void populateMenuItems(JMenu submenu,Map<String,Object> itemsMap){
      Set<String> keys=itemsMap.keySet();      
      for(String key:keys){        
          if(key.startsWith("Separator")){
           if(submenu==null)
             this.addSeparator();
           else
             submenu.addSeparator();  
           continue;  
          }
              
        Object o=itemsMap.get(key);  
          if(o instanceof Map){
            JMenu sub=new JMenu(key);  
            populateMenuItems(sub,(Map<String,Object>)o);
            this.add(sub);
            continue;  
          }
          if(submenu==null){
              JMenuItem item=(JMenuItem)o;
              item.removeActionListener(this);          
              item.addActionListener(this);  
              
            this.add((JMenuItem)o);   //***root popup menu
          }else{   
                JMenuItem item=(JMenuItem)o;
                item.removeActionListener(this);  
                item.addActionListener(this);
              
              submenu.add((JMenuItem)o);   //***submenu
          }    
      }    
    }
    private JMenuItem result;

    private void getCheck(Map map,String itemName){
        if(result!=null)
          return;  
        Set<String> keys=map.keySet();      
        for(String key:keys){        
           if(key.startsWith("Separator")){
            continue;  
           }
            
           Object o=map.get(key);  
           if(o instanceof Map){  
             getCheck((Map<String,Object>)o,itemName);
             continue;  
           }

           JMenuItem item=(JMenuItem)o;
           if(item.getActionCommand().equals(itemName)){         
             result=item;
           }

        }        
    }
    public boolean getChecked(Map map,String itemName){
      result=null;
      getCheck(map,itemName);
      return (result!=null?result.isSelected():false);
    }
    public void setEnabled(Map map,String itemName,boolean enabled){
          Set<String> keys=map.keySet();      
          for(String key:keys){        
              if(key.startsWith("Separator")){
               continue;  
              }
                  
              Object o=map.get(key);  
              if(o instanceof Map){  
                setEnabled((Map<String,Object>)o,itemName,enabled);
                continue;  
              }

              JMenuItem item=(JMenuItem)o;
              if(item.getActionCommand().equals(itemName))
                item.setEnabled(enabled);  
                  
          }    
                   
    }
    
    public void setEnabled(String itemName,boolean visible){
        for(Component item:  this.getComponents()){
            if(item instanceof AbstractButton){
                if(((AbstractButton)item).getActionCommand().equals(itemName)){
                    item.setEnabled(visible); 
                }
            }
         
        }
    }
        public void setVisible(Map map,String itemName,boolean visible){
              Set<String> keys=map.keySet();      
              for(String key:keys){        
                  if(key.startsWith("Separator")){
                   continue;  
                  }
                      
                  Object o=map.get(key);  
                  if(o instanceof Map){
                    setVisible((Map<String,Object>)o,itemName,visible);
                    continue;  
                  }

                  JMenuItem item=(JMenuItem)o;
                  if(item.getActionCommand().equals(itemName))
                    item.setVisible(visible);  
                      
              }    
                       
        }
    
    //iso;ate duplication code from circuit and module popup menu
    public void actionPerformed(ActionEvent e) {
        
        if (e.getActionCommand().endsWith("bend")){            
            //LineBendingProcessor lineBendingProcessor=getUnitComponent().getBendingProcessorFactory().resolve(e.getActionCommand(), getUnitComponent().getLineBendingProcessor());
            //getUnitComponent().setLineBendingProcessor(lineBendingProcessor);
            return;
        }
        if (e.getActionCommand().equalsIgnoreCase("deleteline")) {
              getUnitComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.DELETE_MEMENTO));
              getUnitComponent().getEventMgr().resetEventHandle();
              getUnitComponent().getModel().getUnit().delete(getTarget().getUUID());
              getUnitComponent().Repaint();                    
        } 
        
        if (e.getActionCommand().equalsIgnoreCase("cancelwiring")) {
            //****empty circuit space could be right clicked without a wire beneath
//            getUnitComponent().getLineBendingProcessor().Release();  
//            getTarget().setSelected(false);
//            getUnitComponent().getEventMgr().resetEventHandle();
//            getUnitComponent().getDialogFrame().setButtonGroup(0x00);
//            getUnitComponent().setMode(0x00);
//            getUnitComponent().Repaint(); 

        }
        if (e.getActionCommand().equalsIgnoreCase("deletelastpoint")) {
            Trackable line = (Trackable)getTarget();
            line.deleteLastPoint();

            if (((Trackable)getTarget()).getLinePoints().size() <= 1) {
                getUnitComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.DELETE_MEMENTO));
                getUnitComponent().getEventMgr().resetEventHandle();
                getUnitComponent().getModel().getUnit().delete(getTarget().getUUID());
            }
            //else{
            //    getUnitComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
            //}

            getUnitComponent().Repaint();
        }
        if(e.getActionCommand().equalsIgnoreCase("reload")){
            getUnitComponent().reload();
        }
        if (e.getActionCommand().equalsIgnoreCase("ImportUnit")) {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            fc.setDialogTitle("Import to Project");
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new ImpexFileFilter(".xml"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                    if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".xml")) {
                        getUnitComponent().Import(fc.getSelectedFile().getAbsolutePath());                        
                    } else {
                        getUnitComponent().Import( fc.getSelectedFile().getAbsolutePath() + ".xml");                        
                    }
                  
            }          
          return;
        }
        if(e.getActionCommand().equalsIgnoreCase("topbottom")||e.getActionCommand().equalsIgnoreCase("leftright")){   
//            Collection<Shape> shapes= getUnitComponent().getModel().getUnit().getSelectedShapes(false);
//            Rectangle r=getUnitComponent().getModel().getUnit().getShapesRect(shapes);
//            getUnitComponent().getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
//            Point p=getUnitComponent().getModel().getUnit().getGrid().positionOnGrid((int)r.getCenterX(),(int)r.getCenterY());      
//            UnitMgr unitMgr = new UnitMgr();
//            if(e.getActionCommand().equalsIgnoreCase("topbottom")){
//                unitMgr.mirrorBlock(getUnitComponent().getModel().getUnit(),new Point(p.x-10,p.y),new Point(p.x+10,p.y));
//            }else{
//                unitMgr.mirrorBlock(getUnitComponent().getModel().getUnit(),new Point(p.x,p.y-10),new Point(p.x,p.y+10));
//            }
//            
//            unitMgr.alignBlock(getUnitComponent().getModel().getUnit().getGrid(),shapes);
//            unitMgr.normalizePinText(shapes);
//            getUnitComponent().getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento( MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
//            getUnitComponent().Repaint();
        } 
        if(e.getActionCommand().equalsIgnoreCase("rotateleft")||e.getActionCommand().equalsIgnoreCase("rotateright")){ 
//            Collection<Shape> shapes= getUnitComponent().getModel().getUnit().getSelectedShapes(false);
//            getUnitComponent().getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
//            Rectangle r=getUnitComponent().getModel().getUnit().getShapesRect(shapes);  
//            UnitMgr unitMgr = new UnitMgr();
//            unitMgr.rotateBlock(shapes,AffineTransform.getRotateInstance((e.getActionCommand().equalsIgnoreCase("rotateleft")?-1:1)*Math.PI/2,r.getCenterX(),r.getCenterY()));   
//            unitMgr.alignBlock(getUnitComponent().getModel().getUnit().getGrid(),shapes);  
//            //skip if single pin
//            unitMgr.normalizePinText(shapes);
//            getUnitComponent().getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
//            getUnitComponent().Repaint();
        }
        if(e.getActionCommand().equalsIgnoreCase("deleteunit")){  
            if(getUnitComponent().getModel().isChanged(getUnitComponent().getModel().getUnit().getUUID())){                        
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(getUnitComponent().getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Delete", JOptionPane.YES_NO_OPTION)) {                                       
                    return;
                }                      
            }
            getUnitComponent().getModel().Delete(getUnitComponent().getModel().getUnit().getUUID());
            if (getUnitComponent().getModel().getUnits().size() > 0) {
                getUnitComponent().getModel().setActiveUnit(0);
                getUnitComponent().revalidate();
                getUnitComponent().getModel().fireUnitEvent(new UnitEvent(getUnitComponent().getModel().getUnit(), UnitEvent.SELECT_UNIT));
            }else{
                getUnitComponent().clear();
                getUnitComponent().fireContainerEvent(new ContainerEvent(null, ContainerEvent.DELETE_CONTAINER));
            }
            getUnitComponent().componentResized(null);
            getUnitComponent().revalidate();
            getUnitComponent().Repaint();          
        }  
        
        if(e.getActionCommand().equals("selectall")){ 
            getUnitComponent().getModel().getUnit().setSelected(true);
            getUnitComponent().Repaint();
            return;  
        }
        
        if(e.getActionCommand().equals("Undo")){
         getUnitComponent().getModel().getUnit().undo(null);
         getUnitComponent().Repaint();
         getUnitComponent().revalidate();
         return;
        }
        
        if(e.getActionCommand().equals("Redo")){
         getUnitComponent().getModel().getUnit().redo();
         getUnitComponent().Repaint();
         getUnitComponent().revalidate();        
         return;
        }   
        if (e.getActionCommand().equalsIgnoreCase("delete")) {
              UnitMgr unitMgr=new UnitMgr();
              getUnitComponent().getModel().getUnit().registerMemento(new CompositeMemento(MementoType.DELETE_MEMENTO).add(getUnitComponent().getModel().getUnit().getSelectedShapes(false)));
              unitMgr.deleteBlock(getUnitComponent().getModel().getUnit(),getUnitComponent().getModel().getUnit().getSelectedShapes(false));
              getUnitComponent().Repaint();            
       
        }        
        
        if(e.getActionCommand().equalsIgnoreCase("clone")){  
            UnitMgr unitMgr = new UnitMgr();
            unitMgr.cloneBlock(getUnitComponent().getModel().getUnit(),getUnitComponent().getModel().getUnit().getSelectedShapes(true));
            Collection<Shape> shapes= getUnitComponent().getModel().getUnit().getSelectedShapes(false); 
            Box r=getUnitComponent().getModel().getUnit().getShapesRect(shapes);
            unitMgr.moveBlock(shapes,
                                 r.getWidth(),r.getHeight());
            unitMgr.alignBlock(getUnitComponent().getModel().getUnit().getGrid(),
                                  shapes);
            
            getUnitComponent().getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.CREATE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.CREATE_MEMENTO));                                            
            getUnitComponent().getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                                                                  
            getUnitComponent().Repaint();
            //***emit property event change
            if (shapes.size() == 1) {
               getUnitComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(shapes.iterator().next(), ShapeEvent.SELECT_SHAPE));
            }             
            return;              
        }
        
        if(e.getActionCommand().equalsIgnoreCase("Copy")){
            ClipboardMgr.getInstance().setClipboardContent(Clipboardable.Clipboard.LOCAL,getUnitComponent().getModel().getUnit().createClipboardContent());    
        }
        if(e.getActionCommand().equalsIgnoreCase("Paste")){
            UnitMgr unitMgr=new UnitMgr();            
            getUnitComponent().getModel().getUnit().setSelected(false);
            getUnitComponent().getModel().getUnit().realizeClipboardContent(ClipboardMgr.getInstance().getClipboardContent(Clipboardable.Clipboard.LOCAL));
            unitMgr.locateBlock(getUnitComponent().getModel().getUnit(),
                                                 getUnitComponent().getModel().getUnit().getSelectedShapes(false),
                                                 x, y);

            unitMgr.alignBlock(getUnitComponent().getModel().getUnit().getGrid(),
                                                getUnitComponent().getModel().getUnit().getSelectedShapes(false));
            getUnitComponent().Repaint();            
        } 
        if(e.getActionCommand().equals("positiontocenter")){
//            Unit unit=getUnitComponent().getModel().getUnit();
//            unit.registerMemento(new CompositeMemento(MementoType.MOVE_MEMENTO).Add(unit.getShapes()));  
//            int x=(int)unit.getBoundingRect().getCenterX();
//            int y=(int)unit.getBoundingRect().getCenterY();
//            UnitMgr unitMgr=new UnitMgr();
//            unitMgr.moveBlock(unit.getShapes(), (unit.getWidth()/2)-x, (unit.getHeight()/2)-y);
//            unitMgr.alignBlock(unit.getGrid(),unit.getShapes());
//            unit.registerMemento(new CompositeMemento(MementoType.MOVE_MEMENTO).Add(unit.getShapes())); 
//            //scroll to center
//            getUnitComponent().setScrollPosition((unit.getWidth()/2), (unit.getHeight()/2));
//            getUnitComponent().Repaint();
        } 
        
        if (e.getActionCommand().equalsIgnoreCase("DeleteBendingPoint")) {
            if (((Trackable)getTarget()).getLinePoints().size() == 2){
                //remeber last 2 points to restore
                getUnitComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.DELETE_MEMENTO));
            }else{
                getUnitComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
            }
            ((Trackable)getTarget()).removePoint(x,y);
            //***delete wire if one point remains only
            if (((Trackable)getTarget()).getLinePoints().size() == 1) {
                getUnitComponent().getEventMgr().resetEventHandle();
                getUnitComponent().getModel().getUnit().delete(getTarget().getUUID());
            }
            getUnitComponent().Repaint();
        }
        
        if (e.getActionCommand().equalsIgnoreCase("AddBendingPoint")) {
             ((Trackable)getTarget()).insertPoint(x, y);             
             getUnitComponent().Repaint();
        }
        
        if (e.getActionCommand().equalsIgnoreCase("LoadUnit")) {
//            AbstractLoadDialog.Builder builder=getUnitComponent().getLoadDialogBuilder();
//            AbstractLoadDialog loadDialog =builder.setWindow(getUnitComponent().getDialogFrame().getParentFrame()).setCaption("Load "+getUnitComponent().getModel().getUnit().toString()).setEnabled(true).build();
//            
//            loadDialog.pack();
//            loadDialog.setLocationRelativeTo(null); //centers on screen
//            loadDialog.setVisible(true);
//
//            if(loadDialog.getSelectedModel()==null||loadDialog.getSelectedModel().getUnit()==null){
//                loadDialog.dispose();
//                loadDialog=null;
//                return;
//            }
//            UnitMgr unitMgr=new UnitMgr();
//            try {
//                unitMgr.Load(getUnitComponent().getModel().getUnit(),loadDialog.getSelectedModel().getUnit());
//            } catch (CloneNotSupportedException f) {
//                            f.printStackTrace(System.out);
//                        }
//                        
//            loadDialog.dispose();
//            loadDialog=null;
//                        
//            getUnitComponent().getModel().fireUnitEvent(new UnitEvent(getUnitComponent().getModel().getUnit(), UnitEvent.RENAME_UNIT));
//            getUnitComponent().getModel().fireUnitEvent(new UnitEvent(getUnitComponent().getModel().getUnit(), UnitEvent.SELECT_UNIT));
//            getUnitComponent().getModel().setActiveUnit(getUnitComponent().getModel().getUnit().getUUID());
//                        
//                        //***refresh scrollbars
//            getUnitComponent().componentResized(null);             
//            getUnitComponent().Repaint();
//            
//                        //position on center
//            Rectangle r=getUnitComponent().getModel().getUnit().getBoundingRect();
//            getUnitComponent().setScrollPosition((int)r.getCenterX(),(int)r.getCenterY());
//            
//            getUnitComponent().requestFocusInWindow(); //***for the cancel button  
//            return;                        
        }           
        
    }

}

