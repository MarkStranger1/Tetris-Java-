import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class LeaderboardWindow extends JFrame {
    public LeaderboardWindow(TetrisServer server) {
        setTitle("Таблица лидеров");
        setSize(300, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea leaderboardArea = new JTextArea();
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("Arial", Font.PLAIN, 14));

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Не удалось установить Windows Look and Feel: " + e.getMessage());
        }

        try {
            List<String> leaderboard = server.getLeaderboard();
            StringBuilder leaderboardText = new StringBuilder("Топ игроков:\n\n");
            for (String entry : leaderboard) {
                leaderboardText.append(entry).append("\n");
            }
            leaderboardArea.setText(leaderboardText.toString());
        } catch (RemoteException e) {
            leaderboardArea.setText("Ошибка загрузки рейтинга.");
        }

        JScrollPane scrollPane = new JScrollPane(leaderboardArea);
        add(scrollPane, BorderLayout.CENTER);
    }
}
