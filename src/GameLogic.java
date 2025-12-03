package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameLogic {

    private GameUI lobbyGui;
    private GamePageUI gameGui;
    private Connection connection;
    private String nickname;

    public GameLogic() {
        this.lobbyGui = new GameUI(this);
        this.connection = new Connection(this);
    }

    public void start() {
        SwingUtilities.invokeLater(() -> lobbyGui.setVisible(true));
    }

    public void hostGame(String nickname) {
        this.nickname = nickname;
        connection.startGameAsServer();
    }

    public void cancelHost() {
        connection.cancelServer();
    }

    public void joinGame(String nickname,String hostIp) {
        this.nickname = nickname;
        if (hostIp != null && !hostIp.trim().isEmpty()) {
            connection.startGameAsClient(hostIp);
        } else {
            lobbyGui.showGuiMessage("Gecerli bir bağlantı adresi girilmedi.");
        }
    }

    public void startGamePage(boolean isHost) {
        if (lobbyGui != null) {
            lobbyGui.dispose();
            lobbyGui = null;
        }

        this.gameGui = new GamePageUI(this, this.nickname, isHost);
        gameGui.displayStatus("Bağlantı başarılı! Rakibiniz: " + connection.getOpponentIp());
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
            gameGui.displayStatus(message);
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