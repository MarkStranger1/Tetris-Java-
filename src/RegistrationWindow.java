import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationWindow extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backButton;
    private TetrisServer server;

    public RegistrationWindow(TetrisServer server) {
        this.server = server;

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

        setTitle("Регистрация");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы

        panel.add(new JLabel("Логин:"));
        loginField = new JTextField();
        loginField.setDocument(new JTextFieldLimit(20)); // Ограничение длины
        panel.add(loginField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        passwordField.setDocument(new JTextFieldLimit(20));
        panel.add(passwordField);

        panel.add(new JLabel("Повторите пароль:"));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setDocument(new JTextFieldLimit(20));
        panel.add(confirmPasswordField);

        registerButton = new JButton("Зарегистрироваться");
        backButton = new JButton("Назад");

        registerButton.addActionListener(new RegisterAction());
        backButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        panel.add(registerButton);
        panel.add(backButton);

        add(panel);
    }

    private class RegisterAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (login.length() < 4 || password.length() < 4) {
                JOptionPane.showMessageDialog(RegistrationWindow.this, "Логин и пароль должны содержать от 4 до 20 символов!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(RegistrationWindow.this, "Пароли не совпадают!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (server.register(login, password)) {
                    JOptionPane.showMessageDialog(RegistrationWindow.this, "Регистрация успешна! Теперь войдите.");
                    dispose();
                    new LoginWindow().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(RegistrationWindow.this, "Ошибка регистрации. Логин уже занят.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(RegistrationWindow.this, "Ошибка соединения с сервером!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Класс для ограничения ввода в JTextField
    private static class JTextFieldLimit extends PlainDocument {
        private final int limit;

        public JTextFieldLimit(int limit) {
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
