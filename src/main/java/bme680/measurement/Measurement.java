package bme680.measurement;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Measurement{
    private String name;
    private BigDecimal value;
    private int dailyNorm;
    private int annualNorm;
    private String unit;

    
    public Measurement(String name, BigDecimal value, int dailyNorm, int annualNorm,  int scale, String unit){
        this.name = name;
        this.value = value.setScale(scale, RoundingMode.HALF_UP);
        this.dailyNorm = dailyNorm;
        this.annualNorm = annualNorm;
        this.unit = unit;
    }
    
    public Measurement(String name, BigDecimal value, int scale, String unit){
        this.name = name;
        this.value = value.setScale(scale, RoundingMode.HALF_UP);
        this.dailyNorm = -1;
        this.annualNorm = -1;
        this.unit = unit;
    }
    
    public String getName(){
        return name;
    }
    
    public BigDecimal getValue() {
    	return value;
    }
    
    public int getDailyNorm(){
        return dailyNorm;
    }
    
    public int getAnnualNorm(){
        return annualNorm;
    }
    
    public int percentValueToNorm(){
        float result = (value.floatValue() / dailyNorm) * 100;
        return (int) result;
    }
    
    public String getUnit(){
        return unit;
    }
    
    @Override
    public String toString(){
        String result = name + ": " + getValue() + unit;
        if(hasNorm()) result += " " + "("  + percentValueToNorm() + "%)";
        return result;
    }
    
    public abstract boolean hasNorm();
}