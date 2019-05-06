package com.example.username.remotecontrol.entities;

import java.io.Serializable;
import java.util.Objects;

public class NetworkDevice implements Serializable {
    private String name;
    private String type;
    private String ip;

    public String getName() {
        return name;
    }

    public NetworkDevice setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public NetworkDevice setType(String type) {
        this.type = type;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public NetworkDevice setIp(String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public String toString() {
        return "NetworkDevice{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkDevice device = (NetworkDevice) o;
        return Objects.equals(name, device.name) &&
                Objects.equals(type, device.type) &&
                Objects.equals(ip, device.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, ip);
    }
}
