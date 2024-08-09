package client;

import chess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.BoardPrinter;

import java.util.Collection;
import java.util.List;

public class BoardPrinterTest {

    private ChessBoard board;
    private BoardPrinter boardPrinter;

    @BeforeEach
    public void setup() {
        // Initialize the chessboard
        board = new ChessBoard();

        // Initialize the BoardPrinter
        boardPrinter = new BoardPrinter();
    }

    @Test
    @DisplayName("Test Knight Highlighting in the Middle of the Board")
    public void testKnightHighlighting() {
        // Place a White Knight in the middle of the board (d4 or position 4, 4)
        ChessPosition knightPosition = new ChessPosition(4, 4);
        ChessPiece knight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board.addPiece(knightPosition, knight);

        // Get the legal moves for the Knight
        Collection<ChessMove> knightMoves = knight.pieceMoves(board, knightPosition);
        List<ChessPosition> knightPositions = knightMoves.stream()
                .map(ChessMove::getEndPosition)
                .toList();

        // Print the board with the Knight's possible moves highlighted
        System.out.println("Board with Knight's Moves Highlighted:");
        boardPrinter.printBoardWithHighlights(board, knightPositions);
    }

    @Test
    @DisplayName("Test Queen Highlighting in the Middle of the Board")
    public void testQueenHighlighting() {
        // Place a White Queen in the middle of the board (d4 or position 4, 4)
        ChessPosition queenPosition = new ChessPosition(4, 4);
        ChessPiece queen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board.addPiece(queenPosition, queen);

        // Get the legal moves for the Queen
        Collection<ChessMove> queenMoves = queen.pieceMoves(board, queenPosition);
        List<ChessPosition> queenPositions = queenMoves.stream()
                .map(ChessMove::getEndPosition)
                .toList();

        // Print the board with the Queen's possible moves highlighted
        System.out.println("Board with Queen's Moves Highlighted:");
        boardPrinter.printBoardWithHighlights(board, queenPositions);
    }

    @Test
    @DisplayName("Test King Highlighting in the Middle of the Board")
    public void testKingHighlighting() {
        // Place a White King in the middle of the board (d4 or position 4, 4)
        ChessPosition kingPosition = new ChessPosition(4, 4);
        ChessPiece king = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board.addPiece(kingPosition, king);

        // Get the legal moves for the King
        Collection<ChessMove> kingMoves = king.pieceMoves(board, kingPosition);
        List<ChessPosition> kingPositions = kingMoves.stream()
                .map(ChessMove::getEndPosition)
                .toList();

        // Print the board with the King's possible moves highlighted
        System.out.println("Board with King's Moves Highlighted:");
        boardPrinter.printBoardWithHighlights(board, kingPositions);
    }
}
