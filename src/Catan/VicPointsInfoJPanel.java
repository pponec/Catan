package Catan;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.swing.border.SoftBevelBorder;
import javax.swing.JToolTip;
import Catan.MultiLineToolTip.JMultiLineToolTip;
import java.awt.Point;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

public class VicPointsInfoJPanel extends javax.swing.JPanel
{    
    class PlyrClickInfo
    {
        Player player = null;
        int    left   = 0, top    = 0;
        int    right  = 0, bottom = 0;
        
        public boolean clicked (Point p)
        {
            if ((p.x>=left && p.x<=right) &&
                (p.y>=top  && p.y<= bottom))
            {
                return true;
            }               
            return false;
        }
    }
    
    LinkedList<PlyrClickInfo> plyrClkInfo = new LinkedList<PlyrClickInfo>();
    LinkedList<Player>        players     = null;
    SoftBevelBorder           sbr         = new SoftBevelBorder(SoftBevelBorder.RAISED);
    
    /** Creates new form VicPointsInfoJPanel */
    public VicPointsInfoJPanel ()
    {
        initComponents ();
    }
    
    @Override
    public JToolTip createToolTip()
    {
        return new JMultiLineToolTip();
    }
    
    public void assignPlayerList (LinkedList<Player> players)
    {
        this.players = players;        
    }
    
    public void paint (Graphics g)
    {        
        super.paint (g);
        
        Graphics2D g2 = (Graphics2D)g;        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);      
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);                     
        
        if (players == null) return;
        
        // determine if player has more than 10 points (end of game), and if so, 
        // divide horizontal spacing by that amount.
        int maxPnts = 10;        
        for (Player p:players)
        {
            if (p.vicPntsPublic > maxPnts)
                maxPnts = p.vicPntsPublic;
        }
        
        int    subVert = this.getInsets().bottom + this.getInsets().top;
        int    subHorz = this.getInsets().left   + this.getInsets().right;
        float  yDiv    =  (this.getHeight() - subVert)     / players.size();
        float  xDiv    = ((this.getWidth()  - subHorz)     / 2)  / maxPnts;
        int    xStart  = this.getInsets().left;
        int    idx     = 0;
        String toolTip = "";
        for (Player p:players)
        {
            int vPoints = p.vicPntsPublic;
            
            if (idx > 0)
                toolTip += "\n";
            
            toolTip += "  " + p.name + "\t";
                
            // show all points at the end of the game
            if (p.gameRules.gamePhase == GamePhaseTypes.ENDGAME)
            {
                vPoints = p.vicPntsTotal;
                toolTip += "Total Victory Points";                
            }
            else
            {
                if (p.type == PlayerTypes.HUMAN)
                {
                    vPoints = p.vicPntsTotal;
                    toolTip += "Total Victory Points";
                }
                else
                    toolTip += "Known Victory Points";
            }
                                        
            toolTip += "\t(" + vPoints + ")  ";
            
            if (vPoints > 0)
            {
                g2.setColor(p.col.toCol());
                int x = xStart;
                int y = (int)((subVert/2) + (idx * yDiv));                
                int w = (int)(vPoints * xDiv);
                int h = (int)yDiv;
                g2.fillRect(x, y, w, h);
                
                for (int i = vPoints; i > 0; i--)
                {
 					w = (int)(i * xDiv);
                    sbr.paintBorder(this, g2, x, y, w, h);               
                }
            }
            idx++;
        }
        
        setToolTipText(toolTip);
        
        // Draw squares of each colour for each player
        plyrClkInfo.clear();
        xDiv   = ((this.getWidth() - subHorz)  / 2)  / players.size();
        yDiv   = (this.getHeight() - subVert);
        idx    = 0;
        xStart = (this.getWidth()  - (subHorz/2)) / 2;                
        
        for (Player p:players)
        {
            g2.setColor(p.col.toCol());
            
            if (p.gameRules.thisPlayer != p)
            {
                int red   = p.col.toCol().getRed()  - 160;
                int green = p.col.toCol().getGreen()- 160;
                int blue  = p.col.toCol().getBlue() - 160;

                if (red   < 0) red   = 0;
                if (green < 0) green = 0;
                if (blue  < 0) blue  = 0;
                
                g2.setColor(new Color(red,green,blue));
            }
            else
                g2.setColor(p.col.toCol());
            
            int x = xStart + (int)((subHorz/2) + (idx * xDiv));
            int y = (int)(subVert/2);

            g2.fillRoundRect(x, y, (int)xDiv, (int)yDiv,(int)(yDiv),(int)(yDiv));
                                
            PlyrClickInfo pci = new PlyrClickInfo();
            pci.left   = x;
            pci.top    = y;
            pci.right  = pci.left + (int)xDiv;
            pci.bottom = pci.top  + (int)yDiv;
            pci.player = p;
            plyrClkInfo.add (pci);
                    
            // Draw current player                    
            if (p.gameRules.thisPlayer == p)
            {
                BufferedImage plyrImg = p.getPlayerImage();

                if (plyrImg != null)
                {   
                    int w  = (int)(((float)plyrImg.getWidth() / 300.0) * yDiv);
                    int h  = (int)(((float)plyrImg.getHeight()/ 300.0) * yDiv);
                    
                    g2.drawImage(plyrImg, 
                                 (int)((x+(xDiv/2))-(w/2)), 
                                 (int)(y+((yDiv - h)/2)), w, h, null, null);                    
                }                                        
            }
            idx++;
        }                           
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 19, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

    for (PlyrClickInfo pci:plyrClkInfo)
    {
        if (pci.clicked(evt.getPoint()) != false)
        {
            // Switch the current player info panel to this player
            pci.player.gameRules.gameWindow.playerInfo.setSelectedComponent(pci.player.resPanelInfo);
        }
    }
}//GEN-LAST:event_formMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
