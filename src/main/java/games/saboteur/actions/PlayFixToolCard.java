package games.saboteur.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Deck;
import games.saboteur.SaboteurGameParameters;
import games.saboteur.SaboteurGameState;
import games.saboteur.components.ActionCard;
import games.saboteur.components.SaboteurCard;

import java.util.List;

public class PlayFixToolCard extends AbstractAction {

    private int playerID;
    private int currentPlayerID;
    private ActionCard fixToolCard;
    private ActionCard.ToolCardType ToolType;
    public PlayFixToolCard(int playerID, int currentPlayerID, ActionCard fixToolCard, ActionCard.ToolCardType ToolType) {
        this.playerID = playerID;
        this.currentPlayerID = currentPlayerID;
        this.fixToolCard = fixToolCard;
        this.ToolType = ToolType;
    }
    @Override
    public boolean execute(AbstractGameState gs) {
        SaboteurGameState sgs = (SaboteurGameState) gs;
        Deck<SaboteurCard> otherBrokenToolDeck = sgs.brokenToolDecks.get(playerID);
        Deck<SaboteurCard> currentPlayerDeck = sgs.playerDecks.get(currentPlayerID);

        currentPlayerDeck.getComponents().remove(fixToolCard);

        for(SaboteurCard card : otherBrokenToolDeck.getComponents()){
            ActionCard currentCard= (ActionCard) card;
            assert currentCard.toolTypes != null; // if we somehow check a card that is not a broken tool card

            if(currentCard.toolTypes[0] == ToolType){
                otherBrokenToolDeck.getComponents().remove(card);
                break;
            }
        }
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
        return null;
    }
}
