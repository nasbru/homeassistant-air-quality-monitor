package bme680.measurement;

import java.math.BigDecimal;

public class Pressure extends Measurement{
    public Pressure(float value){
        super("pressure", new BigDecimal(Float.toString(value)), 2, "hPa");
    }
    @Override
    public boolean hasNorm(){
        return false;
    }
}