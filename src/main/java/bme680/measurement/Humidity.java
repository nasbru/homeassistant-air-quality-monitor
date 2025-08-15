package bme680.measurement;

import java.math.BigDecimal;

public class Humidity extends Measurement{
    public Humidity(float value){
        super("humidity", new BigDecimal(Float.toString(value)), 2, "%");
    }
    @Override
    public boolean hasNorm(){
        return false;
    }
}