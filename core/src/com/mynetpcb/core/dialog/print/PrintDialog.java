package com.mynetpcb.core.dialog.print;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.gui.filter.ImpexFileFilter;
import com.mynetpcb.core.capi.print.PrintContext;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.ref.WeakReference;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalIconFactory;

public abstract class PrintDialog extends JDialog implements ActionListener {
    protected final WeakReference<UnitComponent> unitComponent;
    protected JRadioButton colorrb, bwrb;
    protected JButton printButton,selectFileFolderButton;
    protected JComboBox sizeCB;
    protected JTextField  targetFile;
    
    public PrintDialog(Window owner, UnitComponent unitComponent,String caption) {
        super(owner,caption);
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.unitComponent = new WeakReference<>(unitComponent);
        this.setSize(460, 200);        
        Init();        
    }
    
    protected abstract JPanel initDialogContent();

    
    private void Init() {
        Container content = this.getContentPane();
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());


        basePanel.add(initDialogContent(), BorderLayout.CENTER);


        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(getWidth(), 40));
        printButton = new JButton("Print");
        printButton.setActionCommand("PRINT");
        printButton.addActionListener(this);
        JButton CancelButton = new JButton("Cancel");
        CancelButton.setActionCommand("CANCEL");
        CancelButton.addActionListener(this);
        panel.add(printButton);
        panel.add(CancelButton);
        this.getContentPane().add(panel, BorderLayout.SOUTH);

        content.add(basePanel); // Add components to the content
    }



    protected JPanel createColorPanel() {
        JPanel sidegroup = new JPanel();
        sidegroup.setLayout(new BoxLayout(sidegroup, BoxLayout.Y_AXIS));
        Border lowerEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        TitledBorder title = BorderFactory.createTitledBorder(lowerEtched, "Color");
        sidegroup.setBorder(title);

        ButtonGroup bgroup = new ButtonGroup();
        colorrb = new JRadioButton("Color");
        bwrb = new JRadioButton("Black and White");
        bwrb.setSelected(true);
        bgroup.add(colorrb);
        bgroup.add(bwrb);

        sidegroup.add(colorrb);
        sidegroup.add(bwrb);
        
        sidegroup.setAlignmentY(JPanel.TOP_ALIGNMENT);
        return sidegroup;
    }
    
    protected PrintContext createContext(){
        PrintContext context=new PrintContext();
        context.setIsBlackAndWhite(bwrb.isSelected()); 
        return context;
    }
    protected JPanel createScalePanel(){
        JPanel first = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Scale");
        first.add(label);
        sizeCB=new JComboBox();        
        first.add(sizeCB);  
        return first;
    }
    
    protected JPanel createFileSelectPanel(String name,int chooseType){
        JPanel second = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(name);
        second.add(label);

        targetFile = new JTextField("",20);
        second.add(targetFile);

        selectFileFolderButton = new JButton();
        selectFileFolderButton.setActionCommand(String.valueOf(chooseType));
        selectFileFolderButton.addActionListener(this);
        selectFileFolderButton.setIcon(MetalIconFactory.getFileChooserNewFolderIcon());
        selectFileFolderButton.setPreferredSize(new Dimension(20, 20));
        second.add(selectFileFolderButton);
        return second;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==selectFileFolderButton){      
            
                        JFileChooser fc = new JFileChooser(targetFile.getText());
                        fc.setFileSelectionMode(Integer.parseInt(e.getActionCommand()) );
                        if(JFileChooser.FILES_ONLY==Integer.parseInt(e.getActionCommand())){
                         fc.setDialogTitle("Select File");
                         fc.setAcceptAllFileFilterUsed(false);
                         fc.addChoosableFileFilter(new ImpexFileFilter(".jpg"));
                         fc.addChoosableFileFilter(new ImpexFileFilter(".png"));                      
                         fc.addChoosableFileFilter(new ImpexFileFilter(".gif"));
                        }else{
                          fc.setDialogTitle("Select Folder");   
                        }
                    
                        if (fc.showDialog(this,"Select") == JFileChooser.APPROVE_OPTION) {
                            String extension=fc.getFileFilter().getDescription();
                            if(JFileChooser.FILES_ONLY==Integer.parseInt(e.getActionCommand())){         
                            if(fc.getSelectedFile().getAbsolutePath().toLowerCase().endsWith(extension.toLowerCase()))                                           {
                                targetFile.setText(fc.getSelectedFile().getAbsolutePath());                  
                            }else{                            
                                targetFile.setText(fc.getSelectedFile().getAbsolutePath()+extension);                                              
                            }
                            }else{
                                targetFile.setText(fc.getSelectedFile().getAbsolutePath());                  
                            }
                            
                        }   
                        return; 
        }
        if (e.getActionCommand().equals("PRINT")) {
            
            unitComponent.get().print(createContext());
        }

        if (e.getActionCommand().equals("CANCEL")) {
            this.close();
        }

    }
    
    protected void close(){
        unitComponent.clear();
        this.dispose();
    }

}
