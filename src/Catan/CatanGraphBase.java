package Catan;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */
public abstract class CatanGraphBase extends Polygon
{
    public Object paintAreaObj = null;
        
    Polygon basePoints = new Polygon();
    double lastReScale = 1.0;
    int    lastXOffset = 0;
    int    lastYOffset = 0;
    
    public CatanGraphBase (Object paintAreaObj)
    {        
        super();
        this.paintAreaObj = paintAreaObj;
    }
    
    @Override
    public void addPoint (int x, int y)
    {
        basePoints.addPoint(x, y);
        super.addPoint(x, y);
    }
    
    @Override
    public void translate(int deltaX, int deltaY)
    {
        super.translate(deltaX, deltaY);
        basePoints.translate(deltaX, deltaY);
    }
         
    public void scale (double rescaleFromBasePoly, int xOffset, int yOffset)
    {
        lastReScale = rescaleFromBasePoly;
        lastXOffset = xOffset;
        lastYOffset = yOffset;        
        
        for (int idx = 0; idx < this.npoints; idx++)
        {            
            xpoints[idx] = xOffset + (int)((double)basePoints.xpoints[idx] * rescaleFromBasePoly);
            ypoints[idx] = yOffset + (int)((double)basePoints.ypoints[idx] * rescaleFromBasePoly);
        }
        
        this.invalidate();
    }
    
    public void repaint ()
    {       
        this.paint ( (Graphics2D) ((Component)paintAreaObj).getGraphics() );
    }        
    
    public abstract void paint (Graphics2D g2);
}
