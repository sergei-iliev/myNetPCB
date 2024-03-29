package com.mynetpcb.ui.circuit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

import com.mynetpcb.board.unit.BoardMgr;
import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.container.CircuitContainer;
import com.mynetpcb.circuit.dialog.panel.CircuitsPanel;
import com.mynetpcb.circuit.dialog.panel.SymbolsPanel;
import com.mynetpcb.circuit.dialog.print.CircuitPrintDialog;
import com.mynetpcb.circuit.dialog.save.CircuitImageExportDialog;
import com.mynetpcb.circuit.dialog.save.CircuitSaveDialog;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
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
import com.mynetpcb.core.capi.popup.JPopupButton;
import com.mynetpcb.core.capi.shape.CoordinateSystem;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.dialog.SymbolLoadDialog;
import com.mynetpcb.symbol.unit.Symbol;
import com.mynetpcb.ui.AbstractInternalFrame;

public class CircuitInternalFrame extends AbstractInternalFrame implements DialogFrame,CommandListener,ActionListener{
    private CircuitComponent circuitComponent;
    private CircuitsPanel circuitsPanel;
    private JPanel basePanel;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private SymbolsPanel symbolsPanel;
    //private JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
    //private JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL);
    private  GridBagLayout gridBagLayout=new GridBagLayout();    
    private JPanel moduleBasePanel=new JPanel(gridBagLayout);
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    
    private JPanel NorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel WestPanel = new JPanel();
    
    private JPanel SouthPanel = new JPanel();
    private JPanel leftButtonGroupPanel = new JPanel();
    private ButtonGroup group = new ButtonGroup();
    
    private JButton SymbolButton = new JButton();
    private JPopupButton AddCircuitButton=new JPopupButton(this);
    private JButton PrintButton = new JButton();
    private JButton SaveButton = new JButton();
    private JButton LoadButton = new JButton();
    private JButton ScaleIn = new JButton();
    private JButton ScaleOut = new JButton();
    private JButton RotateLeft=new JButton();
    private JButton RotateRight=new JButton();  
    private JToggleButton DragHeand = new JToggleButton();
    private JButton PositionToCenter = new JButton();
    
    private JToggleButton WireButton = new JToggleButton();
    private JToggleButton BusButton = new JToggleButton();
    private JToggleButton BusPinButton = new JToggleButton();
    private JToggleButton SelectionButton = new JToggleButton();
    private JToggleButton JunctionButton = new JToggleButton();
    private JToggleButton LabelButton = new JToggleButton();
    private JToggleButton ConnectorButton = new JToggleButton();
    private JToggleButton NoConnectorButton = new JToggleButton();
    private JToggleButton NetLabelButton = new JToggleButton();
    private JToggleButton VccSymbolButton = new JToggleButton();
    private JToggleButton GndSymbolButton = new JToggleButton();
    private JToggleButton CoordButton = new JToggleButton();
    
    

    
    public CircuitInternalFrame() {
       this(null);
    }
    public CircuitInternalFrame(CircuitContainer circuitContainer) {
        super("Circuit");        
        init();
        LoadCircuits(circuitContainer); 
    }
    private void init() {
        Container content = this.getContentPane();
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        
        //***set module component        
        circuitComponent=new CircuitComponent(this);
        symbolsPanel = new SymbolsPanel(circuitComponent);
        circuitsPanel = new CircuitsPanel(circuitComponent);
        
        circuitComponent.setPreferredSize(new Dimension(700,600));
        circuitComponent.addContainerListener(circuitsPanel);
        circuitComponent.getModel().addUnitListener(circuitsPanel);
        circuitComponent.getModel().addShapeListener(circuitsPanel);
        
        CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent); 
        
        
        
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=1;
        gridBagConstraints.weighty=1;
        moduleBasePanel.add(circuitComponent, gridBagConstraints);
        
        //gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        //gridBagConstraints.gridx=1;
        //gridBagConstraints.gridy=0;
        //gridBagConstraints.weightx=0.001;
        //gridBagConstraints.weighty=0.001;      
        //moduleBasePanel.add(vbar, gridBagConstraints);      

        //gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        //gridBagConstraints.gridx=0;
        //gridBagConstraints.gridy=1;
        //gridBagConstraints.weightx=0.001;
        //gridBagConstraints.weighty=0.001;
        //moduleBasePanel.add(hbar, gridBagConstraints);          
        
        //gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        //gridBagConstraints.gridx=1;
        //gridBagConstraints.gridy=1;
        //moduleBasePanel.add( new JPanel(),gridBagConstraints);                    
        basePanel.add(moduleBasePanel, BorderLayout.CENTER);    
       
        SelectionButton.addActionListener(this);
        SelectionButton.setIcon(Utilities.loadImageIcon(this, 
                                                      "images/selection.png"));
        SelectionButton.setSelected(true);
        SelectionButton.setToolTipText("Select Symbol");
        SelectionButton.setPreferredSize(new Dimension(35, 35));

        SymbolButton.addActionListener(this);
        SymbolButton.setIcon(Utilities.loadImageIcon(this, "images/symbol.png"));
        SymbolButton.setToolTipText("Add Symbol");
        SymbolButton.setPreferredSize(new Dimension(35, 35));

        WireButton.addActionListener(this);
        WireButton.setIcon(Utilities.loadImageIcon(this, "images/wire.png"));
        WireButton.setToolTipText("Draw Wire");
        WireButton.setPreferredSize(new Dimension(35, 35));

        BusButton.addActionListener(this);
        BusButton.setActionCommand("bus");
        BusButton.setIcon(Utilities.loadImageIcon(this, "images/bus.png"));
        BusButton.setToolTipText("Draw Bus");
        BusButton.setPreferredSize(new Dimension(35, 35));

        BusPinButton.addActionListener(this);
        BusPinButton.setIcon(Utilities.loadImageIcon(this, "images/buspin.png"));
        BusPinButton.setToolTipText("Add Bus Pin");
        BusPinButton.setPreferredSize(new Dimension(35, 35));


        JunctionButton.addActionListener(this);
        JunctionButton.setIcon(Utilities.loadImageIcon(this, "images/junction.png"));
        JunctionButton.setToolTipText("Draw Junction");
        JunctionButton.setPreferredSize(new Dimension(35, 35));


        LabelButton.addActionListener(this);
        LabelButton.setIcon(Utilities.loadImageIcon(this, "images/label.png"));
        LabelButton.setToolTipText("Add Label");
        LabelButton.setPreferredSize(new Dimension(35, 35));


        ConnectorButton.addActionListener(this);
        ConnectorButton.setIcon(Utilities.loadImageIcon(this, "images/connector.png"));
        ConnectorButton.setToolTipText("Add Connector");
        ConnectorButton.setPreferredSize(new Dimension(35, 35));

        NoConnectorButton.addActionListener(this);
        NoConnectorButton.setIcon(Utilities.loadImageIcon(this, "images/noconnection.png"));
        NoConnectorButton.setToolTipText("Add No Connection flag");
        NoConnectorButton.setPreferredSize(new Dimension(35, 35));

        NetLabelButton.addActionListener(this);
        NetLabelButton.setIcon(Utilities.loadImageIcon(this, "images/netlabel.png"));
        NetLabelButton.setToolTipText("Add Net label");
        NetLabelButton.setPreferredSize(new Dimension(35, 35));

        VccSymbolButton.addActionListener(this);
        VccSymbolButton.setToolTipText("Add VCC symbol");
        VccSymbolButton.setIcon(Utilities.loadImageIcon(this, "images/vcc.png"));
        VccSymbolButton.setPreferredSize(new Dimension(35, 35));

        GndSymbolButton.addActionListener(this);
        GndSymbolButton.setToolTipText("Add GND symbol");
        GndSymbolButton.setIcon(Utilities.loadImageIcon(this, "images/gnd.png"));
        GndSymbolButton.setPreferredSize(new Dimension(35, 35));
        
        CoordButton.addActionListener(this);
        CoordButton.setToolTipText("Change coordinate origin");
        CoordButton.setIcon(Utilities.loadImageIcon(this, "images/origin.png"));
        CoordButton.setPreferredSize(new Dimension(35, 35));
        
        //***construct Top Buttons Panel
        //AddBoardButton.setToolTipText("Add Circuit");
        AddCircuitButton.setPreferredSize(new Dimension(35, 35));
        AddCircuitButton.setIcon(Utilities.loadImageIcon(this, "images/subject.png"));
        AddCircuitButton.addMenu("Create new circuits project","Create").addMenu("Add circuit to project","Add").addSeparator().addMenu("Close","Close").addSeparator().addMenu("Save","Save").addMenu("Save As","SaveAs").addSeparator().addRootMenu("Export", "export")
            .addSubMenu("export","Image","export.image").addSubMenu("export","XML", "export.xml").addSubMenu("export","Clipboard", "clipboard.export").addSeparator().addMenu("Exit","exit");
        
        PrintButton.addActionListener(this);
        PrintButton.setToolTipText("Print Circuit");
        PrintButton.setPreferredSize(new Dimension(35, 35));
        PrintButton.setIcon(Utilities.loadImageIcon(this, "images/print.png"));
        
        
        SaveButton.addActionListener(this);
        SaveButton.setToolTipText("Save Circuits Project");
        SaveButton.setPreferredSize(new Dimension(35, 35));
        SaveButton.setIcon(Utilities.loadImageIcon(this, "images/save.png"));
        
        LoadButton.addActionListener(this);
        LoadButton.setToolTipText("Load Circuits Project");
        LoadButton.setPreferredSize(new Dimension(35, 35));
        LoadButton.setIcon(Utilities.loadImageIcon(this, "images/folder.png"));

        ScaleIn.addActionListener(this);
        ScaleIn.setToolTipText("Scale In");
        ScaleIn.setPreferredSize(new Dimension(35, 35));
        ScaleIn.setIcon(Utilities.loadImageIcon(this, "images/zoom_out.png"));

        ScaleOut.addActionListener(this);
        ScaleOut.setToolTipText("Scale Out");
        ScaleOut.setPreferredSize(new Dimension(35, 35));
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
        DragHeand.setToolTipText("Drag to view");
        DragHeand.addActionListener(this);
        DragHeand.setIcon(Utilities.loadImageIcon(this, "images/grab.png"));
        
        PositionToCenter.setPreferredSize(new Dimension(35, 35));
        PositionToCenter.setToolTipText("View position to center");
        PositionToCenter.addActionListener(this);
        PositionToCenter.setIcon(Utilities.loadImageIcon(this, "images/tocenter.png"));
        
        
        NorthPanel.add(AddCircuitButton);
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
        group.add(WireButton);
        group.add(BusButton);
        group.add(BusPinButton);
        group.add(JunctionButton);
        group.add(LabelButton);
        group.add(ConnectorButton);
        group.add(NoConnectorButton);
        group.add(NetLabelButton);     
        group.add(VccSymbolButton);
        group.add(GndSymbolButton);
        group.add(DragHeand);
        
        WestPanel.setLayout(new BorderLayout());
        basePanel.add(NorthPanel, BorderLayout.NORTH);
        //****EAST PANEL
        tabbedPane.setPreferredSize(new Dimension(250, 200));
        //***create circuit tab
        tabbedPane.addTab("Circuits", circuitsPanel);
        //***create symbol tab
        tabbedPane.addTab("Symbols", symbolsPanel);
        tabbedPane.addChangeListener(symbolsPanel);        
        
        basePanel.add(tabbedPane, BorderLayout.EAST);
        
        basePanel.add(SouthPanel, BorderLayout.SOUTH);

        leftButtonGroupPanel.setLayout(new BoxLayout(leftButtonGroupPanel, BoxLayout.Y_AXIS));
        leftButtonGroupPanel.setBorder(BorderFactory.createEmptyBorder(35, 4, 0, 4));
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SelectionButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SymbolButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(VccSymbolButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(GndSymbolButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(WireButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(BusButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(BusPinButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(JunctionButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(LabelButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(ConnectorButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(NoConnectorButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(NetLabelButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(CoordButton);
        
        WestPanel.add(leftButtonGroupPanel, BorderLayout.NORTH);
        basePanel.add(WestPanel, BorderLayout.WEST);
                
        content.add(basePanel); // Add components to the content         
    }
    @Override
    public boolean exit() {
        if(circuitComponent.getModel().isChanged()){                        
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "There is a changed element.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {                                                                                              
                return false;
            }                      
        }
        circuitComponent.release();  
        this.dispose(); 
        return true;
    }
    private void LoadCircuits(CircuitContainer source) {
        circuitComponent.clear();
        circuitComponent.setMode(Mode.COMPONENT_MODE);
        setButtonGroup(Mode.COMPONENT_MODE);
        
        if(source==null){
            Circuit circuit=new Circuit(1200,800); 
            circuitComponent.getModel().add(circuit);
        }else{
        for (Circuit circuit : source.getUnits()) {
            try {
                Circuit copy = circuit.clone();                
                copy.getScalableTransformation().reset(1.2, 2, 0, ScalableTransformation.DEFAULT_MAX_SCALE_FACTOR);
                circuitComponent.getModel().add(copy);
                copy.notifyListeners(ShapeEvent.ADD_SHAPE);
            } catch (CloneNotSupportedException f) {
                f.printStackTrace(System.out);
            }
        }
        }
        circuitComponent.getModel().registerInitialState();
        circuitComponent.getModel().setLibraryName(source!=null?source.getLibraryName():null);
        circuitComponent.getModel().setCategoryName(source!=null?source.getCategoryName():null);
        circuitComponent.getModel().setFileName(source!=null?source.getFileName():"Circuits");
        circuitComponent.getModel().setDesignerName(source!=null?source.getDesignerName():"");
        circuitComponent.getModel().setActiveUnit(0);
        circuitComponent.componentResized(null);
        circuitComponent.getModel().getUnit().setSelected(false);
        circuitComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.RENAME_CONTAINER));
        circuitComponent.getModel().fireUnitEvent(new UnitEvent(circuitComponent.getModel().getUnit(),
                                                                  UnitEvent.SELECT_UNIT));
        
		  //position all to circuit center
		  for(var unit : this.circuitComponent.getModel().getUnits()){			   
	            var r=unit.getBoundingRect();
	            var x=unit.getScalableTransformation().getCurrentTransformation().getScaleX()*r.getX()-(this.circuitComponent.getViewportWindow().getWidth()-unit.getScalableTransformation().getCurrentTransformation().getScaleX()*r.getWidth())/2;
	            var y=unit.getScalableTransformation().getCurrentTransformation().getScaleX()*r.getY()-(this.circuitComponent.getViewportWindow().getHeight()-unit.getScalableTransformation().getCurrentTransformation().getScaleX()*r.getHeight())/2;;
	            unit.setViewportPositionValue(x,y);          
		  }	
        //position to symbol center
        com.mynetpcb.d2.shapes.Box r=circuitComponent.getModel().getUnit().getBoundingRect();
        circuitComponent.setViewportPosition(r.getCenter().x,r.getCenter().y);
        circuitComponent.Repaint();
    }
    @Override
    public CircuitComponent getUnitComponent(){
    	return circuitComponent;
    }
    @Override
    public boolean isChanged() {
       return circuitComponent.getModel().isChanged();
    }

    @Override
    public JFrame getParentFrame() {
        return  (JFrame)this.getDesktopPane().getRootPane().getParent();
    }

    @Override
    public void setButtonGroup(int requestedMode) {
        if (circuitComponent.getMode() == Mode.WIRE_MODE) {
            circuitComponent.getEventMgr().resetEventHandle();
        }
        //***post operations
        switch (requestedMode) {
        case Mode.SYMBOL_MODE:
            group.clearSelection();
            break;
        case Mode.COMPONENT_MODE:
            group.setSelected(SelectionButton.getModel(), true);
            break;
        case Mode.WIRE_MODE:
            group.setSelected(WireButton.getModel(), true);
            break;
        case Mode.BUS_MODE:
            group.setSelected(BusButton.getModel(), true);
        }
    }

    @Override
    public void onStart(Class<?> receiver) {
        if(receiver==CircuitComponent.class){
            DisabledGlassPane.block(this.getRootPane(), "Saving..."); 
        }
    }

    @Override
    public void onRecive(String content, Class<?> receiver) {
        // TODO Implement this method

    }

    @Override
    public void onFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getRootPane());        
        
        if(receiver==CircuitComponent.class){ 
           circuitComponent.getModel().registerInitialState();
        }
    }

    @Override
    public void onError(String error) {
        // TODO Implement this method
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("exit")){
            exit();
            return;
        }

        if (e.getActionCommand().equals("Create")) {
            if(circuitComponent.getModel().isChanged()){                        
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(circuitComponent.getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Create", JOptionPane.YES_NO_OPTION)) {                                       
                    return;
                }                      
            }
            circuitComponent.clear();                              
        }
        
        if (e.getActionCommand().equals("Add")||e.getActionCommand().equals("Create")) {
            //rememeber current unit position
            if (circuitComponent.getModel().getUnit() != null) {
                circuitComponent.getModel().getUnit().setViewportPositionValue(circuitComponent.getViewportWindow().getX(),
                                                                             circuitComponent.getViewportWindow().getY());
            }
            Circuit circuit = new Circuit(1000, 800);
            circuitComponent.getModel().add(circuit);
            circuitComponent.getModel().setActiveUnit(circuit.getUUID());
            circuitComponent.componentResized(null);
            circuitComponent.getModel().fireUnitEvent(new UnitEvent(circuitComponent.getModel().getUnit(),
                                                                    UnitEvent.SELECT_UNIT));
            circuitComponent.Repaint();
        }
        
        if (e.getSource()==LoadButton) {            
            AbstractLoadDialog.Builder builder=circuitComponent.getLoadDialogBuilder();
            AbstractLoadDialog circuitLoadDialog =builder.setWindow(this.getParentFrame()).setCaption("Load Project").setEnabled(false).build();
            
            circuitLoadDialog.pack();
            circuitLoadDialog.setLocationRelativeTo(null); //centers on screen
            circuitLoadDialog.setVisible(true);

            if (circuitLoadDialog.getSelectedModel() == null ||
                circuitLoadDialog.getSelectedModel().getUnit() == null) {
                return;
            }

            LoadCircuits((CircuitContainer) circuitLoadDialog.getSelectedModel());

            circuitLoadDialog.dispose();
            circuitLoadDialog = null;

            //position on center
            //Rectangle r=circuitComponent.getModel().getUnit().getBoundingRect();
            //circuitComponent.setScrollPosition((int)r.getCenterX(),(int)r.getCenterY());
        }
//        if(e.getActionCommand().equals("createboard")){
//            BoardEditorDialog boardEditorDialog = new BoardEditorDialog(parent,"Board Editor");        
//            boardEditorDialog.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight())); 
//            boardEditorDialog.pack();
//            boardEditorDialog.setLocationRelativeTo(null); //centers on screen
//            boardEditorDialog.setFocusable(true);
//            boardEditorDialog.setVisible(true);  
//            CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
//        }
//        if (e.getActionCommand().equals("createpackage")) {
//            FootprintEditorDialog footprintEditorDialog = new FootprintEditorDialog(parent, "Footprint Editor");
//            footprintEditorDialog.pack();
//            footprintEditorDialog.setLocationRelativeTo(null); //centers on screen
//            footprintEditorDialog.setFocusable(true);
//            footprintEditorDialog.setVisible(true);
//            CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
//        }
//        if (e.getActionCommand().equals("createsymbol")) {
//            SymbolEditorDialog symbolEditorDialog = new SymbolEditorDialog(parent, "Symbol Editor");
//            symbolEditorDialog.pack();
//            symbolEditorDialog.setLocationRelativeTo(null); //centers on screen
//            symbolEditorDialog.setFocusable(true);
//            symbolEditorDialog.setVisible(true);
//            CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
//        }
//        if (circuitComponent.getModel().getUnit() == null) {
//            return;
//        }
//        if (e.getActionCommand().equals("copy")) {
//            circuitComponent.Copy();                
//        }
//        if (e.getActionCommand().equals("paste")) {
//           circuitComponent.Paste();
//        }
//        if (e.getActionCommand().equals("fixsymbolnames")) {
//            CircuitMgr.getInstance().generateShapeNaming(circuitComponent.getModel().getUnit());
//            circuitComponent.Repaint();
//        }
        if (e.getActionCommand().equals("export.image")) {            
            JDialog d=new CircuitImageExportDialog(getParentFrame(), circuitComponent);
            d.setLocationRelativeTo(null); //centers on screen
            d.setVisible(true);

        }
        if (e.getActionCommand().equals("export.xml")) {
            circuitComponent.getModel().getUnit().setSelected(false);
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            fc.setDialogTitle("Export circuit");
            fc.setAcceptAllFileFilterUsed(false);
            fc.setSelectedFile(new File(circuitComponent.getModel().getFormatedFileName()));
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
                    
                    impexProcessor.process(circuitComponent.getModel(), context);
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.out);
                }
            }
        }

        if (e.getSource()==RotateLeft || e.getSource()==RotateRight) {        
            Collection<Shape> shapes= circuitComponent.getModel().getUnit().getShapes();
            if(shapes.size()==0){
               return; 
            }   
            
            //***notify undo manager                    
            circuitComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
            com.mynetpcb.d2.shapes.Box r=circuitComponent.getModel().getUnit().getShapesRect(shapes);  
            
            BoardMgr.getInstance().rotateBlock(shapes,
                                   ((e.getSource()==RotateLeft?
                                                                      1 :
                                                                      -1) *90),
                                                                     r.getCenter()); 
            BoardMgr.getInstance().alignBlock(circuitComponent.getModel().getUnit().getGrid(),shapes);                     

            //***notify undo manager
            circuitComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
            circuitComponent.Repaint();
        }   
        if (e.getSource()==PrintButton) {            
            JDialog d=new CircuitPrintDialog(this.getParentFrame(),circuitComponent,"Print");
            d.setLocationRelativeTo(null); //centers on screen
            d.setVisible(true);
        }
        if (e.getActionCommand().equals("clipboard.export")) {
            try {
                ClipboardMgr.getInstance().setClipboardContent(Clipboardable.Clipboard.SYSTEM,
                                                               circuitComponent.getModel().createClipboardContent());
            } catch (AccessControlException ace) {
                JOptionPane.showMessageDialog(this.getParentFrame(),
                                              "You need to use the signed applet version.",
                                              "Security exception", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }        
//        if(e.getActionCommand().equals("undo")){
//         circuitComponent.getModel().getUnit().undo(null);
//         circuitComponent.Repaint();
//         circuitComponent.revalidate();
//         return;
//        }
//        
//        if(e.getActionCommand().equals("redo")){
//         circuitComponent.getModel().getUnit().redo();
//         circuitComponent.Repaint();
//         circuitComponent.revalidate();        
//         return;
//        }
//        if(e.getActionCommand().equals("selectall")){
//            circuitComponent.getModel().getUnit().setSelected(true);
//            circuitComponent.Repaint();          
//        }
//        if(e.getActionCommand().equals("deleteunit")){
//            circuitComponent.getModel().delete(circuitComponent.getModel().getUnit().getUUID());
//            if (circuitComponent.getModel().getUnits().size() > 0) {
//                circuitComponent.getModel().setActiveUnit(0);
//                circuitComponent.revalidate();
//                circuitComponent.getModel().fireUnitEvent(new UnitEvent(circuitComponent.getModel().getUnit(), UnitEvent.SELECT_UNIT));
//            }else{
//                circuitComponent.clear();
//                circuitComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.DELETE_CONTAINER));
//            }
//            circuitComponent.componentResized(null);  
//            circuitComponent.Repaint();              
//        }
//        if(e.getActionCommand().equals("deletecontainer")){
//            if(circuitComponent.getModel().isChanged()){                        
//                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(circuitComponent.getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Close", JOptionPane.YES_NO_OPTION)) {                                       
//                    return;
//                }                      
//            }                        
//            circuitComponent.clear();
//            circuitComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.DELETE_CONTAINER));            
//            circuitComponent.componentResized(null);
//            circuitComponent.Repaint();        
//        }
 

        if(e.getSource()==SaveButton||e.getActionCommand().equals("Save")){
                if (circuitComponent.getModel().getLibraryName() == null||circuitComponent.getModel().getLibraryName().length()==0) {
                          new CircuitSaveDialog(this.getParentFrame(), circuitComponent,Configuration.get().isIsOnline()).build();                
                }else{
                                //save the file
                                if (!Configuration.get().isIsApplet()) {
                                    Command writer =
                                        new WriteUnitLocal(this, circuitComponent.getModel().format(),
                                                           Configuration.get().getCircuitsRoot(),
                                                           circuitComponent.getModel().getLibraryName(),null,
                                                           circuitComponent.getModel().getFileName(), true, CircuitComponent.class);
                                    CommandExecutor.INSTANCE.addTask("WriteUnitLocal", writer);
                                } else {
        //                                    Command writer =
        //                                        new WriteConnector(this, symbolComponent.getModel().format(),
        //                                                           new RestParameterMap.ParameterBuilder("/symbols").addURI(symbolComponent.getModel().getLibraryName()).addURI(symbolComponent.getModel().getFormatedFileName()).addAttribute("overwrite",
        //                                                                                                                                                                                                                                          String.valueOf(true)).build(),
        //                                                           SymbolComponent.class);
        //                                    CommandExecutor.INSTANCE.addTask("WriteUnit", writer);
                                }                     
                }
            return;
        }
        if(e.getActionCommand().equals("Close")) {
        	super.Close();
        }           
        if (e.getActionCommand().equals("SaveAs")) {
            if (Configuration.get().isIsOnline() && User.get().isAnonymous()) {
                User.showMessageDialog(circuitComponent.getDialogFrame().getParentFrame(), "Anonymous access denied.");
                return;
            }

            (new CircuitSaveDialog(this.getParentFrame(), circuitComponent,Configuration.get().isIsOnline())).build();
        }
//        if (e.getActionCommand().equals("save")) {
//            if (Configuration.get().isIsOnline() && User.get().isAnonymous()) {
//                User.showMessageDialog(circuitComponent.getDialogFrame().getParentFrame(), "Anonymous access denied.");
//                return;
//            }
//            //could be a freshly imported circuit with no library/project name
//            if (circuitComponent.getModel().getLibraryName() == null && circuitComponent.getModel().getUnit() != null) {
//                (new CircuitSaveDialog(this.parent, circuitComponent,Configuration.get().isIsOnline())).build();
//                return;
//            }
//            //save the file
//            if (!Configuration.get().isIsApplet()) {
//
//                Command writer =
//                    new WriteUnitLocal(this, circuitComponent.getModel().Format(),
//                                       Configuration.get().getCircuitsRoot(),
//                                       circuitComponent.getModel().getLibraryName(), null,
//                                       circuitComponent.getModel().getFileName(), true, CircuitComponent.class);
//                CommandExecutor.INSTANCE.addTask("WriteUnitLocal", writer);
//            } else {
//
//                Command writer =
//                    new WriteConnector(this, circuitComponent.getModel().Format(),
//                                       new RestParameterMap.ParameterBuilder("/circuits").addURI(circuitComponent.getModel().getLibraryName()).addURI(circuitComponent.getModel().getFormatedFileName()).addAttribute("overwrite",
//                                                                                                                                                                                                                      String.valueOf(true)).build(),
//                                       CircuitComponent.class);
//                CommandExecutor.INSTANCE.addTask("WriteUnit", writer);
//            }
//        }
        if (e.getSource()==SymbolButton) {
            AbstractLoadDialog.Builder builder=new SymbolLoadDialog.Builder();
            AbstractLoadDialog symbolLoadDialog =builder.setWindow(this.getParentFrame()).setCaption("Load Symbol").setEnabled(true).build();

            symbolLoadDialog.pack();
            symbolLoadDialog.setLocationRelativeTo(null); //centers on screen
            symbolLoadDialog.setVisible(true);

            if (symbolLoadDialog.getSelectedModel() == null) {
                return;
            }
            

            Symbol symbol = (Symbol) symbolLoadDialog.getSelectedModel().getUnit();
            SCHSymbol schsymbol = CircuitMgr.getInstance().createSCHSymbol(symbol);

            //            //***set chip cursor
            schsymbol.move(-1 * (int) schsymbol.getBoundingShape().getCenter().x,
                           -1 * (int) schsymbol.getBoundingShape().getCenter().y);
            
            circuitComponent.setMode(Mode.SYMBOL_MODE);
            circuitComponent.setContainerCursor(schsymbol);
            circuitComponent.getEventMgr().setEventHandle("cursor", schsymbol);

            symbolLoadDialog.dispose();
            symbolLoadDialog = null;
            this.circuitComponent.requestFocusInWindow(); //***enable keyboard clicks

        }
        if(e.getSource()==VccSymbolButton || e.getSource()==GndSymbolButton) {
        	try {        		
        		String content=Utilities.readResourceFile(Circuit.class,(e.getSource()==VccSymbolButton?"symbols/power.xml":"symbols/ground.xml"));
        		SymbolContainer symbolContainer=new SymbolContainer();
        		symbolContainer.parse(content);        		
        		SCHSymbol schsymbol = CircuitMgr.getInstance().createSCHSymbol(symbolContainer.getUnit());
                //            //***set chip cursor
                schsymbol.move(-1 * (int) schsymbol.getBoundingShape().getCenter().x,
                               -1 * (int) schsymbol.getBoundingShape().getCenter().y);
                circuitComponent.setMode(Mode.SYMBOL_MODE);
                circuitComponent.setContainerCursor(schsymbol);
                circuitComponent.getEventMgr().setEventHandle("cursor", schsymbol);
        	}catch(Exception ioe) {
        		ioe.printStackTrace();
        	}
        }
        if (e.getSource()==SelectionButton ){
            circuitComponent.setMode(Mode.COMPONENT_MODE);
        }
        if (e.getSource()==WireButton) {
            circuitComponent.setMode(Mode.WIRE_MODE);
        }
        if (e.getSource()==BusButton) {
            circuitComponent.setMode(Mode.BUS_MODE);
        }
            if (e.getSource()==BusPinButton) {
            circuitComponent.setMode(Mode.BUSPIN_MODE);
        }
        if (e.getSource()==LabelButton) {
            circuitComponent.setMode(Mode.LABEL_MODE);
        }
        if (e.getSource()==NoConnectorButton) {
            circuitComponent.setMode(Mode.NOCONNECTOR_MODE);
        }   
        if (e.getSource()==NetLabelButton) {
            circuitComponent.setMode(Mode.NETLABEL_MODE);
        }         
        if (e.getSource()==CoordButton){
            if(CoordButton.getModel().isSelected()){
                circuitComponent.getModel().getUnit().setCoordinateSystem(new CoordinateSystem(circuitComponent.getModel().getUnit(),2));
                circuitComponent.setMode(Mode.ORIGIN_SHIFT_MODE);
            }else{
                circuitComponent.getModel().getUnit().deleteCoordinateSystem(); 
                circuitComponent.setMode(Mode.COMPONENT_MODE); 
            }            
        }        
        if (e.getSource()==JunctionButton) {
            circuitComponent.setMode(Mode.JUNCTION_MODE);
        }
        if (e.getSource()==ConnectorButton) {
            circuitComponent.setMode(Mode.CONNECTOR_MODE);
        }
        if (e.getSource()==ScaleIn) {
            circuitComponent.zoomIn(new Point((int) circuitComponent.getVisibleRect().getCenterX(),
                                              (int) circuitComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==ScaleOut) {
            circuitComponent.zoomOut(new Point((int) circuitComponent.getVisibleRect().getCenterX(),
                                               (int) circuitComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==DragHeand) {
            circuitComponent.setMode(Mode.DRAGHEAND_MODE);
        }
        if (e.getSource()==PositionToCenter) {
        	circuitComponent.getModel().getUnit().getScalableTransformation().setScaleFactor(1);
            circuitComponent.setViewportPosition(circuitComponent.getModel().getUnit().getWidth() / 2,
                                               circuitComponent.getModel().getUnit().getHeight() / 2);
            circuitComponent.Repaint();
        }

    }
}
