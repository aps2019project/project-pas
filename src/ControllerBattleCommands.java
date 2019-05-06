import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;

public class ControllerBattleCommands {
    private static ControllerBattleCommands instance = new ControllerBattleCommands();
    private Request request = Request.getInstance();
    private DataBase database = DataBase.getInstance();
    private View view = View.getInstance();

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
                case SHOW_MY_MINIONS:
                    showMinions();
                    break;
                case SHOW_LEADERBOARD:
                    show();
                    break;
                case SELECT_DECK_NAME:
                    select();
                    break;
                case MOVE_TO_X_Y:
                    move();
                    break;
                case HELP:
                    help();
                    break;
                case ATTACK_ID:
                    attack();
                    break;
                case USE_X_Y:
                    use();
                    break;
                case INSERT_NAME_IN_X_Y:
                    insert();
                    break;
                case END_GAME:
                    end();
                    break;
                case ENTER:
                    enter();
                    break;
                case EXIT:
                    didExit = true;
                    break;
                default:
                    view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
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
            List<Unit> minions = database.getCurrentBattle().getBattleGround()
                    .getMinionsOfPlayer(database.getCurrentBattle().getPlayerInTurn());
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
        } else if (request.getCommand().equals("show battleground")) {
            BattleGround battleGround = database.getCurrentBattle().getBattleGround();
            for (Cell[] cellRow : battleGround.getCells()) {
                for (Cell cell : cellRow) {
                    if (cell.getUnit() == null) {
                        view.showCell(" ");
                    } else if (cell.getUnit().getId().contains(database.getCurrentBattle().getPlayer1().getPlayerInfo().getPlayerName())) {
                        if (cell.getUnit().getHeroOrMinion().equals(Constants.HERO)) {
                            view.showCell("H");
                        } else view.showCell("1");
                    } else if (cell.getUnit().getId().contains(database.getCurrentBattle().getPlayer2().getPlayerInfo().getPlayerName())) {
                        if (cell.getUnit().getHeroOrMinion().equals(Constants.HERO)) {
                            view.showCell("h");
                        } else view.showCell("2");
                    }
                }
                view.print("");
            }
        }
    }

    public void select() {
        if (!request.getCommand().toLowerCase().matches("^select .+$")) {
            view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
            return;
        }
        Pattern pattern = Pattern.compile("^select (.+)$");
        Matcher matcher = pattern.matcher(request.getCommand());
        if (!matcher.find()) {
            view.printOutputMessage(OutputMessageType.CARD_NOT_FOUND);
            return;
        }
        switch (database.getCurrentBattle().getPlayerInTurn().select(matcher.group(1))) {
            case INVALID_COLLECTABLE_CARD:
                view.printOutputMessage(OutputMessageType.INVALID_COLLECTABLE_CARD);
                break;
            case UNIT_IS_STUNNED:
                view.printOutputMessage(OutputMessageType.UNIT_IS_STUNNED);
                break;
            case SELECTED:
                view.printOutputMessage(OutputMessageType.SELECTED);
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
                    .getSelectedUnit().attack(matcher.group(1))) {
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
                case A_UNIT_CANT_ATTACK_TARGET:
                    view.printOutputMessage(OutputMessageType.A_UNIT_CANT_ATTACK_TARGET);
                    break;
                case A_UNIT_DOESNT_EXIST:
                    view.printOutputMessage(OutputMessageType.A_UNIT_DOESNT_EXIST);
                    break;
                case COMBO_ATTACK_SUCCESSFUL:
                    view.printOutputMessage(OutputMessageType.COMBO_ATTACK_SUCCESSFUL);
                    break;
                default:
            }
            //todo
        }
        view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
    }

    public void use() {
        if (request.getCommand().toLowerCase().matches("use special power [(]\\d+,\\d+[)]")) {
            Pattern pattern = Pattern.compile("use special power [(](\\d+),(\\d+)[)]");
            Matcher matcher = pattern.matcher(request.getCommand());
            if (Integer.parseInt(matcher.group(1)) < 5 && Integer.parseInt(matcher.group(1)) >= 0
                    && Integer.parseInt(matcher.group(2)) < 9 && Integer.parseInt(matcher.group(2)) >= 0) {
                int row = Integer.parseInt(matcher.group(1));
                int column = Integer.parseInt(matcher.group(2));
                Player player = database.getCurrentBattle().getPlayerInTurn();
                Unit hero = database.getCurrentBattle().getBattleGround().getHeroOfPlayer(player);
                view.printOutputMessage(database.getCurrentBattle().useSpecialPower(hero, player, row, column));
            } else {
                view.printOutputMessage(OutputMessageType.INVALID_NUMBER);
            }
        } else view.printOutputMessage(OutputMessageType.INVALID_COMMAND);
    }

    public void insert() {
        if (request.getCommand().toLowerCase().matches("^insert .+ in [(](\\d+),(\\d+)[)]$")) {
            Pattern pattern = Pattern.compile("^insert (.+) in [(](\\d+),(\\d+)[)]$");
            Matcher matcher = pattern.matcher(request.getCommand());
            if (!matcher.find()) {
                view.printOutputMessage(OutputMessageType.CARD_NOT_FOUND);
                return;
            }
            Card card = database.getCurrentBattle().getPlayerInTurn()
                    .getHand().getCardByName(matcher.group(1));
            int row = Integer.parseInt(matcher.group(2));
            int column = Integer.parseInt(matcher.group(3));
            view.printOutputMessage(database.getCurrentBattle().insert(card, row, column));
        }
    }

    public void end() {
        if (request.getCommand().toLowerCase().equals("end game")) {
            if (!database.getCurrentBattle().isBattleFinished()) {
                request.setOutputMessageType(OutputMessageType.BATTLE_NOT_FINISHED);
                view.printOutputMessage(request.getOutputMessageType());
            } else {
                //todo
            }
            return;
        }
        if (request.getCommand().toLowerCase().equals("end turn")) {
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
