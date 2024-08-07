package ui;

import webclient.WSClient;
import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameplayUI {
    private final WSClient wsClient;
    private final ChessBoard board;
    private final boolean isObserver;
    private final String userColor;
    private final BoardPrinter boardPrinter;
    private final String authToken;
    private final int gameId;

    public GameplayUI(WSClient wsClient, ChessBoard board, boolean isObserver, String userColor, String authToken, int gameId) {
        this.wsClient = wsClient;
        this.board = board;
        this.isObserver = isObserver;
        this.userColor = userColor;
        this.boardPrinter = new BoardPrinter();
        this.authToken = authToken;
        this.gameId = gameId;
    }

    public void display() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Gameplay Commands: Help, Redraw, Leave, Make Move, Resign, Highlight");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "redraw":
                    redrawBoard();
                    break;
                case "leave":
                    leaveGame();
                    return;
                case "make move":
                    makeMove(scanner);
                    break;
                case "resign":
                    resignGame();
                    break;
                case "highlight":
                    highlightMoves(scanner);
                    break;
                default:
                    System.out.println("Unknown command. Type 'Help' for a list of commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("Help - Displays this help message.");
        System.out.println("Redraw - Redraws the chess board.");
        System.out.println("Leave - Leaves the game and returns to the Post-Login UI.");
        System.out.println("Make Move - Prompts for and makes a move.");
        System.out.println("Resign - Resigns from the game.");
        System.out.println("Highlight - Highlights legal moves for a piece.");
    }

    private void redrawBoard() {
        if (userColor.equals("WHITE")) {
            boardPrinter.printBoard(board);
        } else {
            boardPrinter.printBoardReversed(board);
        }
    }

    private void leaveGame() {
        try {
            wsClient.leave(authToken, gameId);
            System.out.println("You have left the game.");
        } catch (Exception e) {
            System.out.println("Failed to leave the game: " + e.getMessage());
        }
    }

    private void makeMove(Scanner scanner) {
        if (isObserver) {
            System.out.println("Observers cannot make moves.");
            return;
        }

        System.out.print("Enter move (e.g., e2 e4 or e7 e8 q for promotion): ");
        String moveInput = scanner.nextLine().trim();
        try {
            ChessMove move = parseMove(moveInput);
            wsClient.makeMove(authToken, gameId, move);
            System.out.println("Move made: " + moveInput);
        } catch (Exception e) {
            System.out.println("Invalid move: " + e.getMessage());
        }
    }

    private ChessMove parseMove(String moveInput) throws Exception {
        String[] parts = moveInput.split(" ");
        if (parts.length < 2 || parts.length > 3) {
            throw new Exception("Usage: <source> <destination> <optional promotion> (e.g., f5 e4 q)");
        }

        ChessPosition from = parsePosition(parts[0]);
        ChessPosition to = parsePosition(parts[1]);
        ChessPiece.PieceType promotion = null;

        if (parts.length == 3) {
            switch (parts[2].charAt(0)) {
                case 'q' -> promotion = ChessPiece.PieceType.QUEEN;
                case 'r' -> promotion = ChessPiece.PieceType.ROOK;
                case 'b' -> promotion = ChessPiece.PieceType.BISHOP;
                case 'n' -> promotion = ChessPiece.PieceType.KNIGHT;
                case 'k' -> throw new Exception("Cannot promote to king");
                case 'p' -> throw new Exception("Cannot promote to pawn");
                default -> throw new Exception("Could not parse %s as a piece type".formatted(parts[2]));
            }
        }

        return new ChessMove(from, to, promotion);
    }

    private ChessPosition parsePosition(String pos) {
        char col = pos.charAt(0);
        int row = Character.getNumericValue(pos.charAt(1));
        return new ChessPosition(row, col - 'a' + 1);
    }

    private void resignGame() {
        if (isObserver) {
            System.out.println("Observers cannot resign the game.");
            return;
        }

        try {
            wsClient.resign(authToken, gameId);
            System.out.println("You have resigned from the game.");
        } catch (Exception e) {
            System.out.println("Failed to resign from the game: " + e.getMessage());
        }
    }

    private void highlightMoves(Scanner scanner) {
        System.out.print("Enter piece position (e.g., e2): ");
        String pos = scanner.nextLine().trim();
        try {
            ChessPosition position = parsePosition(pos);
            highlightLegalMoves(position);
        } catch (Exception e) {
            System.out.println("Invalid position: " + e.getMessage());
        }
    }

    private void highlightLegalMoves(ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece == null) {
            System.out.println("No piece at the given position.");
            return;
        }

        Collection<ChessMove> legalMoves = piece.pieceMoves(board, position);
        List<ChessPosition> legalPositions = legalMoves.stream()
                .map(ChessMove::getEndPosition)
                .collect(Collectors.toList());

        if (userColor.equals("WHITE")) {
            boardPrinter.printBoardWithHighlights(board, legalPositions);
        } else {
            boardPrinter.printBoardReversedWithHighlights(board, legalPositions);
        }
    }
}
