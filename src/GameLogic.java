package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameLogic {

    private GameUI gui;
    private Connection connection;

    public GameLogic() {
        this.gui = new GameUI(this);
        this.connection = new Connection(this);
    }

    public void start() {
        SwingUtilities.invokeLater(() -> gui.setVisible(true));
        gui.showGuiMessage("TrumpGame Oyununa hos geldiniz!");

        String[] options = {"Oyunu Kur (Oda sahibi)", "Oyuna Katil (Misafir)"};
        int choice = JOptionPane.showOptionDialog(
                gui,
                "Oyun kuracak mısınız, yoksa bir oyuna mı katılacaksınız?",
                "Rol Seçimi",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            gui.setTitle("Trump Game - Oyuncu 1 (Oda sahibi)");
            connection.startGameAsServer();
        } else if (choice == 1) {
            gui.setTitle("Trump Game - Oyuncu 2 (Misafir)");
            String hostIp = JOptionPane.showInputDialog(gui, "Oyunun bağlantı adresini girin:");
            if (hostIp != null && !hostIp.trim().isEmpty()) {
                connection.startGameAsClient(hostIp);
            } else {
                gui.showGuiMessage("Gecerli bir bağlantı adresi girilmedi. Oyun kapanıyor.");
                new Thread(() -> { try { Thread.sleep(3000); } catch (InterruptedException e) {} System.exit(0); }).start();
            }
        } else {
            System.exit(0);
        }
    }

    public void handleUserInput(String message) {
        gui.showGuiMessage("Ben: " + message);
        connection.sendMessage(message);
    }

    public void handleOpponentInput(String message) {
        gui.showGuiMessage("Rakip: " + message);
    }

    public void showGameMessage(String message) {
        gui.showGuiMessage(message);
    }

    public static void main(String[] args) {
        GameLogic game = new GameLogic();
        game.start();
    }
}
