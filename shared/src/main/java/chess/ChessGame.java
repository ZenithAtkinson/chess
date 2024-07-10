package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;
    private ChessPosition kingPosition;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.currentTurn = TeamColor.WHITE; //white starts
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    //validMoves: Takes as input a position on the chessboard and returns all moves the piece there can legally make. If there is no piece at that location, this method returns null.
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //Wil lcall the "isinCheck" functions

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null; //Invalid move exception(?)
        }
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            //For TA's: how does this method work with makeMove?
            if (isMoveLegal(move)) { //valid moves is checking if it leads to a check/checkmate
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    private boolean isMoveLegal(ChessMove move) {
        ChessBoard tempBoard = cloneBoard(board);
        makeTempmove(tempBoard, move);
        setBoard(tempBoard);  // Set tempBoard as the current board
        boolean legal = !isInCheck(currentTurn);
        setBoard(board);  // Restore the original board
        return legal;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    //makeMove: Receives a given move and executes it, provided it is a legal move. If the move is illegal, it throws an InvalidMoveException. A move is illegal if the chess piece cannot move there, if the move leaves the team’s king in danger, or if it’s not the corresponding team's turn.
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //adheres to the possible moves from ChessPiece, but also checks for Check and Checkmate.
        //Will only work if given a possible move.

        //This function should call ValidMoves, NOT the other way around.
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            if (validMoves == null || !validMoves.contains(move)) {
                throw new InvalidMoveException("Illegal move");
            }

            ChessPiece piece = board.getPiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

            if (isInCheck(piece.getTeamColor())) {
                throw new InvalidMoveException("Move leaves the king in check (NO NO)");
            }
        }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    //isInCheck: Returns true if the specified team’s King could be captured by an opposing piece.
    public boolean isInCheck(TeamColor teamColor) {
        //Do I need a separate method to get the king based on the teamcolor? or can I change the parameters?
        ChessPosition kingPosition = getKingPosition(teamColor);
        ////Need to loop through ALL positions
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));

                if (piece != null && piece.getTeamColor() != teamColor) { //Given piece is opposite color
                    Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(row + 1, col + 1));
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    //isInCheckmate: Returns true if the given team has no way to protect their king from being captured.
    public boolean isInCheckmate(TeamColor teamColor) {
        //Accesses the same mechanic as Stalement
        //Youre in check, and there are no valid moves

        if (isInCheck(teamColor)){//Check if in check
            return false;
        }
        ChessPosition kingPosition = getKingPosition(teamColor);
        ChessPiece king = board.getPiece(kingPosition);
        Collection<ChessMove> kingMoves = king.pieceMoves(board, kingPosition);
        //Check if the king has NO valid moves.

            //For each of the moves the king has, check if when the king is at the end_position of a given move if it is in check.
            //We can't have a parameter for a position or move into isInCheck(), so we need to just move the piece, and then check from the board if it is in Check.
            for (ChessMove move : kingMoves) {
                ChessBoard tempBoard = cloneBoard(board);
                makeTempmove(tempBoard, move);
                setBoard(tempBoard);  // Set tempBoard as the current board
                if (!isInCheck(teamColor)) {
                    setBoard(board);  // Restore the original board
                    return false;
                }
                setBoard(board);  // Restore the original board
            }

        // If all moves result in check, return true
        return true;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */

    //isInStalemate: Returns true if the given team has no legal moves but their king is not in immediate danger.
    public boolean isInStalemate(TeamColor teamColor) {
        //Accesses the same mechanic as Checkmate
        //Youre not in check, but there are no valid moves.
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
        //set board to a clone of previous board (with modifications)
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition getKingPosition(TeamColor king_color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece != null && piece.getTeamColor() == king_color && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = new ChessPosition(row + 1, col + 1);
                    return new ChessPosition(row + 1, col + 1);
                }
            }
        }
        System.out.println("There s no king found in getKingPosition");
        return null;
    }

    private void makeTempmove(ChessBoard tempBoard, ChessMove move) {
        ChessPiece piece = tempBoard.getPiece(move.getStartPosition());
        tempBoard.addPiece(move.getEndPosition(), piece);
        tempBoard.addPiece(move.getStartPosition(), null);
    }

    private ChessBoard cloneBoard(ChessBoard originalBoard) { //deep copy of ChessBoard
        ChessBoard newBoard = new ChessBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = originalBoard.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece != null) {
                    newBoard.addPiece(new ChessPosition(row + 1, col + 1), piece);
                }
            }
        }
        return newBoard;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
