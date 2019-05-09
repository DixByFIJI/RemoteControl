package com.example.username.remotecontrol.entities;

import java.io.Serializable;
import java.util.Objects;

public class NetworkNode implements Serializable {
    private NetworkDevice device;
    private String data;

    public NetworkNode(NetworkDevice device, String data) {
        this.device = device;
        this.data = data;
    }

    public NetworkDevice getDevice() {
        return device;
    }

    public void setDevice(NetworkDevice device) {
        this.device = device;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NetworkNode{" +
                "device=" + device +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkNode that = (NetworkNode) o;
        return Objects.equals(device, that.device) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, data);
    }
}