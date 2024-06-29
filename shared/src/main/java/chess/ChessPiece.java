package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

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
        ChessPiece given_piece = board.getPiece(myPosition);
        int piece_row = myPosition.getRow();
        int piece_column = myPosition.getColumn();

        if (given_piece.getPieceType() == KING) {
            //System.out.println("This is a King");
            return(KingMovesCalculator(board, piece_row, piece_column));
        } else if (given_piece.getPieceType() == BISHOP) {
            //System.out.println("This is a Bishop");
            return(BishopMovesCalculator(board, piece_row, piece_column));
        } else if (given_piece.getPieceType() == PieceType.KNIGHT) {
            //System.out.println("This is a Knight");
            return (KnightMovesCalculator(board, piece_row, piece_column));
        } else if (given_piece.getPieceType() == PieceType.PAWN) {
            //System.out.println("This is a Pawn");
            return (PawnMovesCalculator(board, piece_row, piece_column));
        } else if (given_piece.getPieceType() == PieceType.QUEEN) {
            System.out.println("This is a Queen");
            return (QueenMovesCalculator(board, piece_row, piece_column));
        }

        //First check for piece type (from board, given position)
        //Call appropriate function ( see below)
        //Return valid moves, then return whatever the function returns (so return whatever KingMovesCalcualor returns)
        //return KingMovesCalculator(board, position)

        //Add the surrounding spaces to the king based on its position, IF they are valid (not taken up by a teammate).
        //Spots occupied by an enemy ARE valid, but spots beyond it are NOT.

        return new ArrayList<>(); //TEMPORARY, needs to be full of the ChessMove class.

    }


    public Collection<ChessMove> BishopMovesCalculator(ChessBoard board, int row, int col) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPosition start_position = new ChessPosition(row, col);
        ChessGame.TeamColor BishopColor = board.getPiece(start_position).getTeamColor();

        //loop for all directions in diagonals, adding moves as you go
        addDiagonalMoves(validMoves, board, start_position, row, col, BishopColor);

        return validMoves;
    }

    public Collection<ChessMove> KingMovesCalculator(ChessBoard board, int row, int col) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPosition start_position = new ChessPosition(row, col);
        int new_row = row;
        int new_col = col;
        ChessPiece new_piece;
        ChessPosition new_position;
        ChessMove new_move;
        ChessGame.TeamColor KingColor = board.getPiece(start_position).getTeamColor();

        //Kings can move one in any direction

        //straight up
        if (BoolValidSingleMove(board, row+1, col, KingColor)) {
            new_position = new ChessPosition(row+1, col);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }

        //top right
        if (BoolValidSingleMove(board, row+1, col+1, KingColor)) {
            new_position = new ChessPosition(row+1, col+1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }

        //right
        if (BoolValidSingleMove(board, row, col+1, KingColor)) {
            new_position = new ChessPosition(row, col+1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }

        //bottom right
        if (BoolValidSingleMove(board, row-1, col+1, KingColor)) {
            new_position = new ChessPosition(row-1, col+1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }

        //straight bottom
        if (BoolValidSingleMove(board, row-1, col, KingColor)) {
            new_position = new ChessPosition(row-1, col);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }

        //bottom left
        if (BoolValidSingleMove(board, row-1, col-1, KingColor)) {
            new_position = new ChessPosition(row-1, col-1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }

        //left
        if (BoolValidSingleMove(board, row, col-1, KingColor)) {
            new_position = new ChessPosition(row, col-1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }

        //top left
        if (BoolValidSingleMove(board, row+1, col-1, KingColor)) {
            new_position = new ChessPosition(row+1, col-1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //arrayPrint(validMoves);
        return validMoves;
    }

    public Collection<ChessMove> KnightMovesCalculator(ChessBoard board, int row, int col) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPosition start_position = new ChessPosition(row, col);
        ChessPosition new_position;
        ChessMove new_move;
        ChessGame.TeamColor KnightColor = board.getPiece(start_position).getTeamColor();


        //top right
        if (BoolValidSingleMove(board, row+2, col+1, KnightColor)){
            new_position = new ChessPosition(row+2, col+1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //right up
        if (BoolValidSingleMove(board, row+1, col+2, KnightColor)){
            new_position = new ChessPosition(row+1, col+2);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //right down
        if (BoolValidSingleMove(board, row-1, col+2, KnightColor)){
            new_position = new ChessPosition(row-1, col+2);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //down right
        if (BoolValidSingleMove(board, row-2, col+1, KnightColor)){
            new_position = new ChessPosition(row-2, col+1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //down left
        if (BoolValidSingleMove(board, row-2, col-1, KnightColor)){
            new_position = new ChessPosition(row-2, col-1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //left down
        if (BoolValidSingleMove(board, row-1, col-2, KnightColor)){
            new_position = new ChessPosition(row-1, col-2);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //left up
        if (BoolValidSingleMove(board, row+1, col-2, KnightColor)){
            new_position = new ChessPosition(row+1, col-2);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        //top left
        if (BoolValidSingleMove(board, row+2, col-1, KnightColor)){
            new_position = new ChessPosition(row+2, col-1);
            new_move = new ChessMove(start_position, new_position, null);
            validMoves.add(new_move);
        }
        return validMoves;

    }

    public Collection<ChessMove> PawnMovesCalculator(ChessBoard board, int row, int col) {
        boolean diagonal = false;
        Collection<ChessMove> validMoves = new ArrayList<>();

        ChessPosition start_position = new ChessPosition(row, col);
        ChessPosition new_position;
        ChessMove new_move;
        ChessGame.TeamColor PawnColor = board.getPiece(start_position).getTeamColor();
        boolean starter;
        new_position = new ChessPosition(row,col);

        if (PawnColor == WHITE) {
            //Check for point right ahead
            PromotionPawnAdder(diagonal, validMoves, board, start_position, row+1, col, PawnColor);
            new_position = new ChessPosition(row+1,col);
            if ((start_position.getRow()==2) && (board.getPiece(new_position) == null)){
                PromotionPawnAdder(diagonal, validMoves, board, start_position, row + 2, col, PawnColor);
            }
            //check for enemy at diagonals
            if (EnemyPosition(board,row+1, col+1, WHITE)) {
                diagonal = true;
                PromotionPawnAdder(diagonal, validMoves, board, start_position, row+1, col+1, PawnColor);
            }
            if (EnemyPosition(board,row+1, col-1, WHITE)) {
                diagonal = true;
                PromotionPawnAdder(diagonal, validMoves, board, start_position, row+1, col-1, PawnColor);
            }
        }

        if (PawnColor == BLACK) {
            //Check for point right ahead
            PromotionPawnAdder(diagonal, validMoves, board, start_position, row-1, col, PawnColor);
            //Check if it is at starting position, provided the previous point is valid.
            new_position = new ChessPosition(row-1,col);
            if ((start_position.getRow()==7) && (board.getPiece(new_position) == null)){ //if yes, check for two spaces ahead and that previous space was empty.
                PromotionPawnAdder(diagonal, validMoves, board, start_position, row-2, col, PawnColor);
            }
        }
            //check for enemy at diagonals
            if (EnemyPosition(board,row-1, col+1, BLACK)) {
                diagonal = true;
                PromotionPawnAdder(diagonal, validMoves, board, start_position, row-1, col+1, PawnColor);
            }
            if (EnemyPosition(board,row-1, col-1, BLACK)) {
                diagonal = true;
                PromotionPawnAdder(diagonal, validMoves, board, start_position, row-1, col-1, PawnColor);
            }

            //arrayPrint(validMoves);
        return validMoves;
    }

    public Collection<ChessMove> QueenMovesCalculator(ChessBoard board, int row, int col) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPosition start_position = new ChessPosition(row, col);
        ChessGame.TeamColor QueenColor = board.getPiece(start_position).getTeamColor();

        //loop for all directions in diagonals, adding moves as you go
        addDiagonalMoves(validMoves, board, start_position, row, col, QueenColor);


        //loop for all directoins in lines, adding moves as you go
        addDirectionalMoves(validMoves, board, start_position, row, col, QueenColor);
        return validMoves;
    }


    /*
    public Collection<ChessMove> RookMovesCalculator(ChessBoard board, ChessPosition myPosition) { }
     */


    private static boolean BoolValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private static boolean BoolValidSingleMove(ChessBoard board, int row, int col, ChessGame.TeamColor color){
        if ((BoolValidPosition(row, col))) {
            ChessPosition new_position = new ChessPosition(row, col);
            ChessPiece new_piece = board.getPiece(new_position);
            return (new_piece == null) || new_piece.getTeamColor() != color;
        }
        return false;
    }

    private static boolean PawnBoolValidSingleMove(boolean diagonal, ChessBoard board, int row, int col, ChessGame.TeamColor color){
        if ((BoolValidPosition(row, col))) {
            ChessPosition new_position = new ChessPosition(row, col);
            ChessPiece new_piece = board.getPiece(new_position);
            if (diagonal){
                if (new_piece.getTeamColor() == color)
                {
                    return false;
                }
                return (new_piece != null);
            }
            return (new_piece == null);
        }
        return false;
    }

    private boolean EnemyPosition(ChessBoard board, int row, int col, ChessGame.TeamColor color){
        boolean EnemyPresent = false;
        ChessPosition position = new ChessPosition(row, col);
        if (board.getPiece(position) != null){
            if ((board.getPiece(position).getTeamColor() != color)){
                EnemyPresent = true;
            }
        }
        return EnemyPresent;
    }

    private void addDiagonalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition start_position, int row, int col, ChessGame.TeamColor color) {
        int new_row = row;
        int new_col = col;
        ChessMove new_move;
        ChessPiece new_piece;
        ChessPosition new_position;

        //Top right quadrant (+,+)
        while ((new_row < 8) && (new_col < 8)){
            new_row++;
            new_col++;
            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);

            if (new_piece == null || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }

        new_row = row;
        new_col = col;

        //Bottom right quadrant (-,+)
        while ((new_row != 1) && (new_col < 8)){
            new_row--;
            new_col++;
            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);

            if (new_piece == null  || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }

        new_row = row;
        new_col = col;

        //Bottom left quadrant (-,-)
        while ((new_row != 1) && (new_col != 1)){
            new_row--;
            new_col--;
            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);

            if (new_piece == null || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }

        new_row = row;
        new_col = col;

        //Top left quadrant (+,-)
        while ((new_row < 8) && (new_col != 1)){
            new_row++;
            new_col--;
            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);
            if (new_piece == null || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }

    }

    private void addDirectionalMoves(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition start_position, int row, int col, ChessGame.TeamColor color) {
        int new_row = row;
        int new_col = col;
        ChessMove new_move;
        ChessPiece new_piece;
        ChessPosition new_position;

        //straight up)
        while ((new_row < 8) && (new_col < 8)){
            new_row++;
            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);

            if (new_piece == null || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }
        new_row = row;
        new_col = col;

        //straight right
        while ((new_row != 0) && (new_col < 8)){
            if (new_row != 1) {new_row--;}
            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);

            if (new_piece == null || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }

        new_row = row;
        new_col = col;

        //straight down
        while ((new_row != 0) && (new_col != 0)){
            if (new_row != 1) {new_row--;} //The directions are totally messed up, review the 0's and 8's

            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);

            if (new_piece == null || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }

        new_row = row;
        new_col = col;

        //straight left
        while ((new_row != 0) && (new_col != 0)){
            if (new_row != 1) {new_row--;}
            if (new_col != 1) {new_col--;}
            new_position = new ChessPosition(new_row, new_col);
            new_piece = board.getPiece(new_position);
            if (new_piece == null || new_piece.getTeamColor()!=color){
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
                if (new_piece != null && new_piece.getTeamColor()!=color){ //break if we are on an opponet piece
                    break;
                }
            }
            if (new_piece != null && new_piece.getTeamColor()==color){ //break if we run into a teammate
                break;
            }
        }
    }

    private void PromotionPawnAdder(boolean diagonal, Collection<ChessMove> validMoves, ChessBoard board, ChessPosition start_position, int row, int col, ChessGame.TeamColor color) {
        ChessMove new_move;
        if (PawnBoolValidSingleMove(diagonal, board, row, col, color)) {
            ChessPosition new_position = new ChessPosition(row, col);

            if ((row == 8 && color == WHITE) || (row == 1 && color == BLACK)) { //
                new_move = new ChessMove(start_position, new_position, QUEEN);
                validMoves.add(new_move);
                new_move = new ChessMove(start_position, new_position, BISHOP);
                validMoves.add(new_move);
                new_move = new ChessMove(start_position, new_position, ROOK);
                validMoves.add(new_move);
                new_move = new ChessMove(start_position, new_position, KNIGHT);
                validMoves.add(new_move);
            } else {
                new_move = new ChessMove(start_position, new_position, null);
                validMoves.add(new_move);
            }
        }
    }

    private void arrayPrint(Collection<ChessMove> array){
        for (ChessMove move : array){
            System.out.println(move.getEndPosition().getRow() + "," + move.getEndPosition().getColumn() + " " + move.getPromotionPiece());
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

}

