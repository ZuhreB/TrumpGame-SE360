package src;

import src.GamePage.GamePageUI;
import src.Model.GameState;
import src.Model.Role;

import javax.swing.SwingUtilities;

public class GameLogic {

    private MainPageUI lobbyGui=MainPageUI.getInstance();
    private GamePageUI gameGui;
    private Connection connection=Connection.getInstance();
    private GameState gameState = GameState.getInstance();

    private static GameLogic instance = new GameLogic();
    public static GameLogic getInstance() {
        return instance;
    }

    private GameLogic() {}

    public void startMainPage() {
        SwingUtilities.invokeLater(() -> lobbyGui.setVisible(true));
    }

    public void startGamePage() {
        if (lobbyGui != null) {
            lobbyGui.dispose();
            lobbyGui = null;
        }

        this.gameGui = GamePageUI.getInstace();
    }

    public void joinGameAsHost(String nickname) {
        gameState.getMe().setNickName(nickname);
        gameState.getMe().setRole(Role.HOST);
        connection.joinGameAsHost();
    }

    public void joinGameAsGuest(String nickname,String code) {
        gameState.getMe().setNickName(nickname);
        gameState.getMe().setRole(Role.GUEST);
        if (code != null && !code.trim().isEmpty()) {
            connection.joinGameAsGuest(code);
        } else {
            lobbyGui.showGuiMessage("Gecerli bir bağlantı adresi girilmedi.");
        }
    }

    public void cancelHost() {
        connection.cancelServer();
    }

    public void disconnect() {
        connection.closeConnection();
        if (gameGui != null) {
            gameGui.dispose();
        }
    }

    public void sendMessage(String message) {
        if (connection != null) {
            showGameMessage("Siz: " + message);
            connection.sendMessage(message);
        }
    }

    public void handleOpponentInput(String message) {
        showGameMessage("Rakip: " + message);
    }

    public void showGameMessage(String message) {
        if (gameGui != null) {
        } else {
            lobbyGui.showGuiMessage(message);
        }
    }

    public void showWaitingDialog(String ip) {
        lobbyGui.showWaitingDialog(ip);
    }

    public void closeWaitingDialog() {
        lobbyGui.closeWaitingDialog();
    }
}