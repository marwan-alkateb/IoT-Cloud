import java.util.Random;
public class Sensors {

    public Sensors() {}
    public static String getCurrentValues() {

        Random r = new Random();
        double temperature = 15 + (50 - 15) * r.nextDouble();
        String temperature_str = String.format("%.1f", temperature);

        int humidity = (int) (Math.random() * 100) + 1;
        String humidity_str = Integer.toString(humidity);

        int fluid = (int) (Math.random() * 100) + 1;
        String fluid_str = Integer.toString(fluid);

        String values = "B"+"_"+temperature_str+"_"+humidity_str+"_"+fluid_str;

        return values;
    }
}
