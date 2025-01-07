package com.battleship.task.controllers;

import com.battleship.task.dtos.ShootRequestDto;
import com.battleship.task.dtos.ShotResponse;
import com.battleship.task.models.GameState;
import com.battleship.task.services.BattleshipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/battleships")
@RequiredArgsConstructor
@Validated
public class BattleShipController {

    private final BattleshipService battleshipService;
    private final HttpSession session;

//    @GetMapping
//    public int[][] getGameBoard(){
//        return battleshipService.getGameBoard();
//    }

    @PostMapping("/start")
    public GameState startGame() {
        session.setAttribute("gameSession", UUID.randomUUID().toString());
        return battleshipService.startGame();
    }

    @PostMapping("/shoot")
    public ShotResponse shoot(@RequestBody ShootRequestDto shootRequestDto) {
        if (session.getAttribute("gameSession") == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No active game session");
        }
        return battleshipService.shoot(shootRequestDto);
    }
}