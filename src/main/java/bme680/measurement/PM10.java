package bme680.measurement;

import java.math.BigDecimal;

public class PM10 extends Measurement{
    public PM10(int concentration){
        super("pm10", new BigDecimal(concentration), Norms.PM10.getDaily(), Norms.PM10.getAnnual(), 0, "\u00B5g/m\u00B3");
    }
    
    @Override
    public boolean hasNorm(){
        return true;
    }
    @Override
    public String toString(){
        String result = String.format("%s: %d%s (%d%%)", getName(), getValue().intValue(), getUnit(), percentValueToNorm());
        return result;
    }
}
