import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;
import java.util.ArrayList;

public class MetalAlloy {
    final Region [][] regions;
    final double [] C;
    final double S, T;
    final int WIDTH, HEIGHT;

    public MetalAlloy(int height, double s, double t, double c1, double c2, double c3) {
        // Save the passed in variables.
        S = s; T = t;
        C = new double[3];
        C[0] = c1;
        C[1] = c2;
        C[2] = c3;
        HEIGHT = height;
        WIDTH = HEIGHT * 2;

        // Initialize the regions in this metal alloy.
        regions = new Region[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                regions[x][y] = new Region();
            }
        }
    }

    public void calculate(CountDownLatch latch) {
        ForkJoinPool pool = new ForkJoinPool();
        Section section = new Section(0, 0, WIDTH-1, HEIGHT-1);
        Heat task = new Heat(this, section, latch);
        pool.invoke(task);
    }

    // Return a list of qualified neighbors, usually 4.
    public Region[] getNeighbors(int x, int y) {
        // Gather the potential neighbors.
        ArrayList<Region> neighbors = new ArrayList<>(4);
        if (x-1 >= 0) {
            neighbors.add(this.regions[x-1][y]);
        }
        if (x+1 < WIDTH) {
            neighbors.add(this.regions[x+1][y]);
        }
        if (y-1 >= 0) {
            neighbors.add(this.regions[x][y-1]);
        }
        if (y+1 < HEIGHT) {
            neighbors.add(this.regions[x][y+1]);
        }

        // Convert it into an array and return it.
        Region[] result = new Region[neighbors.size()];
        for (int i = 0; i < neighbors.size(); i++) {
            result[i] = neighbors.get(i);
        }
        return result;
    }

    // Used for checking values.
    public void printState() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                System.out.print("Region[" + x + "][" + y +"]: ");
                regions[x][y].printState();
                System.out.print(",  ");
            }
            System.out.println();
        }
    }

    // Set the temperature of each region to the calculated value, as they will be used in the next cycle.
    public void newCycle() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                regions[x][y].temperature = regions[x][y].newTemperature;
            }
        }
    }
}



// Calculate a new temperature for each region.
class Heat extends RecursiveAction {
    final int THRESHOLD = 250;
    final MetalAlloy metalAlloy;
    CountDownLatch latch;
    Section section;

    Heat(MetalAlloy metalAlloy, Section section, CountDownLatch latch) {
        this.metalAlloy = metalAlloy;
        this.section = section;
        this.latch = latch;
    }

    private double outerSigma(MetalAlloy metalAlloy, int x, int y) {
        double result = 0;
        Region[] neighbors = metalAlloy.getNeighbors(x, y);
        for (int i = 0; i < metalAlloy.C.length; i++) {
            result += metalAlloy.C[i] * innerSigma(neighbors, i) / neighbors.length;
        }
        return result;
    }

    private double innerSigma(Region[] neighbors, int metalType) {
        double result = 0;
        for (Region neighbor : neighbors) {
            result += neighbor.temperature * neighbor.PERCENT[metalType] / 100;
        }
        return result;
    }

    // No longer relevant due to reimplementation.
    private boolean inBound(int x, int y) {
        return x >= 0 && y >= 0
                && x < this.metalAlloy.WIDTH
                && y < this.metalAlloy.HEIGHT;
    }

    @Override
    protected void compute() {
        int x1 = section.x1; int y1 = section.y1;
        int x2 = section.x2; int y2 = section.y2;

        if (section.area() <= THRESHOLD) {
            // Compute this section if it's small enough.
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    if (x == 0 && y == 0) {
                        metalAlloy.regions[x][y].newTemperature = metalAlloy.S;
                    } else if (x == metalAlloy.WIDTH-1 && y == metalAlloy.HEIGHT-1) {
                        metalAlloy.regions[x][y].newTemperature = metalAlloy.T;
                    } else {
                        metalAlloy.regions[x][y].newTemperature = outerSigma(metalAlloy, x, y);
                    }
                    latch.countDown();
                }
            }

        } else if ((y2-y1) != (x2-x1)) {
            // First division, split section into 2 square sections.
            Section left = section.leftSection();
            Section right = section.rightSection();
            invokeAll(new Heat(metalAlloy, left, latch)
                    , new Heat(metalAlloy, right, latch));

        } else {
            if (!section.isEven()) {
                // Odd division, need to do the middle part itself.
                int midX = section.midX();
                int midY = section.midY();
                // Horizontal part.
                for (int x = x1; x <= x2; x++) {
                    metalAlloy.regions[x][midY].newTemperature = outerSigma(metalAlloy, x, midY);
                    latch.countDown();
                }
                // Vertical part.
                for (int y = y1; y <= y2; y++) {
                    if (y != midY) {
                        metalAlloy.regions[midX][y].newTemperature = outerSigma(metalAlloy, midX, y);
                        latch.countDown();
                    }
                }
            }
            Section topLeft = section.topLeftSection();
            Section topRight = section.topRightSection();
            Section downLeft = section.bottomLeftSection();
            Section downRight = section.bottomRightSection();
            invokeAll(new Heat(metalAlloy, topLeft, latch)
                    , new Heat(metalAlloy, topRight, latch)
                    , new Heat(metalAlloy, downLeft, latch)
                    , new Heat(metalAlloy, downRight, latch));
        }
    }
}
