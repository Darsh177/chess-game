package main;
import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.nio.file.*;

public class Board extends JPanel {
    public int tilesize = 80;
    public int rows = 8;
    public int cols = 8;
    ArrayList<Piece> pieceList = new ArrayList<>();

    public Piece selectedPiece;
    Input input = new Input(this);
    public CheckScanner checkScanner = new CheckScanner(this);

    public int enPassantTile = -1;
    private boolean isWhiteToMove = true;
    private boolean isGameOver = false;

    public Board() {
        this.setPreferredSize(new Dimension(cols * tilesize, rows * tilesize));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        Path path= Paths.get("gamestate.txt");
        if (Files.exists(path)){
            int choice = JOptionPane.showConfirmDialog(this, "Do you want to continue the last game?", "Continue From Last Game", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                restoresavedgame();
            } else {
                addPieces();
            }
        }else {
            addPieces();
        }
    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieceList) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {
        if (move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else if (move.piece.name.equals("King")) {

            moveKing((move));
        }
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xpos = move.newCol * tilesize;
        move.piece.ypos = move.newRow * tilesize;
        move.piece.isFirstMove = false;
        capture(move.capture);
        isWhiteToMove = !isWhiteToMove;
        updateGameState();
        savegamestate();

    }
    private void moveKing(Move move){
            if (Math.abs(move.piece.col - move.newCol)==2){
                Piece rook ;
                if (move.piece.col<move.newCol)
                {
                    rook= getPiece(7 , move.piece.row);
                    rook.col=5;
                }else {
                    rook= getPiece(0 , move.piece.row);
                    rook.col=3;
                }
                rook.xpos = rook.col * tilesize;
            }



    }
    private void movePawn(Move move) {
        int colorIndex = move.piece.isWhite ? 1 : -1;
        if (getTileNum(move.newCol, move.newRow) == enPassantTile) {
            move.capture = getPiece(move.newCol, move.newRow + colorIndex);

        }
        if (Math.abs(move.piece.row - move.newRow) == 2) {
            enPassantTile = getTileNum(move.newCol, move.newRow + colorIndex);

        } else {
            enPassantTile = -1;
        }
//promotions
        colorIndex = move.piece.isWhite ? 0 : 7;
        if (move.newRow == colorIndex) {
            promotePawn(move);
        }


    }

    private void promotePawn(Move move){
        pieceList.add (new Queen (this, move.newCol,move.newRow,move.piece.isWhite));
        capture(move.piece);
    }


    public void capture(Piece piece) {
        pieceList.remove(piece);
    }

    public boolean isValidMove(Move move) {
        if (isGameOver) {
            return false;
        }
        if (move.piece.isWhite != isWhiteToMove) {
            return false;
        }
        if (sameTeam(move.piece, move.capture)) {
            return false;
        }
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }

        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
            return false;
        }
        if(checkScanner.isKingChecked(move)){
            return false;
        }
        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }
    Piece findKing (boolean isWhite) {

        for (Piece piece : pieceList) {

            if (isWhite == piece.isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;

    }
    public int getTileNum(int col , int row){
        return row * row + col;
    }


    public void addPieces(){
        pieceList.add(new Knight(this, 1,0,false ));
        pieceList.add(new Knight(this, 6,0,false));
        pieceList.add(new Rook(this, 0, 0, false));
        pieceList.add(new Rook(this, 7, 0, false));
        pieceList.add(new Bishop(this, 2, 0, false));
        pieceList.add(new Bishop(this, 5, 0, false));
        pieceList.add(new King(this, 4, 0 , false));
        pieceList.add(new Queen(this, 3, 0, false));

        pieceList.add(new Pawn(this,0,1,false));
        pieceList.add(new Pawn(this,1,1,false));
        pieceList.add(new Pawn(this,2,1,false));
        pieceList.add(new Pawn(this,3,1,false));
        pieceList.add(new Pawn(this,4,1,false));
        pieceList.add(new Pawn(this,5,1,false));
        pieceList.add(new Pawn(this,6,1,false));
        pieceList.add(new Pawn(this,7,1,false));


        pieceList.add(new Knight(this, 1,7,true ));
        pieceList.add(new Knight(this, 6,7,true));
        pieceList.add(new Rook(this, 0, 7, true));
        pieceList.add(new Rook(this, 7, 7, true));
        pieceList.add(new Bishop(this, 2, 7, true));
        pieceList.add(new Bishop(this, 5, 7, true));
        pieceList.add(new King(this, 4, 7 , true));
        pieceList.add(new Queen(this, 3, 7, true));

        pieceList.add(new Pawn(this,0,6,true));
        pieceList.add(new Pawn(this,1,6,true));
        pieceList.add(new Pawn(this,2,6,true));
        pieceList.add(new Pawn(this,3,6,true));
        pieceList.add(new Pawn(this,4,6,true));
        pieceList.add(new Pawn(this,5,6,true));
        pieceList.add(new Pawn(this,6,6,true));
        pieceList.add(new Pawn(this,7,6,true));
    }
    private void updateGameState() {
        Piece king = findKing(isWhiteToMove);
        if (checkScanner.isGameOver(king)) {
            if (checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
                System.out.println(isWhiteToMove ? "Black Wins!" : "White Wins!");
            } else {
                System.out.println("Stalemate!");
            }

            isGameOver = true;
            Path path = Paths.get("gamestate.txt");
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        else if (insufficientMaterial(true) && insufficientMaterial(false)){
            System.out.println("Insufficient Material!");
            isGameOver = true;
            Path path = Paths.get("gamestate.txt");
            try {
                Files.deleteIfExists(path);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private boolean insufficientMaterial(boolean isWhite) {
        ArrayList<String> names = pieceList.stream()
                .filter(p -> p.isWhite == isWhite)
                .map(p -> p.name)
                .collect(Collectors.toCollection(ArrayList::new));

        if (names.contains("Queen") || names.contains("Rook") || names.contains("Pawn")) {
            return false;
        }

        return names.size() < 3;
    }
        @Override
    public void paintComponent(Graphics graphics) {
      Graphics2D graphics2D=(Graphics2D) graphics;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                graphics2D.setColor((c + r) % 2 == 0 ? Color.white : Color.gray);
                graphics2D.fillRect(c * tilesize, r * tilesize, tilesize, tilesize);
            }
        }

    if (selectedPiece != null) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (isValidMove(new Move(this, selectedPiece, c, r))) {
                    graphics2D.setColor(new Color(68, 180, 57, 190));
                    graphics2D.fillRect(c * tilesize, r * tilesize, tilesize, tilesize);
                }
            }
        }
    }
        for(Piece piece : pieceList){
            piece.paint(graphics2D) ;
        }

    }
    public void savegamestate(){
        try {
            new FileWriter("gamestate.txt", false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int n = 0;
        for (Piece piece : pieceList) {
            try (FileWriter writer = new FileWriter("gamestate.txt", true)) {
                n = 0;
                if (piece.name.equals("Knight")) n = 1;
                writer.write( (piece.isWhite? piece.name.toUpperCase().charAt(n) : piece.name.toLowerCase().charAt(n)) + "," + piece.row + "," + piece.col + "\n");
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void restoresavedgame(){
        Path path = Paths.get("gamestate.txt");
        try {
            java.util.List<String> lines = null;
            try {
                lines = Files.readAllLines(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String line : lines) {
                String[] parts = line.split(",");
                int col = Integer.parseInt(parts[1]);
                int row = Integer.parseInt(parts[2]);
                switch (parts[0]) {
                    case "p" -> pieceList.add(new Pawn(this,row, col, false));
                    case "r" -> pieceList.add(new Rook(this, row, col, false));
                    case "n" -> pieceList.add(new Knight(this, row, col, false));
                    case "b" -> pieceList.add(new Bishop(this, row, col, false));
                    case "q" -> pieceList.add(new Queen(this, row, col, false));
                    case "k" -> pieceList.add(new King(this, row, col, false));
                    case "P" -> pieceList.add(new Pawn(this, row, col, true));
                    case "R" -> pieceList.add(new Rook(this, row, col, true));
                    case "N" -> pieceList.add(new Knight(this, row, col, true));
                    case "B" -> pieceList.add(new Bishop(this, row, col, true));
                    case "Q" -> pieceList.add(new Queen(this, row, col, true));
                    case "K" -> pieceList.add(new King(this, row, col, true));
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
