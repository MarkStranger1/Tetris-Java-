import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginWindow extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private TetrisServer server;

    public LoginWindow() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Не удалось установить Windows Look and Feel: " + e.getMessage());
        }

        try {
            ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Не удалось загрузить значок: " + e.getMessage());
        }

        setTitle("Вход в Tetris");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы

        panel.add(new JLabel("Логин:"));
        loginField = new JTextField();
        panel.add(loginField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Войти");
        registerButton = new JButton("Регистрация");

        loginButton.addActionListener(new LoginAction());
        registerButton.addActionListener(new RegisterAction());

        panel.add(loginButton);
        panel.add(registerButton);

        add(panel);

        // Подключаемся к серверу
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (TetrisServer) registry.lookup("TetrisServer");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения к серверу!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            try {
                if (server.login(login, password)) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Вход выполнен!");
                    dispose();
                    new GameSetupWindow(server, login).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Неверный логин или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LoginWindow.this, "Ошибка соединения!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RegisterAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            dispose();
            new RegistrationWindow(server).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}
