package com.mynetpcb.board.dialog;

import com.mynetpcb.board.container.BoardContainer;
import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.capi.config.Configuration;
import com.mynetpcb.core.capi.container.UnitContainer;
import com.mynetpcb.core.capi.gui.UnitSelectionPanel;
import com.mynetpcb.core.capi.io.Command;
import com.mynetpcb.core.capi.io.CommandExecutor;
import com.mynetpcb.core.capi.io.CommandListener;
import com.mynetpcb.core.capi.io.ReadRepositoryLocal;
import com.mynetpcb.core.capi.io.ReadUnitLocal;
import com.mynetpcb.core.capi.io.ReadUnitsLocal;
import com.mynetpcb.core.capi.io.remote.ReadConnector;
import com.mynetpcb.core.capi.io.remote.rest.RestParameterMap;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.core.capi.tree.IconListCellRenderer;
import com.mynetpcb.core.dialog.load.AbstractLoadDialog;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class BoardLoadDialog extends AbstractLoadDialog  implements CommandListener,ActionListener,ListSelectionListener{
    private JPanel basePanel = new JPanel();
    private BorderLayout borderLayout1 = new BorderLayout();
    private JPanel leftPanel = new JPanel();
    private BorderLayout borderLayout2 = new BorderLayout();
    private JComboBox projectCombo = new JComboBox();
    private DefaultListModel model = new DefaultListModel();
    private JList boardList = new JList(model);
    private JScrollPane boardListScroll=new JScrollPane();
    private FlowLayout flow=new FlowLayout();
    private JPanel bottomPanel = new JPanel(flow);
    private JScrollPane scrollViewer=new JScrollPane();  
    private JPanel CenterPanel = new JPanel();
    private JPanel TopPanel=new JPanel();
    
    private UnitSelectionPanel selectionPanel;

    
    private JButton LoadButton = new JButton();
    private JButton CloseButton = new JButton();   
    
    private BoardLoadDialog(Window window,String Caption,boolean enabled) {
        super(window,Caption,Dialog.ModalityType.DOCUMENT_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Init(enabled);    
    } 
    private void Init(boolean enabled){
        this.setResizable(false);  
        this.setPreferredSize(new Dimension(489, 343));
        basePanel.setLayout(borderLayout1);
        basePanel.setPreferredSize(new Dimension(400, 300));
        leftPanel.setPreferredSize(new Dimension(150, 10));
        leftPanel.setLayout(borderLayout2);
        bottomPanel.setPreferredSize(new Dimension(10, 40));
        leftPanel.add(projectCombo, BorderLayout.NORTH);
        leftPanel.add(boardListScroll, BorderLayout.CENTER);
        basePanel.add(leftPanel, BorderLayout.WEST);
        basePanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(LoadButton, null);
        bottomPanel.add(CloseButton, null);
        TopPanel.setPreferredSize(new Dimension(TopPanel.getWidth(), 10));
        LoadButton.setText(" Load ");
        CloseButton.setText("Close");
        basePanel.add(TopPanel, BorderLayout.NORTH);
        
        basePanel.add(TopPanel, BorderLayout.NORTH);
        selectionPanel=new UnitSelectionPanel();
        selectionPanel.setBackground(Color.BLACK);
        selectionPanel.setEnabled(enabled);
        selectionPanel.getSelectionGrid().setScaleRatio(0.5);
        selectionPanel.getSelectionGrid().setScaleFactor(11);
        selectionPanel.getSelectionGrid().setMinScaleFactor(4);
        selectionPanel.getSelectionGrid().setMaxScaleFactor(13);
        selectionPanel.setPreferredSize(new Dimension(400, 500));
        scrollViewer = new JScrollPane();
        scrollViewer.getViewport().add(selectionPanel);
        scrollViewer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollViewer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.add(scrollViewer, BorderLayout.CENTER);
        basePanel.add(CenterPanel, BorderLayout.CENTER);
        
        projectCombo.setRenderer(new IconListCellRenderer(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/library.png")));
        projectCombo.addActionListener(this); 
            
        //moduleList.setBackground(TopPanel.getBackground());
        boardList.addListSelectionListener(this); 
        //moduleList.setBorder(BorderFactory.createLineBorder(Color.black));
        boardListScroll.getViewport().add(boardList);
        flow.setHgap(80);
        LoadButton.addActionListener(this);
        CloseButton.addActionListener(this);
        
        this.getContentPane().add(basePanel, null);
        
        addWindowListener(new WindowAdapter() {
          public void windowOpened(WindowEvent e) {
              //***Read Libraries
              if(!Configuration.get().isIsApplet()){  
                   Command reader=new ReadRepositoryLocal(BoardLoadDialog.this, Configuration.get().getBoardsRoot(),BoardLoadDialog.this.projectCombo.getClass());
                   CommandExecutor.INSTANCE.addTask("ReadRepositoryLocal",reader);
              }else{         
                   Command reader=new ReadConnector(BoardLoadDialog.this,new RestParameterMap.ParameterBuilder("/boards").addURI("projects").build(),BoardLoadDialog.this.projectCombo.getClass());           
                   CommandExecutor.INSTANCE.addTask("ReadProjects",reader);                                           
              }
                  
          }
        });  
    }

    @Override
    public void dispose(){
      CommandExecutor.INSTANCE.cancel();
      this.selectionPanel.Release();
      System.gc();
      super.dispose();
    }
    
    @Override
    public void onStart(Class<?> reciever) {
        this.disableControls();   
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        if(reciever.getSimpleName().equals("DefaultListModel")){                      
              model.clear(); 
        }   
    }

    @Override
    public void onRecive(String result, Class reciever) {
        if(reciever.getSimpleName().equals("JComboBox")||reciever.getSimpleName().equals("DefaultListModel")){
            try {
                Document document = Utilities.buildDocument(result);
                
                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                XPathExpression expr;  
                
                expr = xpath.compile("//name");
                NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
                if (reciever.getSimpleName().equals("JComboBox")) {                
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node node=nodes.item(i);
                        projectCombo.addItem(node.getTextContent());                     
                    }    
                }else{
                    //clear selection
                    selectionPanel.Clear();
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Element element=(Element)nodes.item(i);
                          model.addElement(new AttachedItem.Builder(element.getTextContent()).setFileName(element.getAttribute("fullname")).build()) ;                              
                    }    
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace(System.out);
            } catch (SAXException e) {
                e.printStackTrace(System.out);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            } catch (XPathExpressionException e) {
                e.printStackTrace(System.out);
                }          
                               
        }
        //***module xml!      
        if(reciever.getSimpleName().equals("Board")){            
            selectionPanel.Clear();
            try{
            UnitContainer model= new BoardContainer();
            model.parse(result);
            selectionPanel.getSelectionGrid().setModel(model);
            } catch (Exception ioe) {
            ioe.printStackTrace(System.out);
            return;
            }
            selectionPanel.buildSelectionGrid();
            selectionPanel.repaint();
            selectionPanel.revalidate();         
        } 
    }

    @Override
    public void onFinish(Class<?> reciever) {
        this.setCursor(Cursor.getDefaultCursor());     
        this.enableControls();         
    }

    @Override
    public void onError(String error) {
        onFinish(null);
        JOptionPane.showMessageDialog(this, error, "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==projectCombo){
            if(!Configuration.get().isIsApplet()){  
              Command reader=new ReadUnitsLocal(this,Configuration.get().getBoardsRoot().resolve((String)projectCombo.getSelectedItem()),"boards",model.getClass());    
              CommandExecutor.INSTANCE.addTask("ReadUnitsLocal",reader);
            }else{
              Command reader=new ReadConnector(this,new RestParameterMap.ParameterBuilder("/boards").addURI("projects").addURI((String)projectCombo.getSelectedItem()).build(),model.getClass());
              CommandExecutor.INSTANCE.addTask("ReadBoards",reader);
            }
                
        }  
        if (e.getSource() == CloseButton) {
            this.dispose();
        }
        if (e.getSource() == LoadButton) {
            if(this.getSelectedModel()!=null)
               this.setVisible(false); 
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()&&boardList.getSelectedValue()!=null) {
            if(!Configuration.get().isIsApplet()){ 
                Command reader=new ReadUnitLocal(this, Configuration.get().getBoardsRoot(),(String)projectCombo.getSelectedItem(),null,((AttachedItem)boardList.getSelectedValue()).getFileName(),Board.class);         
                CommandExecutor.INSTANCE.addTask("ReadUnitLocal",reader);
            }else{
                Command reader=new ReadConnector(this,new RestParameterMap.ParameterBuilder("/boards").addURI("projects").addURI((String)projectCombo.getSelectedItem()).addURI(((AttachedItem)boardList.getSelectedValue()).getFileName()).build(),Board.class);
                CommandExecutor.INSTANCE.addTask("ReadBoard",reader);                
            }
        } 
    }
    
    private void enableControls() {
        LoadButton.setEnabled(true);
        boardList.setEnabled(true);
        projectCombo.setEnabled(true);
    }

    private void disableControls() {
        LoadButton.setEnabled(false);
        boardList.setEnabled(false);
        projectCombo.setEnabled(false);
    }
    
    public UnitContainer getSelectedModel() {
        return this.selectionPanel.getSelectionGrid().getModel();
    }
    
    public static class Builder extends AbstractLoadDialog.Builder{
        
        @Override
        public AbstractLoadDialog build() {
           return new BoardLoadDialog(window,caption,enabled);
        }
    }

}
