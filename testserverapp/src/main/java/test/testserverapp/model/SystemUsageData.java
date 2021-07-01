package test.testserverapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;


@Entity(name="SystemUsageData")
public class SystemUsageData {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name="hostname")
    private String hostName;
    @Column(name = "location")
    private String location;
    @Column(name = "processorUsage")
    private double processorUsage;
    @Column(name = "usedMemory")
    private double usedMemory;
    @Column(name = "freeMemory")
    private double freeMemory;
    @Column(name = "dateOfMeasurement")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateOfMeasurement;

    public SystemUsageData(){}

    public SystemUsageData(String hostName, String location, double processorUsage, double usedMemory, double freeMemory, Date dateOfMeasurement) {
        this.hostName = hostName;
        this.location = location;
        this.processorUsage = processorUsage;
        this.usedMemory = usedMemory;
        this.freeMemory = freeMemory;
        this.dateOfMeasurement = dateOfMeasurement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getProcessorUsage() {
        return processorUsage;
    }

    public void setProcessorUsage(double processorUsage) {
        this.processorUsage = processorUsage;
    }

    public double getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(double usedMemory) {
        this.usedMemory = usedMemory;
    }

    public double getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(double freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Date getDateOfMeasurement() {
        return dateOfMeasurement;
    }

    public void setDateOfMeasurement(Date dateOfMeasurement) {
        this.dateOfMeasurement = dateOfMeasurement;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
