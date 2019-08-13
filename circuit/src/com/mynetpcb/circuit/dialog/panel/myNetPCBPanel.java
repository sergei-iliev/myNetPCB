package com.mynetpcb.circuit.dialog.panel;


import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.container.BoardContainerFactory;
import com.mynetpcb.board.dialog.BoardEditorDialog;
import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.container.CircuitContainer;
import com.mynetpcb.circuit.container.CircuitContainerFactory;
import com.mynetpcb.circuit.dialog.print.CircuitPrintDialog;
import com.mynetpcb.circuit.dialog.save.CircuitImageExportDialog;
import com.mynetpcb.circuit.dialog.save.CircuitSaveDialog;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.circuit.unit.CircuitMgr;
import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.container.UnitContainerProducer;
import com.mynetpcb.core.capi.credentials.User;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.gui.filter.ImpexFileFilter;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.impex.ImpexProcessor;
import com.mynetpcb.core.capi.impex.XMLExporter;
import com.mynetpcb.core.capi.impex.XMLImportTask;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.FutureCommand;
import com.mynetpcb.core.capi.io.WriteUnitLocal;
import com.mynetpcb.core.capi.io.remote.WriteConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.popup.JPopupButton;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.dialog.config.PreferencesDialog;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.core.utils.VersionUtils;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.container.FootprintContainerFactory;
import com.mynetpcb.pad.dialog.FootprintEditorDialog;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.container.SymbolContainerFactory;
import com.mynetpcb.symbol.dialog.SymbolEditorDialog;
import com.mynetpcb.symbol.dialog.SymbolLoadDialog;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;


public class myNetPCBPanel extends JPanel implements DialogFrame, CommandListener, ActionListener {

    private final Frame parent;
    private final JRootPane rootPane;
    private CircuitComponent circuitComponent;
    private JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
    private JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL);
    private GridBagLayout gridBagLayout = new GridBagLayout();
    private JPanel circuitBasePanel = new JPanel(gridBagLayout);
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();


    private JPanel NorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel WestPanel = new JPanel();
    private SymbolsPanel symbolsPanel;
    private CircuitsPanel circuitsPanel;
    private JPanel leftButtonGroupPanel = new JPanel();

    private JToggleButton WireButton = new JToggleButton();
    private JToggleButton BusButton = new JToggleButton();
    private JToggleButton BusPinButton = new JToggleButton();
    private JToggleButton SelectionButton = new JToggleButton();
    private JToggleButton JunctionButton = new JToggleButton();
    private JToggleButton LabelButton = new JToggleButton();
    private JToggleButton ConnectorButton = new JToggleButton();
    private JToggleButton NoConnectorButton = new JToggleButton();
    private JToggleButton NetLabelButton = new JToggleButton();
    private JToggleButton CoordButton = new JToggleButton();
    
    private JButton SymbolButton = new JButton();
    private ButtonGroup group = new ButtonGroup();
    private JPanel basePanel;
    //private JScrollPane scrollPane;
    private JTabbedPane tabbedPane = new JTabbedPane();


    //top menu icons
    private JPopupButton AddCircuitButton = new JPopupButton(this);
    private JButton PrintButton = new JButton();
    private JButton SaveButton = new JButton();
    private JButton LoadButton = new JButton();
    private JButton ScaleIn = new JButton();
    private JButton ScaleOut = new JButton();
    private JButton RotateLeft = new JButton();
    private JButton RotateRight = new JButton();
    private JToggleButton DragHeand = new JToggleButton();
    private JButton PositionToCenter = new JButton();
    private JButton LoadBoard = new JButton();

    //menu
    private JMenuBar menuBar;
    private JMenu filemenu;
    private JMenuItem menuItem;
    

    public myNetPCBPanel(JRootPane rootPane, Frame parent) {
        this.rootPane = rootPane;
        this.parent = parent;
        Init();
    }

    private void Init() {
        Container content = rootPane.getContentPane();
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());

        JMenu submenu;
        //Create the menu bar.
        menuBar = new JMenuBar();
        //Build the first menu.
        filemenu = new JMenu("File");
        menuItem = new JMenuItem("New Project");
        menuItem.setActionCommand("Create");    
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        
        menuItem = new JMenuItem("Open Project");
        menuItem.setActionCommand("load");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        
        menuItem = new JMenuItem("Close");
        menuItem.setActionCommand("deletecontainer");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);        
        //***separator
        filemenu.addSeparator();
        menuItem = new JMenuItem("New Circuit");
        menuItem.setActionCommand("Add");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        //***separator
        filemenu.addSeparator();
        menuItem = new JMenuItem("Save As...");
        menuItem.setActionCommand("saveas");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);

        menuItem = new JMenuItem("Save");
        menuItem.setActionCommand("save");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);

        //***separator
        filemenu.addSeparator();
        menuItem = new JMenuItem("Import");
        menuItem.setActionCommand("import");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        menuItem.setEnabled(Configuration.get().isLoacalFileAccessAllowed());
        //***separator
        filemenu.addSeparator();

        submenu = new JMenu("Export");

        menuItem = new JMenuItem("Image");
        menuItem.setActionCommand("exportcircuitimage");
        menuItem.addActionListener(this);
        submenu.add(menuItem);
        submenu.setEnabled(Configuration.get().isLoacalFileAccessAllowed());
        filemenu.add(submenu);

        menuItem = new JMenuItem("XML");
        menuItem.setActionCommand("exportcircuitxml");
        menuItem.addActionListener(this);
        submenu.add(menuItem);
        
        filemenu.addSeparator();
        //menuItem = new JMenuItem("Print Preview");
        //menuItem.addActionListener(this);
        //filemenu.add(menuItem);
        menuItem = new JMenuItem("Print");
        menuItem.setActionCommand("printcircuit");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        filemenu.addSeparator();
        menuItem = new JMenuItem("Exit");
        menuItem.setActionCommand("exit");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        menuBar.add(filemenu);

        filemenu = new JMenu("Edit");
        menuBar.add(filemenu);
        menuItem = new JMenuItem("Undo");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        menuItem.setActionCommand("undo");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        menuItem = new JMenuItem("Redo");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        menuItem.setActionCommand("redo");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        filemenu.addSeparator();
        menuBar.add(filemenu);
        
        menuItem = new JMenuItem("Copy");
        menuItem.setActionCommand("copy");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        
        menuItem = new JMenuItem("Paste");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menuItem.setActionCommand("paste");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        
        filemenu.addSeparator();        
        menuItem = new JMenuItem("Select All");
        menuItem.setActionCommand("selectall");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        menuItem = new JMenuItem("Delete Circuit");
        menuItem.setActionCommand("deleteunit");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        menuItem = new JMenuItem("Delete Project");
        menuItem.setActionCommand("deletecontainer");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        filemenu.addSeparator();
        menuItem = new JMenuItem("Generate symbol names");
        menuItem.setActionCommand("fixsymbolnames");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);


        filemenu = new JMenu("Tools");
        menuBar.add(filemenu);
        menuItem = new JMenuItem("Create Symbol");
        menuItem.setActionCommand("createsymbol");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        menuItem = new JMenuItem("Create Package");
        menuItem.setActionCommand("createpackage");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        menuItem = new JMenuItem("Create Board");
        menuItem.setActionCommand("createboard");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);

        filemenu = new JMenu("Project");
        menuBar.add(filemenu);
        menuItem = new JMenuItem("Preferences");
        menuItem.setActionCommand("preferences");
        menuItem.addActionListener(this);
        filemenu.add(menuItem);
        rootPane.setJMenuBar(menuBar);

        filemenu = new JMenu("Help");
        menuBar.add(filemenu);
        menuItem = new JMenuItem("Version - " + VersionUtils.MYNETPCB_VERSION);
        filemenu.add(menuItem);
        rootPane.setJMenuBar(menuBar);

        basePanel.add(NorthPanel, BorderLayout.NORTH);


        circuitComponent = new CircuitComponent(this);
        circuitComponent.setPreferredSize(new Dimension(900, 700));
        circuitComponent.setBackground(Color.WHITE);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        circuitBasePanel.add(circuitComponent, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.001;
        gridBagConstraints.weighty = 0.001;
        circuitBasePanel.add(vbar, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.001;
        gridBagConstraints.weighty = 0.001;
        circuitBasePanel.add(hbar, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        circuitBasePanel.add(new JPanel(), gridBagConstraints);


        basePanel.add(circuitBasePanel, BorderLayout.CENTER);

        symbolsPanel = new SymbolsPanel(circuitComponent);
        circuitsPanel = new CircuitsPanel(circuitComponent);

        //***WEST PANEL
        SymbolButton.addActionListener(this);
        SymbolButton.setActionCommand("addsymbol");
        SymbolButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/symbol.png"));
        SymbolButton.setToolTipText("Add Symbol");
        SymbolButton.setPreferredSize(new Dimension(35, 35));


        SelectionButton.addActionListener(this);
        SelectionButton.setActionCommand("select");
        SelectionButton.setSelected(true);
        SelectionButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/selection.png"));
        SelectionButton.setToolTipText("Selection");
        SelectionButton.setPreferredSize(new Dimension(35, 35));


        WireButton.addActionListener(this);
        WireButton.setActionCommand("wire");
        WireButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/wire.png"));
        WireButton.setToolTipText("Draw Wire");
        WireButton.setPreferredSize(new Dimension(35, 35));

        BusButton.addActionListener(this);
        BusButton.setActionCommand("bus");
        BusButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/bus.png"));
        BusButton.setToolTipText("Draw Bus");
        BusButton.setPreferredSize(new Dimension(35, 35));

        BusPinButton.addActionListener(this);
        BusPinButton.setActionCommand("buspin");
        BusPinButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/buspin.png"));
        BusPinButton.setToolTipText("Add Bus Pin");
        BusPinButton.setPreferredSize(new Dimension(35, 35));


        JunctionButton.addActionListener(this);
        JunctionButton.setActionCommand("junction");
        JunctionButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/junction.png"));
        JunctionButton.setToolTipText("Draw Junction");
        JunctionButton.setPreferredSize(new Dimension(35, 35));


        LabelButton.addActionListener(this);
        LabelButton.setActionCommand("label");
        LabelButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/label.png"));
        LabelButton.setToolTipText("Add Label");
        LabelButton.setPreferredSize(new Dimension(35, 35));


        ConnectorButton.addActionListener(this);
        ConnectorButton.setActionCommand("connector");
        ConnectorButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/connector.png"));
        ConnectorButton.setToolTipText("Add Connector");
        ConnectorButton.setPreferredSize(new Dimension(35, 35));

        NoConnectorButton.addActionListener(this);
        NoConnectorButton.setActionCommand("noconnection");
        NoConnectorButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/noconnection.png"));
        NoConnectorButton.setToolTipText("Add No Connection flag");
        NoConnectorButton.setPreferredSize(new Dimension(35, 35));

        NetLabelButton.addActionListener(this);
        NetLabelButton.setActionCommand("netlabel");
        NetLabelButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/netlabel.png"));
        NetLabelButton.setToolTipText("Add Net label");
        NetLabelButton.setPreferredSize(new Dimension(35, 35));

        CoordButton.addActionListener(this);
        CoordButton.setActionCommand("CoordOrigin");
        CoordButton.setToolTipText("Change coordinate origin");
        CoordButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/origin.png"));
        CoordButton.setPreferredSize(new Dimension(35, 35));
        
        //group.add(SymbolButton);
        group.add(SelectionButton);
        group.add(WireButton);
        group.add(BusButton);
        group.add(BusPinButton);
        group.add(JunctionButton);
        group.add(LabelButton);
        group.add(ConnectorButton);
        group.add(NoConnectorButton);
        group.add(NetLabelButton);
        group.add(CoordButton);
        group.add(DragHeand);

        //***construct Top Buttons Panel
        AddCircuitButton.setToolTipText("New Circuit");
        AddCircuitButton.setPreferredSize(new Dimension(35, 35));
        AddCircuitButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/subject.png"));
        AddCircuitButton.addMenu("Create new circuits project","Create").addMenu("Add circuit to project","Add");
        
        PrintButton.setToolTipText("Print Circuit");
        PrintButton.setPreferredSize(new Dimension(35, 35));
        PrintButton.setActionCommand("printcircuit");
        PrintButton.addActionListener(this);
        PrintButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/print.png"));

        SaveButton.setToolTipText("Save Circuit");
        SaveButton.setPreferredSize(new Dimension(35, 35));
        SaveButton.setActionCommand("save");
        SaveButton.addActionListener(this);
        SaveButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/save.png"));

        LoadButton.setToolTipText("Load Circuit");
        LoadButton.addActionListener(this);
        LoadButton.setPreferredSize(new Dimension(35, 35));
        LoadButton.setActionCommand("load");
        LoadButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/folder.png"));

        ScaleIn.setToolTipText("Scale In");
        ScaleIn.setPreferredSize(new Dimension(35, 35));
        ScaleIn.setActionCommand("scalein");
        ScaleIn.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/zoom_out.png"));

        ScaleOut.setToolTipText("Scale Out");
        ScaleOut.setPreferredSize(new Dimension(35, 35));
        ScaleOut.setActionCommand("scaleout");
        ScaleOut.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/zoom_in.png"));

        RotateLeft.setToolTipText("Rotate Left");
        RotateLeft.setPreferredSize(new Dimension(35, 35));
        RotateLeft.setActionCommand("rotateleft");
        RotateLeft.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/rotate_left.png"));

        RotateRight.setToolTipText("Rotate Right");
        RotateRight.setPreferredSize(new Dimension(35, 35));
        RotateRight.setActionCommand("rotateright");
        RotateRight.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/rotate_right.png"));
        
        DragHeand.setToolTipText("Drag canvas");
        DragHeand.setPreferredSize(new Dimension(35, 35));
        DragHeand.addActionListener(this);
        DragHeand.setActionCommand("dragheand");
        DragHeand.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/grab.png"));

        PositionToCenter.setToolTipText("Position viewport to center");
        PositionToCenter.setPreferredSize(new Dimension(35, 35));
        PositionToCenter.addActionListener(this);
        PositionToCenter.setActionCommand("tocenter");
        PositionToCenter.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/tocenter.png"));
        
        LoadBoard.setToolTipText("Load board");
        LoadBoard.setPreferredSize(new Dimension(35, 35));
        LoadBoard.addActionListener(this);
        LoadBoard.setActionCommand("createboard");
        LoadBoard.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/board.png"));
        //LoadBoard.setVisible(false);
        
        
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
        NorthPanel.add(Box.createRigidArea(new Dimension(35, 35)));
        NorthPanel.add(LoadBoard);
        
        WestPanel.setLayout(new BorderLayout());

        leftButtonGroupPanel.setLayout(new BoxLayout(leftButtonGroupPanel, BoxLayout.Y_AXIS));
        leftButtonGroupPanel.setBorder(BorderFactory.createEmptyBorder(25, 4, 0, 4));
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SymbolButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SelectionButton);
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
        //****EAST PANEL
        tabbedPane.setPreferredSize(new Dimension(220, 200));
        //***create circuit tab
        tabbedPane.addTab("Circuits", circuitsPanel);
        //***create symbol tab
        tabbedPane.addTab("Symbols", symbolsPanel);
        tabbedPane.addChangeListener(symbolsPanel);
        content.add(basePanel); // Add components to the content

        //*****Initialize circuit listeners
        circuitComponent.getModel().addShapeListener(circuitsPanel);
        circuitComponent.getModel().addUnitListener(circuitsPanel);
        circuitComponent.addContainerListener(circuitsPanel);

        //register global keybord hook target
        CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
        //****We need the circuit here

        ScaleIn.addActionListener(this);
        ScaleOut.addActionListener(this);
        RotateLeft.addActionListener(this);
        RotateRight.addActionListener(this);

        //***Add a wheel listener
        basePanel.add(tabbedPane, BorderLayout.EAST);
        requestFocus();

    }
    


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("exit")) {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(parent,
                                                                                        WindowEvent.WINDOW_CLOSING));
        }
        if (e.getActionCommand().equals("preferences")) {
            PreferencesDialog d = new PreferencesDialog(parent, "Preferences");
            d.pack();
            d.setLocationRelativeTo(null); //centers on screen
            d.setFocusable(true);
            d.setVisible(true);
            return;
        }
        if (e.getActionCommand().equals("import")) {
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            fc.setDialogTitle("Import");
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new ImpexFileFilter(".xml"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String targetFile ;
                    if (fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(".xml")) {
                        targetFile=fc.getSelectedFile().getAbsolutePath();
                    } else {
                        targetFile= fc.getSelectedFile().getAbsolutePath() + ".xml";
                    }
                    
                    UnitContainerProducer unitContainerProducer=new UnitContainerProducer().withFactory("circuits", new CircuitContainerFactory()).withFactory("modules", new SymbolContainerFactory()).
                         withFactory("footprints", new FootprintContainerFactory()).withFactory("boards", new BoardContainerFactory());
                    
                    
                    CommandExecutor.INSTANCE.addTask("import",
                                                     new XMLImportTask(this,
                                                                       unitContainerProducer,
                                                                       targetFile, XMLImportTask.class));
                } catch (Exception ioe) {
                    ioe.printStackTrace(System.out);
                    return;
                }
            }
        }
        if (e.getActionCommand().equals("Create")) {
            if(circuitComponent.getModel().isChanged()){                        
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(circuitComponent.getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Create", JOptionPane.YES_NO_OPTION)) {                                       
                    return;
                }                      
            }
            circuitComponent.Clear();                              
        }
        
        if (e.getActionCommand().equals("Add")||e.getActionCommand().equals("Create")) {
            //rememeber current unit position
            if (circuitComponent.getModel().getUnit() != null) {
                circuitComponent.getModel().getUnit().setScrollPositionValue(circuitComponent.getViewportWindow().x,
                                                                             circuitComponent.getViewportWindow().y);
            }
            Circuit circuit = new Circuit(1000, 800);
            circuitComponent.getModel().Add(circuit);
            circuitComponent.getModel().setActiveUnit(circuit.getUUID());
            circuitComponent.componentResized(null);
            circuitComponent.getModel().fireUnitEvent(new UnitEvent(circuitComponent.getModel().getUnit(),
                                                                    UnitEvent.SELECT_UNIT));
            circuitComponent.Repaint();
        }
        
        if (e.getActionCommand().equals("load")) {
            //CircuitLoadDialog circuitLoadDialog = new CircuitLoadDialog(parent, "Load Project", false);
            
            AbstractLoadDialog.Builder builder=getUnitComponent().getLoadDialogBuilder();
            AbstractLoadDialog circuitLoadDialog =builder.setWindow(getUnitComponent().getDialogFrame().getParentFrame()).setCaption("Load Project").setEnabled(false).build();
            
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
        if(e.getActionCommand().equals("createboard")){
            BoardEditorDialog boardEditorDialog = new BoardEditorDialog(parent,"Board Editor");        
            boardEditorDialog.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight())); 
            boardEditorDialog.pack();
            boardEditorDialog.setLocationRelativeTo(null); //centers on screen
            boardEditorDialog.setFocusable(true);
            boardEditorDialog.setVisible(true);  
            CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
        }
        if (e.getActionCommand().equals("createpackage")) {
            FootprintEditorDialog footprintEditorDialog = new FootprintEditorDialog(parent, "Footprint Editor");
            footprintEditorDialog.pack();
            footprintEditorDialog.setLocationRelativeTo(null); //centers on screen
            footprintEditorDialog.setFocusable(true);
            footprintEditorDialog.setVisible(true);
            CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
        }
        if (e.getActionCommand().equals("createsymbol")) {
            SymbolEditorDialog symbolEditorDialog = new SymbolEditorDialog(parent, "Symbol Editor");
            symbolEditorDialog.pack();
            symbolEditorDialog.setLocationRelativeTo(null); //centers on screen
            symbolEditorDialog.setFocusable(true);
            symbolEditorDialog.setVisible(true);
            CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
        }
        if (circuitComponent.getModel().getUnit() == null) {
            return;
        }
        if (e.getActionCommand().equals("copy")) {
            circuitComponent.Copy();                
        }
        if (e.getActionCommand().equals("paste")) {
           circuitComponent.Paste();
        }
        if (e.getActionCommand().equals("fixsymbolnames")) {
            CircuitMgr.getInstance().generateShapeNaming(circuitComponent.getModel().getUnit());
            circuitComponent.Repaint();
        }
        if (e.getActionCommand().equals("exportcircuitimage")) {            
            JDialog d=new CircuitImageExportDialog(parent, circuitComponent);
            d.setLocationRelativeTo(null); //centers on screen
            d.setVisible(true);

        }
        if (e.getActionCommand().equals("exportcircuitxml")) {
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

        if (e.getActionCommand().equals("rotateleft") || e.getActionCommand().equals("rotateright")) {        
            Collection<Shape> shapes= circuitComponent.getModel().getUnit().getShapes();
            if(shapes.size()==0){
               return; 
            }   
            //***notify undo manager                    
            circuitComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
            Rectangle r=circuitComponent.getModel().getUnit().getShapesRect(shapes);  

            CircuitMgr.getInstance().rotateBlock(shapes,
                                   AffineTransform.getRotateInstance((e.getActionCommand().equals("rotateleft")?
                                                                      -1 :
                                                                      1) *(Math.PI /2),
                                                                     r.getCenterX(),
                                                                     r.getCenterY())); 
            CircuitMgr.getInstance().alignBlock(circuitComponent.getModel().getUnit().getGrid(),shapes);                     
            CircuitMgr.getInstance().normalizePinText(shapes);
            //***notify undo manager
            circuitComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).Add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
            circuitComponent.Repaint();
        }
        if (e.getActionCommand().equals("printcircuit")) {            
            JDialog d=new CircuitPrintDialog(parent,circuitComponent,"Print");
            d.setLocationRelativeTo(null); //centers on screen
            d.setVisible(true);
        }
        if(e.getActionCommand().equals("undo")){
         circuitComponent.getModel().getUnit().Undo(null);
         circuitComponent.Repaint();
         circuitComponent.revalidate();
         return;
        }
        
        if(e.getActionCommand().equals("redo")){
         circuitComponent.getModel().getUnit().Redo();
         circuitComponent.Repaint();
         circuitComponent.revalidate();        
         return;
        }
        if(e.getActionCommand().equals("selectall")){
            circuitComponent.getModel().getUnit().setSelected(true);
            circuitComponent.Repaint();          
        }
        if(e.getActionCommand().equals("deleteunit")){
            circuitComponent.getModel().Delete(circuitComponent.getModel().getUnit().getUUID());
            if (circuitComponent.getModel().getUnits().size() > 0) {
                circuitComponent.getModel().setActiveUnit(0);
                circuitComponent.revalidate();
                circuitComponent.getModel().fireUnitEvent(new UnitEvent(circuitComponent.getModel().getUnit(), UnitEvent.SELECT_UNIT));
            }else{
                circuitComponent.Clear();
                circuitComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.DELETE_CONTAINER));
            }
            circuitComponent.componentResized(null);  
            circuitComponent.Repaint();              
        }
        if(e.getActionCommand().equals("deletecontainer")){
            if(circuitComponent.getModel().isChanged()){                        
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(circuitComponent.getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Close", JOptionPane.YES_NO_OPTION)) {                                       
                    return;
                }                      
            }                        
            circuitComponent.Clear();
            circuitComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.DELETE_CONTAINER));            
            circuitComponent.componentResized(null);
            circuitComponent.Repaint();        
        }        
        if (e.getActionCommand().equals("saveas")) {
            if (Configuration.get().isIsOnline() && User.get().isAnonymous()) {
                User.showMessageDialog(circuitComponent.getDialogFrame().getParentFrame(), "Anonymous access denied.");
                return;
            }

            (new CircuitSaveDialog(this.parent, circuitComponent,Configuration.get().isIsOnline())).build();
        }

        if (e.getActionCommand().equals("save")) {
            if (Configuration.get().isIsOnline() && User.get().isAnonymous()) {
                User.showMessageDialog(circuitComponent.getDialogFrame().getParentFrame(), "Anonymous access denied.");
                return;
            }
            //could be a freshly imported circuit with no library/project name
            if (circuitComponent.getModel().getLibraryName() == null && circuitComponent.getModel().getUnit() != null) {
                (new CircuitSaveDialog(this.parent, circuitComponent,Configuration.get().isIsOnline())).build();
                return;
            }
            //save the file
            if (!Configuration.get().isIsApplet()) {

                Command writer =
                    new WriteUnitLocal(this, circuitComponent.getModel().Format(),
                                       Configuration.get().getCircuitsRoot(),
                                       circuitComponent.getModel().getLibraryName(), null,
                                       circuitComponent.getModel().getFileName(), true, CircuitComponent.class);
                CommandExecutor.INSTANCE.addTask("WriteUnitLocal", writer);
            } else {

                Command writer =
                    new WriteConnector(this, circuitComponent.getModel().Format(),
                                       new RestParameterMap.ParameterBuilder("/circuits").addURI(circuitComponent.getModel().getLibraryName()).addURI(circuitComponent.getModel().getFormatedFileName()).addAttribute("overwrite",
                                                                                                                                                                                                                      String.valueOf(true)).build(),
                                       CircuitComponent.class);
                CommandExecutor.INSTANCE.addTask("WriteUnit", writer);
            }
        }
        if (e.getActionCommand().equals("addsymbol")) {
            //SymbolLoadDialog symbolLoadDialog = new SymbolLoadDialog(parent, "Load Symbol", true);
            AbstractLoadDialog.Builder builder=new SymbolLoadDialog.Builder();
            AbstractLoadDialog symbolLoadDialog =builder.setWindow(parent).setCaption("Load Symbol").setEnabled(true).build();

            symbolLoadDialog.pack();
            symbolLoadDialog.setLocationRelativeTo(null); //centers on screen
            symbolLoadDialog.setVisible(true);

            if (symbolLoadDialog.getSelectedModel() == null) {
                return;
            }
            circuitComponent.setMode(CircuitComponent.SYMBOL_MODE);

            Symbol symbol = (Symbol) symbolLoadDialog.getSelectedModel().getUnit();
            SCHSymbol schsymbol = CircuitMgr.getInstance().createSCHSymbol(symbol);


            //footprintComponent.getModel().registerInitialState();
            //            symbolComponent.getModel().setLibraryName(source.getLibraryName());
            //            symbolComponent.getModel().setCategoryName(source.getCategoryName());
            //            symbolComponent.getModel().setFileName(source.getFileName());
            //            symbolComponent.getModel().setDesignerName(source.getDesignerName());
            //            symbolComponent.getModel().setActiveUnit(0);
            //            symbolComponent.componentResized(null);
            //            symbolComponent.getModel().getUnit().setSelected(false);
            //            symbolComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.RENAME_CONTAINER));
            //            symbolComponent.getModel().fireUnitEvent(new UnitEvent(symbolComponent.getModel().getUnit(),
            //                                                                      UnitEvent.SELECT_UNIT));
            //            symbolComponent.Repaint();

            //            Chip chip = CircuitMgr.getInstance().createChip(symbolLoadDialog.getComponent().getModel().getUnit());
            //
            //            circuitComponent.getModel().getUnit().setSelected(false);
            //
            //            //***tweak naming
            //            CircuitMgr.getInstance().symbolNaming(circuitComponent.getModel().getUnit(), chip);
            //            //***set chip cursor
            schsymbol.Move(-1 * (int) schsymbol.getBoundingShape().getBounds().getCenterX(),
                           -1 * (int) schsymbol.getBoundingShape().getBounds().getCenterY());
            circuitComponent.setContainerCursor(schsymbol);
            circuitComponent.getEventMgr().setEventHandle("cursor", schsymbol);

            symbolLoadDialog.dispose();
            symbolLoadDialog = null;
            this.circuitComponent.requestFocusInWindow(); //***enable keyboard clicks

        }
        if (e.getActionCommand().equals("select")) {
            circuitComponent.setMode(CircuitComponent.COMPONENT_MODE);
        }
        if (e.getActionCommand().equals("wire")) {
            circuitComponent.setMode(CircuitComponent.WIRE_MODE);
        }
        if (e.getActionCommand().equals("bus")) {
            circuitComponent.setMode(CircuitComponent.BUS_MODE);
        }
        if (e.getActionCommand().equals("buspin")) {
            circuitComponent.setMode(CircuitComponent.BUSPIN_MODE);
        }
        if (e.getActionCommand().equals("label")) {
            circuitComponent.setMode(CircuitComponent.LABEL_MODE);
        }
        if (e.getActionCommand().equals("netlabel")) {
            circuitComponent.setMode(CircuitComponent.NETLABEL_MODE);
        }        
        if (e.getActionCommand().equals("CoordOrigin")) {
            circuitComponent.setMode(CircuitComponent.ORIGIN_SHIFT_MODE);
        }        
        if (e.getActionCommand().equals("junction")) {
            circuitComponent.setMode(CircuitComponent.JUNCTION_MODE);
        }
        if (e.getActionCommand().equals("connector")) {
            circuitComponent.setMode(CircuitComponent.CONNECTOR_MODE);
        }
        if (e.getActionCommand().equals("noconnection")) {
            circuitComponent.setMode(CircuitComponent.NOCONNECTION_MODE);
        }
        if (e.getActionCommand().equals("scalein")) {
            circuitComponent.ZoomIn(new Point((int) circuitComponent.getVisibleRect().getCenterX(),
                                              (int) circuitComponent.getVisibleRect().getCenterY()));
        }
        if (e.getActionCommand().equals("scaleout")) {
            circuitComponent.ZoomOut(new Point((int) circuitComponent.getVisibleRect().getCenterX(),
                                               (int) circuitComponent.getVisibleRect().getCenterY()));
        }
        if (e.getActionCommand().equals("dragheand")) {
            circuitComponent.setMode(CircuitComponent.DRAGHEAND_MODE);
        }
        if (e.getActionCommand().equals("tocenter")) {
            circuitComponent.setScrollPosition(circuitComponent.getModel().getUnit().getWidth() / 2,
                                               circuitComponent.getModel().getUnit().getHeight() / 2);
        }
    }

    @Override
    public Window getParentFrame() {
        return parent;
    }

    @Override
    public void setButtonGroup(int requestedMode) {
        if (circuitComponent.getMode() == CircuitComponent.WIRE_MODE) {
            circuitComponent.getEventMgr().resetEventHandle();
        }
        //***post operations
        switch (requestedMode) {
        case CircuitComponent.SYMBOL_MODE:
            group.clearSelection();
            break;
        case CircuitComponent.COMPONENT_MODE:
            group.setSelected(SelectionButton.getModel(), true);
            break;
        case CircuitComponent.WIRE_MODE:
            group.setSelected(WireButton.getModel(), true);
            break;
        case CircuitComponent.BUS_MODE:
            group.setSelected(BusButton.getModel(), true);
        }
    }


    public CircuitComponent getUnitComponent() {
        return circuitComponent;
    }

    public JRootPane getRootPane() {
        return rootPane;
    }

    public JScrollBar getVerticalScrollBar() {
        return vbar;
    }

    public JScrollBar getHorizontalScrollBar() {
        return hbar;
    }

    @Override
    public void OnStart(Class<?> receiver) {
        
        if(receiver==XMLImportTask.class){
           DisabledGlassPane.block(this.getRootPane(), "Importing...");
        }
        if(receiver==CircuitComponent.class){
            DisabledGlassPane.block(this.getRootPane(), "Saving..."); 
        }
    }

    @Override
    public void OnRecive(String string, Class receiver) {
    }

    @Override
    public void OnFinish(Class<?> receiver) {
        DisabledGlassPane.unblock(this.getRootPane());        
        
        if(receiver==CircuitComponent.class){ 
           circuitComponent.getModel().registerInitialState();
        }
        if (receiver == XMLImportTask.class) {
            FutureCommand task = CommandExecutor.INSTANCE.getTaskByName("import");
            UnitContainer unitContainer = null;
            try {
                unitContainer = (UnitContainer) task.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace(System.out);
            }
            if(unitContainer==null){
                return;
            }
            if (unitContainer instanceof CircuitContainer) {
                //circuit
                LoadCircuits((CircuitContainer) unitContainer);
            } else if (unitContainer instanceof
                       SymbolContainer) {
                //symbol
                SymbolEditorDialog symbolEditorDialog =
                                             new SymbolEditorDialog(parent, "Symbol Editor",
                                                                    (SymbolContainer) unitContainer);
                symbolEditorDialog.pack();
                symbolEditorDialog.setLocationRelativeTo(null); //centers on screen
                symbolEditorDialog.setFocusable(true);
                symbolEditorDialog.setVisible(true);
                CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
            } else if(unitContainer instanceof FootprintContainer){
                FootprintEditorDialog footprintEditorDialog =
                    new FootprintEditorDialog(parent, "Footprint Editor", (FootprintContainer) unitContainer);
                footprintEditorDialog.pack();
                footprintEditorDialog.setLocationRelativeTo(null); //centers on screen
                footprintEditorDialog.setFocusable(true);
                footprintEditorDialog.setVisible(true);
                CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
            }else{
                BoardEditorDialog boardEditorDialog =
                    new BoardEditorDialog(parent, "Board Editor", (BoardContainer) unitContainer);
                boardEditorDialog.setPreferredSize(new Dimension(parent.getWidth(),parent.getHeight())); 
                boardEditorDialog.pack();
                boardEditorDialog.setLocationRelativeTo(null); //centers on screen
                boardEditorDialog.setFocusable(true);
                boardEditorDialog.setVisible(true);
                CircuitComponent.getUnitKeyboardListener().setComponent(circuitComponent);
            }
            unitContainer.Release();
        }
    }

    @Override
    public void OnError(String error) {
        DisabledGlassPane.unblock(circuitComponent.getDialogFrame().getRootPane()); 
        JOptionPane.showMessageDialog(circuitComponent.getDialogFrame().getParentFrame(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE); 
    }

    private void LoadCircuits(CircuitContainer source) {
        circuitComponent.Clear();
        circuitComponent.setMode(CircuitComponent.COMPONENT_MODE);
        setButtonGroup(CircuitComponent.COMPONENT_MODE);

        for (Circuit circuit : source.getUnits()) {
            try {
                Circuit copy = circuit.clone();
                copy.getScalableTransformation().Reset(1.2, 2, 0, ScalableTransformation.DEFAULT_MAX_SCALE_FACTOR);
                circuitComponent.getModel().Add(copy);
                copy.notifyListeners(ShapeEvent.ADD_SHAPE);
            } catch (CloneNotSupportedException f) {
                f.printStackTrace(System.out);
            }
        }
        circuitComponent.getModel().setLibraryName(source.getLibraryName());
        circuitComponent.getModel().setCategoryName(source.getCategoryName());
        circuitComponent.getModel().setFileName(source.getFileName());
        circuitComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.RENAME_CONTAINER));
        circuitComponent.getModel().setDesignerName(source.getDesignerName());
        circuitComponent.getModel().registerInitialState();
        circuitComponent.getModel().setActiveUnit(0);
        circuitComponent.getModel().getUnit().setSelected(false);
        circuitComponent.getModel().fireUnitEvent(new UnitEvent(circuitComponent.getModel().getUnit(),
                                                                UnitEvent.SELECT_UNIT));
        Rectangle r = circuitComponent.getModel().getUnit().getBoundingRect();
        circuitComponent.setScrollPosition((int) r.getCenterX(), (int) r.getCenterY());
        circuitComponent.componentResized(null);
    }
}
