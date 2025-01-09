package com.battleship.task.controllers;

import com.battleship.task.dtos.ShootRequestDto;
import com.battleship.task.dtos.ShotResponse;
import com.battleship.task.models.GameState;
import com.battleship.task.services.BattleshipService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BattleShipControllerTest {

    @InjectMocks
    private BattleShipController battleShipController;

    @Mock
    private BattleshipService battleshipService;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        when(session.getAttribute("gameSession")).thenReturn("test-session");
    }

    @Test
    void testStartGame() {
        int shotsLeft = 25;
        boolean gameOver = false;

        GameState gameState = new GameState(shotsLeft, gameOver);
        when(battleshipService.startGame()).thenReturn(gameState);

        GameState response = battleShipController.startGame();
        assertNotNull(response);
        assertEquals(shotsLeft, response.shotsLeft());
        assertEquals(gameOver, response.gameOver());
        verify(session).setAttribute(eq("gameSession"), anyString());
    }

    @Test
    void testShootWithActiveSession() {
        ShootRequestDto shootRequestDto = new ShootRequestDto(0, 0);

        boolean hit = false;
        int shotsLeft = 24;
        boolean gameOver = false;
        boolean gameWon = false;
        List<int[]> destroyedShipCoordinates = null;

        ShotResponse shotResponse = new ShotResponse(hit, shotsLeft, gameOver, gameWon, destroyedShipCoordinates);
        when(battleshipService.shoot(shootRequestDto)).thenReturn(shotResponse);

        ShotResponse response = battleShipController.shoot(shootRequestDto);
        assertNotNull(response);
        assertEquals(hit, response.hit());
        assertEquals(shotsLeft, response.shotsLeft());
        assertEquals(gameOver, response.gameOver());
        assertEquals(gameWon, response.gameWon());
        assertEquals(destroyedShipCoordinates, response.destroyedShipCoordinates());
        verify(battleshipService).shoot(shootRequestDto);
    }

    @Test
    void testShootWithoutActiveSession() {
        when(session.getAttribute("gameSession")).thenReturn(null);

        ShootRequestDto shootRequestDto = new ShootRequestDto(0, 0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            battleShipController.shoot(shootRequestDto);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("No active game session", exception.getReason());
    }

}
