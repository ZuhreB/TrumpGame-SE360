package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameLogic {

    private GameUI gui;
    private Connection connection;
    private String nickname;

    public GameLogic() {
        this.gui = new GameUI(this);
        this.connection = new Connection(this);
    }

    public void start() {
        SwingUtilities.invokeLater(() -> gui.setVisible(true));
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
            gui.showGuiMessage("Gecerli bir bağlantı adresi girilmedi.");
        }
    }


    public void handleOpponentInput(String message) {
        gui.showGuiMessage("Rakip: " + message);
    }

    public void showGameMessage(String message) {
        gui.showGuiMessage(message);
    }
    
    public void showWaitingDialog(String ip) {
        gui.showWaitingDialog(ip);
    }

    public void closeWaitingDialog() {
        gui.closeWaitingDialog();
    }
}
