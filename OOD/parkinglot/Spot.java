public class Spot {
    private int level;
    private int row;
    private int spotNumber;
    private SpotType spotType;
    private Vehicle vehicle;
    
    public Spot(int level, int row, int spotNumber, SpotType spotType) {
        this.level = level;
        this.row = row;
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.vehicle = null;
    }
    
    public boolean isAvailable() {
        return vehicle == null;
    }
    
    public boolean parkVehicle(Vehicle v) {
        if (!isAvailable()) {
            return false;
        }
        this.vehicle = v;
        return true;
    }
    
    public Vehicle unparkVehicle() {
        Vehicle v = this.vehicle;
        this.vehicle = null;
        return v;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getSpotNumber() {
        return spotNumber;
    }
    
    public SpotType getSpotType() {
        return spotType;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
}