package Catan;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
enum BuildPointTypes  {NULL, BUILDABLE_LAND, SETTLEMENT, CITY};               

public class BuildPoint  extends CatanGraphBase 
{   
    // Load image resources 
    public static BufferedImage resCityImgs[] = null;
    public static BufferedImage resSetlImgs[] = null;        
   
    public  BuildPointTypes type      = BuildPointTypes.NULL;
    public  LinkedList<Tile> tileJoins = new LinkedList<Tile>(); // connected to 3 tiles.
    public  LinkedList<Road> roadJoins = new LinkedList<Road>(); // connected to 2 roads. 
    
    public  boolean         isPort    = false;
    public  TileTypes       portType  = TileTypes.NULL;
    
    public Player          owner = null;
           
    BuildPoint (Object paintAreaObj)
    {
        super(paintAreaObj);       

        // Load image resources 
        if (resCityImgs == null)
        {
            String files[] = {"/Catan/Resource/city_blue.png",  "/Catan/Resource/city_red.png",
                              "/Catan/Resource/city_white.png", "/Catan/Resource/city_orange.png",
                              "/Catan/Resource/city_green.png", "/Catan/Resource/city_yellow.png"};
            
            resCityImgs = new BufferedImage[files.length];
            
            for (int idx = 0; idx < files.length; idx++)
            {
                try
                {
                    resCityImgs[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx]));
                }
                catch (Exception e)
                {
                    System.err.println ("Failed loading image : "  + files[idx] + " : " + e.toString());
                }                    
            }     
        }        
        
        if (resSetlImgs == null)
        {
            String files[] = {"/Catan/Resource/settlement_blue.png",  "/Catan/Resource/settlement_red.png",
                              "/Catan/Resource/settlement_white.png", "/Catan/Resource/settlement_orange.png",
                              "/Catan/Resource/settlement_green.png", "/Catan/Resource/settlement_yellow.png"};
            
            resSetlImgs = new BufferedImage[files.length];
            
            for (int idx = 0; idx < files.length; idx++)
            {
                try
                {
                    resSetlImgs[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx]));
                }
                catch (Exception e)
                {
                    System.err.println ("Failed loading image : "  + files[idx] + " : " + e.toString());
                }                    
            }     
        }                
    }        
    
    public void setPort (TileTypes port) 
    {
        isPort = true;
        portType = port;
    }
    public boolean bordersType (TileTypes tt)
    {
        for (Tile t:tileJoins)
        {            
            if (t.type == tt)
                return true;            
        }
        return false;
    }
    
    public int uniquenessScore ()
    {
        //              SEA    BRICK, ROCK, WHEAT, WOOD, SHEEP, DESERT, PORT_ANY_3TO1, PORT_BRICK_2TO1, PORT_ROCK_2TO1, PORT_WHEAT_2TO1, PORT_WOOD_2TO1, PORT_SHEEP_2TO1, VAR_VOLCANO
        int scores[] = {0,     0,    0,    0,    0,     0,     0,      0,             0,               0,              0,              0,              0,               0};
        int amnt;
        int idx;
        
        for (Tile t:tileJoins)
        {
            switch (t.type )
            {              
                case BRICK:
                    idx = 1; amnt = 50; break;
                case ROCK:
                    idx = 2; amnt = 50; break;
                case WHEAT:
                    idx = 3; amnt = 50; break;
                case WOOD:
                    idx = 4; amnt = 50; break;                    
                case SHEEP:                    
                    idx = 5; amnt = 50; break;                                        
                case DESERT:                
                    idx = 6; amnt = 1; break;                                                                              
                case PORT_ANY_3TO1:
                    if (isPort != false)
                        { idx = 7; amnt = 50; }
                    else
                        { idx = 0; amnt = 1; }
                    break;                    
                case PORT_BRICK_2TO1:                    
                    if (isPort != false)
                        { idx = 8; amnt = 50; }
                    else
                        { idx = 0; amnt = 1; }
                    break;                                        
                case PORT_ROCK_2TO1:
                    if (isPort != false)
                        { idx = 9; amnt = 50; }
                    else
                        { idx = 0; amnt = 1; }
                    break;                                                            
                case PORT_WHEAT_2TO1:
                    if (isPort != false)
                        { idx = 10; amnt = 50; }
                    else
                        { idx = 0; amnt = 1; }
                    break;                                                            
                case PORT_WOOD_2TO1:
                    if (isPort != false)
                        { idx = 11; amnt = 50; }
                    else
                        { idx = 0; amnt = 1; }
                    break;                                                            
                case PORT_SHEEP_2TO1:
                    if (isPort != false)
                        { idx = 12; amnt = 50; }
                    else
                        { idx = 0; amnt = 1; }
                    break;                                        
                    
                case VAR_VOLCANO:
                    idx  = 13;
                    amnt = 65;
                    break;
                                        
                case SEA:                    
                default:
                    idx = 0; amnt = 1; break;  
            }            
            
            if (scores[idx] > 0)
                scores[idx] += amnt/2;
            else
                scores[idx] += amnt;            
        }
        
        int score = 0;
        
        for (idx = 0; idx < scores.length; idx++)
            score += scores[idx];
        
        return score;
    }
    
    public void addTileJoin (Tile t)
    {
        // Check if we can build on this tile?
        switch (t.type )
        {
            case BRICK:
            case ROCK:
            case WHEAT:
            case WOOD:
            case SHEEP:
            case DESERT:
                type = BuildPointTypes.BUILDABLE_LAND;             
        }
        
        if (tileJoins.indexOf(t) >=0)
            return;
                
        tileJoins.add(t);
    }
    
    public void addRoadJoin (Road r)
    {
        if (roadJoins.indexOf(r) >=0)
            return;
        
        roadJoins.add(r);
    }    
    
    // ------------------------------------------------------
    
    private void buildItem (Player o, BuildPointTypes bType)
    {        
        owner = o;
        type  = bType;        
    }
    
    // ------------------------------------------------------
    
    public Point getCentrePoint ()
    {        
        Rectangle2D r = this.getBounds2D();        
        return new Point ( (int)r.getCenterX(), (int)r.getCenterY() );        
    }

    public void debugHightlight (Graphics g, Color c)
    {
        ((Graphics2D)g).setColor (c);
        ((Graphics2D)g).fillPolygon(this);                
    }
            
    public void debugHightlight (Graphics g)
    {          
        debugHightlight (g, Color.magenta);
    }    
    
    public void highLightSelectedDraw (Graphics2D g2, Color c)
    {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
                        
        int w = (int)((this.getBounds()).getWidth() + (this.getBounds()).getWidth()/2);
                        
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float) 0.5));
        
        Point p = this.getCentrePoint();
        
        g2.setColor (c);
        g2.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (p.x, p.y, p.x, p.y);        
        
        
        int r = c.getRed()   - 20;               
        int g = c.getGreen() - 20;
        int b = c.getBlue()  - 20;
        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;        
        
        w = (int)(((this.getBounds()).getWidth() + (this.getBounds()).getWidth()/2) * 0.8);
        g2.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (p.x, p.y, p.x, p.y);    
        
        g2.setComposite(originalComposite);
    }
        
    public BufferedImage getBuildObjImage ()
    {
        BufferedImage bi = null;

        switch (type)
        {
            case SETTLEMENT:                    
                bi = resSetlImgs[owner.col.toValue()];                
                break;

            case CITY:
                bi = resCityImgs[owner.col.toValue()];
                break;
        }        
        
        return bi;
    }
    
    public Rectangle calcBuildSize ()
    {   
        BufferedImage bi = getBuildObjImage();
        
        if (bi == null) 
            return null;
        
        // calculate scale...
        Point p = getCentrePoint ();                                                
        Rectangle2D r = this.getBounds2D();
        int w = (int)(((float)bi.getWidth()/45.0)  * (float)r.getWidth());
        int h = (int)(((float)bi.getHeight()/45.0) * (float)r.getHeight());        
        
        return new Rectangle (p.x-(w/2), p.y-(h/2), w, h);
    }
    
    public void drawAt (Graphics2D g2, Rectangle r)
    {
        BufferedImage bi = getBuildObjImage();
        
        g2.drawImage(bi, r.x, r.y, r.width, r.height, null);
    }
    
    public void paint (Graphics2D g2)
    {
        if (owner != null)
        {           
            drawAt (g2, calcBuildSize());
        }
    }    
}
