package com.github.nasbru.mqtt;
import com.github.nasbru.measurements.Measurement;

public interface SensorListener{
    void onDataReceived(Measurement[] measurement);
}
