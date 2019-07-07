import com.gilecode.yagson.YaGson;
import com.gilecode.yagson.YaGsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import sun.nio.ch.Net;

import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientHandler extends Thread {
    private Socket socket;
    private NetworkDB netWorkDB = NetworkDB.getInstance();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        Connection connection = new Connection(socket);
        NetworkDB.getInstance().addConnection(connection);
        YaGson yaGson = new YaGsonBuilder().setPrettyPrinting().create();
        JsonStreamParser parser = connection.getParser();
        while (true) {
            JsonObject obj = parser.next().getAsJsonObject();
            Request request = yaGson.fromJson(obj.toString(), Request.class);
            switch (request.getRequestType()) {
                case sendMessage: {
                    System.out.println(request.getMessage());
                    NetworkDB.getInstance().sendResponseToClient
                            (new Response(ResponseType.sendMessage, "hi again", null, null), connection);
                    break;
                }
                case signUp: {
                    Pattern pattern = Pattern.compile("userName:(\\w+)password:(\\w+)");
                    Matcher matcher = pattern.matcher(request.getMessage());
                    matcher.find();
                    Account account = NetworkDB.getInstance().getAccount(matcher.group(1));
                    if (account != null) {
                        NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.signUp, OutputMessageType.USERNAME_ALREADY_EXISTS.getMessage(), null, null), connection);
                    } else {
                        List<Object> accountList = new ArrayList<>();
                        Account newAccount = new Account(matcher.group(1), matcher.group(2));
                        connection.setAccount(newAccount);
                        NetworkDB.getInstance().getAccountStatusMap().put(newAccount, AccountStatus.online);
                        accountList.add(newAccount);
                        NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.signUp, OutputMessageType.CREATED_ACCOUNT_SUCCESSFULLY.getMessage(), null, accountList), connection);
                        updateAccountsInLeaderBoard(newAccount);
                    }
                    break;
                }
                case login: {
                    Pattern pattern = Pattern.compile("userName:(\\w+)password:(\\w+)");
                    Matcher matcher = pattern.matcher(request.getMessage());
                    matcher.find();
                    Account account = NetworkDB.getInstance().getAccount(matcher.group(1));
                    if (account == null) {
                        NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.login, OutputMessageType.ACCOUNT_DOESNT_EXIST.getMessage(), null, null), connection);
                    } else {
                        AccountStatus accountStatus = NetworkDB.getInstance().getAccountStatusMap().get(account);
                        if (!matcher.group(2).equals(account.getPassword())) {
                            NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.login, OutputMessageType.INVALID_PASSWORD.getMessage(), null, null), connection);
                            break;
                        }
                        if (accountStatus == AccountStatus.offline) {
                            List<Object> accountList = new ArrayList<>();
                            accountList.add(account);
                            NetworkDB.getInstance().getAccountStatusMap().put(account, AccountStatus.online);
                            connection.setAccount(account);
                            NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.login, OutputMessageType.LOGGED_IN_SUCCESSFULLY.getMessage(), null, accountList), connection);
                            updateAccountsInLeaderBoard(account);
                        } else {
                            NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.login, OutputMessageType.ALREADY_LOGGED_IN.getMessage(), null, null), connection);
                        }
                    }
                    break;
                }
                case logout: {
                    Pattern pattern = Pattern.compile("userName:(\\w+)");
                    Matcher matcher = pattern.matcher(request.getMessage());
                    if (matcher.find()) {
                        Account account = NetworkDB.getInstance().getAccount(matcher.group(1));
                        if (account != null) {
                            connection.setAccount(null);
                            NetworkDB.getInstance().getAccountStatusMap().put(account, AccountStatus.offline);
                            NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.logout, OutputMessageType.LOGGED_OUT_SUCCESSFULLY.getMessage(), null, null), connection);
                            updateAccountsInLeaderBoard(account);
                        }
                    }
                    break;
                }
                case leaderBoard: {
                    NetworkDB.getInstance().getAccountStatusMap().put(connection.getAccount(), AccountStatus.leaderBoard);
                    List<Object> accountList = new ArrayList<>(NetworkDB.getInstance().getAccountStatusMap().keySet());
                    List<Integer> integerList = new ArrayList<>();
                    setIntegerList(accountList, integerList);
                    NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.leaderBoard, null, integerList, accountList), connection);
                }
            }
            if (request.getRequestType().equals(RequestType.close))
                break;
        }
        NetworkDB.getInstance().closeConnection(socket);
    }

    private void setIntegerList(List<Object> accountList, List<Integer> integerList) {
        for (Object object : accountList) {
            Account account = (Account) object;
            if (NetworkDB.getInstance().getAccountStatusMap().get(account).equals(AccountStatus.offline)) {
                integerList.add(0);
            } else {
                integerList.add(1);
            }
        }
    }

    private synchronized void updateAccountsInLeaderBoard(Account account){
        List<Object> accountList = new ArrayList<>(NetworkDB.getInstance().getAccountStatusMap().keySet());
        List<Integer> integerList = new ArrayList<>();
        setIntegerList(accountList, integerList);
        for (Object object : accountList){
            Account account1 = (Account) object;
            if (NetworkDB.getInstance().getAccountStatusMap().get(account1).equals(AccountStatus.leaderBoard)){
                NetworkDB.getInstance().sendResponseToClient(new Response(ResponseType.updateLeaderBoard, null, integerList, accountList), NetworkDB.getInstance().getConnectionWithAccount(account1));
            }
        }
    }
}