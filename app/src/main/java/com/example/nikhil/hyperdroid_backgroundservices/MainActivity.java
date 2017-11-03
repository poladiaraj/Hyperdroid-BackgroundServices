package com.example.nikhil.hyperdroid_backgroundservices;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Authentication using Firebase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Using Firebase Real Time Database
    public static FirebaseDatabase database;
    public static DatabaseReference mDatabase;
    public static Context ctx;

    // Servive Start And End
    private Button startService;
    private Button stopService;

    // Refress interval
    public static int minterval = 5000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = getApplicationContext();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        mAuth = FirebaseAuth.getInstance();


        mAuth.signInWithEmailAndPassword("admin@hyperdroid.com", "admin-service")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d("Hyperdroid", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            //Log.w("Hyperdroid", "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "auth_failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("VirtualMachine").child("Ref_Interval").getValue().toString();
                //Log.d("Hyperdroid-Ref_Interval", "Value is: " + value);
                try
                {
                    minterval = Integer.parseInt(value);
                }
                catch (Exception E)
                {
                    minterval = 1000;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w("Hyperdroid-Ref_Interval", "Failed to read value.", error.toException());
            }
        });

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
        WifiInfo wifiInfo = wm.getConnectionInfo();
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String ssid = wifiInfo.getSSID();
        map.put("Address" , getIPAddress(true));
        map.put("Port" , "5901");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        map.put("Data&Time" , currentDateandTime);
        map.put("SSID" , ssid );
        //Log.i("#Hyperdroid" , ssid);
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
