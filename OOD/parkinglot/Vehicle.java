public abstract class Vehicle {
    protected String licensePlate;
    protected VehicleSize size;
    
    public Vehicle(String licensePlate, VehicleSize size) {
        this.licensePlate = licensePlate;
        this.size = size;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public VehicleSize getSize() {
        return size;
    }
    
    public abstract int getSpotsNeeded();
    public abstract boolean canFitInSpot(Spot spot);
}