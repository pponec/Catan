package Catan;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */
public class AboutJDialog extends javax.swing.JDialog implements Runnable
{
    int          scrollYIdx = 0;
    boolean      scrollInit = false;
    String       txt[]      = {"",
                               "",
                               "",
                               "Solitaire Settlers of Catan (Version " + CatanJFrame.Version + ")",
                               "Was written by Steven De Toni (May 2008)",
                               "",
                               "",
                               "",
                               "",                               
                               "This game is based on the board game design of ",
                               "Die Siedler Von Catan by Klaus Teuber",
                               "",
                               "",
                               "",
                               "",                               
                               "If you like playing this computer version then",
                               "buy the real board game, it much more fun playing",
                               "with friends.",
                               "",
                               "",
                               "",
                               "",                               
                               "This program is released under the GPL License",
                               "and is free for non-commerical play.",
                               "",
                               "",
                               "",
                               "",
                               "",                               
                               "If you paid money for this game, you paid too much!",
                               "",
                               "",
                               "",
                               "",
                               "",                               
                               "For help on playing Catan, visit:",
                               "http://www.profeasy.com/Settlers_Boardgame/index.html",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",                               
                               "",
                               "",
                               "Bye Now",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",                               
                               "",
                               "",
                               "You can click the O.K button to quit",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",                               
                               "",
                               "",
                               "",
                               "",
                               "",                               
                               "",
                               "",
                               "",                               
                               "",
                               "Still here!",
                               "",
                               "O.k here is a secret recipe for great quick scones:",
                               "",
                               "INGREDIENTS... 4 cups self-raising flour",
                               "* 300ml cream (1 carton or small bottle of cream)",
                               "* 1 can Lemon & Paeroa (found only in N.Z)",
                               "or lemonade (355ml)",
                               "* 1/2 teaspoon salt",
                               "",
                               "[STEP 1]",
                               "Mix all ingredients in a bowl to a smooth dough.",
                               "",
                               "[STEP 2]",
                               "Tip out onto a well-floured surface and ",
                               "cut into squares or",
                               "press out with a round cookie cutter. ",
                               "",
                               "[STEP 3]",
                               "Bake at 220C (fan bake if possible) for about ",
                               "15-20 minutes until starting to colour golden.",
                               "Check they are cooked through and ",
                               "cool on a wire rack.",
                               "Serve warm with jam or whole fruit preserves and",
                               "whipped cream, garnish with fresh fruit as desired.",
                               "",
                               "",
                               "",
                               "",                               
                               "YUMMY!",
                               "",
                               "",
                               "",
                               "",
                               "",
                               "",
                               ""                               
                               };
    BufferedImage tranImg   = null;
    Thread        thread    = null;

    /** Creates new form AboutJDialog */
    public AboutJDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        
        // Load some images
        if (tranImg == null)
        {
            String f = "/Catan/Resource/about_transparency.png";
            try
            {
                tranImg = ImageIO.read(getClass().getResourceAsStream(f));
            }
            catch (Exception e)
            {
                System.err.println ("Failed loading image : "  + f + " : " + e.toString());
                System.exit(1);
            }
        }          
                     
        scrollerPanel.setUI(new BasicPanelUI()
        {
            @Override
            public Dimension getPreferredSize(JComponent c)
            {
                if (tranImg != null)
                    return new Dimension (tranImg.getWidth(), tranImg.getHeight());                
                else
                    return super.getPreferredSize(c);
            }
            
            public void draw (Graphics2D g2)
            {
                if (scrollInit == false)
                {
                    scrollYIdx = scrollerPanel.getHeight() - 25;
                    scrollInit = true;
                }
                    
                if (tranImg != null)
                {                    
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);                                   
        
                   
                    // set text to bold 
                    Font f = g2.getFont();
                    int style = f.getStyle();        
                    style+=Font.BOLD;
                    g2.setFont(f.deriveFont(style));                    
                    
                    g2.setColor(Color.white);
                    int yPos = 0;
                    
                    // Draw Message
                    for (int idx = 0; idx < txt.length; idx++)
                    {
                        
                        int sw = (int)g2.getFontMetrics().getStringBounds(txt[idx], (Graphics)g2).getWidth();                        
                        int sh = (int)g2.getFontMetrics().getStringBounds(txt[idx], (Graphics)g2).getHeight();
                    
                        yPos = scrollYIdx + (sh * idx);
                        
                        g2.drawString(txt[idx], 
                                (scrollerPanel.getWidth() / 2) - (sw/2),
                                 yPos);                                                             
                    }
                    
                    if (yPos < 0)
                    {
                        scrollYIdx = scrollerPanel.getHeight()-25;
                    }
                    
                
                    // Overlay transparency                    
                    g2.drawImage(tranImg, 
                                 0, scrollerPanel.getHeight() - tranImg.getHeight(),
                                 scrollerPanel.getWidth(), tranImg.getHeight(),
                                 null);
                }                
            }
            
            @Override           
            public void paint(Graphics g, JComponent c)
            {
                //super.paint(g, c);                
                draw ((Graphics2D)g);
            }
        });           
        
        this.start();
    }

    public void run()
    {    
        try { Thread.sleep(1000); } catch (InterruptedException e) { }
        do
        {
            if (pauseToggle.isSelected() == false)
                scrollYIdx--;            

            long lastTime = System.currentTimeMillis();
            scrollerPanel.paintImmediately(scrollerPanel.getBounds());
            try
            {   
                long t = 48 - (System.currentTimeMillis() - lastTime);
                if (t > 0)                    
                    Thread.sleep(t);
            }
            catch(InterruptedException e)
            {
                break;
            }            
            
        } while(thread != null);        
    }
    
    public void start()
    {
        if(thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop()
    {
        thread = null;
    }
        
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        scrollerPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        pauseToggle = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Solitaire Catan by Steven De Toni 2008");
        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        scrollerPanel.setBackground(java.awt.Color.blue);
        scrollerPanel.setForeground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout scrollerPanelLayout = new org.jdesktop.layout.GroupLayout(scrollerPanel);
        scrollerPanel.setLayout(scrollerPanelLayout);
        scrollerPanelLayout.setHorizontalGroup(
            scrollerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 355, Short.MAX_VALUE)
        );
        scrollerPanelLayout.setVerticalGroup(
            scrollerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 279, Short.MAX_VALUE)
        );

        okButton.setText("O.K");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        pauseToggle.setText("Pause");
        jPanel1.add(pauseToggle);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, scrollerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(scrollerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-385)/2, (screenSize.height-357)/2, 385, 357);
    }// </editor-fold>//GEN-END:initComponents

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    this.stop();
    this.dispose();
}//GEN-LAST:event_okButtonActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    this.stop();
    this.dispose();
    
}//GEN-LAST:event_formWindowClosing

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AboutJDialog dialog = new AboutJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton okButton;
    private javax.swing.JToggleButton pauseToggle;
    private javax.swing.JPanel scrollerPanel;
    // End of variables declaration//GEN-END:variables
}
