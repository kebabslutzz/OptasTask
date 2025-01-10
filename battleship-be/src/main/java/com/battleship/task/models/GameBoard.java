package com.battleship.task.models;

public class GameBoard {

    private final int[][] board;
    private static final int EMPTY = -1;

    public GameBoard(int size) {
        this.board = new int[size][size];
        fillBoard();
    }

    private void fillBoard() {
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                this.board[i][j] = EMPTY;
            }
        }
    }

    public void placeShip(int x, int y, int shipId) {
        board[x][y] = shipId;
    }

    public int getShipId(int x, int y) {
        return board[x][y];
    }

    public boolean isEmpty(int x, int y) {
        return board[x][y] == EMPTY;
    }

    public int getSize() {
        return board.length;
    }
}
