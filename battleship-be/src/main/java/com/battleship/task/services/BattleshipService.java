package com.battleship.task.services;

import com.battleship.task.dtos.ShootRequestDto;
import com.battleship.task.dtos.ShotResponse;
import com.battleship.task.models.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Arrays;
import java.util.Random;

@Service
@SessionScope
public class BattleshipService {

    private static final int SHOTS = 50;
    // ships sizes from big to small, because small ships first slows down the process of placing the big ones
    private final int[] SHIPS = {5, 4, 3, 3, 2, 2, 2, 1, 1, 1};
    private int[][] gameBoard;
    private int shotsLeft;
    private boolean gameInitialized;

    public BattleshipService() {
        this.gameInitialized = false;
    }

    public GameState startGame() {
        this.gameBoard = createGameBoard();
        this.shotsLeft = SHOTS;
        this.gameInitialized = true;
        return new GameState(shotsLeft, false);
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

    public ShotResponse shoot(ShootRequestDto shootRequestDto) {
        if (!gameInitialized) {
            throw new IllegalStateException("Game not initialized");
        }

        int x = shootRequestDto.x();
        int y = shootRequestDto.y();
        boolean hit = false;

        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            if (gameBoard[x][y] == 1) {
                gameBoard[x][y] = 2;
                hit = true;
            } else if (gameBoard[x][y] == 0) {
                shotsLeft--;
                gameBoard[x][y] = 3;
            }
        }

        boolean gameOver = isGameOver();
        boolean gameWon = isGameWon();
        return new ShotResponse(hit, shotsLeft, gameOver, gameWon);
    }

    public boolean isGameOver() {
        if (shotsLeft <= 0) return true;

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j] == 1) return false;
            }
        }
        return true;
    }

    public boolean isGameWon() {
        return isGameOver() && shotsLeft > 0;
    }
}
