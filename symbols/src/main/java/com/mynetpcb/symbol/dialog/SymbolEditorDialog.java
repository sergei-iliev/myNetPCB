package com.mynetpcb.symbol.dialog;

import com.mynetpcb.core.capi.DialogFrame;
import com.mynetpcb.core.capi.ScalableTransformation;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.shape.CoordinateSystem;
import com.mynetpcb.core.capi.shape.Mode;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.CompositeMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.unit.FootprintMgr;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.dialog.panel.SymbolsPanel;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
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

public class SymbolEditorDialog extends JDialog implements DialogFrame,ActionListener{
    
    protected SymbolComponent symbolComponent;
    private SymbolsPanel symbolsPanel;
    
    private JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
    private JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL);
    private  GridBagLayout gridBagLayout=new GridBagLayout();    
    private JPanel moduleBasePanel=new JPanel(gridBagLayout);
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    
    
    private JPanel NorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel WestPanel = new JPanel();
    private JPanel EastPanel = new JPanel();
    private JPanel SouthPanel = new JPanel();
    private JPanel leftButtonGroupPanel = new JPanel();

    private JToggleButton RectButton = new JToggleButton();
    private JToggleButton EllipseButton = new JToggleButton();
    private JToggleButton SelectionButton = new JToggleButton();
    private JToggleButton ArcButton = new JToggleButton();
    private JToggleButton LineButton = new JToggleButton();
    private JToggleButton ArrowButton = new JToggleButton();
    private JToggleButton TriangleButton = new JToggleButton();
    private JToggleButton PinButton = new JToggleButton();
    private JToggleButton LabelButton = new JToggleButton();
    private JToggleButton SnapToGridButton = new JToggleButton();
    private JToggleButton CoordButton = new JToggleButton();
    
    private ButtonGroup group = new ButtonGroup();
    
    protected JButton SaveButton = new JButton();
    private JButton ScaleIn = new JButton();
    private JButton ScaleOut = new JButton();
    private JButton RotateLeft=new JButton();
    private JButton RotateRight=new JButton();  
    private JToggleButton DragHeand = new JToggleButton();
    private JButton PositionToCenter = new JButton();
    private JButton AssignPackage = new JButton();
    
    private JPanel basePanel;
    
    public SymbolEditorDialog(Window window, String caption) {
       this(window,caption,null);
    }
    public SymbolEditorDialog(Window window, String caption,SymbolContainer symbolContainer) {
        super(window, caption, Dialog.ModalityType.DOCUMENT_MODAL);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setResizable(true);
        Init();
        //set size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension((int)(2*screenSize.getWidth()/3),(int)(2*screenSize.getHeight()/3)));
        LoadSymbols(symbolContainer);
    }
    
    private void Init() {
        Container content = this.getContentPane();
        this.setPreferredSize(new Dimension(750, 600));  
        basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        
        //***set module component        
        symbolComponent=new SymbolComponent(this);
        symbolsPanel=new SymbolsPanel(symbolComponent);
        
        symbolComponent.setPreferredSize(new Dimension(700,500));
        symbolComponent.addContainerListener(symbolsPanel);
        symbolComponent.getModel().addUnitListener(symbolsPanel);
        symbolComponent.getModel().addShapeListener(symbolsPanel);
        
        SymbolComponent.getUnitKeyboardListener().setComponent(symbolComponent); 
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=1;
        gridBagConstraints.weighty=1;
        moduleBasePanel.add(symbolComponent, gridBagConstraints);
        
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

        //***add action listeners
        SelectionButton.addActionListener(this);
        SelectionButton.setIcon(Utilities.loadImageIcon(this, 
                                                      "images/selection.png"));
        SelectionButton.setSelected(true);
        SelectionButton.setToolTipText("Select Symbol");
        SelectionButton.setPreferredSize(new Dimension(35, 35));



        RectButton.addActionListener(this);
        RectButton.setIcon(Utilities.loadImageIcon(this, "images/rect.png"));
        RectButton.setToolTipText("Draw Rectangle");
        RectButton.setPreferredSize(new Dimension(35, 35));        
        
        EllipseButton.addActionListener(this);
        EllipseButton.setToolTipText("Draw Ellipse");
        EllipseButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "images/ellipse.png"));
        EllipseButton.setPreferredSize(new Dimension(35, 35));
         
        ArcButton.addActionListener(this);
        ArcButton.setToolTipText("Draw Arc");
        ArcButton.setIcon(Utilities.loadImageIcon(this, "images/arc.png"));
        ArcButton.setPreferredSize(new Dimension(35, 35));

        LineButton.addActionListener(this);
        LineButton.setToolTipText("Draw Line");
        LineButton.setIcon(Utilities.loadImageIcon(this, "images/line.png"));
        LineButton.setPreferredSize(new Dimension(35, 35));
       
        
        ArrowButton.addActionListener(this);
        ArrowButton.setToolTipText("Draw ArrowLine");
        ArrowButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "images/arrowline.png"));
        ArrowButton.setPreferredSize(new Dimension(35, 35));
        
        TriangleButton.addActionListener(this);
        TriangleButton.setToolTipText("Draw Triangle");
        TriangleButton.setIcon(Utilities.loadImageIcon(this, 
                                                       "images/triangle.png"));
        TriangleButton.setPreferredSize(new Dimension(35, 35));
        
        PinButton.addActionListener(this);
        PinButton.setToolTipText("Add Pin");
        PinButton.setIcon(Utilities.loadImageIcon(this, "images/pin.png"));
        PinButton.setPreferredSize(new Dimension(35, 35));

        LabelButton.addActionListener(this);
        LabelButton.setIcon(Utilities.loadImageIcon(this,"images/label.png"));
        LabelButton.setToolTipText("Add Label");
        LabelButton.setPreferredSize(new Dimension(35, 35));
    
        SnapToGridButton.addActionListener(this);
        SnapToGridButton.setIcon(Utilities.loadImageIcon(this,"images/anchor.png"));
        SnapToGridButton.setToolTipText("Snap dragging point to grid");
        SnapToGridButton.setPreferredSize(new Dimension(35, 35));

        CoordButton.addActionListener(this);
        CoordButton.setToolTipText("Change coordinate origin");
        CoordButton.setIcon(Utilities.loadImageIcon(this, "images/origin.png"));
        CoordButton.setPreferredSize(new Dimension(35, 35));
        
        SaveButton.addActionListener(this);
        SaveButton.setToolTipText("Save Module");
        SaveButton.setPreferredSize(new Dimension(35, 35));
        SaveButton.setIcon(Utilities.loadImageIcon(this, "images/save.png"));
        

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
        DragHeand.setToolTipText("Drag canvas");
        DragHeand.addActionListener(this);
        DragHeand.setActionCommand("dragheand");
        DragHeand.setIcon(Utilities.loadImageIcon(this, "images/grab.png"));
        
        PositionToCenter.setPreferredSize(new Dimension(35, 35));
        PositionToCenter.setToolTipText("Position viewport to center");
        PositionToCenter.addActionListener(this);
        PositionToCenter.setIcon(Utilities.loadImageIcon(this, "images/tocenter.png"));
        
        AssignPackage.setPreferredSize(new Dimension(35, 35));
        AssignPackage.setToolTipText("Assign default package");
        AssignPackage.addActionListener(this);
        AssignPackage.setActionCommand("assignfootprint");
        AssignPackage.setIcon(Utilities.loadImageIcon(this, "images/footprint.png"));
   
        NorthPanel.add(SaveButton);
        NorthPanel.add(ScaleIn);
        NorthPanel.add(ScaleOut);
        NorthPanel.add(RotateLeft);
        NorthPanel.add(RotateRight); 
        NorthPanel.add(DragHeand);
        NorthPanel.add(PositionToCenter);
        //NorthPanel.add(AssignPackage);
        
        EastPanel.setLayout(new BorderLayout());
        EastPanel.setPreferredSize(new Dimension(220, 200));

        //***Add buttons to group
        group.add(SelectionButton);
        group.add(RectButton);
        group.add(EllipseButton);
        group.add(ArcButton);
        group.add(LineButton);
        group.add(ArrowButton);
        group.add(TriangleButton);
        group.add(PinButton);
        group.add(LabelButton);        
        group.add(DragHeand);
        
        WestPanel.setLayout(new BorderLayout());
        basePanel.add(NorthPanel, BorderLayout.NORTH);
        EastPanel.add(symbolsPanel, BorderLayout.CENTER);

        basePanel.add(EastPanel, BorderLayout.EAST);
        basePanel.add(SouthPanel, BorderLayout.SOUTH);

        leftButtonGroupPanel.setLayout(new BoxLayout(leftButtonGroupPanel, BoxLayout.Y_AXIS));
        leftButtonGroupPanel.setBorder(BorderFactory.createEmptyBorder(35, 4, 0, 4));
        leftButtonGroupPanel.add(SelectionButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(RectButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(EllipseButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(ArcButton);
        leftButtonGroupPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        leftButtonGroupPanel.add(LineButton);
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

        if (symbolComponent.getModel().getUnit() == null) {
            return;
        }
        if (e.getSource() ==ScaleIn) {
            symbolComponent.zoomIn(new Point((int)symbolComponent.getVisibleRect().getCenterX(),
                                                (int)symbolComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==ScaleOut) {
            symbolComponent.zoomOut(new Point((int)symbolComponent.getVisibleRect().getCenterX(),
                                                 (int)symbolComponent.getVisibleRect().getCenterY()));
        }
        if (e.getSource()==RotateLeft|| e.getSource()==RotateRight) {        
            
            Collection<Shape> shapes= symbolComponent.getModel().getUnit().getShapes();
            if(shapes.size()==0){
               return; 
            }   
            //***notify undo manager                    
            symbolComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));
            com.mynetpcb.d2.shapes.Box r=symbolComponent.getModel().getUnit().getShapesRect(shapes);  
            
            FootprintMgr.getInstance().rotateBlock(shapes,
                                   ((e.getSource()==RotateLeft?
                                                                      1 :
                                                                      -1) *90),
                                                                     r.getCenter()); 
            FootprintMgr.getInstance().alignBlock(symbolComponent.getModel().getUnit().getGrid(),shapes);                     

            //***notify undo manager
            symbolComponent.getModel().getUnit().registerMemento(shapes.size()>1?new CompositeMemento(MementoType.MOVE_MEMENTO).add(shapes):shapes.iterator().next().getState(MementoType.MOVE_MEMENTO));                    
            symbolComponent.Repaint();            
        }
        if (e.getSource()==RectButton) {
            symbolComponent.setMode(Mode.RECT_MODE);
        }
        if (e.getSource()==EllipseButton) {
            symbolComponent.setMode(Mode.ELLIPSE_MODE);
        }
        if (e.getSource()==ArcButton) {
            symbolComponent.setMode(Mode.ARC_MODE);
        }
        if (e.getSource()==LineButton) {
            symbolComponent.setMode(Mode.LINE_MODE);
        }
        if (e.getSource()==ArrowButton) {
            symbolComponent.setMode(Mode.ARROW_MODE);
        }
        if (e.getSource()==TriangleButton) {
            symbolComponent.setMode(Mode.TRIANGLE_MODE);
        }
        if (e.getSource()==LabelButton) {
            symbolComponent.setMode(Mode.LABEL_MODE);
        }
        if (e.getSource()==DragHeand) {
            symbolComponent.setMode(Mode.DRAGHEAND_MODE);
        }
        if (e.getSource()==PinButton) {
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
            if(CoordButton.getModel().isSelected()){
                symbolComponent.getModel().getUnit().setCoordinateSystem(new CoordinateSystem(symbolComponent.getModel().getUnit(),2));
                symbolComponent.setMode(Mode.ORIGIN_SHIFT_MODE);
            }else{
                symbolComponent.getModel().getUnit().deleteCoordinateSystem(); 
                symbolComponent.setMode(Mode.COMPONENT_MODE); 
            }
        }
        //if(e.getActionCommand().equals("assignfootprint")){            
             //FootprintMgr.getInstance().assignPackage(this, symbolComponent.getModel().getUnit().getPackaging());
        //}
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
    private void LoadSymbols(SymbolContainer source) {
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
        //symbolComponent.fireContainerEvent(new ContainerEvent(null, Mode.RENAME_CONTAINER));
        symbolComponent.getModel().fireUnitEvent(new UnitEvent(symbolComponent.getModel().getUnit(),
                                                                  UnitEvent.SELECT_UNIT));
        //position all to symbol center
        for(Unit unit:symbolComponent.getModel().getUnits()){
            com.mynetpcb.d2.shapes.Box r=unit.getBoundingRect();
            com.mynetpcb.d2.shapes.Point pt=r.min.clone();
            pt.scale(unit.getScalableTransformation().getCurrentTransformation().getScaleX());            
            unit.setScrollPositionValue((int)pt.x,(int)pt.y);            
        }
        
        com.mynetpcb.d2.shapes.Box r=symbolComponent.getModel().getUnit().getBoundingRect();
        symbolComponent.setScrollPosition((int)r.getCenter().x,(int)r.getCenter().y);
        
        symbolComponent.getModel().registerInitialState();
        symbolComponent.Repaint();
    }
    private void exit(){
        if(symbolComponent.getModel().isChanged()){                        
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(SymbolEditorDialog.this, "There is a changed element.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {                                                                                              
                return;
            }                      
        }
        symbolComponent.release();  
        SymbolEditorDialog.this.dispose(); 
    }

}
