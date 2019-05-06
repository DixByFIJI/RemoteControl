package com.example.username.remotecontrol.connections;

import com.example.username.remotecontrol.entities.NetworkDevice;

public interface DiscoveryServiceListener {
    void onFound(NetworkDevice networkDevice);
    void onRemoved(NetworkDevice networkDevice);
}
