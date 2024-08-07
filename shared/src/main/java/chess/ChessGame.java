package chess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Objects;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * For a class that can manage a chess game, making moves on a board.
 * <p>
 * Note: You can add to this class, but you may not alter
 * the signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;
    private ChessPosition kingPosition;



    // new chess game
    public ChessGame() {
        board = new ChessBoard();
        currentTurn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Sets which team's turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }


    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE, BLACK;

        public TeamColor getTeamTurnOpposite() {
            return (this == WHITE) ? BLACK : WHITE;
        }
    }

    /**
     * Gets valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for the requested piece, or null if no piece at startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        /*//Wil lcall the "isinCheck" functions

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null; //Invalid move exception(?)
        }*/
        if (piece == null) {
            return null;
        }

        //All moves (from pieceMoves)
        List<ChessMove> validMoves = new ArrayList<>(piece.pieceMoves(board, startPosition));
        // Remove moves that place king in check
        Iterator<ChessMove> iterator = validMoves.iterator();
        while (iterator.hasNext()) {
            ChessMove givenMove = iterator.next();
            if (isMoveInvalid(board, givenMove)) {
                iterator.remove();
            }
        }

        return validMoves;
    }

    //validMoves: Takes as input a position on the chessboard and returns all moves the piece there can legally make.
    // If there is no piece at that location, this method returns null
    public boolean isMoveInvalid( ChessBoard board, ChessMove givenMove) {
        //Wil lcall the "isinCheck" functions
        try {
            ChessBoard clonedBoard = new ChessBoard(board);
            ChessPiece movingPiece = clonedBoard.getPiece(givenMove.getStartPosition());
            actuallyDoMove(clonedBoard, givenMove);
            return isInCheck(movingPiece.getTeamColor(), clonedBoard);
        } catch (InvalidMoveException invalidmove) {
            return true;
        }
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if the move is invalid
     */
    //makeMove: Receives a given move and executes it, provided it is a legal move. If the move is illegal, it throws
    // an InvalidMoveException. A move is illegal if the chess piece cannot move there, if the move leaves the team’s
    // king in danger, or if it’s not the corresponding team's turn.
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //adheres to the possible moves from ChessPiece, but also checks for Check and Checkmate.
        //Will only work if given a possible move.

        //This function should call ValidMoves, NOT the other way around.
        ChessPiece piece = board.getPiece(move.getStartPosition());

        //VALIDATE
        //if there is no piece...
        if (piece == null) {
            throw new InvalidMoveException("No piece at starting");
        }
        //if it is currently the wrong teams turn...
        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Wrong piece turn");
        }
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        //if the move is not valid, throw error
        if (!moves.contains(move)) {
            throw new InvalidMoveException("Not a valid move");
        }

        //do it
        actuallyDoMove(board, move);
        currentTurn = currentTurn.getTeamTurnOpposite();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board); //Its much easier if we can use the board, so just gonna call a separate
        // function that returns true or false.
    }

    // checking if a team is in check on the board
    // checking if a team is in check on the board
    public static boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition theKing = getKing(teamColor, board);
        if (theKing == null || board.getPiece(theKing) == null) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }

                for (ChessMove move : piece.pieceMoves(board, position)) {
                    if (move.getEndPosition().equals(theKing)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // finding the king
    private static ChessPosition getKing(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition kingPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(kingPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        return kingPosition;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && nullValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    //isInStalemate: Returns true if the given team has no legal moves but their king is not in immediate danger.
    public boolean isInStalemate(TeamColor teamColor) {
        //Accesses the same mechanic as Checkmate(?)
        //Youre not in check, but there are no valid moves.
        return !isInCheck(teamColor) && nullValidMoves(teamColor);
    }

    //Check if no valid moves exist for a team(?)
    private boolean nullValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!validMoves(position).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    //Perform a move on the board
    private void actuallyDoMove(ChessBoard board, ChessMove move ) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), piece);
        } else {
            //If the piece requires a promotion (pawn moving to edge)
            doPromotion(board, move, piece);
        }
        if (piece == null) {
            throw new InvalidMoveException("No piece at starting position");
        }

    }

    //Handle promotion pieces (rewordked)
    private void doPromotion( ChessBoard board, ChessMove givenMove, ChessPiece piece) throws InvalidMoveException {
        //my old promotion function just isnt owrking for some reason

        if (piece.getPieceType() != ChessPiece.PieceType.PAWN){
            throw new InvalidMoveException("Promotion piece isn't a pawn");
        }
        if ((piece.getTeamColor() == TeamColor.WHITE && givenMove.getEndPosition().getRow() != 8) ||
                (piece.getTeamColor() == TeamColor.BLACK && givenMove.getEndPosition().getRow() != 1)){
            throw new InvalidMoveException("Move doesn't end at end of row (promotion)");
        }
        board.addPiece(givenMove.getStartPosition(), null);
        board.addPiece(givenMove.getEndPosition(), new ChessPiece(piece.getTeamColor(), givenMove.getPromotionPiece()));
    }

//private void makeTempmove(ChessBoard tempBoard, ChessMove move) {

    //private ChessBoard cloneBoard(ChessBoard originalBoard) { //deep copy of ChessBoard
    //moved to just the board class
    @Override
    public boolean equals(Object o) { // Added for the ChessGame object checking in SQL
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) &&
                currentTurn == chessGame.currentTurn &&
                Objects.equals(kingPosition, chessGame.kingPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn, kingPosition);
    }


}
