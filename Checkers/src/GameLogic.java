/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jacob Schmidt & Liam Orr
 */

// imports
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    // create a 2d obect array of pieces to represent the board state
    private Piece[][] board;
    private boolean redTurn = true; // state tracker for the player turn
    
    // Constructor
    public GameLogic() {
        board = new Piece[8][8]; // 8x8 grid
        initializeBoard();
    }
    
    /**
     * Method that sets the starting board state; since pieces in checkers
     * can only exist and move on dark tiles, it only places pieces on dark tiles
     * between the border zone of rows 3 & 4; dark tiles are where row + column
     * is odd.
     */
    private void initializeBoard() {
        // these two for loops itterate through the whole boards tiles
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if ((row + column) % 2 != 0) { // Odd tiles are Dark
                    // Place Black pieces on elegible tiles
                    if (row < 3) board[row][column] = new Piece(false); // Black
                    // Place Red pieces on elegible tiles
                    else if (row > 4) board[row][column] = new Piece(true); // Red
                }
            }
        }
    }
    
    /**
     * RULE ENFORCEMENT: Player must jump if possible.
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
    
    /**
     * CALCULATION: Checks diagonal neighbours for 1 square slides (non jumps)
     * Restricts direction based on colour unless the piece is a king.
     * @param currentRow of the piece.
     * @param currentColumn of the piece.
     * @return an object array list of points on the grid of valid end
     * co-ordinates that the piece can move to.
     */
    private List<Point> getRegularMoves(int currentRow, int currentColumn) {
        // Create an array list to store valid points piece can move to
        List<Point> validMoves = new ArrayList<>();
        Piece focusPiece = board[currentRow][currentColumn];
        // If there is not a piece on the tile, return an empty ArrayList
        if (focusPiece == null) return validMoves;


        // Direction Logic
        // Red pawns move up the board (index -1)
        // Black pawns move Down the board (index +1)
        // Kings are assigned both directions {-1, 1} with a nested ternary operator
        int[] rowDirections = focusPiece.isKing() ? new int[]{-1, 1} : (focusPiece.isRed() ? new int[]{-1} : new int[]{1});
        int[] columnDirections = {-1, 1}; // Always move left or right diagonally

        // Enhanced for loops iterate through each possible diagonal direction
        for (int verticalOffset : rowDirections) {
            for (int horizontalOffset : columnDirections) {
                int neighbourRow = currentRow + verticalOffset;
                int neighbourColumn = currentColumn + horizontalOffset;

                // Checking boundaries; make sure we're not looking for a tile
                // off the 8x8 grid
                if (neighbourRow >= 0 && neighbourRow < 8 && neighbourColumn >= 0 && neighbourColumn < 8) {
                    // In Checkers, a regular move must land on an empty square
                    if (board[neighbourRow][neighbourColumn] == null) {
                        // Point(x, y) maps to Point(column, row)
                        // We flip the array indicies [row][col] to fit the UI's
                        // Point(col, row) expectation
                        validMoves.add(new Point(neighbourColumn, neighbourRow));
                    }
                }
            }
        }
        return validMoves;
    }

    /**
     * JUMP / CAPTURE LOGIC
     * Scans for enemy pieces that can be hopped over.
     * @param currentRow
     * @param currentColumn
     * @return 
     */
    private List<Move> getPieceJumps(int currentRow, int currentColumn) {
        List<Move> jumpsFound = new ArrayList<>();
        Piece focusPiece = board[currentRow][currentColumn];
        if (focusPiece == null) return jumpsFound;
        
        int[] rowDirections = focusPiece.isKing() ? new int[]{-1, 1} : (focusPiece.isRed() ? new int[]{-1} : new int[]{1});
        int[] columnDirections = {-1, 1};

        for (int verticalOffset : rowDirections) {
            for (int horizontalOffset : columnDirections) {
                // INTERMEDIATE: The tile immediately diagonal where enemy should be
                int intermediateRow = currentRow + verticalOffset;
                int intermediateColumn = currentColumn + horizontalOffset;
                // LANDING: The tile two steps away (where our piece ends up)
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
     * MOVE ENFORCEMENT & FILTERING
     * This method acts as the brain for the UI's Click event.
     */
    public List<Point> getValidDestinations(int row, int column) {
        // Global Scan: Check if any piece on the board has a mandatory jump
        List<Move> forced = getAllRequiredJumps(redTurn);
        // Make an array list of all valid points user can move to
        List<Point> validPoints = new ArrayList<>();
        
        // RULE: If a jump is available, you must take it
        // If the forced list is not empty, the player must jump
        if (!forced.isEmpty()) {
            for (Move focusMove : forced) {
                // Only highlight the destination if the piece clicked is part of a forced jump
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
    
    /**
     * STATE UPDATE
     * Physically moves the piece in the 2D array and handles kills
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol 
     */
    public void movePiece(int startRow, int startCol, int endRow, int endCol) {
        Piece piece = board[startRow][startCol];

        // DELTA MATH: Calculate distance to detect if the move was a slide (1)
        // or a jump (2)
        int rowDelta = endRow - startRow;
        int colDelta = endCol - startCol;

        // CAPTURE EXECUTION:
        // If the move distance is exactly 2, we know an enemy was jumped
        if (Math.abs(rowDelta) == 2 && Math.abs(colDelta) == 2) {
            // Find the exact midpoint between start and end
            int midRow = startRow + (rowDelta / 2);
            int midCol = startCol + (colDelta / 2);
            // Remove the captured piece from the array.
            board[midRow][midCol] = null;
        }

        // TELEPORT: Move piece to new index, clear the old one
        board[endRow][endCol] = piece;
        board[startRow][startCol] = null;

        // KING PROMOTION:
        // If Red reaches the top (0) or Black reaches the bottom (7), promote
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