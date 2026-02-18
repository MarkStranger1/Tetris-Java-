import java.awt.Color;

public class Tetromino {
    private Color[][] shape;

    public Tetromino(int type) {
        shape = generateShape(type);
    }

    private Color[][] generateShape(int type) {
        switch (type) {
            case 0: // Квадрат
                return new Color[][]{
                        {Color.YELLOW, Color.YELLOW},
                        {Color.YELLOW, Color.YELLOW}
                };
            case 1: // Линия
                return new Color[][]{
                        {null, Color.CYAN, null, null},
                        {null, Color.CYAN, null, null},
                        {null, Color.CYAN, null, null},
                        {null, Color.CYAN, null, null}
                };
            case 2: // L-образная фигура
                return new Color[][]{
                        {null, Color.ORANGE},
                        {null, Color.ORANGE},
                        {Color.ORANGE, Color.ORANGE}
                };
            case 3: // Z-образная фигура
                return new Color[][]{
                        {Color.RED, Color.RED, null},
                        {null, Color.RED, Color.RED}
                };
            case 4: // Обратная L-образная фигура
                return new Color[][]{
                        {Color.BLUE, null},
                        {Color.BLUE, null},
                        {Color.BLUE, Color.BLUE}
                };
            case 5: // T-образная фигура
                return new Color[][]{
                        {Color.MAGENTA, Color.MAGENTA, Color.MAGENTA},
                        {null, Color.MAGENTA, null}
                };
            case 6: // Обратная Z-образная фигура
                return new Color[][]{
                        {null, Color.GREEN, Color.GREEN},
                        {Color.GREEN, Color.GREEN, null}
                };
            default:
                return new Color[][]{{Color.GRAY}};
        }
    }

    public Color[][] getShape() {
        return shape;
    }

    public int getWidth() {
        return shape[0].length;
    }

    public int getHeight() {
        return shape.length;
    }

    public Tetromino rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        Color[][] rotatedShape = new Color[cols][rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                rotatedShape[x][rows - 1 - y] = shape[y][x];  // Поворот на 90 градусов
            }
        }
        Tetromino rotatedPiece = new Tetromino(0);
        rotatedPiece.shape = rotatedShape;
        return rotatedPiece;
    }
}
