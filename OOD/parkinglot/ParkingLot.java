import java.util.*;

public class ParkingLot {
    private int levels;
    private int numRows;
    private int spotsPerRow;
    private Spot[][][] spots;
    private Map<String, Vehicle> parkedVehicles;
    private Map<String, List<Spot>> vehicleSpots;
    
    public ParkingLot(int levels, int numRows, int spotsPerRow) {
        this.levels = levels;
        this.numRows = numRows;
        this.spotsPerRow = spotsPerRow;
        this.spots = new Spot[levels][numRows][spotsPerRow];
        this.parkedVehicles = new HashMap<>();
        this.vehicleSpots = new HashMap<>();
        
        initializeSpots();
    }
    
    private void initializeSpots() {
        int motorcycleEnd = spotsPerRow / 4;
        int compactEnd = motorcycleEnd + (spotsPerRow / 4) * 2;
        
        for (int level = 0; level < levels; level++) {
            for (int row = 0; row < numRows; row++) {
                for (int spot = 0; spot < spotsPerRow; spot++) {
                    SpotType spotType;
                    if (spot < motorcycleEnd) {
                        spotType = SpotType.MOTORCYCLE;
                    } else if (spot < compactEnd) {
                        spotType = SpotType.COMPACT;
                    } else {
                        spotType = SpotType.LARGE;
                    }
                    spots[level][row][spot] = new Spot(level, row, spot, spotType);
                }
            }
        }
    }
    
    public boolean parkVehicle(String licensePlate) {
        if (parkedVehicles.containsKey(licensePlate)) {
            System.out.println("Vehicle " + licensePlate + " is already parked");
            return false;
        }
        
        Vehicle vehicle = createVehicle(licensePlate);
        if (vehicle == null) {
            System.out.println("Invalid vehicle type for " + licensePlate);
            return false;
        }
        
        List<Spot> availableSpots = findAvailableSpots(vehicle);
        if (availableSpots.isEmpty()) {
            System.out.println("No available spots for " + licensePlate);
            return false;
        }
        
        for (Spot spot : availableSpots) {
            spot.parkVehicle(vehicle);
        }
        
        parkedVehicles.put(licensePlate, vehicle);
        vehicleSpots.put(licensePlate, availableSpots);
        
        System.out.println("Parked " + licensePlate + " at " + formatSpotLocation(availableSpots));
        return true;
    }
    
    public boolean unParkVehicle(String licensePlate) {
        if (!parkedVehicles.containsKey(licensePlate)) {
            System.out.println("Vehicle " + licensePlate + " is not parked");
            return false;
        }
        
        List<Spot> occupiedSpots = vehicleSpots.get(licensePlate);
        for (Spot spot : occupiedSpots) {
            spot.unparkVehicle();
        }
        
        parkedVehicles.remove(licensePlate);
        vehicleSpots.remove(licensePlate);
        
        System.out.println("Unparked " + licensePlate + " from " + formatSpotLocation(occupiedSpots));
        return true;
    }
    
    private Vehicle createVehicle(String licensePlate) {
        if (licensePlate.startsWith("Motorcycle_")) {
            return new Motorcycle(licensePlate);
        } else if (licensePlate.startsWith("Car_")) {
            return new Car(licensePlate);
        } else if (licensePlate.startsWith("Bus_")) {
            return new Bus(licensePlate);
        }
        return null;
    }
    
    private List<Spot> findAvailableSpots(Vehicle vehicle) {
        List<Spot> result = new ArrayList<>();
        
        if (vehicle instanceof Bus) {
            return findConsecutiveLargeSpots(5);
        } else {
            for (int level = 0; level < levels; level++) {
                for (int row = 0; row < numRows; row++) {
                    for (int spot = 0; spot < spotsPerRow; spot++) {
                        Spot currentSpot = spots[level][row][spot];
                        if (currentSpot.isAvailable() && vehicle.canFitInSpot(currentSpot)) {
                            result.add(currentSpot);
                            return result;
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    private List<Spot> findConsecutiveLargeSpots(int needed) {
        for (int level = 0; level < levels; level++) {
            for (int row = 0; row < numRows; row++) {
                List<Spot> consecutive = new ArrayList<>();
                for (int spot = 0; spot < spotsPerRow; spot++) {
                    Spot currentSpot = spots[level][row][spot];
                    if (currentSpot.isAvailable() && currentSpot.getSpotType() == SpotType.LARGE) {
                        consecutive.add(currentSpot);
                        if (consecutive.size() == needed) {
                            return consecutive;
                        }
                    } else {
                        consecutive.clear();
                    }
                }
            }
        }
        return new ArrayList<>();
    }
    
    private String formatSpotLocation(List<Spot> spots) {
        if (spots.size() == 1) {
            Spot spot = spots.get(0);
            return "level " + spot.getLevel() + ", row " + spot.getRow() + ", spot " + spot.getSpotNumber();
        } else {
            Spot first = spots.get(0);
            Spot last = spots.get(spots.size() - 1);
            return "level " + first.getLevel() + ", row " + first.getRow() + ", spots " + 
                   first.getSpotNumber() + "-" + last.getSpotNumber();
        }
    }
}