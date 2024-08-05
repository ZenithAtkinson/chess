package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;
import java.util.Map;

public class BoardPrinter {

    private static final Map<String, String> PIECE_SYMBOLS = new HashMap<>();

    static {
        PIECE_SYMBOLS.put("WHITE_PAWN", EscapeSequences.SET_TEXT_COLOR_RED + " ♙ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("WHITE_ROOK", EscapeSequences.SET_TEXT_COLOR_RED + " ♖ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("WHITE_KNIGHT", EscapeSequences.SET_TEXT_COLOR_RED + " ♘ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("WHITE_BISHOP", EscapeSequences.SET_TEXT_COLOR_RED + " ♗ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("WHITE_QUEEN", EscapeSequences.SET_TEXT_COLOR_RED + " ♕ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("WHITE_KING", EscapeSequences.SET_TEXT_COLOR_RED + " ♔ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("BLACK_PAWN", EscapeSequences.SET_TEXT_COLOR_BLUE + " ♟ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("BLACK_ROOK", EscapeSequences.SET_TEXT_COLOR_BLUE + " ♜ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("BLACK_KNIGHT", EscapeSequences.SET_TEXT_COLOR_BLUE + " ♞ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("BLACK_BISHOP", EscapeSequences.SET_TEXT_COLOR_BLUE + " ♝ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("BLACK_QUEEN", EscapeSequences.SET_TEXT_COLOR_BLUE + " ♛ " + EscapeSequences.RESET_TEXT_COLOR);
        PIECE_SYMBOLS.put("BLACK_KING", EscapeSequences.SET_TEXT_COLOR_BLUE + " ♚ " + EscapeSequences.RESET_TEXT_COLOR);
    }

    private void printRowNumbers(int row) {
        System.out.print(" " + row + " ");
    }

    private void printColumnLetters(String order) {
        System.out.print("   ");
        for (char ch : order.toCharArray()) {
            System.out.print(" " + ch + "  ");
        }
        System.out.println();
    }

    private void printCell(ChessBoard board, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = board.getPiece(position);
        boolean isDarkSquare = (row + col) % 2 == 0;
        String backgroundColor;
        if (isDarkSquare) {
            backgroundColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        } else {
            backgroundColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        }

        String pieceSymbol;
        if (piece == null) {
            pieceSymbol = EscapeSequences.EMPTY;
        } else {
            pieceSymbol = PIECE_SYMBOLS.get(piece.getTeamColor().name() + "_" + piece.getPieceType().name());
        }

        System.out.print(backgroundColor + pieceSymbol + EscapeSequences.RESET_BG_COLOR);
    }

    public void printBoard(ChessBoard board) {
        printColumnLetters("abcdefgh");
        for (int row = 8; row >= 1; row--) {
            printRowNumbers(row);
            for (int col = 1; col <= 8; col++) {
                printCell(board, row, col);
            }
            System.out.println(" " + row);
        }
        printColumnLetters("abcdefgh");
    }

    public void printBoardReversed(ChessBoard board) {
        printColumnLetters("hgfedcba");
        for (int row = 1; row <= 8; row++) {
            printRowNumbers(row);
            for (int col = 8; col >= 1; col--) {
                printCell(board, row, col);
            }
            System.out.println(" " + row);
        }
        printColumnLetters("hgfedcba");
    }
}
