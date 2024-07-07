package chess;

import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;

import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 * test
 */
public class ChessBoard {
    private ChessPiece squares[][] = new ChessPiece[8][8];
    public ChessBoard() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //piece = squares[position.getRow()-1][position.getColumn()-1];
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ROOK);
        for (int i = 0; i <= 7; i++){
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, PAWN);
        }

        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ROOK);
        for (int i = 0; i <= 7; i++){
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, PAWN);
        }

    }
}
