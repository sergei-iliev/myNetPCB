package com.mynetpcb.ui;

import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.container.BoardContainerFactory;
import com.mynetpcb.circuit.container.CircuitContainer;
import com.mynetpcb.circuit.container.CircuitContainerFactory;
import com.mynetpcb.core.capi.clipboard.ClipboardMgr;
import com.mynetpcb.core.capi.clipboard.Clipboardable;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.container.UnitContainerProducer;
import com.mynetpcb.core.capi.gui.filter.ImpexFileFilter;
import com.mynetpcb.core.capi.gui.panel.DisabledGlassPane;
import com.mynetpcb.core.capi.impex.ClipboardImportTask;
import com.mynetpcb.core.capi.impex.XMLImportTask;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.FutureCommand;
import com.mynetpcb.core.capi.popup.JPopupButton;
import com.mynetpcb.core.dialog.config.PreferencesDialog;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.core.utils.VersionUtils;
import com.mynetpcb.pad.container.FootprintContainer;
import com.mynetpcb.pad.container.FootprintContainerFactory;
import com.mynetpcb.symbol.container.SymbolContainer;
import com.mynetpcb.symbol.container.SymbolContainerFactory;
import com.mynetpcb.ui.board.BoardInternalFrame;
import com.mynetpcb.ui.circuit.CircuitInternalFrame;
import com.mynetpcb.ui.footprint.FootprintInternalFrame;
import com.mynetpcb.ui.myNetPCB.MainFrameListener;
import com.mynetpcb.ui.symbol.SymbolInternalFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class MainPanel extends JPanel implements InternalFrameListener,MainFrameListener,CommandListener, ActionListener{
    
    private final JDesktopPane desktop;
    private JButton symbolButton,footprintButton,boardButton, circuitButton;
    private AbstractInternalFrame selectedFrame;
    private final JFrame parent;
    
    public MainPanel(JFrame parent,JDesktopPane desktop) {
        this.desktop=desktop;       
        this.parent=parent;
        setLayout(new GridBagLayout());      
        init();
    }
    private void init(){
        GridBagConstraints c = new GridBagConstraints();
         
        /*MENU*/
        JPanel menu=createMenu(); 
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        this.add(menu, c); 
        
        /*HEADER*/
        JPanel header=createHeader();
        
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 40;      //make this component tall
        c.weightx = 1.0;
        c.weighty=3;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        this.add(header, c);
         
        /*BODY*/
        JPanel body=createBody();         
  
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty=1;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 2;
        this.add(body, c);
         
        /*FOOTER*/
        JPanel footer=createFooter(); 
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 40;      //make this component tall
        c.weightx = 0.0;
        c.weighty=1;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 3;
        this.add(footer, c);        
    }
    private JPanel createMenu(){
        JPanel  panel=new JPanel(new GridLayout(1, 1));        
        
        JPanel symbolsPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        symbolsPanel.setBorder(new EmptyBorder(0,0,0,0));
        symbolsPanel.setBackground(Color.white); 
            
        JPopupButton menuButton=new JPopupButton(this);
        menuButton.setBackground(Color.white);
        menuButton.setPreferredSize(new Dimension(44,44));
        menuButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/navbar.png"));
        
        menuButton.addMenu("Preferences","preferences").addSeparator().addRootMenu("Import","import").addSubMenu("import","Clipboard" , "import.clipboard").addSubMenu("import","XML" , "import.xml").addMenu("Exit","exit"); 
        
        symbolsPanel.add(menuButton);
                    
        panel.add(symbolsPanel);
        
        return panel;
    }
    private JPanel createFooter(){
        JPanel  forthRowPanel=new JPanel(new BorderLayout());
        forthRowPanel.setBackground(Color.WHITE);        
        JPanel versionPanel = new JPanel();
        versionPanel.setBackground(Color.WHITE);
        versionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,10,10));
        JLabel label=new JLabel("Version: "+VersionUtils.MYNETPCB_VERSION);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD+Font.ITALIC,16));
        versionPanel.add(label);
        forthRowPanel.add(versionPanel, BorderLayout.PAGE_END);
        
        return forthRowPanel;
    }
    private JPanel createBody(){
        JPanel  thirdRowPanel=new JPanel(new GridLayout(1,4));
        JPanel symbolsPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        symbolsPanel.setBorder(new EmptyBorder(0,0,0,0));
        symbolsPanel.setBackground(Color.white);  
        
        symbolButton=new JButton();
        symbolButton.addActionListener(this);
        symbolButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/symbol_icon.png"));
        symbolButton.setBackground(Color.white);
        symbolButton.setPreferredSize(new Dimension(130,130));
        symbolsPanel.add(symbolButton);
        thirdRowPanel.add(symbolsPanel);
        
        JPanel padsPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        padsPanel.setBorder(new EmptyBorder(0,0,0,0));
        padsPanel.setBackground(Color.white);        
        
        footprintButton=new JButton();
        footprintButton.addActionListener(this);
        footprintButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/footprint_icon.png"));
        footprintButton.setBackground(Color.white);
        footprintButton.setPreferredSize(new Dimension(130,130));
        padsPanel.add(footprintButton);  
        
        thirdRowPanel.add(padsPanel);      
        
        JPanel circuitPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        circuitPanel.setBorder(new EmptyBorder(0,0,0,0));
        circuitPanel.setBackground(Color.white);        
        
        circuitButton=new JButton();
        circuitButton.addActionListener(this);
        circuitButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/circuit_icon.png"));
        circuitButton.setBackground(Color.white);
        circuitButton.setPreferredSize(new Dimension(130,130));
        circuitPanel.add(circuitButton);        
        thirdRowPanel.add(circuitPanel);  

        JPanel boardPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        boardPanel.setBorder(new EmptyBorder(0,0,0,0));
        boardPanel.setBackground(Color.white);        
        
        boardButton=new JButton();
        boardButton.addActionListener(this);
        boardButton.setIcon(Utilities.loadImageIcon(this, 
                                                    "/com/mynetpcb/core/images/board_icon.png"));
        boardButton.setBackground(Color.white);
        boardButton.setPreferredSize(new Dimension(143,130));
        boardPanel.add(boardButton);        
        thirdRowPanel.add(boardPanel); 
        return thirdRowPanel;
    }
    private JPanel createHeader(){
        JPanel  firstRowPanel=new JPanel(new BorderLayout());
        firstRowPanel.setBackground(Color.white);
        
        JPanel panel=new JPanel();
        panel.setBackground(Color.WHITE);
        firstRowPanel.add(panel,BorderLayout.WEST);
        
        JPanel background=new JPanel();
     
        background.setBorder(new EmptyBorder(90, 90, 90, 90));
        background.setBackground(Color.white);
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
  
        
        JLabel title=new JLabel("Create Design Innovate");
        title.setFont(title.getFont().deriveFont(50.0f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        background.add(title,BorderLayout.CENTER);
        
        title=new JLabel("Free and open-source schematic capture and pcb design tool");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(15.0f));
        background.add(title,BorderLayout.CENTER);
        
        
        firstRowPanel.add(background,BorderLayout.CENTER);
        
        panel=new JPanel();
        panel.setBackground(Color.white);
        firstRowPanel.add(panel,BorderLayout.EAST); 
        return firstRowPanel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("exit")) {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new WindowEvent(parent,
                                                                                        WindowEvent.WINDOW_CLOSING));
        }
        if (event.getActionCommand().equals("preferences")) {
            PreferencesDialog d = new PreferencesDialog(parent, "Preferences");
            d.pack();
            d.setLocationRelativeTo(null); //centers on screen
            d.setFocusable(true);
            d.setVisible(true);
            return;
        }
        if(event.getActionCommand().equals("import.xml")){
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
        if(event.getActionCommand().equals("import.clipboard")){
            if(ClipboardMgr.getInstance().isTransferDataAvailable(Clipboardable.Clipboard.SYSTEM)){  
                try{ 
                String content=(String)ClipboardMgr.getInstance().getClipboardContent(Clipboardable.Clipboard.SYSTEM).getTransferData(DataFlavor.stringFlavor);
                    
                    UnitContainerProducer unitContainerProducer=new UnitContainerProducer().withFactory("circuits", new CircuitContainerFactory()).withFactory("modules", new SymbolContainerFactory()).
                         withFactory("footprints", new FootprintContainerFactory()).withFactory("boards", new BoardContainerFactory());
                    
                    
                    CommandExecutor.INSTANCE.addTask("import",
                                                     new ClipboardImportTask(this,
                                                                       unitContainerProducer,
                                                                       content, ClipboardImportTask.class));
                
                }catch(IOException | UnsupportedFlavorException e){
                    e.printStackTrace();
                     
                }
            }
            
        }
        if(event.getSource()==footprintButton){
            this.openInternalFrame(new FootprintInternalFrame());                      
        }
        if(event.getSource()==boardButton){
            this.openInternalFrame(new BoardInternalFrame());
        }   
        if(event.getSource()==symbolButton){
            this.openInternalFrame(new SymbolInternalFrame());
        }
        if(event.getSource()==circuitButton){
            this.openInternalFrame(new CircuitInternalFrame());
        } 
    }
    
    @Override
    public void internalFrameOpened(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent internalFrameEvent) {
        selectedFrame=null;
        desktop.removeAll();
        desktop.add(this);
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
        selectedFrame=null;        
        desktop.removeAll();
        desktop.add(this);
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {

    }

    @Override
    public void internalFrameActivated(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent internalFrameEvent) {
        // TODO Implement this method
    }

    @Override
    public void onMainFrameClose() {
            if(selectedFrame!=null&&selectedFrame.isChanged()){                        
                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this, "There is a changed circuit.Do you want to close?", "Close", JOptionPane.YES_NO_OPTION)) {                                                           
                    System.exit(0);
                }                      
            }else{  
                   System.exit(0);
            }   
    }

    @Override
    public void onStart(Class<?> clazz) {
        DisabledGlassPane.block(this.getRootPane(), "Importing..."); 
    }

    @Override
    public void onRecive(String content, Class<?> clazz) {
        // TODO Implement this method

    }

    @Override
    public void onFinish(Class<?> clazz) {
        DisabledGlassPane.unblock(this.getRootPane()); 
        FutureCommand task = CommandExecutor.INSTANCE.getTaskByName("import");
        UnitContainer unitContainer = null;
        try {
            unitContainer = (UnitContainer) task.get();
        } catch (ExecutionException | InterruptedException e) {        
            e.printStackTrace(System.out);
        }        
        if(unitContainer instanceof SymbolContainer){
            this.openInternalFrame(new SymbolInternalFrame((SymbolContainer)unitContainer));
        }else if(unitContainer instanceof FootprintContainer){
            this.openInternalFrame(new FootprintInternalFrame((FootprintContainer)unitContainer));
        }else if(unitContainer instanceof CircuitContainer){
            this.openInternalFrame(new CircuitInternalFrame((CircuitContainer)unitContainer));
        }else if(unitContainer instanceof BoardContainer){
            this.openInternalFrame(new BoardInternalFrame((BoardContainer)unitContainer));
        }
    }

    @Override
    public void onError(String error) {
        DisabledGlassPane.unblock(this.getRootPane()); 
        JOptionPane.showMessageDialog(this.getParent(), error, "Error",
                                      JOptionPane.ERROR_MESSAGE); 
    }
    
    private void openInternalFrame(AbstractInternalFrame selectedFrame){
        this.selectedFrame=selectedFrame;
        selectedFrame.setVisible(true); //necessary as of 1.3            
        desktop.removeAll();
        desktop.add(selectedFrame);
        selectedFrame.addInternalFrameListener(this);   
    }
}
