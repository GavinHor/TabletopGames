package games.saboteur.components;

import java.util.Arrays;

public class ActionCard extends SaboteurCard
{
    public final ActionTypeCard actionType;
    public final ToolCardType[] toolTypes;

    public enum ToolCardType
    {
        MineCart,
        Lantern,
        Pickaxe,
    }

    public enum ActionTypeCard
    {
        RockFall,
        BrokenTools,
        FixTools,
        Map,
    }

    public ActionCard(ActionTypeCard actionType)
    {
        super(SaboteurCardType.Action);
        this.actionType = actionType;
        toolTypes = null;
    }

    public ActionCard(ActionTypeCard actionType, ToolCardType[] toolTypes)
    {
        super(SaboteurCardType.Action);
        this.actionType = actionType;
        this.toolTypes = toolTypes;
    }

    public ActionCard(ActionTypeCard actionType, ToolCardType toolTypes)
    {
        super(SaboteurCardType.Action);
        this.actionType = actionType;
        this.toolTypes = new ToolCardType[] {toolTypes};
    }

    @Override
    public String toString()
    {
        switch(actionType)
        {
            case Map:
            case RockFall:
                return actionType.toString();

            case BrokenTools:
            case FixTools:
                return actionType + Arrays.toString(toolTypes);
        }
        return null;
    }
}
