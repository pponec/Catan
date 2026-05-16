package Catan;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 *
 * @author  Steven De Toni
 * 
 *  April 2008
 */
enum TurnMode
{

    NONE, DICEROLL, ENDTURN, ENDTRADE, ENDOUTOFTURNBUILD
};

enum DebugLevel
{

    COMPLETE(0), MEDIUM(1), BRIEF(2), NONE(3);
    private int value;

    private DebugLevel(int i)
    {
        value = i;
    }

    public int toValue()
    {
        return value;
    }
};

public class CatanJFrame extends javax.swing.JFrame
{

    public static String Version = "1.9.8";
    // ----- Debug -----
    public DebugLevel dbgLevel = DebugLevel.NONE;
    // ------ Preferences ----
    public Point tradeDialogLastPos = new Point(-1, -1);
    GameNewJDialog lastNGD = null;
    boolean soundFX = true;
    GameBoardJPanel gameBoard = new GameBoardJPanel();
    ShowTextJFrame logView = null;
    TurnMode currTurnMode = TurnMode.NONE;
    GameRules gameRules = null;
    ImageIcon icons[] =
    {
        new ImageIcon(getClass().getResource("/Catan/Icons/icon_question.png")),
        new ImageIcon(getClass().getResource("/Catan/Icons/icon_stop.png")),
        new ImageIcon(getClass().getResource("/Catan/Icons/icon_next.png")),
        new ImageIcon(getClass().getResource("/Catan/Icons/icon_build.png"))
    };

    /** Creates new form CatanJFrame */
    public CatanJFrame()
    {
        initComponents();

        // Change the color of a die
        dieLeft.bgColor = new Color(200, 0, 0);
        dieLeft.dotColor = Color.white;

        // Setup debug menu ... 
        switch (dbgLevel)
        {
            case COMPLETE:
                dbgFullRadMenuItem.setSelected(true);
                break;
            case MEDIUM:
                dbgMediumRadMenuItem.setSelected(true);
                break;
            case BRIEF:
                dbgBriefRadMenuItem.setSelected(true);
                break;
            default:
                dbgNoneRadMenuItem.setSelected(true);
        }

        splitPane.setLeftComponent(gameBoard);

        // Add custom coloured tabs        
        playerInfo.setUI(new BasicTabbedPaneUI()
        {

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected)
            {
                //override paint op to do nothing. super.paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect, isSelected);
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected)
            {
                //override paint op to do nothing. super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
            }

            protected void alteredTabBackground(Graphics g, int tabPlacement,
                    int tabIndex,
                    int x, int y, int w, int h,
                    boolean isSelected,
                    Color newCol)
            {
                // g.setColor(!isSelected || selectedColor == null ? tabPane.getBackgroundAt(tabIndex) : selectedColor);
                g.setColor(newCol);
                switch (tabPlacement)
                {
                    case LEFT:
                        g.fillRect(x + 1, y + 1, w - 1, h - 3);
                        break;
                    case RIGHT:
                        g.fillRect(x, y + 1, w - 2, h - 3);
                        break;
                    case BOTTOM:
                        g.fillRect(x + 1, y, w - 3, h - 1);
                        break;
                    case TOP:
                    default:
                        g.fillRect(x + 1, y + 1, w - 3, h - 1);
                }
            }

            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex)
            {
                int tabCount = tabPane.getTabCount();
                if (tabCount == 0 || rects == null || rects.length == 0)
                {
                    return;
                }

                Rectangle iconRect = new Rectangle();
                Rectangle textRect = new Rectangle();
                Rectangle clipRect = g.getClipBounds();

                int safeSelected = selectedIndex;
                if (safeSelected < 0 || safeSelected >= tabCount)
                {
                    safeSelected = tabPane.getSelectedIndex();
                }
                if (safeSelected < 0 || safeSelected >= tabCount)
                {
                    safeSelected = 0;
                }

                for (int i = runCount - 1; i >= 0; i--)
                {
                    if (i >= tabRuns.length)
                    {
                        continue;
                    }
                    int start = tabRuns[i];
                    if (start < 0 || start >= tabCount)
                    {
                        continue;
                    }
                    int next = tabRuns[(i == runCount - 1) ? 0 : i + 1];
                    int end = (next != 0 ? next - 1 : tabCount - 1);
                    if (end >= tabCount)
                    {
                        end = tabCount - 1;
                    }
                    if (end < start)
                    {
                        continue;
                    }
                    for (int j = start; j <= end; j++)
                    {
                        if (j < 0 || j >= tabCount || j >= rects.length)
                        {
                            continue;
                        }
                        if (j != safeSelected && rects[j].intersects(clipRect))
                        {
                            paintTab(g, tabPlacement, rects, j, iconRect, textRect);
                        }
                    }
                }

                if (safeSelected >= 0 && safeSelected < tabCount && safeSelected < rects.length
                        && rects[safeSelected].intersects(clipRect))
                {
                    paintTab(g, tabPlacement, rects, safeSelected, iconRect, textRect);
                }
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)
            {
                if (tabIndex < 0 || tabIndex >= tabPane.getTabCount())
                {
                    super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                    return;
                }
                try
                {
                    ResBuildPanel rbp = (ResBuildPanel) playerInfo.getComponent(tabIndex);

                    Graphics2D g2 = (Graphics2D) g;

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    if (gameRules != null)
                    {
                        Insets inset = this.getTabAreaInsets(tabPlacement);

                        if (rbp.player.gameRules.thisPlayer == rbp.player)
                        {
                            //g2.setColor(rbp.player.col.toCol());
                            //g2.fillRoundRect (x+inset.left, y+inset.top, w-inset.right, h-inset.bottom, inset.left*2, inset.left*2);                             
                            alteredTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected, rbp.player.col.toCol());
                        }
                        else
                        {
                            super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                        }

                        // Paint this traders icon
                        BufferedImage bi = rbp.player.getPlayerImage();
                        int newHeight = h - inset.top - inset.bottom;
                        float reduce = (float) newHeight / (float) bi.getHeight();
                        int newWidth = (int) ((float) bi.getWidth() * reduce);

                        g2.drawImage(bi, x + ((w / 2) - (newWidth / 2)), y + inset.top, newWidth, newHeight, null);
                    }
                    else
                    {
                        super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                    }
                }
                catch (Exception e)
                {
                    super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h, isSelected);
                }
            }
        });

    //newGame();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        debugMenuGroup = new javax.swing.ButtonGroup();
        gameArena = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        playerInfo = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        nextAction = new javax.swing.JButton();
        dieLeft = new Catan.DieJPanel();
        dieRight = new Catan.DieJPanel();
        viewLog = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        gameStatusBar = new javax.swing.JTextPane();
        vicPointsInfo = new Catan.VicPointsInfoJPanel();
        jMenuBar2 = new javax.swing.JMenuBar();
        gameMenu1 = new javax.swing.JMenu();
        newGameMenuItem = new javax.swing.JMenuItem();
        detailLevelMenuItem = new javax.swing.JCheckBoxMenuItem();
        soundFXMenuItem = new javax.swing.JCheckBoxMenuItem();
        aiPromptingChkBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        endGameGraphMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        quitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        profEasyMenuItem = new javax.swing.JMenuItem();
        quickTradeHelpMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        debugMenu = new javax.swing.JMenu();
        dbgViewCardsChkBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        dbgNoneRadMenuItem = new javax.swing.JRadioButtonMenuItem();
        dbgBriefRadMenuItem = new javax.swing.JRadioButtonMenuItem();
        dbgMediumRadMenuItem = new javax.swing.JRadioButtonMenuItem();
        dbgFullRadMenuItem = new javax.swing.JRadioButtonMenuItem();
        showPntsGraphMenuItem = new javax.swing.JMenuItem();
        compPlayTestMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Solitaire Settlers of Catan - by Steven De Toni - Version " + Version);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        gameArena.setDoubleBuffered(false);

        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(1.0);
        splitPane.setOneTouchExpandable(true);

        playerInfo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playerInfo.setFont(new java.awt.Font("Tahoma", 0, 18));
        splitPane.setRightComponent(playerInfo);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setDoubleBuffered(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setDoubleBuffered(false);

        nextAction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Catan/Icons/icon_question.png"))); // NOI18N
        nextAction.setText("Roll Dice"); // NOI18N
        nextAction.setEnabled(false);
        nextAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextActionActionPerformed(evt);
            }
        });
        jPanel2.add(nextAction);

        dieLeft.setDoubleBuffered(false);

        org.jdesktop.layout.GroupLayout dieLeftLayout = new org.jdesktop.layout.GroupLayout(dieLeft);
        dieLeft.setLayout(dieLeftLayout);
        dieLeftLayout.setHorizontalGroup(
            dieLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 32, Short.MAX_VALUE)
        );
        dieLeftLayout.setVerticalGroup(
            dieLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 32, Short.MAX_VALUE)
        );

        jPanel2.add(dieLeft);

        dieRight.setDoubleBuffered(false);

        org.jdesktop.layout.GroupLayout dieRightLayout = new org.jdesktop.layout.GroupLayout(dieRight);
        dieRight.setLayout(dieRightLayout);
        dieRightLayout.setHorizontalGroup(
            dieRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 33, Short.MAX_VALUE)
        );
        dieRightLayout.setVerticalGroup(
            dieRightLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 33, Short.MAX_VALUE)
        );

        jPanel2.add(dieRight);

        viewLog.setText("..."); // NOI18N
        viewLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewLogActionPerformed(evt);
            }
        });
        viewLog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewLogMouseClicked(evt);
            }
        });

        gameStatusBar.setEditable(false);
        gameStatusBar.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        jScrollPane1.setViewportView(gameStatusBar);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(viewLog, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(viewLog, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        vicPointsInfo.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        vicPointsInfo.setDoubleBuffered(false);

        org.jdesktop.layout.GroupLayout vicPointsInfoLayout = new org.jdesktop.layout.GroupLayout(vicPointsInfo);
        vicPointsInfo.setLayout(vicPointsInfoLayout);
        vicPointsInfoLayout.setHorizontalGroup(
            vicPointsInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 721, Short.MAX_VALUE)
        );
        vicPointsInfoLayout.setVerticalGroup(
            vicPointsInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 30, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout gameArenaLayout = new org.jdesktop.layout.GroupLayout(gameArena);
        gameArena.setLayout(gameArenaLayout);
        gameArenaLayout.setHorizontalGroup(
            gameArenaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 747, Short.MAX_VALUE)
            .add(gameArenaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(gameArenaLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(gameArenaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, splitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, vicPointsInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        gameArenaLayout.setVerticalGroup(
            gameArenaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 599, Short.MAX_VALUE)
            .add(gameArenaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(gameArenaLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(splitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(vicPointsInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        gameMenu1.setText("Game Menu"); // NOI18N

        newGameMenuItem.setText("Start New Game"); // NOI18N
        newGameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameMenuItemActionPerformed(evt);
            }
        });
        gameMenu1.add(newGameMenuItem);

        detailLevelMenuItem.setSelected(true);
        detailLevelMenuItem.setText("High Tile Detail");
        detailLevelMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailLevelMenuItemActionPerformed(evt);
            }
        });
        gameMenu1.add(detailLevelMenuItem);

        soundFXMenuItem.setSelected(true);
        soundFXMenuItem.setText("Sound FX");
        soundFXMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soundFXMenuItemActionPerformed(evt);
            }
        });
        gameMenu1.add(soundFXMenuItem);

        aiPromptingChkBoxMenuItem.setText("Prompt All AI Action");
        aiPromptingChkBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aiPromptingChkBoxMenuItemActionPerformed(evt);
            }
        });
        gameMenu1.add(aiPromptingChkBoxMenuItem);

        endGameGraphMenuItem.setText("End Game Points Graph");
        endGameGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endGameGraphMenuItemActionPerformed(evt);
            }
        });
        gameMenu1.add(endGameGraphMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        gameMenu1.add(aboutMenuItem);
        gameMenu1.add(jSeparator1);

        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        gameMenu1.add(quitMenuItem);

        jMenuBar2.add(gameMenu1);

        jMenu1.setText("Catan Help");

        profEasyMenuItem.setText("Prof Easy Help");
        profEasyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profEasyMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(profEasyMenuItem);

        quickTradeHelpMenuItem.setText("Quick Trade Help");
        quickTradeHelpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quickTradeHelpMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(quickTradeHelpMenuItem);

        jMenuItem1.setText("Sourceforge Wiki");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar2.add(jMenu1);

        debugMenu.setEnabled(false);

        dbgViewCardsChkBoxMenuItem.setText("Show Player Cards");
        dbgViewCardsChkBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbgViewCardsChkBoxMenuItemActionPerformed(evt);
            }
        });
        debugMenu.add(dbgViewCardsChkBoxMenuItem);
        debugMenu.add(jSeparator2);

        debugMenuGroup.add(dbgNoneRadMenuItem);
        dbgNoneRadMenuItem.setSelected(true);
        dbgNoneRadMenuItem.setText("Debug None");
        dbgNoneRadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbgNoneRadMenuItemActionPerformed(evt);
            }
        });
        debugMenu.add(dbgNoneRadMenuItem);

        debugMenuGroup.add(dbgBriefRadMenuItem);
        dbgBriefRadMenuItem.setText("Debug Brief");
        dbgBriefRadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbgBriefRadMenuItemActionPerformed(evt);
            }
        });
        debugMenu.add(dbgBriefRadMenuItem);

        debugMenuGroup.add(dbgMediumRadMenuItem);
        dbgMediumRadMenuItem.setText("Debug Medium");
        dbgMediumRadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbgMediumRadMenuItemActionPerformed(evt);
            }
        });
        debugMenu.add(dbgMediumRadMenuItem);

        debugMenuGroup.add(dbgFullRadMenuItem);
        dbgFullRadMenuItem.setText("Debug Full");
        dbgFullRadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dbgFullRadMenuItemActionPerformed(evt);
            }
        });
        debugMenu.add(dbgFullRadMenuItem);

        showPntsGraphMenuItem.setText("Show Pnts Graph");
        showPntsGraphMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPntsGraphMenuItemActionPerformed(evt);
            }
        });
        debugMenu.add(showPntsGraphMenuItem);

        compPlayTestMenuItem.setText("Com Play Test");
        compPlayTestMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compPlayTestMenuItemActionPerformed(evt);
            }
        });
        debugMenu.add(compPlayTestMenuItem);

        jMenuBar2.add(debugMenu);

        setJMenuBar(jMenuBar2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, gameArena, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gameArena, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-755)/2, (screenSize.height-646)/2, 755, 646);
    }// </editor-fold>//GEN-END:initComponents

    public void setMenuDebugLevel()
    {
        if (dbgNoneRadMenuItem.isSelected() != false)
        {
            dbgLevel = DebugLevel.NONE;
        }
        else if (dbgBriefRadMenuItem.isSelected() != false)
        {
            dbgLevel = DebugLevel.BRIEF;
        }
        else if (dbgMediumRadMenuItem.isSelected() != false)
        {
            dbgLevel = DebugLevel.MEDIUM;
        }
        else if (dbgFullRadMenuItem.isSelected() != false)
        {
            dbgLevel = DebugLevel.COMPLETE;
        }
    }

    public void Debug(String s, DebugLevel dl)
    {
        if (dbgLevel.toValue() <= dl.toValue())
        {
            System.out.println(s);
        }
    }

    public void DebugErr(String s)
    {
        System.err.println(s);
    }

    private void newGameMenuItemActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_newGameMenuItemActionPerformed
    {//GEN-HEADEREND:event_newGameMenuItemActionPerformed
        newGame();
}//GEN-LAST:event_newGameMenuItemActionPerformed

    private void nextActionActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_nextActionActionPerformed
    {//GEN-HEADEREND:event_nextActionActionPerformed
        try
        {
            switch (currTurnMode)
            {
                case DICEROLL:
                    diceRoll();
                    gameRules.playNextPhase();
                    break;

                case ENDTURN:
                case ENDTRADE:
                    gameRules.playEndTurn();
                    break;

                case ENDOUTOFTURNBUILD:
                    gameRules.playEndTurn();
                    this.vicPointsInfo.repaint();
                    break;
            }
        }
        catch (CatanEndGameException e)
        {
        }
}//GEN-LAST:event_nextActionActionPerformed

    private void viewLogActionPerformed (java.awt.event.ActionEvent evt)//GEN-FIRST:event_viewLogActionPerformed
    {//GEN-HEADEREND:event_viewLogActionPerformed
        if (gameRules == null)
        {
            return;
        }
        if (logView == null)
        {
            logView = new ShowTextJFrame(gameRules.log);
        }
        logView.setVisible(true);
}//GEN-LAST:event_viewLogActionPerformed

    private void formComponentResized (java.awt.event.ComponentEvent evt)//GEN-FIRST:event_formComponentResized
    {//GEN-HEADEREND:event_formComponentResized
        Dimension size = getSize();
        int newWidth = size.width;
        int newHeight = size.height;
        if (size.width < 640)
        {
            newWidth = 640;
        }
        if (size.height < 580)
        {
            newHeight = 580;
        }
        setSize(newWidth, newHeight);
    }//GEN-LAST:event_formComponentResized

private void detailLevelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailLevelMenuItemActionPerformed
    if (detailLevelMenuItem.isSelected() != false)
    {
        this.gameBoard.detailLevelHigh = true;
    }
    else
    {
        this.gameBoard.detailLevelHigh = false;
    }
    gameBoard.clrDblBuffCache();
    gameBoard.repaint();
}//GEN-LAST:event_detailLevelMenuItemActionPerformed

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed

    AboutJDialog a = new AboutJDialog(null, true);
    a.setVisible(true);
}//GEN-LAST:event_aboutMenuItemActionPerformed

private void profEasyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profEasyMenuItemActionPerformed
    BrowserControl.displayURL("http://www.profeasy.com/Settlers_Boardgame/index.html");
}//GEN-LAST:event_profEasyMenuItemActionPerformed

private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
    if (logView != null)
    {
        logView.dispose();
    }
    this.dispose();
}//GEN-LAST:event_quitMenuItemActionPerformed

private void dbgViewCardsChkBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbgViewCardsChkBoxMenuItemActionPerformed

    for (Player p : gameRules.players)
    {
        p.resPanelInfo.compCheatViewer = dbgViewCardsChkBoxMenuItem.isSelected();
    }
    this.repaint();
}//GEN-LAST:event_dbgViewCardsChkBoxMenuItemActionPerformed

private void dbgNoneRadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbgNoneRadMenuItemActionPerformed
    setMenuDebugLevel();
}//GEN-LAST:event_dbgNoneRadMenuItemActionPerformed

private void dbgBriefRadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbgBriefRadMenuItemActionPerformed
    setMenuDebugLevel();
}//GEN-LAST:event_dbgBriefRadMenuItemActionPerformed

private void dbgMediumRadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbgMediumRadMenuItemActionPerformed
    setMenuDebugLevel();
}//GEN-LAST:event_dbgMediumRadMenuItemActionPerformed

private void dbgFullRadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dbgFullRadMenuItemActionPerformed
    setMenuDebugLevel();
}//GEN-LAST:event_dbgFullRadMenuItemActionPerformed

private void quickTradeHelpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quickTradeHelpMenuItemActionPerformed
    MessageJDialog md = new MessageJDialog(this, true, false);

    String s = "<h1>Trading Back Tracking or Canceling</h1>\n" +
            "When trading with players, you can quickly back track a trading transaction by\n" +
            "<b>Right-Clicking</b> the <b>Mouse Button</b>.\n" +
            "\n" +
            "This also works to <b>Cancel</b> a requested computer trade as well.\n" +
            "\n";
    md.setText(s, false);
    md.setVisible(true);
}//GEN-LAST:event_quickTradeHelpMenuItemActionPerformed

private void aiPromptingChkBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aiPromptingChkBoxMenuItemActionPerformed
    this.gameRules.msgLogPrompting = aiPromptingChkBoxMenuItem.isSelected();
}//GEN-LAST:event_aiPromptingChkBoxMenuItemActionPerformed

private void viewLogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_viewLogMouseClicked

    // Switch on debugging using right click on '...' more info frame button
    switch (evt.getButton())
    {
        case MouseEvent.BUTTON2:
        case MouseEvent.BUTTON3:
            dbgLevel = DebugLevel.COMPLETE;
            dbgFullRadMenuItem.setSelected(true);
            debugMenu.setText("Debug");
            debugMenu.setEnabled(true);
    }
}//GEN-LAST:event_viewLogMouseClicked

private void soundFXMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soundFXMenuItemActionPerformed
    soundFX = soundFXMenuItem.isSelected();
}//GEN-LAST:event_soundFXMenuItemActionPerformed

private void showPntsGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPntsGraphMenuItemActionPerformed
        GameEndPntGraphJDialog g = new GameEndPntGraphJDialog (this, true);//GEN-LAST:event_showPntsGraphMenuItemActionPerformed

        g.assignGameStats(gameRules.plyrStatsRoundList);
        g.setVisible(true);
    }

private void endGameGraphMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endGameGraphMenuItemActionPerformed

    if ((gameRules != null) && (gameRules.gamePhase == GamePhaseTypes.ENDGAME))
    {
        GameEndPntGraphJDialog g = new GameEndPntGraphJDialog(this, true);

        g.assignGameStats(gameRules.plyrStatsRoundList);
        g.setVisible(true);
    }
    else
    {
        MessageJDialog md = new MessageJDialog(this, true, false);

        String s = "<center><u><b>End Game Points Graph</b></u>\n\n" +
                "This is only available at the end of a game.\n" +
                "\n";
        md.setText(s, false);
        md.setVisible(true);
    }
}//GEN-LAST:event_endGameGraphMenuItemActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed

    BrowserControl.displayURL("http://solitairecatan.wiki.sourceforge.net");
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void compPlayTestMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compPlayTestMenuItemActionPerformed

for (int c = 0; c < 50; c++)    
{                       
        dbgLevel = DebugLevel.NONE;
        soundFX  = false;

        try
        {
            // Remove existing log entries
            if (logView != null)
            {
                logView.clrLog();
            // Start a new Catan Game...
            }
            gameRules = new GameRules();
            gameRules.gameCompTesting = true;
            gameBoard.gameWindow      = this;
            gameBoard.hlAssistActive  = false;

            gameBoard.buildInitialCatanScene(GameTypes.STANDARD4,
                                             GameVariants.NORMAL,
                                             true,
                                             true);
            gameBoard.paintImmediately();

            turnMode(TurnMode.NONE);

            switch (4)
            {
                case 4:
                    gameRules.newGame(PlayerTypes.COMPUTER,
                                      PlayerTypes.COMPUTER,
                                      PlayerTypes.COMPUTER,
                                      PlayerTypes.COMPUTER,
                                      PlayerTypes.NULL,
                                      PlayerTypes.NULL,
                                      this,
                                      CompSkillLevel.NORMAL,
                                      GameTypes.STANDARD4,
                                      GameVariants.NORMAL,
                                      0);
                    
                    // set the build priority for each com player
                    CompBuildPriorities bp[] = {CompBuildPriorities.BUILD_PRI_1, CompBuildPriorities.BUILD_PRI_4, CompBuildPriorities.BUILD_PRI_1,CompBuildPriorities.BUILD_PRI_4};
                    CompAIType      ai[] = {CompAIType.HUERISTIC, CompAIType.HUERISTIC, CompAIType.HIGHSCORE, CompAIType.HIGHSCORE};
                    
                    int i = 0;
                    for (Player p:gameRules.players)
                    {
                        p.compAIType        = ai[i];
                        p.compBuildPriority = bp[i];
                        i++;
                    }
                    break;

                default:
                    return;
            }

            gameRules.tradeDialogLastPos = this.tradeDialogLastPos; // assign prefs

            dbgViewCardsChkBoxMenuItemActionPerformed(null);

            vicPointsInfo.assignPlayerList(gameRules.players);
            
            gameRules.playNextPhase();
            for (;;)
            {
                gameRules.playEndTurn();
            }
        }
        catch (CatanEndGameException e)
        {
            // this.DebugErr(gameRules.thisPlayer.name + " WON! " + " Build Priority = " + gameRules.thisPlayer.compBuildPriority.toString());
            
            // gameNo, playerWon, AI Type, Build_Pri_1, Build_Pri_4, heuristic, highscore, Game Turn No
            
            String s = c + "," + gameRules.thisPlayer.name + ", ";
            s += gameRules.thisPlayer.compAIType.toString() + ", ";
            switch (gameRules.thisPlayer.compBuildPriority)
            {
                case BUILD_PRI_1:
                    s += "1,0";
                    break;
                case BUILD_PRI_4:
                    s += "0,1";
                    break;                                    
            }
            switch (gameRules.thisPlayer.compAIType)
            {
                case HUERISTIC:
                    s += ", 1,0";
                    break;
                    
                case HIGHSCORE:
                    s += ", 0,1";
                    break;                
            }
            
            s += ", " + gameRules.gameTurnNo;
            this.DebugErr(s);
        }
}
}//GEN-LAST:event_compPlayTestMenuItemActionPerformed

    // ---------------------------------------------------------------------------
    public void diceRoll()
    {
        for (int i = 10; i > 0; i--)
        {
            dieLeft.rollDie();
            dieLeft.paintImmediately();
            if (this.gameRules.gameCompTesting == false)
            {                
                try { Thread.sleep(25); } catch (InterruptedException e) {}
            }
            
            dieRight.rollDie();
            dieRight.paintImmediately();
            
            if (this.gameRules.gameCompTesting == false)
            {                
                try { Thread.sleep(25); } catch (InterruptedException e) {}
            }
        }
    }

    public void leftRoll()
    {
        for (int i = 20; i > 0; i--)
        {
            dieLeft.rollDie();
            dieLeft.paintImmediately();
            if (this.gameRules.gameCompTesting == false)
            {                
                try { Thread.sleep(25); } catch (InterruptedException e) {}
            }
        }
    }

    public void nextActionEnable(boolean enable)
    {
        nextAction.setEnabled(enable);
    }

    public void turnMode(TurnMode turnMode)
    {
        currTurnMode = turnMode;

        switch (currTurnMode)
        {
            case NONE:
                nextAction.setIcon(icons[0]);
                nextAction.setText("Roll Dice");
                nextActionEnable(false);
                break;

            case DICEROLL:
                nextAction.setIcon(icons[0]);
                nextAction.setText("Roll Dice");
                nextActionEnable(true);
                break;

            case ENDTURN:
                nextAction.setIcon(icons[1]);
                nextAction.setText("End Turn");
                nextActionEnable(true);
                break;

            case ENDTRADE:
                nextAction.setIcon(icons[2]);
                nextAction.setText("End Trade");
                nextActionEnable(true);
                break;

            case ENDOUTOFTURNBUILD:
                nextAction.setIcon(icons[3]);
                nextAction.setText("End Build");
                nextActionEnable(true);
                break;
        }
    }

    public int diceGetValue()
    {
        return dieLeft.currValue + dieRight.currValue;
    }

    /** Removes all player tabs without leaving stale tab-layout state in the UI delegate. */
    public void clearPlayerTabs()
    {
        while (playerInfo.getTabCount() > 0)
        {
            playerInfo.removeTabAt(playerInfo.getTabCount() - 1);
        }
        resetPlayerTabLayout();
    }

    /** Forces the tabbed pane UI to recalculate tab geometry after tab count changes. */
    public void resetPlayerTabLayout()
    {
        playerInfo.invalidate();
        if (playerInfo.getTabCount() > 0 && playerInfo.getSelectedIndex() >= playerInfo.getTabCount())
        {
            playerInfo.setSelectedIndex(0);
        }
        playerInfo.revalidate();
        playerInfo.repaint();
    }

    // ---------------------------------------------------------------------------
    public void newGame()
    {
        System.gc();

        GameNewJDialog ngd = new GameNewJDialog(this, true);

        ngd.setUpLike(lastNGD); // set up game settings like last game.               

        ngd.setVisible(true);

        if (ngd.startGame == false)
        {
            return;
        }
        try
        {
            // Remove existing log entries
            if (logView != null)
            {
                logView.clrLog();
            // Start a new Catan Game...
            }
            gameRules = new GameRules();
            gameBoard.gameWindow = this;                                       
            gameBoard.hlAssistActive = ngd.hlAssistChkBox.isSelected();

            gameBoard.buildInitialCatanScene(GameTypes.toGameType(ngd.gameTypeComBox.getSelectedIndex()),
                    GameVariants.toGameVariant(ngd.gameVariantComBox.getSelectedIndex()),
                    ngd.resERandRadButton.isSelected(),
                    ngd.portERandRadButton.isSelected());
            gameBoard.paintImmediately();

            turnMode(TurnMode.NONE);

            switch (ngd.playerNo)
            {
                case 3:
                    gameRules.newGame(ngd.blueComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.redComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.whiteComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            PlayerTypes.NULL,
                            PlayerTypes.NULL,
                            PlayerTypes.NULL,
                            this,
                            ngd.skillLevel,
                            GameTypes.toGameType(ngd.gameTypeComBox.getSelectedIndex()),
                            GameVariants.toGameVariant(ngd.gameVariantComBox.getSelectedIndex()),
                            ngd.AITypeComBox.getSelectedIndex());
                    break;

                case 4:
                    gameRules.newGame(ngd.blueComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.redComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.whiteComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.orangeComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            PlayerTypes.NULL,
                            PlayerTypes.NULL,
                            this,
                            ngd.skillLevel,
                            GameTypes.toGameType(ngd.gameTypeComBox.getSelectedIndex()),
                            GameVariants.toGameVariant(ngd.gameVariantComBox.getSelectedIndex()),
                            ngd.AITypeComBox.getSelectedIndex());
                    break;

                case 5:
                    gameRules.newGame(ngd.blueComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.redComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.whiteComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.orangeComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.greenComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            PlayerTypes.NULL,
                            this,
                            ngd.skillLevel,
                            GameTypes.toGameType(ngd.gameTypeComBox.getSelectedIndex()),
                            GameVariants.toGameVariant(ngd.gameVariantComBox.getSelectedIndex()),
                            ngd.AITypeComBox.getSelectedIndex());
                    break;

                case 6:
                    gameRules.newGame(ngd.blueComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.redComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.whiteComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.orangeComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.greenComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            ngd.yellowComBox.getSelectedIndex() == 0 ? PlayerTypes.HUMAN : PlayerTypes.COMPUTER,
                            this,
                            ngd.skillLevel,
                            GameTypes.toGameType(ngd.gameTypeComBox.getSelectedIndex()),
                            GameVariants.toGameVariant(ngd.gameVariantComBox.getSelectedIndex()),
                            ngd.AITypeComBox.getSelectedIndex());
                    break;
                default:
                    return;
            }

            lastNGD = ngd;
            gameRules.tradeDialogLastPos = this.tradeDialogLastPos; // assign prefs

            dbgViewCardsChkBoxMenuItemActionPerformed(null);

            vicPointsInfo.assignPlayerList(gameRules.players);
            gameRules.playNextPhase();
        }
        catch (CatanEndGameException e)
        {
        }
    }
    
    public void updateWindowImmediately()
    {

        this.gameArena.paintImmediately(gameArena.getBounds());
        //this.gameArena.paint(this.gameArena.getGraphics());
        //this.paint(this.getGraphics());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JCheckBoxMenuItem aiPromptingChkBoxMenuItem;
    private javax.swing.JMenuItem compPlayTestMenuItem;
    private javax.swing.JRadioButtonMenuItem dbgBriefRadMenuItem;
    private javax.swing.JRadioButtonMenuItem dbgFullRadMenuItem;
    private javax.swing.JRadioButtonMenuItem dbgMediumRadMenuItem;
    private javax.swing.JRadioButtonMenuItem dbgNoneRadMenuItem;
    private javax.swing.JCheckBoxMenuItem dbgViewCardsChkBoxMenuItem;
    private javax.swing.JMenu debugMenu;
    private javax.swing.ButtonGroup debugMenuGroup;
    private javax.swing.JCheckBoxMenuItem detailLevelMenuItem;
    public Catan.DieJPanel dieLeft;
    public Catan.DieJPanel dieRight;
    private javax.swing.JMenuItem endGameGraphMenuItem;
    public javax.swing.JPanel gameArena;
    private javax.swing.JMenu gameMenu1;
    public javax.swing.JTextPane gameStatusBar;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenuItem newGameMenuItem;
    private javax.swing.JButton nextAction;
    public javax.swing.JTabbedPane playerInfo;
    private javax.swing.JMenuItem profEasyMenuItem;
    private javax.swing.JMenuItem quickTradeHelpMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JMenuItem showPntsGraphMenuItem;
    private javax.swing.JCheckBoxMenuItem soundFXMenuItem;
    private javax.swing.JSplitPane splitPane;
    private Catan.VicPointsInfoJPanel vicPointsInfo;
    private javax.swing.JButton viewLog;
    // End of variables declaration//GEN-END:variables
}
