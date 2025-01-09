package com.battleship.task.services;

import com.battleship.task.dtos.ShootRequestDto;
import com.battleship.task.dtos.ShotResponse;
import com.battleship.task.models.GameState;
import com.battleship.task.models.Ship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@SessionScope
public class BattleshipService {

    private static final int SHOTS = 25;
    // ships sizes from big to small, because small ships first slows down the process of placing the big ones
    private final int[] SHIPS = {5, 4, 3, 3, 2, 2, 2, 1, 1, 1};
    private final int EMPTY = 0;
    private final int SHIP = 1;
    private final int HIT = 2;
    private final int MISS = 3;
    private int[][] gameBoard;
    private int shotsLeft;
    private boolean gameInitialized;
    private List<Ship> ships;
    private int shipIdCounter;

    public BattleshipService() {
        this.gameInitialized = false;
        this.ships = new ArrayList<>();
        this.shipIdCounter = 0;
    }

    public GameState startGame() {
        this.ships = new ArrayList<>();
        this.gameBoard = createGameBoard();
        this.shotsLeft = SHOTS;
        this.gameInitialized = true;
        this.shipIdCounter = 0;
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
            Arrays.fill(gameBoard[i], EMPTY);
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
                    List<int[]> shipCoordinates =  placeShip(gameBoard, row, col, ship, isHorizontal);
                    ships.add(new Ship(shipIdCounter++, shipCoordinates));
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
                    if (jRow >= 0 && jRow < 10 && jCol >= 0 && jCol < 10 && gameBoard[jRow][jCol] == SHIP) {
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
                    if (jRow >= 0 && jRow < 10 && jCol >= 0 && jCol < 10 && gameBoard[jRow][jCol] == SHIP ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private List<int[]> placeShip(int[][] gameBoard, int row, int col, int shipSize, boolean isHorizontal) {
        List<int[]> coordinates = new ArrayList<>();
        if (isHorizontal) {
            for (int i = 0; i < shipSize; i++) {
                gameBoard[row][col + i] = SHIP;
                coordinates.add(new int[]{row, col + i});
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                gameBoard[row + i][col] = SHIP;
                coordinates.add(new int[]{row + i, col});
            }
        }
        return coordinates;
    }

    public ShotResponse shoot(ShootRequestDto shootRequestDto) {
        if (!gameInitialized) {
            throw new IllegalStateException("Game not initialized");
        }

        int x = shootRequestDto.x();
        int y = shootRequestDto.y();
        boolean hit = false;
        List<int[]> destroyedShipCoordinates = null;

        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            if (gameBoard[x][y] == SHIP) {
                gameBoard[x][y] = HIT;
                hit = true;
                Ship hitShip = findShipByCoordinate(x, y);
                if (hitShip != null) {
                    hitShip.hit();
                    if (hitShip.isDestroyed()) {
                        destroyedShipCoordinates = hitShip.getCoordinates();
                    }
                }
            } else if (gameBoard[x][y] == EMPTY) {
                shotsLeft--;
                gameBoard[x][y] = MISS;
            }
        }

        boolean gameOver = isGameOver();
        boolean gameWon = isGameWon();
        return new ShotResponse(hit, shotsLeft, gameOver, gameWon, destroyedShipCoordinates);
    }

    private Ship findShipByCoordinate(int x, int y) {
        for (Ship ship : ships) {
            for (int[] coord : ship.getCoordinates()) {
                if (coord[0] == x && coord[1] == y) {
                    return ship;
                }
            }
        }
        return null;
    }

    public boolean isGameOver() {
        if (shotsLeft <= 0) return true;

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j] == SHIP) return false;
            }
        }
        return true;
    }

    public boolean isGameWon() {
        return isGameOver() && shotsLeft > 0;
    }
}
