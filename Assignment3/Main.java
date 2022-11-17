import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        // Setup initial values
        int height, iterations;
        double s = 100; double t = 100;
        double c1 = 0.75; double c2 = 1.0; double c3 = 1.25;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter metal alloy's height: ");
        height = scanner.nextInt();
        System.out.print("Enter the desired iterations: ");
        iterations = scanner.nextInt();

        // Create the piece of metal.
        MetalAlloy metalAlloy = new MetalAlloy(height, s, t, c1, c2, c3);

        // Wait for the GUI to finish creating.
        int latches = height * height * 2;
        GUI gui = new GUI(height*2, height, s, t);

        // Iteration starts here.
        int sleepTime = height * 10;
        for (int i = 0; i < iterations; i++) {
            CountDownLatch math = new CountDownLatch(latches);
            metalAlloy.calculate(math);
            math.await();

            // Send the current info to the GUI.
            for (int x = 0; x < metalAlloy.WIDTH; x++) {
                for (int y = 0; y < metalAlloy.HEIGHT; y++) {
                    gui.updateRegion(x, y, metalAlloy.regions[x][y].temperature);
                }
            }
            Thread.sleep(sleepTime);        // The GUI needs to catch up.
            metalAlloy.newCycle();          // Use the newly calculated values for next loop
        }
        System.out.println("Program executed successfully.");
    }
}
