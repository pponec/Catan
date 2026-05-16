package Catan;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

enum TileTypes  {NULL(0), 
                 SEA(1), BRICK(2), ROCK(3), WHEAT(4), WOOD(5), SHEEP(6), DESERT(7),
                 PORT_ANY_3TO1(8), 
                 PORT_BRICK_2TO1(9), PORT_ROCK_2TO1(10), PORT_WHEAT_2TO1(11), PORT_WOOD_2TO1(12), PORT_SHEEP_2TO1(13),
                 VAR_VOLCANO(14);
                 private int value;
                 private TileTypes (int i) {value = i;}                 
                 public  int toValue () {return value;}
                 };

public class Tile extends CatanGraphBase
{            
    static Color LandTileBG         = new Color (195,160,105);
    static Color SeaTileBG          = new Color (38, 35, 168);
    
    static BufferedImage tileNoImgs[] = null;
    static BufferedImage tileImgs[]   = null;  
    static BufferedImage robberImg    = null;
    public TileTypes  type            = TileTypes.NULL;
    public boolean    hasRobber       = false;
    public int        diceRoll        = 0;    // dice value 2 - 12    
    public int        diceRollScore   = 0;    // 1 - 5 = likely hood it will come up.
    public LinkedList<BuildPoint> buildJoins = new LinkedList<BuildPoint>(); // connected to 6 build points.
    public LinkedList<Road>       roadJoins  = new LinkedList<Road>();       // connected to 6 roads.
            
    Tile (Object paintAreaObj)
    {   
        super (paintAreaObj);        

        String file ="/Catan/Resource/robber.png";
        try
        {
            robberImg = ImageIO.read(getClass().getResourceAsStream(file));
        }
        catch (Exception e)
        {
            System.err.println ("Failed loading images : " + file + " : " + e.toString());
        }                    
        
        if (tileNoImgs == null)
        {
            String files[] = {"/Catan/Resource/tile_no_2.png", "/Catan/Resource/tile_no_3.png", "/Catan/Resource/tile_no_4.png",  "/Catan/Resource/tile_no_5.png",  "/Catan/Resource/tile_no_6.png", 
                              "/Catan/Resource/tile_no_8.png", "/Catan/Resource/tile_no_9.png", "/Catan/Resource/tile_no_10.png", "/Catan/Resource/tile_no_11.png", "/Catan/Resource/tile_no_12.png"};
                       
            tileNoImgs = new BufferedImage[files.length];
                       
            for (int idx = 0; idx < files.length; idx++)
            {
                try
                {
                    tileNoImgs[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx]));                        
                }
                catch (Exception e)
                {
                    System.err.println ("Failed loading image : " + files[idx] + " : " + e.toString());
                    System.exit(1);
                }                    
            }     
        }
        
        if (tileImgs == null)
        {
            String files[] = {"", 
            
                              "/Catan/Resource/tile_sea.png",       "/Catan/Resource/tile_brick.png",      "/Catan/Resource/tile_rock.png",  
                              "/Catan/Resource/tile_wheat.png",      "/Catan/Resource/tile_wood.png",       "/Catan/Resource/tile_sheep.png",
                              "/Catan/Resource/tile_desert.png", 
                              
                              "/Catan/Resource/tile_port_any.png",  "/Catan/Resource/tile_port_brick.png", "/Catan/Resource/tile_port_rock.png",  
                              "/Catan/Resource/tile_port_wheat.png", "/Catan/Resource/tile_port_wood.png",  "/Catan/Resource/tile_port_sheep.png",
                              "/Catan/Resource/tile_volcano.png" 
                             };
                       
            tileImgs  = new BufferedImage[files.length];
                       
            for (int idx = 0; idx < files.length; idx++)
            {
                if (files[idx].compareTo("") != 0)
                {
                    try
                    {
                        tileImgs[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx])); 
                        
                    }
                    catch (Exception e)
                    {
                        System.err.println ("Failed loading image : " + files[idx] + " : " + e.toString());
                        System.exit(1);
                    }                    
                }
                else
                    tileImgs[idx] = null;
            }     
        }              
    }       
    
    public BuildPoint getBuildPointJoin (int x, int y)
    {
        for (BuildPoint bp:buildJoins)
        {
            if (bp.contains(x, y) != false)
                return bp;
        }
        return null;
    }
    
    public void addBuildPointJoin (BuildPoint pt)
    {
        if (buildJoins.indexOf(pt) >=0)
            return;
        
        buildJoins.add(pt);
    }
        
    public void addRoadJoin (Road r)
    {
        if (roadJoins.indexOf(r) >=0)
            return;
        
        roadJoins.add(r);
    }
               
    public Point getCentre ()
    {        
        Rectangle2D r = this.getBounds2D();        
        return new Point ( (int)r.getCenterX(), (int)r.getCenterY() );        
    }
    
    public void paint (Graphics2D g2)
    {
        paint (g2,false);
    }
    
    public void drawHideBorder (Graphics2D g2, boolean drawSea, boolean detailLevelHigh)
    {
        Stroke   saveStroke = g2.getStroke();
        
        double width   = ((this.buildJoins.get(0)).getBounds2D()).getWidth();
        int    lnWidth = (int)(width/6.0);        
        
        g2.setStroke(new BasicStroke (lnWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));                                                               
 
        switch (type)
        {
            case BRICK:
            case ROCK:
            case WHEAT:
            case WOOD:
            case SHEEP:
            case DESERT:
                if (drawSea == false)
                {
                    if (detailLevelHigh == false)
                        g2.setColor(Color.black);
                    else
                        g2.setColor(LandTileBG);                    
                    g2.drawPolygon(this);                        
                }
                break;   
                
            case PORT_ANY_3TO1:
            case PORT_BRICK_2TO1:
            case PORT_ROCK_2TO1:
            case PORT_WHEAT_2TO1:
            case PORT_WOOD_2TO1:
            case PORT_SHEEP_2TO1:
                if (drawSea != false)
                {
                    if (detailLevelHigh == false)
                        g2.setColor(Color.black);
                    else
                        g2.setColor(SeaTileBG);                      
                    
                    g2.drawPolygon(this);                        
                }
        }   
        
        g2.setStroke(saveStroke);
    }
    
    public void drawPorts  (Graphics2D g2, boolean detailLevelHigh)
    {
        switch (type)
        {
            case PORT_ANY_3TO1:
            case PORT_BRICK_2TO1:
            case PORT_ROCK_2TO1:
            case PORT_WHEAT_2TO1:
            case PORT_WOOD_2TO1:
            case PORT_SHEEP_2TO1:
                for (BuildPoint bp:buildJoins)
                {
                    if (bp.isPort != false)
                    {   
                        double width   = (bp.getBounds2D()).getWidth();
                        int    lnWidth = (int)(width/2.0);

                        g2.setColor(Color.black);
                        g2.setStroke(new BasicStroke (lnWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));

                        Point  p = bp.getCentrePoint();
                        g2.drawLine (p.x, p.y, p.x, p.y);                                
                    }
                }            

        }        
    }
    
    public void highLightSelectedDraw (Graphics2D g2, Color c)
    {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
                        
        int w = (int)(this.getBounds()).getWidth();
                        
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float) 0.4));
        
        Point p = this.getCentre();
        
        g2.setColor (c);
        g2.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (p.x, p.y, p.x, p.y);        
        
        
        int r = c.getRed()   - 20;               
        int g = c.getGreen() - 20;
        int b = c.getBlue()  - 20;
        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;        
        
        w = (int)((this.getBounds()).getWidth() * 0.6);
        g2.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (p.x, p.y, p.x, p.y);    
        
        g2.setComposite(originalComposite);
    }
    
    public void paint (Graphics2D g2, boolean detailLevelHigh)
    {            
        
        int         dRoll  = diceRoll;
        Point       p      = this.getCentre();
        Rectangle2D rect   = this.getBounds2D();  
        String      t      = "";
        Color       c      = null; 
        Color       txtCol = null;          
     
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); 
            
        switch (type)
        {
            case BRICK:
                t      = "Brick";
                c      = new Color(143,95,75);
                txtCol = Color.white;
                break;
                
            case ROCK:
                t      = "Rock";
                c      = new Color(128, 128, 128);
                txtCol = Color.white;
                break;
                
            case WHEAT:
                t      = "Wheat";
                c      = new Color(255,255,0);
                txtCol = Color.black;
                break;
                
            case WOOD:
                t      = "Wood";
                c      = new Color(87,138,50);
                txtCol = Color.white;
                break;
                
            case SHEEP:
                t      = "Sheep";
                c      = new Color(183,255, 183);
                txtCol = Color.black;
                break;
                
            case DESERT:
                t      = "Desert"; 
                c      = new Color(128,128, 0);
                txtCol = Color.black;
                break;   
                
            case VAR_VOLCANO:
                t      = "Volcano"; 
                c      = new Color(225,0,225);
                txtCol = Color.black;
                break;                
                
            case PORT_ANY_3TO1:
                t      = "?"; 
                c      = new Color(192,192, 192);
                txtCol = Color.black;
                dRoll  = -3;
                break;
                
            case PORT_BRICK_2TO1:
                t      = "Brick"; 
                c      = new Color(192,192, 192);
                txtCol = Color.black;
                dRoll  = -2;                
                break;
                
                
            case PORT_ROCK_2TO1:
                t      = "Rock"; 
                c      = new Color(192,192, 192);
                txtCol = Color.black;
                dRoll  = -2;
                break;

            case PORT_WHEAT_2TO1:
                t      = "Wheat";
                c      = new Color(192,192, 192);
                txtCol = Color.black;
                dRoll  = -2;
                break;
                
            case PORT_WOOD_2TO1:
                t      = "Wood"; 
                c      = new Color(192,192, 192);
                txtCol = Color.black;
                dRoll  = -2;
                break;

            case PORT_SHEEP_2TO1:
                t      = "Sheep"; 
                c      = new Color(192,192, 192);
                txtCol = Color.black;
                dRoll  = -2;
                break;
                                
            default:
                return;
                //c = new Color(192,192, 255);
        }   
        
        // Set display text loc
        int yTxtDiff = (g2.getFontMetrics().getHeight()) / 2;
        int xTxtDiff = (g2.getFontMetrics().charsWidth(t.toCharArray(), 0, t.length())) / 2;            
        
        // --------------------------------------------------------
        
        // Draw basic 
        if (detailLevelHigh == false)
        {
            g2.setColor(c);
            g2.fillPolygon(this);   
            
            g2.setColor(Color.black);
            g2.drawPolygon(this);    
                   
            g2.setColor(Color.black);            
            g2.drawString(t, p.x-xTxtDiff, p.y-yTxtDiff);                        
        }
        else
        {        
            // Draw background tile image ...
            switch (type)
            {
                case PORT_ANY_3TO1:
                case PORT_BRICK_2TO1:
                case PORT_ROCK_2TO1:
                case PORT_WHEAT_2TO1:
                case PORT_WOOD_2TO1:
                case PORT_SHEEP_2TO1:                    
                    if (tileImgs[TileTypes.SEA.toValue()] != null)
                        g2.drawImage(tileImgs[TileTypes.SEA.toValue()], (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight(), null);            
                    
                    // Display port build area lines.
                    for (BuildPoint bp:buildJoins)
                    {
                        if (bp.isPort != false)
                        {                                                
                            Point p1       = bp.getCentrePoint();
                            double width   = (bp.getBounds2D()).getWidth();
                            int    lnWidth = (int)(width/5.0);

                            g2.setColor(Color.black);
                            float [] dash = {10.0F, 13.0F, 13.0F, 13.0F };                                 
                            Stroke   saveStroke = g2.getStroke();
                            g2.setStroke(new BasicStroke (lnWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0F, dash, 0.F));                                   
                            g2.drawLine (p1.x, p1.y, (int)rect.getCenterX(), (int)rect.getCenterY());   
                            g2.setStroke(saveStroke);
                        }
                    }                      
                    
                    if (tileImgs[type.toValue()] != null)
                    {     
                        int x = (int)(rect.getCenterX() - (rect.getWidth()  / 4));                        
                        int y = (int)(rect.getCenterY() - (rect.getHeight() / 4));
                        int w = (int)(rect.getWidth()  / 2);
                        int h = (int)(rect.getHeight() / 2);                      
                        
                        g2.drawImage(tileImgs[type.toValue()], x, y, w, h, null);
                    }
                    else
                    {                    
                        g2.setColor(txtCol);
                        g2.drawString(t, p.x-xTxtDiff, p.y-yTxtDiff);                    
                    }
                    break;

                default:
                    if (tileImgs[type.toValue()] != null)
                    {                                                  
                        g2.drawImage(tileImgs[type.toValue()], (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight(), null);            
                    }
            }                                      
        }      
        
        // Draw Volcano destruction numbers
        if (type == TileTypes.VAR_VOLCANO)
        {
            // Only display numbers when tiles is at the larger size 
            if (((rect.getWidth() >= 100) && (detailLevelHigh != false)) ||
                (detailLevelHigh == false))
            {
                g2.setColor (Color.white);
                Point cp = this.getCentre();

                // Change the font to bold
                Font oldFont = g2.getFont();
                int  style   = oldFont.getStyle();
                style+=Font.BOLD;
                Font newFont = oldFont.deriveFont(style);
                g2.setFont(newFont); 

                for (int idx = 0; idx < this.npoints; idx++)
                {            
                    String s = "" + (idx+1);

                    // Reduce the join points from 30% and draw some centred text there !
                    int x = (int) ( ((float)this.xpoints[idx] - (float)cp.x) * 0.7f); 
                    int y = (int) ( ((float)this.ypoints[idx] - (float)cp.y) * 0.7f);

                    int fh = g2.getFontMetrics().getHeight();
                    int fw = g2.getFontMetrics().stringWidth(s);

                     x -= (fw/2);                        
                     y += (fh/4);

                    g2.drawString(s, cp.x + x, cp.y + y);                      
                }                

                g2.setFont(oldFont); 
            }
        }
                    
        // --------------------------------------------------------       
        
        if (dRoll > 0) // **** Display dice roll on Land Tiles
        {
            if (detailLevelHigh == false)
            {
                t = Integer.toString(dRoll);
                xTxtDiff = (g2.getFontMetrics().charsWidth(t.toCharArray(), 0, t.length())) / 2;         
                g2.drawString(t, p.x-xTxtDiff, p.y+yTxtDiff);
            }
            else
            {                                  
                int x = (int)(rect.getCenterX() - (rect.getWidth()  / 4));
                int y = (int)(rect.getCenterY() - (rect.getHeight() / 4));
                int w = (int)(rect.getWidth()  / 2);
                int h = (int)(rect.getHeight() / 2);

                if (dRoll < 7)
                    g2.drawImage(tileNoImgs[dRoll-2], x, y, w, h, null);
                else
                    g2.drawImage(tileNoImgs[dRoll-3], x, y, w, h, null);
            }
        }
        else if (dRoll < 0) // **** Display dice roll on Port Tiles
        {         
            dRoll = Math.abs(dRoll);
                           
            if (detailLevelHigh == false)
            {
                t = Integer.toString(dRoll) + "->1";
                xTxtDiff = (g2.getFontMetrics().charsWidth(t.toCharArray(), 0, t.length())) / 2;         
                g2.drawString(t, p.x-xTxtDiff, p.y+yTxtDiff);
            }            
        } 
        
        // --------------------------------------------------------
        
        // Display dice roll scores on basic tiles
        if ((detailLevelHigh == false) && (this.diceRollScore > 0))
        {
            int  y      = (int)(rect.getCenterY() + (rect.getHeight() / 4));
            int  w      = (int)(rect.getWidth()  / 2);
            int  h      = (int)(rect.getHeight() / 2);
            int  lWidth = (int)(rect.getWidth() / 16);
            int  x      = ((int)rect.getCenterX()) - (lWidth * (this.diceRollScore-1));            
            
            if (this.diceRollScore >= 5)
                g2.setColor (Color.red);
            else
                g2.setColor (Color.black);
            
            Stroke saveStroke  = g2.getStroke();
            g2.setStroke(new BasicStroke(lWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < this.diceRollScore; i++)
            {                
                g2.drawLine ((int)x, y, (int)x, y);    
                x+= (lWidth * 2);
            }
            g2.setStroke(saveStroke);
        }
                
        // --------------------------------------------------------
        
        // display the robber if its on this tile
        if ((robberImg != null) && (hasRobber != false))
        {
            p  = this.getCentre();
            
            // calculate scale...
            Rectangle2D r = this.getBounds2D();
            int w = (int)(((float)robberImg.getWidth()/430.0)  * (float)r.getWidth());
            int h = (int)(((float)robberImg.getHeight()/430.0) * (float)r.getHeight());
            g2.drawImage(robberImg,
                      p.x-(w/2),
                      p.y-(h/2),
                      w,
                      h,
                      null, null);                       
        }        
    }
}
