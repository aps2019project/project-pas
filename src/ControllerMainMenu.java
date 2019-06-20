import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ControllerMainMenu {
    private static ControllerMainMenu ourInstance;
    private DataBase dataBase = DataBase.getInstance();
    private Request request = Request.getInstance();
    private View view = View.getInstance();
    private ControllerMatchInfo controllerMatchInfo = ControllerMatchInfo.getInstance();
    private boolean changeOpacity = true;
    private boolean shouldClose = false;
    private ControllerShop controllerShop = ControllerShop.getOurInstance();
    public static Stage stage;

    public static ControllerMainMenu getInstance() {
        return ourInstance;
    }

    public ControllerMainMenu() {
        ourInstance = this;
    }

    @FXML
    private ImageView multiPlayerBtn;

    @FXML
    private ImageView backBtn;

    @FXML
    private ImageView closeBtn;

    @FXML
    void close(MouseEvent event) {
        dataBase.saveAccounts();
        Main.window.close();
    }

    @FXML
    void makeCloseBtnOpaque(MouseEvent event) {
        closeBtn.setStyle("-fx-opacity: 1");
    }

    @FXML
    void makeCloseBtnTransparent(MouseEvent event) {
        closeBtn.setStyle("-fx-opacity: 0.6");
    }

    @FXML
    void goBack(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ControllerAccount.fxml"));
        Main.window.setScene(new Scene(root));
    }

    @FXML
    void makeBackBtnOpaque(MouseEvent event) {
        backBtn.setStyle("-fx-opacity: 1");
    }

    @FXML
    void makeBackBtnTransparent(MouseEvent event) {
        backBtn.setStyle("-fx-opacity: 0.6");
    }


    @FXML
    void enterSinglePlayer(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ControllerSinglePlayerMenu.fxml"));
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    void enterMultiPlayer(MouseEvent event) {
        //todo not needed for phase 2
    }

    @FXML
    void makeMultiPlayerBtnOpaque(MouseEvent event) {
        multiPlayerBtn.setStyle("-fx-opacity: 1");
    }

    @FXML
    void makeMultiPlayerBtnTransparent(MouseEvent event) {
        multiPlayerBtn.setStyle("-fx-opacity: 0.6");
    }

    @FXML
    private ImageView battleBtn;

    @FXML
    private ImageView singleBtn;

    @FXML
    void customOrMulti(MouseEvent event) {
        battleBtn.setStyle("-fx-opacity: 1");
        changeOpacity = false;
        multiPlayerBtn.setVisible(true);
        singleBtn.setVisible(true);
        if (shouldClose) {
            multiPlayerBtn.setVisible(false);
            singleBtn.setVisible(false);
            shouldClose = false;
            changeOpacity = true;
        } else
            shouldClose = true;
    }

    @FXML
    void makeSingleBtnOpaque(MouseEvent event) {
        singleBtn.setStyle("-fx-opacity: 1");
    }

    @FXML
    void makeSingleBtnTransparent(MouseEvent event) {
        singleBtn.setStyle("-fx-opacity: 0.6");
    }

    @FXML
    void makeBattleBtnOpaque(MouseEvent event) {
        battleBtn.setStyle("-fx-opacity: 1");
    }

    @FXML
    void makeBattleBtnTransparent(MouseEvent event) {
        if (changeOpacity) {
            battleBtn.setStyle("-fx-opacity: 0.6");
        }
    }

    @FXML
    private ImageView shopBtn;

    @FXML
    private ImageView collectionBtn;

    @FXML
    void enterCollection(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ControllerCollection.fxml"));
        Main.window.setScene(new Scene(root));
    }

    @FXML
    void makeCollectionBtnOpaque(MouseEvent event) {
        collectionBtn.setStyle("-fx-opacity: 1");
    }

    @FXML
    void makeCollectionBtnTransparent(MouseEvent event) {
        collectionBtn.setStyle("-fx-opacity: 0.6");
    }

    @FXML
    void enterShop(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("ControllerShop.fxml"));
        Main.window.setScene(new Scene(root));
        controllerShop = ControllerShop.getOurInstance();
        controllerShop.showCards();
    }

    @FXML
    void makeShopBtnOpaque(MouseEvent event) {
        shopBtn.setStyle("-fx-opacity: 1");
    }

    @FXML
    void makeShopBtnTransparent(MouseEvent event) {
        shopBtn.setStyle("-fx-opacity: 0.6");
    }

    public void main() {
        boolean didLogout = false;
        while (!didLogout) {
            request.getNewCommand();
            switch (request.getType()) {
                case ENTER:
                    enter();
                    break;
                case LOGOUT:
                    logout();
                    didLogout = true;
                    break;
                case HELP:
                    help();
                    break;
                case MATCH_HISTORY:
                    view.showMatchHistoryTitle();
                    controllerMatchInfo.showMatchHistory(dataBase.getLoggedInAccount());
                    break;
                default:
                    view.printOutputMessage(OutputMessageType.WRONG_COMMAND);
            }
        }
    }

    public void enter() {
        switch (request.getCommand()) {
            case "enter collection":
                ControllerCollection.getInstance().main();
                break;
            case "enter shop":
                ControllerShop.getOurInstance().main();
                break;
            case "enter battle":
                try {
                    ControllerBattleMenu.getInstance().main();
                } catch (GoToMainMenuException e) {
                }
                break;
            default:
                request.setOutputMessageType(OutputMessageType.WRONG_COMMAND);
                view.printOutputMessage(request.getOutputMessageType());
        }
    }

    public void logout() {
        dataBase.setLoggedInAccount(null);
    }

    public void help() {
        view.printHelp(HelpType.CONTROLLER_MAIN_MENU_HELP);
    }
}
