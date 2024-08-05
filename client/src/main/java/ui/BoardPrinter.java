package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;
import java.util.Map;

public class BoardPrinter {

    private static final Map<String, String> PIECE_SYMBOLS = new HashMap<>();

    static {
        PIECE_SYMBOLS.put("WHITE_PAWN", "P");
        PIECE_SYMBOLS.put("WHITE_ROOK", "R");
        PIECE_SYMBOLS.put("WHITE_KNIGHT", "N");
        PIECE_SYMBOLS.put("WHITE_BISHOP", "B");
        PIECE_SYMBOLS.put("WHITE_QUEEN", "Q");
        PIECE_SYMBOLS.put("WHITE_KING", "K");
        PIECE_SYMBOLS.put("BLACK_PAWN", "p");
        PIECE_SYMBOLS.put("BLACK_ROOK", "r");
        PIECE_SYMBOLS.put("BLACK_KNIGHT", "n");
        PIECE_SYMBOLS.put("BLACK_BISHOP", "b");
        PIECE_SYMBOLS.put("BLACK_QUEEN", "q");
        PIECE_SYMBOLS.put("BLACK_KING", "k");
    }

    public void printBoard(ChessBoard board) {
        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    System.out.print(".");
                } else {
                    String symbolKey = piece.getTeamColor().name() + "_" + piece.getPieceType().name();
                    System.out.print(PIECE_SYMBOLS.get(symbolKey));
                }
                System.out.print(" ");
            }
            System.out.println(row);
        }
        System.out.println("a b c d e f g h");
    }

    public void printBoardReversed(ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    System.out.print(".");
                } else {
                    String symbolKey = piece.getTeamColor().name() + "_" + piece.getPieceType().name();
                    System.out.print(PIECE_SYMBOLS.get(symbolKey));
                }
                System.out.print(" ");
            }
            System.out.println(row);
        }
        System.out.println("h g f e d c b a");
    }

    public static class BoardPrinterTest {

        public static void main(String[] args) {
            ChessBoard board1 = new ChessBoard();
            board1.resetBoard();

            ChessBoard board2 = new ChessBoard();
            board2.resetBoard();
            board2.addPiece(new ChessPosition(4, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));

            BoardPrinter printer = new BoardPrinter();

            System.out.println("Initial Board:");
            printer.printBoard(board1);
            System.out.println("\nModified Board:");
            printer.printBoard(board2);
        }
    }

}
