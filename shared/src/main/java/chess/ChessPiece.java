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
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
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

        switch (type) {
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
        addSingleStepMoves(validMoves, board, myPosition, new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}});
    }

    private void knightValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        addSingleStepMoves(validMoves, board, myPosition, new int[][]{{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}});
    }

    private void pawnValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int direction = (color == WHITE) ? 1 : -1;
        int startRow = (color == WHITE) ? 2 : 7;
        int promotionRow = (color == WHITE) ? 8 : 1;

        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(newPosition) == null && moveIsValid(newPosition.getRow(), newPosition.getColumn())) {
            pawnAddMoves(validMoves, myPosition, newPosition);
            newPosition = new ChessPosition(newPosition.getRow() + direction, newPosition.getColumn());
            if (myPosition.getRow() == startRow && board.getPiece(newPosition) == null) {
                pawnAddMoves(validMoves, myPosition, newPosition);
            }
        }

        addPawnCaptureMoves(validMoves, board, myPosition, direction, promotionRow);
    }

    private void addPawnCaptureMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, int direction, int promotionRow) {
        int[] captureColumns = {1, -1};
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        for (int captureColumn : captureColumns) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + captureColumn);
            if (moveIsValid(newPosition.getRow(), newPosition.getColumn()) && board.getPiece(newPosition) != null &&
                    board.getPiece(newPosition).getTeamColor() != color) {
                pawnAddMoves(validMoves, myPosition, newPosition);
            }
        }
    }

    private void addSingleStepMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, int[][] directions) {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        for (int[] direction : directions) {
            int newRow = myPosition.getRow() + direction[0];
            int newCol = myPosition.getColumn() + direction[1];
            if (moveIsValid(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private void diagonalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        addSlidingMoves(validMoves, board, myPosition, new int[][]{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}});
    }

    private void directionalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        addSlidingMoves(validMoves, board, myPosition, new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}});
    }

    private void addSlidingMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition, int[][] directions) {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        for (int[] direction : directions) {
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();
            while (true) {
                newRow += direction[0];
                newCol += direction[1];
                if (!moveIsValid(newRow, newCol)) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() != color) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
                validMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    private boolean moveIsValid(int row, int col) {
        return row > 0 && row <= 8 && col > 0 && col <= 8;
    }

    private void pawnAddMoves(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessPosition newPosition) {
        if (newPosition.getRow() == 8 || newPosition.getRow() == 1) {
            pawnPromotionPieces(validMoves, myPosition, newPosition);
        } else {
            validMoves.add(new ChessMove(myPosition, newPosition, null));
        }
    }

    private void pawnPromotionPieces(Collection<ChessMove> validMoves, ChessPosition startPosition, ChessPosition endPosition) {
        for (PieceType promotionType : new PieceType[]{PieceType.QUEEN, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK}) {
            validMoves.add(new ChessMove(startPosition, endPosition, promotionType));
        }
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
