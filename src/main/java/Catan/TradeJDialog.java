package Catan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */
enum TradeTypes
{

    NULL, BUY, SELL, PORT
};

public class TradeJDialog extends javax.swing.JDialog implements ListCellRenderer
{

    public Player thisPlayer = null;
    public ResourceCard resCardDrawer = new ResourceCard(ResCardTypes.BRICK);
    public TradeTypes tradeType   = TradeTypes.NULL;
    public ResourceCard tradeThis = null;
    public boolean tradeSuccess   = false;
    public boolean compTrade      = false;
    public boolean hasTradeOffers = false;
    public Point   startPos       = null;
    
    private JLabel noOffers = new JLabel();
    
    static BufferedImage tradeArrowImgs[] = null;

    /** Creates new form TradeJDialog */
    public TradeJDialog(java.awt.Frame parent, boolean modal, JComponent centreToThis, Point startPos)
    {
        super(parent, modal);
        initComponents();

        if (tradeArrowImgs == null)
        {
            String files[] = {"/Catan/Resource/trade_sell_arrow.png",  
                              "/Catan/Resource/trade_buy_arrow.png",
                              "/Catan/Icons/icon_anchor.png"
                             };
            tradeArrowImgs = new BufferedImage[files.length];

            for (int idx = 0; idx < files.length; idx++)
            {
                try
                {
                    tradeArrowImgs[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx]));

                }
                catch (Exception e)
                {
                    System.err.println ("Failed loading image : "  + files[idx] + " : " + e.toString());
                    System.exit(1);
                }
            }            
        }

        // Create noOffer label
        noOffers.setFont(new java.awt.Font("Tahoma", 0, 18)); 
        noOffers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noOffers.setText("No Offers Made");
        traderList.removeAll();
        offersPanel.removeAll(); // Remove trading list from view                       
                            
        tradeBrick.setTypeResize(ResCardTypes.BRICK);
        tradeWheat.setTypeResize(ResCardTypes.WHEAT);
        tradeRock.setTypeResize(ResCardTypes.ROCK);
        tradeSheep.setTypeResize(ResCardTypes.SHEEP);
        tradeWood.setTypeResize(ResCardTypes.WOOD);

        portBrick.setTypeResize(ResCardTypes.BRICK);
        portWheat.setTypeResize(ResCardTypes.WHEAT);
        portRock.setTypeResize(ResCardTypes.ROCK);
        portSheep.setTypeResize(ResCardTypes.SHEEP);
        portWood.setTypeResize(ResCardTypes.WOOD);

        // Add custom List Item Rendering ...
        traderList.setCellRenderer(this);
        traderList.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseClicked(MouseEvent e)
            {
                JList lObj = (JList) e.getComponent();
                int index = lObj.locationToIndex(e.getPoint());

                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    if (e.getClickCount() == 2)
                    {
                        if (index >= 0)
                        {
                            ListModel dlm = lObj.getModel();
                            Object item = dlm.getElementAt(index);
                            lObj.ensureIndexIsVisible(index);
                            // System.out.println("Double clicked on " + item);
                            switch (tradeType)
                            {
                                case SELL:
                                    sellTradedResCardThis((TraderItem) item);
                                    break;

                                case BUY:
                                    buyTradedResCardThis((TraderItem) item);
                                    break;
                            }
                        }
                    }
                }
                else
                {
                    formMouseClicked(e);
                }
            }
        });

        tradeListScroller.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseClicked(MouseEvent e)
            {
                formMouseClicked(e);
            }
        });

        if (startPos != null)
        {
            if ((startPos.x >= 0) || (startPos.y >= 0))
                this.setLocation(startPos);
            else
                centreToPanel (centreToThis);
            
            this.startPos = startPos;
        }
        else
        {
            if (centreToThis == null)
                centre(parent);
            else
                centreToPanel (centreToThis);
        }
    }
    
    public void centre(Frame parent)
    {
        if (parent != null)
        {
            Point pp = parent.getLocationOnScreen();

            this.setLocation((pp.x + parent.getWidth() / 2) - (this.getWidth() / 2),
                    (pp.y + parent.getHeight() / 2) - (this.getHeight() / 2));
        }
    }

    public void centreToPanel (JComponent p)
    {
        if (p != null)
        {
            try
            {
                Point pp = p.getLocationOnScreen();
            
                this.setLocation((pp.x + p.getWidth()/2) - (this.getWidth()/2), 
                                 (pp.y + p.getHeight()/2) - (this.getHeight()/2));
            }
            catch (Exception e)
            {  
            }
        }
    }  
    
    public void assignThisPlayer(Player player, TradeTypes trade, boolean compTransaction)
    {
        tradeSuccess   = false;
        thisPlayer     = player;
        tradeType      = trade;
        compTrade      = compTransaction;
        tradeThis      = null;
        hasTradeOffers = false;
               
        forLabel.setText(" ");
        tradeButton.setText("Cancel");
        portPanel.setVisible(false);        

        // Clear selections and remove list from view 
        traderList.clearSelection();
        traderList.removeAll();
        offersPanel.removeAll();
        
        selectResJPanel.removeAll();
        selectResJPanel.add(tradeWheat);
        selectResJPanel.add(tradeSheep);
        selectResJPanel.add(tradeWood);
        selectResJPanel.add(tradeBrick);
        selectResJPanel.add(tradeRock);

        switch (tradeType)
        {
            case SELL:
                tradeBrick.tradeNum = 1;
                tradeWheat.tradeNum = 1;
                tradeWood.tradeNum = 1;
                tradeSheep.tradeNum = 1;
                tradeRock.tradeNum = 1;

                if (player.countResType(player.resCards, ResCardTypes.BRICK) > 0)
                {
                    tradeBrick.setVisible(true);
                }
                else
                {
                    tradeBrick.setVisible(false);
                    selectResJPanel.remove(tradeBrick);
                }

                if (player.countResType(player.resCards, ResCardTypes.WHEAT) > 0)
                {
                    tradeWheat.setVisible(true);
                }
                else
                {
                    tradeWheat.setVisible(false);
                    selectResJPanel.remove(tradeWheat);
                }

                if (player.countResType(player.resCards, ResCardTypes.WOOD) > 0)
                {
                    tradeWood.setVisible(true);
                }
                else
                {
                    tradeWood.setVisible(false);
                    selectResJPanel.remove(tradeWood);
                }

                if (player.countResType(player.resCards, ResCardTypes.SHEEP) > 0)
                {
                    tradeSheep.setVisible(true);
                }
                else
                {
                    tradeSheep.setVisible(false);
                    selectResJPanel.remove(tradeSheep);
                }

                if (player.countResType(player.resCards, ResCardTypes.ROCK) > 0)
                {
                    tradeRock.setVisible(true);
                }
                else
                {
                    tradeRock.setVisible(false);
                    selectResJPanel.remove(tradeRock);
                }

                this.setTitle("Trade and Exchange (Selling)");
                buySellLabel.setText("Sell");
                break;

            case BUY:
                tradeBrick.tradeNum = 1;
                tradeWheat.tradeNum = 1;
                tradeWood.tradeNum = 1;
                tradeSheep.tradeNum = 1;
                tradeRock.tradeNum = 1;

                this.setTitle("Trade and Exchange (Buying)");
                tradeBrick.setVisible(true);
                tradeWheat.setVisible(true);
                tradeWood.setVisible(true);
                tradeSheep.setVisible(true);
                tradeRock.setVisible(true);
                buySellLabel.setText("Buy");
                break;

            case PORT:
                tradeBrick.tradeNum = 4;
                tradeWheat.tradeNum = 4;
                tradeWood.tradeNum = 4;
                tradeSheep.tradeNum = 4;
                tradeRock.tradeNum = 4;

                this.setTitle("Trade and Exchange (Port Trade)");
                buySellLabel.setText("Trade");

                // determine  which ports this person has.
                for (BuildPoint bp : player.builtObjs)
                {
                    if (bp.isPort != false)
                    {
                        switch (bp.portType)
                        {
                            case PORT_ANY_3TO1:
                                if (tradeBrick.tradeNum > 3)
                                {
                                    tradeBrick.tradeNum = 3;
                                }
                                if (tradeWheat.tradeNum > 3)
                                {
                                    tradeWheat.tradeNum = 3;
                                }
                                if (tradeWood.tradeNum > 3)
                                {
                                    tradeWood.tradeNum = 3;
                                }
                                if (tradeSheep.tradeNum > 3)
                                {
                                    tradeSheep.tradeNum = 3;
                                }
                                if (tradeRock.tradeNum > 3)
                                {
                                    tradeRock.tradeNum = 3;
                                }
                                break;

                            case PORT_BRICK_2TO1:
                                tradeBrick.tradeNum = 2;
                                break;
                            case PORT_ROCK_2TO1:
                                tradeRock.tradeNum = 2;
                                break;
                            case PORT_WHEAT_2TO1:
                                tradeWheat.tradeNum = 2;
                                break;
                            case PORT_WOOD_2TO1:
                                tradeWood.tradeNum = 2;
                                break;
                            case PORT_SHEEP_2TO1:
                                tradeSheep.tradeNum = 2;
                                break;
                        }
                    }
                }

                // determine which resources they have available to trade ...                
                if (player.countResType(player.resCards, ResCardTypes.BRICK) >= tradeBrick.tradeNum)
                {
                    tradeBrick.setVisible(true);
                }
                else
                {
                    tradeBrick.setVisible(false);
                    selectResJPanel.remove(tradeBrick);
                }

                if (player.countResType(player.resCards, ResCardTypes.WHEAT) >= tradeWheat.tradeNum)
                {
                    tradeWheat.setVisible(true);
                }
                else
                {
                    tradeWheat.setVisible(false);
                    selectResJPanel.remove(tradeWheat);
                }

                if (player.countResType(player.resCards, ResCardTypes.WOOD) >= tradeWood.tradeNum)
                {
                    tradeWood.setVisible(true);
                }
                else
                {
                    tradeWood.setVisible(false);
                    selectResJPanel.remove(tradeWood);
                }

                if (player.countResType(player.resCards, ResCardTypes.SHEEP) >= tradeSheep.tradeNum)
                {
                    tradeSheep.setVisible(true);
                }
                else
                {
                    tradeSheep.setVisible(false);
                    selectResJPanel.remove(tradeSheep);
                }

                if (player.countResType(player.resCards, ResCardTypes.ROCK) >= tradeRock.tradeNum)
                {
                    tradeRock.setVisible(true);
                }
                else
                {
                    tradeRock.setVisible(false);
                    selectResJPanel.remove(tradeRock);
                }
                break;
        }

        this.repaint();
    }

    public boolean canTrade()
    {
        if (tradeSheep.isVisible() != false)
        {
            return true;
        }
        if (tradeWheat.isVisible() != false)
        {
            return true;
        }
        if (tradeBrick.isVisible() != false)
        {
            return true;
        }
        if (tradeWood.isVisible() != false)
        {
            return true;
        }
        if (tradeRock.isVisible() != false)
        {
            return true;
        }
        return false;
    }

    public void setLog(String l)
    {
        if (thisPlayer != null)
        {
            thisPlayer.gameRules.setLog(l);
        }
    }

    public ResourceCard getTradingCardByType(ResCardTypes type)
    {
        switch (type)
        {
            case WHEAT:
                return tradeWheat;
            case SHEEP:
                return tradeSheep;
            case ROCK:
                return tradeRock;
            case WOOD:
                return tradeWood;
            case BRICK:
                return tradeBrick;
        }

        return null;
    }

    public void tradeResCardThis(ResourceCard             tradeThis,
                                 Player                   toThisPlayer, 
                                 LinkedList<ResourceCard> toThisPlayerTradeList)
    {
        tradeBrick.setVisible(false);
        tradeWheat.setVisible(false);
        tradeWood.setVisible(false);
        tradeSheep.setVisible(false);
        tradeRock.setVisible(false);

        this.tradeThis = tradeThis;

        selectResJPanel.removeAll();
        selectResJPanel.add(tradeThis);
        tradeThis.setVisible(true);                

        switch (tradeType)
        {
            case SELL:
                {
                    if (compTrade == false)
                    {
                        // determine selling mode, 1 -> 1, or 1 -> Many                  
                        LinkedList<Player> tradePlyrs = null;
                        if (thisPlayer.gameRules.thisPlayer == thisPlayer) // 1 -> many

                        {
                            thisPlayer.Debug("TradeJDialog.tradeResCardThis : SELL 1 -> Many", DebugLevel.COMPLETE);
                            tradePlyrs = thisPlayer.gameRules.players;
                        }
                        else
                        {
                            thisPlayer.Debug("TradeJDialog.tradeResCardThis : SELL 1 -> 1 (" + thisPlayer.gameRules.thisPlayer.name + ")", DebugLevel.COMPLETE);
                            tradePlyrs = new LinkedList<Player>();
                            tradePlyrs.add(thisPlayer.gameRules.thisPlayer);
                        }

                        LinkedList<TraderItem> tradeItems = new LinkedList<TraderItem>();
                        for (Player p : tradePlyrs)
                        {
                            if (p == thisPlayer)
                            {
                                continue;
                            }
                            if (p.type == PlayerTypes.HUMAN)
                            {
                                continue;
                            }
                            p.COMP_Trade_Sell(thisPlayer, tradeThis, tradeItems);
                        }
                        
                        if (tradeItems.size() > 0)
                        {
                            forLabel.setText("For");
                            traderList.setListData(tradeItems.toArray());
                            offersPanel.add(tradeListScroller, java.awt.BorderLayout.CENTER);    
                            hasTradeOffers = true;
                        }
                        else
                        {   
                            offersPanel.add(noOffers, java.awt.BorderLayout.CENTER);    
                            hasTradeOffers = false;
                        }
                    }
                    else if ((toThisPlayer != null) && (toThisPlayerTradeList != null))
                    {
                        // Computer -> Player Trade
                        LinkedList<TraderItem> tradeItems = new LinkedList<TraderItem>();
                        for (ResourceCard rc : toThisPlayerTradeList)
                        {
                            if (tradeThis.type == rc.type)
                            {
                                continue; // don't trade the same type

                            // Dont add duplicate type resource cards.
                            }
                            boolean addItem = true;
                            for (TraderItem ti : tradeItems)
                            {
                                if (toThisPlayer.countResType(ti.tradeItems, rc.type) > 0)
                                {
                                    addItem = false;
                                    break;
                                }
                            }
                            if (addItem != false)
                            {
                                TraderItem ti = new TraderItem();
                                ti.owner = toThisPlayer;
                                ti.tradeItems.add(rc);
                                tradeItems.add(ti);
                            }
                        }

                        if (tradeItems.size() > 0)
                        {                            
                            forLabel.setText("For");
                            traderList.setListData(tradeItems.toArray());                         
                            offersPanel.add(tradeListScroller, java.awt.BorderLayout.CENTER);    
                            hasTradeOffers = true;
                        }
                        else
                        {   
                            offersPanel.add(noOffers, java.awt.BorderLayout.CENTER);    
                            hasTradeOffers = false;
                        }
                    }
                }
                break;

            case BUY:
                {
                    if (compTrade == false)
                    {
                        // determine selling mode, 1 -> 1, or 1 -> Many
                        LinkedList<Player> tradePlyrs = null;
                        if (thisPlayer.gameRules.thisPlayer == thisPlayer) // 1 -> many

                        {
                            thisPlayer.Debug("TradeJDialog.tradeResCardThis : BUY 1 -> Many", DebugLevel.COMPLETE);
                            tradePlyrs = thisPlayer.gameRules.players;
                        }
                        else
                        {
                            thisPlayer.Debug("TradeJDialog.tradeResCardThis : BUY 1 -> 1 (" + thisPlayer.gameRules.thisPlayer.name + ")", DebugLevel.COMPLETE);
                            tradePlyrs = new LinkedList<Player>();
                            tradePlyrs.add(thisPlayer.gameRules.thisPlayer);
                        }

                        LinkedList<TraderItem> tradeItems = new LinkedList<TraderItem>();
                        for (Player p : tradePlyrs)
                        {
                            if (p == thisPlayer)
                            {
                                continue;
                            }
                            if (p.type == PlayerTypes.HUMAN)
                            {
                                continue;
                            }
                            p.COMP_Trade_Buy(thisPlayer, tradeThis, tradeItems);
                        }
                        
                        if (tradeItems.size() > 0)
                        {
                            forLabel.setText("For");
                            traderList.setListData(tradeItems.toArray());
                            offersPanel.add(tradeListScroller, java.awt.BorderLayout.CENTER);    
                            hasTradeOffers = true;
                        }
                        else
                        {   
                            offersPanel.add(noOffers, java.awt.BorderLayout.CENTER);    
                            hasTradeOffers = false;
                        }                        
                    }
                    else 
                    {
                        // Computer buying is not supported as the SELLING operation does the same thing.
                    }
                }
                break;

            case PORT:
                portPanel.setVisible(true);
                forLabel.setText("For");
                break;
        }
        
        this.repaint();
    }
    
    public boolean hasTradeItemsToTrade ()
    {
        return hasTradeOffers;
    }

    public void buyTradedResCardThis(TraderItem forThis)
    {
        String s = thisPlayer.name + " traded " + tradeThis.type.toString() + " for";

        // Add resources from other player to this player
        thisPlayer.resCards.add(new ResourceCard(tradeThis.type));
        forThis.owner.resCards.addAll(forThis.tradeItems);

        // Remove this players resources
        for (ResourceCard rc : forThis.tradeItems)
        {
            s += " ";
            thisPlayer.delResType(rc.type, 1);
            s += rc.type.toString();
        }
        
        s += " from " + forThis.owner.name;
                
        forThis.owner.delResType(tradeThis.type, 1);
        setLog(s);
        tradeSuccess = true;
        this.dispose();
    }

    public void sellTradedResCardThis(TraderItem forThis)
    {
        if ((thisPlayer == null) || (forThis == null))
            return;
        
        String s = thisPlayer.name + " traded " + tradeThis.type.toString() + " for";

        // Add resources from player to other player
        thisPlayer.resCards.addAll(forThis.tradeItems);

        // Remove other player's resource
        for (ResourceCard rc : forThis.tradeItems)
        {
            s += " ";
            forThis.owner.delResType(rc.type, 1);
            s += rc.type.toString();
        }

        s += " from " + forThis.owner.name;
        
        forThis.owner.resCards.add(new ResourceCard(tradeThis.type));
        thisPlayer.delResType(tradeThis.type, 1);
        setLog(s);
        tradeSuccess = true;
        this.dispose();
    }

    public void portTradedResCardThis(ResourceCard forThis)
    {
        setLog(thisPlayer.name + " port trade " + tradeThis.type.toString() + " for " + forThis.type.toString());

        // remove resources from player...
        thisPlayer.delResType(tradeThis.type, tradeThis.tradeNum);

        // add wanted resource...
        thisPlayer.resCards.add(new ResourceCard(forThis.type));
        tradeSuccess = true;
        this.dispose();
    }

    // Implement Custom List Item Rendering ....
    public Component getListCellRendererComponent(final JList list, final Object value, int index, final boolean isSelected, boolean cellHasFocus)
    {
        return new JPanel()
        {

            @Override
            public void paintComponent(Graphics g)
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                TraderItem ti = (TraderItem) value;
                // Draw the high light Bar
                this.setBackground(isSelected ? Color.black : Color.white);
                this.setForeground(isSelected ? Color.white : Color.black);
                super.paintComponent(g);

                int x = 10;

                // Add our Custom Drawing ... 
                BufferedImage bi = ti.owner.getPlayerImage();
                int newHeight = this.getHeight() - 10;
                float reduce = (float) newHeight / (float) bi.getHeight();
                int newWidth = (int) ((float) bi.getWidth() * reduce);

                g2.drawImage(bi, x, 5, newWidth, newHeight, null);
                x += 5 + newWidth;

                for (ResourceCard rc : ti.tradeItems)
                {
                    rc.drawAt(null, g, x, 5, false, 0);
                    x += (rc.getWidth() / 4);
                }

                if (isSelected != false)
                {
                    tradeButton.setText("Trade");
                }
            }

            @Override
            public Dimension getPreferredSize()
            {
                Insets inset = list.getInsets();
                return new Dimension(list.getWidth() - inset.left - inset.right, portBrick.getHeight() + 10);
            }
        };
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if (thisPlayer != null)
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Paint this traders icon        
            Insets        inset          = this.getInsets();
            BufferedImage bi             = thisPlayer.getPlayerImage();
            int           plyrIconHeight = 47;
            float         reduce         = (float) plyrIconHeight / (float) bi.getHeight();
            int           plyrIconWidth  = (int) ((float) bi.getWidth() * reduce);
            int           xPos           = 0;

            g2.drawImage(bi, inset.left + 10, inset.top + 5, plyrIconWidth, plyrIconHeight, null);
                                        
            if (this.tradeType != TradeTypes.PORT)
            {
                // Draw group other players we are trading with
                LinkedList<Player> tradePlyrs = new LinkedList<Player>();
                if (compTrade == false)
                {
                    // determine selling mode, 1 -> 1, or 1 -> Many                  
                    if (thisPlayer.gameRules.thisPlayer == thisPlayer) // 1 -> many

                    {
                        tradePlyrs = thisPlayer.gameRules.players;
                    }
                    else
                    {
                        tradePlyrs.add(thisPlayer.gameRules.thisPlayer);
                    }
                }
                else
                {
                    tradePlyrs.add(thisPlayer.gameRules.thisPlayer);
                }
                int othrsHeight = 24;
                reduce = (float) othrsHeight / (float) bi.getHeight();
                int othrsWidth = (int) ((float) bi.getWidth() * reduce);
                int idx = 1;
                
                xPos = (inset.left + 45) - (int) ((float) othrsWidth * ((float) tradePlyrs.size() / 2));

                for (Player tp : tradePlyrs)
                {
                    if ((tp == thisPlayer) && (compTrade == false))
                    {
                        continue;
                    }
                    bi = tp.getPlayerImage();
                    g2.drawImage(bi, xPos + (othrsWidth * idx), inset.top + 47, othrsWidth, othrsHeight, null);
                    idx++;
                }
            }
            else
            {
                g2.drawImage(tradeArrowImgs[2],  inset.left + 50, inset.top + 47,  null); 
            }
            
            // display trading direction arrows...
            switch (this.tradeType)
            {
                case SELL:
                    g2.drawImage(tradeArrowImgs[0], 
                                 inset.left + 12, inset.top + 5 + plyrIconHeight,                                   
                                 null);                                                 
                    break;
                    
                case BUY:
                    g2.drawImage(tradeArrowImgs[1], 
                                 inset.left + 12, inset.top + 5 + plyrIconHeight,                                     
                                 null);                           
                    break;                    
            }                        
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectResJPanel = new javax.swing.JPanel();
        tradeWheat = new Catan.ResourceCard();
        tradeSheep = new Catan.ResourceCard();
        tradeWood = new Catan.ResourceCard();
        tradeBrick = new Catan.ResourceCard();
        tradeRock = new Catan.ResourceCard();
        portPanel = new javax.swing.JPanel();
        portWheat = new Catan.ResourceCard();
        portSheep = new Catan.ResourceCard();
        portWood = new Catan.ResourceCard();
        portBrick = new Catan.ResourceCard();
        portRock = new Catan.ResourceCard();
        forLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        tradeButton = new javax.swing.JButton();
        buySellLabel = new javax.swing.JLabel();
        offersPanel = new javax.swing.JPanel();
        tradeListScroller = new javax.swing.JScrollPane();
        traderList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("");
        setName("Sell Resouces To Other Players"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        selectResJPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tradeWheat.setMinimumSize(tradeWheat.getPreferredSize());
        tradeWheat.setPreferredSize(tradeWheat.getPreferredSize());
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

        tradeSheep.setMinimumSize(tradeSheep.getPreferredSize());
        tradeSheep.setPreferredSize(getPreferredSize());
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

        tradeWood.setMinimumSize(tradeWood.getPreferredSize());
        tradeWood.setPreferredSize(getPreferredSize());
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

        tradeBrick.setMinimumSize(tradeBrick.getPreferredSize());
        tradeBrick.setPreferredSize(getPreferredSize());
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

        tradeRock.setMinimumSize(tradeRock.getPreferredSize());
        tradeRock.setPreferredSize(getPreferredSize());
        tradeRock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tradeRockMousePressed(evt);
            }
        });
        selectResJPanel.add(tradeRock);

        portWheat.setMinimumSize(tradeWheat.getPreferredSize());
        portWheat.setPreferredSize(tradeWheat.getPreferredSize());
        portWheat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                portWheatMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout portWheatLayout = new org.jdesktop.layout.GroupLayout(portWheat);
        portWheat.setLayout(portWheatLayout);
        portWheatLayout.setHorizontalGroup(
            portWheatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        portWheatLayout.setVerticalGroup(
            portWheatLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        portPanel.add(portWheat);

        portSheep.setMinimumSize(tradeSheep.getPreferredSize());
        portSheep.setPreferredSize(getPreferredSize());
        portSheep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                portSheepMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout portSheepLayout = new org.jdesktop.layout.GroupLayout(portSheep);
        portSheep.setLayout(portSheepLayout);
        portSheepLayout.setHorizontalGroup(
            portSheepLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        portSheepLayout.setVerticalGroup(
            portSheepLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        portPanel.add(portSheep);

        portWood.setMinimumSize(tradeWood.getPreferredSize());
        portWood.setPreferredSize(getPreferredSize());
        portWood.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                portWoodMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout portWoodLayout = new org.jdesktop.layout.GroupLayout(portWood);
        portWood.setLayout(portWoodLayout);
        portWoodLayout.setHorizontalGroup(
            portWoodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        portWoodLayout.setVerticalGroup(
            portWoodLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        portPanel.add(portWood);

        portBrick.setMinimumSize(tradeBrick.getPreferredSize());
        portBrick.setPreferredSize(getPreferredSize());
        portBrick.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                portBrickMousePressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout portBrickLayout = new org.jdesktop.layout.GroupLayout(portBrick);
        portBrick.setLayout(portBrickLayout);
        portBrickLayout.setHorizontalGroup(
            portBrickLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 37, Short.MAX_VALUE)
        );
        portBrickLayout.setVerticalGroup(
            portBrickLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 47, Short.MAX_VALUE)
        );

        portPanel.add(portBrick);

        portRock.setMinimumSize(tradeRock.getPreferredSize());
        portRock.setPreferredSize(getPreferredSize());
        portRock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                portRockMousePressed(evt);
            }
        });
        portPanel.add(portRock);

        forLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forLabel.setText(" ");

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        jPanel1.add(resetButton);

        tradeButton.setText("Cancel");
        tradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeButtonActionPerformed(evt);
            }
        });
        jPanel1.add(tradeButton);

        buySellLabel.setText("      ");

        offersPanel.setLayout(new java.awt.BorderLayout());

        tradeListScroller.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        traderList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tradeListScroller.setViewportView(traderList);

        offersPanel.add(tradeListScroller, java.awt.BorderLayout.CENTER);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(portPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .add(forLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(41, 41, 41)
                .add(buySellLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(selectResJPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
            .add(offersPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(selectResJPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(buySellLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(forLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(portPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(offersPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-348)/2, (screenSize.height-404)/2, 348, 404);
    }// </editor-fold>//GEN-END:initComponents

    private void tradeWheatMouseClicked (java.awt.event.MouseEvent evt)//GEN-FIRST:event_tradeWheatMouseClicked
    {//GEN-HEADEREND:event_tradeWheatMouseClicked
        if ((tradeWheat.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.tradeResCardThis(tradeWheat, null, null);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
            formMouseClicked(evt);
    }//GEN-LAST:event_tradeWheatMouseClicked

    private void tradeSheepMouseClicked (java.awt.event.MouseEvent evt)//GEN-FIRST:event_tradeSheepMouseClicked
    {//GEN-HEADEREND:event_tradeSheepMouseClicked
        if ((tradeSheep.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.tradeResCardThis(tradeSheep, null, null);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
    }//GEN-LAST:event_tradeSheepMouseClicked

    private void tradeWoodMouseClicked (java.awt.event.MouseEvent evt)//GEN-FIRST:event_tradeWoodMouseClicked
    {//GEN-HEADEREND:event_tradeWoodMouseClicked
        if ((tradeWood.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.tradeResCardThis(tradeWood, null, null);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
    }//GEN-LAST:event_tradeWoodMouseClicked

    private void tradeBrickMousePressed (java.awt.event.MouseEvent evt)//GEN-FIRST:event_tradeBrickMousePressed
    {//GEN-HEADEREND:event_tradeBrickMousePressed
        if ((tradeBrick.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.tradeResCardThis(tradeBrick, null, null);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
    }//GEN-LAST:event_tradeBrickMousePressed

    private void tradeRockMousePressed (java.awt.event.MouseEvent evt)//GEN-FIRST:event_tradeRockMousePressed
    {//GEN-HEADEREND:event_tradeRockMousePressed
        if ((tradeRock.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.tradeResCardThis(tradeRock, null, null);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
    }//GEN-LAST:event_tradeRockMousePressed

    private void portWheatMouseClicked (java.awt.event.MouseEvent evt)//GEN-FIRST:event_portWheatMouseClicked
    {//GEN-HEADEREND:event_portWheatMouseClicked
        if ((portWheat.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.portTradedResCardThis(portWheat);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
}//GEN-LAST:event_portWheatMouseClicked

    private void portSheepMouseClicked (java.awt.event.MouseEvent evt)//GEN-FIRST:event_portSheepMouseClicked
    {//GEN-HEADEREND:event_portSheepMouseClicked
        if ((portSheep.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.portTradedResCardThis(portSheep);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
}//GEN-LAST:event_portSheepMouseClicked

    private void portWoodMouseClicked (java.awt.event.MouseEvent evt)//GEN-FIRST:event_portWoodMouseClicked
    {//GEN-HEADEREND:event_portWoodMouseClicked
        if ((portWood.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.portTradedResCardThis(portWood);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
}//GEN-LAST:event_portWoodMouseClicked

    private void portBrickMousePressed (java.awt.event.MouseEvent evt)//GEN-FIRST:event_portBrickMousePressed
    {//GEN-HEADEREND:event_portBrickMousePressed
        if ((portBrick.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.portTradedResCardThis(portBrick);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
}//GEN-LAST:event_portBrickMousePressed

    private void portRockMousePressed (java.awt.event.MouseEvent evt)//GEN-FIRST:event_portRockMousePressed
    {//GEN-HEADEREND:event_portRockMousePressed
        if ((portRock.isVisible() != false) && (evt.getButton() == MouseEvent.BUTTON1))
        {
            this.portTradedResCardThis(portRock);
        // Right click to reset transaction...
        }
        if ((evt.getButton() == MouseEvent.BUTTON2) || (evt.getButton() == MouseEvent.BUTTON3))
        {
            formMouseClicked(evt);
        }
}//GEN-LAST:event_portRockMousePressed

private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
    if (compTrade != false)
    {
        tradeButton.setText("Cancel");
        this.traderList.clearSelection();
        this.traderList.removeAll();        
    }
    else
    {
        this.assignThisPlayer(thisPlayer, this.tradeType, compTrade);
    }
}//GEN-LAST:event_resetButtonActionPerformed

private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

    int minX = 340;
    int minY = 375;

    Dimension size = getSize();
    int newWidth = size.width;
    int newHeight = size.height;
    if (size.width < minX)
    {
        newWidth = minX;
    }
    if (size.height < minY)
    {
        newHeight = minY;
    }
    setSize(newWidth, newHeight);

    this.repaint();
}//GEN-LAST:event_formComponentResized

private void tradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeButtonActionPerformed

    TraderItem item = (TraderItem) this.traderList.getSelectedValue();

    if (item != null)
    {
        switch (tradeType)
        {
            case SELL:
                sellTradedResCardThis((TraderItem) item);
                break;

            case BUY:
                buyTradedResCardThis((TraderItem) item);
                break;
        }
    }

    this.dispose();
}//GEN-LAST:event_tradeButtonActionPerformed

    @Override
    public void dispose()
    {
        if (startPos != null)
        {
            // Save the setting back to the passed obj
            startPos.x = this.getLocation().x;
            startPos.y = this.getLocation().y;
        }
        super.dispose();
    }

private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

    // If right click, reset buy/sell operation.
    if ((evt.getClickCount() > 0) &&
            ((evt.getButton() == MouseEvent.BUTTON3) || (evt.getButton() == MouseEvent.BUTTON2)))
    {
        if (compTrade != false)
        {
            this.dispose();
        }
        else if (tradeThis != null)
        {
            offersPanel.remove (this.tradeListScroller);
            offersPanel.remove (this.noOffers);
            this.assignThisPlayer(thisPlayer, this.tradeType, compTrade);
        }
        else
        {
            this.dispose();
        }
    }
}//GEN-LAST:event_formMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel buySellLabel;
    private javax.swing.JLabel forLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel offersPanel;
    private Catan.ResourceCard portBrick;
    private javax.swing.JPanel portPanel;
    private Catan.ResourceCard portRock;
    private Catan.ResourceCard portSheep;
    private Catan.ResourceCard portWheat;
    private Catan.ResourceCard portWood;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel selectResJPanel;
    private Catan.ResourceCard tradeBrick;
    private javax.swing.JButton tradeButton;
    private javax.swing.JScrollPane tradeListScroller;
    private Catan.ResourceCard tradeRock;
    private Catan.ResourceCard tradeSheep;
    private Catan.ResourceCard tradeWheat;
    private Catan.ResourceCard tradeWood;
    private javax.swing.JList traderList;
    // End of variables declaration//GEN-END:variables
}
