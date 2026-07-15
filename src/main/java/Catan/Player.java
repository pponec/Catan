package Catan;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import javax.imageio.ImageIO;

/**
 * Single shared, high-quality generator for the package-level enum helpers
 * below (random default AI type / build priority). Created once and reused,
 * rather than allocating a new Random per call.
 */
final class CompRandom
{
    static final RandomGenerator RND = RandomGeneratorFactory.of("L64X128MixRandom").create();
    private CompRandom() {}
}

/**
 *
 * @author  Steven De Toni
 *
 *  April 2008
 */

enum CompAIType
{
    HUERISTIC(0), HIGHSCORE(1);
    private int value;

    private CompAIType(int i)
    {
        value = i;
    }
    
    static CompAIType rand()
    {
        switch (CompRandom.RND.nextInt(2))
        {
            case 0: return HUERISTIC;
            case 1: return HIGHSCORE;
        }
        return HIGHSCORE;
    }

    static public CompAIType toType (int val)
    {
       switch (val)
       {
           case 1: return HIGHSCORE;
           default:
           case 0: return HUERISTIC;               
       }
    }
            
}
    
enum CompSkillLevel
{
    UBERMEANTRADER(0), MEDMEANTRADER(1), NORMAL(2), EASIER(3);
    private int value;

    private CompSkillLevel(int i)
    {
        value = i;
    }

    public int toValue()
    {
        return value;
    }

    public String toString()
    {
        switch (value)
        {
            case 0:
                return "Hardest - CPU Really Hates You";
            case 1:
                return "Hard - CPU Will Trade Against You";
            case 2:
                return "Normal - All Round Competitive Play";
            case 3:
                return "Little Easier - Maybe?";
        }
        return "";
    }
};

enum CanBuildTypes
{

    NULL, ROAD, SETTLEMENT, CITY, DEVCARD
};

enum PlayerTypes
{

    NULL, HUMAN, COMPUTER
};

enum PlayerColTypes
{

    NULL(-1), BLUE(0), RED(1), WHITE(2), ORANGE(3), GREEN(4), YELLOW(5);
    private static Color orange = new Color(228, 137, 8);
    private static Color green = new Color(90, 240, 90);
    private static Color altOrange = new Color(193, 113, 0);
    private static Color altBlue = new Color(0, 0, 170);
    private static Color altRed = new Color(170, 0, 0);
    private static Color altWhite = new Color(198, 189, 173);
    private static Color altGreen = new Color(75, 156, 75);
    private static Color altYellow = new Color(203, 154, 69);
    private int value;

    private PlayerColTypes(int i)
    {
        value = i;
    }

    public int toValue()
    {
        return value;
    }

    public Color toAltCol()
    {
        switch (value)
        {
            case 0:
                return altBlue;
            case 1:
                return altRed;
            case 2:
                return altWhite;
            case 3:
                return altOrange;
            case 4:
                return altGreen;
            case 5:
                return altYellow;
        }
        return Color.black;
    }

    public Color toCol()
    {
        switch (value)
        {
            case 0:
                return Color.blue;
            case 1:
                return Color.red;
            case 2:
                return Color.white;
            case 3:
                return orange;
            case 4:
                return green;
            case 5:
                return Color.yellow;
        }
        return Color.black;
    }
};

enum CompBuildPriorities {BUILD_PRI_1(0), BUILD_PRI_2(1), BUILD_PRI_3(2),BUILD_PRI_4(3);
    private int value;

    private CompBuildPriorities(int i)
    {
        value = i;
    }   
    
    static CompBuildPriorities randBest()
    {
        switch (CompRandom.RND.nextInt(2))
        {
            case 0: return BUILD_PRI_1;
            case 1: return BUILD_PRI_4;
        }
        return BUILD_PRI_4;
    }

    static CompBuildPriorities rand()
    {
        switch (CompRandom.RND.nextInt(4))
        {
            case 0: return BUILD_PRI_1;
            case 1: return BUILD_PRI_2;
            case 2: return BUILD_PRI_3;
            case 3: return BUILD_PRI_4;
        }
        return BUILD_PRI_1;
    }
    };

public class Player
{
    // --------------- Computer Hueristics Structures ---------------

    class RoadInfo
    {

        Road road = null;
        int buildPotentialScore = 0;
        int roadLen = 0;
        LinkedList<Road> roadGroup = new LinkedList<Road>();

        RoadInfo(Road r)
        {
            road = r;
        }
    }

    class TileInfo
    {
        BuildPoint bp = null;
        int score = 0;
        int uniqueScore = 0;  // how unqiue or how many different resources are there in this build point?


        TileInfo(BuildPoint bp)
        {
            this.bp = bp;
        }
    }

    class COMPTurnInfo
    {
        Player plyr = null;
        LinkedList<RoadInfo> endRoadPoints = new LinkedList<RoadInfo>();
        int roadGrpSplts = 0;
        LinkedList<RoadInfo> roadGrp1Points     = new LinkedList<RoadInfo>();
        LinkedList<RoadInfo> roadGrp2Points     = new LinkedList<RoadInfo>();
        LinkedList<CanBuildTypes> buildPriority = new LinkedList<CanBuildTypes>();
        LinkedList<TileInfo> availBuildPoints   = new LinkedList<TileInfo>();
        int canBuildRoad = 0;
        int canBuildCity = 0;
        int canBuildSettlement = 0;
        int canBuyDevCard = 0;
        int canPortTrade = 0;
        int sheepPTradeNum = 4;
        int wheatPTradeNum = 4;
        int brickPTradeNum = 4;
        int woodPTradeNum = 4;
        int rockPTradeNum = 4;
        int sheep = 0;
        int wheat = 0;
        int wood = 0;
        int brick = 0;
        int rock = 0;
        int scoreOccurBrick = 0;
        int scoreOccurWheat = 0;
        int scoreOccurWood = 0;
        int scoreOccurRock = 0;
        int scoreOccurSheep = 0;
        int likely2BuildRoad = 0;
        int likely2BuildSettlement = 0;
        int likely2BuildCity = 0;
        int likely2BuyDevCard = 0;

        public COMPTurnInfo(Player p)
        {
            plyr = p;
            //int vicPts = plyr.vicPntsTotal; // plyr.vicPntsTotalNoArmyNoRoad;
            int vicPts = plyr.vicPntsTotalNoArmyNoRoad;

            switch (plyr.compBuildPriority)
            {
                case BUILD_PRI_1:
                    // Allocate build priorities
                    if (vicPts <= 3)
                    {
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.NULL);
                    }
                    else if (vicPts >= 4 && vicPts <= 6)
                    {
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);
                        buildPriority.add(CanBuildTypes.DEVCARD);
                    }
                    else if (vicPts >= 7)
                    {
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.DEVCARD);
                        buildPriority.add(CanBuildTypes.ROAD);
                    }
                    else
                    {
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.NULL);
                    }
                    break;
                    
                case BUILD_PRI_2:
                    // Allocate build priorities
                    if (vicPts <= 3)
                    {
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.NULL);
                    }
                    else if (vicPts >= 4 && vicPts <= 6)
                    {
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.CITY);                        
                        buildPriority.add(CanBuildTypes.ROAD);
                        buildPriority.add(CanBuildTypes.DEVCARD);
                    }
                    else if (vicPts >= 7)
                    {
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.DEVCARD);
                        buildPriority.add(CanBuildTypes.SETTLEMENT);                        
                        buildPriority.add(CanBuildTypes.ROAD);
                    }
                    else
                    {
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);                        
                        buildPriority.add(CanBuildTypes.NULL);
                    }
                    break;
                    
                case BUILD_PRI_3:
                    // Allocate build priorities
                    if (vicPts <= 3)
                    {
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.NULL);
                    }
                    else if (vicPts >= 4 && vicPts <= 6)
                    {
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);                        
                        buildPriority.add(CanBuildTypes.CITY);                        
                        buildPriority.add(CanBuildTypes.DEVCARD);
                    }
                    else if (vicPts >= 7)
                    {
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.SETTLEMENT);   
                        buildPriority.add(CanBuildTypes.ROAD);                        
                        buildPriority.add(CanBuildTypes.DEVCARD);                             
                    }
                    else
                    {
                        buildPriority.add(CanBuildTypes.CITY);
                        buildPriority.add(CanBuildTypes.SETTLEMENT);
                        buildPriority.add(CanBuildTypes.ROAD);                        
                        buildPriority.add(CanBuildTypes.NULL);
                    } 
                    break;
                    
                case BUILD_PRI_4:
                    // Allocate build priorities
                    buildPriority.add(CanBuildTypes.CITY);
                    buildPriority.add(CanBuildTypes.SETTLEMENT);
                    buildPriority.add(CanBuildTypes.ROAD);                        
                    buildPriority.add(CanBuildTypes.DEVCARD);
                    break;
            }

            sheep = plyr.countResType(plyr.resCards, ResCardTypes.SHEEP);
            wheat = plyr.countResType(plyr.resCards, ResCardTypes.WHEAT);
            wood = plyr.countResType(plyr.resCards, ResCardTypes.WOOD);
            brick = plyr.countResType(plyr.resCards, ResCardTypes.BRICK);
            rock = plyr.countResType(plyr.resCards, ResCardTypes.ROCK);

            // identify valid build points
            availBuildPoints = plyr.COMP_BuildListOfValidBuildPoints(true, false);
            endRoadPoints = new LinkedList<RoadInfo>();

            plyr.calcRoadLens(endRoadPoints);
            roadGrpSplts = plyr.buildDiffRoadGroups(endRoadPoints, roadGrp1Points, roadGrp2Points);
            canBuildRoad = plyr.canBuild(CanBuildTypes.ROAD);
            canBuildCity = plyr.canBuild(CanBuildTypes.CITY);
            canBuildSettlement = plyr.canBuild(CanBuildTypes.SETTLEMENT);
            canBuyDevCard = plyr.canBuild(CanBuildTypes.DEVCARD);

            scoreOccurBrick = plyr.COMP_CanGetResType(ResCardTypes.BRICK);
            scoreOccurWheat = plyr.COMP_CanGetResType(ResCardTypes.WHEAT);
            scoreOccurSheep = plyr.COMP_CanGetResType(ResCardTypes.SHEEP);
            scoreOccurWood = plyr.COMP_CanGetResType(ResCardTypes.WOOD);
            scoreOccurRock = plyr.COMP_CanGetResType(ResCardTypes.ROCK);

            for (BuildPoint bp : plyr.builtObjs)
            {
                // determine which ports player has.
                if (bp.isPort != false)
                {
                    switch (bp.portType)
                    {
                        case PORT_ANY_3TO1:
                            if (brickPTradeNum > 3)
                            {
                                brickPTradeNum = 3;
                            }
                            if (wheatPTradeNum > 3)
                            {
                                wheatPTradeNum = 3;
                            }
                            if (woodPTradeNum > 3)
                            {
                                woodPTradeNum = 3;
                            }
                            if (sheepPTradeNum > 3)
                            {
                                sheepPTradeNum = 3;
                            }
                            if (rockPTradeNum > 3)
                            {
                                rockPTradeNum = 3;
                            }
                            break;

                        case PORT_BRICK_2TO1:
                            brickPTradeNum = 2;
                            break;
                        case PORT_ROCK_2TO1:
                            rockPTradeNum = 2;
                            break;
                        case PORT_WHEAT_2TO1:
                            wheatPTradeNum = 2;
                            break;
                        case PORT_WOOD_2TO1:
                            woodPTradeNum = 2;
                            break;
                        case PORT_SHEEP_2TO1:
                            sheepPTradeNum = 2;
                            break;
                    }
                }

                // Adjust score for ports builds
                for (Tile t : bp.tileJoins)
                {
                    switch (t.type)
                    {
                        case PORT_ANY_3TO1:
                            scoreOccurBrick += (scoreOccurBrick / 4);
                            scoreOccurRock += (scoreOccurRock / 4);
                            scoreOccurWheat += (scoreOccurWheat / 4);
                            scoreOccurWood += (scoreOccurWood / 4);
                            scoreOccurSheep += (scoreOccurSheep / 4);
                            break;
                        case PORT_BRICK_2TO1:
                            scoreOccurBrick += (scoreOccurBrick / 2);
                            break;
                        case PORT_ROCK_2TO1:
                            scoreOccurRock += (scoreOccurRock / 2);
                            break;
                        case PORT_WHEAT_2TO1:
                            scoreOccurWheat += (scoreOccurWheat / 2);
                            break;
                        case PORT_WOOD_2TO1:
                            scoreOccurWood += (scoreOccurWood / 2);
                            break;
                        case PORT_SHEEP_2TO1:
                            scoreOccurSheep += (scoreOccurSheep / 2);
                            break;
                    }
                }
            }

            // Calc port trade number
            if (sheep > 0)
            {
                canPortTrade += sheep / sheepPTradeNum;
            }
            if (wheat > 0)
            {
                canPortTrade += wheat / wheatPTradeNum;
            }
            if (rock > 0)
            {
                canPortTrade += rock / rockPTradeNum;
            }
            if (wood > 0)
            {
                canPortTrade += wood / woodPTradeNum;
            }
            if (brick > 0)
            {
                canPortTrade += brick / brickPTradeNum;
            }
            likely2BuildRoad = ((scoreOccurBrick + scoreOccurWood) / 2);
            likely2BuildSettlement = ((scoreOccurBrick + scoreOccurWood + scoreOccurSheep + scoreOccurWheat) / 4);
            likely2BuildCity = ((scoreOccurRock + scoreOccurRock + scoreOccurRock + scoreOccurWheat + scoreOccurWheat) / 5);
            likely2BuyDevCard = (scoreOccurSheep + scoreOccurWheat + scoreOccurRock) / 3;

            dumpBasicStats();
        }

        public ResCardTypes getLeastLikelyResource()
        {
            class SortType
            {

                int score = 0;
                ResCardTypes type = ResCardTypes.NULL;

                SortType(int s, ResCardTypes t)
                {
                    score = s;
                    type = t;
                }
            }

            SortType res[] =
            {
                new SortType(scoreOccurBrick, ResCardTypes.BRICK),
                new SortType(scoreOccurWood, ResCardTypes.WOOD),
                new SortType(scoreOccurRock, ResCardTypes.ROCK),
                new SortType(scoreOccurSheep, ResCardTypes.SHEEP),
                new SortType(scoreOccurWheat, ResCardTypes.WHEAT)
            };

            SortType least = res[0];

            for (int idx = 1; idx < res.length; idx++)
            {
                if (res[idx].score <= least.score)
                {
                    least = res[idx];
                }
            }

            return least.type;
        }

        public void dumpBasicStats()
        {
            plyr.Debug("=========== COMPTurnInfo : " + plyr.name + " " + plyr.compAIType.name() + " ==========", DebugLevel.COMPLETE);
            plyr.Debug("Build Priority Type: " + plyr.compBuildPriority.toString(), DebugLevel.COMPLETE);
            plyr.Debug("sheep: " + sheep, DebugLevel.COMPLETE);
            plyr.Debug("wheat: " + wheat, DebugLevel.COMPLETE);
            plyr.Debug("rock: " + rock, DebugLevel.COMPLETE);
            plyr.Debug("wood: " + wood, DebugLevel.COMPLETE);
            plyr.Debug("brick: " + brick, DebugLevel.COMPLETE);
            plyr.Debug("", DebugLevel.COMPLETE);
            plyr.Debug("Can Build Road: " + canBuildRoad, DebugLevel.COMPLETE);
            plyr.Debug("Can Build City: " + canBuildCity, DebugLevel.COMPLETE);
            plyr.Debug("Can Build Settlement: " + canBuildSettlement, DebugLevel.COMPLETE);
            plyr.Debug("Can Buy Dev Card: " + canBuyDevCard, DebugLevel.COMPLETE);
            plyr.Debug("", DebugLevel.COMPLETE);
            plyr.Debug("Score Occurance Brick: " + scoreOccurBrick, DebugLevel.COMPLETE);
            plyr.Debug("Score Occurance Wheat: " + scoreOccurWheat, DebugLevel.COMPLETE);
            plyr.Debug("Score Occurance Wood: " + scoreOccurWood, DebugLevel.COMPLETE);
            plyr.Debug("Score Occurance Rock: " + scoreOccurRock, DebugLevel.COMPLETE);
            plyr.Debug("Score Occurance Sheep: " + scoreOccurSheep, DebugLevel.COMPLETE);
            plyr.Debug("", DebugLevel.COMPLETE);
            plyr.Debug("Likely Build Road: " + likely2BuildRoad, DebugLevel.COMPLETE);
            plyr.Debug("Likely Build Settlement: " + likely2BuildSettlement, DebugLevel.COMPLETE);
            plyr.Debug("Likely Build City: " + likely2BuildCity, DebugLevel.COMPLETE);
            plyr.Debug("Likely Buy Dev Card: " + likely2BuyDevCard, DebugLevel.COMPLETE);
            plyr.Debug("", DebugLevel.COMPLETE);
            plyr.Debug("Port Trade Wood : " + this.woodPTradeNum + " -> 1", DebugLevel.COMPLETE);
            plyr.Debug("Port Trade Brick : " + this.brickPTradeNum + " -> 1", DebugLevel.COMPLETE);
            plyr.Debug("Port Trade Sheep : " + this.sheepPTradeNum + " -> 1", DebugLevel.COMPLETE);
            plyr.Debug("Port Trade Rock  : " + this.rockPTradeNum + " -> 1", DebugLevel.COMPLETE);
            plyr.Debug("Port Trade Wheat  : " + this.wheatPTradeNum + " -> 1", DebugLevel.COMPLETE);
            plyr.Debug("========================================================", DebugLevel.COMPLETE);
        }

        public int scoreLikelyNextCity()
        {
            int score = likely2BuildCity;

            if (wheat < 2)
            {
                score += wheat;
            }
            else
            {
                score += 2;
            }
            if (rock < 3)
            {
                score += rock;
            }
            else
            {
                score += 3;
            }
            return score;
        }

        public int scoreLikelyNextSettlement()
        {
            int score = likely2BuildSettlement;

            if (wheat > 1)
            {
                score++;
            }
            if (sheep > 1)
            {
                score++;
            }
            if (brick > 1)
            {
                score++;
            }
            if (wood > 1)
            {
                score++;
            }
            return score;
        }

        public int scoreLikelyNextRoad()
        {
            int score = likely2BuildSettlement;

            if (brick > 1)
            {
                score++;
            }
            if (wood > 1)
            {
                score++;
            }
            return score;
        }

        public int scoreLikelyNextDevCard()
        {
            int score = likely2BuildSettlement;

            if (sheep > 1)
            {
                score++;
            }
            if (wheat > 1)
            {
                score++;
            }
            if (rock > 1)
            {
                score++;
            }
            return score;
        }

        public boolean canBuildViaPortTrades(CanBuildTypes type)
        {
            int pBrick = brick;
            int pWood = wood;
            int pSheep = sheep;
            int pWheat = wheat;
            int pRock = rock;

            int portTradesPossible = 0;
            int reqRes = 0;
            switch (type)
            {
                case ROAD:
                    pWood--;
                    pBrick--;
                    break;

                case CITY:
                    pRock -= 3;
                    pWheat -= 2;
                    break;

                case SETTLEMENT:
                    pBrick--;
                    pWood--;
                    pWheat--;
                    pSheep--;
                    break;

                case DEVCARD:
                    pRock--;
                    pSheep--;
                    pWheat--;
                    break;
            }

            if (pWood > 0)
            {
                portTradesPossible += pWood / woodPTradeNum;
            }
            else
            {
                reqRes += pWood;
            }
            if (pWheat > 0)
            {
                portTradesPossible += pWheat / woodPTradeNum;
            }
            else
            {
                reqRes += pWheat;
            }
            if (pSheep > 0)
            {
                portTradesPossible += pSheep / woodPTradeNum;
            }
            else
            {
                reqRes += pSheep;
            }
            if (pRock > 0)
            {
                portTradesPossible += pRock / woodPTradeNum;
            }
            else
            {
                reqRes += pRock;
            }
            if (pBrick > 0)
            {
                portTradesPossible += pBrick / woodPTradeNum;
            }
            else
            {
                reqRes += pBrick;
            }
            for (ResourceCard rc : plyr.newDevCards)
            {
                if (rc.purchasedOnTurn >= plyr.gameRules.gameTurnNo)
                {
                    continue;
                }
                if (rc.type == ResCardTypes.DEV_YEAROFPLENTY)
                {
                    portTradesPossible += 2;
                }
            }


            if (portTradesPossible >= Math.abs(reqRes))
            {
                return true;
            }
            return false;
        }

        public CanBuildTypes calcNeedNextObj()
        {
            class SortType
            {

                int score = 0;
                CanBuildTypes type = CanBuildTypes.NULL;

                SortType(int s, CanBuildTypes t)
                {
                    score = s;
                    type = t;
                }
            }

            SortType bldCity = new SortType(scoreLikelyNextCity(), CanBuildTypes.CITY);
            SortType bldSettlement = new SortType(scoreLikelyNextSettlement(), CanBuildTypes.SETTLEMENT);
            SortType bldRoad = new SortType(scoreLikelyNextRoad(), CanBuildTypes.ROAD);
            SortType buyDevCard = new SortType(scoreLikelyNextDevCard(), CanBuildTypes.DEVCARD);

            // Don't try and build something when there is no space to build things
            if ((availBuildPoints.size() <= 0) || (plyr.bldStckSettlement <= 0))
            {
                bldSettlement.score = 0;
            // cant upgrade settlement if they are all cities
            }
            if ((plyr.bldStckSettlement >= 5) || (plyr.bldStckCity <= 0))
            {
                bldCity.score = 0;
                // Stop building roads, and start building settlements early in the game
            }
            if ((canBuildRoad > 0) && (availBuildPoints.size() > 0) && (plyr.vicPntsTotalNoArmyNoRoad <= 8))
            {
                TileInfo ti = COMP_Calc_BestBuildPoint(availBuildPoints);
            
                if (ti.score >= 3)
                {
                    bldSettlement.score += 2;
                    bldRoad.score -= 2;
                }
            }

            // Stop buying dev card if there are non available
            if (gameRules.gameDevCards.size() <= 0)
            {
                buyDevCard.score = 0;                
            }
            
            // scan through build priorities
            int idx = 4;
            for (CanBuildTypes cbp : buildPriority)
            {
                idx--;
                switch (cbp)
                {
                    case ROAD:
                        bldRoad.score += idx;
                        break;
                    case SETTLEMENT:
                        bldSettlement.score += idx;
                        break;
                    case CITY:
                        bldCity.score += idx;
                        break;
                    case DEVCARD:
                        buyDevCard.score += idx;
                        break;
                }
            }

            LinkedList<SortType> bestList = new LinkedList<SortType>();
            bestList.add(bldCity);
            bestList.add(bldSettlement);
            bestList.add(bldRoad);
            bestList.add(buyDevCard);

            SortType best = null;

            for (SortType st : bestList)
            {
                if (best == null)
                {
                    best = st;
                }
                else if (st.score > best.score)
                {
                    best = st;
                }
            }

            return best.type;
        }

        public CanBuildTypes helpCantBuildException(CanBuildTypes cantBuildObj)
        {
            switch (cantBuildObj)
            {
                case ROAD:
                    if ((availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                    {
                        return CanBuildTypes.SETTLEMENT;
                    }
                    else if ((plyr.bldStckCity > 0) && (plyr.bldStckSettlement < 5))
                    {
                        return CanBuildTypes.CITY;
                    }
                    else if (canBuyDevCard > 0)
                    {
                        return CanBuildTypes.DEVCARD;
                    }
                    break;

                case SETTLEMENT:
                    if ((plyr.bldStckCity > 0) && (plyr.bldStckSettlement < 5))
                    {
                        return CanBuildTypes.CITY;
                    }
                    else if (canBuyDevCard > 0)
                    {
                        return CanBuildTypes.DEVCARD;
                    }
                    return CanBuildTypes.ROAD;


                case CITY:
                    if ((availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                    {
                        return CanBuildTypes.SETTLEMENT;
                    }
                    else if (canBuyDevCard > 0)
                    {
                        return CanBuildTypes.DEVCARD;
                    }
                    return CanBuildTypes.ROAD;

                case DEVCARD:
                    if ((availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                    {
                        return CanBuildTypes.SETTLEMENT;
                    }
                    else if ((plyr.bldStckCity > 0) && (plyr.bldStckSettlement < 5))
                    {
                        return CanBuildTypes.CITY;
                    }
                    return CanBuildTypes.ROAD;
            }

            return cantBuildObj.DEVCARD;
        }

        public CanBuildTypes checkBldException(CanBuildTypes tryBuild, CanBuildTypes[] needObj)
        {
            if (tryBuild != CanBuildTypes.NULL)
            {
                switch (tryBuild)
                {
                    case ROAD:
                    case DEVCARD:
                        if ((canBuildCity > 0) && (plyr.bldStckCity > 0) && (plyr.bldStckSettlement < 5))
                        {
                            Debug("COMPTurnInfo.checkBldException (OVERRULE BUILD)" + name + " by passing requested *" + tryBuild.toString() + "* to *" + CanBuildTypes.CITY.toString() + "*", DebugLevel.COMPLETE);
                            return CanBuildTypes.CITY;
                        }
                        else if ((canBuildSettlement > 0) && (availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                        {
                            Debug("COMPTurnInfo.checkBldException (OVERRULE BUILD)" + name + " by passing requested *" + tryBuild.toString() + "* to *" + CanBuildTypes.SETTLEMENT.toString() + "*", DebugLevel.COMPLETE);
                            return CanBuildTypes.SETTLEMENT;
                        }
                }
            }

            return tryBuild;
        }

        // Local variables must be filled before calling this method.
        public CanBuildTypes doNextNormal(CanBuildTypes[] needObj)
        {
            needObj[0] = CanBuildTypes.NULL;
            for (CanBuildTypes cbp : buildPriority)
            {
                switch (cbp)
                {
                    case NULL:
                        break;

                    case ROAD:
                        if ((canBuildRoad > 0) && (availBuildPoints.size() <= 0) && (plyr.bldStckRoad > 0))
                        {
                            return checkBldException(CanBuildTypes.ROAD, needObj);
                        }
                        else if ((canBuildViaPortTrades(CanBuildTypes.ROAD) != false) && (availBuildPoints.size() <= 0) && (plyr.bldStckRoad > 0))
                        {
                            needObj[0] = CanBuildTypes.ROAD;
                            return CanBuildTypes.NULL;
                        }
                        else if ((canBuildSettlement <= 0) && (canBuildRoad > 1))
                        {
                            return CanBuildTypes.ROAD;
                        }
                        break;

                    case SETTLEMENT:
                        if ((canBuildSettlement > 0) && (availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                        {
                            return checkBldException(CanBuildTypes.SETTLEMENT, needObj);
                        }
                        else if ((canBuildViaPortTrades(CanBuildTypes.SETTLEMENT) != false) && (availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                        {
                            needObj[0] = CanBuildTypes.SETTLEMENT;
                            return CanBuildTypes.NULL;
                        }
                        break;

                    case CITY:
                        if ((canBuildCity > 0) && (plyr.bldStckCity > 0) && (plyr.bldStckSettlement < 5))
                        {
                            return checkBldException(CanBuildTypes.CITY, needObj);
                        }
                        else if ((canBuildViaPortTrades(CanBuildTypes.CITY) != false) && (plyr.bldStckCity > 0) && (plyr.bldStckSettlement < 5))
                        {
                            needObj[0] = CanBuildTypes.CITY;
                            return CanBuildTypes.NULL;
                        }
                        break;

                    case DEVCARD:
                        // Check if we really want to buy a dev card and not try for a city build instead?
                        if ((plyr.bldStckCity       > 0) && 
                            (plyr.bldStckSettlement < 5) && 
                            (plyr.devCardPurchasedOnTurn >= plyr.gameRules.gameTurnNo-2))
                        {
                            plyr.Debug("Player:COMPTurnInfo.doNextNormal " + plyr.name + " Overriding purchase of dev card :: " + 
                                       plyr.devCardPurchasedOnTurn + ":" + 
                                       plyr.gameRules.gameTurnNo   + " (" + plyr.devCardPurchasedNo + 
                                       ") - Try to build a city instead!", DebugLevel.COMPLETE);
                            needObj[0] = CanBuildTypes.CITY;
                            return CanBuildTypes.NULL;
                        }
                            
                        if ((canBuyDevCard > 0) && (gameRules.gameDevCards.size() > 0))
                        {
                            return checkBldException(CanBuildTypes.DEVCARD, needObj);
                        }
                        else if ((canBuildViaPortTrades(CanBuildTypes.DEVCARD) != false) && (gameRules.gameDevCards.size() > 0))
                        {
                            needObj[0] = CanBuildTypes.DEVCARD;
                            return CanBuildTypes.NULL;
                        }
                        break;
                }
            }
            if (needObj[0] == CanBuildTypes.NULL)
            {
                needObj[0] = calcNeedNextObj();

                // Check if we can't build exceptions!
                switch (needObj[0])
                {
                    case DEVCARD:
                        if (canBuyDevCard > 0)
                        {
                            needObj[0] = helpCantBuildException(needObj[0]);
                        }
                        break;
                    case ROAD:
                        if (canBuildRoad > 0)
                        {
                            needObj[0] = helpCantBuildException(needObj[0]);
                        }
                        break;

                    case SETTLEMENT:
                        if (canBuildSettlement > 0)
                        {
                            needObj[0] = helpCantBuildException(needObj[0]);
                        }
                        break;

                    case CITY:
                        if (canBuildCity > 0)
                        {
                            needObj[0] = helpCantBuildException(needObj[0]);
                        }
                        break;
                }
            }
            return CanBuildTypes.NULL;
        }

        // Local variables must be filled before calling this method.
        public CanBuildTypes doNextEasier(CanBuildTypes[] needObj)
        {
            needObj[0] = CanBuildTypes.NULL;
            for (CanBuildTypes cbp : buildPriority)
            {
                switch (cbp)
                {
                    case NULL:
                        break;

                    case ROAD:
                        if ((canBuildRoad > 0) && (availBuildPoints.size() <= 0) && (plyr.bldStckRoad > 0))
                        {
                            return CanBuildTypes.ROAD;
                        }
                        else if ((canBuildSettlement <= 0) && (canBuildRoad > 1))
                        {
                            return CanBuildTypes.ROAD;
                        }
                        if (needObj[0] == CanBuildTypes.NULL)
                        {
                            needObj[0] = cbp;
                        }
                        break;

                    case SETTLEMENT:
                        if ((canBuildSettlement > 0) && (availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                        {
                            return CanBuildTypes.SETTLEMENT;
                        }
                        if ((needObj[0] == CanBuildTypes.NULL) && (availBuildPoints.size() > 0) && (plyr.bldStckSettlement > 0))
                        {
                            needObj[0] = cbp;
                        }
                        break;

                    case CITY:
                        if ((canBuildCity > 0) && (plyr.bldStckCity > 0) && (plyr.bldStckSettlement < 5))
                        {
                            return CanBuildTypes.CITY;
                        }
                        if (needObj[0] == CanBuildTypes.NULL)
                        {
                            needObj[0] = cbp;
                        }
                        break;

                    case DEVCARD:
                        if ((canBuyDevCard > 0) && (gameRules.gameDevCards.size() > 0))
                        {
                            return CanBuildTypes.DEVCARD;
                        }
                        if (needObj[0] == CanBuildTypes.NULL)
                        {
                            needObj[0] = cbp;
                        }
                        break;
                }
            }
            if (needObj[0] == CanBuildTypes.NULL)
            {
                needObj[0] = buildPriority.get(0);
            }
            return CanBuildTypes.NULL;
        }

        // Local variables must be filled before calling this method, wrapper for the 2 above methods.
        public CanBuildTypes doNext(CanBuildTypes[] needObj)
        {
            switch (plyr.compSkillLevel)
            {
                case EASIER:
                    return doNextEasier(needObj);

                case NORMAL:
                case UBERMEANTRADER:
                case MEDMEANTRADER:
                default:
                    return doNextNormal(needObj);
            }
        }
    }

    
    // --------------------------------------------------------------    
    public String name = "";
    public PlayerColTypes col = PlayerColTypes.NULL;
    public PlayerTypes type = PlayerTypes.NULL;
    public ResBuildPanel resPanelInfo = null;
    public LinkedList<ResourceCard> resCards = new LinkedList<ResourceCard>();
    public LinkedList<ResourceCard> newDevCards = new LinkedList<ResourceCard>();
    public LinkedList<ResourceCard> usedDevCards = new LinkedList<ResourceCard>();
    public ResourceCard largestArmyCard = null;
    public ResourceCard longestRoadCard = null;    

    public int devCardPurchasedOnTurn = -1;    
    public int devCardPurchasedNo     = -1;
    public int devCardPlayedOnTurn    = 0;

    // Must have called calcVictoryPoints() before accessing these variables.
    public int vicPntsHidden = 0;
    public int vicPntsPublic = 0;
    public int vicPntsTotal = 0;
    public int vicPntsTotalNoArmyNoRoad = 0;
    public int maxRoadLen = 0;
    public int bldStckRoad = 15;
    public int bldStckCity = 4;
    public int bldStckSettlement = 5;
    public CompBuildPriorities    compBuildPriority = CompBuildPriorities.randBest();
    public CompAIType             compAIType        = CompAIType.rand();
    public CompSkillLevel         compSkillLevel    = CompSkillLevel.NORMAL;
    public GameBoardJPanel        gameBoard         = null;
    public GameRules              gameRules         = null;
    public LinkedList<BuildPoint> builtObjs         = new LinkedList<BuildPoint>();
    public LinkedList<Road>       builtRoadObjs     = new LinkedList<Road>();
    static BufferedImage          plyrImgs[]        = null;

    /**
     * Bare constructor used only by unit tests for the road-length logic. It runs
     * the field initialisers (so collections like {@link #builtRoadObjs} are ready)
     * but skips the heavy Swing/image setup of the real constructor.
     */
    Player()
    {
    }

    public Player(PlayerTypes type,
            String                name,
            PlayerColTypes        col,
            GameBoardJPanel       gameBoard,
            GameRules             gameRules,
            ResBuildPanel         resPanelInfo,
            CompSkillLevel        skillLevel,
            CompAIType            AIType,
            CompBuildPriorities   buildPriorityType)
    {
        // Load some images
        if (plyrImgs == null)
        {
            String files[] =
            {
                "/Catan/Resource/plyr_blue.png", "/Catan/Resource/plyr_red.png",
                "/Catan/Resource/plyr_white.png", "/Catan/Resource/plyr_orange.png",
                "/Catan/Resource/plyr_green.png", "/Catan/Resource/plyr_yellow.png"
            };
            plyrImgs = new BufferedImage[files.length];

            for (int idx = 0; idx < files.length; idx++)
            {
                try
                {
                    plyrImgs[idx] = ImageIO.read(getClass().getResourceAsStream(files[idx]));

                }
                catch (Exception e)
                {
                    System.err.println("Failed loading image : " + files[idx] + " : " + e.toString());
                    System.exit(1);
                }
            }
        }

        this.type           = type;
        this.name           = name;
        this.col            = col;
        this.gameBoard      = gameBoard;
        this.gameRules      = gameRules;
        this.resPanelInfo   = resPanelInfo;
        this.compSkillLevel = skillLevel;
        this.compBuildPriority = buildPriorityType;
        this.compAIType        = AIType;
       
        resPanelInfo.assignPlayer(this);
        resPanelInfo.setEnabled(false);
    }

    public BufferedImage getPlayerImage()
    {
        if (plyrImgs != null)
        {
            return plyrImgs[col.toValue()];
        }
        return null;
    }

    public boolean canPortTrade()
    {
        COMPTurnInfo cpi = new COMPTurnInfo(this);
        if (cpi.canPortTrade > 0)
        {
            return true;
        }
        return false;
    }

    public int buildDiffRoadGroups(LinkedList<RoadInfo> endRoadPoints,
            LinkedList<RoadInfo> grp1,
            LinkedList<RoadInfo> grp2)
    {
        if (endRoadPoints.size() <= 0)
        {
            return 0;
        }
        if (grp1 != null)
        {
            grp1.clear();
        }
        else
        {
            grp1 = new LinkedList<RoadInfo>();
        }
        if (grp2 != null)
        {
            grp2.clear();
        }
        else
        {
            grp2 = new LinkedList<RoadInfo>();
        }
        RoadInfo rg1 = endRoadPoints.getFirst();

        grp1.add(rg1);
        for (Road ri : rg1.roadGroup)
        {
            RoadInfo rg2 = null;
            for (RoadInfo rgi : endRoadPoints)
            {
                if (rgi == rg1)
                {
                    continue;
                }
                if (rgi.roadGroup.contains(ri) == false)
                {
                    rg2 = rgi;
                    break;
                }
            }

            if (rg2 != null)
            {
                grp2.add(rg2);
            }
            else
            {
                grp1.add(rg1);
            }
        }

        // show groups of roads ...
        Debug("Player:buildDiffRoadGroups : " + name + " group 1 n.o = " + grp1.size(), DebugLevel.COMPLETE);
        Debug("Player:buildDiffRoadGroups : " + name + " group 2 n.o = " + grp2.size(), DebugLevel.COMPLETE);

        if ((grp1.size() > 0) && (grp1.size() > 0))
        {
            return 2;
        }
        else if ((grp1.size() > 0) || (grp1.size() > 0))
        {
            return 1;
        }
        return 0;
    }

    // endPntRoads : this can be null if you don't need the road info,
    //               otherwise a list of filled RoadInfo objects are returned.
    public int calcRoadLens(LinkedList<RoadInfo> endRoadPoints)
    {
        // determine road end points (roads connected to null or a different owner road points)
        // allocate memory as needed...
        if (endRoadPoints == null)
        {
            endRoadPoints = new LinkedList<RoadInfo>();
        }
        for (Road r : this.builtRoadObjs)
        {
            for (BuildPoint bp : r.buildJoins)
            {
                for (Road fr : bp.roadJoins)
                {
                    if (fr.owner == null)
                    {
                        endRoadPoints.add(new RoadInfo(fr));
                    }
                }
            }
        }

        // Calculate road lengths for each end Point.
        int maxRLen = 0;
        for (RoadInfo ri : endRoadPoints)
        {
            // ri.road.debugHightlight (this.gameBoard.getGraphics()); // *** debug  Highlight end road segments ***
            ri.roadLen = roadLen(ri.road, null, this, ri.roadGroup, new LinkedList<Road>());

            // Dump Road info ....
            // Debug ("calcRoadLens:" + this.name + " obj:" + ri.road.toString() + " Road Len = " + ri.roadLen, DebugLevel.COMPLETE);
            if (ri.roadLen > maxRLen)
            {
                maxRLen = ri.roadLen;
            }
        }

        // The search above only starts from empty "tip" segments next to our network.
        // A road component with no such tip - a closed loop, or a run whose both ends
        // are boxed in by foreign roads - is never reached and would score 0. Collect
        // the roads already covered above, then measure any leftover component directly
        // by treating each of its roads as a trail end.
        LinkedList<Road> covered = new LinkedList<Road>();
        for (RoadInfo ri : endRoadPoints)
        {
            for (Road r : ri.roadGroup)
            {
                if (covered.contains(r) == false)
                {
                    covered.add(r);
                }
            }
        }

        for (Road r : this.builtRoadObjs)
        {
            if (covered.contains(r))
            {
                continue;
            }

            // Discover the whole component reachable from r. The DFS never crosses
            // a foreign building, so this is exactly one continuous-road group.
            LinkedList<Road> component = new LinkedList<Road>();
            component.add(r);
            for (BuildPoint bp : r.buildJoins)
            {
                LinkedList<Road> path = new LinkedList<Road>();
                path.add(r);
                roadLen(r, bp, this, component, path);
            }

            // The longest trail must start at one of the component's roads, so try
            // every road as a trail end (from both build points) and keep the best.
            // Starting from a middle road would only yield one arm, not their sum.
            for (Road start : component)
            {
                if (covered.contains(start))
                {
                    continue;
                }
                covered.add(start);

                for (BuildPoint bp : start.buildJoins)
                {
                    LinkedList<Road> path = new LinkedList<Road>();
                    path.add(start);
                    int len = roadLen(start, bp, this, null, path);
                    if (len > maxRLen)
                    {
                        maxRLen = len;
                    }
                }
            }
        }

        Debug("CalcRoadLens:" + this.name + " max road len of " + maxRLen, DebugLevel.COMPLETE);
        Debug("CalcRoadLens:" + this.name + " has " + buildDiffRoadGroups(endRoadPoints, null, null) + " different road groupings", DebugLevel.COMPLETE);

        Debug("", DebugLevel.COMPLETE);

        return maxRLen;
    }

    // addToThisGroup : this can be null if additonal road group info is not needed.
    public int roadLen(Road thisR, BuildPoint startBuildPoint, Player thisOwner,
            LinkedList<Road> addToThisGroup,
            LinkedList<Road> thisPath)
    {
        int len = thisPath.size();

        Iterator bItem = thisR.buildJoins.iterator();
        while (bItem.hasNext())
        {
            BuildPoint rbp = (BuildPoint) bItem.next();
            if (startBuildPoint == null)
            {
                startBuildPoint = rbp;
            }
            else if (startBuildPoint == rbp)
            {
                continue;
            }           
            // don't count road length through other players settlements/cities other than your own!
            // rbp is the vertex we would extend the road *through*, so the foreign
            // building that breaks the road must be tested on rbp (not on the vertex
            // we arrived from). Checking startBuildPoint here let a road pass straight
            // through an opponent's settlement/city, over-counting the longest road.
            else if (rbp.owner != null && rbp.owner != thisOwner)
            {
                continue;
            }
 
            Iterator rjItems = rbp.roadJoins.iterator();

            while (rjItems.hasNext())
            {
                Road rjRoad = (Road) rjItems.next();

                if (rjRoad.owner == thisOwner)
                {
                    if (thisPath.contains(rjRoad) == false)
                    {
                        if (addToThisGroup != null)
                        {
                            addToThisGroup.add(rjRoad);
                        }
                        thisPath.add(rjRoad);
                        int len2 = roadLen(rjRoad, rbp, thisOwner, addToThisGroup, thisPath);
                        thisPath.remove(rjRoad);
                        if (len < len2 + 1)
                        {
                            len = len2;
                        }
                    }
                }
            }
        }

        return len;
    }

    public int countResType(LinkedList<ResourceCard> resCardList, ResCardTypes crdType)
    {
        int count = 0;

        if (resCardList != null)
        {
            for (ResourceCard rc : resCardList)
            {
                if (rc.type == crdType)
                {
                    count++;
                }
            }
        }
        return count;
    }

    public void delResType(ResCardTypes crdType, int num)
    {
        while (num > 0)
        {
            Iterator item = resCards.iterator();

            while (item.hasNext())
            {
                ResourceCard rc = (ResourceCard) item.next();
                if (rc.type == crdType)
                {
                    resCards.remove(rc);
                    break;
                }
            }
            num--;
        }
    }

    /**
     * Monopoly: take every card of the given resource type from all other
     * players. The confiscated cards are appended to {@code into}, which may be
     * this player's own hand (AI) or a staging list for the human picker dialog.
     * Returns how many cards were taken.
     */
    public int appropriateAllOfType(ResCardTypes crdType, LinkedList<ResourceCard> into)
    {
        int count = 0;
        for (Player p : this.gameRules.players)
        {
            if (p == this)
                continue;

            int delCount = 0;
            for (ResourceCard prc : p.resCards)
            {
                if (prc.type == crdType)
                {
                    into.add(prc);
                    delCount++;
                }
            }
            if (delCount > 0)
            {
                p.delResType(crdType, delCount);
                count += delCount;
            }
        }
        return count;
    }

    public void refreshPlayerInfo()
    {
        if (resPanelInfo != null)
        {
            resPanelInfo.refreshPlayerInfo();
        }
    }

    public void calcVictoryPoints() throws CatanEndGameException
    {
        // calc current points
        Iterator item = gameBoard.buildList.iterator();

        vicPntsHidden = vicPntsPublic = 0;

        while (item.hasNext())
        {
            BuildPoint bp = (BuildPoint) item.next();

            if ((bp.owner == this) && (bp.type == BuildPointTypes.SETTLEMENT))
            {
                vicPntsPublic++;
            }
            else if ((bp.owner == this) && (bp.type == BuildPointTypes.CITY))
            {
                vicPntsPublic += 2;
            }
        }

        // Count public victory point cards
        item = usedDevCards.iterator();
        while (item.hasNext())
        {
            ResourceCard rc = (ResourceCard) item.next();
            switch (rc.type)
            {
                case DEV_VP_CHAPEL:
                case DEV_VP_UNIVERSITY:
                case DEV_VP_PALACE:
                case DEV_VP_MARKET:
                case DEV_VP_LIBRARY:
                    vicPntsPublic++;
                    break;
            }
        }

        // Count private/unplayed victory points
        item = newDevCards.iterator();
        while (item.hasNext())
        {
            ResourceCard rc = (ResourceCard) item.next();
            switch (rc.type)
            {
                case DEV_VP_CHAPEL:
                case DEV_VP_UNIVERSITY:
                case DEV_VP_PALACE:
                case DEV_VP_MARKET:
                case DEV_VP_LIBRARY:
                    vicPntsHidden++;
                    break;
            }
        }

        maxRoadLen = calcRoadLens(null);

        // Calculate longest road
        if ((this.maxRoadLen >= 5) || (this.longestRoadCard != null))
        {
            Player hasLRoad = null;
            
            // A road block has been build in place, so we loose the longest road card!
            if (this.maxRoadLen < 5)            
                this.longestRoadCard = null;
            
            for (Player p : this.gameRules.players)
            {
                if (p.longestRoadCard != null)
                {
                    hasLRoad = p;
                    break;
                }
            }
            
            if ((hasLRoad != this) && (hasLRoad != null))
            {
                // Measure the current holder's road length freshly. calcVictoryPoints()
                // only refreshes the calling player's own maxRoadLen, so the holder's
                // cached value can be stale within the per-player update pass fired after
                // a build. Comparing against a stale (too large) value would let the holder
                // keep the card even after a challenger's road became strictly longer.
                hasLRoad.maxRoadLen = hasLRoad.calcRoadLens(null);
                if (maxRoadLen > hasLRoad.maxRoadLen)
                {
                    this.gameRules.playSound(AudioClipTypes.LONGEST_ROAD);
                    this.gameRules.setMsgLog(this.name + " obtained the longest road");

                    this.longestRoadCard = hasLRoad.longestRoadCard;
                    hasLRoad.longestRoadCard = null;
                    hasLRoad.calcVictoryPoints();
                }
            }
            else if ((this.longestRoadCard == null) && (this.maxRoadLen >= 5))
            {
                this.gameRules.playSound(AudioClipTypes.LONGEST_ROAD);
                this.gameRules.setMsgLog(this.name + " obtained the longest road");
                this.longestRoadCard = new ResourceCard(ResCardTypes.LONGEST_ROAD);
            }
        }

        // Calculate largest army
        int armyNum = this.countResType(this.usedDevCards, ResCardTypes.DEV_ARMY);
        if (armyNum >= 3)
        {
            Player hasLArmy = null;
            for (Player p : this.gameRules.players)
            {
                if (p.largestArmyCard != null)
                {
                    hasLArmy = p;
                    break;
                }
            }
            if ((hasLArmy != this) && (hasLArmy != null))
            {
                if (armyNum > hasLArmy.countResType(hasLArmy.usedDevCards, ResCardTypes.DEV_ARMY))
                {

                    this.gameRules.playSound(AudioClipTypes.LARGEST_ARMY);
                    this.gameRules.setMsgLog(this.name + " obtained the largest army");

                    this.largestArmyCard = hasLArmy.largestArmyCard;
                    hasLArmy.largestArmyCard = null;
                    hasLArmy.calcVictoryPoints();
                }
            }
            else if (this.largestArmyCard == null)
            {
                this.gameRules.playSound(AudioClipTypes.LARGEST_ARMY);
                this.gameRules.setMsgLog(this.name + " obtained the largest army");
                this.largestArmyCard = new ResourceCard(ResCardTypes.LARGEST_ARMY);
            }
        }

        vicPntsTotalNoArmyNoRoad = vicPntsHidden + vicPntsPublic;

        if (this.longestRoadCard != null)
        {
            Debug("calcVictoryPoints:" + this.name + " Have longest road", DebugLevel.MEDIUM);
            vicPntsPublic += 2;
        }

        if (this.largestArmyCard != null)
        {
            Debug("calcVictoryPoints:" + this.name + " Have largest army", DebugLevel.MEDIUM);
            vicPntsPublic += 2;
        }

        vicPntsTotal = vicPntsHidden + vicPntsPublic;

        Debug("calcVictoryPoints:" + this.name + " Public Victory Points: " + vicPntsPublic, DebugLevel.MEDIUM);
        Debug("calcVictoryPoints:" + this.name + " Hidden Victory Points: " + vicPntsHidden, DebugLevel.MEDIUM);
        Debug("calcVictoryPoints:" + this.name + " Total Victory Points: " + vicPntsTotal, DebugLevel.MEDIUM);
        Debug("calcVictoryPoints:" + this.name + " Max Road Len: " + maxRoadLen, DebugLevel.MEDIUM);

        // check if player has won?
        boolean v = gameRules.checkForVictory(this);

        this.refreshPlayerInfo();

        if (v != false)
        {
            throw new CatanEndGameException();
        }
    }

    public void giveInitialResources()
    {
        Iterator item = ((BuildPoint) (builtObjs.getLast())).tileJoins.iterator();

        while (item.hasNext())
        {
            Tile t = (Tile) item.next();
            switch (t.type)
            {
                case BRICK:
                    this.gameRules.setLog(name + "\t received initial resource of " + t.type.toString());
                    resCards.add(new ResourceCard(ResCardTypes.BRICK));
                    break;
                case ROCK:
                    this.gameRules.setLog(name + "\t received initial resource of " + t.type.toString());
                    resCards.add(new ResourceCard(ResCardTypes.ROCK));
                    break;
                case WHEAT:
                    this.gameRules.setLog(name + "\t received initial resource of " + t.type.toString());
                    resCards.add(new ResourceCard(ResCardTypes.WHEAT));
                    break;
                case WOOD:
                    this.gameRules.setLog(name + "\t received initial resource of " + t.type.toString());
                    resCards.add(new ResourceCard(ResCardTypes.WOOD));
                    break;
                case SHEEP:
                    this.gameRules.setLog(name + "\t received initial resource of " + t.type.toString());
                    resCards.add(new ResourceCard(ResCardTypes.SHEEP));
                    break;
            }
        }
    }

    public void eatBuild(CanBuildTypes cbt)
    {
        switch (cbt)
        {
            case SETTLEMENT:
                delResType(ResCardTypes.WOOD, 1);
                delResType(ResCardTypes.BRICK, 1);
                delResType(ResCardTypes.SHEEP, 1);
                delResType(ResCardTypes.WHEAT, 1);
                break;

            case ROAD:
                delResType(ResCardTypes.WOOD, 1);
                delResType(ResCardTypes.BRICK, 1);
                break;

            case DEVCARD:
                delResType(ResCardTypes.ROCK, 1);
                delResType(ResCardTypes.SHEEP, 1);
                delResType(ResCardTypes.WHEAT, 1);
                break;

            case CITY:
                delResType(ResCardTypes.WHEAT, 2);
                delResType(ResCardTypes.ROCK, 3);
                break;
        }
    }

    public int canBuild(CanBuildTypes cbt)
    {
        int sheep = this.countResType(resCards, ResCardTypes.SHEEP);
        int wheat = this.countResType(resCards, ResCardTypes.WHEAT);
        int brick = this.countResType(resCards, ResCardTypes.BRICK);
        int wood = this.countResType(resCards, ResCardTypes.WOOD);
        int rock = this.countResType(resCards, ResCardTypes.ROCK);
        int num = 0;

        switch (cbt)
        {
            case SETTLEMENT:
                for (;;)
                {
                    if (sheep > 0 && wheat > 0 && brick > 0 && wood > 0)
                    {
                        sheep--;
                        wheat--;
                        brick--;
                        wood--;
                        num++;
                    }
                    else
                    {
                        break;
                    }
                }
                break;

            case ROAD:
                for (;;)
                {
                    if (brick > 0 && wood > 0)
                    {
                        brick--;
                        wood--;
                        num++;
                    }
                    else
                    {
                        break;
                    }
                }
                break;

            case DEVCARD:
                for (;;)
                {
                    if (sheep > 0 && wheat > 0 && rock > 0)
                    {
                        sheep--;
                        wheat--;
                        rock--;
                        num++;
                    }
                    else
                    {
                        break;
                    }
                }
                break;

            case CITY:
                for (;;)
                {
                    if (wheat >= 2 && rock >= 3)
                    {
                        wheat -= 2;
                        rock -= 3;
                        num++;
                    }
                    else
                    {
                        break;
                    }
                }
                break;
        }

        return num;
    }

    public boolean buyDevCard() throws CatanEndGameException
    {
        if (this.canBuild(CanBuildTypes.DEVCARD) <= 0)
        {
            return false;
        }
        if (this.gameRules.gameDevCards.size() <= 0)
        {
            return false;
        }
        
        if (this.gameRules.gameTurnNo > devCardPurchasedOnTurn)        
        {
            devCardPurchasedOnTurn = this.gameRules.gameTurnNo;
            devCardPurchasedNo = 1;
        }
        else
        {
            devCardPurchasedNo++;
        }

        int idx = this.gameRules.rand.nextInt(this.gameRules.gameDevCards.size());
        ResourceCard rc = (ResourceCard) this.gameRules.gameDevCards.get(idx);
        this.gameRules.gameDevCards.remove(idx);
        this.gameRules.playSound(AudioClipTypes.DEVCARD);

        switch (rc.type)
        {
            // additional victory points, these can be played any time.
            case DEV_VP_CHAPEL:
            case DEV_VP_UNIVERSITY:
            case DEV_VP_PALACE:
            case DEV_VP_MARKET:
            case DEV_VP_LIBRARY:
                break;
            default:
                rc.purchasedOnTurn = this.gameRules.gameTurnNo;
        }

        this.eatBuild(CanBuildTypes.DEVCARD);
        this.newDevCards.add(rc);

        // Add informative text
        String s = this.name + " purchases a development card";
        switch (gameRules.gameWindow.currTurnMode)
        {
            case ENDOUTOFTURNBUILD:
                s += " [out of turn buy]";
                break;
        }

        calcVictoryPoints();
        this.gameRules.setMsgLog(s);
        this.gameRules.gameWindow.repaint();

        if (this.type == PlayerTypes.COMPUTER)
        {
            this.pause(500);
        }

        return true;
    }

    // volcano Variant
    public boolean reduceBuildObject(BuildPoint bp) throws CatanEndGameException
    {
        if (bp.owner != this)
        {
            return false;
        }
        switch (bp.type)
        {
            case CITY:
                bp.type = BuildPointTypes.SETTLEMENT;
                this.bldStckCity++;
                this.bldStckSettlement--;
                calcVictoryPoints();
                break;

            case SETTLEMENT:
                bp.type = BuildPointTypes.BUILDABLE_LAND;
                builtObjs.remove(bp);
                calcVictoryPoints();
                bp.owner = null;
                this.bldStckSettlement++;
                break;

            default:
                return false;
        }

        return true;
    }

    public boolean buildObject(CanBuildTypes t, Object item) throws CatanEndGameException
    {
        switch (t)
        {
            case CITY:
                if (bldStckCity <= 0)
                {
                    return false;
                }
                switch (this.gameRules.gamePhase)
                {
                    case TRADE_BUILD:
                    case BUILD_CITY:
                    case TRADE_WITHPLYRS:
                    case OUTOFTURNBUILD:
                        this.eatBuild(t);
                }

                if (this.type == PlayerTypes.COMPUTER)
                {
                    this.gameBoard.blinkBGInit();
                }
                bldStckCity--;
                bldStckSettlement++;
                ((BuildPoint) item).owner = this;
                ((BuildPoint) item).type = BuildPointTypes.CITY;
                this.gameRules.playSound(AudioClipTypes.CITY);

                if (this.type == PlayerTypes.COMPUTER)
                {
                    this.gameBoard.blinkBGObj(700, (CatanGraphBase) item, null);
                }
                break;

            case SETTLEMENT:
                if (bldStckSettlement <= 0)
                {
                    return false;
                }
                switch (this.gameRules.gamePhase)
                {
                    case TRADE_BUILD:
                    case BUILD_SETTLEMENT:
                    case TRADE_WITHPLYRS:
                    case OUTOFTURNBUILD:
                        this.eatBuild(t);
                }

                if (this.type == PlayerTypes.COMPUTER)
                {
                    this.gameBoard.blinkBGInit();
                }

                bldStckSettlement--;
                this.builtObjs.add((BuildPoint) item);
                ((BuildPoint) item).owner = this;
                ((BuildPoint) item).type = BuildPointTypes.SETTLEMENT;
                this.gameRules.playSound(AudioClipTypes.SETTLEMENT);

                if (this.type == PlayerTypes.COMPUTER)
                {
                    this.gameBoard.blinkBGObj(700, (CatanGraphBase) item, null);
                }
                break;

            case ROAD:
                if (bldStckRoad <= 0)
                {
                    return false;
                }
                switch (this.gameRules.gamePhase)
                {
                    case TRADE_BUILD:
                    case BUILD_ROAD:
                    case TRADE_WITHPLYRS:
                    case OUTOFTURNBUILD:
                        this.eatBuild(t);
                }

                if (this.type == PlayerTypes.COMPUTER)
                {
                    this.gameBoard.blinkBGInit();
                }

                bldStckRoad--;
                this.builtRoadObjs.add((Road) item);
                ((Road) item).owner = this;
                ((Road) item).type = RoadTypes.BUILT;
                this.gameRules.playSound(AudioClipTypes.ROAD);

                if (this.type == PlayerTypes.COMPUTER)
                {
                    this.gameBoard.blinkBGObj(700, (CatanGraphBase) item, null);
                }
                break;

            default:
                return false;
        }

        // Add informative text
        String s = this.name + " built a " + t.name();
        switch (gameRules.gameWindow.currTurnMode)
        {
            case ENDOUTOFTURNBUILD:
                s += " [out of turn build]";
                break;
        }

        // repaint the game area now!
        for (Player p:this.gameRules.players)
            p.calcVictoryPoints();

        this.gameRules.setMsgLog(s);

this.gameBoard.clearHighlightAssist();
        this.gameRules.gameWindow.updateWindowImmediately();

        return true;
    }

    // priorityLevel: 0  = All Trade Wants
    //                1  = Really Needed Trade Wants
    //                2  = Potential Future Trades 
    public void COMP_needResFor(CanBuildTypes            wantObjType, 
                                LinkedList<ResourceCard> wantResCards, 
                                LinkedList<ResourceCard> canTradeResCards,
                                int                      priorityLevel) 
    {
        int sheep = this.countResType(resCards, ResCardTypes.SHEEP);
        int wheat  = this.countResType(resCards, ResCardTypes.WHEAT);
        int brick = this.countResType(resCards, ResCardTypes.BRICK);
        int wood  = this.countResType(resCards, ResCardTypes.WOOD);
        int rock  = this.countResType(resCards, ResCardTypes.ROCK);

        switch (wantObjType)
        {
            case ROAD:
                if (wood <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.WOOD));
                }
                if (brick <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.BRICK));
                }
                
                if (sheep > 0)
                {
                    for (int i = 0; i < sheep; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.SHEEP));
                    }
                }
                if (wheat > 0)
                {
                    for (int i = 0; i < wheat; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WHEAT));
                    }
                }
                if (wood > 1)
                {
                    for (int i = 1; i < wood; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WOOD));
                    }
                }
                if (brick > 1)
                {
                    for (int i = 1; i < brick; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.BRICK));
                    }
                }
                if (rock > 0)
                {
                    for (int i = 0; i < rock; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.ROCK));
                    }
                }
                break;

            case SETTLEMENT:
                if (sheep <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.SHEEP));
                }
                if (wheat <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.WHEAT));
                }
                if (wood <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.WOOD));
                }
                if (brick <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.BRICK));
                }
                if (sheep > 1)
                {
                    for (int i = 1; i < sheep; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.SHEEP));
                    }
                }
                if (wheat > 1)
                {
                    for (int i = 1; i < wheat; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WHEAT));
                    }
                }
                if (wood > 1)
                {
                    for (int i = 1; i < wood; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WOOD));
                    }
                }
                if (brick > 1)
                {
                    for (int i = 1; i < brick; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.BRICK));
                    }
                }
                if (rock > 0)
                {
                    for (int i = 0; i < rock; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.ROCK));
                    }
                }
                break;

            case CITY:
                if (wheat < 2)
                {
                    for (int i = wheat; i < 2; i++)
                    {
                        wantResCards.add(new ResourceCard(ResCardTypes.WHEAT));
                    }
                }
                if (rock < 3)
                {
                    for (int i = rock; i < 3; i++)
                    {
                        wantResCards.add(new ResourceCard(ResCardTypes.ROCK));
                    }
                }
                if (sheep > 0)
                {
                    for (int i = 0; i < sheep; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.SHEEP));
                    }
                }
                if (wheat > 2)
                {
                    for (int i = 2; i < wheat; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WHEAT));
                    }
                }
                if (wood > 0)
                {
                    for (int i = 0; i < wood; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WOOD));
                    }
                }
                if (brick > 0)
                {
                    for (int i = 1; i < brick; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.BRICK));
                    }
                }
                if (rock > 3)
                {
                    for (int i = 3; i < rock; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.ROCK));
                    }
                }
                break;

            case DEVCARD:
                if (sheep <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.SHEEP));
                }
                if (wheat <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.WHEAT));
                }
                if (rock <= 0)
                {
                    wantResCards.add(new ResourceCard(ResCardTypes.ROCK));
                }
                if (sheep > 1)
                {
                    for (int i = 1; i < sheep; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.SHEEP));
                    }
                }
                if (wheat > 1)
                {
                    for (int i = 1; i < wheat; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WHEAT));
                    }
                }
                if (wood > 0)
                {
                    for (int i = 0; i < wood; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.WOOD));
                    }
                }
                if (brick > 0)
                {
                    for (int i = 1; i < brick; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.BRICK));
                    }
                }
                if (rock > 1)
                {
                    for (int i = 1; i < rock; i++)
                    {
                        canTradeResCards.add(new ResourceCard(ResCardTypes.ROCK));
                    }
                }
                break;
        }


        // Remove resources we can't obtain, and keep those we can ...
        LinkedList<ResourceCard> keepers = new LinkedList<ResourceCard>();
        int maxKeepers = 3;
        for (ResourceCard rc : canTradeResCards)
        {
            int score = COMP_CanGetResType(rc.type);
            if ((score <= 2) && (maxKeepers > 0))
            {
                maxKeepers--;
                keepers.add(rc);
            }
        }                                      

        // Purge the keepers from the trader list that are hard to get
        for (ResourceCard rc : keepers)
            canTradeResCards.remove(rc);

        // ----------------------------------------------------

        // If only wanting potentials trade wants, then 
        // remove the current high priority list
        if (priorityLevel == 2)
            wantResCards.clear();
           
        // If no cards to trade, and not after potential trades, then return,
        if ((canTradeResCards.size() <= 0) || (priorityLevel == 1))
            return;
        
        // **** Build Potential Trades Want List ****
        
        // Determine a resource that we are unlikely to obtain 
        // at this moment and trade for that.
        ResCardTypes rtypes[] = {ResCardTypes.BRICK, ResCardTypes.WHEAT, ResCardTypes.ROCK,
                                 ResCardTypes.SHEEP, ResCardTypes.WOOD};
        for (int idx = 0; idx < rtypes.length; idx++)
        {
            int score = COMP_CanGetResType(rtypes[idx]);            
            if ((score <= 2) && (countResType(this.resCards, rtypes[idx]) <= 0))
            {
                this.Debug("Trading : Wanting unlikely resource " + rtypes[idx].toString(), DebugLevel.COMPLETE);  
                wantResCards.add(new ResourceCard(rtypes[idx])); 
            }
        }            
        
        // Determine if we can do a trade for an unwanted card to a another unwanted card but 
        // it would make a port trade.
        int prtSheep = this.countResType(canTradeResCards, ResCardTypes.SHEEP);
        int prtBrick = this.countResType(canTradeResCards, ResCardTypes.BRICK);
        int prtRock  = this.countResType(canTradeResCards, ResCardTypes.ROCK);
        int prtWood  = this.countResType(canTradeResCards, ResCardTypes.WOOD);
        int prtWheat  = this.countResType(canTradeResCards, ResCardTypes.WHEAT);
        
        COMPTurnInfo cpi       = new COMPTurnInfo(this);
        int          pTradeNum = prtSheep / cpi.sheepPTradeNum;
        if (((prtSheep + 1) / cpi.sheepPTradeNum) > pTradeNum)
        {
            this.Debug("Trading : Wanting SHEEP for a possible port trade", DebugLevel.COMPLETE);            
            wantResCards.add(new ResourceCard(ResCardTypes.SHEEP));
        }
                
        pTradeNum = prtBrick / cpi.brickPTradeNum;
        if (((prtBrick + 1) / cpi.brickPTradeNum) > pTradeNum)
        {
            this.Debug("Trading : Wanting BRICK for a possible port trade", DebugLevel.COMPLETE);            
            wantResCards.add(new ResourceCard(ResCardTypes.BRICK));        
        }

        pTradeNum = prtWood / cpi.woodPTradeNum;
        if (((prtWood + 1) / cpi.woodPTradeNum) > pTradeNum)
        {
            this.Debug("Trading : Wanting WOOD for a possible port trade", DebugLevel.COMPLETE);            
            wantResCards.add(new ResourceCard(ResCardTypes.WOOD));        
        }
        
        pTradeNum = prtRock / cpi.rockPTradeNum;
        if (((prtRock + 1) / cpi.rockPTradeNum) > pTradeNum)
        {
            this.Debug("Trading : Wanting ROCK for a possible port trade", DebugLevel.COMPLETE);
            wantResCards.add(new ResourceCard(ResCardTypes.ROCK));        
        }
        
        pTradeNum = prtWheat / cpi.wheatPTradeNum;
        if (((prtWheat + 1) / cpi.wheatPTradeNum) > pTradeNum)
        {
            this.Debug("Trading : Wanting WHEAT for a possible port trade", DebugLevel.COMPLETE);
            wantResCards.add(new ResourceCard(ResCardTypes.WHEAT));
        }
    }

    public void COMP_CalcReceiveGoldRes(LinkedList<ResourceCard> rtnList, int resNumber)
    {
        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo ti = new COMPTurnInfo(this);
        CanBuildTypes next = ti.doNext(wantObjType);

        LinkedList<ResourceCard> wantResCards = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();

        COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, 0);

        for (int resNum = 0; resNum < resNumber; resNum++)
        {
            if (wantResCards.size() >= 1)
            {
                rtnList.add(wantResCards.remove(0));
            }
            else
            {
                wantObjType[0] = ti.helpCantBuildException(wantObjType[0]);
                COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, 0);

                // if still not good resource then try least obtainable resource.
                if (wantResCards.size() >= 1)
                {
                    rtnList.add(wantResCards.remove(0));
                }
                else
                {
                    rtnList.add(new ResourceCard(ti.getLeastLikelyResource())); // determine the least obtainable resource and pick that!

                }
            }
        }
    }

    // -------------------------------------------------------------------------------------------
    public boolean COMP_PlayTurn_EndTurnBuild() throws CatanEndGameException
    {
        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo ti = new COMPTurnInfo(this);
        CanBuildTypes next = ti.doNext(wantObjType);

        switch (next)
        {
            case ROAD:
                if (((this.vicPntsTotalNoArmyNoRoad >= 4) &&
                        (this.longestRoadCard == null)) &&
                        (ti.roadGrpSplts > 1))
                {
                    if (COMP_BuildNextRoadGroupJoin(ti) != false)
                    {
                        return true; // success at building a road!
                    }
                }

                if (COMP_BuildNextRoad(ti.endRoadPoints) == false)
                {
                    Debug("Player.COMP_PlayTurn_EndTurnBuild:Warning! failed  *** ROAD Build ***", DebugLevel.BRIEF);
                    return false;
                }
                return true;

            case SETTLEMENT:
                TileInfo nextBestBuildPoint = COMP_Calc_BestBuildPoint(ti.availBuildPoints);
                this.buildObject(CanBuildTypes.SETTLEMENT, nextBestBuildPoint.bp);
                return true;

            case CITY:
                BuildPoint bp = COMP_Calc_NextCity();
                if (bp == null)
                {
                    Debug("Player.COMP_PlayTurn_EndTurnBuild:Warning! failed  *** CITY Build ***", DebugLevel.BRIEF);
                    return false;
                }
                else
                {
                    this.buildObject(CanBuildTypes.CITY, bp);
                }
                return true;

            case DEVCARD:
                if (this.buyDevCard() == false)
                {
                    Debug("Player.COMP_PlayTurn_EndTurnBuild:Warning! failed  *** Dev Card Out of Stock ***", DebugLevel.BRIEF);
                    return false;
                }
                return true;
        }

        return false;
    }

    // -------------------------------------------------------------------------------------------
    public void COMP_PlayTurn(int level, ResCardTypes[] lastTradedType) throws CatanEndGameException
    {
        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo ti = new COMPTurnInfo(this);
        CanBuildTypes next = ti.doNext(wantObjType);
        boolean breakLoop = false;

        while ((next != CanBuildTypes.NULL) && (breakLoop == false))
        {
            switch (next)
            {
                case ROAD:
                    if ((this.vicPntsTotalNoArmyNoRoad >= 4) && (ti.roadGrpSplts > 1))
                    {
                        if (COMP_BuildNextRoadGroupJoin(ti) != false)
                        {
                            break; // success at building a road!
                        }
                    }

                    if (COMP_BuildNextRoad(ti.endRoadPoints) == false)
                    {
                        wantObjType[0] = ti.helpCantBuildException(next);
                        breakLoop = true;
                        Debug("Player.COMP_PlayTurn:Warning! failed  *** ROAD Build ***", DebugLevel.BRIEF);
                    }
                    break;

                case SETTLEMENT:
                    TileInfo nextBestBuildPoint = COMP_Calc_BestBuildPoint(ti.availBuildPoints);
                    this.buildObject(CanBuildTypes.SETTLEMENT, nextBestBuildPoint.bp);
                    break;

                case CITY:
                    BuildPoint bp = COMP_Calc_NextCity();
                    if (bp == null)
                    {
                        wantObjType[0] = ti.helpCantBuildException(next);
                        breakLoop = true;
                        Debug("Player.COMP_PlayTurn:Warning! failed  *** CITY Build ***", DebugLevel.BRIEF);
                    }
                    else
                    {
                        this.buildObject(CanBuildTypes.CITY, bp);
                    }
                    break;

                case DEVCARD:
                    if (this.buyDevCard() == false)
                    {
                        wantObjType[0] = ti.helpCantBuildException(next);
                        breakLoop = true;
                        Debug("Player.COMP_PlayTurn:Warning! failed  *** Dev Card Out of Stock ***", DebugLevel.BRIEF);
                    }
                    break;

                default:
                    return;
            }

            if (breakLoop == false)
            {
                ti = new COMPTurnInfo(this);
                next = ti.doNext(wantObjType);
            }
        }

        // play any dev cards that are playable ...
        boolean restart;
        for (;;)
        {
            restart = false;
            for (ResourceCard rc : this.newDevCards)
            {
                if (rc.purchasedOnTurn >= this.gameRules.gameTurnNo)
                {
                    continue;
                }
                switch (rc.type)
                {
                    case DEV_ARMY:
                        if (this.devCardPlayedOnTurn == 0)
                        {
                            this.gameRules.playSound(AudioClipTypes.ROBBER);
                            COMP_MoveRobber();
                            this.usedDevCards.add(rc);
                            this.newDevCards.remove(rc);
                            this.calcVictoryPoints();                       
                            this.devCardPlayedOnTurn++;
                            COMP_PlayTurn(level + 1, lastTradedType);
                            restart = true; // do this to way to prevent list interator con-currency
                            // issues due to removing items while selecting them.
                        }
                        break;

                    case DEV_MONOPOLY:
                        if (this.devCardPlayedOnTurn == 0)
                        {                        
                            int arry[] =
                            {
                                0, 0, 0, 0, 0
                            };
                            ResCardTypes rt[] =
                            {
                                ResCardTypes.SHEEP, ResCardTypes.WOOD, ResCardTypes.WHEAT, ResCardTypes.BRICK, ResCardTypes.ROCK
                            };
                            for (GameRules.DiceRollInfo dri : this.gameRules.turnDiceRolls)
                            {
                                arry[0] += dri.count(ResCardTypes.SHEEP);
                                arry[1] += dri.count(ResCardTypes.WOOD);
                                arry[2] += dri.count(ResCardTypes.WHEAT);
                                arry[3] += dri.count(ResCardTypes.BRICK);
                                arry[4] += dri.count(ResCardTypes.ROCK);
                            }
                            int bIdx = 0;
                            for (int idx = 0; idx < 5; idx++)
                            {
                                if (arry[bIdx] < arry[idx])
                                {
                                    bIdx = idx;
                                }
                            }

                            // Make it worth our while
                            if (arry[bIdx] >= 4)
                            {
                                int count = this.appropriateAllOfType(rt[bIdx], this.resCards);

                                this.gameRules.setMsgLog(this.name + " Monopoly : appropriated " + count + " of " + rt[bIdx].toString() + " from other players");
                                this.newDevCards.remove(rc);
                                this.devCardPlayedOnTurn++;
                                COMP_PlayTurn(level + 1, lastTradedType);
                                restart = true; // do this to way to prevent list interator con-currency
                                // issues due to removing items while selecting them.
                            }
                        }
                        break;

                    case DEV_YEAROFPLENTY:
                        if (this.devCardPlayedOnTurn == 0)
                        {                         
                            LinkedList<ResourceCard> wantResCards = new LinkedList<ResourceCard>();
                            LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();

                            COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, 0);
                            if (wantResCards.size() >= 1)
                            {
                                String s = this.name + " Year of Plenty : ";

                                if (wantResCards.size() == 1)
                                {
                                    s += wantResCards.get(0).type.toString() + " & ";
                                    resCards.add(wantResCards.remove(0));

                                    // Try for next best resource/build option.
                                    wantObjType[0] = ti.helpCantBuildException(wantObjType[0]);
                                    COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, 0);

                                    // if still not good resource then try least obtainable resource.
                                    ResourceCard r = new ResourceCard(ti.getLeastLikelyResource());

                                    if (wantResCards.size() >= 1)
                                    {
                                        r = wantResCards.remove(0);
                                    }
                                    else
                                    {
                                        r = new ResourceCard(ti.getLeastLikelyResource()); // determine the least obtainable resource and pick that!

                                    }
                                    s += r.type.toString();
                                    resCards.add(r);
                                }
                                else
                                {
                                    s += wantResCards.get(0).type.toString() + " & " + wantResCards.get(1).type.toString();
                                    resCards.add(wantResCards.remove(0));
                                    resCards.add(wantResCards.remove(0));
                                }

                                this.gameRules.setMsgLog(s);
                                this.newDevCards.remove(rc);
                                this.devCardPlayedOnTurn++;
                                COMP_PlayTurn(level + 1, lastTradedType);
                                restart = true; // do this to way to prevent list interator con-currency
                                // issues due to removing items while selecting them.
                            }
                        }

                        break;

                    case DEV_ROADBUILD:
                        if (this.devCardPlayedOnTurn == 0)
                        {                           
                            int i = 2;
                            do
                            {
                                if (COMP_BuildNextRoad(ti.endRoadPoints) == false)
                                {
                                    DebugErr("Player.COMP_PlayTurn:Error failed  *** ROAD *** build!");
                                    return;
                                }
                                i--;
                                ti = new COMPTurnInfo(this);
                            } while (i > 0);
                            this.usedDevCards.add(rc);
                            this.newDevCards.remove(rc);
                            this.devCardPlayedOnTurn++;
                            restart = true; // do this to way to prevent list interator con-currency
                            // issues due to removing items while selecting them.
                        }
                        break;

                    // additional victory points, these can remain hidden!
                    case DEV_VP_CHAPEL:
                    case DEV_VP_UNIVERSITY:
                    case DEV_VP_PALACE:
                    case DEV_VP_MARKET:
                    case DEV_VP_LIBRARY:
                        break;
                }
                if (restart != false)
                {
                    break;
                }
            }
            if (restart == false)
            {
                break;
            }
        }
        
        // End this turn now ... have gotten into an infinite recursive loop
        if (level > 16)  return;
        
// TODO : Determine trade order type , Humans first or Player Ordered.        
        //        if (COMP_Trade_Trader(lastTradedType) != false)
        if (COMP_Trade_Trader_PlayOrdered(lastTradedType) != false)                
        {            
            COMP_PlayTurn(level + 1, lastTradedType);
        }
        
        // Conduct a port trade ...
        if (COMP_Trade_Port(ti, wantObjType[0]) > 0)
        {
            COMP_PlayTurn(level + 1, lastTradedType);
        }
    }

    // -------------------------------------------------------------------------------------------
    public void COMP_PurgeDiceRoll7()
    {
        if (resCards.size() <= 7)
        {
            return;
        }
        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo ti = new COMPTurnInfo(this);
        CanBuildTypes next = ti.doNext(wantObjType);

        LinkedList<ResourceCard> wantResCards = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canDelResCards = new LinkedList<ResourceCard>();
        this.COMP_needResFor(wantObjType[0], wantResCards, canDelResCards, 0);

        int delNum = (int) (resCards.size() / 2);

        Debug(name + " COMP_PurgeDiceRoll7: resNum:" + resCards.size() + " delNum:" + delNum + " guessDel:" + canDelResCards.size() + " wantToBuild:" + wantObjType[0].name(), DebugLevel.COMPLETE);
        for (ResourceCard rc : canDelResCards)
        {
            if (delNum > 0)
            {
                this.delResType(rc.type, 1);
                Debug(name + " COMP_PurgeDiceRoll7: discarding (1) " + rc.type.toString(), DebugLevel.COMPLETE);
                delNum--;
            }
            else
            {
                break;
            }
        }

        canDelResCards.clear();
        if (delNum > 0)
        {
            for (ResourceCard rc : resCards)
            {
                switch (wantObjType[0])
                {
                    case ROAD:
                        if ((rc.type != ResCardTypes.BRICK) && (rc.type != ResCardTypes.WOOD))
                        {
                            canDelResCards.add(rc);
                            delNum--;
                        }
                        break;
                    case CITY:
                        if ((rc.type != ResCardTypes.WHEAT) && (rc.type != ResCardTypes.ROCK))
                        {
                            canDelResCards.add(rc);
                            delNum--;
                        }
                        break;
                    case SETTLEMENT:
                        if ((rc.type != ResCardTypes.WHEAT) && (rc.type != ResCardTypes.WOOD) &&
                                (rc.type != ResCardTypes.SHEEP) && (rc.type != ResCardTypes.BRICK))
                        {
                            canDelResCards.add(rc);
                            delNum--;
                        }
                        break;
                    case DEVCARD:
                        if ((rc.type != ResCardTypes.WHEAT) && (rc.type != ResCardTypes.SHEEP) && (rc.type != ResCardTypes.BRICK))
                        {
                            canDelResCards.add(rc);
                            delNum--;
                        }
                        break;

                    default:
                        break;
                }

                if (delNum <= 0)
                {
                    break;
                }
            }

            // Another purge ...
            for (ResourceCard rc : canDelResCards)
            {
                this.delResType(rc.type, 1);
                Debug(name + " COMP_PurgeDiceRoll7: discarding (2) " + rc.type.toString(), DebugLevel.COMPLETE);
            }
        }

        // Purge all resource we can obtain, and keep those we cant ...
        if (delNum > 0)
        {
            canDelResCards.clear();
            for (ResourceCard rc : resCards)
            {
                int score = COMP_CanGetResType(rc.type);
                if (score >= 3)
                {
                    canDelResCards.add(rc);
                    delNum--;
                }

                if (delNum <= 0)
                {
                    break;
                }
            }

            // Another purge ...
            for (ResourceCard rc : canDelResCards)
            {
                this.delResType(rc.type, 1);
                Debug(name + " COMP_PurgeDiceRoll7: discarding (3) " + rc.type.toString(), DebugLevel.COMPLETE);
            }
        }

        // Last resort purge ...
        if (delNum > 0)
        {
            canDelResCards.clear();
            for (ResourceCard rc : resCards)
            {
                canDelResCards.add(rc);
                delNum--;
                if (delNum <= 0)
                {
                    break;
                }
            }

            for (ResourceCard rc : canDelResCards)
            {
                this.delResType(rc.type, 1);
                Debug(name + " COMP_PurgeDiceRoll7: discarding (4) " + rc.type.toString(), DebugLevel.COMPLETE);
            }
        }

    }

    public int COMP_CanGetResType(ResCardTypes resType)
    {
        int score = 0;
        for (BuildPoint bp : this.builtObjs)
        {
            int mult = 1;
            if (bp.type == BuildPointTypes.CITY)
            {
                mult = 2;
            }
            for (Tile t : bp.tileJoins)
            {
                switch (resType)
                {
                    case SHEEP:
                        if (t.type == TileTypes.SHEEP)
                        {
                            score += (t.diceRollScore * mult);
                        }
                        break;
                    case WHEAT:
                        if (t.type == TileTypes.WHEAT)
                        {
                            score += (t.diceRollScore * mult);
                        }
                        break;
                    case WOOD:
                        if (t.type == TileTypes.WOOD)
                        {
                            score += (t.diceRollScore * mult);
                        }
                        break;
                    case BRICK:
                        if (t.type == TileTypes.BRICK)
                        {
                            score += (t.diceRollScore * mult);
                        }
                        break;
                    case ROCK:
                        if (t.type == TileTypes.ROCK)
                        {
                            score += (t.diceRollScore * mult);
                        }
                        break;
                }
            }
        }

        return score;
    }

    /**
     * The robber steal: move one uniformly random resource card from the victim
     * into this player's hand and return it. A victim's hand is hidden, so the
     * robber cannot legitimately choose a card - the pick must be random.
     * Returns null (and changes nothing) if the victim is null or has no cards.
     */
    public ResourceCard stealRandomCard(Player victim)
    {
        if ((victim == null) || (victim.resCards.isEmpty()))
        {
            return null;
        }

        ResourceCard rc = victim.resCards.get(this.gameRules.rand.nextInt(victim.resCards.size()));
        victim.resCards.remove(rc);
        this.resCards.add(rc);
        return rc;
    }

    public void COMP_MoveRobber()
    {
        Player leadPlayer = null;
        class TilePick
        {

            Tile t = null;
            int score = 0; // larger score = better place to put robber.

            LinkedList<Player> plyrs = new LinkedList<Player>();

            TilePick(Tile t)
            {
                this.t = t;
            }
        }

        // Pick player with the most known victory points
        for (Player p : this.gameRules.players)
        {
            if (p == this)
            {
                continue;
            }
            if (leadPlayer == null)
            {
                leadPlayer = p;
            }
            else if (leadPlayer.vicPntsPublic < p.vicPntsPublic)
            {
                leadPlayer = p;
            }
        }

        // Make a list of all tiles that are not assicated with this player
        TilePick bestPick = null;
        Tile lastRobberTile = null;
        for (Tile t : this.gameBoard.tileList)
        {
            switch (t.type)
            {
                case WOOD:
                case SHEEP:
                case BRICK:
                case ROCK:
                case WHEAT:
                case VAR_VOLCANO:
                    break;
                default:
                    continue;
            }

            if (t.hasRobber != false)
            {
                continue;

            // Score this tile for selection.
            }
            TilePick tPick = new TilePick(t);
            tPick.score = t.diceRollScore;
            for (BuildPoint bp : t.buildJoins)
            {
                if (bp.owner == this)
                {
                    tPick.score -= 7; // Don't pick this tile, as it has this player associated with it.

                    continue;
                }
                else if (bp.owner != null)
                {
                    tPick.score++;
                    if (tPick.plyrs.contains(bp.owner) == false)
                    {
                        tPick.plyrs.add(bp.owner);
                        tPick.score++;

                        if (bp.owner.resCards.size() > 0)
                        {
                            tPick.score++;
                        }
                    }
                }

                if (bp.type == BuildPointTypes.CITY)
                {
                    tPick.score += 2;
                }
                if (bp.owner == leadPlayer)
                {
                    tPick.score++;
                }
            }

            if (bestPick == null)
            {
                bestPick = tPick;
            }
            else if (bestPick.score < tPick.score)
            {
                bestPick = tPick;
            }
        }

        if (bestPick == null)
        {
            DebugErr("Player.COMP_PlayTurn:Error failed to pick a robber tile!");
            return;
        }

        // Change the robber location.
        for (Tile tr : this.gameBoard.tileList)
        {
            if (tr.hasRobber != false)
            {
                lastRobberTile = tr;
                this.gameBoard.blinkBGInit();
                tr.hasRobber = false;
            }
        }
        bestPick.t.hasRobber = true;

        // Pick a player to steal from ...
        Player pickPlyr = null;
        for (Player p : bestPick.plyrs)
        {
            if ((leadPlayer == p) && (leadPlayer.resCards.size() > 0))
            {
                pickPlyr = p;
                break;
            }

            if (pickPlyr == null)
            {
                pickPlyr = p;
            }
            else if ((p.resCards.size() > 0) && (p.vicPntsPublic < pickPlyr.vicPntsPublic))
            {
                pickPlyr = p;
            }
        }

        // Rob from player!!! (a tile can have no reachable opponent, in which
        // case pickPlyr stays null and only the robber moves.)
        if (pickPlyr != null)
        {
            this.stealRandomCard(pickPlyr);
        }

        this.gameRules.gameWindow.updateWindowImmediately(); // Update resource panel show missing/stolen goods.

        this.gameBoard.clrDblBuffCache();
        java.awt.Rectangle lastRobberArea = (lastRobberTile != null) ? lastRobberTile.getBounds() : null;
        this.gameBoard.blinkBGObj(700, (CatanGraphBase) bestPick.t, lastRobberArea);
        this.gameBoard.repaint();

        if (pickPlyr != null)
            this.gameRules.setMsgLog(name + " moved robber and stole from player " + pickPlyr.name);
        else
            this.gameRules.setMsgLog(name + " moved robber");
    }

    public ResCardTypes COMP_CostBenefitTrade(ResourceCard buyCard, LinkedList<ResourceCard> traderList)
    {
        // if we have this card ....
        if (this.countResType(resCards, buyCard.type) <= 0)
        {
            return ResCardTypes.NULL;
        }
        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo cti = new COMPTurnInfo(this);
        CanBuildTypes canDo = cti.doNext(wantObjType);

        // Do a cost benefit analysis
        for (ResourceCard rc : traderList)
        {
            switch (canDo)
            {
                case CITY:
                    return ResCardTypes.NULL;

                case SETTLEMENT:
                    {
                        LinkedList<ResourceCard> saveList = new LinkedList<ResourceCard>();
                        saveList.addAll(resCards);

                        this.delResType(buyCard.type, 1);
                        resCards.add(rc);

                        CanBuildTypes wot[] =
                        {
                            CanBuildTypes.NULL
                        };
                        COMPTurnInfo cti2 = new COMPTurnInfo(this);
                        CanBuildTypes canDo2 = cti2.doNext(wantObjType);

                        resCards = saveList;

                        if (canDo2 == CanBuildTypes.CITY)
                        {
                            return rc.type;
                        }
                    }
                    break;

                case ROAD:
                    {
                        LinkedList<ResourceCard> saveList = new LinkedList<ResourceCard>();
                        saveList.addAll(resCards);

                        this.delResType(buyCard.type, 1);
                        resCards.add(rc);

                        CanBuildTypes wot[] =
                        {
                            CanBuildTypes.NULL
                        };
                        COMPTurnInfo cti2 = new COMPTurnInfo(this);
                        CanBuildTypes canDo2 = cti2.doNext(wantObjType);

                        resCards = saveList;

                        switch (canDo2)
                        {
                            case CITY:
                            case SETTLEMENT:
                            case DEVCARD:
                                return rc.type;
                        }
                    }
                    break;

                case DEVCARD:
                    {
                        LinkedList<ResourceCard> saveList = new LinkedList<ResourceCard>();
                        saveList.addAll(resCards);

                        this.delResType(buyCard.type, 1);
                        resCards.add(rc);

                        CanBuildTypes wot[] =
                        {
                            CanBuildTypes.NULL
                        };
                        COMPTurnInfo cti2 = new COMPTurnInfo(this);
                        CanBuildTypes canDo2 = cti2.doNext(wantObjType);

                        resCards = saveList;

                        switch (canDo2)
                        {
                            case CITY:
                            case SETTLEMENT:
                                return rc.type;
                        }
                    }
                    break;
                case NULL:
                    {
                        LinkedList<ResourceCard> saveList = new LinkedList<ResourceCard>();
                        saveList.addAll(resCards);

                        this.delResType(buyCard.type, 1);
                        resCards.add(rc);

                        CanBuildTypes wot[] =
                        {
                            CanBuildTypes.NULL
                        };
                        COMPTurnInfo cti2 = new COMPTurnInfo(this);
                        CanBuildTypes canDo2 = cti2.doNext(wantObjType);

                        resCards = saveList;

                        switch (canDo2)
                        {
                            case DEVCARD:
                            case CITY:
                            case SETTLEMENT:
                                return rc.type;
                        }
                    }
                    break;
            }
        }

        return ResCardTypes.NULL;
    }

    public boolean COMP_Trade_Trader_PlayOrdered(ResCardTypes[] lastTradedType)
    {
        boolean                  tradeSuccess     = false;
        LinkedList<ResourceCard> wantResCards     = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();
        int                      lastTradeIdx     = 0;

        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo cti = new COMPTurnInfo(this);
        CanBuildTypes next = cti.doNext(wantObjType);

                
        for (int tradePriority = 1; tradePriority <= 2; tradePriority++)
        {
            COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, tradePriority);
                                
            int plyrCount  = 0;
            int plyrIdx    = this.gameRules.playOrderIdx;
            int plyrMaxIdx = this.gameRules.players.size();
            while (plyrCount < plyrMaxIdx)
            {
                Player p = this.gameRules.players.get(plyrIdx);
                if (p == this)
                {
                    // Next player ...
                    plyrIdx++;
                    if (plyrIdx >= plyrMaxIdx)
                        plyrIdx = 0;
                    plyrCount++;                                        
                    continue;
                }
                
                // don't trade with winning players!
                if (this.compSkillLevel != CompSkillLevel.EASIER)
                {
                    if ((p.vicPntsPublic >= 7) && (this.vicPntsTotal < p.vicPntsPublic))
                    {
                        // Next player ...
                        plyrIdx++;
                        if (plyrIdx >= plyrMaxIdx)
                            plyrIdx = 0;
                        plyrCount++;                         
                        continue;
                    }
                }

                ResourceCard hasThis = null;
                for (ResourceCard wrc : wantResCards)
                {
                    for (ResourceCard rc : p.resCards)
                    {
                        if (rc.type == wrc.type)
                        {
                            hasThis = wrc;
                            break;
                        }
                    }

                    if (hasThis != null)
                    {
                        break;
                    }
                }

                if (p.type == PlayerTypes.HUMAN)
                {
                    // Start a trade (Other player sell) operation
                    if ((hasThis != null) && (canTradeResCards.size() > 0))
                    {
                        // Dont annoy player with the same trade of the same type more than once.
                        if ((hasThis.type == lastTradedType[0]) || 
                            (hasThis.type == lastTradedType[1]))

                        {
                            // Next player ...
                            plyrIdx++;
                            if (plyrIdx >= plyrMaxIdx)
                                plyrIdx = 0;
                            plyrCount++;                             
                            continue;
                        }
                        TradeJDialog tradeBuy = new TradeJDialog(gameRules.gameWindow, true, this.gameRules.gameWindow.playerInfo, this.gameRules.tradeDialogLastPos);

                        tradeBuy.assignThisPlayer(p, TradeTypes.SELL, true);
                        tradeBuy.tradeResCardThis(tradeBuy.getTradingCardByType(hasThis.type), this, canTradeResCards);

                        if (tradeBuy.hasTradeItemsToTrade() != false)
                        {
                            tradeBuy.setVisible(true);
                            lastTradeIdx = (lastTradeIdx + 1) % 2;
                            lastTradedType[lastTradeIdx] = hasThis.type;
                            if (tradeBuy.tradeSuccess != false)
                            {
                                this.gameRules.gameWindow.updateWindowImmediately();
                                tradeSuccess = true;
                                wantResCards.clear();
                                canTradeResCards.clear();
                                COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, tradePriority);
                            }
                        }
                    }
                }
                else if (p.type == PlayerTypes.COMPUTER)
                {
                    // Start a trade (Other player sell) operation
                    if ((hasThis != null) && (canTradeResCards.size() > 0))
                    {
                        // Collect stats from other computer ...
                        if (p.COMP_Trade_With_COMP(this, hasThis, canTradeResCards) != false)
                        {
                            tradeSuccess = true;
                            wantResCards.clear();
                            canTradeResCards.clear();
                            COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, tradePriority);
                        }
                    }                    
                }
                
                // Next player ....
                plyrIdx++;
                if (plyrIdx >= plyrMaxIdx)
                    plyrIdx = 0;
                plyrCount++;
            }            
        }

        return tradeSuccess;
    }

    
    public boolean COMP_Trade_Trader_HumansFirst(ResCardTypes[] lastTradedType)
    {
        boolean                  tradeSuccess     = false;
        LinkedList<ResourceCard> wantResCards     = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();
        int                      lastTradeIdx     = 0;

        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo cti = new COMPTurnInfo(this);
        CanBuildTypes next = cti.doNext(wantObjType);

        for (int tradePriority = 1; tradePriority <= 2; tradePriority++)
        {
            COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, tradePriority);

            // Human Players first ...
            for (Player p : this.gameRules.players)
            {
                if (p == this)
                {
                    continue;
                }
                
                if (p.type == PlayerTypes.COMPUTER)
                {
                    continue;                
                }
                
                // don't trade with winning players!
                if (this.compSkillLevel != CompSkillLevel.EASIER)
                {
                    if ((p.vicPntsPublic >= 7) && (this.vicPntsTotal < p.vicPntsPublic))
                    {
                        continue;
                    }
                }

                ResourceCard hasThis = null;
                for (ResourceCard wrc : wantResCards)
                {
                    for (ResourceCard rc : p.resCards)
                    {
                        if (rc.type == wrc.type)
                        {
                            hasThis = wrc;
                            break;
                        }
                    }

                    if (hasThis != null)
                    {
                        break;
                    }
                }

                // Start a trade (Other player sell) operation
                if ((hasThis != null) && (canTradeResCards.size() > 0))
                {
                    // Dont annoy player with the same trade of the same type more than once.
                    if ((hasThis.type == lastTradedType[0]) || 
                        (hasThis.type == lastTradedType[1]))
                            
                    {
                        break;
                    }
                    TradeJDialog tradeBuy = new TradeJDialog(gameRules.gameWindow, true, this.gameRules.gameWindow.playerInfo, this.gameRules.tradeDialogLastPos);

                    tradeBuy.assignThisPlayer(p, TradeTypes.SELL, true);
                    tradeBuy.tradeResCardThis(tradeBuy.getTradingCardByType(hasThis.type), this, canTradeResCards);

                    if (tradeBuy.hasTradeItemsToTrade() != false)
                    {
                        tradeBuy.setVisible(true);
                        lastTradeIdx = (lastTradeIdx + 1) % 2;
                        lastTradedType[lastTradeIdx] = hasThis.type;
                        if (tradeBuy.tradeSuccess != false)
                        {
                            this.gameRules.gameWindow.updateWindowImmediately();
                            tradeSuccess = true;
                            wantResCards.clear();
                            canTradeResCards.clear();
                            COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, tradePriority);
                        }
                    }
                }
            }

            // Computer trading ...
            wantResCards.clear();
            canTradeResCards.clear();
            COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, tradePriority);

            if ((wantResCards.size() > 0) && (canTradeResCards.size() > 0))
            {
                for (Player p : this.gameRules.players)
                {
                    if (p == this)
                    {
                        continue;
                    }
                    if (p.type == PlayerTypes.HUMAN)
                    {
                        continue;
                    }
                    ResourceCard hasThis = null;
                    for (ResourceCard wrc : wantResCards)
                    {
                        for (ResourceCard rc : p.resCards)
                        {
                            if (rc.type == wrc.type)
                            {
                                hasThis = wrc;
                                break;
                            }
                        }

                        if (hasThis != null)
                        {
                            break;
                        }
                    }

                    // Start a trade (Other player sell) operation
                    if ((hasThis != null) && (canTradeResCards.size() > 0))
                    {
                        // Collect stats from other computer ...
                        if (p.COMP_Trade_With_COMP(this, hasThis, canTradeResCards) != false)
                        {
                            tradeSuccess = true;
                            wantResCards.clear();
                            canTradeResCards.clear();
                            COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, tradePriority);
                        }
                    }
                }
            }
        }

        return tradeSuccess;
    }

    public boolean COMP_Trade_With_COMP(Player traderPlayer,
            ResourceCard traderWantCard,
            LinkedList<ResourceCard> traderList)
    {
        if (traderWantCard == null)
        {
            return false;
        }
        if (traderList == null)
        {
            return false;
        }
        if (traderList.size() <= 0)
        {
            return false;
        // --- Trade goods wanted only if we get a better deal ---
        }
        ResCardTypes wantFor = COMP_CostBenefitTrade(traderWantCard, traderList);
        if ((wantFor != ResCardTypes.NULL) && (traderWantCard.type != wantFor))
        {
            Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);
            Debug("Player:COMP_Trade_With_COMP  COMP_CostBenefitTrade = " + wantFor.toString(), DebugLevel.COMPLETE);
            Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);

            // Trade  based on cost benefit analysis
            this.gameRules.setMsgLog(traderPlayer.name + " traded " + traderWantCard.type.toString() + " for " + wantFor.toString() + " from " + this.name);

            traderPlayer.resCards.add(new ResourceCard(traderWantCard.type));
            this.resCards.add(new ResourceCard(wantFor));

            traderPlayer.delResType(wantFor, 1);
            this.delResType(traderWantCard.type, 1);
            return true;
        }

        LinkedList<ResourceCard> wantResCards = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();

        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo cti = new COMPTurnInfo(this);
        CanBuildTypes next = cti.doNext(wantObjType);

        COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, 0);

        switch (this.compSkillLevel)
        {
            case MEDMEANTRADER:
                // Don;t trade meanly with winning comp players!
                if (traderPlayer.vicPntsPublic > this.vicPntsTotal)
                {
                    break;
                }
            case UBERMEANTRADER:
                {
                    this.Debug("Player.COMP_Trade_With_COMP : Trading meanly", DebugLevel.COMPLETE);
                    int have = this.countResType(this.resCards, traderWantCard.type);
                    int willTrade = this.countResType(canTradeResCards, traderWantCard.type);

                    if (willTrade <= 0 && have >= 0)
                    {
                        this.Debug("Player.COMP_Trade_With_COMP : have " + traderWantCard.type.toString() + " which " + traderPlayer.name + " wants!", DebugLevel.COMPLETE);
                        canTradeResCards.add(new ResourceCard(traderWantCard.type));
                    }
                }
        }

        if (wantResCards.size() <= 0)
        {
            return false;
        }
        if (canTradeResCards.size() <= 0)
        {
            return false;
        }
        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);
        Debug("Player:COMP_Trade_With_COMP " + name + " BUY WANT TO BUILD : " + wantObjType[0].name(), DebugLevel.COMPLETE);

        Debug("", DebugLevel.COMPLETE);
        for (ResourceCard rc : wantResCards)
        {
            Debug("Player:COMP_Trade_With_COMP " + name + " WANT : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);
        for (ResourceCard rc : canTradeResCards)
        {
            Debug("Player:COMP_Trade_With_COMP " + name + " CAN TRADE : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);
        Debug("Player:COMP_Trade_With_COMP " + traderPlayer.name + " they want " + traderWantCard.type.toString(), DebugLevel.COMPLETE);

        for (ResourceCard rc : traderList)
        {
            Debug("Player:COMP_Trade_With_COMP " + traderPlayer.name + " CAN TRADE : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);


        if (this.countResType(canTradeResCards, traderWantCard.type) <= 0)
        {
            return false;
        }
        for (ResourceCard wrc : wantResCards)
        {
            if (traderPlayer.countResType(traderList, wrc.type) > 0)
            {
                // Do not trade the same card between players! 
                // This causes an endless trading loop (stack overflow!)
                if (traderWantCard.type != wrc.type)
                {
                    this.gameRules.setMsgLog(traderPlayer.name + " traded " + traderWantCard.type.toString() + " for " + wrc.type.toString() + " from " + this.name);

                    traderPlayer.resCards.add(new ResourceCard(traderWantCard.type));
                    this.resCards.add(new ResourceCard(wrc.type));

                    traderPlayer.delResType(wrc.type, 1);
                    this.delResType(traderWantCard.type, 1);
                    return true;
                }
            }
        }

        return false;
    }

    public void COMP_Trade_Buy(Player thisTrader, ResourceCard wantThis, LinkedList<TraderItem> rtnList)
    {
        // don't trade with winning players!
        if (this.compSkillLevel != CompSkillLevel.EASIER)
        {
            if (thisTrader.type == PlayerTypes.HUMAN)
            {
                if ((thisTrader.vicPntsPublic >= 8) && (this.vicPntsTotal < thisTrader.vicPntsPublic))
                {
                    return;
                }
            }
        }

        LinkedList<ResourceCard> wantResCards     = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();

        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo cti = new COMPTurnInfo(this);
        CanBuildTypes next = cti.doNext(wantObjType);

        COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, 0);

        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);
        Debug("Player:COMP_Trade_Buy they want " + wantThis.type.toString(), DebugLevel.COMPLETE);
        Debug("Player:COMP_Trade_Buy " + name + " PORTTRADE WANT TO BUILD : " + wantObjType[0].name(), DebugLevel.COMPLETE);

        for (ResourceCard rc : resCards)
        {
            Debug("Player:COMP_Trade_Buy " + name + " BUY HAVE : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);

        for (ResourceCard rc : wantResCards)
        {
            Debug("Player:COMP_Trade_Buy " + name + " BUY WANT : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);

        for (ResourceCard rc : canTradeResCards)
        {
            Debug("Player:COMP_Trade_Buy " + name + " BUY DONT NEED : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);

        for (ResourceCard rc : canTradeResCards)
        {
            if (rc.type == wantThis.type)
            {
                for (ResourceCard trc : wantResCards)
                {
                    if (trc.type != wantThis.type)
                    {
                        // Don't add duplicates
                        boolean addCard = true;
                        for (TraderItem ti : rtnList)
                        {
                            if (ti.tradeItems.get(0).type == trc.type)
                            {
                                addCard = false;
                            }
                        }

                        // Create new trade object
                        if (addCard != false)
                        {
                            TraderItem item = new TraderItem();
                            item.owner = this;
                            item.tradeItems.add(trc);
                            rtnList.add(item);
                        }
                    }
                }
            }
        }

        // Remove resources from rtnlist that thisTrader does not own, so can't trade
        LinkedList<Object> delItems = new LinkedList<Object>();
        for (TraderItem ti : rtnList)
        {
            if (thisTrader.countResType(thisTrader.resCards, (ti.tradeItems.get(0)).type) <= 0)
            {
                delItems.add(ti);
            }
        }
        for (Object o : delItems)
        {
            rtnList.remove(o);
        }
    }

    public void COMP_Trade_Sell(Player thisTrader, ResourceCard tradingThis, LinkedList<TraderItem> rtnList)
    {
        // don't trade with winning players!
        if (this.compSkillLevel != CompSkillLevel.EASIER)
        {
            if (thisTrader.type == PlayerTypes.HUMAN)
            {
                if ((thisTrader.vicPntsPublic >= 7) && (this.vicPntsTotal < thisTrader.vicPntsPublic))
                {
                    return;
                }
            }
        }

        LinkedList<ResourceCard> wantResCards = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();

        CanBuildTypes wantObjType[] =
        {
            CanBuildTypes.NULL
        };
        COMPTurnInfo cti = new COMPTurnInfo(this);
        CanBuildTypes next = cti.doNext(wantObjType);

        COMP_needResFor(wantObjType[0], wantResCards, canTradeResCards, 0);

        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);
        Debug("Player:COMP_Trade_Sell " + name + " SELL WANT TO BUILD : " + wantObjType[0].name(), DebugLevel.COMPLETE);

        for (ResourceCard rc : resCards)
        {
            Debug("Player:COMP_Trade_Sell " + name + " SELL HAVE : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);

        for (ResourceCard rc : wantResCards)
        {
            Debug("Player:COMP_Trade_Sell " + name + " SELL WANT : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);

        for (ResourceCard rc : canTradeResCards)
        {
            Debug("Player:COMP_Trade_Sell " + name + " SELL DONT NEED : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);

        /*broken is tradingThis == wheat and canTradeResCards contains wheat
        // Make an offer of 2 items if only 1 card is needed on this player's turn
        if ((wantResCards.size()       == 1)    &&
        (this.gameRules.thisPlayer == this) &&
        (canTradeResCards.size() >= 2))
        {
        TraderItem item = new TraderItem();
        item.owner      = this;
        
        item.tradeItems.add(canTradeResCards.get(0));
        canTradeResCards.remove(0);
        item.tradeItems.add(canTradeResCards.get(0));
        canTradeResCards.remove(0);
        rtnList.add (item);
        }
         */
        // Add match whats left
        for (ResourceCard rc : wantResCards)
        {
            if (rc.type == tradingThis.type)
            {
                for (ResourceCard trc : canTradeResCards)
                {
                    if (trc.type != tradingThis.type)
                    {
                        // Don't add duplicates
                        boolean addCard = true;
                        for (TraderItem ti : rtnList)
                        {
                            if (ti.tradeItems.get(0).type == trc.type)
                            {
                                addCard = false;
                            }
                        }

                        // Create new trade object
                        if (addCard != false)
                        {
                            TraderItem item = new TraderItem();
                            item.owner = this;
                            item.tradeItems.add(trc);
                            rtnList.add(item);
                        }
                    }
                }
            }
        }
    }

    // return number of items traded.
    public int COMP_Trade_Port(COMPTurnInfo ti, CanBuildTypes wantObjType)
    {
        LinkedList<ResourceCard> wantResCards = new LinkedList<ResourceCard>();
        LinkedList<ResourceCard> canTradeResCards = new LinkedList<ResourceCard>();

        COMP_needResFor(wantObjType, wantResCards, canTradeResCards, 1);

        int sheep = this.countResType(canTradeResCards, ResCardTypes.SHEEP);
        int wheat  = this.countResType(canTradeResCards, ResCardTypes.WHEAT);
        int brick = this.countResType(canTradeResCards, ResCardTypes.BRICK);
        int wood  = this.countResType(canTradeResCards, ResCardTypes.WOOD);
        int rock  = this.countResType(canTradeResCards, ResCardTypes.ROCK);

        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);
        Debug("Player:COMP_Trade_Port " + name + " PORTTRADE WANT TO BUILD : " + wantObjType.name(), DebugLevel.COMPLETE);

        for (ResourceCard rc : resCards)
        {
            Debug("Player:COMP_Trade_Port " + name + " PORTTRADE HAVE : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);

        for (ResourceCard rc : wantResCards)
        {
            Debug("Player:COMP_Trade_Port " + name + " PORTTRADE WANT : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("", DebugLevel.COMPLETE);

        for (ResourceCard rc : canTradeResCards)
        {
            Debug("Player:COMP_Trade_Port " + name + " PORTTRADE DONT NEED : " + rc.type.toString(), DebugLevel.COMPLETE);
        }
        Debug("-----------------------------------------------------------------------", DebugLevel.COMPLETE);

        int tradedNum = 0;
        boolean restart;
        for (;;)
        {
            restart = false;
            for (ResourceCard rc : wantResCards)
            {
                if ((sheep >= ti.sheepPTradeNum) && (rc.type != ResCardTypes.SHEEP))
                {
                    this.delResType(ResCardTypes.SHEEP, ti.sheepPTradeNum);
                    sheep -= ti.sheepPTradeNum;
                    this.resCards.add(rc);
                    tradedNum++;
                    wantResCards.remove(rc);// do this to way to prevent list interator con-currency issues due to removing items while selecting them.

                    restart = true;
                    this.gameRules.gameWindow.repaint();
                    this.gameRules.setMsgLog(this.name + " port trade " + ti.sheepPTradeNum + " Sheep for " + rc.type.toString());
                    Debug("Player:COMP_Trade_Port " + this.name + " port trade " + ti.sheepPTradeNum + " Sheep for " + rc.type.toString(), DebugLevel.COMPLETE);
                }
                else if ((wheat >= ti.wheatPTradeNum) && (rc.type != ResCardTypes.WHEAT))
                {
                    this.delResType(ResCardTypes.WHEAT, ti.wheatPTradeNum);
                    wheat -= ti.wheatPTradeNum;
                    this.resCards.add(rc);
                    tradedNum++;
                    wantResCards.remove(rc);// do this to way to prevent list interator con-currency issues due to removing items while selecting them.

                    restart = true;
                    this.gameRules.gameWindow.repaint();
                    this.gameRules.setMsgLog(this.name + " port trade " + ti.wheatPTradeNum + " Wheat for " + rc.type.toString());
                    Debug("Player:COMP_Trade_Port " + this.name + " port trade " + ti.wheatPTradeNum + " Wheat for " + rc.type.toString(), DebugLevel.COMPLETE);
                }
                else if ((wood >= ti.woodPTradeNum) && (rc.type != ResCardTypes.WOOD))
                {
                    this.delResType(ResCardTypes.WOOD, ti.woodPTradeNum);
                    wood -= ti.woodPTradeNum;
                    this.resCards.add(rc);
                    tradedNum++;
                    wantResCards.remove(rc);// do this to way to prevent list interator con-currency issues due to removing items while selecting them.

                    restart = true;
                    this.gameRules.gameWindow.repaint();
                    this.gameRules.setMsgLog(this.name + " port trade " + ti.woodPTradeNum + " Wood for " + rc.type.toString());
                    Debug("Player:COMP_Trade_Port " + this.name + " port trade " + ti.woodPTradeNum + " Wood for " + rc.type.toString(), DebugLevel.COMPLETE);
                }
                else if ((brick >= ti.brickPTradeNum) && (rc.type != ResCardTypes.BRICK))
                {
                    this.delResType(ResCardTypes.BRICK, ti.brickPTradeNum);
                    brick -= ti.brickPTradeNum;
                    this.resCards.add(rc);
                    tradedNum++;
                    wantResCards.remove(rc);// do this to way to prevent list interator con-currency issues due to removing items while selecting them.

                    restart = true;
                    this.gameRules.gameWindow.repaint();
                    this.gameRules.setMsgLog(this.name + " port trade " + ti.brickPTradeNum + " Brick for " + rc.type.toString());
                    Debug("Player:COMP_Trade_Port " + this.name + " port trade " + ti.brickPTradeNum + " Brick for " + rc.type.toString(), DebugLevel.COMPLETE);
                }
                else if ((rock >= ti.rockPTradeNum) && (rc.type != ResCardTypes.ROCK))
                {
                    this.delResType(ResCardTypes.ROCK, ti.rockPTradeNum);
                    rock -= ti.rockPTradeNum;
                    this.resCards.add(rc);
                    tradedNum++;
                    wantResCards.remove(rc);// do this to way to prevent list interator con-currency issues due to removing items while selecting them.

                    restart = true;
                    this.gameRules.gameWindow.repaint();
                    this.gameRules.setMsgLog(this.name + " port trade " + ti.rockPTradeNum + " Rock for " + rc.type.toString());
                    Debug("Player:COMP_Trade_Port " + this.name + " port trade " + ti.rockPTradeNum + " Rock for " + rc.type.toString(), DebugLevel.COMPLETE);
                }
                if (restart != false)
                {
                    break;
                }
            }
            if (restart == false)
            {
                break;
            }
        }


        return tradedNum;
    }

    public boolean COMP_BuildNextRoadGroupJoin(COMPTurnInfo ti) throws CatanEndGameException
    {
        if ((ti.roadGrp1Points.size() <= 0) || (ti.roadGrp2Points.size() <= 0))
        {
            return false;
        }
        LinkedList<Road> bestRoadList = null;
        int searchDepths[] =
        {
            2, 3, 4
        };
        RoadInfo endRdPtsGrp1 = ti.roadGrp1Points.getFirst();
        RoadInfo endRdPtsGrp2 = ti.roadGrp2Points.getFirst();
        LinkedList<BuildPoint> destRoadBp = new LinkedList<BuildPoint>();

        if ((ti.roadGrp1Points.size() <= 0) || (ti.roadGrp2Points.size() <= 0))
        {
            return false;
        // create a list of detinations from the first group.
        }
        for (Road ri : endRdPtsGrp1.roadGroup)
        {
            for (BuildPoint bp : ri.buildJoins)
            {
                if (destRoadBp.contains(bp) == false)
                {
                    destRoadBp.add(bp);
                }
            }
        }

        for (int idx = 0; ((idx < searchDepths.length) && (bestRoadList == null)); idx++)
        {
            Debug("Player.COMP_BuildNextRoadGroupJoin : Scan Depth = " + searchDepths[idx], DebugLevel.MEDIUM);

            for (BuildPoint destBP : destRoadBp)
            {
                for (Road ri : endRdPtsGrp2.roadGroup)
                {
                    LinkedList<Road> roadList = new LinkedList<Road>();
                    BuildPoint fromBP = ri.getJoinedRoadBP(this);

                    if (fromBP == null)
                    {
                        continue;
                    }
                    for (Road r : fromBP.roadJoins)
                    {
                        if ((r.type != RoadTypes.BUILDABLE) && (r.owner == null))
                        {
                            continue;
                        }
                        if ((r.owner != null) && (r.owner != this))
                        {
                            continue;
                        }
                        BuildPoint toBP = r.getOtherBuildPoint(fromBP);

                        // increment max scan path iteratively to find the optimum path
                        for (int depth = 1; depth <= searchDepths[idx]; depth++)
                        {
                            roadList.clear();
                            roadList.add(r);
                            boolean foundDest = COMP_Calc_Distances(fromBP, toBP, destBP, roadList, depth);

                            if (foundDest != false)
                            {
                                if (bestRoadList == null)
                                {
                                    bestRoadList = new LinkedList<Road>();
                                    bestRoadList.addAll(roadList);
                                }
                                else if (roadList.size() < bestRoadList.size())
                                {
                                    bestRoadList = new LinkedList<Road>();
                                    bestRoadList.addAll(roadList);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (bestRoadList != null)
        {
            for (Road r : bestRoadList)
            {
                if (r.owner == this)
                {
                    continue;
                }
                else if (r.owner != null)
                {
                    continue;
                }
                Debug("Player.COMP_BuildNextRoadGroupJoin : Building road to join to other group!", DebugLevel.COMPLETE);
                this.buildObject(CanBuildTypes.ROAD, r);

                return true;
            }
            DebugErr("Player.COMP_BuildNextRoadGroupJoin : Failed to match road destination?");
        }

        return false;
    }

    public boolean COMP_BuildNextRoad(LinkedList<RoadInfo> endRoadPoints) throws CatanEndGameException
    {
        LinkedList<Road> bestRoadList = null;
        int searchDepths[] =
        {
            2, 3, 4, 5, 6, 7, 9, 13
        };

        for (int idx = 0; ((idx < searchDepths.length) && (bestRoadList == null)); idx++)
        {
            LinkedList<TileInfo> tileBuildPoints = COMP_BuildListOfValidBuildPoints(false, false);

            Debug("Player.COMP_BuildNextRoad : Scan Depth = " + searchDepths[idx], DebugLevel.MEDIUM);

            do
            {
                TileInfo nextBestBuildPoint = COMP_Calc_BestBuildPoint(tileBuildPoints);

                //nextBestBuildPoint.bp.debugHightlight (this.gameBoard.getGraphics());
                //this.pause(1000);
                for (RoadInfo ri : endRoadPoints)
                {
                    LinkedList<Road> roadList = new LinkedList<Road>();
                    BuildPoint fromBP = ri.road.getJoinedRoadBP(this);

                    if (fromBP == null)
                    {
                        continue;

                    //nextBestBuildPoint.bp.debugHightlight (this.gameBoard.getGraphics());
                    //fromBP.debugHightlight (this.gameBoard.getGraphics(), Color.cyan);
                    //ri.road.debugHightlight(this.gameBoard.getGraphics(), Color.pink);
                    //this.pause(500);
                    }
                    for (Road r : fromBP.roadJoins)
                    {
                        if ((r.type != RoadTypes.BUILDABLE) && (r.owner == null))
                        {
                            //r.debugHightlight(this.gameBoard.getGraphics(), Color.black);
                            //this.pause(1500);
                            continue;
                        }

                        if ((r.owner != null) && (r.owner != this))
                        {
                            //r.debugHightlight(this.gameBoard.getGraphics(), Color.darkGray);
                            //this.pause(1500);
                            continue;
                        }

                        //r.debugHightlight(this.gameBoard.getGraphics(), Color.GREEN);
                        //this.pause(100);

                        BuildPoint toBP = r.getOtherBuildPoint(fromBP);

                        // increment max scan path iteratively to find the optimum path
                        for (int depth = 1; depth <= searchDepths[idx]; depth++)
                        {
                            roadList.clear();
                            roadList.add(r);
                            boolean foundDest = COMP_Calc_Distances(fromBP, toBP, nextBestBuildPoint.bp, roadList, depth);

                            if (foundDest != false)
                            {
                                if (bestRoadList == null)
                                {
                                    bestRoadList = new LinkedList<Road>();
                                    bestRoadList.addAll(roadList);
                                }
                                else if (roadList.size() < bestRoadList.size())
                                {
                                    bestRoadList = new LinkedList<Road>();
                                    bestRoadList.addAll(roadList);
                                }
                                break;
                            }
                        }
                    }
                }

                if (bestRoadList == null)
                {
                    tileBuildPoints.remove(nextBestBuildPoint);
                }
                else
                {
                    // Check if road build is to a new destination, and not
                    // one already road built there.
                    boolean ignoreThisBuildPoint = true;
                    for (Road r : bestRoadList)
                    {
                        if (r.owner != this)
                        {
                            ignoreThisBuildPoint = false;
                        }
                    }
                    if (ignoreThisBuildPoint != false)
                    {
                        bestRoadList = null;
                        tileBuildPoints.remove(nextBestBuildPoint);
                    }
                }
            } while ((tileBuildPoints.size() > 0) && (bestRoadList == null));
        }

        if (bestRoadList != null)
        {
            /*** DEBUG **
            int u = 0;
            for (Road r:bestRoadList)
            {
            if (u == 0)
            r.debugHightlight(this.gameBoard.getGraphics(), Color.YELLOW);
            else
            r.debugHightlight(this.gameBoard.getGraphics());
            this.pause(500);
            u++;
            }
            this.pause(2000);
             ** DEBUG ***/
            for (Road r : bestRoadList)
            {
                if (r.owner == this)
                {
                    continue;
                }
                else if (r.owner != null)
                {
                    // ********* DEBUG **********
                    /*
                    DebugErr ("Player.COMP_InitPlaceSettlement : ERROR of some sort! Funny road!");
                    
                    for (Road rr:bestRoadList)
                    {
                    rr.debugHightlight(this.gameBoard.getGraphics());
                    this.pause(500);
                    }
                    r.debugHightlight(this.gameBoard.getGraphics(), Color.MAGENTA);
                    this.pause(3000);
                     */
                    // *************************
                    continue;
                }

                this.buildObject(CanBuildTypes.ROAD, r);
                return true;
            }
            DebugErr("Player.COMP_BuildNextRoad : Failed to match road destination?");
        }
        else
        {
            Debug("Player.COMP_BuildNextRoad : Really bad! Failed to pick a road destination?", DebugLevel.BRIEF);
        }
        return false;
    }

    public boolean COMP_Calc_Distances(BuildPoint fromBP, BuildPoint toBP, BuildPoint destBP, LinkedList<Road> roadList, int maxDepth)
    {
        if (toBP == destBP)
        {
            return true;
        }

        // We can't extend a road *through* another player's settlement/city: it
        // breaks the road, both for placement and for the longest-road bonus.
        // Test toBP - the vertex we would continue through - not fromBP: checking
        // the vertex we arrived from (as this did before) let the search route
        // straight through an opponent's building, so the AI planned and built
        // roads past a foreign settlement that can never earn the bonus. This
        // mirrors the same fix already made in roadLen().
        if ((toBP.owner != null) && (toBP.owner != this))
        {
            //toBP.debugHightlight(this.gameBoard.getGraphics(), Color.PINK);
            //this.pause(100);
            return false;
        }

        if (roadList.size() >= maxDepth)
        {
            return false;
        }
        LinkedList<Road> bestRoadPath = null;

        for (Road r : toBP.roadJoins)
        {
            if ((r.type != RoadTypes.BUILDABLE) && (r.owner == null))
            {
                continue;
            }
            if ((r.owner != null) && (r.owner != this))
            {
                continue;
            }
            if (roadList.contains(r) != false)
            {
                continue;

            // can we use this road ... is it owned by another player ?
            }
            if ((r.owner != null) && (r.owner != this))
            {
                continue;
            }
            BuildPoint nextBP = r.getOtherBuildPoint(toBP);
            roadList.add(r);

            // r.debugHightlight(this.gameBoard.getGraphics(), Color.yellow);

            // Follow down path ...
            if (COMP_Calc_Distances(toBP, nextBP, destBP, roadList, maxDepth) == false)
            {
                roadList.remove(r);
            }
            else
            {
                if (bestRoadPath == null)
                {
                    bestRoadPath = new LinkedList<Road>();
                    bestRoadPath.addAll(roadList);
                }
                else if (roadList.size() <= bestRoadPath.size())
                {
                    bestRoadPath = new LinkedList<Road>();
                    bestRoadPath.addAll(roadList);
                }
            }
        }

        if (bestRoadPath != null)
        {
            roadList.clear();
            roadList.addAll(bestRoadPath);
            return true;
        }

        return false;
    }

    public BuildPoint COMP_Calc_NextCity()
    {
        TileInfo ti = new TileInfo(null);
        ti.score = -1;

        for (BuildPoint bp : builtObjs)
        {
            if (bp.type == BuildPointTypes.SETTLEMENT)
            {
                int score = 0;
                for (Tile t : bp.tileJoins)
                {
                    if (this.vicPntsTotalNoArmyNoRoad < 5)
                    {
                        switch (t.type)
                        {
                            case WOOD:
                            case BRICK:
                            case VAR_VOLCANO:
                                score += 2;
                                break;

                            case WHEAT:
                            case SHEEP:
                                score++;
                        }
                        score += t.diceRollScore;
                    }
                    else if (this.vicPntsTotalNoArmyNoRoad > 5)
                    {
                        switch (t.type)
                        {
                            case ROCK:
                            case SHEEP:
                            case VAR_VOLCANO:
                                score += 2;
                        }
                        score += t.diceRollScore;
                    }
                }

                if (score > ti.score)
                {
                    ti.bp = bp;
                    ti.score = score;
                }
            }
        }

        return ti.bp;
    }

    // return 0: if not exist, or > 0 on dice roll score
    public int COMP_HaveBuiltBuildType(TileTypes type)
    {
        int diceScore = 0;
        for (BuildPoint bo : builtObjs)
        {
            for (Tile t : bo.tileJoins)
            {
                if (t.type == type)
                {
                    diceScore += t.diceRollScore;
                }
            }
        }

        return diceScore;
    }

    public LinkedList<TileInfo> COMP_BuildListOfValidBuildPoints(boolean checkForRoad, boolean initalRoadDestCheck)
    {
        // Build List of valid settlement points...
        LinkedList<TileInfo> validBuildPoints = new LinkedList<TileInfo>();

        for (BuildPoint bp : gameBoard.buildList)
        {
            if (bp.type == BuildPointTypes.BUILDABLE_LAND)
            {
                if (gameRules.isValidBuildPoint(bp, checkForRoad, this, initalRoadDestCheck) != false)
                {
                    // calculate scores.
                    TileInfo ti = new TileInfo(bp);

                    for (Tile t : bp.tileJoins)
                    {
                        ti.score += t.diceRollScore;
                    }
                    ti.uniqueScore = bp.uniquenessScore();
                    validBuildPoints.add(ti);
                }
            }
        }

        // Calc build points needed based upon priority and availiblity...
        int sheep = COMP_HaveBuiltBuildType(TileTypes.SHEEP);
        int wheat = COMP_HaveBuiltBuildType(TileTypes.WHEAT);
        int brick = COMP_HaveBuiltBuildType(TileTypes.BRICK);
        int wood = COMP_HaveBuiltBuildType(TileTypes.WOOD);
        int rock = COMP_HaveBuiltBuildType(TileTypes.ROCK);
        int gold = COMP_HaveBuiltBuildType(TileTypes.VAR_VOLCANO);

        for (TileInfo ti : validBuildPoints)
        {
            for (Tile t : ti.bp.tileJoins)
            {
                switch (t.type)
                {
                    case VAR_VOLCANO:
                        ti.score += 2 - gold;
                        break;

                    case SHEEP:
                    case WHEAT:
                    case BRICK:
                    case WOOD:
                        break;
                        
                    case ROCK:
                        ti.score += 2; // rock are rarer than other resources.
                        break;

                    case PORT_ANY_3TO1:
                        int count = 0;

                        if (sheep >= 5)
                        {
                            count++;
                        }
                        if (wheat >= 5)
                        {
                            count++;
                        }
                        if (brick >= 5)
                        {
                            count++;
                        }
                        if (wood >= 5)
                        {
                            count++;
                        }
                        if (rock >= 5)
                        {
                            count++;
                        }
                        if (count >= 3)
                        {
                            ti.score *= 2;
                        }
                        break;

                    case PORT_BRICK_2TO1:
                        if (brick >= 5)
                        {
                            ti.score *= 2;
                        }
                        break;

                    case PORT_ROCK_2TO1:
                        if (rock >= 5)
                        {
                            ti.score *= 2;
                        }
                        break;

                    case PORT_WHEAT_2TO1:
                        if (wheat >= 5)
                        {
                            ti.score *= 2;
                        }
                        break;

                    case PORT_WOOD_2TO1:
                        if (wood >= 5)
                        {
                            ti.score *= 2;
                        }
                        break;

                    case PORT_SHEEP_2TO1:
                        if (sheep >= 5)
                        {
                            ti.score *= 2;
                        }
                        break;
                }
                
                switch (compAIType)
                {
                    case HUERISTIC:
                        if ((ti.bp.bordersType(TileTypes.ROCK) != false) && (ti.bp.bordersType(TileTypes.WHEAT) != false))
                        {
                            ti.score ++;
                        }     
                        if ((ti.bp.bordersType(TileTypes.WOOD) != false) && (ti.bp.bordersType(TileTypes.BRICK) != false))
                        {
                            ti.score ++;
                        }                
                }                
            }
        }

        return validBuildPoints;
    }

    public TileInfo COMP_Calc_BestBuildPoint(LinkedList<TileInfo> tileInfoList)
    {
        TileInfo bestTI = null;

        for (TileInfo ti : tileInfoList)
        {
            if (bestTI == null)
            {
                bestTI = ti;
                continue;
            }
           
            if (ti.score  > bestTI.score) 
                bestTI = ti;                    
            else if (ti.score == bestTI.score)
            {
                if (ti.uniqueScore > bestTI.uniqueScore)
                    bestTI = ti;                    
            }
        }

        if (bestTI != null)
        {
            Debug("", DebugLevel.COMPLETE);
            Debug("COMP_Calc_BestBuildPoint: " + this.name + " AI Type = " + compAIType.toString() + " Picked Obj = " + bestTI + " sc:" + bestTI.score + " un:" + bestTI.uniqueScore, DebugLevel.COMPLETE);            
            Debug("", DebugLevel.COMPLETE);
        }

        return bestTI;
    }

    public void COMP_InitPlaceSettlement() throws CatanEndGameException
    {
        BuildPoint fromBP = COMP_PlaceSettlement(false);
        LinkedList<Road> bestRoadList = null;
        int searchDepths[] =
        {
            3, 6, 9
        };

        for (int idx = 0; ((idx < searchDepths.length) && (bestRoadList == null)); idx++)
        {
            LinkedList<TileInfo> tileBuildPoints = COMP_BuildListOfValidBuildPoints(false, true);

            Debug("Player.COMP_InitPlaceSettlement : Scan Depth = " + searchDepths[idx], DebugLevel.MEDIUM);

            do
            {
                TileInfo nextBestBuildPoint = COMP_Calc_BestBuildPoint(tileBuildPoints);
                LinkedList<Road> roadList = new LinkedList<Road>();

                for (Road r : fromBP.roadJoins)
                {
                    if ((r.type != RoadTypes.BUILDABLE) && (r.owner == null))
                    {
                        continue;
                    }
                    if ((r.owner != null) && (r.owner != this))
                    {
                        continue;
                    }
                    BuildPoint toBP = r.getOtherBuildPoint(fromBP);

                    // increment max scan path iteratively to find the optimum path
                    for (int depth = 1; depth <= searchDepths[idx]; depth++)
                    {
                        roadList.clear();
                        roadList.add(r);
                        boolean foundDest = COMP_Calc_Distances(fromBP, toBP, nextBestBuildPoint.bp, roadList, depth);

                        if (foundDest != false)
                        {
                            if (bestRoadList == null)
                            {
                                bestRoadList = new LinkedList<Road>();
                                bestRoadList.addAll(roadList);
                            }
                            else if (roadList.size() < bestRoadList.size())
                            {
                                bestRoadList = new LinkedList<Road>();
                                bestRoadList.addAll(roadList);
                            }
                            break;
                        }
                    }
                }

                if (bestRoadList == null)
                {
                    tileBuildPoints.remove(nextBestBuildPoint);
                }
            } while ((tileBuildPoints.size() > 0) && (bestRoadList == null));
        }

        if (bestRoadList != null)
        {
            /********** Debug ************
            for (Road r:bestRoadList)
            {
            r.debugHightlight(this.gameBoard.getGraphics());
            this.pause(500);
            }
            this.pause(1500);
             *********/
            for (Road r : bestRoadList)
            {
                if (r.owner == this)
                {
                    continue;
                }
                else if (r.owner != null)
                {
                    continue;
                }
                this.buildObject(CanBuildTypes.ROAD, r);
                return;
            }
            DebugErr("Player.COMP_InitPlaceSettlement : Failed to match road destination?");
        }
        else
        {
            DebugErr("Player.COMP_InitPlaceSettlement : Really bad! Failed to pick a road destination?");
        }
    }

    public void Debug(String s, DebugLevel dl)
    {
        try
        {
            this.gameRules.gameWindow.Debug(s, dl);
        }
        catch (Exception e)
        {
        }
    }

    public void DebugErr(String s)
    {
        try
        {
            this.gameRules.gameWindow.DebugErr(s);
        }
        catch (Exception e)
        {
        }
    }

    public BuildPoint COMP_PlaceSettlement(boolean checkForRoad) throws CatanEndGameException
    {
        LinkedList<TileInfo> tileBuildPoints = COMP_BuildListOfValidBuildPoints(checkForRoad, false);
        TileInfo buildPoint = COMP_Calc_BestBuildPoint(tileBuildPoints);

        buildObject(CanBuildTypes.SETTLEMENT, buildPoint.bp);
        return buildPoint.bp;
    }

    public void pause(int milliSeconds)
    {
        try
        {
            Thread.sleep(milliSeconds);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
