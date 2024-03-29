package com.mynetpcb.pad.dialog;


import com.mynetpcb.core.capi.shape.CoordinateSystem;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.credentials.User;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.WriteUnitLocal;
import com.mynetpcb.core.capi.io.remote.WriteConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.dialog.panel.FootprintsPanel;
import com.mynetpcb.pad.dialog.save.FootprintSaveDialog;
import com.mynetpcb.pad.unit.Footprint;
import com.mynetpcb.pad.unit.FootprintMgr;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

public class FootprintEditorDialog extends JDialog implements DialogFrame,CommandListener, ActionListener {

    protected FootprintComponent footprintComponent;
    private JPanel basePanel;

    private FootprintsPanel footprintsPanel;
    private JPanel footprintBasePanel = new JPanel(new GridBagLayout());
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    //private JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
    //private JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL);

    private JToggleButton RectButton = new JToggleButton();
    private JToggleButton EllipseButton = new JToggleButton();
    private JToggleButton ArcButton = new JToggleButton();
    private JToggleButton SelectionButton = new JToggleButton();
    private JToggleButton LineButton = new JToggleButton();
    private JToggleButton PadButton = new JToggleButton();
    private JToggleButton LabelButton = new JToggleButton();
    private JToggleButton SnapToGridButton = new JToggleButton();
    private JToggleButton CoordButton = new JToggleButton();
    private JToggleButton MeasureButton = new JToggleButton();
    private JToggleButton SolidRegionButton = new JToggleButton();
    private ButtonGroup group = new ButtonGroup();


    
    protected JButton SaveButton = new JButton();
    private JButton ScaleIn = new JButton();
    private JButton ScaleOut = new JButton();
    private JButton RotateLeft = new JButton();
    private JButton RotateRight = new JButton();
    private JToggleButton DragHeand = new JToggleButton();
    private JButton PositionToCenter = new JButton();

    private JPanel NorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel WestPanel = new JPanel();
    private JPanel EastPanel = new JPanel();
    private JPanel SouthPanel = new JPanel();
    private JPanel leftButtonGroupPanel = new JPanel();

    public FootprintEditorDialog(Window f, String caption) {
       this(f,caption,null);
    }
    public FootprintEditorDialog(Window f, String Caption,FootprintContainer footprintContainer) {
        super(f, Caption, Dialog.ModalityType.DOCUMENT_MODAL);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(true);
        Init();
        //set size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension((int)(2*screenSize.getWidth()/3),(int)(2*screenSize.getHeight()/3)));
        
        LoadFootprints(footprintContainer); 
    }
    private void Init() {
        Container content = this.getContentPane();
        this.setPreferredSize(new Dimension(750, 600));
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());


        footprintComponent = new FootprintComponent(this);
        footprintsPanel = new FootprintsPanel(footprintComponent);

        footprintComponent.setPreferredSize(new Dimension(700, 500));
        footprintComponent.addContainerListener(footprintsPanel);
        footprintComponent.getModel().addUnitListener(footprintsPanel);
        footprintComponent.getModel().addShapeListener(footprintsPanel);
        //register key hook
        FootprintComponent.getUnitKeyboardListener().setComponent(footprintComponent);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        footprintBasePanel.add(footprintComponent, gridBagConstraints);

        //gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        //gridBagConstraints.gridx = 1;
        //gridBagConstraints.gridy = 0;
        //gridBagConstraints.weightx = 0.001;
        //gridBagConstraints.weighty = 0.001;
        //footprintBasePanel.add(vbar, gridBagConstraints);

        //gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        //gridBagConstraints.gridx = 0;
        //gridBagConstraints.gridy = 1;
        //gridBagConstraints.weightx = 0.001;
        //gridBagConstraints.weighty = 0.001;
        //footprintBasePanel.add(hbar, gridBagConstraints);

        //gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        //gridBagConstraints.gridx = 1;
        //gridBagConstraints.gridy = 1;
        //footprintBasePanel.add(new JPanel(), gridBagConstraints);
        basePanel.add(footprintBasePanel, BorderLayout.CENTER);

        SelectionButton.addActionListener(this);
        SelectionButton.setSelected(true);
        SelectionButton.setIcon(Utilities.loadImageIcon(this, "images/selection.png"));
        SelectionButton.setToolTipText("Selection");
        SelectionButton.setPreferredSize(new Dimension(35, 35));

        RectButton.addActionListener(this);
        RectButton.setIcon(Utilities.loadImageIcon(this, "images/rect.png"));
        RectButton.setToolTipText("Add Rectangle");
        RectButton.setPreferredSize(new Dimension(35, 35));

        SolidRegionButton.addActionListener(this);
        SolidRegionButton.setIcon(Utilities.loadImageIcon(this, "images/solid_region.png"));
        SolidRegionButton.setToolTipText("Add Solid Region");
        SolidRegionButton.setPreferredSize(new Dimension(35, 35));
        
        EllipseButton.addActionListener(this);
        EllipseButton.setIcon(Utilities.loadImageIcon(this, "images/ellipse.png"));
        EllipseButton.setToolTipText("Add Circle");
        EllipseButton.setPreferredSize(new Dimension(35, 35));

        ArcButton.addActionListener(this);
        ArcButton.setIcon(Utilities.loadImageIcon(this, "images/arc.png"));
        ArcButton.setToolTipText("Add Arc");
        ArcButton.setPreferredSize(new Dimension(35, 35));

        LineButton.addActionListener(this);
        LineButton.setIcon(Utilities.loadImageIcon(this, "images/linepoligone.png"));
        LineButton.setToolTipText("Add Line or Poligone");
        LineButton.setPreferredSize(new Dimension(35, 35));

        PadButton.addActionListener(this);
        PadButton.setToolTipText("Add Pad");
        PadButton.setIcon(Utilities.loadImageIcon(this, "images/pad.png"));
        PadButton.setPreferredSize(new Dimension(35, 35));

        LabelButton.addActionListener(this);
        LabelButton.setToolTipText("Add Label");
        LabelButton.setIcon(Utilities.loadImageIcon(this, "images/label.png"));
        LabelButton.setPreferredSize(new Dimension(35, 35));

        SnapToGridButton.addActionListener(this);
        SnapToGridButton.setToolTipText("Snap dragging point to grid");
        SnapToGridButton.setIcon(Utilities.loadImageIcon(this, "images/anchor.png"));
        SnapToGridButton.setPreferredSize(new Dimension(35, 35));

        CoordButton.addActionListener(this);
        CoordButton.setToolTipText("Change coordinate origin");
        CoordButton.setIcon(Utilities.loadImageIcon(this, "images/origin.png"));
        CoordButton.setPreferredSize(new Dimension(35, 35));

        MeasureButton.setPreferredSize(new Dimension(35, 35));
        MeasureButton.setToolTipText("Distance measurement");
        MeasureButton.addActionListener(this);
        MeasureButton.setIcon(Utilities.loadImageIcon(this, "images/measure.png"));
        

        SaveButton.addActionListener(this);
        SaveButton.setToolTipText("Save Footprint");
        SaveButton.setPreferredSize(new Dimension(35, 35));
        SaveButton.setIcon(Utilities.loadImageIcon(this, "images/save.png"));


        ScaleIn.addActionListener(this);
        ScaleIn.setToolTipText("Scale In");
        ScaleIn.setPreferredSize(new Dimension(35, 35));
        ScaleIn.setIcon(Utilities.loadImageIcon(this, "images/zoom_out.png"));

        ScaleOut.addActionListener(this);
        ScaleOut.setPreferredSize(new Dimension(35, 35));
        ScaleOut.setActionCommand("ScaleOut");
        ScaleOut.setIcon(Utilities.loadImageIcon(this, "images/zoom_in.png"));

        RotateLeft.addActionListener(this);
        RotateLeft.setToolTipText("Rotate Left");
        RotateLeft.setPreferredSize(new Dimension(35, 35));
        RotateLeft.setIcon(Utilities.loadImageIcon(this, "images/rotate_left.png"));

        RotateRight.addActionListener(this);
        RotateRight.setToolTipText("Rotate Right");
        RotateRight.setPreferredSize(new Dimension(35, 35));
        RotateRight.setIcon(Utilities.loadImageIcon(this, "images/rotate_right.png"));

        DragHeand.setPreferredSize(new Dimension(35, 35));
        DragHeand.setToolTipText("Drag canvas");
        DragHeand.addActionListener(this);
        DragHeand.setIcon(Utilities.loadImageIcon(this, "images/grab.png"));

        PositionToCenter.setPreferredSize(new Dimension(35, 35));
        PositionToCenter.setToolTipText("Position viewport to center");
        PositionToCenter.addActionListener(this);
        PositionToCenter.setIcon(Utilities.loadImageIcon(this, "images/tocenter.png"));

        NorthPanel.add(SaveButton);
        NorthPanel.add(ScaleIn);
        NorthPanel.add(ScaleOut);
        NorthPanel.add(RotateLeft);
        NorthPanel.add(RotateRight);
        NorthPanel.add(DragHeand);
        NorthPanel.add(PositionToCenter);


        //***Add buttons to group
        group.add(SelectionButton);        
        group.add(EllipseButton);
        group.add(ArcButton);
        group.add(LineButton);
        group.add(RectButton);    
        group.add(SolidRegionButton);    
        group.add(PadButton);
        group.add(LabelButton);
        group.add(DragHeand);
        group.add(MeasureButton);
        
        EastPanel.setLayout(new BorderLayout());
        EastPanel.setPreferredSize(new Dimension(200, 200));


        WestPanel.setLayout(new BorderLayout());
        basePanel.add(NorthPanel, BorderLayout.NORTH);
        EastPanel.add(footprintsPanel, BorderLayout.CENTER);

        basePanel.add(EastPanel, BorderLayout.EAST);
        basePanel.add(SouthPanel, BorderLayout.SOUTH);

        leftButtonGroupPanel.setLayout(new BoxLayout(leftButtonGroupPanel, BoxLayout.Y_AXIS));
        leftButtonGroupPanel.setBorder(BorderFactory.createEmptyBorder(35, 4, 0, 4));
        leftButtonGroupPanel.add(SelectionButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(EllipseButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(ArcButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(LineButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(RectButton);                
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SolidRegionButton);                
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(PadButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(LabelButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SnapToGridButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(CoordButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(MeasureButton);
        
        WestPanel.add(leftButtonGroupPanel, BorderLayout.NORTH);
        basePanel.add(WestPanel, BorderLayout.WEST);


        content.add(basePanel); // Add components to the content

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
exit();
            }
        });

    }

    @Override
    public Frame getParentFrame() {
        return null;
    }

    @Override
    public void setButtonGroup(int requestedMode) {
        if (requestedMode == Mode.COMPONENT_MODE) {
            group.setSelected(SelectionButton.getModel(), true);
        }
        
        if(requestedMode==Mode.LINE_MODE){
            group.setSelected(LineButton.getModel(), true);            
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("exit")){
            exit();
            return;
        }
        if (e.getActionCommand().equals("Create")) {
            if(footprintComponent.getModel().isChanged()){                        
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(footprintComponent.getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Create", JOptionPane.YES_NO_OPTION)) {                                       
                    return;
                }                      
            }
            footprintComponent.clear();                              
        }
        
        if (e.getActionCommand().equals("Add")||e.getActionCommand().equals("Create")) {  
            //rememeber current unit position
            if(footprintComponent.getModel().getUnit()!=null){
                footprintComponent.getModel().getUnit().setViewportPositionValue(footprintComponent.getViewportWindow().getX(),footprintComponent.getViewportWindow().getY());                      
            }
            Footprint footprint = new Footprint((int)Grid.MM_TO_COORD(50), (int)Grid.MM_TO_COORD(50));
            footprintComponent.getModel().add(footprint);
            footprintComponent.getModel().setActiveUnit(footprint.getUUID());
            footprintComponent.componentResized(null);
            footprintComponent.getModel().fireUnitEvent(new UnitEvent(footprint, UnitEvent.SELECT_UNIT));
            footprintComponent.Repaint();
        }

        if (footprintComponent.getModel().getUnit() == null) {
            return;
        }
        
        if(e.getActionCommand().equals("export.clipboard")){            
            ClipboardMgr.getInstance().setClipboardContent(Clipboardable.Clipboard.SYSTEM, new StringSelection(footprintComponent.getModel().format().toString()));
        }
        
        if (e.getActionCommand().equals("Save")||e.getActionCommand().equals("SaveAs")) {

            //could be a freshly imported circuit with no library/project name
            if(e.getActionCommand().equals("Save")){
              if (footprintComponent.getModel().getLibraryName() == null||footprintComponent.getModel().getLibraryName().length()==0) {
                  new FootprintSaveDialog(this.getParentFrame(), footprintComponent,Configuration.get().isIsOnline()).build();
                  return;
              }
            }else{
                new FootprintSaveDialog(this.getParentFrame(), footprintComponent,Configuration.get().isIsOnline()).build();
                return;                
            }            
            
            if (Configuration.get().isIsOnline() && User.get().isAnonymous()) {
                User.showMessageDialog(footprintComponent.getDialogFrame().getParentFrame(), "Anonymous access denied.");
                return;
            }
            //could be a freshly imported circuit with no library/project name
            if(e.getActionCommand().equals("Save")){
              if(Configuration.get().isIsOnline()&&User.get().isAnonymous()){
                   User.showMessageDialog(footprintComponent.getDialogFrame().getParentFrame(),"Anonymous access denied."); 
                   return;
              }                
              if (footprintComponent.getModel().getLibraryName() == null||footprintComponent.getModel().getLibraryName().length()==0) {
                 (new FootprintSaveDialog(this.getParentFrame(), footprintComponent,Configuration.get().isIsOnline())).build();
                  return;
              }
            }else{
                (new FootprintSaveDialog(this.getParentFrame(), footprintComponent,Configuration.get().isIsOnline())).build();
                return;                
            }
            
            //save the file
            if (!Configuration.get().isIsApplet()) {
                Command writer =
                    new WriteUnitLocal(this, footprintComponent.getModel().format(),
                                       Configuration.get().getFootprintsRoot(),
                                       footprintComponent.getModel().getLibraryName(), null,
                                       footprintComponent.getModel().getFileName(), true, FootprintComponent.class);
                CommandExecutor.INSTANCE.addTask("WriteUnitLocal", writer);
            } else {
                Command writer =
                    new WriteConnector(this, footprintComponent.getModel().format(),
                                       new RestParameterMap.ParameterBuilder("/footprints").addURI(footprintComponent.getModel().getLibraryName()).addURI(footprintComponent.getModel().getFormatedFileName()).addAttribute("overwrite",
                                                                                                                                                                                                                      String.valueOf(true)).build(),
                                       FootprintComponent.class);
                CommandExecutor.INSTANCE.addTask("WriteUnit", writer);
            }            
             
        }
        if (e.getSource()==ScaleIn) {
            footprintComponent.zoomOut(new Point((int)footprintComponent.getVisibleRect().getCenterX(),
                                                (int)footprintComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==ScaleOut) {
            footprintComponent.zoomIn(new Point((int)footprintComponent.getVisibleRect().getCenterX(),
                                                 (int)footprintComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==RotateLeft || e.getSource()==RotateRight) {        
            Collection<Shape> shapes= footprintComponent.getModel().getUnit().getShapes();
            if(shapes.size()==0){
               return; 
            }   
            //***notify undo manager                    
            footprintComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
            com.mynetpcb.d2.shapes.Box r=footprintComponent.getModel().getUnit().getShapesRect(shapes);  
            
            FootprintMgr.getInstance().rotateBlock(shapes,
                                   ((e.getSource()==RotateLeft?
                                                                      1 :
                                                                      -1) *90),
                                                                     r.getCenter()); 
            FootprintMgr.getInstance().alignBlock(footprintComponent.getModel().getUnit().getGrid(),shapes);                     

            //***notify undo manager
            footprintComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
            footprintComponent.Repaint();
        }
        
        if (e.getSource()==PositionToCenter) {
            footprintComponent.setViewportPosition(footprintComponent.getModel().getUnit().getWidth() / 2,
                                                 footprintComponent.getModel().getUnit().getHeight() / 2);
            footprintComponent.setViewportPosition(footprintComponent.getModel().getUnit().getWidth() / 2,
            		footprintComponent.getModel().getUnit().getHeight() / 2);
            footprintComponent.Repaint();            
        }

        if (e.getSource()==RectButton) {
            footprintComponent.setMode(Mode.RECT_MODE);
        }
        if (e.getSource()==SolidRegionButton) {
            footprintComponent.setMode(Mode.SOLID_REGION);
        }
        if (e.getSource()==EllipseButton) {
            footprintComponent.setMode(Mode.ELLIPSE_MODE);
        }
        if (e.getSource()==ArcButton) {
            footprintComponent.setMode(Mode.ARC_MODE);
        }
        if (e.getSource()==LineButton) {
            footprintComponent.setMode(Mode.LINE_MODE);
        }
        
        if (e.getSource()==PadButton) {
            footprintComponent.setMode(Mode.PAD_MODE);
        }
        
        if (e.getSource()==LabelButton) {
            footprintComponent.setMode(Mode.LABEL_MODE);
        }
        
        if (e.getSource()==DragHeand) {
            footprintComponent.setMode(Mode.DRAGHEAND_MODE);
        }
        if (e.getSource()==SnapToGridButton) {
            footprintComponent.setParameter("snaptogrid", ((JToggleButton)e.getSource()).getModel().isSelected());
        }
        if(e.getSource()==CoordButton){ 
            if(CoordButton.getModel().isSelected()){
               footprintComponent.getModel().getUnit().setCoordinateSystem(new CoordinateSystem(footprintComponent.getModel().getUnit()));
               footprintComponent.setMode(Mode.ORIGIN_SHIFT_MODE);
            }else{
               footprintComponent.getModel().getUnit().deleteCoordinateSystem(); 
               footprintComponent.setMode(Mode.COMPONENT_MODE); 
            }
        }
        if (e.getSource()==SelectionButton) {
            footprintComponent.setMode(Mode.COMPONENT_MODE);
        }
        if (e.getSource()==MeasureButton) {
            footprintComponent.setMode(Mode.MEASUMENT_MODE);
        }
    }
    @Override
    public void onStart(Class<?> receiver) {
        if(receiver==FootprintComponent.class){
            DisabledGlassPane.block(this.getRootPane(), "Saving..."); 
        }
    }

    @Override
    public void onRecive(String string, Class receiver) {
    }
    
    @Override
    public void onFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getRootPane());        
        
        if(receiver==FootprintComponent.class){ 
           footprintComponent.getModel().registerInitialState();
        }
    }

    @Override
    public void onError(String message) {
    }    

/**
     *Create,load footprint
     * @param source 
     */
    private void LoadFootprints(FootprintContainer source) {
        footprintComponent.clear();
        footprintComponent.setMode(Mode.COMPONENT_MODE);
        if(source==null){
            Footprint footprint=new Footprint((int)Grid.MM_TO_COORD(50),(int)Grid.MM_TO_COORD(50)); 
            footprintComponent.getModel().add(footprint);
        }else{
        for (Footprint footprint : source.getUnits()) {
            try {
                Footprint copy = footprint.clone();
                copy.getScalableTransformation().reset(0.5,10,4,13);
                footprintComponent.getModel().add(copy);
                copy.notifyListeners(ShapeEvent.ADD_SHAPE);
            } catch (CloneNotSupportedException f) {
                f.printStackTrace(System.out);
            }
        }
        }
        footprintComponent.getModel().setLibraryName(source!=null?source.getLibraryName():"");
        footprintComponent.getModel().setCategoryName(source!=null?source.getCategoryName():"");
        footprintComponent.getModel().setFileName(source!=null?source.getFileName():"Footprints");
        footprintComponent.getModel().setDesignerName(source!=null?source.getDesignerName():"");
        footprintComponent.getModel().setActiveUnit(0);
        footprintComponent.componentResized(null);
        footprintComponent.getModel().getUnit().setSelected(false);
        footprintComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.RENAME_CONTAINER));
        footprintComponent.getModel().fireUnitEvent(new UnitEvent(footprintComponent.getModel().getUnit(),
                                                                  UnitEvent.SELECT_UNIT));

        //align to grid in board in-line editing
        FootprintMgr.getInstance().alignBlock(footprintComponent.getModel().getUnit().getGrid(), footprintComponent.getModel().getUnit().getShapes());

        //position all to symbol center
        for(Unit unit:footprintComponent.getModel().getUnits()){
            com.mynetpcb.d2.shapes.Box r=unit.getBoundingRect();
            com.mynetpcb.d2.shapes.Point pt=r.min.clone();
            pt.scale(unit.getScalableTransformation().getCurrentTransformation().getScaleX());            
            unit.setViewportPositionValue(pt.x,pt.y);            
        }
        //position to symbol center
        com.mynetpcb.d2.shapes.Box r=footprintComponent.getModel().getUnit().getBoundingRect();
        footprintComponent.setViewportPosition(r.getCenter().x,r.getCenter().y);

        //remember state
        footprintComponent.getModel().registerInitialState();
        footprintComponent.Repaint();
    }

    private void exit(){
        if(footprintComponent.getModel().isChanged()){
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(FootprintEditorDialog.this, "There is a changed element.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {
                return;
            }
        }
        footprintComponent.release();
        FootprintEditorDialog.this.dispose();
    }
    public static void main(String[] args) {
        //***initialize configuration
        Configuration.Initilize(false);
        Configuration.get().read();
        //*****************Footprint editor
        final FootprintEditorDialog f = new FootprintEditorDialog(null, "Footprint Editor");
        f.setPreferredSize(new Dimension(730, 600));
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {

            }
        });
        f.pack();
        f.setVisible(true);


    }
}
