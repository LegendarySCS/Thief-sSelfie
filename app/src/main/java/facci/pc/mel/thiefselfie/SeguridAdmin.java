package facci.pc.mel.thiefselfie;

import android.Manifest;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SeguridAdmin extends DeviceAdminReceiver{
    private LocationManager locManager;
    private Location loc;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String conf1;
    static SharedPreferences getSamplePreferences(Context context) {
        return context.getSharedPreferences(
                DeviceAdminReceiver.class.getName(), 0);
    }

    static String PREF_PASSWORD_QUALITY = "password_quality";
    static String PREF_PASSWORD_LENGTH = "password_length";
    static String PREF_MAX_FAILED_PW = "max_failed_pw";

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return super.onDisableRequested(context, intent);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {

        SharedPreferences preferences=context.getSharedPreferences("perfil", context.MODE_PRIVATE);
        editor = preferences.edit();

        String Correo = preferences.getString("Correo", "Sin nombre");
        conf1 = preferences.getString("Carpeta", "dvvv");
        int intento = preferences.getInt("intento", 1);
        boolean estado = preferences.getBoolean("ubicacion", false);
        int cont = preferences.getInt("contador", 0);



        int counter =cont;
        if (counter >= intento+1) {
            //counter = counter + 1;

            Log.d("intentouno","Se paso de 3 ontetos Tomado ");
            SendEmail emailSender=new SendEmail();
            takeSnapShots(context);
            String ubcacion= ubicacion(context);
            emailSender.execute("luispazmanta@gmail.com","********",Correo,"Intento de desbloqueo Fallido",
                    "Alguien intento desbloquer su celular lugar:"+ubcacion+"<a class=\"home-link\" href=\"https://www.google.com/maps/search/?api=1&amp;query="+loc.getLatitude()+","+loc.getLongitude()+">Ver en el Mapa Aqui</a><br><br> ","/sdcard/Pictures/"+conf1+"/Rufian.jpg");

            editor.putInt("contador",0);
            editor.apply();

        }else{
            counter = counter + 1;
            editor.putInt("contador",counter);
            editor.apply();
            Log.d("intentouno",""+counter);

        }








    }



    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        showToast(context, "Sample Device Admin: pw succeeded");
        SharedPreferences preferences=context.getSharedPreferences("perfil", context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt("contador",0);
        editor.apply();
        Log.d("desBloqueo","ssssssssss");

    }

    private String ubicacion(Context context) {
        locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.d("Localizacion",String.valueOf(loc.getLatitude()));
        Log.d("Localizacion",String.valueOf(loc.getLongitude()));



        Geocoder geocoder;
        List<Address> direccion;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            direccion = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1); // 1 representa la cantidad de resultados a obtener
            String address = direccion.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = direccion.get(0).getLocality();
            String state = direccion.get(0).getAdminArea();
            String country = direccion.get(0).getCountryName();
            String postalCode = direccion.get(0).getPostalCode();

            Log.d("Localizacion",address+"-"+city+", "+country);

            return address+"-"+city+", "+country ;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void takeSnapShots(Context context) {
        Toast.makeText(context, "Image snapshot   Started",Toast.LENGTH_SHORT).show();
        // here below "this" is activity context.
        SurfaceView surface = new SurfaceView(context);
        Camera camera;
        int numberOfCameras;
        numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Log.d("Camaras",""+i );

        }

        camera = Camera.open(1);

        try {
            camera.setPreviewDisplay(surface.getHolder());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        camera.startPreview();
        camera.takePicture(null,null,jpegCallback);

    }
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public static final String TAG ="" ;

        public void onPictureTaken(byte[] data, Camera camera)
        {
            FileOutputStream outStream = null;
            try {
                String dir_path = "/sdcard/Pictures/"+conf1+"/";// set your directory path here
                String image_name="Rufian";
                crearDirectorioPublico(conf1);
                outStream = new FileOutputStream(dir_path+File.separator+image_name+".jpg");
                outStream.write(data);
                outStream.close();
                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
                camera.stopPreview();
                camera.release();
                camera = null;

                Log.d(TAG, "Image snapshot Done");

            }
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };
    public File crearDirectorioPublico(String nombreDirectorio) {
        //Crear directorio público en la carpeta Pictures.
        File directorio = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), nombreDirectorio);
        //Muestro un mensaje en el logcat si no se creo la carpeta por algun motivo
        if (!directorio.mkdirs())
            Log.e("Foto", "Error: No se creo el directorio público");

        return directorio;
    }
}
