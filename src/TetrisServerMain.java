import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TetrisServerMain {
    public static void main(String[] args) {
        try {
            TetrisServer server = new TetrisServerImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("TetrisServer", server);
            System.out.println("Сервер Tetris запущен!");
        } catch (Exception e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }
}
