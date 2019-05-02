import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

public class ControllerBattleCommands {
    private static final Request request = Request.getInstance();
    private static final DataBase database = DataBase.getInstance();
    private static final View view = View.getInstance();
    private static final ControllerBattleCommands instance = new ControllerBattleCommands();

    public static ControllerBattleCommands getInstance() {
        return instance;
    }

    private ControllerBattleCommands() {
    }

    public void main() {
        boolean didExit = false;
        while (!didExit) {
            request.getNewCommand();
            switch (request.getType()) {
                case GAME_INFO:
                    showGameInfo();
                    break;
                case SHOW_MINIONS:
                    showMinions();
                    break;
                case SHOW:
                    show();
                    break;
                case SELECT:
                    select();
                    break;
                case MOVE:
                    move();
                    break;
                case HELP:
                    help();
                    break;
                case ATTACK:
                    attack();
                    break;
                case USE:
                    use();
                    break;
                case INSERT:
                    insert();
                    break;
                case END:
                    end();
                    break;
                case ENTER:
                    enter();
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

    public void showGameInfo() {
        if (request.getCommand().equals("game info")) {
            view.showGameInfo(database.getCurrentBattle());
        }
    }

    public void showMinions() {
        if (request.getCommand().equals("show my minions")) {
            List<Unit> minions = database.getCurrentBattle().getBattleGround().getMinionsOfPlayer(database.getCurrentBattle().getPlayerInTurn());
            for (Unit minion : minions) {
                view.showMinionInBattle(minion, database.getCurrentBattle().getBattleGround().getCoordinationOfUnit(minion));
            }
        } else if (request.getCommand().equals("show opponent minions")) {
            Player player;
            if (database.getCurrentBattle().getPlayerInTurn() == database.getCurrentBattle().getPlayer1())
                player = database.getCurrentBattle().getPlayer2();
            else player = database.getCurrentBattle().getPlayer1();
            List<Unit> minions = database.getCurrentBattle().getBattleGround().getMinionsOfPlayer(player);
            for (Unit minion : minions) {
                view.showMinionInBattle(minion, database.getCurrentBattle().getBattleGround().getCoordinationOfUnit(minion));
            }
        }
    }

    public void show() {
        if (request.getCommand().matches("show card info .+")) {
            String cardId = request.getCommand().split("\\s+")[3];
            Card card = database.getCurrentBattle().getBattleGround().getCardByID(cardId);
            if (card != null) {
                if (card instanceof Spell) {
                    view.showCardInfoSpell((Spell) card);
                } else if (card instanceof Unit) {
                    if (((Unit) card).getHeroOrMinion().equals("Minion")) {
                        view.showCardInfoMinion((Unit) card);
                    } else if (((Unit) card).getHeroOrMinion().equals("Hero")) {
                        view.showCardInfoHero((Unit) card);
                    }
                }
            } else {
                request.setOutputMessageType(OutputMessageType.NO_CARD_IN_BATTLEGROUND);
                view.printOutputMessage(request.getOutputMessageType());
                //todo
            }
        } else if (request.getCommand().equals("show collectables")) {
            view.showCollectables(database.getCurrentBattle().getPlayerInTurn().getCollectables());
        } else if (request.getCommand().equals("show info")) {
            if (database.getCurrentBattle().getPlayerInTurn().getSelectedCollectable() != null) {
                view.showCollectable(database.getCurrentBattle().getPlayerInTurn().getSelectedCollectable());
            }
        } else if (request.getCommand().equals("show next card")) {
            Card card = database.getCurrentBattle().getPlayerInTurn().getNextCard();
            if (card instanceof Spell) {
                view.showCardInfoSpell((Spell) card);
            } else if (card instanceof Unit) {
                view.showCardInfoMinion((Unit) card);
            }
        } else if (request.getCommand().equals("show hand")) {
            view.showHand(database.getCurrentBattle().getPlayerInTurn().getHand());
        } else if (request.getCommand().equals("show menu")) {
            view.printHelp(HelpType.BATTLE_COMMANDS_HELP);
        }
    }

    public void select() {
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

    public void move() {
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

    public void attack() {
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
            String[] orderPieces = request.getCommand().split(" ");
            String[] attackers = new String[orderPieces.length - 3];
            if (orderPieces.length - 3 >= 0)
                System.arraycopy(orderPieces, 3, attackers, 0
                        , orderPieces.length - 3);
            switch (Unit.attackCombo(orderPieces[2], attackers)) {
                case
            }
            //todo
        }
        view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
    }

    public void use() {
        if (request.getCommand().matches("use special power [(]\\d+,\\d+[)]")) {
            Pattern pattern = Pattern.compile("use special power [(](\\d+),(\\d+)[)]");
            Matcher matcher = pattern.matcher(request.getCommand());
            if (Integer.parseInt(matcher.group(1)) < 5 && Integer.parseInt(matcher.group(1)) >= 0
                    && Integer.parseInt(matcher.group(2)) < 9 && Integer.parseInt(matcher.group(2)) >= 0) {
                int row=Integer.parseInt(matcher.group(1));
                int column= Integer.parseInt(matcher.group(2));
                Player player = database.getCurrentBattle().getPlayerInTurn();
                Unit hero = database.getCurrentBattle().getBattleGround().getHeroOfPlayer(player);
                view.printOutputMessage(database.getCurrentBattle().useSpecialPower(hero,player,row,column));
            } else {
                view.printOutputMessage(OutputMessageType.INVALID_NUMBER);
            }
        }else view.printOutputMessage(OutputMessageType.INVALID_COMMAND);
    }

    public void insert() {
        if (request.getCommand().matches("insert .+ in [(](\\d+),(\\d+)[)]")) {
            Pattern pattern = Pattern.compile("insert (.+) in [(](\\d+),(\\d+)[)]");
            Matcher matcher = pattern.matcher(request.getCommand());
            Card card = database.getCurrentBattle().getPlayerInTurn().getHand().getCardByName(matcher.group(1));
            int row = Integer.parseInt(matcher.group(2));
            int column = Integer.parseInt(matcher.group(3));
            view.printOutputMessage(database.getCurrentBattle().insert(card,row,column));
        }
    }

    public void end() {
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

    public void enter() {
        if (!request.getCommand().equals("enter graveyard")) {
            request.setOutputMessageType(OutputMessageType.WRONG_COMMAND);
            view.printOutputMessage(request.getOutputMessageType());
        } else {
            ControllerGraveYard.getInstance().main();
        }
    }

    public void help() {
        view.printList(database.getCurrentBattle().getAvailableMoves());
    }
}
