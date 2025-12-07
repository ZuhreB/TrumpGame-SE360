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
                    SwingUtilities.invokeLater(() -> GameLogic.getInstance().showWaitingDialog(AddressConverter.addressToCode(ip)));

                    Socket clientSocket = serverSocket.accept();
                    this.activeSocket = clientSocket;
                    try {
                        assignStreams(clientSocket);
                        listenOpponent(clientSocket);
                    } catch (IOException e) {
                        GameLogic.getInstance().showGameMessage("Oyun başlatılamadı: " + e.getMessage());
                    }


                    SwingUtilities.invokeLater(() -> {
                        GameLogic.getInstance().closeWaitingDialog();
                        GameLogic.getInstance().startGamePage();


                    });

                } catch (SocketException e) {
                    SwingUtilities.invokeLater(() -> {
                        GameLogic.getInstance().closeWaitingDialog();
                        GameLogic.getInstance().showGameMessage("Oyun kurulumu iptal edildi.");
                    });
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> GameLogic.getInstance().showGameMessage("Oyun başlatılamadı: " + e.getMessage()));
                }
            }).start();
            return ip;
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> GameLogic.getInstance().showGameMessage("Oyun başlatılamadı: " + e.getMessage()));
            return null;
        }
    }

    public void cancelServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            GameLogic.getInstance().showGameMessage("Sunucu kapatılırken bir hata oluştu: " + e.getMessage());
        }
    }

    public void joinGameAsGuest(String code) {
        GameLogic.getInstance().showGameMessage("Oyuna bağlanılıyor: " + code);
        new Thread(() -> {
            try {
                Socket socket = new Socket(AddressConverter.codeToAddress(code), 12345);
                this.activeSocket = socket;
                try {
                    assignStreams(socket);
                    listenOpponent(socket);
                } catch (IOException e) {
                    GameLogic.getInstance().showGameMessage("Oyun başlatılamadı: " + e.getMessage());
                }
                SwingUtilities.invokeLater(() -> {
                    GameLogic.getInstance().startGamePage();
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> GameLogic.getInstance().showGameMessage("Oyuna bağlanılamadı: " + e.getMessage()));
            }
        }).start();
    }

    private void listenOpponent(Socket socket) throws IOException {
        System.out.println("assign bitti");

        Thread listenerThread = new Thread(() -> {
            try {
                System.out.println(out);
                while (true) {
                    Object obj = in.readObject();
                    System.out.println("start");

                    if(obj instanceof User user){
                        System.out.println("ife girdi");

                        if(user.getRole()== Role.HOST){
                            System.out.println("host geldi");

                            GameState.getInstance().setOpponent(user);
                            System.out.println(user.getNickName());
                            System.out.println(user.getRole());
                            System.out.println(user.getBoard_cards().toArray().toString());

                        }else if(user.getRole()==Role.GUEST){
                            System.out.println("guest geldi");
                            System.out.println(user.getRole());
                            System.out.println(user.getBoard_cards().toArray().toString());
                            System.out.println(user.getHand_cards().toArray().toString());


                            GameState.getInstance().setMe(user);
                        }
                    }else{
                        String receivedMessage = (String) obj;
                        System.out.println(receivedMessage);
                        GameLogic.getInstance().handleOpponentInput(receivedMessage);
                    }

                }
            } catch (Exception e) {
                GameLogic.getInstance().showGameMessage("Rakibin bağlantısı koptu.");
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void assignStreams(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        GameLogic.getInstance().showGameMessage("Bağlantı başarılı oynayabilirsiniz");
        System.out.println("assign edildi");

    }

    public void sendMessage(String message) {
        if (out != null) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                GameLogic.getInstance().showGameMessage("Mesaj gönderilemedi:" + e.getMessage());
            }
        }
    }

    public void sendUserObject(User user){
        System.out.println("send user objecte girdi");

        if(out!=null){
            System.out.println("out null değil");
            try{
                System.out.println("gönderiliyor"+user);
                out.writeObject(user);
            }catch (IOException e){
                GameLogic.getInstance().showGameMessage("Bilgiler gönderilemedi:" + e.getMessage());
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
            GameLogic.getInstance().showGameMessage("Bağlantılar kapatılırken hata oluştu: " + e.getMessage());
        }
    }
}