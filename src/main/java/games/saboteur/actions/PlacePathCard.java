package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.actions.SetGridValueAction;
import core.components.Deck;
import games.saboteur.SaboteurGameState;
import games.saboteur.components.PathCard;
import games.saboteur.components.SaboteurCard;
import utilities.Vector2D;

public class PlacePathCard extends SetGridValueAction
{
    boolean rotated;
    PathCard pathCard;
    int x;
    int y;
    public PlacePathCard(int gridBoard, int x, int y, PathCard pathCard, boolean rotated) {
        super(gridBoard, x, y, pathCard);
        this.rotated = rotated;
        this.pathCard = pathCard;
    }

    @Override
    public boolean execute(AbstractGameState gs) {
        SaboteurGameState sgs = (SaboteurGameState) gs;
        if(rotated)
        {
            pathCard.Rotate();
        }
        sgs.gridBoard.setElement(x, y, pathCard);
        sgs.pathCardOptions.remove(new Vector2D(x, y));

        Deck<SaboteurCard> currentDeck = sgs.playerDecks.get(sgs.getCurrentPlayer());
        currentDeck.add(sgs.drawDeck.draw()); //may need to talk about this as well
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
        return "Placed Path Card at (" + x + ", " + y + ")" + (rotated ? " rotated" : "");
    }
}
