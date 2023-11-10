package games.saboteur.components;

public class RoleCard extends SaboteurCard{

    public final RoleCardType type;

    public enum RoleCardType
    {
        GoldMiner,
        Saboteur,
    }

    public RoleCard(RoleCardType type)
    {
        super(SaboteurCardType.Role);
        this.type = type;
    }
}
