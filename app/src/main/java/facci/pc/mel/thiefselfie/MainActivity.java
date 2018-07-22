package facci.pc.mel.thiefselfie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int RESULT_ENABLE = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int SENSOR_PERMISSION_REQUEST_CODE = 2;
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 3;
    Switch simpleSwitch, ubicacion;
    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;
    View intentos, carpeta;
    TextView intentosrespuesta, carpetarespuesta;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Spinner intentosspinner;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("perfil", MODE_PRIVATE);
        editor = preferences.edit();


        deviceManger = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(this, SeguridAdmin.class);


        simpleSwitch = (Switch) findViewById(R.id.switch1);
        ubicacion = (Switch) findViewById(R.id.switch3);


        intentos = findViewById(R.id.intentos);
        intentosrespuesta = findViewById(R.id.intentosrespuesta);
        carpeta = findViewById(R.id.carpeta);
        carpetarespuesta = findViewById(R.id.carpetarespuesta);
        intentosspinner = findViewById(R.id.intentosspinner);

        intentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });

        carpeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog2();
            }
        });

        intentosspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                editor.putInt("intento", position);
                editor.apply();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        boolean active = deviceManger.isAdminActive(compName);
        simpleSwitch.setChecked(active);
        ubicacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("ubicacion", true);
                    editor.apply();
                } else {
                    editor.putBoolean("ubicacion", false);
                    editor.apply();
                }
            }
        });

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
                    admin();
                    permisos();
                    activartodo(true);

                } else {
                    Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
                    activartodo(false);
                    deviceManger.removeActiveAdmin(compName);
                }
            }
        });
        if (active) {
            permisos();
            activartodo(true);
        } else {
            activartodo(false);
        }

        ///

        SharedPreferences preferences =getSharedPreferences("perfil", MODE_PRIVATE);
        String Correo = preferences.getString("Correo", "Sin nombre");
        String conf1 = preferences.getString("Carpeta", "dvvv");
        int intento = preferences.getInt("intento", 1);
        boolean estado = preferences.getBoolean("ubicacion", false);
        intentosrespuesta.setText(Correo);
        carpetarespuesta.setText(conf1);
        intentosspinner.setSelection(intento);
        ubicacion.setChecked(estado);


        ///
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion mlocListener = new Localizacion();
        mlocListener.setMainActivity(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mlocListener);


    }

    private void admin() {
        Intent intent = new Intent(DevicePolicyManager
                .ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Additional text explaining why this needs to be added.");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    private void activartodo(boolean b) {
        if(b){
            findViewById(R.id.fila_container2).setVisibility(View.VISIBLE);
            findViewById(R.id.fila_container3).setVisibility(View.VISIBLE);
            findViewById(R.id.fila_container5).setVisibility(View.VISIBLE);
            findViewById(R.id.fila_container6).setVisibility(View.VISIBLE);
            findViewById(R.id.fila_container4).setVisibility(View.VISIBLE);

        }else{
            findViewById(R.id.fila_container2).setVisibility(View.INVISIBLE);
            findViewById(R.id.fila_container3).setVisibility(View.INVISIBLE);
            findViewById(R.id.fila_container5).setVisibility(View.INVISIBLE);
            findViewById(R.id.fila_container6).setVisibility(View.INVISIBLE);
            findViewById(R.id.fila_container4).setVisibility(View.INVISIBLE);



        }
    }

    private void showDialog1() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        intentosrespuesta.setText(editText.getText());
                        editor.putString("Correo", String.valueOf(editText.getText()));
                        editor.apply();


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    private void showDialog2() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        carpetarespuesta.setText(editText.getText());
                        editor.putString("Carpeta", String.valueOf(editText.getText()));
                        editor.apply();

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private void permisos() {
        String[] permissions = new String[]{
                 Manifest.permission.CAMERA
                ,Manifest.permission.INTERNET
                ,Manifest.permission.ACCESS_NETWORK_STATE
                ,Manifest.permission.ACCESS_WIFI_STATE
                ,Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                ,Manifest.permission.ACCESS_FINE_LOCATION
                ,Manifest.permission.ACCESS_COARSE_LOCATION};
//
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissions[1]) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissions[2]) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissions[3]) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissions[4]) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissions[5]) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissions[6]) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(MainActivity.this, permissions[7]) != PackageManager.PERMISSION_GRANTED) {
                //Si alguno de los permisos no esta concedido lo solicita
                Toast.makeText(this, "Permissions are not granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(MainActivity.this, permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);

            } else {
                //Si todos los permisos estan concedidos prosigue con el flujo normal
                Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_LONG).show();

            }
  //





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "OK Permissions granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
               // openCamera();
            } else {
                Toast.makeText(this, "Permissions are not granted ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direcci—n de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    Log.d("Mi direcci—n es: \n" , address.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
