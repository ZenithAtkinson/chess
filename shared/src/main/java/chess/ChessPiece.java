package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        switch (piece.getPieceType()) {
            case BISHOP:
                diagonalMoves(validMoves, board, myPosition);
                break;
            case ROOK:
                directionalMoves(validMoves, board, myPosition);
                break;
            case QUEEN:
                diagonalMoves(validMoves, board, myPosition);
                directionalMoves(validMoves, board, myPosition);
                break;
            case KING:
                kingValidMoves(validMoves, board, myPosition);
                break;
            case KNIGHT:
                knightValidMoves(validMoves, board, myPosition);
                break;
            case PAWN:
                pawnValidMoves(validMoves, board, myPosition);
                break;
        }
        return validMoves;
    }

    private void kingValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int[][] directions = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        addValidMovesInDirections(validMoves, board, myPosition, directions, color, 1);
    }

    private void knightValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int[][] knightMoves = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };
        addValidMovesInDirections(validMoves, board, myPosition, knightMoves, color, 1);
    }

    private void pawnValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int direction = (color == WHITE) ? 1 : -1;
        int startRow = (color == WHITE) ? 2 : 7;
        int promotionRow = (color == WHITE) ? 8 : 1;

        addPawnMoves(validMoves, board, myPosition, direction, startRow, promotionRow, color);
    }

    private void diagonalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        addValidMovesInDirections(validMoves, board, myPosition, directions, null, 8);
    }

    private void directionalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        addValidMovesInDirections(validMoves, board, myPosition, directions, null, 8);
    }

    private void addValidMovesInDirections(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, int[][] directions, ChessGame.TeamColor color, int maxSteps) {
        for (int[] direction : directions) {
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();
            for (int step = 0; step < maxSteps; step++) {
                newRow += direction[0];
                newCol += direction[1];
                if (!moveIsValid(newRow, newCol)) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                if (pieceAtNewPosition != null) {
                    if (color == null || pieceAtNewPosition.getTeamColor() != color) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    private void addPawnMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, int direction, int startRow, int promotionRow, ChessGame.TeamColor color) {
        ChessPosition oneStepForward = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(oneStepForward) == null && moveIsValid(oneStepForward.getRow(), oneStepForward.getColumn())) {
            if (oneStepForward.getRow() == promotionRow) {
                pawnPromotionPieces(validMoves, myPosition, oneStepForward);
            } else {
                validMoves.add(new ChessMove(myPosition, oneStepForward, null));
                if (myPosition.getRow() == startRow) {
                    ChessPosition twoStepsForward = new ChessPosition(oneStepForward.getRow() + direction, oneStepForward.getColumn());
                    if (board.getPiece(twoStepsForward) == null && moveIsValid(twoStepsForward.getRow(), twoStepsForward.getColumn())) {
                        validMoves.add(new ChessMove(myPosition, twoStepsForward, null));
                    }
                }
            }
        }

        int[][] diagonals = {{direction, 1}, {direction, -1}};
        for (int[] diagonal : diagonals) {
            ChessPosition diagonalPosition = new ChessPosition(myPosition.getRow() + diagonal[0], myPosition.getColumn() + diagonal[1]);
            if (moveIsValid(diagonalPosition.getRow(), diagonalPosition.getColumn()) && board.getPiece(diagonalPosition) != null && board.getPiece(diagonalPosition).getTeamColor() != color) {
                if (diagonalPosition.getRow() == promotionRow) {
                    pawnPromotionPieces(validMoves, myPosition, diagonalPosition);
                } else {
                    validMoves.add(new ChessMove(myPosition, diagonalPosition, null));
                }
            }
        }
    }

    private boolean moveIsValid(int row, int col) {
        return (row > 0 && row <= 8) && (col > 0 && col <= 8);
    }

    private void pawnPromotionPieces(Collection<ChessMove> validMoves, ChessPosition startPosition, ChessPosition endPosition) {
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.QUEEN));
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.BISHOP));
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.KNIGHT));
        validMoves.add(new ChessMove(startPosition, endPosition, PieceType.ROOK));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
