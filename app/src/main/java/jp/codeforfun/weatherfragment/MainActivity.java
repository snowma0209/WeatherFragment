package jp.codeforfun.weatherfragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    String CityName = null;
    public double _latitude = 0;
    public double _longitude = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button tk = findViewById(R.id.bt_tk);
        Button hk = findViewById(R.id.bt_hk);
        Button ok = findViewById(R.id.bt_ok);
        Button here = findViewById(R.id.bt_here);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        GPSLocationListener locationListener = new GPSLocationListener();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED) {
            String[] permissions ={Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1000);
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);

        HelloListener listener = new HelloListener();
        tk.setOnClickListener(listener);
        ok.setOnClickListener(listener);
        hk.setOnClickListener(listener);
        here.setOnClickListener(listener);




    }

    private class HelloListener implements View.OnClickListener{
        @Override
        public void onClick(View view){

            int id = view.getId();
            CityName = null;
            switch (id){
                case R.id.bt_hk :
                     CityName = "Ebetsu";
                     break;
                case  R.id.bt_tk :
                     CityName = "Tokyo";
                     break;
                case R.id.bt_ok :
                     CityName = "Naha";
                     break;
                case R.id.bt_here :
                    ;
                    break;
            }

            Intent intent = new Intent(MainActivity.this,WeatherIntent.class);
            intent.putExtra("CityName",CityName);
            intent.putExtra("latitude", _latitude);
            intent.putExtra("longitude", _longitude);

            if(CityName == null){
                if(_latitude == 0) {
                    Toast.makeText(MainActivity.this,"もう一度押して下さい。",Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(intent);
                }
            }else {
                startActivity(intent);
            }

        }



    }

    public class GPSLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(@NonNull Location location) {
            _latitude = location.getLatitude();
            _longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
            GPSLocationListener locationListener = new GPSLocationListener();
            if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }
}