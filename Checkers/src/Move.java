/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author shiosaii & liamorr24
 */
public class Move {
    // instance variables
    public int startRow, startColumn; // Starting location of piece
    public int endRow, endColumn; // End location of piece; where it's moving to
    
    // constructor, takes the starting and ending co-ordinates
    public Move(int startRow, int startColumn, int endRow, int endColumn) {
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.endRow = endRow;
        this.endColumn = endColumn;
    }
}
