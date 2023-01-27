package games.sirius;


import core.components.Component;
import core.components.PartialObservableDeck;
import games.sirius.SiriusConstants.MoonType;
import utilities.Utils;

import java.util.Objects;

public class Moon extends Component {

    PartialObservableDeck<SiriusCard> deck;
    MoonType moonType;
    int cartelPlayer = -1;
    boolean policePresent = false;

    public Moon(String name, MoonType type, int nPlayers) {
        super(Utils.ComponentType.AREA, name);
        init(type, nPlayers);
    }

    private Moon(String name, MoonType type, int componentID, int nPlayers) {
        super(Utils.ComponentType.AREA, name, componentID);
        init(type, nPlayers);
    }

    private void init(MoonType type, int nPlayers) {
        deck = new PartialObservableDeck<>("Cards on " + componentName, nPlayers);
        this.moonType = type;
    }

    public SiriusCard drawCard() {
        // if police are present then the last card in the deck is locked
        if (policePresent && deck.getSize() == 1)
            return null;
        return deck.draw();
    }

    public int getCartelOwner() {
        return cartelPlayer;
    }

    public void setCartelOwner(int player) {
        cartelPlayer = player;
    }

    public MoonType getMoonType() {
        return moonType;
    }

    public int getDeckSize() {
        if (policePresent)
            return deck.getSize() -1;
        return deck.getSize();
    }

    public void lookAtDeck(int player) {
        for (int i = 0; i < deck.getSize(); i++) {
            deck.setVisibilityOfComponent(i, player, true);
        }
    }

    public void setPolicePresence() {
        policePresent = true;
        cartelPlayer = -1;  // removes cartel
    }

    public void removePolicePresence() {
        policePresent = false;
    }

    public boolean getPolicePresence() {
        return policePresent;
    }

    public PartialObservableDeck<SiriusCard> getDeck() {
        return deck;
    }

    public void addCard(SiriusCard card) {
        deck.add(card);
    }

    @Override
    public Moon copy() {
        Moon retValue = new Moon(this.componentName, this.moonType, componentID, getDeckSize());
        retValue.deck = deck.copy();
        retValue.cartelPlayer = cartelPlayer;
        copyComponentTo(retValue);
        return retValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Moon) {
            Moon moon = (Moon) obj;
            return super.equals(moon) && deck.equals(moon.deck) && moonType == moon.moonType &&
                    cartelPlayer == moon.cartelPlayer && policePresent == moon.policePresent;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(deck, moonType, cartelPlayer, componentID, policePresent);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %s", componentName, moonType, cartelPlayer > -1 ? "Cartel :  " + cartelPlayer : "");
    }
}

