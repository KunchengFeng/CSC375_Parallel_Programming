public class Region {
    final int [] PERCENT;
    double temperature;
    double newTemperature;

    public Region() {
        // Create 3 metal percentages.
        // A total variation of 25% -> ~8% variation for each -> 4% up or down.
        int max = 37;
        int min = 29;
        PERCENT = new int[3];
        PERCENT[0] = (int) ((Math.random() * (max - min)) + min);
        PERCENT[1] = (int) ((Math.random() * (max - min)) + min);
        PERCENT[2] = 100 - PERCENT[0] - PERCENT[1];

        // Assume room temperature in Celsius.
        temperature = 20.0;
    }

    // Used for checking values.
    public void printState() {
        System.out.print("Metal percentage:");
        for (int i : PERCENT) {
            System.out.print(" " + i);
        }
        System.out.print(", Temperature: " + temperature);
    }
}
