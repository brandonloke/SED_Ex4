package ic.doc.camera;

public class Camera implements WriteListener {

  private final Sensor sensor;
  private final MemoryCard memoryCard;
  private boolean isPoweredOn = false;
  private boolean isWriting = false;

  public Camera(Sensor sensor, MemoryCard memoryCard) {
    this.sensor = sensor;
    this.memoryCard = memoryCard;
  }

  public void pressShutter() {
    if (isPoweredOn) {
      isWriting = true;
      byte[] data = sensor.readData();
      memoryCard.write(data);
    }
  }

  @Override
  public void writeComplete() {
    isWriting = false;
    if (!isPoweredOn) {
      sensor.powerDown(); // Power down the sensor if the camera is off and writing is complete
    }
  }

  public void powerOn() {
    sensor.powerUp();
    isPoweredOn = true;
  }

  public void powerOff() {
    isPoweredOn = false;
    if (!isWriting) {
      sensor.powerDown();
    }
  }
}

