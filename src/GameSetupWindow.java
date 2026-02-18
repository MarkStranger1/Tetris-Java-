import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameSetupWindow extends JFrame {
    private JComboBox<Integer> widthBox, heightBox;
    private JButton startButton, rulesButton;
    private String playerLogin;
    private TetrisServer server;

    public GameSetupWindow(TetrisServer server, String playerLogin) {
        this.server = server;
        this.playerLogin = playerLogin;

        setLookAndFeel();
        setTitle("Настройки игры");
        setSize(320, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIcon();

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Ширина поля:"));
        widthBox = new JComboBox<>(new Integer[]{5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20});
        widthBox.setSelectedItem(12);
        panel.add(widthBox);

        panel.add(new JLabel("Высота поля:"));
        heightBox = new JComboBox<>(new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30});
        heightBox.setSelectedItem(20);
        panel.add(heightBox);

        startButton = new JButton("Начать игру");
        startButton.addActionListener(new StartGameAction());
        panel.add(startButton);

        rulesButton = new JButton("Правила");
        rulesButton.addActionListener(new ShowRulesAction());
        panel.add(rulesButton);

        add(panel);
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Не удалось установить Windows Look and Feel: " + e.getMessage());
        }
    }

    private void setIcon() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Не удалось загрузить значок: " + e.getMessage());
        }
    }

    private class StartGameAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int width = (int) widthBox.getSelectedItem();
            int height = (int) heightBox.getSelectedItem();

            dispose();
            new TetrisGameWindow(server, playerLogin, width, height).setVisible(true);
        }
    }

    private class ShowRulesAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFrame rulesFrame = new JFrame("Правила игры");
            rulesFrame.setSize(500, 450);
            rulesFrame.setResizable(false);
            rulesFrame.setLocationRelativeTo(null);
            rulesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            setIcon();
            setLookAndFeel();

            JTextArea rulesText = new JTextArea(
                    "Эту игру \"Тетрис\" выполнил студент Самарского университета Титов Артем Сергеевич " +
                            "группы 6401-020302D в 2025 году в рамках курсового проекта по дисциплине " +
                            "\"Объектно-распределенная обработка\".\n\n" +
                            "Приложение представляет из себя классическую игру \"Тетрис\". " +
                            "Основная задача - заполнить как можно больше линий снизу вверх с помощью случайных фигур, " +
                            "падающих сверху. Как только линия заполняется, то она пропадает и игроку добавляется один бал " +
                            "в счет.\n\n" +
                            "Фигурами можно управлять с помощью клавиш со стрелками и специальных клавиш:\n" +
                            "→ - передвинет фигуру вправо на 1 ячейку\n" +
                            "← - передвинет фигуру влево на 1 ячейку\n" +
                            "↓ - сбросит фигуру вниз\n" +
                            "↑ - повернет фигуру на 90° по часовой стрелке\n" +
                            "Esc - поставит игру на паузу\n\n" +
                            "В конце игры можно посмотреть статистику за текущую игру и свой рекорд за все время."
            );
            rulesText.setFont(new Font("Arial", Font.PLAIN, 14));
            rulesText.setWrapStyleWord(true);
            rulesText.setLineWrap(true);
            rulesText.setEditable(false);
            rulesText.setMargin(new Insets(10, 10, 10, 10));

            rulesFrame.add(new JScrollPane(rulesText));
            rulesFrame.setVisible(true);
        }
    }
}