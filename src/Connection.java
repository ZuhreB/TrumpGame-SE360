package src;

import src.GameLogic;
import src.GamePage.GamePageLogic;
import src.GamePage.GamePageUI;
import src.Model.Card;
import src.Model.GameState;
import src.Model.Role;
import src.Model.User;
import src.utils.AddressConverter;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
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


                        SwingUtilities.invokeAndWait(() -> {
                            GameLogic.getInstance().closeWaitingDialog();
                            GameLogic.getInstance().startGamePage();
                        });

                        GamePageLogic.getInstance().startHostGame();

                        listenOpponent(clientSocket);
                    } catch (IOException e) {
                        GameLogic.getInstance().showGameMessage("Oyun başlatılamadı: " + e.getMessage());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }


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

                    SwingUtilities.invokeAndWait(() -> {
                        GameLogic.getInstance().startGamePage();
                    });

                    listenOpponent(socket);

                } catch (IOException e) {
                    GameLogic.getInstance().showGameMessage("Oyun başlatılamadı: " + e.getMessage());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

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
                System.out.println("a");
                while (true) {
                    System.out.println("b");

                    Object obj = in.readObject();
                    System.out.println("start");
                    System.out.println("c");

                    if(obj instanceof User user){
                        System.out.println("ife girdi");

                        if(user.getRole()== Role.HOST){
                            System.out.println("host geldi");
                            GameState.getInstance().setOpponent(user);
                            System.out.println(user.getNickName());
                            System.out.println(user.getRole());
                            for(Card card:GameState.getInstance().getOpponent().getBoard_cards()){
                                System.out.print(card.getNumber()+" "+card.getType()+" ");
                            }
                            System.out.println();
                        }else if(user.getRole()==Role.GUEST){
                            System.out.println("guest geldi");
                            System.out.println(user.getRole());
                            GameState.getInstance().setMe(user);
                            for(Card card:GameState.getInstance().getMe().getBoard_cards()){
                                System.out.print(card.getNumber()+" "+card.getType()+" ");
                            }
                            GamePageLogic.getInstance().initTrumpMoment();
                            System.out.println();
                        }
                    }else {
                        String receivedMessage = (String) obj;
                        System.out.println(receivedMessage);
                        if (receivedMessage.startsWith("Koz:")) {
                            String trumpInfo = receivedMessage.split(":")[1];
                            GameState.getInstance().setSecilen_trump(trumpInfo);
                            System.out.println("Rakip kozu belirledi: " + trumpInfo);
                            GameLogic.getInstance().showGameMessage("Koz belirlendi: " + trumpInfo);
                            GameState.getInstance().makeAllCardsVisible();

                            GamePageUI.getInstace().refreshGrids();
                        } else {
                            GameLogic.getInstance().handleOpponentInput(receivedMessage);

                        }
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
                out.reset();
                System.out.println("gönderiliyor"+user);
                out.writeObject(user);
                out.flush();
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