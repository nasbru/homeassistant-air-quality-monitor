package bme680.measurement;

import java.math.BigDecimal;

/*
 * Equivalent Carbon Dioxide
 * Równowartość dwutlenku węgla
 */

public class CO2 extends Measurement{
    public CO2(int value){
        super("co2", new BigDecimal(value), Norms.CO2_Out.getDaily(), Norms.CO2_Out.getAnnual(), 0, "ppm");
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