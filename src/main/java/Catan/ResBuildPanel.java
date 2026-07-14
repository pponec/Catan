package Catan;

import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import javax.swing.JToolTip;
import Catan.MultiLineToolTip.JMultiLineToolTip;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

public class ResBuildPanel extends javax.swing.JPanel
{       
    class ResClickInfo
    {
        ResCardTypes type  = ResCardTypes.NULL;
        int          left  = 0, top    = 0;
        int          right = 0, bottom = 0;
        
        public ResClickInfo (ResCardTypes type, int left, int top, int right, int bottom)
        {
            this.type = type; this.left = left; this.right = right; this.top = top; this.bottom = bottom;
        }
        
        public boolean clicked (Point p)
        {
            if ((p.x>=left &&p.x<=right) &&
                (p.y>=top && p.y <= bottom))
            {
                return true;
            }               
            return false;
        }
    }
    
    // ------------------------------------------------------------------------
        
    LinkedList<ResClickInfo> ttRegionList = new LinkedList<ResClickInfo>();    
    BuildInfoJFrame  buildInfo            = null;
    Player           player               = null;
    TradeJDialog     tradeDialog          = null;
    boolean          isBuilding           = false;
    boolean          compCheatViewer      = false;  // view computer cards (cheat/debug)            
                 
    /** Creates new form ResBuildPanel */
    public ResBuildPanel ()
    {       
        initComponents ();        
    }
    
    public void assignPlayer (Player p)
    {
        player = p;    
    }
    
    public void refreshPlayerInfo ()
    {
        updatePurchanseActions ();
        super.repaint();
    }
    
    public ResBuildPanel returnThis ()
    {
        return this;
    }
       
    public void updatePurchanseActions ()
    {       
        if (player.type == PlayerTypes.COMPUTER)            
        {
            tradePort.setVisible(false);
            tradeSell.setVisible(false);
            tradeBuy.setVisible(false);
            buildSettlement.setVisible(false);
            buildRoad.setVisible(false);
            buyCard.setVisible(false);
            buildCity.setVisible(false);
            cancelAction.setVisible(false);
            showBuildInfo.setVisible(false);                                
            return;
        }
        
        if (isBuilding != false)
        {
            buildSettlement.setEnabled(false); 
            buildRoad.setEnabled(false);
            buyCard.setEnabled(false);
            buildCity.setEnabled(false);
            
            tradePort.setEnabled(false);
            tradeSell.setEnabled(false);
            tradeBuy.setEnabled(false);    
            
            cancelAction.setEnabled(true);
            showBuildInfo.setEnabled(true);                                
        }
        else
        {
            switch (player.gameRules.gameWindow.currTurnMode)
            {
                case NONE:
                case DICEROLL:                    
                    tradePort.setEnabled(false);
                    tradeSell.setEnabled(false);
                    tradeBuy.setEnabled(false);   
                    buildSettlement.setEnabled(false); 
                    buildRoad.setEnabled(false);
                    buyCard.setEnabled(false);
                    buildCity.setEnabled(false);
                    cancelAction.setEnabled(false);
                    showBuildInfo.setEnabled(false);                    
                    break;
                    
                case ENDOUTOFTURNBUILD:
                case ENDTURN:
                    if ((player.canBuild (CanBuildTypes.SETTLEMENT) > 0) && (player.bldStckSettlement > 0))
                        buildSettlement.setEnabled(true);
                    else
                        buildSettlement.setEnabled(false);

                    if ((player.canBuild (CanBuildTypes.ROAD) > 0) && (player.bldStckRoad > 0))
                        buildRoad.setEnabled(true);
                    else
                        buildRoad.setEnabled(false);

                    if ((player.canBuild (CanBuildTypes.DEVCARD) > 0) && (player.gameRules.gameDevCards.size() > 0))
                        buyCard.setEnabled(true);
                    else
                        buyCard.setEnabled(false);

                    if ((player.canBuild (CanBuildTypes.CITY) > 0) && (player.bldStckCity > 0))
                        buildCity.setEnabled(true);
                    else
                        buildCity.setEnabled(false);        

                    // If in out of turn build phase (5-6 player catan), then no trade allowed!
                    if (player.gameRules.gameWindow.currTurnMode == TurnMode.ENDOUTOFTURNBUILD)
                    {
                        tradePort.setEnabled(false);
                        tradeSell.setEnabled(false);
                        tradeBuy.setEnabled(false);                                                                            
                    }
                    else
                    {
                        // Enable if we can port trade...                                        
                        tradePort.setEnabled(player.canPortTrade());

                        // Enable if we can trade... 
                        if (player.resCards.size() > 0)
                        {
                            tradeSell.setEnabled(true);
                            tradeBuy.setEnabled(true);                            
                        }
                        else
                        {
                            tradeSell.setEnabled(false);
                            tradeBuy.setEnabled(false);                                                    
                        }
                    }
                    
                    cancelAction.setEnabled(false);
                    showBuildInfo.setEnabled(true);                                        
                    break;
                    
                case ENDTRADE:
                    buildSettlement.setEnabled(false); 
                    buildRoad.setEnabled(false);
                    buyCard.setEnabled(false);
                    buildCity.setEnabled(false);
                    cancelAction.setEnabled(false);
                    tradePort.setEnabled(false);

                    // Enable if we can trade... 
                    if (player.resCards.size() > 0)
                    {
                        tradeSell.setEnabled(true);
                        tradeBuy.setEnabled(true);                            
                    }
                    else
                    {
                        tradeSell.setEnabled(false);
                        tradeBuy.setEnabled(false);                                                    
                    }
                    
                    showBuildInfo.setEnabled(true); 
                    break;                    
            }
        }
    }
    
    @Override 
    public void setEnabled (boolean e)
    {
        super.setEnabled(e);
        
        updatePurchanseActions ();
    }
        
    
    @Override
    public void paint (Graphics g)
    {
        super.paint(g);
        
        Graphics2D g2 = (Graphics2D)g;        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);                                   
                
        if (player == null)
            return;
        
        // --------- Draw Player Resources --------      
        int xOffset = this.getInsets().left + 10;
        int yOffset = this.getInsets().top  + 10;
        ResCardTypes dispOrder[] = {ResCardTypes.SHEEP, ResCardTypes.WHEAT, ResCardTypes.BRICK, ResCardTypes.WOOD, ResCardTypes.ROCK};

        if ((player.type == PlayerTypes.HUMAN) || (compCheatViewer != false) || (player.gameRules.gamePhase == GamePhaseTypes.ENDGAME))
        {
            for (int idx = 0; idx < dispOrder.length; idx++)
            {
                int xCnt = 0;            
                ResourceCard lastRC = null;
                for (ResourceCard rc:player.resCards)
                {
                    if (rc.type == dispOrder[idx])
                    {
                        rc.drawAt (player, g, xOffset + (xCnt * (rc.getWidth()/2)), yOffset, false, player.gameRules.gameTurnNo);
                        lastRC = rc;
                        xCnt++;
                    }
                }                     

                if ((xCnt > 0) && (lastRC != null))
                    yOffset = yOffset + (lastRC.getHeight() + 5);
            }
        }
        else
        {         
            // Display computer cards as hidden
            int count = 0;
            int x     = xOffset;
            int y     = yOffset;
            int inset = this.getInsets().right;
            for (ResourceCard rc:player.resCards)
            {            
                if ((x+rc.getWidth()) >= this.getWidth()-((xOffset*4)+inset))
                {                    
                    x = this.getInsets().left + 10;
                    y = y + rc.getHeight() + 10;
                }
                else if (count > 0)
                {
                    x = x + (rc.getWidth()/2);  // over lap cards 
                }
                
                rc.drawAt(player, g, x, y, true, player.gameRules.gameTurnNo); // display as a hidden card
                count++;
            }
        }
        
        // --------- Draw build resource available ----------

        // set text to bold 
        Font f = g2.getFont();
        int style = f.getStyle();        
        style+=Font.BOLD;
        g2.setFont(f.deriveFont(style));

        BufferedImage bi = BuildPoint.resSetlImgs[player.col.toValue()];
        int inset = this.getInsets().right;
        int xPos =  this.getWidth() - inset - 20;
        int yPos =  this.getInsets().top + 10;
        g2.drawImage (bi, xPos, yPos, 10, 16, null);
        g2.setColor (Color.black);
        g2.drawString (""+player.bldStckSettlement, xPos+2, yPos + 27);
                            
        bi = BuildPoint.resCityImgs[player.col.toValue()];
        g2.drawImage(bi, xPos, yPos+40, 15, 16, null);        
        g2.setColor (Color.black);
        g2.drawString (""+player.bldStckCity, xPos+3, yPos + 40 + 27);
        
        g2.setColor (player.col.toAltCol());
        Stroke save = g2.getStroke();
        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (xPos, yPos + 80, xPos + 15, yPos + 80);  
        g2.setStroke (save);      
        g2.setColor (Color.black);
        g2.drawString (""+player.bldStckRoad, xPos+1, yPos + 80 + 18);
                            
        
        // --------- Draw player Dev Cards (Not Played) ---------    
        ttRegionList.clear();

        xOffset = this.getWidth()     - 10 - this.getInsets().right;               
        yOffset = botSeparator.getY() - 10 - this.getInsets().bottom;

        for (ResourceCard rc:player.newDevCards)
        {
            if (player.type == PlayerTypes.COMPUTER)
            {
                boolean drawHidden = !compCheatViewer;
                
                if (player.gameRules.gamePhase == GamePhaseTypes.ENDGAME)
                    drawHidden = false;
                
                rc.drawAt      (player, g, xOffset - rc.getWidth(), yOffset-rc.getHeight(), drawHidden, player.gameRules.gameTurnNo);
                
                if ((compCheatViewer != false) || (player.gameRules.gamePhase == GamePhaseTypes.ENDGAME))
                {
                    rc.setLocation (xOffset - rc.getWidth(),    yOffset-rc.getHeight()); // used in user click selection
                    ttRegionList.add(new ResClickInfo (rc.type, rc.getX(), rc.getY(), rc.getX() + rc.getWidth(), rc.getY() + rc.getHeight()));                
                }
            }
            else
            {
                rc.drawAt      (player, g, xOffset - rc.getWidth(), yOffset-rc.getHeight(), false, player.gameRules.gameTurnNo);            
                rc.setLocation (xOffset - rc.getWidth(),    yOffset-rc.getHeight()); // used in user click selection
                ttRegionList.add(new ResClickInfo (rc.type, rc.getX(), rc.getY(), rc.getX() + rc.getWidth(), rc.getY() + rc.getHeight()));
            }
            
            xOffset -= rc.getWidth()/2;                
        }
                 
        // --------- Draw player Dev Cards (Used/Played) ---------       
        xOffset = this.getInsets().left + 10;               
        yOffset = botSeparator.getY()   - 10;
    	for(ResourceCard rc:player.usedDevCards)
        {            
            if (rc.type == ResCardTypes.DEV_ARMY)
            {
                int x = xOffset;
                int y = yOffset-(rc.getHeight()/2);
                int w = rc.getWidth()/2;
                int h = rc.getHeight()/2;
                
                rc.drawAt      (g, x, y, w, h, false, player.gameRules.gameTurnNo);            
                ttRegionList.add(new ResClickInfo (rc.type, 
                                                   x, y, 
                                                   x + w, y + h));
                xOffset += rc.getWidth()/6;
            }
        }        
                
        for (ResourceCard rc:player.usedDevCards)
        {            
            switch (rc.type)
            {
                case DEV_VP_CHAPEL:
                case DEV_VP_UNIVERSITY:
                case DEV_VP_PALACE:
                case DEV_VP_MARKET:
                case DEV_VP_LIBRARY:
                    int x = xOffset;
                    int y = yOffset-(rc.getHeight()/2);
                    int w = rc.getWidth()/2;
                    int h = rc.getHeight()/2;                    
                    rc.drawAt      (g, x, y, w, h, false, player.gameRules.gameTurnNo);            
                    ttRegionList.add(new ResClickInfo (rc.type, 
                                                       x, y, 
                                                       x + w, y + h));
                    xOffset += rc.getWidth()/6;
                    break;
            }
        }        

        if (player.longestRoadCard != null)
        {
            if (xOffset > this.getInsets().left + 10) 
                xOffset += player.longestRoadCard.getWidth()/2;
        }
        else if (player.largestArmyCard != null)
        {
            if (xOffset > this.getInsets().left + 10) 
                xOffset += player.largestArmyCard.getWidth()/2;            
        }
       
        if (player.longestRoadCard != null)
        {
            int x = xOffset;
            int y = yOffset-(player.longestRoadCard.getHeight()/2);
            int w = player.longestRoadCard.getWidth()/2;
            int h = player.longestRoadCard.getHeight()/2;            
            player.longestRoadCard.drawAt      (g, x, y, w, h, false, player.gameRules.gameTurnNo);            
            ttRegionList.add(new ResClickInfo (player.longestRoadCard.type, 
                                               x, y, 
                                               x + w, y + h));
            xOffset += player.longestRoadCard.getWidth()/6;
        }
        if (player.largestArmyCard != null)
        {
            int x = xOffset;
            int y = yOffset-(player.largestArmyCard.getHeight()/2);
            int w = player.largestArmyCard.getWidth()/2;
            int h = player.largestArmyCard.getHeight()/2;              
            player.largestArmyCard.drawAt     (g, x, y, w, h, false, player.gameRules.gameTurnNo);            
            ttRegionList.add(new ResClickInfo (player.largestArmyCard.type, 
                                               x, y, 
                                               x + w, y + h));
            xOffset += player.largestArmyCard.getWidth()/6;            
        }
    }
               
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buildRoad = new javax.swing.JButton();
        tradePort = new javax.swing.JButton();
        buyCard = new javax.swing.JButton();
        buildCity = new javax.swing.JButton();
        tradeSell = new javax.swing.JButton();
        botSeparator = new javax.swing.JSeparator();
        cancelAction = new javax.swing.JButton();
        showBuildInfo = new javax.swing.JButton();
        buildSettlement = new javax.swing.JButton();
        tradeBuy = new javax.swing.JButton();

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        buildRoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_build.png"))); // NOI18N
        buildRoad.setText("Road"); // NOI18N
        buildRoad.setFocusable(false);
        buildRoad.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        buildRoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildRoadActionPerformed(evt);
            }
        });

        tradePort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_anchor.png"))); // NOI18N
        tradePort.setText("Port");
        tradePort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradePortActionPerformed(evt);
            }
        });

        buyCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_diamond.png"))); // NOI18N
        buyCard.setText("Development Card"); // NOI18N
        buyCard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buyCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyCardActionPerformed(evt);
            }
        });

        buildCity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_build.png"))); // NOI18N
        buildCity.setText("City"); // NOI18N
        buildCity.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buildCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildCityActionPerformed(evt);
            }
        });

        tradeSell.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_sell_trader.png"))); // NOI18N
        tradeSell.setText("Sell");
        tradeSell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeSellActionPerformed(evt);
            }
        });

        cancelAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_stop.png"))); // NOI18N
        cancelAction.setText("Cancel Build"); // NOI18N
        cancelAction.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cancelAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionActionPerformed(evt);
            }
        });

        showBuildInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_info.png"))); // NOI18N
        showBuildInfo.setText("Build Info");
        showBuildInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        showBuildInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBuildInfoActionPerformed(evt);
            }
        });

        buildSettlement.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_build.png"))); // NOI18N
        buildSettlement.setText("Settlement"); // NOI18N
        buildSettlement.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buildSettlement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildSettlementActionPerformed(evt);
            }
        });

        tradeBuy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_buy_trader.png"))); // NOI18N
        tradeBuy.setText("Buy");
        tradeBuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeBuyActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(botSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, buildRoad, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, buildSettlement, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, buildCity, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(cancelAction, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .add(showBuildInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .add(buyCard, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(tradePort, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                    .add(tradeBuy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tradeSell, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(395, Short.MAX_VALUE)
                .add(botSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tradePort)
                    .add(buildSettlement)
                    .add(buyCard))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tradeBuy)
                    .add(buildRoad)
                    .add(showBuildInfo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tradeSell)
                    .add(buildCity)
                    .add(cancelAction)))
        );
    }// </editor-fold>//GEN-END:initComponents
            
    private void showBuildInfoActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_showBuildInfoActionPerformed
    {//GEN-HEADEREND:event_showBuildInfoActionPerformed
        if (buildInfo == null)
            buildInfo = new BuildInfoJFrame();
        
        if (buildInfo.isVisible() == false)
            buildInfo.resetSize();
        
        buildInfo.setVisible(true);
    }//GEN-LAST:event_showBuildInfoActionPerformed

    private void tradeBuyActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_tradeBuyActionPerformed
    {//GEN-HEADEREND:event_tradeBuyActionPerformed
        if (tradeDialog == null)
            tradeDialog = new TradeJDialog(player.gameRules.gameWindow, true, this, this.player.gameRules.tradeDialogLastPos);
        
        tradeDialog.assignThisPlayer (player, TradeTypes.BUY, false);
        tradeDialog.setVisible       (true); 
        this.updatePurchanseActions ();                
        this.repaint();
    }//GEN-LAST:event_tradeBuyActionPerformed

    private void tradeSellActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_tradeSellActionPerformed
    {//GEN-HEADEREND:event_tradeSellActionPerformed
        if (tradeDialog == null)
            tradeDialog = new TradeJDialog(player.gameRules.gameWindow, true, this, this.player.gameRules.tradeDialogLastPos);
        
        tradeDialog.assignThisPlayer (player, TradeTypes.SELL, false);
        tradeDialog.setVisible       (true);  
        this.updatePurchanseActions ();

        // ask computer to have another go at its turn 
        if ((player.gameRules.thisPlayer.type == PlayerTypes.COMPUTER) && 
            (tradeDialog.tradeSuccess != false))
        {
            try                    
            {
                player.gameRules.setGamePhase(GamePhaseTypes.TRADE_BUILD);            
            }
            catch (CatanEndGameException e)
            {                
            }
        }
        this.repaint();
    }//GEN-LAST:event_tradeSellActionPerformed

    private void buildRoadActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_buildRoadActionPerformed
    {//GEN-HEADEREND:event_buildRoadActionPerformed
        if (player.canBuild (CanBuildTypes.ROAD) <= 0)
            return;
        
        // Set game rules mode to build road...
        this.isBuilding = true;
        this.updatePurchanseActions();
        
        try
        {
            player.gameRules.setGamePhase(GamePhaseTypes.BUILD_ROAD);  
        }
        catch (CatanEndGameException e) 
        {}        
    }//GEN-LAST:event_buildRoadActionPerformed

    private void cancelActionActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelActionActionPerformed
    {//GEN-HEADEREND:event_cancelActionActionPerformed
        this.isBuilding = false;
        this.updatePurchanseActions();
        try
        {
            player.gameRules.setGamePhase(GamePhaseTypes.TRADE_BUILD);                    
        } 
        catch (CatanEndGameException e)
        {            
        }
    }//GEN-LAST:event_cancelActionActionPerformed

    private void buildCityActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_buildCityActionPerformed
    {//GEN-HEADEREND:event_buildCityActionPerformed
        if (player.canBuild (CanBuildTypes.CITY) <= 0)
            return;
        
        // Set game rules mode to build road...
        this.isBuilding = true;
        this.updatePurchanseActions();
        try
        {
            player.gameRules.setGamePhase(GamePhaseTypes.BUILD_CITY);
        }        
        catch (CatanEndGameException e)
        {}                            
    }//GEN-LAST:event_buildCityActionPerformed

    private void buildSettlementActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_buildSettlementActionPerformed
    {//GEN-HEADEREND:event_buildSettlementActionPerformed
        if (player.canBuild (CanBuildTypes.SETTLEMENT) <= 0)
            return;
        
        // Set game rules mode to build road...
        this.isBuilding = true;
        this.updatePurchanseActions();
        try
        {
            player.gameRules.setGamePhase(GamePhaseTypes.BUILD_SETTLEMENT);
        } 
        catch (CatanEndGameException e)
        {}            
    }//GEN-LAST:event_buildSettlementActionPerformed

    private void tradePortActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_tradePortActionPerformed
    {//GEN-HEADEREND:event_tradePortActionPerformed
        if (tradeDialog == null)
            tradeDialog = new TradeJDialog(player.gameRules.gameWindow, true, this, this.player.gameRules.tradeDialogLastPos);
        
        // assign player info to trade frame ...        
        tradeDialog.assignThisPlayer (player, TradeTypes.PORT, false);
        
        if (tradeDialog.canTrade() == false)
        {
            MessageJDialog msg = new MessageJDialog(this.player.gameRules.gameWindow, true, false);
            
            msg.setText("You do not have sufficient\nresources for a Port Trade", true);
            msg.centreToPanel(this);
            msg.setVisible(true);
        }
        else 
        {            
            tradeDialog.setVisible       (true);  
            this.updatePurchanseActions ();
            this.repaint();
        }
    }//GEN-LAST:event_tradePortActionPerformed

    private void buyCardActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_buyCardActionPerformed
    {//GEN-HEADEREND:event_buyCardActionPerformed
        try 
        { 
            if (player.buyDevCard () != false)
                this.repaint();
        
            this.updatePurchanseActions();
        }
        catch (CatanEndGameException e)
        {
            // Buying the winning victory-point card ends the game. checkForVictory()
            // already switched to the ENDGAME phase and showed the end-game dialog
            // before this exception was thrown to unwind the stack, so there is
            // nothing left to do here (same as the other build-action handlers).
        }
    }//GEN-LAST:event_buyCardActionPerformed

    private void formMouseClicked (java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseClicked
    {//GEN-HEADEREND:event_formMouseClicked
    try
    {        
        if ((player.gameRules.thisPlayer.type == PlayerTypes.COMPUTER) || 
            (player.gameRules.thisPlayer != player))
        {
            player.gameRules.playSound(AudioClipTypes.ERROR);                        
            return;
        }
        
        if (this.isEnabled() == false)
            return;

        // Can't play dev cards in out-of-turn build mode (Catan 5-6 game type)        
        switch (player.gameRules.gameWindow.currTurnMode)
        {
            case ENDOUTOFTURNBUILD:
                player.gameRules.playSound(AudioClipTypes.ERROR);
                return;
        }      
        
        // Determine which card was clicked in the not-played list
        Object objs[] = player.newDevCards.toArray();
        ResourceCard rc;
        Point        p = evt.getPoint();
        for (int idx = objs.length-1; idx >= 0; idx--)
        {
            rc = (ResourceCard)objs[idx];
            if (rc.clicked(p) != false)
            {                
                if (rc.purchasedOnTurn >= player.gameRules.gameTurnNo)
                {
                    MessageJDialog m = new MessageJDialog(this.player.gameRules.gameWindow, true, false);                    
                    m.setText("You cannot play this card on\nthe turn you have purchased it", true);
                    m.centreToPanel(this);
                    m.setVisible(true);
                    break;
                }
                
                if (player.devCardPlayedOnTurn > 0)
                {
                    MessageJDialog m = new MessageJDialog(this.player.gameRules.gameWindow, true, false);                    
                    m.setText("You can only play 1 development card per turn", true);
                    m.centreToPanel(this);
                    m.setVisible(true);
                    break;
                }
                
                switch (rc.type)
                {
                    case DEV_ARMY:
                        {
                            // Move robber
                            MessageJDialog m = new MessageJDialog(this.player.gameRules.gameWindow, true, true);
                            m.setText("Do you want to move the robber?", true);
                            m.centreToPanel(this);
                            m.setVisible(true);

                            if (m.userYesNoSel != false)
                            {
                                player.usedDevCards.add(rc);
                                player.newDevCards.remove(rc); 
                                player.devCardPlayedOnTurn++;
                                player.calcVictoryPoints();
                                this.setEnabled(false);
                                player.gameRules.setGamePhase(GamePhaseTypes.DEV_ARMY);                                 
                            }
                        }
                        break;                        
                        
                    case DEV_MONOPOLY:
                        {
                            // Pick resource to appropriate from players
                            DevCardResourcePickerJDialog dcp = new DevCardResourcePickerJDialog (player.gameRules.gameWindow, true, player, DecCardResPickType.MONOPOLY);
                            dcp.centreToPanel(this);
                            dcp.setVisible(true);
                            
                            if (dcp.okSelection != false)                            
                            {
                                player.newDevCards.remove(rc);   
                                player.devCardPlayedOnTurn++;
                            }
                            this.updatePurchanseActions();
                            this.repaint();
                        }
                        break;
                        
                    case DEV_YEAROFPLENTY:
                        {
                            // Pick 2 resources from the bank
                            DevCardResourcePickerJDialog dcp = new DevCardResourcePickerJDialog (player.gameRules.gameWindow, true, player, DecCardResPickType.YEAROFPLENTY1);
                            dcp.centreToPanel(this);
                            dcp.setVisible(true);
                            
                            if (dcp.okSelection != false)    
                            {
                                player.newDevCards.remove(rc);                        
                                player.devCardPlayedOnTurn++;
                            }
                            this.updatePurchanseActions();
                            this.repaint();
                        }                        
                        break;
                        
                    case DEV_ROADBUILD:
                        // Build 2 Roads
                        {
                            MessageJDialog m = new MessageJDialog(this.player.gameRules.gameWindow, true, true);
                            m.setText("Do you want to build 2 extra Roads?", true);
                            m.centreToPanel(this);
                            m.setVisible(true);

                            if (m.userYesNoSel != false)                        
                            {
                                player.newDevCards.remove(rc);
                                player.devCardPlayedOnTurn++;
                                this.setEnabled(false);
                                player.gameRules.setGamePhase(GamePhaseTypes.DEV_ROAD_BUILD_1);                        
                            }                                                           
                        }
                        break;
                                                
                    case DEV_VP_CHAPEL:
                    case DEV_VP_UNIVERSITY:
                    case DEV_VP_PALACE:
                    case DEV_VP_MARKET:
                    case DEV_VP_LIBRARY:
                        // additional victory point
                        player.usedDevCards.add(rc);
                        player.newDevCards.remove(rc);
                        player.devCardPlayedOnTurn++;
                        break;
                }
                
                this.repaint();
                break;
            }
        }                
    }
    catch (CatanEndGameException e)
    {}          
    }//GEN-LAST:event_formMouseClicked

    private void formMouseMoved (java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseMoved
    {//GEN-HEADEREND:event_formMouseMoved
        Object objs[] = ttRegionList.toArray();
               
        ResClickInfo rc;
        Point        p = evt.getPoint();
        boolean      showInfo = false;
        for (int idx = objs.length-1; idx >= 0; idx--)
        {
            rc = (ResClickInfo)objs[idx];            
            if (rc.clicked(p) != false)
            {
                this.setToolTipText(rc.type.toString());
                showInfo = true;
                break;
            }
        }          
    
        if (showInfo == false)
            this.setToolTipText(null);
    }//GEN-LAST:event_formMouseMoved
    
    @Override
    public JToolTip createToolTip()
    {
        return new JMultiLineToolTip();
    }
    
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator botSeparator;
    private javax.swing.JButton buildCity;
    private javax.swing.JButton buildRoad;
    private javax.swing.JButton buildSettlement;
    private javax.swing.JButton buyCard;
    private javax.swing.JButton cancelAction;
    private javax.swing.JButton showBuildInfo;
    private javax.swing.JButton tradeBuy;
    private javax.swing.JButton tradePort;
    private javax.swing.JButton tradeSell;
    // End of variables declaration//GEN-END:variables
    
}
