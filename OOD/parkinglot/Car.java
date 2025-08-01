public class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleSize.CAR);
    }
    
    @Override
    public int getSpotsNeeded() {
        return 1;
    }
    
    @Override
    public boolean canFitInSpot(Spot spot) {
        return spot.getSpotType() == SpotType.COMPACT || spot.getSpotType() == SpotType.LARGE;
    }
}