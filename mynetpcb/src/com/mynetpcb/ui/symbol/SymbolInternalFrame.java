package com.mynetpcb.ui.symbol;

import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.popup.JPopupButton;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.dialog.SymbolLoadDialog;
import com.mynetpcb.symbol.dialog.panel.SymbolsPanel;
import com.mynetpcb.symbol.unit.Symbol;
import com.mynetpcb.symbol.unit.SymbolMgr;
import com.mynetpcb.ui.AbstractInternalFrame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JToggleButton;

public class SymbolInternalFrame extends AbstractInternalFrame implements DialogFrame,CommandListener,ActionListener{
    
    protected SymbolComponent symbolComponent;
    private JPanel basePanel;

    private SymbolsPanel symbolsPanel;
    private JPanel symbolBasePanel = new JPanel(new GridBagLayout());
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    private JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
    private JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL);

    private JToggleButton RectButton = new JToggleButton();
    private JToggleButton EllipseButton = new JToggleButton();
    private JToggleButton ArcButton = new JToggleButton();
    private JToggleButton SelectionButton = new JToggleButton();
    private JToggleButton LineButton = new JToggleButton();
    private JToggleButton PinButton = new JToggleButton();
    private JToggleButton LabelButton = new JToggleButton();
    private JToggleButton SnapToGridButton = new JToggleButton();
    private JToggleButton CoordButton = new JToggleButton();
    private JToggleButton ArrowButton = new JToggleButton();
    private JToggleButton TriangleButton = new JToggleButton();
    private ButtonGroup group = new ButtonGroup();

    protected JPopupButton AddFootprintButton = new JPopupButton(this);
    private JButton PrintButton = new JButton();
    private JButton SaveButton = new JButton();
    protected JButton LoadButton = new JButton();
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
    
    public SymbolInternalFrame() {
        this(null);    
    }
    
    public SymbolInternalFrame(SymbolContainer symbolContainer) {
        super("Symbols");
        init();
            
        loadSymbols(symbolContainer); 
    
    }
    private void init(){
        Container content = this.getContentPane();
        this.setPreferredSize(new Dimension(750, 600));
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());


        symbolComponent = new SymbolComponent(this);
        symbolsPanel = new SymbolsPanel(symbolComponent);

        symbolComponent.setPreferredSize(new Dimension(700, 500));
        symbolComponent.addContainerListener(symbolsPanel);
        symbolComponent.getModel().addUnitListener(symbolsPanel);
        symbolComponent.getModel().addShapeListener(symbolsPanel);
        //register key hook
        SymbolComponent.getUnitKeyboardListener().setComponent(symbolComponent);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        symbolBasePanel.add(symbolComponent, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.001;
        gridBagConstraints.weighty = 0.001;
        symbolBasePanel.add(vbar, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.001;
        gridBagConstraints.weighty = 0.001;
        symbolBasePanel.add(hbar, gridBagConstraints);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        symbolBasePanel.add(new JPanel(), gridBagConstraints);
        basePanel.add(symbolBasePanel, BorderLayout.CENTER);

        SelectionButton.addActionListener(this);
        SelectionButton.setSelected(true);
        SelectionButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/selection.png"));
        SelectionButton.setToolTipText("Selection");
        SelectionButton.setPreferredSize(new Dimension(35, 35));

        RectButton.addActionListener(this);
        RectButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/rect.png"));
        RectButton.setToolTipText("Add Rectangle");
        RectButton.setPreferredSize(new Dimension(35, 35));

        ArrowButton.addActionListener(this);
        ArrowButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/arrowline.png"));
        ArrowButton.setToolTipText("Add Arrow");
        ArrowButton.setPreferredSize(new Dimension(35, 35));
        
        TriangleButton.addActionListener(this);
        TriangleButton.setActionCommand("Triangle");
        TriangleButton.setToolTipText("Draw Triangle");
        TriangleButton.setIcon(Utilities.loadImageIcon(this, 
                                                       "/com/mynetpcb/core/images/triangle.png"));
        TriangleButton.setPreferredSize(new Dimension(35, 35));
        
        EllipseButton.addActionListener(this);
        EllipseButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/ellipse.png"));
        EllipseButton.setToolTipText("Add Ellipse");
        EllipseButton.setPreferredSize(new Dimension(35, 35));

        ArcButton.addActionListener(this);
        ArcButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/arc.png"));
        ArcButton.setToolTipText("Add Arc");
        ArcButton.setPreferredSize(new Dimension(35, 35));

        LineButton.addActionListener(this);
        LineButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/linepoligone.png"));
        LineButton.setToolTipText("Add Line or Poligone");
        LineButton.setPreferredSize(new Dimension(35, 35));

        PinButton.addActionListener(this);
        PinButton.setToolTipText("Add Pin");
        PinButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/pin.png"));
        PinButton.setPreferredSize(new Dimension(35, 35));

        LabelButton.addActionListener(this);
        LabelButton.setToolTipText("Add Label");
        LabelButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/label.png"));
        LabelButton.setPreferredSize(new Dimension(35, 35));

        SnapToGridButton.addActionListener(this);
        SnapToGridButton.setToolTipText("Snap dragging point to grid");
        SnapToGridButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/anchor.png"));
        SnapToGridButton.setPreferredSize(new Dimension(35, 35));

        CoordButton.addActionListener(this);
        CoordButton.setToolTipText("Change coordinate origin");
        CoordButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/origin.png"));
        CoordButton.setPreferredSize(new Dimension(35, 35));

        
        //***construct Top Buttons Panel

        AddFootprintButton.setToolTipText("Add footprint");
        AddFootprintButton.setPreferredSize(new Dimension(35, 35));
        AddFootprintButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/subject.png"));
        AddFootprintButton.addMenu("Create symbols bundle","Create").addMenu("Add symbol to bundle","Add").addSeparator().addMenu("Save","Save").addMenu("Save As","SaveAs").
                           addSeparator().addMenu("Export to Clipboard","clipboard.export").
                           addSeparator().addMenu("Exit","exit"); 
        
        PrintButton.addActionListener(this);
        PrintButton.setToolTipText("Print footprint");
        PrintButton.setPreferredSize(new Dimension(35, 35));
        PrintButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/print.png"));

        SaveButton.addActionListener(this);
        SaveButton.setToolTipText("Save Footprint");
        SaveButton.setActionCommand("Save");  //for inline editing
        SaveButton.setPreferredSize(new Dimension(35, 35));
        SaveButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/save.png"));

        LoadButton.addActionListener(this);
        LoadButton.setToolTipText("Load Footprint");
        //LoadButton.setEnabled(false);
        LoadButton.setPreferredSize(new Dimension(35, 35));
        LoadButton.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/folder.png"));

        ScaleIn.addActionListener(this);
        ScaleIn.setToolTipText("Scale In");
        ScaleIn.setPreferredSize(new Dimension(35, 35));
        ScaleIn.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/zoom_out.png"));

        ScaleOut.addActionListener(this);
        ScaleOut.setPreferredSize(new Dimension(35, 35));
        ScaleOut.setActionCommand("ScaleOut");
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
        DragHeand.setToolTipText("Drag canvas");
        DragHeand.addActionListener(this);
        DragHeand.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/grab.png"));

        PositionToCenter.setPreferredSize(new Dimension(35, 35));
        PositionToCenter.setToolTipText("Position viewport to center");
        PositionToCenter.addActionListener(this);
        PositionToCenter.setIcon(Utilities.loadImageIcon(this, "/com/mynetpcb/core/images/tocenter.png"));

        NorthPanel.add(AddFootprintButton);
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
        group.add(LineButton);
        group.add(RectButton);    
        group.add(ArrowButton);   
        group.add(TriangleButton);        
        group.add(PinButton);
        group.add(LabelButton);
        group.add(DragHeand);
        
        EastPanel.setLayout(new BorderLayout());
        EastPanel.setPreferredSize(new Dimension(250, 200));


        WestPanel.setLayout(new BorderLayout());
        basePanel.add(NorthPanel, BorderLayout.NORTH);
        EastPanel.add(symbolsPanel, BorderLayout.CENTER);

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
        leftButtonGroupPanel.add(ArrowButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5))); 
        leftButtonGroupPanel.add(TriangleButton);        
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(PinButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(LabelButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(SnapToGridButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(CoordButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        
        WestPanel.add(leftButtonGroupPanel, BorderLayout.NORTH);
        basePanel.add(WestPanel, BorderLayout.WEST);


        content.add(basePanel); // Add components to the content        
    }

    @Override
    public boolean isChanged() {
        return symbolComponent.getModel().isChanged();    
    }

    @Override
    public JFrame getParentFrame() {
        return  (JFrame)this.getDesktopPane().getRootPane().getParent();
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
    public void OnStart(Class<?> c) {
        // TODO Implement this method
    }

    @Override
    public void OnRecive(String string, Class<?> c) {
        // TODO Implement this method

    }

    @Override
    public void OnFinish(Class<?> c) {
        // TODO Implement this method
    }

    @Override
    public void OnError(String string) {
        // TODO Implement this method
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("exit")){
                exit();
                return;
            }
            if (e.getActionCommand().equals("Create")) {
                if(symbolComponent.getModel().isChanged()){                        
                    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(symbolComponent.getDialogFrame().getParentFrame(), "There are unsaved changes. Do you want to continue?", "Create", JOptionPane.YES_NO_OPTION)) {                                       
                        return;
                    }                      
                }
                symbolComponent.clear();                              
            }
            
            if (e.getActionCommand().equals("Add")||e.getActionCommand().equals("Create")) {                
                //rememeber current unit position
                if(symbolComponent.getModel().getUnit()!=null){
                    symbolComponent.getModel().getUnit().setScrollPositionValue((int)symbolComponent.getViewportWindow().getX(),(int)symbolComponent.getViewportWindow().getY());                      
                }
                Symbol symbol  = new Symbol(500, 500);
                symbolComponent.getModel().add(symbol);
                symbolComponent.getModel().setActiveUnit(symbol.getUUID());
                symbolComponent.componentResized(null);
                symbolComponent.getModel().fireUnitEvent(new UnitEvent(symbol, UnitEvent.SELECT_UNIT));
                symbolComponent.Repaint();
            }
            if (e.getSource()==LoadButton) {
                            AbstractLoadDialog.Builder builder=new SymbolLoadDialog.Builder();
                            AbstractLoadDialog symbolLoadDialog =builder.setWindow(this.getParentFrame()).setCaption("Load Symbol").setEnabled(false).build();
                    
                            symbolLoadDialog.pack();
                            symbolLoadDialog.setLocationRelativeTo(null); //centers on screen
                            symbolLoadDialog.setVisible(true);
                
                            if(symbolLoadDialog.getSelectedModel()==null){
                              return;
                            }
                
                            loadSymbols((SymbolContainer)symbolLoadDialog.getSelectedModel());
                
                            symbolLoadDialog.dispose();
                            symbolLoadDialog=null;
                            setButtonGroup(Mode.COMPONENT_MODE);
                
                            //position on center
                            //Rectangle r=symbolComponent.getModel().getUnit().getBoundingRect();
                            //symbolComponent.setScrollPosition((int)r.getCenterX(),(int)r.getCenterY());

            }
            if (symbolComponent.getModel().getUnit() == null) {
                return;
            }
            if (e.getActionCommand().equals("Save")) {  
//                if(Configuration.get().isIsOnline()&&User.get().isAnonymous()){
//                   User.showMessageDialog(symbolComponent.getDialogFrame().getParentFrame(),"Anonymous access denied."); 
//                   return;
//                }
//                (new SymbolSaveDialog(this.getParentFrame(), symbolComponent,Configuration.get().isIsOnline())).build();
            } 
        if (e.getSource()==ScaleIn) {
            symbolComponent.zoomIn(new Point((int)symbolComponent.getVisibleRect().getCenterX(),
                                                (int)symbolComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==ScaleOut) {
            symbolComponent.zoomOut(new Point((int)symbolComponent.getVisibleRect().getCenterX(),
                                                 (int)symbolComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==RotateLeft || e.getSource()==RotateRight) {        
            Collection<Shape> shapes= symbolComponent.getModel().getUnit().getShapes();
            if(shapes.size()==0){
               return; 
            }   
            //***notify undo manager                    
            symbolComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
            com.mynetpcb.d2.shapes.Box r=symbolComponent.getModel().getUnit().getShapesRect(shapes);  
            
            SymbolMgr.getInstance().rotateBlock(shapes,
                                   ((e.getSource()==RotateLeft?
                                                                      1 :
                                                                      -1) *90),
                                                                     r.getCenter()); 
            SymbolMgr.getInstance().alignBlock(symbolComponent.getModel().getUnit().getGrid(),shapes);                     

            //***notify undo manager
            symbolComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
            symbolComponent.Repaint();
        }        
        if (e.getSource()==RectButton){
                symbolComponent.setMode(Mode.RECT_MODE);
        }
            if (e.getSource()==EllipseButton) {
                symbolComponent.setMode(Mode.ELLIPSE_MODE);
            }
            if (e.getSource()==ArcButton){
                symbolComponent.setMode(Mode.ARC_MODE);
            }
            if (e.getSource()==LineButton) {
                symbolComponent.setMode(Mode.LINE_MODE);
            }
            if (e.getSource()==ArrowButton){
                symbolComponent.setMode(Mode.ARROW_MODE);
            }
            if (e.getSource()==TriangleButton) {
                symbolComponent.setMode(Mode.TRIANGLE_MODE);
            }
            if (e.getSource() == LabelButton) {
                symbolComponent.setMode(Mode.LABEL_MODE);
            }
            if (e.getActionCommand().equals("Print")) {
                PrintContext printContext=new PrintContext();
                printContext.setIsMirrored(false);
                printContext.setLayermaskId(Layer.LAYER_ALL);
                printContext.setTag("symbols");
                symbolComponent.print(printContext);
            }
            if (e.getSource()==DragHeand) {
                symbolComponent.setMode(Mode.DRAGHEAND_MODE);
            }
            if (e.getSource()==PinButton){
                symbolComponent.setMode(Mode.PIN_MODE);
            }
            if(e.getSource()==SelectionButton){
                symbolComponent.setMode(Mode.COMPONENT_MODE);          
            }
            if (e.getSource()==PositionToCenter) {      
                symbolComponent.setScrollPosition(symbolComponent.getModel().getUnit().getWidth()/2,symbolComponent.getModel().getUnit().getHeight()/2);
            }
            if (e.getSource()==SnapToGridButton) {
                symbolComponent.setParameter("snaptogrid", ((JToggleButton)e.getSource()).getModel().isSelected());
            }
            if (e.getSource()==CoordButton) {
                symbolComponent.setMode(Mode.ORIGIN_SHIFT_MODE);
            }
            if(e.getActionCommand().equals("assignfootprint")){            
                 //FootprintMgr.getInstance().assignPackage(this, symbolComponent.getModel().getUnit().getPackaging());
            }
        

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
    /**
     *Load symbol method
     * @param source to load or create new if null
     */
    private void loadSymbols(SymbolContainer source) {
        symbolComponent.clear();
        symbolComponent.setMode(Mode.COMPONENT_MODE);
        if(source==null){
            Symbol symbol =new Symbol(500,500);
            symbolComponent.getModel().add(symbol);
        }else{
        for (Symbol symbol : source.getUnits()) {
            try {
                Symbol copy = symbol.clone();
                copy.getScalableTransformation().reset(1.2, 2, 0, ScalableTransformation.DEFAULT_MAX_SCALE_FACTOR);
                symbolComponent.getModel().add(copy);
                copy.notifyListeners(ShapeEvent.ADD_SHAPE);
            } catch (CloneNotSupportedException f) {
                f.printStackTrace(System.out);
            }
        }
        }
        
        symbolComponent.getModel().setLibraryName(source!=null?source.getLibraryName():"");
        symbolComponent.getModel().setCategoryName(source!=null?source.getCategoryName():"");
        symbolComponent.getModel().setFileName(source!=null?source.getFileName():"Symbols");
        symbolComponent.getModel().setDesignerName(source!=null?source.getDesignerName():"");
        symbolComponent.getModel().setActiveUnit(0);
        symbolComponent.componentResized(null);
        symbolComponent.getModel().getUnit().setSelected(false);
        symbolComponent.fireContainerEvent(new ContainerEvent(null, ContainerEvent.RENAME_CONTAINER));
        symbolComponent.getModel().fireUnitEvent(new UnitEvent(symbolComponent.getModel().getUnit(),
                                                                  UnitEvent.SELECT_UNIT));
        //position all to symbol center
        for(Unit unit:symbolComponent.getModel().getUnits()){
            com.mynetpcb.d2.shapes.Box r=unit.getBoundingRect();
            com.mynetpcb.d2.shapes.Point pt=r.min.clone();
            pt.scale(unit.getScalableTransformation().getCurrentTransformation().getScaleX());            
            unit.setScrollPositionValue((int)pt.x,(int)pt.y);            
        }
        //position to symbol center
        com.mynetpcb.d2.shapes.Box r=symbolComponent.getModel().getUnit().getBoundingRect();
        symbolComponent.setScrollPosition((int)r.getCenter().x,(int)r.getCenter().y);

        //remember state
        symbolComponent.getModel().registerInitialState();
        symbolComponent.Repaint();
    }    
    
    @Override
    public boolean exit(){
        if(symbolComponent.getModel().isChanged()){
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "There is a changed element.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {
                return false;
            }
        }
        symbolComponent.release();
        this.dispose();
        return true;
    }    
}
