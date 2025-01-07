package com.battleship.task.controllers;

import com.battleship.task.services.BattleshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/battleships")
@RequiredArgsConstructor
@Validated
public class BattleShipController {

    private final BattleshipService battleshipService;

    @GetMapping
    public int[][] getGameBoard(){
        return battleshipService.getGameBoard();
    }
}
