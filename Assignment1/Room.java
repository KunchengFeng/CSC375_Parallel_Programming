import java.util.concurrent.ThreadLocalRandom;

public class Room {
    /* 4 flavors, 0 is workstation with a base score of 10,
     * 1, 2, and 3 have no base score, but can increase the workstation nearby.
     * Details are in Factory.java */
    private int flavor, score;

    // Constructor
    Room() { flavor = 0; score = 10; }

    // Randomize new flavors and subFlavors.
    void randomize() {
        flavor = ThreadLocalRandom.current().nextInt(0, 4);
        if (flavor == 0) {
            score = 10;
        } else {
            score = 0;
        }
    }

    // Simple functions.
    int getFlavor() { return flavor; }
    int getScore() { return score; }
    void setFlavor(int flavor) { this.flavor = flavor; }
    void setScore(int score) { this.score = score; }

    Room copy() {
        Room newRoom = new Room();
        newRoom.setFlavor(flavor);
        newRoom.setScore(score);
        return newRoom;
    }

    void addScore(int number) {
        if (flavor == 0) {
            score += number;
        }
    }
}
