import javax.swing.*;
import java.awt.*;

public class GUI {
    JFrame frame;
    JLabel[][] blocks;
    int blocksX;
    int blocksY;
    Color black = new Color(0x19181A);
    Color green = new Color(0x479761);
    Color yellow = new Color(0xCEBC81);
    Color blue = new Color(0x45A291);
    Color grey = new Color(0xB19F9E);

    public GUI(int length, int height) {
        this.frame = new JFrame();
        this.blocks = new JLabel[length][height];
        blocksX = length;
        blocksY = height;
    }

    public void init() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("CSC 375 Assignment 1");
        frame.getContentPane().setBackground(black);
        frame.setLayout(new GridLayout(blocksX, blocksY, 1, 1));
        frame.setSize(1080, 720);

        for (int x = 0; x < blocksX; x++) {
            for (int y = 0; y < blocksY; y++) {
                blocks[x][y] = new JLabel();
                blocks[x][y].setOpaque(true);
                blocks[x][y].setForeground(black);
                blocks[x][y].setVerticalAlignment(JLabel.CENTER);
                blocks[x][y].setHorizontalAlignment(JLabel.CENTER);
                frame.add(blocks[x][y]);
            }
        }

        frame.setVisible(true);
    }

    public void showFactory(Factory factory) {
        for (int x = 0; x < blocksX; x++) {
            for (int y = 0; y < blocksY; y++) {
                int flavor = factory.rooms[x][y].getFlavor();
                blocks[x][y].setText(Integer.toString(flavor));

                if (flavor == 0) {
                    blocks[x][y].setBackground(grey);
                } else if (flavor == 1) {
                    blocks[x][y].setBackground(green);
                } else if (flavor == 2) {
                    blocks[x][y].setBackground(blue);
                } else {
                    blocks[x][y].setBackground(yellow);
                }

                frame.setTitle("Affinity: " + factory.getAffinity());
            }
        }
    }
}
