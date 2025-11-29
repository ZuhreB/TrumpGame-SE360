package src;

import src.GameLogic;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameLogic logic;

    public Connection(GameLogic logic) {
        this.logic = logic;
    }

    public void startGameAsServer() {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> logic.showGameMessage("Oyun kurucusu oyunu başlattı. Rakip bekleniyor..."));

                try (ServerSocket serverSocket = new ServerSocket(12345)) {
                    String ip = InetAddress.getLocalHost().getHostAddress();
                    SwingUtilities.invokeLater(() -> {
                        logic.showGameMessage("Bağlantı adresiniz: " + ip);
                        logic.showGameMessage("Rakibiniz bu bağlantı adresini kullanarak baglanacak.");
                    });

                    Socket clientSocket = serverSocket.accept(); 
                    
                    SwingUtilities.invokeLater(() -> {
                        logic.showGameMessage("Rakip bağlandı: " + clientSocket.getInetAddress().getHostAddress());
                        try {
                            listenOpponent(clientSocket);
                        } catch (IOException e) {
                             logic.showGameMessage("Oyun başlatılamadı: " + e.getMessage());
                        }
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> logic.showGameMessage("Oyun başlatılamadı: " + e.getMessage()));
            }
        }).start();
    }

    public void startGameAsClient(String hostIp) {
        logic.showGameMessage("Oyuna bağlanılıyor: " + hostIp);
        new Thread(() -> {
            try {
                Socket socket = new Socket(hostIp, 12345);
                SwingUtilities.invokeLater(() -> {
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
}
