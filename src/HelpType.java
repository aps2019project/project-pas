public enum HelpType {
    CONTROLLER_SHOP_HELP("Shop Commands:" +
            "\n\texit" +
            "\n\tshow collection" +
            "\n\tsearch [item name | card name]" +
            "\n\tsearch collection [item name | card name]" +
            "\n\tbuy [item name | card name]" +
            "\n\tsell [card id | item id]" +
            "\n\tshow" +
            "\n\thelp"),
    CONTROLLER_ACCOUNT_HELP("Account Commands:\n" +
            "\tcreate account [username]\n" +
            "\tlogin [username]\n" +
            "\tshow leaderboard\n" +
            "\thelp\n"),
    CONTROLLER_COLLECTION_HELP("Collection Commands:" +
            "\n\texit" +
            "\n\tshow" +
            "\n\tsearch [card name | item name]" +
            "\n\tsave" +
            "\n\tcreate deck [deck name]" +
            "\n\tdelete deck [deck name]" +
            "\n\tadd [card id | item id | hero id] to deck [deck name]" +
            "\n\tremove [card id | item id | hero id] from deck [deck name]" +
            "\n\tvalidate deck [deck name]" +
            "\n\tselectId deck [deck name]" +
            "\n\tshow all decks" +
            "\n\tshow deck [deck name]" +
            "\n\thelp"),
    BATTLE_COMMANDS_HELP("In battle commands:" +
            "\n\tgame info" +
            "\n\tshow my minions" +
            "\n\tshow opponent minions" +
            "\n\tshow card info [card id]" +
            "\n\tselect [card id]" +
            "\n\tmove to ([row],[column])" +
            "\n\tattack [opponent card id]" +
            "\n\tattack combo [opponent card id] [my card id] [my card id]..." +
            "\n\tuse special power ([row],[column])" +
            "\n\tshow hand" +
            "\n\tinsert [card name] in ([row],[column])" +
            "\n\tend turn" +
            "\n\tshow collectables" +
            "\n\tselect [collectable id])" +
            "\n\tuse ([row],[column])" +
            "\n\tshow next card" +
            "\n\tenter graveyard" +
            "\n\tshow battleground" +
            "\n\thelp" +
            "\n\tforfeit" +
            "\n\tend game"),
    CONTROLLER_SINGLE_PLAYER_MENU("Single player commands :" +
            "\n\tenter story" +
            "\n\tenter custom"),
    STORY_MODE_OPTIONS("level1\nlevel2\nlevel3"),
    CONTROLLER_BATTLE_MENU_HELP("Battle menu commands:" +
            "\n\tenter singleplayer" +
            "\n\tenter multiplayer" +
            "\n\thelp" +
            "\n\tshow users" +
            "\n\texit"),
    CONTROLLER_MAIN_MENU_HELP("Main Menu Commands:\n" +
            "\tenter collection\n" +
            "\tenter shop\n" +
            "\tenter battle\n" +
            "\tlogout\n" +
            "\thelp\n" +
            "\tshow match history"),
    MODES_HELP("flags\none_flag\nclassic"),

    CONTROLLER_MULTI_PLAYER_MENU("Commands:\n" +
            "\tselect user [username]\n" +
            "\thelp\n" +
            "\texit\n"),
    CONTROLLER_GRAVEYARD("Commands:\n" +
            "\tshow info [card id]\n" +
            "\tshow cards");

    private String message;

    HelpType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
