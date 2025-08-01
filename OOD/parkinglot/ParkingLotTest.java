public class ParkingLotTest {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(1, 1, 11);
        
        System.out.println("=== Parking Lot Test ===");
        System.out.println("Configuration: 1 level, 1 row, 11 spots per row");
        System.out.println("Spot distribution: [0-2] motorcycle, [2-6] compact, [6-11] large");
        System.out.println();
        
        parkingLot.parkVehicle("Motorcycle_1");
        parkingLot.parkVehicle("Car_1");
        parkingLot.parkVehicle("Car_2");
        parkingLot.parkVehicle("Car_3");
        parkingLot.parkVehicle("Car_4");
        parkingLot.parkVehicle("Car_5");
        parkingLot.parkVehicle("Bus_1");
        parkingLot.unParkVehicle("Car_5");
        parkingLot.parkVehicle("Bus_1");
    }
}