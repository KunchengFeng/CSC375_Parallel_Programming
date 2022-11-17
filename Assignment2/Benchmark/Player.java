package Benchmark;

import java.util.concurrent.ThreadLocalRandom;

public class Player {
    int health;
    boolean alive;
    int someNumber;     // No longer relevant

    public Player() {
        alive = true;
        health = 100;
    }

    public void heal() {
        if (alive) {
            health += ThreadLocalRandom.current().nextInt(1, 35);
            if (health > 200) {health = 200;}
        } else {
            alive = true; health = 100;
        }
    }

    public void damage() {
        if (alive) {
            health -= ThreadLocalRandom.current().nextInt(1, 35);
            if (health <= 0) {health=0;alive=false;}
        }
    }

    // No longer relevant
    public void revive() {alive = true; health = 100;}
}
