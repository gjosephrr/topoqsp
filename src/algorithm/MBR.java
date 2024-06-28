package util;

public class MBR { 

    private double lowerLeftX;  
    private double lowerLeftY;  
    private double upperRightX; 
    private double upperRightY; 

    public MBR(double lowerLeftX, double lowerLeftY, double upperRightX, double upperRightY) {
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLeftY;
        this.upperRightX = upperRightX;
        this.upperRightY = upperRightY;
    }

    public double getLowerLeftX() {  
        return lowerLeftX;
    }

    public double getLowerLeftY() { 
        return lowerLeftY;
    }

    public double getUpperRightX() { 
        return upperRightX;
    }

    public double getUpperRightY() {  
        return upperRightY;
    }

    @Override  
    public String toString() {
        return "[" + lowerLeftX + ", " + lowerLeftY + ", " + upperRightX + ", " + upperRightY + "]";
    }

    public double getCenterX() {  
        return (upperRightX + lowerLeftX) * 0.5;
    }

    public double getCenterY() {  
        return (upperRightY + lowerLeftY) * 0.5;
    }

    public double getWidth() {  
        return upperRightX - lowerLeftX;
    }

    public double getHeight() { 
        return upperRightY - lowerLeftY;
    }
}
}
