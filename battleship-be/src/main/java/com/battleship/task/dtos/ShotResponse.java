package com.battleship.task.dtos;

public record ShotResponse(boolean hit, int shotsLeft, boolean gameOver) { }
