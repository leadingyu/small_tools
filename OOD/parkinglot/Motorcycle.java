public class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleSize.MOTORCYCLE);
    }
    
    @Override
    public int getSpotsNeeded() {
        return 1;
    }
    
    @Override
    public boolean canFitInSpot(Spot spot) {
        return true;
    }
}