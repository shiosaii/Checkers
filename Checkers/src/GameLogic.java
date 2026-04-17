/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author shiosaii & liamorr24
 */

// imports
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    // create a 2d obect array of pieces to represent the board state
    private Piece[][] board;
    private boolean redTurn = true;
    
    // Constructor
    public GameLogic() {
        board = new Piece[8][8];
        initializeBoard();
    }
    
    /**
     * Method that sets the starting board state; since pieces in checkers
     * can only exist and move on dark tiles, it only places pieces on dark tiles
     * between the border zone of rows 3 & 4.
     */
    private void initializeBoard() {
        // these two for loops itterate through the whole boards tiles
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if ((row + column) % 2 != 0) { // Odd tiles are Dark
                    if (row < 3) board[row][column] = new Piece(false); // Black
                    else if (row > 4) board[row][column] = new Piece(true); // Red
                }
            }
        }
    }
    
    /**
     * Method that checks and forces a jump if it exists for the current player.
     * @param isRed takes a boolean value of whether or not it's red's turn.
     * @return an array list of moves comprising of all possible jumps.
     */
    public List<Move> getAllRequiredJumps(boolean isRed) {
        // Create the array list
        List<Move> jumps = new ArrayList<>();
        // Itterate through all the tiles
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Piece focusPiece = board[row][column];
                // this if statement triggers if the piece belongs to the
                // current player; false == false equals true.
                if (focusPiece != null && focusPiece.isRed() == isRed) {
                    jumps.addAll(getPieceJumps(row, column));
                }
            }
        }
        return jumps;
    }
    
    private List<Point> getRegularMoves(int currentRow, int currentColumn) {
        List<Point> validMoves = new ArrayList<>();
        Piece focusPiece = board[currentRow][currentColumn];
        if (focusPiece == null) return validMoves;

        // Determine vertical directions: Red moves -1 (up), Black moves 1 (down), Kings move both
        int[] rowDirections = focusPiece.isKing() ? new int[]{-1, 1} : (focusPiece.isRed() ? new int[]{-1} : new int[]{1});
        int[] columnDirections = {-1, 1}; // Always move left or right diagonally

        for (int verticalOffset : rowDirections) {
            for (int horizontalOffset : columnDirections) {
                int neighbourRow = currentRow + verticalOffset;
                int neighbourColumn = currentColumn + horizontalOffset;

                // Ensure the neighbour square is within the 8x8 board boundaries
                if (neighbourRow >= 0 && neighbourRow < 8 && neighbourColumn >= 0 && neighbourColumn < 8) {
                    // In Checkers, a regular move must land on an empty square
                    if (board[neighbourRow][neighbourColumn] == null) {
                        // Point(x, y) maps to Point(column, row)
                        validMoves.add(new Point(neighbourColumn, neighbourRow));
                    }
                }
            }
        }
        return validMoves;
    }

    private List<Move> getPieceJumps(int currentRow, int currentColumn) {
        List<Move> jumpsFound = new ArrayList<>();
        Piece focusPiece = board[currentRow][currentColumn];
        if (focusPiece == null) return jumpsFound;
        
        int[] rowDirections = focusPiece.isKing() ? new int[]{-1, 1} : (focusPiece.isRed() ? new int[]{-1} : new int[]{1});
        int[] columnDirections = {-1, 1};

        for (int verticalOffset : rowDirections) {
            for (int horizontalOffset : columnDirections) {
                int intermediateRow = currentRow + verticalOffset;
                int intermediateColumn = currentColumn + horizontalOffset;
                int landingRow = currentRow + (verticalOffset * 2);
                int landingColumn = currentColumn + (horizontalOffset * 2);

                // Check if the landing square is on the board
                if (landingRow >= 0 && landingRow < 8 && landingColumn >= 0 && landingColumn < 8) {
                    Piece pieceToJump = board[intermediateRow][intermediateColumn];
                    Piece landingSquare = board[landingRow][landingColumn];

                    // Logic: Intermediate square must have an ENEMY, and Landing square must be EMPTY
                    if (pieceToJump != null && pieceToJump.isRed() != focusPiece.isRed() && landingSquare == null) {
                        jumpsFound.add(new Move(currentRow, currentColumn, landingRow, landingColumn));
                    }
                }
            }
        }
        return jumpsFound;
    }
    
    /**
     * Method that calculates and enforces valid movements for pieces.
     */
    public List<Point> getValidDestinations(int row, int column) {
        // Make an array list of all the required moves
        List<Move> forced = getAllRequiredJumps(redTurn);
        // Make an array list of all valid points user can move to
        List<Point> validPoints = new ArrayList<>();
        
        // RULE: If a jump is available, you must take it
        if (!forced.isEmpty()) {
            for (Move focusMove : forced) {
                if (focusMove.startRow == row && focusMove.startColumn == column) {
                validPoints.add(new Point(focusMove.endColumn, focusMove.endRow));   
                }
            }
        } else {
            // If no jumps are available, calculate regular diagonal moves
            validPoints.addAll(getRegularMoves(row, column));
        }
        return validPoints;
    }
    
    public void movePiece(int startRow, int startCol, int endRow, int endCol) {
        Piece piece = board[startRow][startCol];

        // 1. Calculate the distance traveled
        int rowDelta = endRow - startRow;
        int colDelta = endCol - startCol;

        // 2. If it's a jump (distance of 2), kill the middle man
        if (Math.abs(rowDelta) == 2 && Math.abs(colDelta) == 2) {
            int midRow = startRow + (rowDelta / 2);
            int midCol = startCol + (colDelta / 2);
            board[midRow][midCol] = null;
        }

        // 3. Teleport the piece
        board[endRow][endCol] = piece;
        board[startRow][startCol] = null;

        // 4. Check for King
        if ((piece.isRed() && endRow == 0) || (!piece.isRed() && endRow == 7)) {
            piece.makeKing();
        }
    }
    /**
     * Getter for the current 2D array of pieces.
     * @return the current board state.
     */
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Checks whose turn it is.
     * @return true if it is currently Red's turn.
     */
    public boolean isRedTurn() {
        return redTurn;
    }
    
    /**
     * Simple toggle to switch turns after a successful move.
     */
    public void toggleTurn() {
        redTurn = !redTurn;
    }
}