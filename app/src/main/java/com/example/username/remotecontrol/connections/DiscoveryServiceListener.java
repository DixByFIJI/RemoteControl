package com.example.username.remotecontrol.connections;

import com.example.username.remotecontrol.entities.NetworkDevice;

import javax.jmdns.ServiceInfo;

public interface DiscoveryServiceListener {
    void onFound(ServiceInfo serviceInfo);
    void onRemoved(ServiceInfo serviceInfo);
}
