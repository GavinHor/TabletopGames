package games.saboteur.components;

import core.components.Card;

public class SaboteurCard extends Card
{
    public final SaboteurCardType type;
    public final int nOfNuggets;
    public enum SaboteurCardType
    {
        Path,
        Role,
        GoldNugget,
        Action,
    }

    public SaboteurCard (SaboteurCardType type)
    {
        super(type.toString());
        this.type = type;
        this.nOfNuggets = 0;
    }

    public SaboteurCard (int nOfNuggets)
    {
        super(SaboteurCardType.GoldNugget.toString());
        this.type = SaboteurCardType.GoldNugget;
        this.nOfNuggets = nOfNuggets;
    }
}
