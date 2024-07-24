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
        if (piece.getPieceType() == PieceType.BISHOP){
            bishopValidMoves(validMoves, board, myPosition);
        } else if (piece.getPieceType() == PieceType.ROOK){
            rookValidMoves(validMoves, board, myPosition);
        } else if (piece.getPieceType() == PieceType.QUEEN){
            queenValidMoves(validMoves, board, myPosition);
        } else if (piece.getPieceType() == PieceType.KING){
            kingValidMoves(validMoves, board, myPosition);
        } else if (piece.getPieceType() == PieceType.KNIGHT){
            knightValidMoves(validMoves, board, myPosition);
        } else if (piece.getPieceType() == PieceType.PAWN){
            pawnValidMoves(validMoves, board, myPosition);
        }
        return validMoves;
    }

    private void bishopValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        //call diagonal moves
        diagonalMoves(validMoves, board, myPosition);
    }

    private void rookValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        //Call Directional Moves
        directionalMoves(validMoves, board, myPosition);
    }

    private void queenValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {
        //call diagonal moves
        diagonalMoves(validMoves, board, myPosition);
        //call directional moves
        directionalMoves(validMoves, board, myPosition);
    }

    private void kingValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition) {

        //Check each direction if it is empty OR if it is enemy occupied. If either, add it as a valid move
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        ChessMove move;
        ChessPosition newPosition;

        //Straight up
        if (moveIsValid(myPosition.getRow()+1, myPosition.getColumn())){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Top right
        if (moveIsValid(myPosition.getRow()+1, myPosition.getColumn()+1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Straight right
        if (moveIsValid(myPosition.getRow(), myPosition.getColumn()+1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Bottom right
        if (moveIsValid(myPosition.getRow()-1, myPosition.getColumn()+1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }

        //Straight bottom
        if (moveIsValid(myPosition.getRow()-1, myPosition.getColumn())){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Bottom left
        if (moveIsValid(myPosition.getRow()-1, myPosition.getColumn()-1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Straight left
        if (moveIsValid(myPosition.getRow(), myPosition.getColumn()-1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Top left
        if (moveIsValid(myPosition.getRow()+1, myPosition.getColumn()-1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }

    }

    private void knightValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition){
        //Check each direction if it is empty OR if it is enemy occupied. If either, add it as a valid move
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        ChessMove move;
        ChessPosition newPosition;

        //Up right
        if (moveIsValid(myPosition.getRow()+2, myPosition.getColumn()+1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Right up
        if (moveIsValid(myPosition.getRow()+1, myPosition.getColumn()+2)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Right down
        if (moveIsValid(myPosition.getRow()-1, myPosition.getColumn()+2)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Down right
        if (moveIsValid(myPosition.getRow()-2, myPosition.getColumn()+1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Down left
        if (moveIsValid(myPosition.getRow()-2, myPosition.getColumn()-1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Left down
        if (moveIsValid(myPosition.getRow()-1, myPosition.getColumn()-2)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Left up
        if (moveIsValid(myPosition.getRow()+1, myPosition.getColumn()-2)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
        //Up left
        if (moveIsValid(myPosition.getRow()+2, myPosition.getColumn()-1)){ //if Move is not out of bounds
            newPosition = new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1);
            move = new ChessMove(myPosition, newPosition, null);
            if (board.getPiece(newPosition) == null){
                validMoves.add(move);
            } else if (enemyOccupiedSpace(board, newPosition, color)){
                validMoves.add(move);
            }
        }
    }
    private void pawnValidMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition){

        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        ChessPosition newPosition;
        //For each valid move added, check if it would be a promotion piece. If so, instead of adding 1 move, add 4
        // with each of the promotion pieces
        //FOR WHITE PIECES
        if (color == WHITE){
            newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()); //Check for piece
            // straight ahead.
            if (board.getPiece(newPosition) == null && moveIsValid(newPosition.getRow(), newPosition.getColumn())){
                pawnAddMoves(validMoves, board, myPosition, newPosition);
                newPosition = new ChessPosition(newPosition.getRow()+1, newPosition.getColumn());
                if (myPosition.getRow() == 2 && board.getPiece(newPosition) == null){ //Check if on starter row
                    pawnAddMoves(validMoves, board, myPosition, newPosition);
                }
            }
            //Check diagonals
            if (myPosition.getColumn()+1 < 8){
                //Right diagonal
                newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
                //Check if diagonal is occupied by an enemy AND if it is valid
                if (board.getPiece(newPosition) != null) {
                    if (moveIsValid(newPosition.getRow(), newPosition.getColumn()) &&
                            board.getPiece(newPosition).getTeamColor() != color){
                            //Will this lead to a null team color error? Probably
                        pawnAddMoves(validMoves, board, myPosition, newPosition);
                    }
                }
            }
            if (myPosition.getColumn()-1 > 0){
                //Right diagonal
                newPosition = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
                //Check if diagonal is occupied by an enemy AND if it is valid
                if (board.getPiece(newPosition) != null) {
                    if (moveIsValid(newPosition.getRow(), newPosition.getColumn()) &&
                            board.getPiece(newPosition).getTeamColor() != color){
                        //Will this lead to a null team color error? Probably
                        pawnAddMoves(validMoves, board, myPosition, newPosition);
                    }
                }
            }
        }
        if (color == BLACK){
            //Check for piece straight ahead.
            newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            if (board.getPiece(newPosition) == null && moveIsValid(newPosition.getRow(), newPosition.getColumn())){
                pawnAddMoves(validMoves, board, myPosition, newPosition);
                newPosition = new ChessPosition(newPosition.getRow()-1, newPosition.getColumn());
                if (myPosition.getRow() ==7 && board.getPiece(newPosition) == null){ //Check if on starter row
                    pawnAddMoves(validMoves, board, myPosition, newPosition);
                }
            }
            //Check diagonals
            if (myPosition.getColumn()+1 < 8){
                //Right diagonal
                newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
                //Check if diagonal is occupied by an enemy AND if it is valid
                if (board.getPiece(newPosition) != null) {
                    if (moveIsValid(newPosition.getRow(), newPosition.getColumn()) &&
                            board.getPiece(newPosition).getTeamColor() != color){
                        //Will this lead to a null team color error? Probably
                        pawnAddMoves(validMoves, board, myPosition, newPosition);
                    }
                }
            }
            if (myPosition.getColumn()-1 > 0){
                //Right diagonal
                newPosition = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
                //Check if diagonal is occupied by an enemy AND if it is valid
                if (board.getPiece(newPosition) != null) {
                    //Will this lead to a null team color error? Probably
                    if (moveIsValid(newPosition.getRow(), newPosition.getColumn()) &&
                            board.getPiece(newPosition).getTeamColor() != color){
                        pawnAddMoves(validMoves, board, myPosition, newPosition);
                    }
                }
            }
        }
        //Check if first space ahead is valid.
        //if it is, check if we are on a starter row for given color. if so, check if two spots ahead are valid and
        // add if it is.
        //Check diagonals for attacks. Remeber to also use the valid move checker for a promotion piece.
    }
    private void diagonalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessMove move;

        //list of directions
        int [][] directions = {{1,1},{-1, 1},{-1, -1},{1, -1}};
        //clockwise directions, starting at top right
        //loop through directions
        for (int[] direction : directions){
            //for given int, col
            int newRow = row;
            int newCol = col;
            while (true) {
                //increase int, col by directional val(.?)
                newRow += direction[0];
                newCol += direction[1];
                //check statements

                //default valid position
                if (!moveIsValid(newRow, newCol)){
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                //attack check

                //First check if null. If it is, can just move on. If not, check if the piece is an enemy or ally.
                // If enemy, add it and break. If not, just break.
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                        //Team color is the same, so break
                        break;
                    }
                    //This means that the piece here is an enemy.
                    move = new ChessMove(myPosition, newPosition, null);
                    validMoves.add(move);
                    break;
                }
                // Add move
                move = new ChessMove(myPosition, newPosition, null);
                validMoves.add(move);
            }
        }
    }

    private void directionalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessMove move;

        //list of directions
        int [][] directions = {{1,0},{0, 1},{-1, 0},{0, -1}};
        //clockwise directions, starting at straight up

        //loop through directions
        for (int[] direction : directions){
            //for given int, col
            int newRow = row;
            int newCol = col;
            while (true) {
                //increase int, col by directional val(.?)
                newRow += direction[0];
                newCol += direction[1];
                //check statements

                //default valid position
                if (!moveIsValid(newRow, newCol)){
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                //attack check

                //First check if null. If it is, can just move on. If not, check if the piece is an enemy or ally.
                // If enemy, add it and break. If not, just break.
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
                        //Team color is the same, so break
                        break;
                    }
                    //This means that the piece here is an enemy.
                    move = new ChessMove(myPosition, newPosition, null);
                    validMoves.add(move);
                    break;
                }
                // Add move
                move = new ChessMove(myPosition, newPosition, null);
                validMoves.add(move);
            }
        }
    }
    private boolean moveIsValid(int row, int col){
        if ((row > 0 && row <= 8) &&(col > 0 && col <= 8)){
            return true;
        }
        return false;
    }
    private boolean enemyOccupiedSpace(ChessBoard board, ChessPosition newPosition, ChessGame.TeamColor color){
        if (board.getPiece(newPosition) != null){
            if(board.getPiece(newPosition).getTeamColor() != color) {
                return true;
            }
        }
        return false;
    }
    private void pawnAddMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition myPosition,
                              ChessPosition newPosition){ //DONT call this if space is not valid
        ChessMove move;
        if(newPosition.getRow() == 8 || newPosition.getRow() == 1){ //PROMOTION PIECE.
            pawnPromotionPieces(validMoves, myPosition, newPosition);
        }
        else{
            move = new ChessMove(myPosition, newPosition, null);
            validMoves.add(move);
        }
    }
    private void pawnPromotionPieces(Collection<ChessMove> validMoves, ChessPosition startPosition, ChessPosition
            endPosition){
        ChessMove move = new ChessMove(startPosition, endPosition, PieceType.QUEEN);
        validMoves.add(move);
        move = new ChessMove(startPosition, endPosition, PieceType.BISHOP);
        validMoves.add(move);
        move = new ChessMove(startPosition, endPosition, PieceType.KNIGHT);
        validMoves.add(move);
        move = new ChessMove(startPosition, endPosition, PieceType.ROOK);
        validMoves.add(move);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
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