package bme680.measurement;

import java.math.BigDecimal;

public class VOC extends Measurement{
    public VOC(float value){
        super("voc", new BigDecimal(Float.toString(value)), 0, "ppm");
    }
    @Override
    public boolean hasNorm(){
        return false;
    }
}
