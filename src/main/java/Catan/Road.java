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
import java.util.LinkedList;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */

enum RoadTypes  {NULL, BUILDABLE, BUILT};

public class Road extends CatanGraphBase
{   
    public LinkedList<BuildPoint> buildJoins = new LinkedList<BuildPoint>();  // connected to 2 points.
    public LinkedList<Tile>       tileJoins  = new LinkedList<Tile>();  // connected to 2 tiles. 
    
    RoadTypes type  = RoadTypes.NULL;
    public Player    owner = null;
    public 
    
    Road (Object paintAreaObj)
    {
        super (paintAreaObj);        
    }
    
    // ------------------------------------------------------
    
    void buildItem (Player o, RoadTypes rType)
    {
        owner = o;
        type  = rType;
    }
    
    public BuildPoint getOtherBuildPoint (BuildPoint bp)
    {
        for (BuildPoint b:buildJoins)
        {
            if (b != bp)
                return b;
        }        
        return null;
    }
    
    // Returns the end point of this road from which player p may extend its road
    // network, or null if there is none. A settlement/city owned by anybody else
    // breaks the network at that vertex, so such an end point must be skipped:
    // p may own a road on one side of a foreign building, but it can never carry
    // a new road out the other side.
    public BuildPoint getJoinedRoadBP (Player p)
    {
        for (BuildPoint b:buildJoins)
        {
            if ((b.owner != null) && (b.owner != p))
                continue;

            for (Road r:b.roadJoins)
            {
                if (r.owner == p)
                    return b;
            }
        }

        return null;
    }
            
    // ------------------------------------------------------
    public void addTileJoin (Tile t)
    {
        if (tileJoins.indexOf(t) >=0)
            return;
        
        tileJoins.add(t);
    }    
    
    // This method is used in game board creation only. 
    // In game play use local variable: if (roadObj.type == isBuildableRoad.BUILDABLE) { /* build road */ }
    public static boolean isBuildableRoad (Road r)
    {      
        for (BuildPoint bp:r.buildJoins)
        {
            if (bp.type != BuildPointTypes.BUILDABLE_LAND)
            {
                return false;
            }
        }
        return true;
    }
    
    public void addBuildPointJoin (BuildPoint pt)
    {
        if (buildJoins.indexOf(pt) >=0)
            return;
        
        buildJoins.add(pt);
    }    
    
    public void debugHightlight (Graphics g)
    {
        draw ((Graphics2D)g, Color.green);
    }
    
    public void debugHightlight (Graphics g, Color c)
    {
        draw ((Graphics2D)g, c);
    }    
       
    public void draw (Graphics2D g2, Color c)
    {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        
        BuildPoint bp   = (BuildPoint)buildJoins.getFirst();
        Point p1        = bp.getCentrePoint();
        Point p2        = ((BuildPoint)buildJoins.getLast()).getCentrePoint();            
        double width    = (bp.getBounds2D()).getWidth();
        int    hlfWidth = (int)(width/1.7);               
        
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float) 0.3));        
        int r = c.getRed()   + 20;               
        int g = c.getGreen() + 20;
        int b = c.getBlue()  + 20;
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;        
        g2.setColor (new Color (r,g,b));        
        g2.setStroke(new BasicStroke(hlfWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (p1.x, p1.y, p2.x, p2.y);    
        g2.setComposite(originalComposite);
                
        g2.setColor (c);
        hlfWidth = (int)((width/1.7) * 0.7);      
        g2.setStroke(new BasicStroke(hlfWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (p1.x, p1.y, p2.x, p2.y);                
    }
   
    public void highLightSelectedDraw (Graphics2D g2, Color c)
    {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        
        BuildPoint bp = (BuildPoint)buildJoins.getFirst();
        Point p1      = bp.getCentrePoint();
        Point p2      = ((BuildPoint)buildJoins.getLast()).getCentrePoint();            
        int   width   = (int)(bp.getBounds2D()).getWidth();      
                
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float) 0.5));
        
        int r = c.getRed()   - 20;               
        int g = c.getGreen() - 20;
        int b = c.getBlue()  - 20;
        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;        
        g2.setColor (new Color (r,g,b));
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));       
        g2.drawLine (p1.x, p1.y, p2.x, p2.y);        
        
        width = (int)((bp.getBounds2D()).getWidth() * 0.8);      
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));               
        g2.drawLine (p1.x, p1.y, p2.x, p2.y);        
        
        g2.setComposite(originalComposite);
    }
    
    public Rectangle calcBuildSize()
    {
        Rectangle r1     = ((BuildPoint)buildJoins.getFirst()).getBounds();
        Rectangle r2     = ((BuildPoint)buildJoins.getLast()).getBounds();                                
        Rectangle arry[] = {r1, r2};
                
        int x  = r1.x; 
        int y  = r1.y;
        int x2 = r1.x;
        int y2 = r1.y;
        
        for (int idx = 0; idx < arry.length; idx++)
        {
            if (arry[idx].x < x)
                x = arry[idx].x;
            else if (arry[idx].x+arry[idx].width > x2)
                x2 = arry[idx].x+arry[idx].width;
            
            if (arry[idx].y < y)
                y = arry[idx].y;
            else if (arry[idx].y+arry[idx].height > y2)
                y2 = arry[idx].y+arry[idx].height;            
        }
        
        return new Rectangle (x, y, x2 - x, y2 - y);
    }
    
    public void paint (Graphics2D g2)
    {
        if (owner != null)
        {                        
            draw (g2, owner.col.toCol());            
            
            /*        
            g2.setColor (new Color(r,g,b));
            g2.fillPolygon(this);
            */
        }
    }        
}
