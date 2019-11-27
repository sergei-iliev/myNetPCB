package com.mynetpcb.board.dialog;


import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.dialog.panel.BoardsPanel;
import com.mynetpcb.board.dialog.panel.FootprintsPanel;
import com.mynetpcb.board.dialog.panel.LayersPanel;
import com.mynetpcb.board.dialog.print.BoardPrintDialog;
import com.mynetpcb.board.dialog.save.BoardImageExportDialog;
import com.mynetpcb.board.dialog.save.BoardSaveDialog;
import com.mynetpcb.board.dialog.save.GerberExportDialog;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.board.unit.BoardMgr;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.credentials.User;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.gui.filter.ImpexFileFilter;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.impex.ImpexProcessor;
import com.mynetpcb.core.capi.impex.XMLExporter;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.WriteUnitLocal;
import com.mynetpcb.core.capi.io.remote.WriteConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.popup.JPopupButton;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.pad.dialog.FootprintLoadDialog;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;


public class BoardEditorDialog extends JDialog implements DialogFrame,CommandListener,ActionListener{
    
    private BoardComponent boardComponent;
    private BoardsPanel boardsPanel;
    private FootprintsPanel footprintsPanel;
    private LayersPanel layersPanel;
    
    private JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
    private JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL);
    private  GridBagLayout gridBagLayout=new GridBagLayout();    
    private JPanel moduleBasePanel=new JPanel(gridBagLayout);
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    
    
    private JPanel NorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel WestPanel = new JPanel();
    
    private JPanel SouthPanel = new JPanel();
    private JPanel leftButtonGroupPanel = new JPanel();

    private JButton FootprintButton = new JButton();
    private JToggleButton EllipseButton = new JToggleButton();
    private JToggleButton SelectionButton = new JToggleButton();
    private JToggleButton ArcButton = new JToggleButton();
    private JToggleButton TrackButton = new JToggleButton();
    private JToggleButton LineButton = new JToggleButton();
    private JToggleButton RectButton = new JToggleButton();
    private JToggleButton ViaButton = new JToggleButton();
    private JToggleButton HoleButton = new JToggleButton();
    private JToggleButton LabelButton = new JToggleButton();
    private JToggleButton CopperAreaButton = new JToggleButton();
    private JToggleButton SnapToGridButton = new JToggleButton();
    private JToggleButton CoordButton = new JToggleButton();
    private JToggleButton MeasureButton = new JToggleButton();
    private JToggleButton SolidRegionButton = new JToggleButton();
    private ButtonGroup group = new ButtonGroup();
    
    private JPopupButton AddBoardButton=new JPopupButton(this);
    private JButton PrintButton = new JButton();
    private JButton SaveButton = new JButton();
    private JButton LoadButton = new JButton();
    private JButton ScaleIn = new JButton();
    private JButton ScaleOut = new JButton();
    private JButton RotateLeft=new JButton();
    private JButton RotateRight=new JButton();  
    private JToggleButton DragHeand = new JToggleButton();
    private JButton PositionToCenter = new JButton();

    
    private JPanel basePanel;
    private JTabbedPane tabbedPane = new JTabbedPane();
    
    public BoardEditorDialog(Frame frame, String caption) {
        this(frame,caption,null);
    }
    public BoardEditorDialog(Window f, String Caption,BoardContainer boardContainer) {
        super(f, Caption, Dialog.ModalityType.DOCUMENT_MODAL);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(true);
        Init();
        LoadBoards(boardContainer); 
    }
    private void Init() {
        Container content = this.getContentPane();
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        
        //***set module component        
        boardComponent=new BoardComponent(this);
        boardsPanel=new BoardsPanel(boardComponent);
        footprintsPanel = new FootprintsPanel(boardComponent);
        layersPanel=new LayersPanel(boardComponent);
        
        boardComponent.setPreferredSize(new Dimension(700,600));
        boardComponent.addContainerListener(boardsPanel);
        boardComponent.getModel().addUnitListener(boardsPanel);
        boardComponent.getModel().addShapeListener(boardsPanel);
        
        BoardComponent.getUnitKeyboardListener().setComponent(boardComponent); 
        
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=1;
        gridBagConstraints.weighty=1;
        moduleBasePanel.add(boardComponent, gridBagConstraints);
        
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=0.001;
        gridBagConstraints.weighty=0.001;      
        moduleBasePanel.add(vbar, gridBagConstraints);      

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=1;
        gridBagConstraints.weightx=0.001;
        gridBagConstraints.weighty=0.001;
        moduleBasePanel.add(hbar, gridBagConstraints);          
        
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=1;
        moduleBasePanel.add( new JPanel(),gridBagConstraints);                    
        basePanel.add(moduleBasePanel, BorderLayout.CENTER);    

        FootprintButton.addActionListener(this);
        FootprintButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/footprint.png"));
        FootprintButton.setToolTipText("Add Footprint");
        FootprintButton.setPreferredSize(new Dimension(35, 35));
        
        //***add action listeners
        SelectionButton.addActionListener(this);
        SelectionButton.setIcon(Utilities.loadImageIcon(this, 
                                                      "/com/mynetpcb/core/images/selection.png"));
        SelectionButton.setSelected(true);
        SelectionButton.setToolTipText("Select Symbol");
        SelectionButton.setPreferredSize(new Dimension(35, 35));
     
        
        EllipseButton.addActionListener(this);
        EllipseButton.setToolTipText("Add Circle");
        EllipseButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/ellipse.png"));
        EllipseButton.setPreferredSize(new Dimension(35, 35));
         
        ArcButton.addActionListener(this);
        ArcButton.setToolTipText("Add Arc");
        ArcButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/arc.png"));
        ArcButton.setPreferredSize(new Dimension(35, 35));

        TrackButton.addActionListener(this);
        TrackButton.setToolTipText("Add Track and Via");
        TrackButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/line.png"));
        TrackButton.setPreferredSize(new Dimension(35, 35));

        LineButton.addActionListener(this);
        LineButton.setToolTipText("Add Line or Poligone");
        LineButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/linepoligone.png"));
        LineButton.setPreferredSize(new Dimension(35, 35));
        
        RectButton.addActionListener(this);
        RectButton.setToolTipText("Add Rectangle");
        RectButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/rect.png"));
        RectButton.setPreferredSize(new Dimension(35, 35));
        
        SolidRegionButton.addActionListener(this);
        SolidRegionButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/solid_region.png"));
        SolidRegionButton.setToolTipText("Add Solid Region");
        SolidRegionButton.setPreferredSize(new Dimension(35, 35));
        
        ViaButton.addActionListener(this);
        ViaButton.setToolTipText("Add Via");
        ViaButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/pad.png"));
        ViaButton.setPreferredSize(new Dimension(35, 35));

        HoleButton.addActionListener(this);
        HoleButton.setToolTipText("Add Hole");
        HoleButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/hole.png"));
        HoleButton.setPreferredSize(new Dimension(35, 35));
        
        LabelButton.addActionListener(this);
        LabelButton.setIcon(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/label.png"));
        LabelButton.setToolTipText("Add Label");
        LabelButton.setPreferredSize(new Dimension(35, 35));
        
        CopperAreaButton.addActionListener(this);
        CopperAreaButton.setIcon(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/copperarea.png"));
        CopperAreaButton.setToolTipText("Add Copper Area");
        CopperAreaButton.setPreferredSize(new Dimension(35, 35));
        
        SnapToGridButton.addActionListener(this);
        SnapToGridButton.setIcon(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/anchor.png"));
        SnapToGridButton.setToolTipText("Snap dragging point to grid");
        SnapToGridButton.setPreferredSize(new Dimension(35, 35));

        CoordButton.addActionListener(this);
        CoordButton.setToolTipText("Change coordinate origin");
        CoordButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/origin.png"));
        CoordButton.setPreferredSize(new Dimension(35, 35));

        MeasureButton.setPreferredSize(new Dimension(35, 35));
        MeasureButton.setToolTipText("Distance measurement");
        MeasureButton.addActionListener(this);
        MeasureButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/measure.png"));
        
        //***construct Top Buttons Panel
        AddBoardButton.setToolTipText("Add board");
        AddBoardButton.setPreferredSize(new Dimension(35, 35));
        AddBoardButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/subject.png"));
        AddBoardButton.addMenu("Create new boards project","Create").addMenu("Add board to project","Add").addSeparator().addMenu("Save","Save").addMenu("Save As","SaveAs").addSeparator().addRootMenu("Export", "export")
            .addSubMenu("export","Image","export.image").addSubMenu("export","XML", "export.xml").addSubMenu("export","Gerber RS-274X/X2", "export.gerber").addSeparator().addMenu("Exit","exit");
 
        PrintButton.addActionListener(this);
        PrintButton.setToolTipText("Print footprint");
        PrintButton.setPreferredSize(new Dimension(35, 35));
        PrintButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/print.png"));
        
        
        SaveButton.addActionListener(this);
        SaveButton.setActionCommand("Save");
        SaveButton.setToolTipText("Save Module");
        SaveButton.setPreferredSize(new Dimension(35, 35));
        SaveButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/save.png"));
       
        LoadButton.addActionListener(this);
        LoadButton.setToolTipText("Load Module");
        LoadButton.setPreferredSize(new Dimension(35, 35));
        LoadButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/folder.png"));

        ScaleIn.addActionListener(this);
        ScaleIn.setToolTipText("Scale In");
        ScaleIn.setPreferredSize(new Dimension(35, 35));
        ScaleIn.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/zoom_out.png"));

        ScaleOut.addActionListener(this);
        ScaleOut.setToolTipText("Scale Out");
        ScaleOut.setPreferredSize(new Dimension(35, 35));
        ScaleOut.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/zoom_in.png"));

        RotateLeft.addActionListener(this);    
        RotateLeft.setToolTipText("Rotate Left");
        RotateLeft.setPreferredSize(new Dimension(35, 35));
        RotateLeft.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/rotate_left.png"));
        
        RotateRight.addActionListener(this);
        RotateRight.setToolTipText("Rotate Right");
        RotateRight.setPreferredSize(new Dimension(35, 35));
        RotateRight.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/rotate_right.png"));        
        
        DragHeand.setPreferredSize(new Dimension(35, 35));
        DragHeand.setToolTipText("Drag to view");
        DragHeand.addActionListener(this);
        DragHeand.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/grab.png"));
        
        PositionToCenter.setPreferredSize(new Dimension(35, 35));
        PositionToCenter.setToolTipText("View position to center");
        PositionToCenter.addActionListener(this);
        PositionToCenter.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/tocenter.png"));
        
        
        NorthPanel.add(AddBoardButton);
        NorthPanel.add(PrintButton);
        NorthPanel.add(SaveButton);
        NorthPanel.add(LoadButton);
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
        group.add(TrackButton);
        group.add(LineButton);
        group.add(RectButton);
        group.add(SolidRegionButton);   
        group.add(ViaButton);
        group.add(HoleButton);
        group.add(LabelButton);
        group.add(CopperAreaButton);
        group.add(CoordButton);
        group.add(DragHeand);
        group.add(MeasureButton);
        
        WestPanel.setLayout(new BorderLayout());
        basePanel.add(NorthPanel, BorderLayout.NORTH);
        //****EAST PANEL
        tabbedPane.setPreferredSize(new Dimension(220, 200));
        //***create circuit tab
        tabbedPane.addTab("Boards", boardsPanel);
        //***create symbol tab
        tabbedPane.addTab("Footprints", footprintsPanel);
        tabbedPane.addChangeListener(footprintsPanel);
        //***create layout
        tabbedPane.addTab("Layers", layersPanel);
        tabbedPane.addChangeListener(layersPanel);
        basePanel.add(tabbedPane, BorderLayout.EAST);
        
        basePanel.add(SouthPanel, BorderLayout.SOUTH);

        leftButtonGroupPanel.setLayout(new BoxLayout(leftButtonGroupPanel, BoxLayout.Y_AXIS));
        leftButtonGroupPanel.setBorder(BorderFactory.createEmptyBorder(35, 4, 0, 4));
        leftButtonGroupPanel.add(FootprintButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SelectionButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(EllipseButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(ArcButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(LineButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(RectButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SolidRegionButton);                
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(TrackButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(ViaButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(HoleButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5))); 
        leftButtonGroupPanel.add(LabelButton);  
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5))); 
        leftButtonGroupPanel.add(CopperAreaButton);         
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5))); 
        leftButtonGroupPanel.add(SnapToGridButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(CoordButton);
        leftButtonGroupPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(MeasureButton);
        
        WestPanel.add(leftButtonGroupPanel, BorderLayout.NORTH);
        basePanel.add(WestPanel, BorderLayout.WEST);
                
        content.add(basePanel); // Add components to the content 
        
        addWindowListener(new WindowAdapter(){

                public void windowClosing(WindowEvent e) { 
                    exit();                                       
                }
            });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("exit")){
            exit();
            return;
        }
        if (e.getSource()==LoadButton) {
                        
            AbstractLoadDialog.Builder builder=boardComponent.getLoadDialogBuilder();
            AbstractLoadDialog boardLoadDialog =builder.setWindow(boardComponent.getDialogFrame().getParentFrame()).setCaption("Load Project").setEnabled(false).build();
            
            boardLoadDialog.pack();
            boardLoadDialog.setLocationRelativeTo(null); //centers on screen
            boardLoadDialog.setVisible(true);

            if (boardLoadDialog.getSelectedModel() == null ||
                boardLoadDialog.getSelectedModel().getUnit() == null) {
                return;
            }

            LoadBoards((BoardContainer) boardLoadDialog.getSelectedModel());

            boardLoadDialog.dispose();
            boardLoadDialog = null;
        }
        //new boards project
        if (e.getActionCommand().equals("Create")) {
            if(boardComponent.getModel().isChanged()){                        
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(boardComponent.getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Create", JOptionPane.YES_NO_OPTION)) {                                       
                    return;
                }                      
            }
            boardComponent.clear();                              
        }
        
        if (e.getActionCommand().equals("Add")||e.getActionCommand().equals("Create")) {  
            //rememeber current unit position
            if(boardComponent.getModel().getUnit()!=null){
                boardComponent.getModel().getUnit().setScrollPositionValue((int)boardComponent.getViewportWindow().getX(),(int)boardComponent.getViewportWindow().getY());                      
            }
            Board board  = new Board((int)Grid.MM_TO_COORD(100),(int)Grid.MM_TO_COORD(100));
            boardComponent.getModel().add(board);
            boardComponent.getModel().setActiveUnit(board.getUUID());
            boardComponent.componentResized(null);
            boardComponent.getModel().fireUnitEvent(new UnitEvent(board, UnitEvent.SELECT_UNIT));
        }
        
        if (boardComponent.getModel().getUnit() == null) {
            return;
        }
        if (e.getActionCommand().equals("export.xml")) {
            boardComponent.getModel().getUnit().setSelected(false);
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            fc.setDialogTitle("Export Board");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setSelectedFile(new File(boardComponent.getModel().getFormatedFileName()));
            fc.addChoosableFileFilter(new ImpexFileFilter(".xml"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                ImpexProcessor impexProcessor = new ImpexProcessor(new XMLExporter());
                try {
                    Map<String,Object> context=new HashMap<String,Object>(1);
                    if(fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".xml"))                                           {
                        context.put("target.file",fc.getSelectedFile().getAbsolutePath());               
                    }else{                            
                        context.put("target.file",fc.getSelectedFile().getAbsolutePath()+".xml");                                             
                    }
                    
                    impexProcessor.process(boardComponent.getModel(), context);
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.out);
                }
            }
        }
       
        if (e.getActionCommand().equals("export.image")) {
            JDialog d=new BoardImageExportDialog(this,boardComponent);
            d.setLocationRelativeTo(null); //centers on screen
            d.setVisible(true);            
            return;
        }
      
        if (e.getActionCommand().equals("export.gerber")) {
            JDialog d=new GerberExportDialog(this,boardComponent);
            d.setLocationRelativeTo(null); //centers on screen
            d.setVisible(true);                                                
            return;
        }        
        if (e.getSource()==RotateLeft || e.getSource()==RotateRight) {        
            Collection<Shape> shapes= boardComponent.getModel().getUnit().getShapes();
            if(shapes.size()==0){
               return; 
            }   
            //***notify undo manager                    
            boardComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
//            Rectangle r=boardComponent.getModel().getUnit().getShapesRect(shapes);  
//
//            BoardMgr.getInstance().rotateBlock(shapes,
//                                   AffineTransform.getRotateInstance((e.getSource()==RotateLeft?
//                                                                      -1 :
//                                                                      1) *(Math.PI /2),
//                                                                     r.getCenterX(),
//                                                                     r.getCenterY())); 
            BoardMgr.getInstance().alignBlock(boardComponent.getModel().getUnit().getGrid(),shapes);                     
            //CircuitMgr.getInstance().normalizePinText(shapes);
            //***notify undo manager
            boardComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
            boardComponent.Repaint();
        }   

        if (e.getActionCommand().equals("Save")||e.getActionCommand().equals("SaveAs")) {
            if (Configuration.get().isIsOnline() && User.get().isAnonymous()) {
                User.showMessageDialog(boardComponent.getDialogFrame().getParentFrame(), "Anonymous access denied.");
                return;
            }
            //could be a freshly imported circuit with no library/project name
            if(e.getActionCommand().equals("Save")){
              if (boardComponent.getModel().getLibraryName() == null||boardComponent.getModel().getLibraryName().length()==0) {
                 (new BoardSaveDialog(this, boardComponent,Configuration.get().isIsOnline())).build();
                  return;
              }
            }else{
                (new BoardSaveDialog(this, boardComponent,Configuration.get().isIsOnline())).build();
                return;                
            }
            
            //save the file
            if (!Configuration.get().isIsApplet()) {

                Command writer =
                    new WriteUnitLocal(this, boardComponent.getModel().format(),
                                       Configuration.get().getBoardsRoot(),
                                       boardComponent.getModel().getLibraryName(), null,
                                       boardComponent.getModel().getFileName(), true, BoardComponent.class);
                CommandExecutor.INSTANCE.addTask("WriteUnitLocal", writer);
            } else {

                Command writer =
                    new WriteConnector(this, boardComponent.getModel().format(),
                                       new RestParameterMap.ParameterBuilder("/boards").addURI(boardComponent.getModel().getLibraryName()).addURI(boardComponent.getModel().getFormatedFileName()).addAttribute("overwrite",
                                                                                                                                                                                                                      String.valueOf(true)).build(),
                                       BoardComponent.class);
                CommandExecutor.INSTANCE.addTask("WriteUnit", writer);
            }
        }
        if (e.getSource()==FootprintButton) {           
            
            AbstractLoadDialog.Builder builder=new FootprintLoadDialog.Builder();
            AbstractLoadDialog symbolLoadDialog =builder.setWindow(this).setCaption("Load Footprint").setEnabled(true).build();

            symbolLoadDialog.pack();
            symbolLoadDialog.setLocationRelativeTo(null); //centers on screen
            symbolLoadDialog.setVisible(true);

            if (symbolLoadDialog.getSelectedModel() == null) {
                return;
            }
            boardComponent.setMode(Mode.FOOTPRINT_MODE);

            Footprint footprint = (Footprint) symbolLoadDialog.getSelectedModel().getUnit();
            PCBFootprint pcbfootprint = BoardMgr.getInstance().createPCBFootprint(footprint,boardComponent.getModel().getUnit().getActiveSide());
            //            //***set chip cursor
            pcbfootprint.move(-1 * (int) pcbfootprint.getBoundingShape().getCenter().x,
                           -1 * (int) pcbfootprint.getBoundingShape().getCenter().y);
pcbfootprint.setRotation(60, pcbfootprint.getBoundingShape().getCenter());
            boardComponent.setContainerCursor(pcbfootprint);
            boardComponent.getEventMgr().setEventHandle("cursor", pcbfootprint);

            symbolLoadDialog.dispose();
            symbolLoadDialog = null;
            this.boardComponent.requestFocusInWindow(); //***enable keyboard clicks

        }
        if (e.getSource()== PrintButton) {
            JDialog d=new BoardPrintDialog(this,boardComponent,"Print");
            d.setLocationRelativeTo(null); //centers on screen
            d.setVisible(true);
            //boardComponent.Print("board");
        }
        if (e.getSource()== DragHeand) {
            boardComponent.setMode(Mode.DRAGHEAND_MODE);
        }
        
        if (e.getSource()==ScaleIn) {
            boardComponent.zoomOut(new Point((int)boardComponent.getVisibleRect().getCenterX(),
                                                (int)boardComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==ScaleOut) {
            boardComponent.zoomIn(new Point((int)boardComponent.getVisibleRect().getCenterX(),
                                                 (int)boardComponent.getVisibleRect().getCenterY()));
        }
        
        if (e.getSource()==PositionToCenter) {
            boardComponent.setScrollPosition(boardComponent.getModel().getUnit().getWidth() / 2,
                                                 boardComponent.getModel().getUnit().getHeight() / 2);

        }
        if (e.getSource()==SelectionButton) {
            boardComponent.setMode(Mode.COMPONENT_MODE);
        }
        //if (e.getActionCommand().equals("Rectangle")) {
        //    boardComponent.setMode(BoardComponent.RECT_MODE);
        //}
        if (e.getSource()==EllipseButton) {
            boardComponent.setMode(Mode.ELLIPSE_MODE);
        }
        
        if (e.getSource()==CopperAreaButton) {
            boardComponent.setMode(Mode.COPPERAREA_MODE);
        }        
        if (e.getSource()==ArcButton) {
            boardComponent.setMode(Mode.ARC_MODE);
        }
        if (e.getSource()==TrackButton) {
            boardComponent.setMode(Mode.TRACK_MODE);
        }
        if (e.getSource()==LineButton) {
            boardComponent.setMode(Mode.LINE_MODE);
        }
        if (e.getSource()==RectButton) {
            boardComponent.setMode(Mode.RECT_MODE);
        }
        if (e.getSource()==SolidRegionButton) {
            boardComponent.setMode(Mode.SOLID_REGION);
        }        
        if (e.getSource()==LabelButton) {
            boardComponent.setMode(Mode.LABEL_MODE);
        }
        if (e.getSource()==ViaButton) {
            boardComponent.setMode(Mode.VIA_MODE);
        }
        if (e.getSource()==HoleButton) {
            boardComponent.setMode(Mode.HOLE_MODE);
        }
        if (e.getSource()==SnapToGridButton) {
            boardComponent.setParameter("snaptogrid", ((JToggleButton)e.getSource()).getModel().isSelected());
        }
        if (e.getSource()==CoordButton) {
            boardComponent.setMode(Mode.ORIGIN_SHIFT_MODE);
        }
        if (e.getSource()==MeasureButton) {
            boardComponent.setMode(Mode.MEASUMENT_MODE);
        }
    }
    
    @Override
    public Window getParentFrame() {
        return this;
    }

    @Override
    public JScrollBar getVerticalScrollBar() {
        return vbar;
    }

    @Override
    public JScrollBar getHorizontalScrollBar() {
        return hbar;
    }
    @Override
    public void setButtonGroup(int requestedMode) {
            //***post operations
            switch (requestedMode) {
            case Mode.COMPONENT_MODE:
                group.setSelected(SelectionButton.getModel(), true);
                break;
            case Mode.TRACK_MODE:
                group.setSelected(TrackButton.getModel(), true);
                break;            
            case Mode.LINE_MODE:
               group.setSelected(LineButton.getModel(), true);
               break;
            }
    }

    @Override
    public void OnStart(Class<?> receiver) {
        if(receiver==BoardComponent.class){
            DisabledGlassPane.block(this.getRootPane(), "Saving..."); 
        }
    }

    @Override
    public void OnRecive(String string, Class receiver) {
    }
    
    @Override
    public void OnFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getRootPane());        
        
        if(receiver==BoardComponent.class){ 
           boardComponent.getModel().registerInitialState();
        }
    }

    @Override
    public void OnError(String message) {
    }
    
    /**
         *Create,load footprint
         * @param source 
         */
        private void LoadBoards(BoardContainer source) {
            boardComponent.clear();
            boardComponent.setMode(Mode.COMPONENT_MODE);
            setButtonGroup(Mode.COMPONENT_MODE);
            
            if(source==null){
                Board board=new Board((int)Grid.MM_TO_COORD(100),(int)Grid.MM_TO_COORD(100)); 
                boardComponent.getModel().add(board);
            }else{
            for (Board board : source.getUnits()) {
                try {
                    Board copy = board.clone();
                    copy.getScalableTransformation().Reset(0.5,10,3,13);
                    boardComponent.getModel().add(copy);
                    copy.notifyListeners(ShapeEvent.ADD_SHAPE);
                } catch (CloneNotSupportedException f) {
                    f.printStackTrace(System.out);
                }
            }
            }
            boardComponent.getModel().registerInitialState();
            boardComponent.getModel().setLibraryName(source!=null?source.getLibraryName():null);
            boardComponent.getModel().setCategoryName(source!=null?source.getCategoryName():null);
            boardComponent.getModel().setFileName(source!=null?source.getFileName():"Boards");
            boardComponent.getModel().setDesignerName(source!=null?source.getDesignerName():"");
            boardComponent.getModel().setActiveUnit(0);
            boardComponent.componentResized(null);
            boardComponent.getModel().getUnit().setSelected(false);
            boardComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.RENAME_CONTAINER));
            boardComponent.getModel().fireUnitEvent(new UnitEvent(boardComponent.getModel().getUnit(),
                                                                      UnitEvent.SELECT_UNIT));
            //position to symbol center
            Box r=boardComponent.getModel().getUnit().getBoundingRect();
            boardComponent.setScrollPosition((int)r.getCenter().x,(int)r.getCenter().y);
            boardComponent.Repaint();
        }

    private void exit(){
        if(boardComponent.getModel().isChanged()){                        
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(BoardEditorDialog.this, "There is a changed element.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {                                                                                              
                return;
            }                      
        }
        boardComponent.release();  
        BoardEditorDialog.this.dispose(); 
    }
    
    public static void main(String[] args) {
        //***initialize configuration
        Configuration.Initilize(false);
        Configuration.get().read();
        //*****************Footprint editor
        final BoardEditorDialog f = new BoardEditorDialog(null, "Board Editor");
        f.setPreferredSize(new Dimension(730, 800));
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
