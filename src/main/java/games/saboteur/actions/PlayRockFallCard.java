package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.actions.SetGridValueAction;
import core.components.Deck;
import games.saboteur.SaboteurGameState;
import games.saboteur.components.SaboteurCard;


public class PlayRockFallCard extends SetGridValueAction
{
    public PlayRockFallCard(int gridBoard, int x, int y) {
        super(gridBoard, x, y, null);
    }


    public boolean execute(AbstractGameState gs) {
        super.execute(gs);
        SaboteurGameState sgs = (SaboteurGameState) gs;
        Deck<SaboteurCard> currentDeck = sgs.playerDecks.get(sgs.getCurrentPlayer());
        currentDeck.add(sgs.drawDeck.draw()); //may need to talk about this as well
        return false;
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
        return "RockFall at (" + super.getX() + ", " + super.getY() + ")";
    }
}

