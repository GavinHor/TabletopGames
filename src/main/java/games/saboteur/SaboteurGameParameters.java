package games.saboteur;

import core.AbstractParameters;
import evaluation.optimisation.TunableParameters;

import java.util.Arrays;
import java.util.Objects;

public class SaboteurGameParameters extends TunableParameters
{

    public int nMiners           = 5;
    public int nSaboteurs        = 2;
    public int nNuggets          = 27; //nOfNuggetCards not total sum of nuggets
    public int nPathCards        = 44;
    public int nGoalCards        = 3;
    public int nBrokenToolCards  = 9;
    public int nRepairCards      = 9;
    public int nRockFallCards    = 1;
    public int nMapCards         = 1;
    public int nRounds           = 3;
    public int GridSize          = 37;
    //map combination specific cards to number of cards in that deck
    //map for nminers and nsaboteurs for number of players
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
        _reset();
    }

    @Override
    protected AbstractParameters _copy()
    {
        SaboteurGameParameters sgp = new SaboteurGameParameters(System.currentTimeMillis());
        sgp.nMiners = nMiners;
        sgp.nSaboteurs = nSaboteurs;
        sgp.nNuggets = nNuggets;
        sgp.nPathCards = nPathCards;
        sgp.nGoalCards = nGoalCards;
        sgp.nBrokenToolCards = nBrokenToolCards;
        sgp.nRepairCards = nRepairCards;
        sgp.nRockFallCards = nRockFallCards;
        sgp.nMapCards = nMapCards;
        sgp.nRounds = nRounds;
        return sgp;
    }

    @Override
    protected boolean _equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof SaboteurGameParameters)) return false;
        if (!super.equals(0)) return false;
        SaboteurGameParameters that = (SaboteurGameParameters) o;
        return nMiners == that.nMiners &&
                nSaboteurs == that.nSaboteurs &&
                nNuggets == that.nNuggets &&
                nPathCards == that.nPathCards &&
                nGoalCards == that.nGoalCards &&
                nBrokenToolCards == that.nBrokenToolCards &&
                nRepairCards == that.nRepairCards &&
                nRockFallCards == that.nRockFallCards &&
                nMapCards == that.nMapCards &&
                nRounds == that.nRounds &&
                GridSize == that.GridSize;
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
        nMiners = (int) getParameterValue("nMiners");
        nSaboteurs = (int) getParameterValue("nSaboteurs");
        nNuggets = (int) getParameterValue("nNuggets");
        nPathCards = (int) getParameterValue("nPathCards");
        nGoalCards = (int) getParameterValue("nGoalCards");
        nBrokenToolCards = (int) getParameterValue("nBrokenToo");
        nRepairCards = (int) getParameterValue("nRepairCar");
        nRockFallCards = (int) getParameterValue("nRockFallC");
        nMapCards = (int) getParameterValue("nMapCards");
        nRounds = (int) getParameterValue("nRounds");
        GridSize = (int) getParameterValue("GridSize");
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                nMiners,
                nSaboteurs,
                nNuggets,
                nPathCards,
                nGoalCards,
                nBrokenToolCards,
                nRepairCards,
                nRockFallCards,
                nMapCards,
                nRounds,
                GridSize);
    }
}
