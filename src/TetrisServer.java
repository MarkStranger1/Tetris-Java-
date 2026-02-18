import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TetrisServer extends Remote {
    boolean register(String login, String password) throws RemoteException;
    boolean login(String login, String password) throws RemoteException;
    int getBestScore(String login) throws RemoteException;
    void updateBestScore(String login, int score) throws RemoteException;
    List<String> getLeaderboard() throws RemoteException;
}