package games.saboteur;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Component;
import core.components.Deck;
import core.components.PartialObservableDeck;
import games.GameType;
import games.saboteur.components.*;
import utilities.Vector2D;

import java.util.*;

public class SaboteurGameState extends AbstractGameState
{
    public List<Deck<SaboteurCard>> playerDecks;
    public List<Deck<SaboteurCard>> brokenToolDecks;
    public Deck<SaboteurCard> drawDeck;
    PartialObservableDeck<SaboteurCard> discardDeck;
    Deck<SaboteurCard> goalDeck;
    PartialObservableDeck<SaboteurCard> roleDeck; // add list for roles as well due to visibility when copying
    public PartialObservableGridBoard<PathCard> gridBoard;
    public List<PartialObservableDeck<SaboteurCard>> playerNuggetDecks;
    public Deck<SaboteurCard> nuggetDeck;
    public List<Vector2D> pathCardOptions;
    int centerOfGrid;
    int[] playerScore;

    public int nOfMiners;
    public int nOfSaboteurs;


    public SaboteurGameState(AbstractParameters parameters, int nPlayers)
    {
        super(parameters, nPlayers);
        //to finish
    }

    @Override
    protected GameType _getGameType() {
        return GameType.Saboteur;
    }

    @Override
    protected List<Component> _getAllComponents()
    {
        return new ArrayList<Component>()
        {{
            addAll(playerDecks);
            addAll(brokenToolDecks);
            add(drawDeck);
            add(discardDeck);
            add(goalDeck);
            add(roleDeck);
        }};
    }

    @Override
    protected AbstractGameState _copy(int playerId)
    {
        SaboteurGameState copy = new SaboteurGameState(gameParameters.copy(), getNPlayers());

        //copying playerDecks
        for(Deck<SaboteurCard> playerDeck : playerDecks)
        {
            copy.playerDecks.add(playerDeck.copy());
        }

        //copying brokenToolsDeck
        for(Deck<SaboteurCard> currentDeck : brokenToolDecks)
        {
            copy.brokenToolDecks.add(currentDeck.copy());
        }

        copy.drawDeck = drawDeck.copy();
        copy.discardDeck = discardDeck.copy();
        copy.goalDeck = goalDeck.copy();
        copy.roleDeck = roleDeck.copy();
        return copy;
    }

    @Override
    protected double _getHeuristicScore(int playerId) {
        return 0;
    }

    @Override
    public double getGameScore(int playerId) {
        return 0;
    }

    @Override
    protected boolean _equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof SaboteurGameState)) return false;
        if (!super.equals(o)) return false;
        SaboteurGameState that = (SaboteurGameState) o;
        return
                Objects.equals(playerDecks, that.playerDecks) &&
                Objects.equals(brokenToolDecks, that.brokenToolDecks) &&
                Objects.equals(drawDeck,that.drawDeck) &&
                Objects.equals(discardDeck,that.discardDeck) &&
                Objects.equals(goalDeck,that.goalDeck) &&
                Objects.equals(roleDeck,that.roleDeck) &&
                Objects.equals(gridBoard,that.gridBoard);
    }
}
