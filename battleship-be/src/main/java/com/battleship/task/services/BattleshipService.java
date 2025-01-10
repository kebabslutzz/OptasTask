package com.battleship.task.services;

import com.battleship.task.dtos.ShootRequestDto;
import com.battleship.task.dtos.ShotResponseDto;
import com.battleship.task.models.GameBoard;
import com.battleship.task.models.GameState;
import com.battleship.task.models.Ship;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@SessionScope
public class BattleshipService {

    private static final int SHOTS = 25;
    private final int GAMEBOARD_SIZE = 10;
    // ships sizes from big to small, because small ships first slows down the process of placing the big ones
    private final int[] SHIPS = {5, 4, 3, 3, 2, 2, 2, 1, 1, 1};
    private GameBoard gameBoard;
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
        this.gameBoard = new GameBoard(GAMEBOARD_SIZE);
        this.shotsLeft = SHOTS;
        this.gameInitialized = true;
        this.shipIdCounter = 0;
        placeShips();
        return new GameState(shotsLeft, false);
    }

    private void placeShips() {
        Random random = new Random();

        for (int ship : SHIPS) {
            boolean isPlaced = false;
            while (!isPlaced) {
                int row = random.nextInt(10);
                int col = random.nextInt(10);
                boolean isHorizontal = random.nextBoolean();

                if (isPlaceAvailable(row, col, ship, isHorizontal)) {
                    List<int[]> shipCoordinates =  placeShip(row, col, ship, isHorizontal);
                    ships.add(new Ship(shipIdCounter++, shipCoordinates));
                    isPlaced = true;
                }
            }
        }
    }

    private boolean isPlaceAvailable(int row, int col, int shipSize, boolean isHorizontal) {
        if (isHorizontal) {
            if (col + shipSize > gameBoard.getSize())
                return false;
            for (int iRow = -1; iRow <= 1; iRow++) {
                for (int iCol = -1; iCol <= shipSize; iCol++) {
                    int jRow = iRow + row;
                    int jCol = iCol + col;
                    // check if the cell (or surrounding one) is occupied by another ship or out of bounds
                    if (jRow >= 0 && jRow < 10 && jCol >= 0 && jCol < 10 && !gameBoard.isEmpty(jRow, jCol)) {
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
                    if (jRow >= 0 && jRow < 10 && jCol >= 0 && jCol < 10 && !gameBoard.isEmpty(jRow, jCol)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private List<int[]> placeShip(int row, int col, int shipSize, boolean isHorizontal) {
        List<int[]> coordinates = new ArrayList<>();
        if (isHorizontal) {
            for (int i = 0; i < shipSize; i++) {
                gameBoard.placeShip(row, col + i, shipIdCounter);
                coordinates.add(new int[]{row, col + i});
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                gameBoard.placeShip(row + i, col, shipIdCounter);
                coordinates.add(new int[]{row + i, col});
            }
        }
        return coordinates;
    }

    public ShotResponseDto shoot(ShootRequestDto shootRequestDto) {
        if (!gameInitialized) {
            throw new IllegalStateException("Game not initialized");
        }

        int x = shootRequestDto.x();
        int y = shootRequestDto.y();
        boolean hit = false;
        List<int[]> destroyedShipCoordinates = null;

        if (x >= 0 && x < gameBoard.getSize() && y >= 0 && y < gameBoard.getSize()) {
            int shipId = gameBoard.getShipId(x, y);
            if (shipId != -1) {
                hit = true;
                Ship hitShip = ships.get(shipId);
                hitShip.hit();
                if (hitShip.isDestroyed()) {
                    destroyedShipCoordinates = hitShip.getCoordinates();
                }
            }  else {
                shotsLeft--;
            }
        }

        boolean gameOver = isGameOver();
        boolean gameWon = isGameWon();
        return new ShotResponseDto(hit, shotsLeft, gameOver, gameWon, destroyedShipCoordinates);
    }

    public boolean isGameOver() {
        if (shotsLeft <= 0) return true;

        for (Ship ship : ships) {
            if (!ship.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public boolean isGameWon() {
        return isGameOver() && shotsLeft > 0;
    }
}
