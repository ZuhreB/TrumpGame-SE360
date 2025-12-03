package src;

import src.GameLogic;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Connection {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameLogic logic;
    private ServerSocket serverSocket;
    private Socket activeSocket;

    public Connection(GameLogic logic) {
        this.logic = logic;
    }

    public String startGameAsServer() {
        try {
            serverSocket = new ServerSocket(12345);
            String ip = InetAddress.getLocalHost().getHostAddress();

            new Thread(() -> {
                try {
                    SwingUtilities.invokeLater(() -> logic.showWaitingDialog(ip));

                    Socket clientSocket = serverSocket.accept();
                    this.activeSocket = clientSocket;

                    SwingUtilities.invokeLater(() -> {
                        logic.closeWaitingDialog();
                        logic.startGamePage(true);

                        try {
                            listenOpponent(clientSocket);
                        } catch (IOException e) {
                            logic.showGameMessage("Oyun başlatılamadı: " + e.getMessage());
                        }
                    });

                } catch (SocketException e) {
                    SwingUtilities.invokeLater(() -> {
                        logic.closeWaitingDialog();
                        logic.showGameMessage("Oyun kurulumu iptal edildi.");
                    });
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> logic.showGameMessage("Oyun başlatılamadı: " + e.getMessage()));
                }
            }).start();
            return ip;
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> logic.showGameMessage("Oyun başlatılamadı: " + e.getMessage()));
            return null;
        }
    }

    public void cancelServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logic.showGameMessage("Sunucu kapatılırken bir hata oluştu: " + e.getMessage());
        }
    }

    public void startGameAsClient(String hostIp) {
        logic.showGameMessage("Oyuna bağlanılıyor: " + hostIp);
        new Thread(() -> {
            try {
                Socket socket = new Socket(hostIp, 12345);
                this.activeSocket = socket;

                SwingUtilities.invokeLater(() -> {
                    logic.startGamePage(false);

                    try {
                        listenOpponent(socket);
                    } catch (IOException e) {
                        logic.showGameMessage("Oyun başlatılamadı: " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> logic.showGameMessage("Oyuna bağlanılamadı: " + e.getMessage()));
            }
        }).start();
    }

    private void listenOpponent(Socket socket) throws IOException {
        assignStreams(socket);
        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    String receivedMessage = (String) in.readObject();
                    logic.handleOpponentInput(receivedMessage);
                }
            } catch (Exception e) {
                logic.showGameMessage("Rakibin bağlantısı koptu.");
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void assignStreams(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        logic.showGameMessage("Bağlantı başarılı oynayabilirsiniz");
    }

    public void sendMessage(String message) {
        if (out != null) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                logic.showGameMessage("Mesaj gönderilemedi:" + e.getMessage());
            }
        }
    }

    public String getOpponentIp() {
        if (activeSocket != null) {
            return activeSocket.getInetAddress().getHostAddress();
        }
        return "Bilinmiyor";
    }

    public void closeConnection() {
        try {
            if (activeSocket != null && !activeSocket.isClosed()) {
                activeSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logic.showGameMessage("Bağlantılar kapatılırken hata oluştu: " + e.getMessage());
        }
    }
}