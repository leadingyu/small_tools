public class Bus extends Vehicle {
    public Bus(String licensePlate) {
        super(licensePlate, VehicleSize.BUS);
    }
    
    @Override
    public int getSpotsNeeded() {
        return 5;
    }
    
    @Override
    public boolean canFitInSpot(Spot spot) {
        return spot.getSpotType() == SpotType.LARGE;
    }
}