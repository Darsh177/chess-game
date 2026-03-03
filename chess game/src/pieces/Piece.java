package pieces;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import main.*;

public class Piece {
    public int col, row;
    public int xpos, ypos;
    public boolean isWhite;
    public String name;
    public int value;

    public boolean isFirstMove = true;

    BufferedImage sheet;

    {
        try {
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("res/pieces.png"));
            if (sheet == null) {
                throw new IOException("Image not found: /res/pieces.png");
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    protected int sheetScale= sheet.getWidth()/6;

    Image sprite;
    Board board;
    public Piece(Board board){
        this.board= board;
    }

    public boolean isValidMovement(int col, int row) {
        return true;
    }
    public boolean moveCollidesWithPiece(int col, int row) {
        return false;
    }

    public void paint(Graphics2D graphics2D){
        graphics2D.drawImage(sprite, xpos, ypos, null);
    }

}
