package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //Looking for all valid moves for a given piece.
            //When finding valid positions, DONT add a spot where a team piece is, but DO add a spot where an enemy piece is (as technically it is a capture)

        //First check for piece type (from board, given position)
        //Call appropriate function ( see below)
        //Return valid moves, then return whatever the function returns (so return whatever KingMovesCalcualor returns)
        //return KingMovesCalculator(board, position)


        return new ArrayList<>(); //TEMPORARY, needs to be full of the ChessMove class.

    }

    public Collection<ChessMove> KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        new ArrayList<>();

        //Add the surrounding spaces to the king based on its position, IF they are valid (not taken up by a teammate).
            //Spots occupied by an enemy ARE valid, but spots beyond it are NOT.

        return ArrayList<>();
    }

    public Collection<ChessMove> QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) { }

    public Collection<ChessMove> KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) { }

    public Collection<ChessMove> PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) { }

    public Collection<ChessMove> BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) { }

    public Collection<ChessMove> RookMovesCalculator(ChessBoard board, ChessPosition myPosition) { }
}
