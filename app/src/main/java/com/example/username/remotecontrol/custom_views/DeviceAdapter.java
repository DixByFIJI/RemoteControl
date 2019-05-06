package com.example.username.remotecontrol.custom_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.username.remotecontrol.R;
import com.example.username.remotecontrol.entities.NetworkDevice;

import java.util.List;

import javax.jmdns.ServiceInfo;

public class DeviceAdapter extends ArrayAdapter<NetworkDevice> {

    public DeviceAdapter(Context context, List<NetworkDevice> devices) {
        super(context, R.layout.spinner_cell, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetworkDevice device = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.spinner_cell, null);

        TextView txtSpinnerCell = convertView.findViewById(R.id.txtSelectedServiceName);
        txtSpinnerCell.setText(device.getName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        NetworkDevice device = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.spinner_selection_cell, null);

        TextView txtSpinnerCell = convertView.findViewById(R.id.txtServiceName);
        txtSpinnerCell.setText(device.getName());
        TextView txtIpAddress = convertView.findViewById(R.id.txtIpAddress);
        txtIpAddress.setText(device.getIp());
        return convertView;
    }
}
