package ic.doc.camera;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class CameraTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();
  Sensor sensor = context.mock(Sensor.class);
  MemoryCard memoryCard = context.mock(MemoryCard.class);
  Camera camera = new Camera(sensor, memoryCard);

  private void expectSensorPowerUp() {
    context.checking(new Expectations() {{
      exactly(1).of(sensor).powerUp();
    }});
  }

  private void expectSensorPowerDown() {
    context.checking(new Expectations() {{
      exactly(1).of(sensor).powerDown();
    }});
  }

  private void expectationFromPressingTheShutter(byte[] data) {
    context.checking(new Expectations() {{
      exactly(1).of(sensor).readData();
      will(returnValue(data));
      exactly(1).of(memoryCard).write(data);
    }});
  }

  @Test
  public void switchingTheCameraOnPowersUpTheSensor() {
    expectSensorPowerUp();
    camera.powerOn();
  }

  @Test
  public void switchingTheCameraOffPowersDownTheSensor() {
    expectSensorPowerUp();
    camera.powerOn();
    expectSensorPowerDown();
    camera.powerOff();
  }

  @Test
  public void pressingTheShutterWhenThePowerIsOffDoesNothing() {
    context.checking(new Expectations() {{
      never(sensor);
      never(memoryCard);
    }});
    camera.pressShutter();
  }

  @Test
  public void pressingTheShutterWithThePowerOnCopiesDataFromSensorToMemoryCard() {
    byte[] data = new byte[4];
    expectSensorPowerUp();
    camera.powerOn();
    expectationFromPressingTheShutter(data);
    camera.pressShutter();
  }

  @Test
  public void switchingCameraOffWhenDataIsBeingWrittenDoesNotPowerDownSensor() {
    byte[] data = new byte[4];
    expectSensorPowerUp();
    camera.powerOn();
    expectationFromPressingTheShutter(data);
    camera.pressShutter();
    camera.powerOff();
  }

  @Test
  public void cameraPowersDownSensorAfterDataWritingIsComplete() {
    byte[] data = new byte[4];
    expectSensorPowerUp();
    camera.powerOn();
    expectationFromPressingTheShutter(data);
    camera.pressShutter();
    camera.powerOff();
    expectSensorPowerDown();
    camera.writeComplete(); // called by other component after writing the data is complete
  }

}