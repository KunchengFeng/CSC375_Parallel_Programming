import java.util.concurrent.ThreadLocalRandom;

public class Factory {
    private final int length;
    private final int height;
    private long affinity;
    Room[][] rooms;

    Factory(int length, int height) {
        this.length = length;
        this.height = height;
        rooms = new Room[length][height];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                rooms[i][j] = new Room();
            }
        }
    }

    public Factory copy() {
        Factory newFactory = new Factory(length, height);
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                newFactory.rooms[x][y] = rooms[x][y].copy();
            }
        }
        newFactory.setAffinity(affinity);
        return newFactory;
    }

    // -------------------------------------------- Functions associated with affinity ------------------------------ //
    // Change 1 room at a time.
    public void mutate() {
        int ranX = ThreadLocalRandom.current().nextInt(0, length);
        int ranY = ThreadLocalRandom.current().nextInt(0, height);
        rooms[ranX][ranY].randomize();
    }

    // Change the whole factory at once.
    public void randomize() {
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                rooms[x][y].randomize();
            }
        }
    }

    private void calculateRoomScore(int x, int y) {
        // Only flavor #0 rooms have scores, the others are supplementary rooms.
        if (rooms[x][y].getFlavor() == 0) {
            rooms[x][y].setScore(10);
        } else {
            rooms[x][y].setScore(0);
            return;
        }

        boolean hasTools = false;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                int radiusX = Math.abs(x - i);
                int radiusY = Math.abs(y - j);
                if ((radiusX < 4 && radiusY < 4) && rooms[i][j].getFlavor() == 1) {
                    rooms[x][y].addScore(1);
                }
                if ((radiusX < 3 && radiusY < 3) && rooms[i][j].getFlavor() == 2) {
                    rooms[x][y].addScore(2);
                }
                if ((radiusX < 2 && j == y) || (radiusY < 2 && i == x)) {
                    if (rooms[i][j].getFlavor() == 3) {
                        hasTools = true;
                    }
                }
            }
        }

        if (hasTools) {
            int i = rooms[x][y].getScore() * 2;
            rooms[x][y].addScore(i);
        }
    }

    public void calculateAffinity() {
        affinity = 0;
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                calculateRoomScore(x, y);
                affinity += rooms[x][y].getScore();
            }
        }
    }

    // ----------------------------------------------- End of affinity functions ------------------------------------ //


    // ----------------------------------------- Functions associated with produce child -----------------------------//
    // Left and right inheritance.
    private void inheritLAR (Factory parent1, Factory parent2) {
        if (length % 2 == 0) {
            // Even length.
            int midPoint = length / 2;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < midPoint; x++) {
                    this.rooms[x][y] = parent1.rooms[x][y].copy();
                    this.rooms[length - 1 - x][y] = parent2.rooms[length - 1 - x][y].copy();
                }
            }
        } else {
            // Uneven length.
            int midPoint = (length - 1) / 2;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < midPoint; x++) {
                    this.rooms[x][y] = parent1.rooms[x][y].copy();
                    this.rooms[length - 1 - x][y] = parent2.rooms[length - 1 - x][y].copy();
                }
                this.rooms[midPoint][y] = parent1.rooms[midPoint][y].copy();
            }
        }
    }

    // Up and down inheritance.
    private void inheritUAD (Factory parent1, Factory parent2) {
        if (height % 2 == 0) {
            // Even width.
            int midPoint = height / 2;
            for (int x = 0; x < length; x++) {
                for (int y = 0; y < midPoint; y++) {
                    this.rooms[x][y] = parent1.rooms[x][y].copy();
                    this.rooms[x][height - 1 - y] = parent2.rooms[x][height - 1 - y].copy();
                }
            }
        } else {
            // Uneven width.
            int midPoint = (height - 1) / 2;
            for (int x = 0; x < length; x++) {
                for (int y = 0; y < midPoint; y++) {
                    this.rooms[x][y] = parent1.rooms[x][y].copy();
                    this.rooms[x][height - 1 - y] = parent2.rooms[x][height - 1 - y].copy();
                }
                this.rooms[x][midPoint] = parent1.rooms[x][midPoint].copy();
            }
        }
    }

    // Alternate inheritance.
    private void inheritALT (Factory parent1, Factory parent2) {
        boolean turn = false;
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                if (turn) {
                    this.rooms[x][y] = parent1.rooms[x][y].copy();
                    turn = false;
                } else {
                    this.rooms[x][y] = parent2.rooms[x][y].copy();
                    turn = true;
                }
            }
        }
    }

    public Factory produceChild(Factory partner) {
        Factory child = new Factory(length, height);             // Child is same size as parent.
        int style = ThreadLocalRandom.current().nextInt(0, 3);

        if (style == 0) {
            child.inheritLAR(this, partner);
        } else if (style == 1){
            child.inheritUAD(this, partner);
        } else {
            child.inheritALT(this, partner);
        }

        return child;
    }

    // -------------------------------------- End of produce child functions ------------------------------------- //

    public void setAffinity(long affinity) { this.affinity = affinity; }
    public long getAffinity() { return affinity; }

    public void printFactory() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(rooms[i][j].getFlavor());
            }
            System.out.print("\n");
        }
        System.out.println("Affinity: " + affinity);
    }
}
