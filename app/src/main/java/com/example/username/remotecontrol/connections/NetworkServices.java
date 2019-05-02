/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.username.remotecontrol.connections;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class NetworkServices {
	private final String TAG = "Network";

	private final String TYPE = "_http._tcp.local.";
    private final String SERVICE_NAME = "ClientService";
	private final int PORT = 49151;

	private Context localContext;
	private JmDNS jmDNS;
	private ServiceInfo mServiceInfo;

	public NetworkServices(Context context) {
	    localContext = context;
		try {
			registerService();
		} catch (IOException ex) {
            Log.d(TAG, "Service register exception", ex);
		}
	}
	
	private void registerService() throws UnknownHostException, IOException{
        jmDNS = JmDNS.create(getCurrentInetAddress());
		mServiceInfo = ServiceInfo.create(TYPE, SERVICE_NAME, PORT, SERVICE_NAME);
		jmDNS.registerService(mServiceInfo);
	}
	
	public List<String> discoveryServices(){
		List<String> services = null;
		jmDNS.addServiceListener(TYPE, new ServiceListener() {
			@Override
			public void serviceAdded(ServiceEvent serviceEvent) {
				ServiceInfo serviceInfo = jmDNS.getServiceInfo(serviceEvent.getType(), serviceEvent.getName());
                services.add(serviceInfo.getName() + ":" + serviceInfo.getInetAddress().getHostName());
			}

			@Override
			public void serviceRemoved(ServiceEvent se) {
				
			}

			@Override
			public void serviceResolved(ServiceEvent se) {
				jmDNS.requestServiceInfo(se.getType(), se.getName(), 1);
			}
        });
		return services;
	}

	private InetAddress getCurrentInetAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) localContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        int ip = connectionInfo.getIpAddress();
        String ipAddress = String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
        return InetAddress.getByName(ipAddress);
    }
}
