public class Section {
    final int x1, x2, y1, y2;
    // A section of (0, 3, 0, 3) looks like this :-
    // (0,0) (1,0) (2,0) (3,0)
    // (0,1) (1,1) (2,1) (3,1)
    // (0,2) (1,2) (2,2) (3,2)
    // (0,3) (1,3) (2,3) (3,3)

    public Section(int leftX, int leftY, int rightX, int rightY) {
        x1 = leftX;
        y1 = leftY;
        x2 = rightX;
        y2 = rightY;
    }

    public boolean isEven() {return (x2 - x1 + 1) % 2 == 0;}

    // Should only be used if self is not even.
    public int midX() {return (x1 + x2) / 2;}
    public int midY() {return (y1 + y2) / 2;}
    public int area() {return (x2-x1+1) * (y2-y1+1);}

    // Should only be used in the first division.
    public Section leftSection() {
        return new Section(x1, y1, (x1+ x2 - 1) / 2, y2);
    }

    // Should only be used in the first division.
    public Section rightSection() {
        return new Section((x1 + x2 + 1) / 2, y1, x2, y2);
    }

    public Section topLeftSection() {
        if (isEven()) {
            int rightX = (x1 + x2 - 1) / 2;
            int rightY = (y1 + y2 - 1) / 2;
            return new Section(x1, y1, rightX, rightY);
        } else {
            int rightX = ((x1 + x2) / 2) - 1;
            int rightY = ((y1 + y2) / 2) - 1;
            return new Section(x1, y1, rightX, rightY);
        }
    }

    public Section topRightSection() {
        if (isEven()) {
            int leftX = (x1 + x2 + 1) / 2;
            int rightY = (y1 + y2 - 1) / 2;
            return new Section(leftX, y1, x2, rightY);
        } else {
            int leftX = ((x1 + x2) / 2) + 1;
            int rightY = ((y1 + y2) / 2) + 1;
            return new Section(leftX, y1, x2, rightY);
        }
    }

    public Section bottomLeftSection() {
        if (isEven()) {
            int rightX = (x1 + x2 - 1) / 2;
            int leftY = (y1 + y2 + 1) / 2;
            return new Section(x1, leftY, rightX, y2);
        } else {
            int rightX = ((x1 + x2) / 2) - 1;
            int leftY = ((y1 + y2) / 2) + 1;
            return new Section(x1, leftY, rightX, y2);
        }
    }

    public Section bottomRightSection() {
        if (isEven()) {
            int leftX = (x1 + x2 + 1) / 2;
            int leftY = (y1 + y2 + 1) / 2;
            return new Section(leftX, leftY, x2, y2);
        } else {
            int leftX = ((x1 + x2) / 2) + 1;
            int leftY = ((y1 + y2) / 2) + 1;
            return new Section(leftX, leftY, x2, y2);
        }
    }
}
