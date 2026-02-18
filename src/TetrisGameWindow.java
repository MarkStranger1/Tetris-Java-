import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TetrisGameWindow extends JFrame {
    private final TetrisBoard board;
    private final TetrisServer server;
    private Tetromino currentPiece;
    private int pieceX, pieceY;
    private Timer timer;
    private BufferedImage buffer;
    private int score = 0;
    private final String playerLogin;
    private final int cellSize = 30;
    private final int boardWidth;
    private final int boardHeight;
    private final int topMargin = 60; // Отступ сверху для информации об игроке
    private final int borderOffset = 5; // Отступ от границы
    private boolean isPaused = false;
    private JLabel pauseLabel;

    public TetrisGameWindow(TetrisServer server, String playerLogin, int width, int height) {
        this.server = server;
        this.board = new TetrisBoard(width, height);
        this.currentPiece = new Tetromino((int) (Math.random() * 7));
        this.pieceX = width / 2 - 1;
        this.pieceY = 0;
        this.playerLogin = playerLogin;
        this.boardWidth = width * cellSize;
        this.boardHeight = height * cellSize;

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

        setTitle("Тетрис");
        setSize(boardWidth + 2 * borderOffset, boardHeight + topMargin + borderOffset + 5);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Метка паузы
        pauseLabel = new JLabel("Игра на паузе. Нажмите ESC для продолжения.", SwingConstants.CENTER);
        pauseLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pauseLabel.setForeground(Color.RED);
        pauseLabel.setVisible(false);
        add(pauseLabel, BorderLayout.NORTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
                if (!isPaused) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveLeft();
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveRight();
                            break;
                        case KeyEvent.VK_DOWN:
                            moveDown();
                            break;
                        case KeyEvent.VK_UP:
                            rotatePiece();
                            break;
                    }
                    repaint();
                }
            }
        });

        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isPaused) {
                    moveDown();
                    repaint();
                }
            }
        });
        timer.start();

        setFocusable(true);
        setVisible(true);
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseLabel.setVisible(isPaused);

        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }
        repaint();
    }

    private void moveLeft() {
        if (board.canPlace(currentPiece, pieceX - 1, pieceY)) {
            pieceX--;
        }
    }

    private void moveRight() {
        if (board.canPlace(currentPiece, pieceX + 1, pieceY)) {
            pieceX++;
        }
    }

    private void moveDown() {
        if (board.canPlace(currentPiece, pieceX, pieceY + 1)) {
            pieceY++;
        } else {
            board.placePiece(currentPiece, pieceX, pieceY);
            checkFullRows();

            currentPiece = new Tetromino((int) (Math.random() * 7));
            pieceX = board.getGrid()[0].length / 2 - 1;
            pieceY = 0;

            if (!board.canPlace(currentPiece, pieceX, pieceY)) {
                timer.stop();
                endGame();
            }
        }
    }

    public void rotatePiece() {
        Tetromino rotated = currentPiece.rotate();
        if (board.canMove(rotated, pieceX, pieceY)) {
            currentPiece = rotated;
            repaint();
        }
    }

    private void checkFullRows() {
        int clearedRows = board.clearFullRows();
        score += clearedRows;
    }

    private void endGame() {
        int best_score = 0;
        try {
            server.updateBestScore(playerLogin, score);
            best_score = server.getBestScore(playerLogin);
        } catch (RemoteException e) {
            System.err.println("Ошибка при обновлении счета в БД: " + e.getMessage());
        }

        // Создаем окно результата игры
        JDialog resultDialog = new JDialog(this, "Результаты игры", true);
        resultDialog.setLayout(new BorderLayout());
        resultDialog.setSize(175, 175);
        resultDialog.setResizable(false); // Окно фиксированного размера
        resultDialog.setLocationRelativeTo(this);

        // Панель с текстом и кнопками
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Добавляем отступ сверху перед заголовком
        panel.add(Box.createVerticalStrut(10));

        JLabel resultLabel = new JLabel("<html><b>Игра окончена!</b><br>Игрок: " + playerLogin +
                "<br>Счет: " + score + "<br>Рекорд: " + best_score + "</html>", SwingConstants.CENTER);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton leaderboardButton = new JButton("Таблица лидеров");
        JButton okButton = new JButton("OK");

        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Обработчик кнопки "OK"
        okButton.addActionListener(e -> resultDialog.dispose());

        // Обработчик кнопки "Таблица лидеров"
        leaderboardButton.addActionListener(e -> {
            resultDialog.dispose(); // Закрываем окно результата
            new LeaderboardWindow(server).setVisible(true); // Открываем таблицу лидеров
        });

        // Добавляем элементы в панель
        panel.add(resultLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(okButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(leaderboardButton);

        resultDialog.add(panel, BorderLayout.CENTER);
        resultDialog.setVisible(true);

        dispose();
        new GameSetupWindow(server, playerLogin).setVisible(true);
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) buffer.getGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        drawGameStats(g2d);
        drawBoard(g2d);
        drawCurrentPiece(g2d);
        drawBorders(g2d);
        g.drawImage(buffer, 0, 0, this);

        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(borderOffset - 1, topMargin + borderOffset - 1, boardWidth + 1, boardHeight + 1);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String pauseText = "Пауза";
            int textWidth = g.getFontMetrics().stringWidth(pauseText);
            g.drawString(pauseText, (boardWidth - textWidth) / 2, boardHeight / 2);
        }
    }

    private void drawBoard(Graphics g) {
        Color[][] grid = board.getGrid();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] != null) {
                    int drawX = x * cellSize + borderOffset;
                    int drawY = y * cellSize + topMargin + borderOffset;
                    g.setColor(grid[y][x]);
                    g.fillRect(drawX, drawY, cellSize, cellSize);

                    g.setColor(Color.BLACK);
                    g.drawRect(drawX, drawY, cellSize, cellSize);
                }
            }
        }
    }

    private void drawCurrentPiece(Graphics g) {
        Color[][] shape = currentPiece.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != null) {
                    int drawX = (pieceX + j) * cellSize + borderOffset;
                    int drawY = (pieceY + i) * cellSize + topMargin + borderOffset;
                    g.setColor(shape[i][j]);
                    g.fillRect(drawX, drawY, cellSize, cellSize);

                    g.setColor(Color.BLACK);
                    g.drawRect(drawX, drawY, cellSize, cellSize);
                }
            }
        }
    }

    private void drawBorders(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(borderOffset - 1, topMargin + borderOffset - 1, boardWidth + 1, boardHeight + 1);
    }


    private void drawGameStats(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String playerText = "Игрок: " + playerLogin;
        String scoreText = "Счет: " + score;

        FontMetrics fm = g.getFontMetrics();
        int playerTextWidth = fm.stringWidth(playerText);
        int scoreTextWidth = fm.stringWidth(scoreText);

        int centerX = boardWidth / 2;

        g.drawString(playerText, centerX - playerTextWidth / 2, 25);
        g.drawString(scoreText, centerX - scoreTextWidth / 2, 50);
    }
}
