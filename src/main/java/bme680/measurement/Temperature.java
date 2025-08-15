package bme680.measurement;

import java.math.BigDecimal;

public class Temperature extends Measurement{
    public Temperature(float value){
        super("temperature", new BigDecimal(Float.toString(value)), 2, "\u00B0C");
    }
    @Override
    public boolean hasNorm(){
        return false;
    }
}