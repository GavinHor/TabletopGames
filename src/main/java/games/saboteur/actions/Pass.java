package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.actions.DoNothing;
import core.components.Deck;
import games.saboteur.SaboteurGameState;
import games.saboteur.components.SaboteurCard;

public class Pass extends AbstractAction
{
    private final SaboteurCard card;
    private int fromID;
    public Pass(SaboteurCard card) {
        this.card = card;
        this.fromID = -1;
    }
    @Override
    public boolean execute(AbstractGameState gs) {
        SaboteurGameState sgs = (SaboteurGameState) gs;
        Deck<SaboteurCard> currentDeck = sgs.playerDecks.get(sgs.getCurrentPlayer());
        currentDeck.remove(card);
        currentDeck.add(sgs.drawDeck.draw());

        this.fromID = sgs.getCurrentPlayer();
        return true;
    }

    @Override
    public AbstractAction copy() {
        return new DoNothing();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof DoNothing;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }

    @Override
    public String toString() {
        return "Player " + fromID + " passed.";
    }
}
