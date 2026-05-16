package Catan;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import javax.imageio.ImageIO;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

enum ResCardTypes {NULL(0),      BRICK(1),         ROCK(2), WHEAT(3), WOOD(4), SHEEP(5),
                   DEV_ARMY(6),  DEV_MONOPOLY(7),  DEV_YEAROFPLENTY(8),    DEV_ROADBUILD(9),                   
                   DEV_VP_CHAPEL(10), DEV_VP_UNIVERSITY(11), DEV_VP_PALACE(12), DEV_VP_MARKET(13), DEV_VP_LIBRARY(14),
                   LARGEST_ARMY(15),  LONGEST_ROAD(16),
                   RES_CARDBACK(17),  DEV_CARDBACK(18);
                   private int value;
                   private ResCardTypes (int i) {value = i;}
                   public  int toValue () {return value;}
                   
                   @Override
                   public String toString ()
                   {
                       switch (this)
                       {
                           case NULL:             return "Null";
                           case BRICK:            return "Brick";
                           case ROCK:             return "Rock";
                           case WHEAT:            return "Wheat";
                           case WOOD:             return "Wood";
                           case SHEEP:            return "Sheep";
                           case DEV_ARMY:         return "Dev-Card: Army\nMove robber and\nsteal a player's card";
                           case DEV_MONOPOLY:     return "Dev-Card: Monopoly\nTake selected resource from all players";
                           case DEV_YEAROFPLENTY: return "Dev-Card: Year Of Plenty\nPick any 2 resources";
                           case DEV_ROADBUILD:    return "Dev-Card: Build Road\nAble to build 2 new roads";
                           case DEV_VP_CHAPEL:    return "Dev-Card: Chapel\n1 Additional Victory Point";
                           case DEV_VP_UNIVERSITY:return "Dev-Card: University\n1 Additional victory point";
                           case DEV_VP_PALACE:    return "Dev-Card: Palace\n1 Additional victory point";
                           case DEV_VP_MARKET:    return "Dev-Card: Market\n1 Additional victory point";
                           case DEV_VP_LIBRARY:   return "Dev-Card: Library\n1 Additional victory point";
                           case LARGEST_ARMY:     return "Largest Army\n3 or more army cards\n2 Additional victory points";
                           case LONGEST_ROAD:     return "Longest Road\n5 or more contiguous joined roads\n2 additional victory points";
                       }
                    
                       return super.toString();
                   }
                  };
public class ResourceCard extends javax.swing.JPanel
{
    static BufferedImage cardImgs[]     = null;   
    static BufferedImage cardImgsDark[] = null;
    static BufferedImage darkenImg      = null;


    public boolean      devCardPlayed   = false;
    public int          purchasedOnTurn = -1;
    public int          lastGameTurnNo  = 0;
    
    public ResCardTypes type            = ResCardTypes.NULL;
    int    tradeNum                     = 1; // used in port trade to identify number of cards to trade for.

    private void initImages ()
    {
        if (darkenImg == null)
        {
            try
            {
                darkenImg = ImageIO.read(getClass().getResourceAsStream("/Catan/Resource/darken_template.png"));
            }
            catch (Exception e)
            {
                System.err.println ("Failed loading shader image : " + e.toString());
                System.exit(1);
            }
        }

        if (cardImgs == null)
        {
            String files[] = {"", 
                              "/Catan/Resource/res_card_brick.png",         "/Catan/Resource/res_card_rock.png",
                              "/Catan/Resource/res_card_wheat.png",         "/Catan/Resource/res_card_wood.png",
                              "/Catan/Resource/res_card_sheep.png",         "/Catan/Resource/res_card_army.png",
                              "/Catan/Resource/res_card_monopoly.png",      "/Catan/Resource/res_card_yeat_of_plenty.png",
                              "/Catan/Resource/res_card_road_building.png", "/Catan/Resource/res_card_chapel_vp.png",
                              "/Catan/Resource/res_card_vp_university.png", "/Catan/Resource/res_card_vp_palace.png",
                              "/Catan/Resource/res_card_vp_market.png",     "/Catan/Resource/res_card_vp_library.png",
                              "/Catan/Resource/res_card_largest_army.png",  "/Catan/Resource/res_card_longest_road.png",
                              "/Catan/Resource/res_card_blank.png",         "/Catan/Resource/res_card_dev_blank.png"
                             };
                       
            cardImgs     = new BufferedImage[files.length];
            cardImgsDark = new BufferedImage[files.length];
           
            for (int idx = 0; idx < files.length; idx++)
            {
                if (files[idx].compareTo("") != 0)
                {
                    try
                    {
                        cardImgs[idx]     = ImageIO.read(getClass().getResourceAsStream(files[idx]));
                        cardImgsDark[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx]));

                        // Create a darken version of this card
                        int w = cardImgs[idx].getWidth();
                        int h = cardImgs[idx].getHeight();

                        // Create a darker version of this card
                        for (int i = 0; i < h; i += darkenImg.getHeight() )
                        {
                            cardImgsDark[idx].getGraphics().drawImage(darkenImg, 0, i, w, darkenImg.getHeight(), null);
                        }
                    }
                    catch (Exception e)
                    {
                        System.err.println ("Failed loading image : "  + files[idx] + " : " + e.toString());
                        System.exit(1);
                    }
                }
                else
                    cardImgs[idx] = null;
            }     
        }      
    }
    
    public void setTypeResize (ResCardTypes t)
    {
        type = t;
        BufferedImage bi = cardImgs[type.toValue()];        
        if (bi != null)
        {
            this.setSize(bi.getWidth(), bi.getHeight());
        }
        
    }
    
    public boolean clicked (Point p)
    {
        if ((p.x>=this.getX() && p.x <= this.getX()+this.getWidth()) &&
            (p.y>=this.getY() && p.y <= this.getY()+this.getHeight()))
        {
            return true;
        }               
        return false;
    }
    
    public ResourceCard ()
    {
        super();               
        initImages();       
    }

    public ResourceCard (ResCardTypes type)
    {   
        super();
        initImages();       
        this.type = type;
    }
   
    @Override
    public int getHeight()
    {
        BufferedImage bi = cardImgs[type.toValue()];
        if (bi != null)
            return bi.getHeight();
        
        return 0;        
    }
    
    @Override
    public int getWidth()
    {
        BufferedImage bi = cardImgs[type.toValue()];
        if (bi != null)
            return bi.getWidth();
        
        return 0;        
    }        
       
        
    @Override
    public Dimension getPreferredSize()
    {         
        Dimension d = new Dimension (37, 47);
        BufferedImage bi = cardImgs[type.toValue()];
        if (bi != null)
        {
            d.setSize(bi.getWidth() + ((tradeNum-1) * (bi.getWidth()/4)), bi.getHeight());
        }
        
        return d;
    }
  
    public void drawAt (Player p, Graphics g, int x, int y, boolean drawAsBlank, int gameTurnNo)
    {       
        BufferedImage bi = cardImgs[type.toValue()];

        if (gameTurnNo >= 0)
            this.lastGameTurnNo = gameTurnNo;
                
        if ((this.lastGameTurnNo == purchasedOnTurn) && (purchasedOnTurn >= 0))
        {
            bi = cardImgsDark[type.toValue()];                
        }

        // Not your turn, can't play dev cards!
        if ((p != null) && ((p.gameRules.thisPlayer != p) || (p.devCardPlayedOnTurn > 0)))
        {
            switch (type)            
            {
                case DEV_ARMY:
                case DEV_MONOPOLY:
                case DEV_YEAROFPLENTY:
                case DEV_ROADBUILD:
                    bi = cardImgsDark[type.toValue()];                                               
            }        
        }
        
        if (drawAsBlank != false)
        {
            switch (type)
            {
                case BRICK:
                case ROCK:
                case WHEAT:
                case WOOD:
                case SHEEP:
                    bi = cardImgs[ResCardTypes.RES_CARDBACK.toValue()];
                    break;  
                    
                case DEV_ARMY:
                case DEV_MONOPOLY:
                case DEV_YEAROFPLENTY:
                case DEV_ROADBUILD:
                case DEV_VP_CHAPEL:
                case DEV_VP_UNIVERSITY:
                case DEV_VP_PALACE:
                case DEV_VP_MARKET:
                case DEV_VP_LIBRARY:
                    bi = cardImgs[ResCardTypes.DEV_CARDBACK.toValue()];
            }
        }
                    
        if (bi != null)
        {
            Graphics2D g2 = (Graphics2D)g;            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);      
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            g2.drawImage(bi, x, y, null);
        }        
    } 
    
    // player p can be null, its used to dark dev card on non-active turns.
    public void drawAt (Graphics g, int x, int y, int width, int height, boolean drawAsBlank, int gameTurnNo)
    {       
        BufferedImage bi = cardImgs[type.toValue()];
        
        if (gameTurnNo >= 0)
            this.lastGameTurnNo = gameTurnNo;
        
               
        if (drawAsBlank != false)
        {
            switch (type)
            {
                case BRICK:
                case ROCK:
                case WHEAT:
                case WOOD:
                case SHEEP:
                    bi = cardImgs[ResCardTypes.RES_CARDBACK.toValue()];
                    break;  
                    
                case DEV_ARMY:
                case DEV_MONOPOLY:
                case DEV_YEAROFPLENTY:
                case DEV_ROADBUILD:
                case DEV_VP_CHAPEL:
                case DEV_VP_UNIVERSITY:
                case DEV_VP_PALACE:
                case DEV_VP_MARKET:
                case DEV_VP_LIBRARY:
                    bi = cardImgs[ResCardTypes.DEV_CARDBACK.toValue()];
            }
        }
        
        if (bi != null)
        {
            Graphics2D g2 = (Graphics2D)g;            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);      
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2.drawImage(bi, x, y, width, height, null);
        }
    }     
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
                
        BufferedImage bi = cardImgs[type.toValue()];           
        
        if (bi != null)
        {        
            int width = bi.getWidth() + ((tradeNum-1) * (bi.getWidth()/4));

            Graphics2D g2 = (Graphics2D)g;            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);      
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); 
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            g2.setClip(0, 0, width, this.getHeight());

            for (int x = 0; x < tradeNum; x++)
                g2.drawImage(bi, x * (bi.getWidth()/4), 0, null);
        }            
    }          
}
