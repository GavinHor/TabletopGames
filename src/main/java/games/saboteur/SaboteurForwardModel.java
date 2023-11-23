package games.saboteur;

import core.AbstractGameState;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Deck;
import games.saboteur.components.ActionCard;
import games.saboteur.components.PathCard;
import games.saboteur.components.SaboteurCard;
import utilities.Vector2D;

import static core.CoreConstants.VisibilityMode.*;
import java.util.*;

public class SaboteurForwardModel extends StandardForwardModel {
//--------------------------------------------------------------------------------------------------//
//region Setup Functions
    @Override
    protected void _setup(AbstractGameState firstState)
    {
        SaboteurGameState sgs = (SaboteurGameState) firstState;
        SaboteurGameParameters sgp = (SaboteurGameParameters) sgs.getGameParameters();

        sgs.playerScore = new int[firstState.getCurrentPlayer()];
        sgs.playerDecks = new ArrayList<>();
        for (int i = 0; i < sgs.getNPlayers(); i++)
        {
            sgs.playerDecks.add(new Deck<>("Player" + i + "deck", i, VISIBLE_TO_OWNER));
        }

        //Initialise Decks
        sgs.drawDeck = new Deck<>("DrawDeck", HIDDEN_TO_ALL);
        sgs.discardDeck = new Deck<>("DiscardDeck", VISIBLE_TO_ALL);
        sgs.goalDeck = new Deck<>("GoalDeck", HIDDEN_TO_ALL);
        sgs.roleDeck = new Deck<>("RoleDeck", HIDDEN_TO_ALL); //change to partial observable deck

        //fill in decks
        FillDeckViaMap(sgs.drawDeck, sgp.pathCardDeck);
        FillDeckViaMap(sgs.drawDeck, sgp.actionCardDeck);
        FillDeckViaMap(sgs.roleDeck, sgp.roleCardDeck);

        //Initialise GridBoard with starting card
        sgs.centerOfGrid = (int) Math.floor(sgp.GridSize / 2.0);
        sgs.gridBoard.setElement(sgs.centerOfGrid, sgs.centerOfGrid, new PathCard(PathCard.PathCardType.Start, new boolean[]{true, true, true, true}));

        //set GoalCards on gridBoard
        ResetGoals(sgs,sgp);
    }

    private void SetupRound(SaboteurGameState sgs, SaboteurGameParameters sgp)
    {
        Random r = new Random(sgs.getGameParameters().getRandomSeed() + sgs.getRoundCounter());

        //Clear player deck and readd them to drawDeck
        for (int i = 0; i < sgs.getNPlayers(); i++)
        {
            //Adding back all brokenToolCards from players deck to Draw Deck
            sgs.drawDeck.add(sgs.brokenToolDecks.get(i));
            sgs.brokenToolDecks.get(i).clear();

            //Adding back all cards from players deck to Draw Deck
            sgs.drawDeck.add(sgs.playerDecks.get(i));
            sgs.playerDecks.get(i).clear();

            //Adding back allCards from discard deck to Draw Deck
            sgs.drawDeck.add(sgs.discardDeck);
            sgs.discardDeck.clear();
        }

        //Shuffle Necessary Decks
        sgs.drawDeck.shuffle(r);
        sgs.goalDeck.shuffle(r);
        sgs.roleDeck.shuffle(r);//need to reassign visibility for players

        ResetBoard(sgs);
        ResetGoals(sgs,sgp);
    }
//endregion
//--------------------------------------------------------------------------------------------------//
//region Reset Functions
private void ResetBoard(SaboteurGameState sgs) {
    //Adding back cards from grid to Draw Deck
    //excluding goal and start card
    for (int i = 0; i < sgs.gridBoard.getWidth(); i++) {
        for (int j = 0; j < sgs.gridBoard.getHeight(); j++) {

            PathCard cardOnGrid = sgs.gridBoard.getElement(i, j);
            if (cardOnGrid == null) {
                continue;
            }

            switch (cardOnGrid.type)
            {
                case Start:
                case Goal:
                    break;

                default:
                    sgs.drawDeck.add(cardOnGrid);
                    sgs.gridBoard.setElement(i, j, null);//reset visibility as well
                    break;
            }
        }
    }
}

    private void ResetGoals(SaboteurGameState sgs, SaboteurGameParameters sgp)
    {
        int totalLength = sgs.goalDeck.getSize() * (sgp.GoalSpacingY + 1);
        int startingY = (int) Math.floor(totalLength / 2.0) - 1;

        assert sgp.GoalSpacingX > Math.floor(sgs.gridBoard.getWidth() / 2.0): "Placing Goal card out of bounds for X";

        for(PathCard goalCard: sgs.goalDeck.getComponents())
        {
            assert startingY > sgs.gridBoard.getHeight(): "Placing Goal card out of bounds for Y";

            sgs.gridBoard.setElement(sgp.GoalSpacingX + 1, startingY,goalCard);
            startingY -= (sgp.GoalSpacingY + 1);
        }
    }
//endregion
//--------------------------------------------------------------------------------------------------//
//region Compute Action Functions
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState)
    {
        //Initialise ArrayList of Abstract Actions
        ArrayList<AbstractAction> actions = new ArrayList<>();
        SaboteurGameState sgs = (SaboteurGameState) gameState;

        int player = sgs.getCurrentPlayer();
        Deck<SaboteurCard> currentPlayersDeck = sgs.playerDecks.get(player);

        boolean playerHasBrokenTool = sgs.brokenToolDecks.get(player).getSize() > 0;

        //Check Each card in players deck
        //Switch Case for each type of card you would find in hand
        for(SaboteurCard card: currentPlayersDeck.getComponents())
        {
            switch(card.type)
            {
                case Path:
                    if(playerHasBrokenTool)
                    {
                        continue;
                    }
                    else
                    {
                        //TODO
                    }
                    break;

                case Action:
                    actions.addAll(ComputeActionAction((ActionCard) card ,sgs));
                    break;
            }
        }
        return actions;
    }

    private Map<Vector2D, boolean[]> ComputeAllPathActions (int player, SaboteurGameState sgs, Vector2D currentCell)
    {
        Map<Vector2D, boolean[]> allPlaceableCells = new HashMap<>();
        //breadth or depth search
        //initialise Vector2d Array of 4 with coordinates for adjacent cells in the grid
        //for each Vector2d in the array
            //if coordinates are outside of grid
                //continue
            //if cell is null
                /*
                new key pair value in map wa
                need to know which where to place
                need to know what path card is playable
                */
            //if card adjacent is edge
                //continue
            //if card adjacent is path
                //if path connects to start card
                    //recursive call current function via actions.addAll
        return allPlaceableCells;
    }

    private ArrayList<AbstractAction> ComputeActionAction (ActionCard card, SaboteurGameState sgs)
    {
        ArrayList<AbstractAction> actions = new ArrayList<>();
        switch(card.actionType)
        {
            case BrokenTools:
                //for everyone's BrokenToolDeck
                //If that player doesn't have a BrokenTool Matching it
                    //new action to add card onto their BrokenToolDeck
            case FixTools:
                //for everyone's BrokenToolDeck
                //If that player does have a BrokenTool Matching it
                    //new action to remove players
            case Map:
                //new action to check either 1 of the 3 goals and make it visible to the player
                //need to make grid board have visibility of some kind
            case RockFall:
                //for entire grid
                //if cell is null
                    //continue
                //if cell has PathCard and is PathCardType "path" or "edge"
                    //new action SetGridValueAction coordinates and then move card to discard deck
                    //may need new action class for this
        }
        return actions;
    }
//endregion
//--------------------------------------------------------------------------------------------------//
//region OtherFunctions
    private void FillDeckViaMap(Deck<? extends SaboteurCard> deck, Map<SaboteurCard, Integer> map)
    {
        //add all the Path Cards
        for (Map.Entry<SaboteurCard, Integer> entry : map.entrySet())
        {
            for (int i = 0; i < entry.getValue(); i++)
            {
                deck.add(entry.getKey());
            }
        }
    }
//endregion
//--------------------------------------------------------------------------------------------------//
}

//partial observable grid eztends gridBoard
//booleanarray list (size = nOfPlayers)
//default constructor visible to all
    //set true for all values in boolean array