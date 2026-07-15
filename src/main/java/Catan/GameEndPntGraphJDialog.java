/*
 * GameEndPntGraphJDialog.java
 *
 * Created on 4 May 2008, 21:16
 */
package Catan;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 *
 * @author  Steven De Toni
 * 
 *  May 2008
 */ 
public class GameEndPntGraphJDialog extends javax.swing.JDialog
{
    public LinkedList<GameRules.StatsRound> gameStats = null;
        
    /** Creates new form GameEndPntGraphJDialog */
    public GameEndPntGraphJDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();

        // Render the graph in a lightweight content-pane panel rather than in
        // the dialog's paint(). Painting a heavyweight window directly is
        // unreliable on first show: the initial background/child paint pass can
        // clobber part of the custom drawing, so the dashed grid ("helper")
        // lines only appeared after a resize forced a clean repaint.
        setContentPane(new GraphPanel());

        // A little more breathing room than the original 319x320 default.
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int gw = 480;
        int gh = 400;
        setBounds((screenSize.width - gw) / 2, (screenSize.height - gh) / 2, gw, gh);
    }

    public void assignGameStats (LinkedList<GameRules.StatsRound> gameStats)
    {
        this.gameStats = gameStats;
    }

    /** Panel that draws the per-round player-points graph. */
    private class GraphPanel extends JPanel
    {
        GraphPanel ()
        {
            setBackground(Color.white);
        }

        @Override
        protected void paintComponent (Graphics g)
        {
            super.paintComponent(g);

            if (gameStats == null || gameStats.isEmpty()) return;

            // Build a graph
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int    w = getWidth();
            int    h = getHeight();
            Insets i = getInsets();
            int    xT = i.left+10;
            int    yT = i.top+10;
            int    xB = w-i.right-10;
            int    yB = h-i.bottom-10;

            g2.setColor(Color.black);
            g2.drawLine(xT, yT, xT, yB);
            g2.drawLine(xT, yB, xB, yB);


            // Determine max end game point to plot graph
            int maxPoints = 1;
            GameRules.StatsRound tnts = gameStats.getLast();
            for (GameRules.StatsPlyr p:tnts.plyrs)
            {
                if (maxPoints < p.tpnts)
                    maxPoints = p.tpnts;
            }

            // Draw graph Marks
            float yInc = ((float)(yB - yT) / (float)maxPoints);

            Stroke saveStroke = (Stroke) g2.getStroke();
            float [] dash = {3.0F, 2.0F, 3.0F, 2.0F };

            g2.setStroke(new BasicStroke (1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0F, dash, 0.F));
            g2.setColor(Color.lightGray);


            for (int inc = 0; inc < maxPoints; inc++)
            {
                g2.drawLine (xT, yT + (int)((float)inc * yInc),
                             xB, yT + (int)((float)inc * yInc));
            }

            // Draw vertical grid
            float xInc = ((float)(xB - xT) / (float)gameStats.size());
            for (int inc = 1; inc <= gameStats.size(); inc++)
            {
                g2.drawLine (xT + (int)((float)inc * xInc), yT,
                             xT + (int)((float)inc * xInc), yB);
            }

            g2.setStroke(saveStroke);

            // Draw points Graph.
            //
            // Each player's line is stroked in its own colour, but the panel
            // background is white - so a white player would otherwise be
            // invisible. Draw a slightly wider dark outline under every line
            // first; that makes the white player show up and improves contrast
            // for all the light colours.
            Stroke outlineStroke = new BasicStroke (5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0F, null, 0.F);
            Stroke lineStroke    = new BasicStroke (3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0F, null, 0.F);

            GameRules.StatsRound p = gameStats.getFirst();
            int n = gameStats.size();
            for (int pIdx = 0; pIdx < p.plyrs.size(); pIdx++)
            {
                // Collect the polyline points, starting from the bottom-left origin.
                int[]  xs  = new int[n + 1];
                int[]  ys  = new int[n + 1];
                Color  col = p.plyrs.get(pIdx).col;

                xs[0] = xT;
                ys[0] = (int)yB;

                float xLast = xT;
                int   k     = 1;
                for (GameRules.StatsRound pi:gameStats)
                {
                    GameRules.StatsPlyr pr = pi.plyrs.get(pIdx);

                    xs[k] = (int)(xLast + xInc);
                    ys[k] = (int)((yB - yInc * (float)pr.tpnts) - (float)(pIdx));
                    col   = pr.col;

                    xLast += xInc;
                    k++;
                }

                // Dark outline first, then the player's colour on top.
                g2.setStroke(outlineStroke);
                g2.setColor(Color.darkGray);
                g2.drawPolyline(xs, ys, n + 1);

                g2.setStroke(lineStroke);
                g2.setColor(col);
                g2.drawPolyline(xs, ys, n + 1);
            }
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Player Points Over Game Rounds");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 311, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 291, Short.MAX_VALUE)
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-319)/2, (screenSize.height-320)/2, 319, 320);
    }// </editor-fold>//GEN-END:initComponents

private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
    repaint();
}//GEN-LAST:event_formComponentResized

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {

            public void run()
            {
                GameEndPntGraphJDialog dialog = new GameEndPntGraphJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {

                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
