package com.battleship.task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.Random;

@Service
@SessionScope
public class BattleshipService {

    // ships sizes from big to small, because small ships first slows down the process of placing the big ones
    private final int[] SHIPS = {5, 4, 3, 3, 2, 2, 2, 1, 1, 1};
    private int[][] gameBoard;

    public BattleshipService() {
        this.gameBoard = createGameBoard();
    }

    public int[][] getGameBoard() {
        return gameBoard;
    }

    public int[][] createGameBoard() {
        int[][] gameBoard = new int[10][10];
        fillBoard(gameBoard);
        placeShips(gameBoard);

        return gameBoard;
    }

    private void fillBoard(int[][] gameBoard) {
        for (int i = 0; i < gameBoard.length; i++) {
            Arrays.fill(gameBoard[i], 0);
        }
    }

    private void placeShips(int[][] gameBoard) {
        Random random = new Random();

        for (int ship : SHIPS) {
            boolean isPlaced = false;
            while (!isPlaced) {
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                boolean isHorizontal = random.nextBoolean();

                if (isPlaceAvailable(gameBoard, row, col, ship, isHorizontal)) {
                    placeShip(gameBoard, row, col, ship, isHorizontal);
                    isPlaced = true;
                }
            }
        }
    }

    private boolean isPlaceAvailable(int[][] gameBoard, int row, int col, int shipSize, boolean isHorizontal) {
        if (isHorizontal) {
            if (col + shipSize > 10)
                return false;
            for (int iRow = -1; iRow <= 1; iRow++) {
                for (int iCol = -1; iCol <= shipSize; iCol++) {
                    int jRow = iRow + row;
                    int jCol = iCol + col;
                    // check if the cell (or surrounding one) is occupied by another ship or out of bounds
                    if (jRow >= 0 && jRow < 10 && jCol >= 0 && jCol < 10 && gameBoard[jRow][jCol] == 1) {
                        return false;
                    }
                }
            }
        } else {
            if (row + shipSize > 10)
                return false;
            for (int iRow = -1; iRow <= shipSize; iRow++) {
                for (int iCol = -1; iCol <= 1; iCol++) {
                    int jRow = iRow + row;
                    int jCol = iCol + col;
                    // check if the cell (or surrounding one) is occupied by another ship or out of bounds
                    if (jRow >= 0 && jRow < 10 && jCol >= 0 && jCol < 10 && gameBoard[jRow][jCol] == 1 ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeShip(int[][] gameBoard, int row, int col, int shipSize, boolean isHorizontal) {
        if (isHorizontal) {
            for (int i = 0; i < shipSize; i++) {
                gameBoard[row][col + i] = 1;
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                gameBoard[row + i][col] = 1;
            }
        }
    }
}
