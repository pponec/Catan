/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Catan;

import Catan.Partical.ExplodeTypes;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

enum GameTypes      {STANDARD4(0), STANDARD6_NO_ENDTURNBLD(1), STANDARD6_ENDTURNBLD(2);
                     private int value;
                     private GameTypes (int v) {value = v;};
                     public int toValue () {return value;}
                     
                     static public GameTypes toGameType (int gt)
                     {
                         switch (gt)
                         {
                             case 0: return GameTypes.STANDARD4;
                             case 1: return GameTypes.STANDARD6_NO_ENDTURNBLD;
                             case 2: return GameTypes.STANDARD6_ENDTURNBLD;
                         }
                         return GameTypes.STANDARD4;
                     }
                     public String toString ()
                     {
                         switch (value)
                         {
                             case 0: return "Std 4 Plyr Catan";
                             case 1: return "Std 6 Plyr Catan (Only build on player's turn)";
                             case 2: return "Std 6 Plyr Catan (Normal)";
                         }
                         return "Invalid";
                     }
                    };
                     
enum GameVariants   { NORMAL(0), VOLCANO(1);
                     private int value;
                     private GameVariants (int v) {value = v;};
                     public int toValue () {return value;}
                     
                     static public GameVariants toGameVariant (int gv)
                     {
                         switch (gv)
                         {
                             case 0: return GameVariants.NORMAL;
                             case 1: return GameVariants.VOLCANO;
                         }
                         return GameVariants.NORMAL;
                     }    
                     public String toString ()
                     {
                         switch (value)
                         {
                             case 0: return "Normal";
                             case 1: return "Volcano";
                         }
                         return "Invalid";
                     };                     
                    };

enum GamePhaseTypes { NULL, 
                      START_PLACEMENT_1, 
                      START_PLACEMENT_2, 
                     
                      ROLLDICE, 
                      GIVE_RESOURCE,
                      
                      ROLL_7_ROBBER, 
                      PLACE_ROBBER,
                      
                      TRADE_BUILD,
                      TRADE_WITHPLYRS,
                      
                      OUTOFTURNBUILD, // used in 6 player catan
                      
                      BUILD_ROAD, 
                      BUILD_SETTLEMENT,
                      BUILD_CITY,
                                         
                      DEV_ARMY,
                      DEV_ROAD_BUILD_1,
                      DEV_ROAD_BUILD_2,                      
                                       
                      ENDGAME                                          
                                                             
                   };

enum AudioClipTypes {ERROR(0), ROAD(1), SETTLEMENT(2), CITY(3), WIN(4), LOSE(5), ROBBER(6),
                     LONGEST_ROAD(7), LARGEST_ARMY(8), DEVCARD(9), DICEROLL(10), EXPLODE(11);
                     private int value;
                     private AudioClipTypes (int v) {value = v;};
                     public int toValue () {return value;}
                    };
enum BuildCursors {NORMAL, SETTLEMENT, CITY, ROAD};


/**
 *
 * @author steven
 */
public class GameRules implements GameMouseNotifyInterf
{  
    public class DiceRollInfo
    {
        class ResGiven
        {
            ResCardTypes resType = ResCardTypes.NULL;            
            int          resNo   = 0;            
            ResGiven (ResCardTypes resType, int resNo)
            {
                this.resType = resType;
                this.resNo   = resNo;
            }
        }
        
        int                  diceNo   = 0;        
        LinkedList<ResGiven> resGiven = new LinkedList<ResGiven>();
        
        public DiceRollInfo (int diceNo)
        {
            this.diceNo   = diceNo;
        }
        
        public void add (ResCardTypes rct, int no)
        {
            resGiven.add(new ResGiven(rct,no));
        }
        
        public int count (ResCardTypes rct)
        {
            int num = 0;
            for (ResGiven rg:resGiven)
            {
                if (rg.resType == rct)
                    num += rg.resNo;
            } 
            return num;
        }
    }
    
    public class StatsPlyr
    {
        Color col   = Color.BLACK;
        int   tpnts = 0;
        StatsPlyr (Color c, int tp) {col = c; tpnts = tp;}
    }
    
    public class StatsRound
    {
        LinkedList<StatsPlyr> plyrs = new LinkedList<StatsPlyr>();        
    }
    
    public LinkedList<StatsRound>   plyrStatsRoundList = new LinkedList<StatsRound>();       
    public LinkedList<Player>       players       = new LinkedList<Player>();
    public Player                   thisPlayer        = null;
    public Player                   contextPlayerSave = null;
    public String                   log           = "";
    
    public int                      playOrderIdx = 0;
    public Random                   rand         = new Random();
    public CatanJFrame              gameWindow   = null;        
    public GamePhaseTypes           gamePhase    = GamePhaseTypes.NULL;    
    public LinkedList<ResourceCard> gameDevCards = new LinkedList<ResourceCard>();
    public int                      gameTurnNo   = 0;
    
    public LinkedList<DiceRollInfo> turnDiceRolls   = new LinkedList<DiceRollInfo>();
    public boolean                  msgLogPrompting = false;
    public GameTypes                gameType        = GameTypes.STANDARD4;
    public GameVariants             gameVariant     = GameVariants.NORMAL;
    public boolean                  gameCompTesting = false;
    
    public Point                    tradeDialogLastPos = new Point (-1, -1);
        
    public GameRules ()            
    {        
    }           
    
    // AIPlayType :  0 mixed, 1 hueristics, 2 high score
    public boolean newGame (PlayerTypes    plyr1, 
                            PlayerTypes    plyr2, 
                            PlayerTypes    plyr3, 
                            PlayerTypes    plyr4, 
                            PlayerTypes    plyr5, 
                            PlayerTypes    plyr6, 
                            CatanJFrame    cf,
                            CompSkillLevel skillLevel,
                            GameTypes      gameType,
                            GameVariants   gameVariant,
                            int            AIPlayType) throws CatanEndGameException
    {
        CompBuildPriorities cbp[] = {CompBuildPriorities.BUILD_PRI_1, CompBuildPriorities.BUILD_PRI_4, 
                                     CompBuildPriorities.BUILD_PRI_1, CompBuildPriorities.BUILD_PRI_4, 
                                     CompBuildPriorities.BUILD_PRI_1, CompBuildPriorities.BUILD_PRI_4};
        
        LinkedList<CompAIType> AIPlayList = new LinkedList<CompAIType>();
        switch (AIPlayType)
        {
            case 1:
                for (int i = 0; i < 6; i++)                    
                    AIPlayList.add(CompAIType.HUERISTIC);
                break;                
            case 2:
                for (int i = 0; i < 6; i++)                    
                    AIPlayList.add(CompAIType.HIGHSCORE);
                break;
            default:
                for (int i = 0; i < 6; i++)                    
                {
                    if (this.rand.nextInt(2) == 0)                    
                        AIPlayList.add(CompAIType.HIGHSCORE);
                    else
                        AIPlayList.add(CompAIType.HUERISTIC);                    
                }
                break;
        }        
                
        this.gameType    = gameType;
        this.gameVariant = gameVariant;
        
        // Load development cards
        gameDevCards.clear();
        
        switch (gameType)
        {
            case STANDARD4:
                // Add army cards
                for (int count = 0; count < 14; count++)
                    gameDevCards.add (new ResourceCard(ResCardTypes.DEV_ARMY));

                // Add resource gathering cards
                for (int count = 0; count < 2; count++)
                {
                    gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_MONOPOLY));
                    gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_YEAROFPLENTY));
                    gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_ROADBUILD));
                }        

                // Add victory point cards
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_CHAPEL));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_PALACE));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_MARKET));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_LIBRARY));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_UNIVERSITY));
                break;
                            
            case STANDARD6_NO_ENDTURNBLD:
            case STANDARD6_ENDTURNBLD:
                // Add army cards
                for (int count = 0; count < 20; count++)
                    gameDevCards.add (new ResourceCard(ResCardTypes.DEV_ARMY));

                // Add resource gathering cards
                for (int count = 0; count < 3; count++)
                {
                    gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_MONOPOLY));
                    gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_YEAROFPLENTY));
                    gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_ROADBUILD));
                }        

                // Add victory point cards
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_CHAPEL));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_PALACE));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_MARKET));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_LIBRARY));
                gameDevCards.add(rand.nextInt(gameDevCards.size()), new ResourceCard(ResCardTypes.DEV_VP_UNIVERSITY));                
                break;
        }
                
        
        LinkedList<Player> playOrder = new LinkedList<Player>(); 
         
        gameWindow = cf;
        
        players.clear();
        plyrStatsRoundList.clear();
        
        gameWindow.playerInfo.removeAll();
                        
        if (plyr1 != PlayerTypes.NULL)
            players.add(new Player(plyr1, PlayerColTypes.BLUE.name(),   PlayerColTypes.BLUE,   gameWindow.gameBoard, this,  new ResBuildPanel(), skillLevel, AIPlayList.removeFirst(), cbp[0]));
        if (plyr2 != PlayerTypes.NULL)
            players.add(new Player(plyr2, PlayerColTypes.RED.name(),    PlayerColTypes.RED,    gameWindow.gameBoard, this,  new ResBuildPanel(), skillLevel, AIPlayList.removeFirst(), cbp[1]));
        if (plyr3 != PlayerTypes.NULL)
            players.add(new Player(plyr3, PlayerColTypes.WHITE.name(),  PlayerColTypes.WHITE,  gameWindow.gameBoard, this,  new ResBuildPanel(), skillLevel, AIPlayList.removeFirst(), cbp[2]));
        if (plyr4 != PlayerTypes.NULL)
            players.add(new Player(plyr4, PlayerColTypes.ORANGE.name(), PlayerColTypes.ORANGE, gameWindow.gameBoard, this,  new ResBuildPanel(), skillLevel, AIPlayList.removeFirst(), cbp[3]));               
        if (plyr5 != PlayerTypes.NULL)
            players.add(new Player(plyr5, PlayerColTypes.GREEN.name(),  PlayerColTypes.GREEN,  gameWindow.gameBoard, this,  new ResBuildPanel(), skillLevel, AIPlayList.removeFirst(), cbp[4]));        
        if (plyr6 != PlayerTypes.NULL)
            players.add(new Player(plyr6, PlayerColTypes.YELLOW.name(), PlayerColTypes.YELLOW, gameWindow.gameBoard, this,  new ResBuildPanel(), skillLevel, AIPlayList.removeFirst(), cbp[5]));
        
        // determine random play order.
        for (int num = players.size(); num > 0; num--)
        {
            Player p = players.get(rand.nextInt(players.size()));
            
            while (playOrder.contains(p) != false)
                  p = players.get(rand.nextInt(players.size()));
            
            playOrder.add(p);
        }               
        
        // Add players to player info tab         
        for (Player p:playOrder)
            gameWindow.playerInfo.add(p.col.name() + " Player", p.resPanelInfo);                        
        
        // Make the first human tab as initially selected.
        for (Player p:playOrder)
        {
            if (p.type == PlayerTypes.HUMAN)
            {
                gameWindow.playerInfo.setSelectedComponent(p.resPanelInfo);
                break;
            }         
        }                           

        // Set the initial players and start index        
        players      = playOrder;                
        playOrderIdx = 0;
        thisPlayer   = (Player) players.get(playOrderIdx);         
        setGamePhase(GamePhaseTypes.START_PLACEMENT_1);       
  
        
        // Display computer AI type 
        for (Player p:playOrder)
        {            
            if (p.type == PlayerTypes.COMPUTER)
            {
                this.setMsgLog(p.name + " is " + p.type.toString() + " A.I using method " + p.compAIType.toString() + " and build priority " + p.compBuildPriority.toString());
            }            
        }             
/*
thisPlayer   = (Player) players.get(0); 
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.WOOD));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.BRICK));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.WOOD));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.BRICK));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.WOOD));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.BRICK));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.WOOD));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.BRICK));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.WOOD));
thisPlayer.resCards.add (new ResourceCard(ResCardTypes.BRICK));
thisPlayer.newDevCards.add (new ResourceCard(ResCardTypes.DEV_ROADBUILD));
thisPlayer.newDevCards.add (new ResourceCard(ResCardTypes.DEV_ROADBUILD));
thisPlayer.newDevCards.add (new ResourceCard(ResCardTypes.DEV_ROADBUILD));
thisPlayer.newDevCards.add (new ResourceCard(ResCardTypes.DEV_ROADBUILD));
thisPlayer.newDevCards.add (new ResourceCard(ResCardTypes.DEV_ROADBUILD));
*/

        return true;
    }   
    
    public void addStatsRound ()
    {
        StatsRound sr = new StatsRound();
        
        for (Player p:players)
            sr.plyrs.add(new StatsPlyr(p.col.toCol(), p.vicPntsTotal));
        
        plyrStatsRoundList.add(sr);
    }
    
    public void contextSwapOutEndTurnBuild ()
    {   
        Player s   = thisPlayer;
        thisPlayer = contextPlayerSave;
        contextPlayerSave = s;
    }
           
    public void contextSwapInEndTurnBuild (Player swapThisPlayer)
    {        
        contextPlayerSave = thisPlayer;
        thisPlayer = swapThisPlayer;
    }
            
    public boolean playOutOfTurnBuild (boolean matchContextSwappedPlayer) throws CatanEndGameException
    {
        // -------------------------------------------
        // Determine if this is a end-turn build game
        // -------------------------------------------
        switch (gameType)
        {
            case STANDARD6_ENDTURNBLD:
                // Ask next player till the end if they want to build.
                boolean canPlay = false;
                boolean contextMatched = false;
                for (Player p:players)
                {
                    if (matchContextSwappedPlayer != false)
                    {
                        if ((p == this.contextPlayerSave) && (contextMatched != false))
                        {                            
                            canPlay = true;
                            continue;
                        }
                        else if ((p == thisPlayer) && (contextMatched == false))
                        {
                            matchContextSwappedPlayer = false;
                            contextMatched            = true;
                        }
                    }
                    else if (p == thisPlayer) 
                    {
                        canPlay = true;
                        continue;
                    }
                    
                    if (canPlay != false)
                    {
                        // Only prompt if we can actually build/buy something?
                        int canBuild  = p.canBuild(CanBuildTypes.CITY);
                        canBuild  += p.canBuild(CanBuildTypes.SETTLEMENT);
                        canBuild  += p.canBuild(CanBuildTypes.ROAD);
                        canBuild  += p.canBuild(CanBuildTypes.DEVCARD);
                        
                        if (canBuild <= 0)
                            continue;
                        
                        if (p.type == PlayerTypes.COMPUTER)
                        {
                            TurnMode s = this.gameWindow.currTurnMode;
                            this.gameWindow.currTurnMode = TurnMode.ENDOUTOFTURNBUILD;
                            if (p.COMP_PlayTurn_EndTurnBuild () != false)
                                gameWindow.updateWindowImmediately();
                            this.gameWindow.currTurnMode = s;
                        }
                        else 
                        {
                            // Ask to build something
                            MessageJDialog  m = new MessageJDialog (gameWindow, true, true);
                                                        
                            m.setUndecorated (false);
                            m.setText        ("Do you wish to do an\nout-of-turn build?", true); 
                            m.setSize        (m.getPreferredSize());
                            m.centreToPanel  (this.gameWindow.playerInfo);
                            m.setVisible     (true);
                            if (m.userYesNoSel != false)
                            {
                                this.contextSwapInEndTurnBuild(p);
                                this.setGamePhase(GamePhaseTypes.OUTOFTURNBUILD);
                                return true;
                            }
                        }
                    }                                
                }

                // Do the rest of the player from the start.
                for (Player p:players)
                {
                    if (matchContextSwappedPlayer != false)
                    {
                        if (p == this.contextPlayerSave)
                        {                            
                            matchContextSwappedPlayer = false;
                            canPlay = true;
                            continue;
                        }
                    }
                    else if (p == thisPlayer) 
                    {
                        canPlay = true;
                        break;
                    }
                    
                    if (canPlay != false)
                    {
                        // Only prompt if we can actually build/buy something?
                        int canBuild  = p.canBuild(CanBuildTypes.CITY);
                        canBuild  += p.canBuild(CanBuildTypes.SETTLEMENT);
                        canBuild  += p.canBuild(CanBuildTypes.ROAD);
                        canBuild  += p.canBuild(CanBuildTypes.DEVCARD);
                        
                        if (canBuild <= 0)
                            continue;
                        
                        if (p.type == PlayerTypes.COMPUTER)
                        {
                            TurnMode s = this.gameWindow.currTurnMode;
                            this.gameWindow.currTurnMode = TurnMode.ENDOUTOFTURNBUILD;
                            if (p.COMP_PlayTurn_EndTurnBuild () != false)
                                gameWindow.updateWindowImmediately();
                            this.gameWindow.currTurnMode = s;                                                        
                        }
                        else 
                        {                            
                            // Ask to build something
                            MessageJDialog  m = new MessageJDialog (gameWindow, true, true);

                            m.setUndecorated (false);
                            m.setText        ("Do you wish to do an\nout-of-turn build?", true);  
                            m.setSize        (m.getPreferredSize());
                            m.centreToPanel  (this.gameWindow.playerInfo);                            
                            m.setVisible     (true);
                            
                            if (m.userYesNoSel != false)
                            {
                                this.contextSwapInEndTurnBuild(p);
                                this.setGamePhase(GamePhaseTypes.OUTOFTURNBUILD);
                                return true;
                            }                            
                        }
                    }                                
                }                        
                break;
        }            
        
        return false;
    }
        
    public void playEndTurn () throws CatanEndGameException
    {               
        thisPlayer.calcVictoryPoints ();
                        
        switch (gamePhase)
        {
            case START_PLACEMENT_1:                                 
                if (playOrderIdx >= (players.size()-1))
                {                    
                    setGamePhase(GamePhaseTypes.START_PLACEMENT_2);                  
                }
                else
                {
                    playOrderIdx++;
                    thisPlayer = (Player) players.get(playOrderIdx);
                    this.gameWindow.gameBoard.clearHighlightAssist ();
                    if (thisPlayer.type == PlayerTypes.HUMAN)
                        setBuildingCursor(BuildCursors.SETTLEMENT);
                }
                break;

            case START_PLACEMENT_2:                 
                // Give out resources for the second placement of the building.                
                thisPlayer.giveInitialResources();
                if (playOrderIdx > 0)
                {                   
                    playOrderIdx--;                        
                    setGamePhase(GamePhaseTypes.START_PLACEMENT_2);
                    thisPlayer = (Player) players.get(playOrderIdx);
                    this.gameWindow.gameBoard.clearHighlightAssist ();
                    
                    if (thisPlayer.type == PlayerTypes.HUMAN)
                        setBuildingCursor(BuildCursors.SETTLEMENT);
                    else 
                        setBuildingCursor(BuildCursors.NORMAL);
                }
                else
                {       
                    this.gameWindow.gameBoard.blinkBGInit();                   
                    Tile t = this.gameWindow.gameBoard.setRobberInitLoc ();
                    this.gameWindow.gameBoard.clrDblBuffCache();
                    this.gameWindow.gameBoard.blinkBGObj(700, (CatanGraphBase)t, null);                      
                   
                    this.addStatsRound ();
                    setGamePhase(GamePhaseTypes.ROLLDICE);
                }
                break;    
                
            case OUTOFTURNBUILD: 
                // Swap context back...
                contextSwapOutEndTurnBuild ();                
                if (playOutOfTurnBuild (true) != false) // if more building ... then go around again.
                    break;                   
            case TRADE_WITHPLYRS:
            case TRADE_BUILD:                   
                // -------------------------------------------
                // Determine if this is a end-turn build game
                // -------------------------------------------                
                if (gamePhase != GamePhaseTypes.OUTOFTURNBUILD)
                {
                    if (playOutOfTurnBuild (false) != false) // if building ... then go wait until human player is finished.
                        break;                                                  
                }
                
                thisPlayer.resPanelInfo.updatePurchanseActions();                                                                
                
                if (playOrderIdx >= (players.size()-1))
                {                    
                    gameTurnNo++;                    
                    this.setLog("--- End Of Round (" + gameTurnNo + ") ---");
                    playOrderIdx = 0;  
                    this.addStatsRound ();
                }
                else
                    playOrderIdx++;                        
        
                thisPlayer = (Player) players.get(playOrderIdx);
                this.gameWindow.gameBoard.clearHighlightAssist ();
                this.setGamePhase(GamePhaseTypes.ROLLDICE);
                break;
        }
                            
        gameWindow.repaint();
        playNextPhase();
    }

    public void setGamePhase (GamePhaseTypes gp) throws CatanEndGameException
    {
        this.gamePhase = gp;
                
        switch (gamePhase)
        {
            case START_PLACEMENT_1:                    
                    setBuildingCursor(BuildCursors.SETTLEMENT);
                    break;
                    
            case START_PLACEMENT_2:
                    setBuildingCursor(BuildCursors.SETTLEMENT);
                    break;
                                        
            case ROLLDICE:                   
                setBuildingCursor(BuildCursors.NORMAL);
                this.gameWindow.gameBoard.clearHighlightAssist();
                thisPlayer.devCardPlayedOnTurn = 0;
                if (thisPlayer.type == PlayerTypes.HUMAN)  
                {                       
                    gameWindow.turnMode(TurnMode.ENDTRADE);
                    for (Player p:players)
                    {
                        if (p == thisPlayer) 
                            continue;
                        if (p.type == PlayerTypes.HUMAN)
                            p.resPanelInfo.refreshPlayerInfo();
                    }                    
                    
                    gameWindow.turnMode(TurnMode.DICEROLL);                                                          
                    thisPlayer.resPanelInfo.setEnabled(false);                    
                }
                else
                {
                    gameWindow.turnMode(TurnMode.ENDTRADE);
                }
                break;
                
            case ROLL_7_ROBBER:
                if (thisPlayer.type == PlayerTypes.HUMAN) 
                {
                    gameWindow.nextActionEnable (false);
                }
                break;
                                
            case TRADE_BUILD:                
                // If in out-of-turn build phase then go back into that phase (Catan 5-6 game type)
                switch (gameWindow.currTurnMode)
                {
                    case ENDOUTOFTURNBUILD:
                        setGamePhase(GamePhaseTypes.OUTOFTURNBUILD);                                    
                        return;
                }
            
                this.gameWindow.gameBoard.clearHighlightAssist ();
                setBuildingCursor(BuildCursors.NORMAL);
                thisPlayer.resPanelInfo.setEnabled(true);                                                
                if (thisPlayer.type == PlayerTypes.HUMAN)              
                {
                    gameWindow.turnMode(TurnMode.ENDTURN);
                    thisPlayer.resPanelInfo.isBuilding = false;
                    thisPlayer.resPanelInfo.updatePurchanseActions();                      
                }    
                else
                {
                    setGamePhase (GamePhaseTypes.TRADE_WITHPLYRS);
                    
                    ResCardTypes  lastTradByRef[] = {ResCardTypes.NULL, ResCardTypes.NULL, ResCardTypes.NULL};
                    thisPlayer.COMP_PlayTurn (0, lastTradByRef);                                                              
                }
                break;
                
            case OUTOFTURNBUILD:
                setBuildingCursor(BuildCursors.NORMAL);
                if (thisPlayer.type == PlayerTypes.HUMAN)              
                {
                    gameWindow.turnMode(TurnMode.ENDOUTOFTURNBUILD);
                    thisPlayer.resPanelInfo.isBuilding = false;
                    thisPlayer.resPanelInfo.updatePurchanseActions(); 
                }
                else
                {
                    this.playEndTurn();
                }
                break;
                
            case TRADE_WITHPLYRS:
                setBuildingCursor(BuildCursors.NORMAL);
                this.gameWindow.turnMode(TurnMode.ENDTRADE);
                for (Player p:players)
                {
                    if (p.type == PlayerTypes.HUMAN)
                        p.resPanelInfo.refreshPlayerInfo();
                }
                break;                
                                
            case BUILD_CITY:
                setLog (thisPlayer.name + " select an existing settlement to upgrade to a city");
                if (thisPlayer.type == PlayerTypes.HUMAN)
                {
                    setBuildingCursor(BuildCursors.CITY);
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.BUILDPOINT);                                        
                    gameWindow.nextActionEnable (false); 
                }
                break;
                
            case BUILD_SETTLEMENT:
                setLog (thisPlayer.name + " select a road point to build a new settlement");
                if (thisPlayer.type == PlayerTypes.HUMAN)
                {
                    setBuildingCursor(BuildCursors.SETTLEMENT);                   
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.BUILDPOINT);                                        
                    gameWindow.nextActionEnable (false); 
                }
                break;                                
                
            case BUILD_ROAD:                
                    setLog (thisPlayer.name + " select a road to build");
                    if (thisPlayer.type == PlayerTypes.HUMAN)
                    {
                        setBuildingCursor(BuildCursors.ROAD);                        
                        gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.ROAD);                                        
                        gameWindow.nextActionEnable (false); 
                    }
                    break;
                    
            case DEV_ARMY:
                setLog (thisPlayer.name + " move the robber to a new tile");
                if (thisPlayer.type == PlayerTypes.HUMAN)
                {
                    setGamePhase(GamePhaseTypes.PLACE_ROBBER);                    
                    setRobberCursor (true);
                    gameWindow.nextActionEnable (false); 
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.TILE);                            
                }
                break;
                
            case DEV_ROAD_BUILD_1:
                setLog (thisPlayer.name + " select a road to build (1)");
                if (thisPlayer.type == PlayerTypes.HUMAN)
                {
                    setBuildingCursor(BuildCursors.ROAD);                    
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.ROAD);                                        
                    gameWindow.nextActionEnable (false);                     
                }
                break;
                
            case DEV_ROAD_BUILD_2:
                setLog (thisPlayer.name + " select a road to build (2)");
                if (thisPlayer.type == PlayerTypes.HUMAN)
                {
                    setBuildingCursor(BuildCursors.ROAD);                    
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.ROAD);                                        
                    gameWindow.nextActionEnable (false);                     
                }
                break;                
                
            case ENDGAME:
                gameWindow.setCursor(Cursor.getDefaultCursor()); 
                this.addStatsRound();                
                break;
        }
       
        gameWindow.repaint();
    }
    
    public void playNextPhase () throws CatanEndGameException
    {                 
        switch (gamePhase)
        {
            case ROLLDICE:               
                // this.playSound(AudioClipTypes.DICEROLL);
                gameWindow.diceRoll();                      
                setGamePhase(GamePhaseTypes.GIVE_RESOURCE);                             
                
                if (thisPlayer.type == PlayerTypes.HUMAN)                    
                    gameWindow.nextActionEnable (true);
                
                this.playNextPhase();                
                break;
            
            case START_PLACEMENT_1:                 
                if (thisPlayer.type == PlayerTypes.COMPUTER)
                {
                    thisPlayer.COMP_InitPlaceSettlement();
                    this.playEndTurn();                                                      
                }
                else
                {
                    setLog (thisPlayer.name + " select a build point");
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.BUILDPOINT);
                }
                break;
            
            case START_PLACEMENT_2:                 
                if (thisPlayer.type == PlayerTypes.COMPUTER)
                {
                    thisPlayer.COMP_InitPlaceSettlement();                                  
                    this.playEndTurn();

                }
                else
                {
                    setLog (thisPlayer.name + " select a build point");
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.BUILDPOINT);
                }
                break;
                                                                
            case GIVE_RESOURCE:                                  
                setLog (thisPlayer.name + " rolled " + gameWindow.diceGetValue());
                             
                // Process Dice roll of 7 / Robber event
                if (gameWindow.diceGetValue() == 7)                   
                {
                    setGamePhase(GamePhaseTypes.ROLL_7_ROBBER);                        
                    this.playNextPhase();
                    break;
                }  
                
                // Scan through Tiles with the associate rolled value
                int          diceRoll = gameWindow.diceGetValue();
                DiceRollInfo diceInfo = new DiceRollInfo (diceRoll);
                
                for (Tile t:gameWindow.gameBoard.tileList)
                {                    
                    if ((t.diceRoll == diceRoll) && (t.hasRobber == false))
                    {
                        // Give resource to all players that have build on this tile's border.
                        for (BuildPoint bp:t.buildJoins)
                        {                           
                            if (bp.owner != null)
                            {                                
                                String res = " ";      
                                switch (t.type)
                                {
                                    case VAR_VOLCANO:                                        
                                        if (bp.owner.type == PlayerTypes.COMPUTER)
                                        {
                                            int resNum = 0;
                                            switch (bp.type)
                                            {
                                                case CITY:                                     
                                                    resNum = 2;
                                                    break;

                                                case SETTLEMENT:
                                                    resNum = 1;
                                            }                                        

                                            LinkedList<ResourceCard> rtnList = new LinkedList<ResourceCard>();                
                                            bp.owner.COMP_CalcReceiveGoldRes (rtnList, resNum);

                                            int c = 0;
                                            for (ResourceCard rc:rtnList) 
                                            {       
                                                if (c > 0)
                                                    res += "\n ";
                                                diceInfo.add(rc.type, 1);
                                                res += bp.owner.name + "\t received Gold -> " + rc.type.toString() + " for their " + bp.type.toString();
                                                c++;
                                            }                        
                                        }
                                        else
                                        {
                                            DevCardResourcePickerJDialog dcp = null;

                                          switch (bp.type)
                                          {        
                                            case CITY:      
                                                // Pick resource for gold             
                                                dcp = new DevCardResourcePickerJDialog (this.gameWindow, true, bp.owner, DecCardResPickType.CITY_GOLD1);    
                                                while (dcp.okSelection == false)
                                                {
                                                    dcp.centreToPanel(this.gameWindow.playerInfo);
                                                    dcp.setVisible(true);
                                                }                                    
                                                break;

                                            case SETTLEMENT:
                                                // Pick resource for gold             
                                                dcp = new DevCardResourcePickerJDialog (this.gameWindow, true, bp.owner, DecCardResPickType.SETTLEMENT_GOLD);    
                                                while (dcp.okSelection == false)
                                                {
                                                    dcp.centreToPanel(this.gameWindow.playerInfo);
                                                    dcp.setVisible(true);
                                                }                                                
                                          }

                                          if (dcp != null)
                                          {
                                                int c = 0;
                                                for (ResourceCard rc:dcp.objs) 
                                                {       
                                                    if (c > 0)
                                                        res += "\n ";
                                                    diceInfo.add(rc.type, 1);
                                                    res += bp.owner.name + "\t received Gold -> " + rc.type.toString() + " for their " + bp.type.toString();
                                                    c++;
                                                }                                             
                                            }      
                                        }
                                        break;                   

                                    case BRICK:
                                        res += bp.owner.name + "\t received ";
                                        switch (bp.type)
                                        {
                                            case CITY:                                            
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.BRICK));                    
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.BRICK));                    
                                                diceInfo.add(ResCardTypes.BRICK, 2);
                                                res += "2 ";
                                                break;
                                                
                                            case SETTLEMENT:
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.BRICK));                    
                                                diceInfo.add(ResCardTypes.BRICK, 1);
                                                res += "1 ";
                                        }
                                        res += t.type.toString() + " for their " + bp.type.toString();
                                        break;                   
                                    case ROCK:
                                        res += bp.owner.name + "\t received ";
                                        switch (bp.type)
                                        {
                                            case CITY:                                            
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.ROCK));                    
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.ROCK));                    
                                                diceInfo.add(ResCardTypes.ROCK, 2);
                                                res += "2 ";
                                                break;
                                                
                                            case SETTLEMENT:
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.ROCK));                                                                    
                                                diceInfo.add(ResCardTypes.ROCK, 1);
                                                res += "1 ";
                                        }
                                        res += t.type.toString() + " for their " + bp.type.toString();
                                        break;                    
                                    case WHEAT:
                                        res += bp.owner.name + "\t received ";
                                        switch (bp.type)
                                        {
                                            case CITY:                                            
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.WHEAT));
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.WHEAT));
                                                diceInfo.add(ResCardTypes.WHEAT, 2);
                                                res += "2 ";
                                                break;
                                                
                                            case SETTLEMENT:
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.WHEAT));
                                                diceInfo.add(ResCardTypes.WHEAT, 1);
                                                res += "1 ";
                                        }
                                        res += t.type.toString() + " for their " + bp.type.toString();
                                        break;                    
                                    case WOOD:
                                        res += bp.owner.name + "\t received ";
                                        switch (bp.type)
                                        {
                                            case CITY:                                            
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.WOOD));                    
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.WOOD));                    
                                                diceInfo.add(ResCardTypes.WOOD, 2);
                                                res += "2 ";
                                                break;
                                                
                                            case SETTLEMENT:
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.WOOD));                    
                                                diceInfo.add(ResCardTypes.WOOD, 1);
                                                res += "1 ";
                                        }
                                        res += t.type.toString() + " for their " + bp.type.toString();
                                        break;                    
                                    case SHEEP:
                                        res += bp.owner.name + "\t received ";
                                        switch (bp.type)
                                        {
                                            case CITY:                                            
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.SHEEP));                    
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.SHEEP));                    
                                                diceInfo.add(ResCardTypes.SHEEP, 2);
                                                res += "2 ";
                                                break;
                                                
                                            case SETTLEMENT:
                                                bp.owner.resCards.add(new ResourceCard(ResCardTypes.SHEEP));                    
                                                diceInfo.add(ResCardTypes.SHEEP, 1);
                                                res += "1 ";
                                        }
                                        res += t.type.toString() + " for their " + bp.type.toString();
                                        break;                                        
                                }                                               
                                                                
                                setLog (res);
                            }                        
                        }
                        
                    }
                }
                
                // Do Variant Tiles (e.g volcano)
                for (Tile t:gameWindow.gameBoard.tileList)
                {       
                    if (t.diceRoll == diceRoll)
                    {
                        switch (t.type)
                        {
                            // volcano erupts to destroys a settlement/city?
                            case VAR_VOLCANO: 
                                boolean hasBuildings = false;
                                for (BuildPoint bp:t.buildJoins)
                                {
                                    if (bp.owner != null)
                                    {
                                        hasBuildings = true;
                                        break;
                                    }
                                }
                                                                
                                // Roll 1 die to determine which build point get destroyed, only if there are buildings 
                                // to destroy.
                                if (hasBuildings != false)
                                {                                    
                                    Graphics2D g2 = (Graphics2D)this.gameWindow.gameBoard.getGraphics();
                                    for (int i = 0; i < 20; i++)
                                    {
                                        gameWindow.dieLeft.rollDie();
                                        gameWindow.dieLeft.paintImmediately();

                                        int idx = (gameWindow.dieLeft.currValue - 1);
                                        BuildPoint bp = t.getBuildPointJoin(t.xpoints[idx], t.ypoints[idx]);

                                        // highlight build point 
                                        bp.highLightSelectedDraw(g2, Color.white);
                                        pause(50);
                                        gameWindow.gameBoard.paintImmediately();
                                        pause(50);
                                    } 

                                    // Blink final quickly
                                    for (int i = 0; i < 10; i++)
                                    {
                                        int idx = (gameWindow.dieLeft.currValue - 1);
                                        BuildPoint bp = t.getBuildPointJoin(t.xpoints[idx], t.ypoints[idx]);

                                        // highlight build point 
                                        bp.highLightSelectedDraw(g2, Color.white);
                                        pause(16);
                                        gameWindow.gameBoard.paintImmediately();
                                        pause(16);    
                                    }
       
                                    int ldie = gameWindow.dieLeft.currValue;
                                    setLog (" Volcano tile has errupted! Rolled : " + ldie);
                                    
                                    // Determine if destruction 
                                    Point hitPoint = new Point (t.xpoints[ldie-1], t.ypoints[ldie-1]);
                                    for (BuildPoint bp:t.buildJoins)
                                    {                                     
                                        if ((bp.contains(hitPoint) != false) && (bp.owner != null))
                                        {           
                                            String l = bp.owner.name + " " + bp.type.toString() + " was destroyed to a ";
     
                                            this.playSound(AudioClipTypes.EXPLODE);
                                            
                                            // Explode the building from the game board!
                                            Rectangle loc = new Rectangle();                                            
                                            BufferedImage splodeImg = this.gameWindow.gameBoard.splodeInit (bp, loc);
                                            bp.owner.reduceBuildObject (bp);
                                            this.gameWindow.gameBoard.splodeBuilding (120, loc, splodeImg, ExplodeTypes.toType(this.rand.nextInt(5)));                                           
                                            
                                            l += bp.type.toString();
                                            setLog (l);
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
                
                
                // Turn based dice roll stats...
                if (turnDiceRolls.size()+1 > players.size())
                    turnDiceRolls.remove(turnDiceRolls.getFirst());

                turnDiceRolls.add(diceInfo);
                
                this.setGamePhase(GamePhaseTypes.TRADE_BUILD);
                break;
                
            case ROLL_7_ROBBER:       
                if (thisPlayer.type == PlayerTypes.COMPUTER)
                    playSound (AudioClipTypes.ROBBER);
                
                // Remove resources from people with 8 or more resources, they loose half!
                for (Player p:players)
                {
                    if (p.type != PlayerTypes.HUMAN)
                        p.COMP_PurgeDiceRoll7 (); // Auto Delete computer resources cards from robber 7 roll (if need be)
                }
                
                for (Player p:players)
                {
                    if (p.resCards.size()>7)
                    {
                        if (p.type == PlayerTypes.HUMAN)
                        {
                            RobOver7CardsJDialog d = new RobOver7CardsJDialog (gameWindow, true);
                            d.centreToPanel(gameWindow.playerInfo);
                            d.assignPlayer(p);                            
                            do
                            {
                                d.setVisible(true);                           
                            } while (d.completedOK == false);
                        }
                    }
                }
                
                this.gameWindow.repaint();
                
                if (thisPlayer.type == PlayerTypes.HUMAN)
                {
                    MessageJDialog m = new MessageJDialog (gameWindow, true, false);
                    m.setText("You have rolled a 7,\n Place the Robber on a new tile", true);
                    m.centreToPanel(gameWindow.playerInfo);
                    m.setVisible(true);     
                    
                    setGamePhase(GamePhaseTypes.PLACE_ROBBER);                    
                    setRobberCursor (true);
                    gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.TILE);                     
                }
                else
                {
                    thisPlayer.COMP_MoveRobber();                    
                    this.setGamePhase(GamePhaseTypes.TRADE_BUILD);
                }                
                break;                               
        }
    }        
        
    public void GMNI_MouseSelectEvent (Object srcObj, LinkedList objsSelected, ObjSelectType types)
    {
    try
    {
        if (objsSelected == null)    
            return;
        
        if (objsSelected.size() < 0)
            return;

        switch (gamePhase)
        {
            case START_PLACEMENT_1:
            case START_PLACEMENT_2:
                switch (types)
                {
                    case BUILDPOINT:
                        BuildPoint bp = (BuildPoint) objsSelected.getFirst();
                        
                        if (isValidBuildPoint (bp, false, thisPlayer, false) == false)
                        {
                            this.playSound (AudioClipTypes.ERROR);
                            break;
                        }
                                      
                        thisPlayer.buildObject(CanBuildTypes.SETTLEMENT, bp);                        
                        setLog (thisPlayer.name + " select a road from build point");
                        gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.ROAD);                                            
                        setBuildingCursor(BuildCursors.ROAD);
                        break;
                        
                    case ROAD:
                        Road r = (Road) objsSelected.getFirst();
                        
                        if (r.owner != null)
                        {
                            this.playSound (AudioClipTypes.ERROR);
                            break;
                        }
                        
                        if (this.isValidRoadBuild(r, thisPlayer) != false)                        
                        {                            
                            thisPlayer.buildObject(CanBuildTypes.ROAD, r);                            
                            gameWindow.gameBoard.setMouseSelectionType(this, ObjSelectType.BUILDPOINT);                                                                        
                            setBuildingCursor(BuildCursors.NORMAL);
                            this.playEndTurn ();                            
                        }                        
                        else
                        {
                            this.playSound (AudioClipTypes.ERROR);                            
                        }
                        break;
                }
                break;                
                
            case PLACE_ROBBER:
                {
                    Tile t = (Tile) objsSelected.getFirst();

                    switch (t.type)
                    {
                        case WOOD:
                        case ROCK:
                        case SHEEP:
                        case WHEAT:
                        case BRICK:
                        case VAR_VOLCANO:
                            if (t.hasRobber != false)
                            {
                                this.playSound (AudioClipTypes.ERROR);
                                break;
                            }         
                            else
                            {                                                                
                                // Change the robber location.                        
                                for (Tile tr:gameWindow.gameBoard.tileList)
                                {
                                    if (tr.hasRobber != false)
                                    {
                                        tr.hasRobber = false;                                   
                                    }                                
                                }                            
                                t.hasRobber = true;                                
                                gameWindow.gameBoard.clearHighlightAssist();
                                gameWindow.gameBoard.clrDblBuffCache();
                                gameWindow.gameBoard.repaint();
                                playSound (AudioClipTypes.ROBBER);
                                setRobberCursor (false);
                                
                                // Steal a card from another player ...                           
                                StealPlyrResCardJDialog stealCard = new StealPlyrResCardJDialog (gameWindow, true);
                                                                
                                stealCard.assignThisPlayer(t, thisPlayer, gameWindow);                                
                                stealCard.centreToPanel(gameWindow.playerInfo);
                                if (stealCard.plyrInfo.getTabCount() > 0)
                                {   
                                    // Prevent checking by closing window using alt+f4 with completeing action.
                                    do 
                                    {
                                        stealCard.setVisible(true);                                   
                                    }while (stealCard.completedOK == false);
                                    
                                    gameWindow.repaint();                            
                                }                                                                
                                setGamePhase(GamePhaseTypes.TRADE_BUILD);
                            }
                            break;

                        default:
                            this.playSound (AudioClipTypes.ERROR);
                    }
                }
                break;
                                      
            case BUILD_ROAD:
            case DEV_ROAD_BUILD_1:
            case DEV_ROAD_BUILD_2:
                {
                    Road r = (Road) objsSelected.getFirst();

                    if (r.owner != null)
                    {
                        this.playSound (AudioClipTypes.ERROR);
                        break;
                    }

                    if (isValidRoadBuild (r, thisPlayer) != false)
                    {                                    
                        thisPlayer.buildObject(CanBuildTypes.ROAD, r);                                                 
                        
                        switch (gamePhase)
                        {
                            case BUILD_ROAD:
                                
                                setGamePhase(GamePhaseTypes.TRADE_BUILD);
                                break;
                                
                            case DEV_ROAD_BUILD_1:
                                setGamePhase(GamePhaseTypes.DEV_ROAD_BUILD_2);
                                break;
                                
                            case DEV_ROAD_BUILD_2:      
                                setGamePhase(GamePhaseTypes.TRADE_BUILD);
                                break;                                
                        }                                    
                    }                  
                    else
                    {
                        this.playSound (AudioClipTypes.ERROR);                            
                    }   
                }
                break;
                
            case BUILD_CITY:
                {
                    BuildPoint bp = (BuildPoint) objsSelected.getFirst();                
                    if (bp.owner != thisPlayer)
                    {
                        this.playSound (AudioClipTypes.ERROR);
                        break;
                    }

                    if (bp.type == BuildPointTypes.SETTLEMENT)
                    {                                        
                        thisPlayer.buildObject(CanBuildTypes.CITY, bp);
                        setGamePhase(GamePhaseTypes.TRADE_BUILD);
                    }                  
                    else
                    {
                        this.playSound (AudioClipTypes.ERROR);                            
                    }                   
                }
                break;
                
                
            case BUILD_SETTLEMENT:
                {
                    BuildPoint bp = (BuildPoint) objsSelected.getFirst();
                    if (isValidBuildPoint (bp, true, thisPlayer, false) != false)
                    {                                        
                        thisPlayer.buildObject(CanBuildTypes.SETTLEMENT, bp);
                        setGamePhase(GamePhaseTypes.TRADE_BUILD);
                    }                  
                    else
                    {
                        this.playSound (AudioClipTypes.ERROR);                            
                    }                                   
                }
                break;                
        }                
    }
    catch (CatanEndGameException e)
    {}
    }
    
    public boolean checkForVictory (Player p) throws CatanEndGameException
    {
        // Can't win while in out-of-turn build mode (Catan 5-6 game type), need to be your current/real turn        
        switch (gameWindow.currTurnMode)
        {
            case ENDOUTOFTURNBUILD:
                return false;
        }  
        
        if (p.vicPntsTotal >= 10)
        {
            setGamePhase(GamePhaseTypes.ENDGAME);
            
            // Play all victory points that are hidden
            for (Player vpp:players)
            {
                LinkedList<ResourceCard> playCrds = new LinkedList<ResourceCard>();
                for (ResourceCard rc:vpp.newDevCards)                    
                {
                    switch (rc.type)
                    {
                        case DEV_VP_CHAPEL:
                        case DEV_VP_UNIVERSITY:
                        case DEV_VP_PALACE:
                        case DEV_VP_MARKET:
                        case DEV_VP_LIBRARY:                                                        
                            playCrds.add(rc); // play/show additional victory point(s) to other players                                               
                            break;            // at the end of the game
                    }
                }
                
                // move new dev cards to played dev cards list
                for (ResourceCard rc:playCrds) 
                {   
                    vpp.newDevCards.remove(rc);
                    vpp.usedDevCards.add(rc);
                }
            }
            
            // paint now!
            gameWindow.paint(gameWindow.getGraphics());                        

            if (gameCompTesting == false)            
            {            
                
                this.setMsgLog(p.name + " has won with " + p.vicPntsTotal + " victory points!");
                if (p.type == PlayerTypes.COMPUTER)
                {
                    this.setMsgLog(p.name + " was " + p.type.toString() + " A.I using method " + p.compAIType.toString() + " and build priority " + p.compBuildPriority.toString());
                }

                GameEndJDialog endGame = new GameEndJDialog(gameWindow, true, thisPlayer);

                if (p.type == PlayerTypes.COMPUTER)
                    this.playSound(AudioClipTypes.LOSE);
                else
                    this.playSound(AudioClipTypes.WIN);

                endGame.setVisible(true);

                if (endGame.startNewGame != false)
                {
                    gameWindow.newGame();
                }
                else
                {
                    gameWindow.turnMode(TurnMode.NONE);
                    for (Player p2:players)
                        p2.resPanelInfo.updatePurchanseActions();
                }                                           
            }
                        
            return true;                                
        }         
        return false;
    }
        
           
    public boolean isValidBuildPoint (BuildPoint bp, boolean checkForRoad, Player p, boolean compInitalRoadDestCheck)
    {       
        if (bp.type == BuildPointTypes.NULL)
            return false;
                
        // check if its  >= 2 spaces from neighbor?
        boolean  canBuild = false;               
        
        // Check initial building restrictions.
        switch (gamePhase)
        {
            case START_PLACEMENT_1:
            case START_PLACEMENT_2:                                       
                // Check if initally building next to a volcano tile.
                // its not allowed in the first placment turns.
                for (Tile t:bp.tileJoins)
                {
                    switch (t.type)
                    {
                        case VAR_VOLCANO:
                            if (compInitalRoadDestCheck == false)                            
                                return false;
                    }
                } 
                break;                  
        }
        
        
        for (Road r:bp.roadJoins)
        {                           
            for (BuildPoint checkbp:r.buildJoins)
            {    
                // Check if we can build here....
                if ((checkbp.type == BuildPointTypes.BUILDABLE_LAND) || 
                    (checkbp.type == BuildPointTypes.NULL))
                {               
                }
                else
                    return false;                
            }
            
            // match to an adjoining player road.
            if (r.owner == p)
                canBuild = true;
        }
                
        if (checkForRoad == false)
            return true;
        
        return canBuild;
    }
    
    public boolean isValidRoadBuild (Road r, Player p)
    {
        if (r.owner != null)
            return false;

        // Determine if player can build the road here...
        boolean  canBuild = false;
        
        switch (gamePhase)
        {
            case START_PLACEMENT_1: 
            case START_PLACEMENT_2:
                if (thisPlayer.builtObjs.size() > 0)
                {
                    if ((r.buildJoins.contains(thisPlayer.builtObjs.getLast()) != false) && (r.type == RoadTypes.BUILDABLE))       
                        return true;
                }
                break;

            default:
                if (r.type != RoadTypes.BUILDABLE)
                    break;
                
                for (Road bro:p.builtRoadObjs)
                {
                    BuildPoint f = r.buildJoins.getFirst();
                    BuildPoint l = r.buildJoins.getLast();

                    if (bro.buildJoins.contains(f) != false) 
                    {
                        if ((f.owner != null) && (f.owner != p))
                            canBuild = false;
                        else
                        {
                            canBuild = true;
                            break;
                        }
                    }
                    else if (bro.buildJoins.contains(l) != false)
                    {
                        if ((l.owner != null) && (l.owner != p))
                            canBuild = false;
                        else
                        {
                            canBuild = true;
                            break;
                        }
                    }
                }                                   
       }
                        
       return canBuild;
    }

    public void setBuildingCursor (BuildCursors cursorType)
    {
        switch (cursorType)
        {
            case NORMAL:
                gameWindow.setCursor(Cursor.getDefaultCursor());
                break;
                
            default:        
                if (thisPlayer.type == PlayerTypes.COMPUTER) return;
                
                Toolkit   toolkit   = Toolkit.getDefaultToolkit();
                ImageIcon imageIcon = new ImageIcon(getClass().getResource("/Catan/Resource/builder_cursor.png"));            
                
                switch (cursorType)
                {
                    case SETTLEMENT:                        
                        {
                            BufferedImage bi = BuildPoint.resSetlImgs[thisPlayer.col.toValue()];
                            BufferedImage c    = new BufferedImage (32, 32, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D    cG2  = (Graphics2D)c.getGraphics();
                            cG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
                            cG2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            cG2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);       
                            cG2.drawImage(imageIcon.getImage(), 0, 0,  imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
                            cG2.drawImage(bi, 15, 10, 10, 16, null);
                            
                            Cursor cursor = toolkit.createCustomCursor(c , new Point(0, 0), "settlement");
                            gameWindow.setCursor(cursor);                                           
                        }
                        break;
                        
                    case CITY:
                        {
                            BufferedImage bi = BuildPoint.resCityImgs[thisPlayer.col.toValue()];
                            BufferedImage c    = new BufferedImage (32, 32, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D    cG2  = (Graphics2D)c.getGraphics();
                            cG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
                            cG2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            cG2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);                                   
                            cG2.drawImage(imageIcon.getImage(), 0, 0,  imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);
                            cG2.drawImage(bi, 15, 10, 15, 16, null);

                            Cursor cursor = toolkit.createCustomCursor(c , new Point(0, 0), "city");
                            gameWindow.setCursor(cursor);                                           
                        }
                        break;
                        
                    case ROAD:    
                        {
                            BufferedImage c    = new BufferedImage (32, 32, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D    cG2  = (Graphics2D)c.getGraphics();
                            cG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);      
                            cG2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            cG2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);                                   
                            cG2.drawImage(imageIcon.getImage(), 0, 0,  imageIcon.getIconWidth(), imageIcon.getIconHeight(), null);

                            cG2.setColor (thisPlayer.col.toCol());
                            cG2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));                                   
                            cG2.drawLine (5, 16, 27, 16);                                                    
                            
                            Cursor cursor = toolkit.createCustomCursor(c , new Point(0, 0), "road");
                            gameWindow.setCursor(cursor);                                                                       
                        }                        
                }
          }
    }
    
    public void setRobberCursor (boolean showRobber)
    {
        if (showRobber != false)
        {
            Toolkit   toolkit   = Toolkit.getDefaultToolkit();
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/Catan/Resource/robber_cursor.png"));            
            Image     image     = imageIcon.getImage();
            Cursor    c = toolkit.createCustomCursor(image , new Point(image.getWidth(null)/2, image.getHeight(null)/5), "robber");
            Dimension d = toolkit.getBestCursorSize(image.getWidth(null), image.getHeight(null));
            gameWindow.setCursor(c);           
        }
        else
        {
            gameWindow.setCursor(Cursor.getDefaultCursor());           
        }
    }
    
    public void playSound (AudioClipTypes act) 
    {
        if (gameWindow.soundFX == false)
            return;
        
        String files[] = {"/Catan/Resource/audio_error.wav",
                          "/Catan/Resource/audio_buildroad.wav",
                          "/Catan/Resource/audio_settlement.wav",
                          "/Catan/Resource/audio_city.wav",
                          "/Catan/Resource/audio_win.wav",
                          "/Catan/Resource/audio_lose.wav",
                          "/Catan/Resource/audio_robber.wav",
                          "/Catan/Resource/audio_lroad.wav",
                          "/Catan/Resource/audio_larmy.wav",
                          "/Catan/Resource/audio_devcard.wav",
                          "/Catan/Resource/audio_dice.wav",
                          "/Catan/Resource/audio_explodeswiv.wav"};
                                       
        try
        {

            // Create an AudioStream object from the input stream.
            AudioStream as = new AudioStream(getClass().getResourceAsStream(files[act.toValue()]));
            AudioPlayer.player.start(as);
/****
            AudioInputStream as = AudioSystem.getAudioInputStream (getClass().getResourceAsStream(files[act.toValue()]));

            DataLine.Info info = new DataLine.Info(Clip.class, as.getFormat()); 
            Clip          clip = (Clip) AudioSystem.getLine(info); 
            
            clip.open(as); 
            clip.start();
 */
        } catch (Exception e) {}  
    }
    
    public void pause (int milliSeconds)
    {
        try { Thread.sleep(milliSeconds); } catch (InterruptedException e) { }
    }
    
    public void setLog (String s)
    {
        if (log.compareTo("") != 0)             
            s = "\n" + s;        
            
        log += s;
        
        Document d = gameWindow.gameStatusBar.getDocument();
        try
        {
            d.insertString(d.getLength(), s, null);            
            gameWindow.gameStatusBar.setCaretPosition(d.getLength());            
            gameWindow.gameStatusBar.repaint();
                        
            if (gameWindow.logView != null)
            {
                gameWindow.logView.updateText(s);           
            }
        }
        catch (Exception e){}
    }
  
    public void setMsgLog (String s)
    {
        setLog (s);

        switch (gamePhase)
        {
            case START_PLACEMENT_1:
            case START_PLACEMENT_2:
                break;
                
            default:
                if (thisPlayer.type == PlayerTypes.HUMAN)
                    break;
                
                if (msgLogPrompting != false)
                {
                    MessageJDialog msg = new MessageJDialog (gameWindow, true, false);
                    msg.setText(s, true);
                    msg.centreToPanel(gameWindow.playerInfo);
                    msg.setVisible(true);
                }
        }
    }       
}
