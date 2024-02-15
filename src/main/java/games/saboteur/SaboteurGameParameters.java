package games.saboteur;

import core.AbstractParameters;
import evaluation.optimisation.TunableParameters;
import games.saboteur.components.ActionCard;
import games.saboteur.components.PathCard;
import games.saboteur.components.RoleCard;
import games.saboteur.components.SaboteurCard;
import utilities.Vector2D;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SaboteurGameParameters extends TunableParameters
{
    public int nPlayers          = 5;
    public int nNuggets          = 27;
    public int nGoalCards        = 3;
    public int nRounds           = 3;
    public int GridSize          = 37;
    public int GoalSpacingX      = 8;
    public int GoalSpacingY      = 1;

    //map combination of specific cards to number of cards in that deck
    public Map<SaboteurCard, Integer> pathCardDeck= new HashMap<>();
    public Map<SaboteurCard, Integer> roleCardDeck = new HashMap<>();
    public Map<SaboteurCard, Integer> actionCardDeck = new HashMap<>();
    public Map<SaboteurCard, Integer> goalCardDeck = new HashMap<>();
    public Map<SaboteurCard, Integer> goldNuggetDeck = new HashMap<>();

    public SaboteurGameParameters (long seed)
    {
        super(seed);
        addTunableParameter("nMiners ", 5,Arrays.asList(3,4,5,6,7));
        addTunableParameter("nSaboteurs", 2,Arrays.asList(1,2,3,4));
        addTunableParameter("nNuggets", 27,Arrays.asList(18,27,36,45));
        addTunableParameter("nPathCards", 44,Arrays.asList(44,88,132,176));
        addTunableParameter("nGoalCards", 3,Arrays.asList(3,6,9,12));
        addTunableParameter("nBrokenToolCards", 9,Arrays.asList(9,18,27,36));
        addTunableParameter("nRepairToolCards", 9,Arrays.asList(9,18,27,36));
        addTunableParameter("nRockFallCards", 1,Arrays.asList(1,2,3,4));
        addTunableParameter("nMapCards", 1,Arrays.asList(1,2,3,4));
        addTunableParameter("nRounds", 3,Arrays.asList(3,6,9,12));
        addTunableParameter("GridSize", 37,Arrays.asList(37,53,69,85));
        addTunableParameter("GoalSpacingX", 7,Arrays.asList(7,14,21,28));
        addTunableParameter("GoalSpacingY", 1,Arrays.asList(1,2,3,4));

        //All Path type cards in a deck excluding goal and start card
        PathCard.PathCardType edge = PathCard.PathCardType.Edge;
        PathCard.PathCardType path = PathCard.PathCardType.Path;
        Vector2D up = new Vector2D(0,1);
        Vector2D down = new Vector2D(0,-1);
        Vector2D left = new Vector2D(1,0);
        Vector2D right = new Vector2D(-1,0);

        pathCardDeck.put(new PathCard(edge, new boolean[]{false, true , false, false}), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{false, false, true , false}), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{true , true , false, false}), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{false, false, true , true }), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{false, true , false, true }), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{false, true , true , false}), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{true , true , false, true }), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{true , false, true , true }), 1);
        pathCardDeck.put(new PathCard(edge, new boolean[]{true , true , true , true }), 1);

        pathCardDeck.put(new PathCard(path, new boolean[]{true , true , false, false}), 4);
        pathCardDeck.put(new PathCard(path, new boolean[]{false, false, true , true }), 3);
        pathCardDeck.put(new PathCard(path, new boolean[]{false, true , false, true }), 4);
        pathCardDeck.put(new PathCard(path, new boolean[]{false, true , true , false}), 5);
        pathCardDeck.put(new PathCard(path, new boolean[]{true , true , false, true }), 5);
        pathCardDeck.put(new PathCard(path, new boolean[]{true , false, true , true }), 5);
        pathCardDeck.put(new PathCard(path, new boolean[]{true , true , true , true }), 5);

        //All goal cards
        goalCardDeck.put(new PathCard(PathCard.PathCardType.Goal, new boolean[]{true, true, true, true}), 5);

        //All RolesCards in a deck depending on number of players
        //nPlayers, nMiners, nSaboteurs
        //4	    3	1
        //5	    4	1
        //6	    4	2
        //7	    5	2
        //8	    5	3
        //9	    6	3
        //10	7	3
        //11    7	4
        int nMiners;
        int nSaboteurs;
        switch(nPlayers)
        {
            case 4:
                nMiners = 3;
                break;
            case 5:
            case 6:
                nMiners = 4;
                break;
            case 7:
            case 8:
                nMiners = 5;
                break;
            case 9:
                nMiners = 6;
                break;
            case 10:
            case 11:
                nMiners = 7;
                break;
            default:
                nMiners = 2;
        }

        nSaboteurs = nPlayers - nMiners;

        roleCardDeck.put(new RoleCard(RoleCard.RoleCardType.GoldMiner), nMiners);
        roleCardDeck.put(new RoleCard(RoleCard.RoleCardType.GoldMiner), nSaboteurs);

        //All Actions Cards
        ActionCard.ToolCardType mineCart = ActionCard.ToolCardType.MineCart;
        ActionCard.ToolCardType lantern = ActionCard.ToolCardType.Lantern;
        ActionCard.ToolCardType pickaxe = ActionCard.ToolCardType.Pickaxe;

        ActionCard.ActionTypeCard brokenTools = ActionCard.ActionTypeCard.BrokenTools;
        ActionCard.ActionTypeCard fixTools = ActionCard.ActionTypeCard.FixTools;
        ActionCard.ActionTypeCard map = ActionCard.ActionTypeCard.Map;
        ActionCard.ActionTypeCard rockFall = ActionCard.ActionTypeCard.RockFall;

        actionCardDeck.put(new ActionCard(brokenTools, mineCart), 3);
        actionCardDeck.put(new ActionCard(brokenTools, lantern), 3);
        actionCardDeck.put(new ActionCard(brokenTools, pickaxe), 3);

        actionCardDeck.put(new ActionCard(fixTools, mineCart), 2);
        actionCardDeck.put(new ActionCard(fixTools, lantern), 2);
        actionCardDeck.put(new ActionCard(fixTools, pickaxe), 2);
        actionCardDeck.put(new ActionCard(fixTools, new ActionCard.ToolCardType[]{mineCart,lantern}), 1);
        actionCardDeck.put(new ActionCard(fixTools, new ActionCard.ToolCardType[]{lantern, pickaxe}), 1);
        actionCardDeck.put(new ActionCard(fixTools, new ActionCard.ToolCardType[]{pickaxe, mineCart}), 1);

        actionCardDeck.put(new ActionCard(map), 1);
        actionCardDeck.put(new ActionCard(rockFall), 1);
    }

    @Override
    protected AbstractParameters _copy()
    {
        SaboteurGameParameters sgp = new SaboteurGameParameters(System.currentTimeMillis());
        sgp.nPlayers = nPlayers;
        sgp.nNuggets = nNuggets;
        sgp.nGoalCards = nGoalCards;
        sgp.nRounds = nRounds;
        sgp.GridSize = GridSize;
        sgp.GoalSpacingY = GoalSpacingY;
        sgp.GoalSpacingX = GoalSpacingX;
        return sgp;
    }

    @Override
    protected boolean _equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof SaboteurGameParameters)) return false;
        if (!super.equals(o)) return false;
        SaboteurGameParameters that = (SaboteurGameParameters) o;
        return nPlayers == that.nPlayers &&
                nNuggets == that.nNuggets &&
                nGoalCards == that.nGoalCards &&
                nRounds == that.nRounds &&
                GridSize == that.GridSize &&
                GoalSpacingX == that.GoalSpacingX &&
                GoalSpacingY == that.GoalSpacingY;
    }

    @Override
    public Object instantiate()
    {
        //to be completed later
        return null;
    }

    @Override
    public void _reset()
    {
        nPlayers = (int) getParameterValue("nPlayers");
        nNuggets = (int) getParameterValue("nNuggets");
        nGoalCards = (int) getParameterValue("nGoalCards");
        nRounds = (int) getParameterValue("nRounds");
        GridSize = (int) getParameterValue("GridSize");
        GoalSpacingX = (int) getParameterValue("GoalSpacingX");
        GoalSpacingY = (int) getParameterValue("GoalSpacingY");
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                nPlayers,
                nNuggets,
                nGoalCards,
                nRounds,
                GridSize,
                GoalSpacingX,
                GoalSpacingY);
    }
}
