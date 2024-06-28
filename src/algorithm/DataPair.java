package util;

public class DataPair { 

    private int firstId = -1;
    private int secondId = -1;
    private String pairing = null;
    private int connectionCost = -1;

    public DataPair(int firstId, int secondId, int connectionCost) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.connectionCost = connectionCost;
    }

    public DataPair(int firstId, int secondId, boolean linkedOut) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.pairing = linkedOut ? "Linked Out" : "Linked In";
    }

    @Override
    public String toString() {
        return "[First ID: " + firstId + ", Second ID: " + secondId + ", Pairing: " + pairing + "]";
    }
}
