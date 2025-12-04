package src;

import src.GameLogic;
import src.Model.GameState;
import src.Model.Role;
import src.Model.User;
import src.utils.AddressConverter;

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
    private ServerSocket serverSocket;
    private Socket activeSocket;

    private GameLogic logic=GameLogic.getInstance();
    private GameState gameState = GameState.getInstance();

    private static Connection instance = new Connection();

    private Connection() {
    }

    public static Connection getInstance() {
        return instance;
    }


    public String joinGameAsHost() {
        try {
            serverSocket = new ServerSocket(12345);
            String ip = InetAddress.getLocalHost().getHostAddress();

            new Thread(() -> {
                try {
                    SwingUtilities.invokeLater(() -> logic.showWaitingDialog(AddressConverter.addressToCode(ip)));

                    Socket clientSocket = serverSocket.accept();
                    this.activeSocket = clientSocket;

                    SwingUtilities.invokeLater(() -> {
                        logic.closeWaitingDialog();
                        logic.startGamePage();

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

    public void joinGameAsGuest(String code) {
        logic.showGameMessage("Oyuna bağlanılıyor: " + code);
        new Thread(() -> {
            try {
                Socket socket = new Socket(AddressConverter.codeToAddress(code), 12345);
                this.activeSocket = socket;

                SwingUtilities.invokeLater(() -> {
                    logic.startGamePage();

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
                    Object obj = in.readObject();

                    if(obj instanceof User user){
                        if(user.getRole()== Role.HOST){
                            gameState.setOpponent(user);
                        }else if(user.getRole()==Role.GUEST){
                            gameState.setMe(user);
                        }
                    }else{
                        String receivedMessage = (String) obj;
                        logic.handleOpponentInput(receivedMessage);
                    }

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

    public void sendUserObject(User user){
        if(out!=null){
            try{
                out.writeObject(user);
            }catch (IOException e){
                logic.showGameMessage("Bilgiler gönderilemedi:" + e.getMessage());
            }
        }
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