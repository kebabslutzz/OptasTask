package com.battleship.task.services;

import com.battleship.task.dtos.ShootRequestDto;
import com.battleship.task.dtos.ShotResponse;
import com.battleship.task.models.GameState;
import com.battleship.task.models.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BattleShipServiceTest {

    @Autowired
    private BattleshipService battleshipService;

    @BeforeEach
    void setUp() {
        battleshipService = new BattleshipService();
    }

    @Test
    void testStartGame() {
        GameState gameState = battleshipService.startGame();
        assertNotNull(gameState);
        assertEquals(25, gameState.shotsLeft());
        assertFalse(gameState.gameOver());
    }

    @Test
    void testShootHitsShipWithFakeBoard() throws Exception {
        int[][] gameBoard = new int[10][10];
        gameBoard[0][0] = 1; // Placing a ship at (0,0)
        gameBoard[0][1] = 1; // Place a ship at (0,1)

        List<int[]> shipCoordinates = new ArrayList<>();
        shipCoordinates.add(new int[]{0, 0});
        shipCoordinates.add(new int[]{0, 1});

        boolean gameInitialized = true;

        setupFakeBoard(gameBoard, shipCoordinates, gameInitialized);

        ShootRequestDto shootRequestDto = new ShootRequestDto(0, 0);
        ShotResponse shotResponse = battleshipService.shoot(shootRequestDto);
        assertNotNull(shotResponse);
        assertTrue(shotResponse.hit());
        assertEquals(25, shotResponse.shotsLeft()); // Ship is hit so no shots are lost
        assertFalse(shotResponse.gameOver());
        assertFalse(shotResponse.gameWon());
        assertNull(shotResponse.destroyedShipCoordinates());
    }

    @Test
    void testShootHMissesShipWithFakeBoard() throws Exception {
        int[][] gameBoard = new int[10][10];
        gameBoard[0][0] = 1; // Placing a ship at (0,0)
        gameBoard[0][1] = 1; // Place a ship at (0,1)

        List<int[]> shipCoordinates = new ArrayList<>();
        shipCoordinates.add(new int[]{0, 0});
        shipCoordinates.add(new int[]{0, 1});

        boolean gameInitialized = true;

        setupFakeBoard(gameBoard, shipCoordinates, gameInitialized);

        ShootRequestDto shootRequestDto = new ShootRequestDto(1, 1);
        ShotResponse shotResponse = battleshipService.shoot(shootRequestDto);
        assertNotNull(shotResponse);
        assertFalse(shotResponse.hit());
        assertEquals(24, shotResponse.shotsLeft()); // Ship is missed so 1 shot is lost
        assertFalse(shotResponse.gameOver());
        assertFalse(shotResponse.gameWon());
        assertNull(shotResponse.destroyedShipCoordinates());
    }

    @Test
    void testShootDestroysShipWithFakeBoard() throws Exception {
        int[][] gameBoard = new int[10][10];
        gameBoard[0][0] = 1; // Placing a ship at (0,0)
        gameBoard[0][1] = 1; // Place a ship at (0,1)

        List<int[]> shipCoordinates = new ArrayList<>();
        shipCoordinates.add(new int[]{0, 0});
        shipCoordinates.add(new int[]{0, 1});

        boolean gameInitialized = true;

        setupFakeBoard(gameBoard, shipCoordinates, gameInitialized);

        // Making two shots to destroy the ship
        ShootRequestDto shootRequestDto1 = new ShootRequestDto(0, 0);
        ShootRequestDto shootRequestDto2 = new ShootRequestDto(0, 1);

        ShotResponse shotResponse1 = battleshipService.shoot(shootRequestDto1);
        assertNotNull(shotResponse1);
        assertTrue(shotResponse1.hit());
        assertEquals(25, shotResponse1.shotsLeft()); // Ship is hit so no shots are lost
        assertFalse(shotResponse1.gameOver());
        assertFalse(shotResponse1.gameWon());
        assertNull(shotResponse1.destroyedShipCoordinates());

        ShotResponse shotResponse2 = battleshipService.shoot(shootRequestDto2);
        assertNotNull(shotResponse2);
        assertTrue(shotResponse2.hit());
        assertEquals(25, shotResponse2.shotsLeft()); // Ship is hit so no shots are lost
        assertTrue(shotResponse2.gameOver());
        assertTrue(shotResponse2.gameWon());
        assertNotNull(shotResponse2.destroyedShipCoordinates());
        assertEquals(2, shotResponse2.destroyedShipCoordinates().size());
        assertEquals(shipCoordinates, shotResponse2.destroyedShipCoordinates());
    }

    @Test
    void testShootWithFakeBoardGameNotInitialized() throws Exception {
        int[][] gameBoard = new int[10][10];
        gameBoard[0][0] = 1; // Placing a ship at (0,0)

        List<int[]> shipCoordinates = new ArrayList<>();
        shipCoordinates.add(new int[]{0, 0});

        boolean gameInitialized = false;

        setupFakeBoard(gameBoard, shipCoordinates, gameInitialized);

        ShootRequestDto shootRequestDto = new ShootRequestDto(0, 0);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            battleshipService.shoot(shootRequestDto);
        });

        assertEquals("Game not initialized", exception.getMessage());
    }

    @Test
    void testFillBoard() throws Exception {
        int[][] gameBoard = new int[10][10];

        Method fillBoardMethod = BattleshipService.class.getDeclaredMethod("fillBoard", int[][].class);
        fillBoardMethod.setAccessible(true);
        fillBoardMethod.invoke(battleshipService, (Object) gameBoard);

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                assertEquals(0, gameBoard[i][j]);
            }
        }
    }

    @Test
    void testPlaceShip() throws Exception {
        int[][] gameBoard = new int[10][10];
        Method placeShipMethod = BattleshipService.class.getDeclaredMethod("placeShip", int[][].class, int.class, int.class, int.class, boolean.class);
        placeShipMethod.setAccessible(true);
        placeShipMethod.invoke(battleshipService, gameBoard, 0, 0, 2, true);

        // Asserting that [0,0] and [0,1] are 1 (SHIP)
        assertEquals(1, gameBoard[0][0]);
        assertEquals(1, gameBoard[0][1]);

        // Asserting that [0,2] is not 1 (SHIP)
        assertEquals(0, gameBoard[0][2]);

        // Asserting that [1,0], [1,1], and [1,2] are not 1 (SHIP). Places around the placed ship should be empty
        assertEquals(0, gameBoard[1][0]);
        assertEquals(0, gameBoard[1][1]);
        assertEquals(0, gameBoard[1][2]);
    }

    @Test
    void testGameOver() throws Exception {
        int[][] gameBoard = new int[10][10];

        List<int[]> shipCoordinates = new ArrayList<>();
        shipCoordinates.add(new int[]{9, 9});
        gameBoard[9][9] = 1; // Placing a ship at (9,9)
        boolean gameInitialized = true;

        setupFakeBoard(gameBoard, shipCoordinates, gameInitialized);

        for (int i = 0; i < 25; i++) {
            ShootRequestDto shootRequestDto = new ShootRequestDto(i / 10, i % 10);
            ShotResponse shotResponse = battleshipService.shoot(shootRequestDto);
            assertNotNull(shotResponse);
            assertFalse(shotResponse.hit());
            assertEquals(24 - i, shotResponse.shotsLeft());
            if (i < 24) {
                assertFalse(shotResponse.gameOver());
            } else {
                assertTrue(shotResponse.gameOver());
            }
        }
    }

    @Test
    void testFindShipByCoordinate() throws Exception {
        int[][] gameBoard = new int[10][10];
        gameBoard[0][0] = 1; // Placing a ship at (0,0)
        gameBoard[0][1] = 1; // Placing a ship at (0,1)

        List<int[]> shipCoordinates = new ArrayList<>();
        shipCoordinates.add(new int[]{0, 0});
        shipCoordinates.add(new int[]{0, 1});

        boolean gameInitialized = true;

        setupFakeBoard(gameBoard, shipCoordinates, gameInitialized);

        Method findShipByCoordinateMethod = BattleshipService.class.getDeclaredMethod("findShipByCoordinate", int.class, int.class);
        findShipByCoordinateMethod.setAccessible(true);
        Ship foundShip = (Ship)findShipByCoordinateMethod.invoke(battleshipService, 0, 0);

        assertNotNull(foundShip);
        assertEquals(0, foundShip.getId());
        assertEquals(shipCoordinates, foundShip.getCoordinates());

        Ship notFoundShip = (Ship)findShipByCoordinateMethod.invoke(battleshipService, 1, 1);
        assertNull(notFoundShip);
    }

    private void setupFakeBoard(int[][] gameBoard, List<int[]> shipCoordinates, boolean gameInitialized) throws Exception {
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship(0, shipCoordinates));
        setGameBoard(gameBoard, ships, gameInitialized);
    }

    private void setGameBoard(int[][] gameBoard, List<Ship> ships, boolean gameInitialized) throws Exception {
        Field gameBoardField = BattleshipService.class.getDeclaredField("gameBoard");
        gameBoardField.setAccessible(true);
        gameBoardField.set(battleshipService, gameBoard);

        Field shipsField = BattleshipService.class.getDeclaredField("ships");
        shipsField.setAccessible(true);
        shipsField.set(battleshipService, ships);

        Field gameInitializedField = BattleshipService.class.getDeclaredField("gameInitialized");
        gameInitializedField.setAccessible(true);
        gameInitializedField.set(battleshipService, gameInitialized);

        Field shotsLeftField = BattleshipService.class.getDeclaredField("shotsLeft");
        shotsLeftField.setAccessible(true);
        shotsLeftField.set(battleshipService, 25);
    }
}
