package bme680.measurement;

import java.math.BigDecimal;

public class PM1_0 extends Measurement{
    public PM1_0(int concentration){
        super("pm1_0", new BigDecimal(concentration), 0, "\u00B5g/m\u00B3");
    }
    
    @Override
    public boolean hasNorm(){
        return false;
    }
    
    @Override
    public String toString(){
        String result = String.format("%s: %d%s", getName(), getValue().intValue(), getUnit());
        return result;
    }
}
