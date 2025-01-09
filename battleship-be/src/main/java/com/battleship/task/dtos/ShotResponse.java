package com.battleship.task.dtos;

import java.util.List;

public record ShotResponse(boolean hit, int shotsLeft, boolean gameOver, boolean gameWon, List<int[]> destroyedShipCoordinates) { }
