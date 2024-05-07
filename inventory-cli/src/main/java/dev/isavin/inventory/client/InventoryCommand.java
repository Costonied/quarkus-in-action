package dev.isavin.inventory.client;

import dev.isavin.inventory.model.InsertCarRequest;
import dev.isavin.inventory.model.InventoryService;
import dev.isavin.inventory.model.RemoveCarRequest;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.logging.Log;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class InventoryCommand implements QuarkusApplication {

  @GrpcClient("inventory")
  InventoryService inventory;

  private static final String USAGE =
      "Usage: inventory <add>|<remove> " +
      "<license plate number> <manufacturer> <model>";

  @Override
  public int run(String... args) throws Exception {
    String action = args.length > 0 ? args[0] : null;

    if ("add".equals(action) && args.length >= 4) {
      add(args[1], args[2], args[3]);
      return 0;
    } else if ("remove".equals(action) && args.length >= 2) {
      remove(args[1]);
      return 0;
    }

    Log.error(USAGE);
    return 1;
  }

  public void add(String licensePlateNumber, String manufacturer, String model) {

    inventory.add(InsertCarRequest.newBuilder()
        .setLicensePlateNumber(licensePlateNumber)
        .setManufacturer(manufacturer)
        .setModel(model)
        .build())
        .onItem().invoke(carResponse -> Log.info("Inserted new car " + carResponse))
        .await().indefinitely();

  }

  public void remove(String licensePlateNumber) {

    inventory.remove(RemoveCarRequest.newBuilder()
        .setLicensePlateNumber(licensePlateNumber)
        .build())
        .onItem().invoke(carResponse -> Log.info("Removed car " + carResponse))
        .await().indefinitely();

  }

}
