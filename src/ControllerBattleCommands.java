import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

public class ControllerBattleCommands {
    private static final DataBase database = DataBase.getInstance();
    private static final View view = View.getInstance();

    private ControllerBattleCommands() {
    }

    public void main() {
        boolean didExit = false;
        Request request=Request.getInstance();
        while (!didExit) {
            request.getNewCommand();
            switch (request.getType()) {
                case GAME_INFO:
                    showGameInfo(request);
                    break;
                case SHOW_MINIONS:
                    break;
                case SHOW:
                    break;
                case SELECT:
                    break;
                case MOVE:
                    break;
                case ATTACK:
                    break;
                case USE:
                    break;
                case INSERT:
                    break;
                case END:
                    end(request);
                    break;
                case ENTER:
                    enter(request);
                    break;
                case EXIT:
                    didExit = true;
                    break;
                default:
                    System.out.println("!!!!!! bad input in ControllerBattleCommands.main");
                    System.exit(-1);
            }
        }
    }

    public void showGameInfo(Request request) {

    }

    public void showMinions(Request request) {
        if (request.getCommand().matches("^show my minions$")) {

            return;
        }
        if (request.getCommand().matches("^show opponent minions$")) {

        }
    }

    public void show(Request request) {
        if(request.getCommand().equals("show game info")){
            view.showGameInfo(database.getCurrentBattle());
        }
        else if(request.getCommand().equals("show my minions")){
            List<Unit> minions=database.getCurrentBattle().getBattleGround().getMinionsOfPlayer(database.getCurrentBattle().getPlayerInTurn());
            for(Unit minion:minions){
                view.showMinionInBattle(minion,database.getCurrentBattle().getBattleGround().getCoordinationOfUnit(minion));
            }
        }
        else if(request.getCommand().equals("show opponent minions")){
            Player player;
            if(database.getCurrentBattle().getPlayerInTurn()==database.getCurrentBattle().getPlayer1())
                player=database.getCurrentBattle().getPlayer2();
            else player=database.getCurrentBattle().getPlayer1();
            List<Unit> minions=database.getCurrentBattle().getBattleGround().getMinionsOfPlayer(player);
            for (Unit minion:minions){
                view.showMinionInBattle(minion,database.getCurrentBattle().getBattleGround().getCoordinationOfUnit(minion));
            }
        }
        else if(request.getCommand().matches("show card info \\w+")){
            String cardId=request.getCommand().split("\\s+")[3];
            Card card=database.getCurrentBattle().getBattleGround().getCardByID(cardId);
            if(card!=null){
                if(card instanceof Spell)
                {
                    view.showCardInfoSpell((Spell) card);
                }else if(card instanceof Unit){
                    if(((Unit)card).getHeroOrMinion().equals("Minion")){
                        view.showCardInfoMinion((Unit)card);
                    }
                    else if(((Unit)card).getHeroOrMinion().equals("Hero")){
                        view.showCardInfoHero((Unit)card);
                    }
                }
            }else {
                request.setOutputMessageType(OutputMessageType.NO_CARD_IN_BATTLEGROUND);
                //todo
                view.printOutputMessage(request.getOutputMessageType());
            }
        }
    }

    public void select(Request request) {
        if (!request.getCommand().matches("^select .+$")) {
            view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
            return;
        }
        Pattern pattern = Pattern.compile("^select (.+)$");
        Matcher matcher = pattern.matcher(request.getCommand());
        switch (database.getCurrentBattle().getPlayerInTurn().select(matcher.group(1))) {
            case INVALID_COLLECTABLE_CARD:
                view.printOutputMessage(OutputMessageType.INVALID_COLLECTABLE_CARD);
                break;
            case SELECTED:
                break;
            default:
        }
    }

    public void move(Request request) {
        if (!request.getCommand().matches("^move to \\d+ \\d+$")) {
            view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
            return;
        }
        Pattern pattern = Pattern.compile("^move to (\\d+) (\\d+)$");
        Matcher matcher = pattern.matcher(request.getCommand());
        int destinationRow = Integer.parseInt(matcher.group(1));
        int destinationColumn = Integer.parseInt(matcher.group(2));
        switch (database.getCurrentBattle().getBattleGround().
                moveUnit(destinationRow, destinationColumn)) {
            case UNIT_NOT_SELECTED:
                view.printOutputMessage(OutputMessageType.UNIT_NOT_SELECTED);
                break;
            case UNIT_MOVED:
                view.showUnitMove(database.getCurrentBattle().
                                getPlayerInTurn().getSelectedUnit().getId()
                        , destinationRow, destinationColumn);
                break;
            default:
        }
    }

    public void attack(Request request) {
        if (request.getCommand().matches("^attack .+$")) {
            Pattern pattern = Pattern.compile("^attack (.+)$");
            Matcher matcher = pattern.matcher(request.getCommand());
            if (database.getCurrentBattle().getPlayerInTurn().getSelectedUnit() == null) {
                view.printOutputMessage(OutputMessageType.UNIT_NOT_SELECTED);
                return;
            }

            switch (database.getCurrentBattle().getPlayerInTurn()
                    .getSelectedUnit().attackUnit(matcher.group(1))) {
                case TARGET_NOT_IN_RANGE:
                    view.printOutputMessage(OutputMessageType.TARGET_NOT_IN_RANGE);
                    break;
                case INVALID_CARD:
                    view.printOutputMessage(OutputMessageType.INVALID_CARD);
                    break;
                case ALREADY_ATTACKED:
                    view.printOutputMessage(OutputMessageType.ALREADY_ATTACKED);
                    break;
                default:
            }
            return;
        }
        if (request.getCommand().matches("^attack combo .+$")) {
            //todo
        }
        view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
    }

    public void use() {

    }

    public void insert() {

    }

    public void end(Request request) {
        if (request.getCommand().equals("end game")) {
            if (!database.getCurrentBattle().isBattleFinished()) {
                request.setOutputMessageType(OutputMessageType.BATTLE_NOT_FINISHED);
                view.printOutputMessage(request.getOutputMessageType());
            } else {
                //todo
            }
            return;
        }
        if (request.getCommand().equals("end turn")) {
            database.getCurrentBattle().nextTurn();
            return;
        }
        request.setOutputMessageType(OutputMessageType.WRONG_COMMAND);
        view.printOutputMessage(request.getOutputMessageType());
    }

    public void enter(Request request) {
        if (!request.getCommand().equals("enter graveyard")) {
            request.setOutputMessageType(OutputMessageType.WRONG_COMMAND);
            view.printOutputMessage(request.getOutputMessageType());
        } else {
            ControllerGraveYard.getInstance().main();
        }
    }
}
