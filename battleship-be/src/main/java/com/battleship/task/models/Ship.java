package com.battleship.task.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Ship {
    private final int id;
    private final List<int[]> coordinates;
    private int hp;

    public Ship(int id, List<int[]> coordinates) {
        this.id = id;
        this.coordinates = coordinates;
        this.hp = coordinates.size();
    }

    public void hit() {
        this.hp--;
    }

    public boolean isDestroyed() {
        return this.hp == 0;
    }
}
