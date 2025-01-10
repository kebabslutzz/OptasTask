package com.battleship.task.dtos;

import java.util.List;

public record ShotResponseDto(boolean hit, int shotsLeft, boolean gameOver, boolean gameWon, List<int[]> destroyedShipCoordinates) { }
