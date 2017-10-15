package com.example.nikhil.hyperdroid_backgroundservices;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static FirebaseDatabase database;
    public static DatabaseReference mDatabase;
    public static Context ctx;

    private Button startService;
    private Button stopService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplicationContext();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        startService = (Button) findViewById(R.id.startService);
        stopService = (Button) findViewById(R.id.stopService);
        // Updating the time on firebase to Address the problem of Status.
        UpdateTime();
        // Starting the Service
        startService( new Intent(getApplicationContext(), MyService.class) );
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService( new Intent(getApplicationContext(), MyService.class) );
            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService( new Intent(getApplicationContext(), MyService.class) );
            }
        });

    }


    public static void UpdateTime()
    {
        HashMap<String,String> map = new HashMap<>();
        WifiManager wm = (WifiManager)  ctx.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        map.put("Address" , getIPAddress(true));
        map.put("Port" , "5901");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        map.put("Data&Time" , currentDateandTime);
        mDatabase.child("VirtualMachine").child(InitialSetup.VMName).setValue(map);
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs)
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        }
                        else if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                        }
                    }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
