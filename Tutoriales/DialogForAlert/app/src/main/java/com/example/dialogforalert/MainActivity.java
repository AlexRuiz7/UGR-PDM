package com.example.dialogforalert;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickShowAlert(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);

        // Set the dialog title and message
        alertBuilder.setTitle(R.string.alert_title);
        alertBuilder.setMessage(R.string.alert_message);

        // Add the dialog buttons
        alertBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), R.string.ok_button_message, Toast.LENGTH_SHORT).show();
            }
        });

        alertBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), R.string.cancel_button_message, Toast.LENGTH_SHORT).show();
            }
        });

        alertBuilder.show();
    }
}