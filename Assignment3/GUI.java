import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import javax.swing.*;
import java.awt.*;

public class GUI {
    JFrame frame;
    JLabel[][] regions;
    final int WIDTH;
    final int HEIGHT;
    Color black = new Color(0x19181A);
    final double S, T;

    public GUI(int width, int height, double s, double t) throws InterruptedException, InvocationTargetException {
        this.frame = new JFrame();
        this.regions = new JLabel[width][height];
        WIDTH = width;
        HEIGHT = height;
        S = s;
        T = t;

        SwingUtilities.invokeAndWait(this::init);
    }

    public void init() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("CSC 375 Assignment 3");
        frame.getContentPane().setBackground(black);
        frame.setLayout(new GridLayout(WIDTH, HEIGHT, 1, 1));
        frame.setSize(1080, 720);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                regions[x][y] = new JLabel();
                regions[x][y].setOpaque(true);
                regions[x][y].setForeground(black);
                regions[x][y].setVerticalAlignment(JLabel.CENTER);
                regions[x][y].setHorizontalAlignment(JLabel.CENTER);
                frame.add(regions[x][y]);
            }
        }

        frame.setVisible(true);
    }

    public void updateRegion(int x, int y, double temp) {
        SwingUtilities.invokeLater(() -> {
            // Color stuff:
            //    Red    <->    Yellow   <->    Green    <->     CYAN    <->   BLUE
            // (255,0,0) <-> (255,255,0) <-> (0, 255, 0) <-> (0,255,255) <-> (0,0,255)
            double colorRange = 1020;
            double max = Math.max(S, T);
            double min = 20.0;
            double tempRange = max - min;

            // Update the color of each region.
            double colorValue = (temp-20) / tempRange * colorRange;

            if (colorValue <= 0) {
                regions[x][y].setBackground(new Color(0, 0, 255));
            } else if (colorValue > 0 && colorValue <= 255) {
                regions[x][y].setBackground(new Color(0, (int)colorValue, 255));
            } else if (colorValue > 255 && colorValue <= 510) {
                regions[x][y].setBackground(new Color(0, 255, (int)(510 - colorValue)));
            } else if (colorValue > 510 && colorValue <= 765) {
                regions[x][y].setBackground(new Color((int)(colorValue - 510), 255, 0));
            } else if (colorValue > 765 && colorValue <= 1020) {
                regions[x][y].setBackground(new Color(255, (int)(1020 - colorValue), 0));
            } else {
                regions[x][y].setBackground(new Color(255, 0, 0));
            }
        });
    }
}
