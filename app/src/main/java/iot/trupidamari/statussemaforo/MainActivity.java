package iot.trupidamari.statussemaforo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import iot.trupidamari.statussemaforo.models.Semaforo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "getStatus";
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    final int MY_LOCATION_REQUEST_CODE = 100, LANG_AVAILABLE=0;
    LocationManager myManager;
    LocationListener locationListener;
    double lat,lon;
    Location location;
    Locale myLocale = new Locale("PT", "BR");

    TextView tvLatitude, tvLongitude;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude = (TextView) findViewById(R.id.latitude);
        tvLongitude = (TextView) findViewById(R.id.longitude);


        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });

        myManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = myManager.getBestProvider(criteria, false);

        location = myManager.getLastKnownLocation(provider);
        Log.i("Latitude: ", "" + location.getLatitude());
        tvLatitude.setText(String.valueOf(location.getLatitude()));
        lat=location.getLatitude();
        Log.i("Longitude: ", "" + location.getLongitude());
        tvLongitude.setText(String.valueOf(location.getLongitude()));
        lon=location.getLongitude();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("Latitude: ", "" + location.getLatitude());
                tvLatitude.setText(String.valueOf(location.getLatitude()));
                lat=location.getLatitude();
                Log.i("Longitude: ", "" + location.getLongitude());
                tvLongitude.setText(String.valueOf(location.getLongitude()));
                lon=location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        checkPermission();

    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET},MY_LOCATION_REQUEST_CODE );
            return;
        }else{
            myManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case MY_LOCATION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Latitude: ", "" + location.getLatitude());
                    tvLatitude.setText(String.valueOf(location.getLatitude()));
                    Log.i("Longitude: ", "" + location.getLongitude());
                    tvLongitude.setText(String.valueOf(location.getLongitude()));
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    public void atravessar(View view) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SemaforoService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SemaforoService service = retrofit.create(SemaforoService.class);

        Semaforo semaforo = new Semaforo();
        semaforo.setLat(lat);
        semaforo.setLongi(lon);


        Call<Semaforo> requestStatus = service.getStatus(semaforo);

        requestStatus.enqueue(new Callback<Semaforo>() {
            @Override
            public void onResponse(Call<Semaforo> call, Response<Semaforo> response) {

                if (!response.isSuccess()) {
                    Log.i("TAG", "Erro!!!!!!!!!: " + response.code());
                } else {
                    Semaforo s = response.body();
                    Log.i(TAG, Integer.toString(s.getStatus()));

                    Context context = getApplicationContext();
                    CharSequence text = Integer.toString(s.getStatus());
                    Vibrator vs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    switch (s.getStatus()){
                        case 0:
                            t1.speak("Walk",TextToSpeech.QUEUE_FLUSH,null);

                            vs.vibrate(500);
                            break;
                        case 1:
                            t1.speak("Don't Walk",TextToSpeech.QUEUE_FLUSH,null);

                            vs.vibrate(1500);
                            break;
                        case 2:
                            t1.speak("Não há semáforo",TextToSpeech.QUEUE_FLUSH,null);
                    }

                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }

            @Override
            public void onFailure(Call<Semaforo> call, Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
                Toast.makeText(MainActivity.this,"Falha na conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

}

