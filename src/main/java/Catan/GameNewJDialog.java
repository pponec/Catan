package Catan;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

enum PlyrSel {HUMAN(0), COMPUTER(1);
              private int value;
              private PlyrSel (int i) {value = i;}
              public int toValue () { return value; }
}

public class GameNewJDialog extends javax.swing.JDialog 
{
    boolean         startGame       = false;
    int             playerNo        = 4;
    CompSkillLevel  skillLevel      = CompSkillLevel.NORMAL;
    
    static BufferedImage plyrImgs[] = null;
   
    /** Creates new form GameNewJDialog */
    public GameNewJDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);       
        initComponents();                        
                
        // Load some images
        if (plyrImgs == null)
        {
            String files[] = {"/Catan/Resource/plyr_blue.png",  "/Catan/Resource/plyr_red.png",
                              "/Catan/Resource/plyr_white.png", "/Catan/Resource/plyr_orange.png",
                              "/Catan/Resource/plyr_green.png", "/Catan/Resource/plyr_yellow.png"
                             };
            plyrImgs = new BufferedImage[files.length];

            for (int idx = 0; idx < files.length; idx++)
            {
                try
                {
                    plyrImgs[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx]));

                }
                catch (Exception e)
                {
                    System.err.println ("Failed loading image : "  + files[idx] + " : " + e.toString());
                    System.exit(1);
                }
            }
        }                   
        
        this.drawBlueArea.setUI(new BasicLabelUI()
        {
            @Override
            public void paint(Graphics g, JComponent c)
            {
                super.paint(g, c);
                drawIcon (drawBlueArea, (Graphics2D) g, 0);
            }
        });
        
        this.drawRedArea.setUI(new BasicLabelUI()
        {
            @Override
            public void paint(Graphics g, JComponent c)
            {
                super.paint(g, c);
                drawIcon (drawRedArea, (Graphics2D) g, 1);
            }
        });        
        
        this.drawWhiteArea.setUI(new BasicLabelUI()
        {
            @Override
            public void paint(Graphics g, JComponent c)
            {
                super.paint(g, c);
                drawIcon (drawWhiteArea, (Graphics2D) g, 2);
            }
        });
        
        this.drawOrangeArea.setUI(new BasicLabelUI()
        {
            @Override
            public void paint(Graphics g, JComponent c)
            {
                super.paint(g, c);
                drawIcon (drawOrangeArea, (Graphics2D) g, 3);
            }
        });
        
        this.drawGreenArea.setUI(new BasicLabelUI()
        {
            @Override
            public void paint(Graphics g, JComponent c)
            {
                super.paint(g, c);
                drawIcon (drawOrangeArea, (Graphics2D) g, 4);
            }
        });        
        
        this.drawYellowArea.setUI(new BasicLabelUI()
        {
            @Override
            public void paint(Graphics g, JComponent c)
            {
                super.paint(g, c);
                drawIcon (drawOrangeArea, (Graphics2D) g, 5);
            }
        });        
    
        // setup settings
        this.resERandRadButton.setSelected(true);        
        this.portERandRadButton.setSelected(true);
        
        skillLevelComBox.setModel(new DefaultComboBoxModel(new String[] {CompSkillLevel.UBERMEANTRADER.toString(), 
                                                                         CompSkillLevel.MEDMEANTRADER.toString(),
                                                                         CompSkillLevel.NORMAL.toString(),
                                                                         CompSkillLevel.EASIER.toString()}));
        skillLevelComBox.setSelectedIndex(2);
        
        gameVariantComBox.setModel(new DefaultComboBoxModel(new String[] {GameVariants.NORMAL.toString(), 
                                                                          GameVariants.VOLCANO.toString()}));       
        
        gameTypeComBoxActionPerformed(null);        
        
        // Setup player settings        
        redComBox.setSelectedIndex    (PlyrSel.COMPUTER.toValue());
        whiteComBox.setSelectedIndex  (PlyrSel.COMPUTER.toValue());
        orangeComBox.setSelectedIndex (PlyrSel.COMPUTER.toValue());
        blueComBox.setSelectedIndex   (PlyrSel.HUMAN.toValue());       
                
        gameTypeComBox.setModel(new DefaultComboBoxModel(new String[] { GameTypes.STANDARD4.toString(), 
                                                                        GameTypes.STANDARD6_NO_ENDTURNBLD.toString(),
                                                                        GameTypes.STANDARD6_ENDTURNBLD.toString()}));
        gameTypeComBoxActionPerformed(null);
                                     
        
        centre (parent);
    }
    
    public void centre (Frame parent)
    {
        if (parent != null)
        {
            Point pp = parent.getLocationOnScreen();
            
            this.setLocation((pp.x + parent.getWidth()/2) - (this.getWidth()/2), 
                             (pp.y + parent.getHeight()/2) - (this.getHeight()/2));
        }
    }
                
    public void setUpLike (GameNewJDialog thisGame)
    {
        if (thisGame == null) return;
        
        this.plyr3RadButton.setSelected(thisGame.plyr3RadButton.isSelected());
        this.plyr4RadButton.setSelected(thisGame.plyr4RadButton.isSelected());
        this.plyr5RadButton.setSelected(thisGame.plyr5RadButton.isSelected());
        this.plyr6RadButton.setSelected(thisGame.plyr6RadButton.isSelected());
        
        
        gameTypeComBox.setSelectedIndex(thisGame.gameTypeComBox.getSelectedIndex());
        gameVariantComBox.setSelectedIndex(thisGame.gameVariantComBox.getSelectedIndex());
        
        
        hlAssistChkBox.setSelected(thisGame.hlAssistChkBox.isSelected());
        skillLevelComBox.setSelectedIndex(thisGame.skillLevelComBox.getSelectedIndex());
        skillLevelComBoxActionPerformed  (null);       
               
        blueComBox.setSelectedIndex(thisGame.blueComBox.getSelectedIndex());
        redComBox.setSelectedIndex(thisGame.redComBox.getSelectedIndex());
        whiteComBox.setSelectedIndex(thisGame.whiteComBox.getSelectedIndex());
        orangeComBox.setSelectedIndex(thisGame.orangeComBox.getSelectedIndex());                
        yellowComBox.setSelectedIndex(thisGame.yellowComBox.getSelectedIndex());                
        greenComBox.setSelectedIndex(thisGame.greenComBox.getSelectedIndex());      
         
        AITypeComBox.setSelectedIndex (thisGame.AITypeComBox.getSelectedIndex());
        
        this.resVRandRadButton.setSelected(thisGame.resVRandRadButton.isSelected());
        this.resERandRadButton.setSelected(thisGame.resERandRadButton.isSelected());
        
        this.portVRandRadButton.setSelected(thisGame.portVRandRadButton.isSelected());
        this.portERandRadButton.setSelected(thisGame.portERandRadButton.isSelected());
    }
    
    public void drawIcon (JLabel l, Graphics2D g2, int idx)
    {       
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        
        BufferedImage bi    = plyrImgs[idx];
        Insets        inset = l.getInsets();

        int           newHeight = l.getHeight() + (inset.top + inset.bottom);
        float         reduce    = (float)newHeight / (float)bi.getHeight();
        int           newWidth  = (int)((float)bi.getWidth()*reduce);

        g2.drawImage (bi, inset.left, inset.top, newWidth, newHeight, null);    
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PlayNoButGrp = new javax.swing.ButtonGroup();
        resRandButGrp = new javax.swing.ButtonGroup();
        portRandButGrp = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        skillLevelComBox = new javax.swing.JComboBox();
        gameTypeComBox = new javax.swing.JComboBox();
        gameVariantComBox = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        resERandRadButton = new javax.swing.JRadioButton();
        resVRandRadButton = new javax.swing.JRadioButton();
        jPanel13 = new javax.swing.JPanel();
        portERandRadButton = new javax.swing.JRadioButton();
        portVRandRadButton = new javax.swing.JRadioButton();
        jPanel14 = new javax.swing.JPanel();
        hlAssistChkBox = new javax.swing.JCheckBox();
        AITypeComBox = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        plyr4RadButton = new javax.swing.JRadioButton();
        plyr3RadButton = new javax.swing.JRadioButton();
        plyr5RadButton = new javax.swing.JRadioButton();
        plyr6RadButton = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        drawBlueArea = new javax.swing.JLabel();
        blueComBox = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        drawWhiteArea = new javax.swing.JLabel();
        whiteComBox = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        drawOrangeArea = new javax.swing.JLabel();
        orangeComBox = new javax.swing.JComboBox();
        jPanel15 = new javax.swing.JPanel();
        drawGreenArea = new javax.swing.JLabel();
        greenComBox = new javax.swing.JComboBox();
        jPanel16 = new javax.swing.JPanel();
        drawYellowArea = new javax.swing.JLabel();
        yellowComBox = new javax.swing.JComboBox();
        jPanel18 = new javax.swing.JPanel();
        drawRedArea = new javax.swing.JLabel();
        redComBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Catan Game Settings");

        jPanel1.setMaximumSize(new java.awt.Dimension(131, 33));

        okButton.setText("O.K");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        jPanel10.setMaximumSize(new java.awt.Dimension(600, 400));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Computer Skill Level, Game Type, Varient"));

        skillLevelComBox.setSelectedItem(1);
        skillLevelComBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skillLevelComBoxActionPerformed(evt);
            }
        });

        gameTypeComBox.setSelectedItem(1);
        gameTypeComBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                gameTypeComBoxItemStateChanged(evt);
            }
        });
        gameTypeComBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameTypeComBoxActionPerformed(evt);
            }
        });

        gameVariantComBox.setSelectedItem(1);
        gameVariantComBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameVariantComBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, gameVariantComBox, 0, 250, Short.MAX_VALUE)
                    .add(gameTypeComBox, 0, 250, Short.MAX_VALUE)
                    .add(skillLevelComBox, 0, 250, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(skillLevelComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gameTypeComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gameVariantComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Randomise Game"));

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Resource Tiles"));

        portRandButGrp.add(resERandRadButton);
        resERandRadButton.setText("Evenly Random");

        portRandButGrp.add(resVRandRadButton);
        resVRandRadButton.setText("Very Random");

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(resVRandRadButton)
                    .add(resERandRadButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(resVRandRadButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resERandRadButton)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Port Tiles"));

        resRandButGrp.add(portERandRadButton);
        portERandRadButton.setText("Evenly Random");

        resRandButGrp.add(portVRandRadButton);
        portVRandRadButton.setText("Very Random");

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(portVRandRadButton)
                    .add(portERandRadButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(portVRandRadButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(portERandRadButton))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Game Board Assist, AI Type"));

        hlAssistChkBox.setSelected(true);
        hlAssistChkBox.setText("Highlight Assist");

        AITypeComBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AI Mixed", "AI Hueristics", "AI High Score" }));

        org.jdesktop.layout.GroupLayout jPanel14Layout = new org.jdesktop.layout.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, AITypeComBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, hlAssistChkBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .add(hlAssistChkBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(AITypeComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Player Number"));

        PlayNoButGrp.add(plyr4RadButton);
        plyr4RadButton.setText("4 Players");
        plyr4RadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plyr4RadButtonActionPerformed(evt);
            }
        });

        PlayNoButGrp.add(plyr3RadButton);
        plyr3RadButton.setText("3 Players");
        plyr3RadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plyr3RadButtonActionPerformed(evt);
            }
        });

        PlayNoButGrp.add(plyr5RadButton);
        plyr5RadButton.setText("5 Players");
        plyr5RadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plyr5RadButtonActionPerformed(evt);
            }
        });

        PlayNoButGrp.add(plyr6RadButton);
        plyr6RadButton.setText("6 Players");
        plyr6RadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plyr6RadButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(plyr3RadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(plyr4RadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(plyr5RadButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(plyr6RadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(plyr4RadButton)
                    .add(plyr3RadButton))
                .add(18, 18, 18)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(plyr6RadButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(plyr5RadButton))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Player Type"));

        blueComBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Human", "Computer" }));
        blueComBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                blueComBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel17Layout = new org.jdesktop.layout.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .add(drawBlueArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(blueComBox, 0, 81, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(drawBlueArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(blueComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        whiteComBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Human", "Computer" }));
        whiteComBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                whiteComBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(drawWhiteArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(whiteComBox, 0, 81, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(drawWhiteArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(whiteComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        orangeComBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Human", "Computer" }));
        orangeComBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                orangeComBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(drawOrangeArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(orangeComBox, 0, 81, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(drawOrangeArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orangeComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        greenComBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Human", "Computer" }));
        greenComBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                greenComBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel15Layout = new org.jdesktop.layout.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .add(drawGreenArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(greenComBox, 0, 84, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(drawGreenArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(greenComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        yellowComBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Human", "Computer" }));
        yellowComBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                yellowComBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel16Layout = new org.jdesktop.layout.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .add(drawYellowArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(yellowComBox, 0, 80, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(drawYellowArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(yellowComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        redComBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Human", "Computer" }));
        redComBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                redComBoxItemStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel18Layout = new org.jdesktop.layout.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .add(drawRedArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(redComBox, 0, 83, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(drawRedArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(redComBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jPanel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(3, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel5, 0, 483, Short.MAX_VALUE))
                .add(9, 9, 9))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 215, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-512)/2, (screenSize.height-579)/2, 512, 579);
    }// </editor-fold>//GEN-END:initComponents

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    startGame = false;
    this.dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

    boolean ok = false;
    
    // Check if there is at least 1 human player active.
    if (blueComBox.getSelectedIndex() == PlyrSel.HUMAN.toValue())
        ok = true;
    
    if (redComBox.getSelectedIndex() == PlyrSel.HUMAN.toValue())
        ok = true;
    
    if (whiteComBox.getSelectedIndex() == PlyrSel.HUMAN.toValue())
        ok = true;
    
    if ((orangeComBox.getSelectedIndex() == PlyrSel.HUMAN.toValue()) && (playerNo > 3))
        ok = true;
    
    if ((greenComBox.getSelectedIndex() == PlyrSel.HUMAN.toValue()) &&  (playerNo > 4))        
        ok = true;    
    
    if ((yellowComBox.getSelectedIndex() == PlyrSel.HUMAN.toValue()) && (playerNo > 5))
        ok = true;        
    
    if (ok != false)
    {    
        startGame = true;
        this.dispose();
    }
    else
    {
        MessageJDialog d = new MessageJDialog ((Frame)this.getParent(), true, false);

        d.setText("\nYou need to select at least 1\nHuman player\n\n", true);
        d.setVisible(true);
    }
}//GEN-LAST:event_okButtonActionPerformed

private void yellowComBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_yellowComBoxItemStateChanged
    try
    {
        if (blueComBox.getSelectedIndex()   != 1) blueComBox.setSelectedIndex(1);    
        if (redComBox.getSelectedIndex()    != 1) redComBox.setSelectedIndex(1);
        if (orangeComBox.getSelectedIndex() != 1) orangeComBox.setSelectedIndex(1);
        if (greenComBox.getSelectedIndex()  != 1) greenComBox.setSelectedIndex(1);
        if (whiteComBox.getSelectedIndex()  != 1) whiteComBox.setSelectedIndex(1);                
    }catch (Exception e){ e.printStackTrace(); }
}//GEN-LAST:event_yellowComBoxItemStateChanged

private void blueComBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_blueComBoxItemStateChanged
try
    {
        if (whiteComBox.getSelectedIndex()  != 1) whiteComBox.setSelectedIndex(1);
        if (redComBox.getSelectedIndex()    != 1) redComBox.setSelectedIndex(1);
        if (orangeComBox.getSelectedIndex() != 1) orangeComBox.setSelectedIndex(1);
        if (greenComBox.getSelectedIndex()  != 1) greenComBox.setSelectedIndex(1);
        if (yellowComBox.getSelectedIndex() != 1) yellowComBox.setSelectedIndex(1);
    }catch (Exception e){ e.printStackTrace(); }
}//GEN-LAST:event_blueComBoxItemStateChanged

private void orangeComBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_orangeComBoxItemStateChanged
try
    {
        if (whiteComBox.getSelectedIndex()  != 1) whiteComBox.setSelectedIndex(1);
        if (redComBox.getSelectedIndex()    != 1) redComBox.setSelectedIndex(1);
        if (blueComBox.getSelectedIndex()   != 1) blueComBox.setSelectedIndex(1);
        if (greenComBox.getSelectedIndex()  != 1) greenComBox.setSelectedIndex(1);
        if (yellowComBox.getSelectedIndex() != 1) yellowComBox.setSelectedIndex(1);        
    }catch (Exception e){ e.printStackTrace(); }
}//GEN-LAST:event_orangeComBoxItemStateChanged

private void greenComBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_greenComBoxItemStateChanged
    try
    {
        if (blueComBox.getSelectedIndex()   != 1) blueComBox.setSelectedIndex(1);    
        if (redComBox.getSelectedIndex()    != 1) redComBox.setSelectedIndex(1);
        if (orangeComBox.getSelectedIndex() != 1) orangeComBox.setSelectedIndex(1);
        if (whiteComBox.getSelectedIndex()  != 1) whiteComBox.setSelectedIndex(1);
        if (yellowComBox.getSelectedIndex() != 1) yellowComBox.setSelectedIndex(1);                
    }catch (Exception e){ e.printStackTrace(); }
}//GEN-LAST:event_greenComBoxItemStateChanged

private void whiteComBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_whiteComBoxItemStateChanged
    try
    {
        if (blueComBox.getSelectedIndex()   != 1) blueComBox.setSelectedIndex(1);    
        if (redComBox.getSelectedIndex()    != 1) redComBox.setSelectedIndex(1);
        if (orangeComBox.getSelectedIndex() != 1) orangeComBox.setSelectedIndex(1);
        if (greenComBox.getSelectedIndex()  != 1) greenComBox.setSelectedIndex(1);
        if (yellowComBox.getSelectedIndex() != 1) yellowComBox.setSelectedIndex(1);                
    }catch (Exception e){ e.printStackTrace(); }
}//GEN-LAST:event_whiteComBoxItemStateChanged

private void plyr6RadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plyr6RadButtonActionPerformed
    playerNo = 6;
    this.orangeComBox.setEnabled(true);
    this.greenComBox.setEnabled (true);
    this.yellowComBox.setEnabled(true);       
}//GEN-LAST:event_plyr6RadButtonActionPerformed

private void plyr5RadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plyr5RadButtonActionPerformed
playerNo = 5;
    this.orangeComBox.setEnabled(true);
    this.greenComBox.setEnabled (true);
    this.yellowComBox.setEnabled(false);       
}//GEN-LAST:event_plyr5RadButtonActionPerformed

private void plyr3RadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plyr3RadButtonActionPerformed
playerNo = 3;
    this.orangeComBox.setEnabled(false);
    this.greenComBox.setEnabled(false);
    this.yellowComBox.setEnabled(false);
}//GEN-LAST:event_plyr3RadButtonActionPerformed

private void plyr4RadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plyr4RadButtonActionPerformed
playerNo = 4;
    this.orangeComBox.setEnabled(true);
    this.greenComBox.setEnabled(false);
    this.yellowComBox.setEnabled(false);    
}//GEN-LAST:event_plyr4RadButtonActionPerformed

private void skillLevelComBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skillLevelComBoxActionPerformed
switch (skillLevelComBox.getSelectedIndex())
    {
        case 0:
            skillLevel = CompSkillLevel.UBERMEANTRADER;
            break;                    
        case 1:
            skillLevel = CompSkillLevel.MEDMEANTRADER;
            break;                             
        case 2:
            skillLevel = CompSkillLevel.NORMAL;
            break;            
        case 3:       
            skillLevel = CompSkillLevel.EASIER;
    }
}//GEN-LAST:event_skillLevelComBoxActionPerformed

private void redComBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_redComBoxItemStateChanged
    try
    {
        if (blueComBox.getSelectedIndex()   != 1) blueComBox.setSelectedIndex(1);    
        if (whiteComBox.getSelectedIndex()  != 1) whiteComBox.setSelectedIndex(1);
        if (orangeComBox.getSelectedIndex() != 1) orangeComBox.setSelectedIndex(1);
        if (greenComBox.getSelectedIndex()  != 1) greenComBox.setSelectedIndex(1);
        if (yellowComBox.getSelectedIndex() != 1) yellowComBox.setSelectedIndex(1);                
    }catch (Exception e){ e.printStackTrace(); }
}//GEN-LAST:event_redComBoxItemStateChanged

private void gameTypeComBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameTypeComBoxActionPerformed

    switch (GameTypes.toGameType(gameTypeComBox.getSelectedIndex()))
    {
        case STANDARD4:
            greenComBox.setEnabled(false);
            yellowComBox.setEnabled(false);
            plyr5RadButton.setEnabled(false);
            plyr6RadButton.setEnabled(false);
            plyr3RadButton.setEnabled(true);
            plyr4RadButton.setEnabled(true); 
            plyr4RadButton.setSelected(true);           
            plyr4RadButtonActionPerformed(null);
            break;
                        
        case STANDARD6_NO_ENDTURNBLD:
        case STANDARD6_ENDTURNBLD:
            plyr3RadButton.setEnabled(false);
            plyr4RadButton.setEnabled(false);
            greenComBox.setEnabled(true);
            yellowComBox.setEnabled(true); 
            plyr5RadButton.setEnabled(true);
            plyr6RadButton.setEnabled(true);
            plyr6RadButton.setSelected(true);
            plyr6RadButtonActionPerformed(null);
            break;                                        
    }        
}//GEN-LAST:event_gameTypeComBoxActionPerformed

private void gameVariantComBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameVariantComBoxActionPerformed
}//GEN-LAST:event_gameVariantComBoxActionPerformed

private void gameTypeComBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gameTypeComBoxItemStateChanged
    
    
}//GEN-LAST:event_gameTypeComBoxItemStateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GameNewJDialog dialog = new GameNewJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JComboBox AITypeComBox;
    private javax.swing.ButtonGroup PlayNoButGrp;
    public javax.swing.JComboBox blueComBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel drawBlueArea;
    private javax.swing.JLabel drawGreenArea;
    private javax.swing.JLabel drawOrangeArea;
    private javax.swing.JLabel drawRedArea;
    private javax.swing.JLabel drawWhiteArea;
    private javax.swing.JLabel drawYellowArea;
    public javax.swing.JComboBox gameTypeComBox;
    public javax.swing.JComboBox gameVariantComBox;
    public javax.swing.JComboBox greenComBox;
    public javax.swing.JCheckBox hlAssistChkBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton okButton;
    public javax.swing.JComboBox orangeComBox;
    public javax.swing.JRadioButton plyr3RadButton;
    public javax.swing.JRadioButton plyr4RadButton;
    public javax.swing.JRadioButton plyr5RadButton;
    public javax.swing.JRadioButton plyr6RadButton;
    public javax.swing.JRadioButton portERandRadButton;
    private javax.swing.ButtonGroup portRandButGrp;
    public javax.swing.JRadioButton portVRandRadButton;
    public javax.swing.JComboBox redComBox;
    public javax.swing.JRadioButton resERandRadButton;
    private javax.swing.ButtonGroup resRandButGrp;
    public javax.swing.JRadioButton resVRandRadButton;
    public javax.swing.JComboBox skillLevelComBox;
    public javax.swing.JComboBox whiteComBox;
    public javax.swing.JComboBox yellowComBox;
    // End of variables declaration//GEN-END:variables

}
