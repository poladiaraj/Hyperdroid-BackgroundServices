package com.example.nikhil.hyperdroid_backgroundservices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InitialSetup extends AppCompatActivity {

    private EditText name;
    private Button setup;

    public static String VMName=null;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        name = (EditText)findViewById(R.id.name);
        setup = (Button)findViewById(R.id.setup);

        sharedPreferences = this.getSharedPreferences("com.example.nikhil.backgroundservicevm", Context.MODE_PRIVATE);

        final String s = sharedPreferences.getString("name@VM" , null);
        if( s != null )
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            VMName = s;
            finish();
        }

        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().equals(""))
                {
                    VMName = name.getText().toString();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name@VM",name.getText().toString());
                    editor.commit();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(InitialSetup.this, "Enter the name for \nVMService Provider", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
