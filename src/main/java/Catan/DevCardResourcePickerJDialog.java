package Catan;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import javax.swing.JComponent;

enum DecCardResPickType {NULL, MONOPOLY, YEAROFPLENTY1, YEAROFPLENTY2,
                         SETTLEMENT_GOLD, SETTLEMENT_GOLDEND, 
                         CITY_GOLD1, CITY_GOLD2, CITY_GOLDEND};

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

public class DevCardResourcePickerJDialog extends javax.swing.JDialog 
{
    Player                   thisPlayer  = null;
    DecCardResPickType       type        = DecCardResPickType.NULL;
    LinkedList<ResourceCard> objs        = new LinkedList<ResourceCard>();
    boolean                  okSelection = false;   
    
    /** Creates new form DevCardResourcePickerJDialog */
    public DevCardResourcePickerJDialog(java.awt.Frame parent, boolean modal, 
                                        Player thisPlayer, DecCardResPickType type) 
    {
        super(parent, modal);
        initComponents();
                
        tradeBrick.setTypeResize (ResCardTypes.BRICK);
        tradeWheat.setTypeResize  (ResCardTypes.WHEAT);
        tradeRock.setTypeResize  (ResCardTypes.ROCK);
        tradeSheep.setTypeResize (ResCardTypes.SHEEP);
        tradeWood.setTypeResize  (ResCardTypes.WOOD);                
       
        this.thisPlayer = thisPlayer;
        
        init (type);
        
        centre (parent);
    }

    public void init (DecCardResPickType type)
    {
        objs.clear();
        cancel.setVisible(true);
        ok.setVisible(false);
        
        this.type = type;
        switch (type)
        {
            case MONOPOLY:
                this.setTitle("Monoploy Development Card");
                cancel.setVisible(true);
                ok.setVisible(false);                  
                txt.setText ("Select a card type to recieve from all players");
                break;
                
            case YEAROFPLENTY1:
                this.setTitle("Year of Plenty Development Card");
                cancel.setVisible(true);
                ok.setVisible(false);                
                txt.setText ("Select 2 cards for year of plenty");
                break;
                
            case SETTLEMENT_GOLD:
                this.setTitle("Gold Resource for Settlement");
                cancel.setText("Redo");
                cancel.setVisible(false);
                ok.setVisible(false);                
                txt.setText ("Select a card for a settlement gold resource");
                break;
                
            case CITY_GOLD1:
                this.setTitle("Gold Resource for City");
                cancel.setText("Redo");
                cancel.setVisible(false);
                ok.setVisible(false);                
                txt.setText ("Select 2 cards for a city gold resource");
                break;
        }    
        
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
        
    public void centreToPanel (JComponent p)
    {
        if (p != null)
        {
            Point pp = p.getLocationOnScreen();
            
            this.setLocation((pp.x + p.getWidth()/2) - (this.getWidth()/2), 
                             (pp.y + p.getHeight()/2) - (this.getHeight()/2));
        }
    }  
    
    public void processSelection (ResourceCard rc)
    {
        switch (type)
        {
            case MONOPOLY:
                {
                    // Take every card of this resource type from all other players.
                    thisPlayer.appropriateAllOfType(rc.type, objs);

                    txt.setText ("You have appropriated " + objs.size() + " card(s)");
                    ok.setVisible(true); 
                    cancel.setVisible(false); 
                }                
                break;
                
            case YEAROFPLENTY1:
                type = DecCardResPickType.YEAROFPLENTY2;
                txt.setText ("Select last card for year of plenty");
                objs.add(rc);
                break;
                
            case YEAROFPLENTY2:
                type = DecCardResPickType.NULL;
                txt.setText ("Click O.K to accept, Cancel to abort");
                objs.add(rc);
                ok.setVisible(true);                 
                break;
                
            case SETTLEMENT_GOLD:
                type = DecCardResPickType.SETTLEMENT_GOLDEND;
                txt.setText ("Click O.K to accept, or Redo");
                objs.add(rc);
                cancel.setVisible(true);
                ok.setVisible(true);                                
                break;
                
            case CITY_GOLD1:
                type = DecCardResPickType.CITY_GOLD2;
                cancel.setVisible(true);
                objs.add(rc);
                txt.setText ("Select last card for a city gold resource");
                break;
                
            case CITY_GOLD2:
                cancel.setVisible(true);
                ok.setVisible(true);                                                
                objs.add(rc);
                type = DecCardResPickType.CITY_GOLDEND;
                cancel.setVisible(true);
                txt.setText ("Click O.K to accept, or Redo");
                break;                
        }  
        
        this.repaint();
    }

    public void paint (Graphics g)
    {                  
        super.paint (g);

        // Draw current selection ... 
        int      xOffset = 10;
        for (ResourceCard rc:objs)
        {                       
            rc.drawAt(null, g, xOffset, txt.getY() + txt.getHeight() + 40, false, thisPlayer.gameRules.gameTurnNo);
            xOffset += (rc.getWidth() / 2);
        }         
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        borderPanel = new javax.swing.JPanel();
        selectResJPanel = new javax.swing.JPanel();
        tradeWheat = new Catan.ResourceCard();
        tradeSheep = new Catan.ResourceCard();
        tradeWood = new Catan.ResourceCard();
        tradeBrick = new Catan.ResourceCard();
        tradeRock = new Catan.ResourceCard();
        txt = new javax.swing.JLabel();
        botPanel = new javax.swing.JPanel();
        cancel = new javax.swing.JButton();
        ok = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        borderPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        borderPanel.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        borderPanel.setDoubleBuffered(false);

        selectResJPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        selectResJPanel.setDoubleBuffered(false);

        tradeWheat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tradeWheatMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tradeWheatLayout = new org.jdesktop.layout.GroupLayout(tradeWheat);
        tradeWheat.setLayout(tradeWheatLayout);
        tradeWheatLayout.setHorizontalGroup(
            tradeWheatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        tradeWheatLayout.setVerticalGroup(
            tradeWheatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        selectResJPanel.add(tradeWheat);

        tradeSheep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tradeSheepMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tradeSheepLayout = new org.jdesktop.layout.GroupLayout(tradeSheep);
        tradeSheep.setLayout(tradeSheepLayout);
        tradeSheepLayout.setHorizontalGroup(
            tradeSheepLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        tradeSheepLayout.setVerticalGroup(
            tradeSheepLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        selectResJPanel.add(tradeSheep);

        tradeWood.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tradeWoodMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tradeWoodLayout = new org.jdesktop.layout.GroupLayout(tradeWood);
        tradeWood.setLayout(tradeWoodLayout);
        tradeWoodLayout.setHorizontalGroup(
            tradeWoodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        tradeWoodLayout.setVerticalGroup(
            tradeWoodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        selectResJPanel.add(tradeWood);

        tradeBrick.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tradeBrickMousePressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tradeBrickLayout = new org.jdesktop.layout.GroupLayout(tradeBrick);
        tradeBrick.setLayout(tradeBrickLayout);
        tradeBrickLayout.setHorizontalGroup(
            tradeBrickLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        tradeBrickLayout.setVerticalGroup(
            tradeBrickLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        selectResJPanel.add(tradeBrick);

        tradeRock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tradeRockMousePressed(evt);
            }
        });
        selectResJPanel.add(tradeRock);

        botPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        botPanel.setDoubleBuffered(false);

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });
        botPanel.add(cancel);

        ok.setText("O.K");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });
        botPanel.add(ok);

        org.jdesktop.layout.GroupLayout borderPanelLayout = new org.jdesktop.layout.GroupLayout(borderPanel);
        borderPanel.setLayout(borderPanelLayout);
        borderPanelLayout.setHorizontalGroup(
            borderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(selectResJPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
            .add(txt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
            .add(botPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
        );
        borderPanelLayout.setVerticalGroup(
            borderPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(borderPanelLayout.createSequentialGroup()
                .add(selectResJPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 77, Short.MAX_VALUE)
                .add(botPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(borderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(borderPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-290)/2, (screenSize.height-230)/2, 290, 230);
    }// </editor-fold>//GEN-END:initComponents

    private void tradeWheatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tradeWheatMouseClicked
        if ((tradeWheat.isVisible() != false) && (ok.isVisible() == false))
            this.processSelection(tradeWheat);
}//GEN-LAST:event_tradeWheatMouseClicked

    private void tradeSheepMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tradeSheepMouseClicked
        if ((tradeSheep.isVisible() != false) && (ok.isVisible() == false))
            this.processSelection(tradeSheep);
    }//GEN-LAST:event_tradeSheepMouseClicked

    private void tradeWoodMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tradeWoodMouseClicked
        if ((tradeWood.isVisible() != false) && (ok.isVisible() == false))
            this.processSelection(tradeWood);
    }//GEN-LAST:event_tradeWoodMouseClicked

    private void tradeBrickMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tradeBrickMousePressed
        if ((tradeBrick.isVisible() != false) && (ok.isVisible() == false))
            this.processSelection(tradeBrick);
    }//GEN-LAST:event_tradeBrickMousePressed

    private void tradeRockMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tradeRockMousePressed
        if ((tradeRock.isVisible() != false) && (ok.isVisible() == false))
            this.processSelection(tradeRock);
    }//GEN-LAST:event_tradeRockMousePressed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        
        switch (type)
        {
            case SETTLEMENT_GOLDEND:
                init (DecCardResPickType.SETTLEMENT_GOLD);
                break;                
            case CITY_GOLDEND:            
                init (DecCardResPickType.CITY_GOLD1);
                break;
                
            default:
                okSelection = false;        
                this.setVisible(false); 
        }
    }//GEN-LAST:event_cancelActionPerformed

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        thisPlayer.resCards.addAll(objs);
        okSelection = true;
        this.setVisible(false); 
    }//GEN-LAST:event_okActionPerformed

    private void formComponentResized (java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized
        Dimension size = getSize();
        int newWidth  = size.width;
        int newHeight = size.height;
        if (size.width < 280) 
            newWidth = 280;
        if (size.height < 240) 
            newHeight = 240;
        setSize(newWidth, newHeight);                                 
        this.repaint();        
    }//GEN-LAST:event_formComponentResized

    private void formWindowClosing (java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        if (ok.isVisible() != false)
        {
            if (type == DecCardResPickType.MONOPOLY)
            {
                thisPlayer.resCards.addAll(objs);
                okSelection = true;            
            }
            else
                okSelection = false;
        }
        this.setVisible(false); 
    }//GEN-LAST:event_formWindowClosing
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel borderPanel;
    private javax.swing.JPanel botPanel;
    private javax.swing.JButton cancel;
    private javax.swing.JButton ok;
    private javax.swing.JPanel selectResJPanel;
    private Catan.ResourceCard tradeBrick;
    private Catan.ResourceCard tradeRock;
    private Catan.ResourceCard tradeSheep;
    private Catan.ResourceCard tradeWheat;
    private Catan.ResourceCard tradeWood;
    private javax.swing.JLabel txt;
    // End of variables declaration//GEN-END:variables
    
}
