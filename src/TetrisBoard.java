import java.awt.Color;

public class TetrisBoard {
    private final int width, height;
    private final Color[][] grid;

    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Color[height][width]; // Создаем пустое игровое поле
    }

    public boolean canMove(Tetromino piece, int newX, int newY) {
        int width = piece.getWidth();
        int height = piece.getHeight();
        Color[][] shape = piece.getShape();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (shape[y][x] != null) {
                    int boardX = newX + x;
                    int boardY = newY + y;

                    // Проверяем выход за границы
                    if (boardX < 0 || boardX >= this.width || boardY >= this.height) {
                        return false;
                    }
                    // Проверяем столкновение с уже установленными блоками
                    if (grid[boardY][boardX] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean canPlace(Tetromino piece, int x, int y) {
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i][j] != null) {
                    int newX = x + j;
                    int newY = y + i;
                    if (newX < 0 || newX >= width || newY >= height || (newY >= 0 && grid[newY][newX] != null)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placePiece(Tetromino piece, int x, int y) {
        for (int i = 0; i < piece.getHeight(); i++) {
            for (int j = 0; j < piece.getWidth(); j++) {
                if (piece.getShape()[i][j] != null) {
                    grid[y + i][x + j] = piece.getShape()[i][j];
                }
            }
        }
    }

    public int clearFullRows() {
        int cleared = 0;
        for (int r = 0; r < height; r++) {
            boolean full = true;
            for (int c = 0; c < width; c++) {
                if (grid[r][c] == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                clearRow(r);
                cleared++;
            }
        }
        return cleared;
    }

    private void clearRow(int row) {
        for (int r = row; r > 0; r--) {
            System.arraycopy(grid[r - 1], 0, grid[r], 0, width);
        }
        grid[0] = new Color[width]; // Очистить верхнюю строку
    }

    public Color[][] getGrid() {
        return grid;
    }
}
