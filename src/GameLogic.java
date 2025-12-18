package src;

import src.Database.DatabaseManager;
import src.GamePage.GamePageUI;
import src.Model.GameState;
import src.Model.Role;

import javax.swing.*;

public class GameLogic {

    private GamePageUI gameGui;

    private static GameLogic instance = new GameLogic();
    public static GameLogic getInstance() {
        return instance;
    }

    private GameLogic() {}

    public void startMainPage() {
        SwingUtilities.invokeLater(() -> MainPageUI.getInstance().setVisible(true));
    }

    public void startGamePage() {
        if (MainPageUI.getInstance() != null) {
            MainPageUI.getInstance().dispose();
            //lobbyGui = null;
        }

        this.gameGui = GamePageUI.getInstace();
    }

    public void joinGameAsHost(String nickname) {
        GameState.getInstance().getMe().setNickName(nickname);
        GameState.getInstance().getMe().setRole(Role.HOST);
        GameState.getInstance().getOpponent().setRole(Role.GUEST);
        Connection.getInstance().joinGameAsHost();

    }

    public void joinGameAsGuest(String nickname,String code) {
        GameState.getInstance().getMe().setNickName(nickname);
        GameState.getInstance().getMe().setRole(Role.GUEST);
        if (code != null && !code.trim().isEmpty()) {
            Connection.getInstance().joinGameAsGuest(code);
        } else {
            MainPageUI.getInstance().showGuiMessage("Gecerli bir bağlantı adresi girilmedi.");
        }
    }

    public void cancelHost() {
        Connection.getInstance().cancelServer();
    }

    public void disconnect() {
        Connection.getInstance().closeConnection();
        if (gameGui != null) {
            gameGui.dispose();
        }
    }

    public void sendMessage(String message) {
        if (Connection.getInstance() != null) {
            showGameMessage("Siz: " + message);
            Connection.getInstance().sendMessage(message);
        }
    }

    public void handleOpponentInput(String message) {
        showGameMessage("Rakip: " + message);
    }

    public void showGameMessage(String message) {
        if (gameGui != null) {
            JOptionPane.showMessageDialog(gameGui,message);
        } else {
            MainPageUI.getInstance().showGuiMessage(message);
        }
    }

    public void showWaitingDialog(String ip) {
        MainPageUI.getInstance().showWaitingDialog(ip);
    }

    public void closeWaitingDialog() {
        MainPageUI.getInstance().closeWaitingDialog();
    }
}