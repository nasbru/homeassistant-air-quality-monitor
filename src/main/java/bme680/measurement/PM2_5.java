package bme680.measurement;

import java.math.BigDecimal;

public class PM2_5 extends Measurement{
    public PM2_5(int concentration){
        super("pm2_5", new BigDecimal(concentration), Norms.PM2_5.getDaily(), Norms.PM2_5.getAnnual(), 0, "\u00B5g/m\u00B3");
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