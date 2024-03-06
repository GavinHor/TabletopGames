package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Deck;
import games.saboteur.SaboteurGameState;
import games.saboteur.components.ActionCard;
import games.saboteur.components.SaboteurCard;

public class PlayBrokenToolCard extends AbstractAction {

    private final int playerID;
    private int fromID;
    private final ActionCard brokenToolCard;

    public PlayBrokenToolCard(ActionCard brokenToolCard, int playerID)
    {
        this.brokenToolCard = brokenToolCard;
        this.playerID = playerID;
        this.fromID = -1;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        SaboteurGameState sgs = (SaboteurGameState) gs;
        Deck<SaboteurCard> currentPlayerDeck = sgs.playerDecks.get(sgs.getCurrentPlayer());
        currentPlayerDeck.remove(brokenToolCard);
        sgs.brokenToolDecks.get(playerID).add(brokenToolCard);

        Deck<SaboteurCard> currentDeck = sgs.playerDecks.get(sgs.getCurrentPlayer());
        currentDeck.add(sgs.drawDeck.draw()); //may need to talk about this as well

        this.fromID = sgs.getCurrentPlayer();
        return true;
    }

    @Override
    public AbstractAction copy() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return "Broken Tool" + brokenToolCard.toolTypes[0] + fromID + " to " + playerID;
    }
}
