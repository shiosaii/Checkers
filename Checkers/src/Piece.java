/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Jacob Schmidt & Liam Orr
 */
public class Piece {
    // instance variables
    private boolean isRed; // true = Red, false = Black
    private boolean isKing; // Starts as false & becomes true at end of board
    
    /**
     * Constructor 
     * @param isRed stores the colour of the piece in a boolean.
     */
    public Piece(boolean isRed) {
        this.isRed = isRed;
        this.isKing = false; // every piece starts as a pawn, but can upgrade
    }
    
    // Getters and Setters
    public boolean isRed() {
        return isRed;
    }
    
    public boolean isKing() {
        return isKing;
    }
    
    // This setter promotes a pawn to king
    public void makeKing() {
        this.isKing = true;
    }
    
    /**
     * This method returns the full identity of a piece in image format
     * so that we can later assign it a matching image on the game board.
     * @return the image name for the piece.
     */
    public String getIconName() {
        if (isRed) {
            return isKing ? "red_king.png" : "red_pawn.png";
        } else {
            return isKing ? "black_king.png" : "black_pawn.png";
        }
    }
}

