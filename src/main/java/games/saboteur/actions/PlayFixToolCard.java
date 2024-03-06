package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Deck;
import games.saboteur.SaboteurGameState;
import games.saboteur.components.ActionCard;
import games.saboteur.components.SaboteurCard;


public class PlayFixToolCard extends AbstractAction {

    private final int currentPlayerID;
    private int fromID;
    private final ActionCard fixToolCard;
    private final ActionCard.ToolCardType toolType;
    public PlayFixToolCard(int currentPlayerID, ActionCard fixToolCard, ActionCard.ToolCardType ToolType) {
        this.currentPlayerID = currentPlayerID;
        this.fixToolCard = fixToolCard;
        this.toolType = ToolType;
        this.fromID = -1;
    }
    @Override
    public boolean execute(AbstractGameState gs) {
        SaboteurGameState sgs = (SaboteurGameState) gs;
        Deck<SaboteurCard> otherBrokenToolDeck = sgs.brokenToolDecks.get(sgs.getCurrentPlayer());
        Deck<SaboteurCard> currentPlayerDeck = sgs.playerDecks.get(currentPlayerID);

        currentPlayerDeck.getComponents().remove(fixToolCard);

        for(SaboteurCard card : otherBrokenToolDeck.getComponents()){
            ActionCard currentCard= (ActionCard) card;
            assert currentCard.toolTypes != null; // if we somehow check a card that is not a broken tool card

            if(currentCard.toolTypes[0] == toolType){
                otherBrokenToolDeck.getComponents().remove(card);
                break;
            }
        }

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
    public String getString(AbstractGameState gameState)
    {
        return "Fixed Tool" + toolType + " " + fromID + " to " + currentPlayerID;
    }
}
