package jp.codeforfun.weatherfragment;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherIntent extends AppCompatActivity  {

    TextView text;
    String CityName;
    String icon;
    double latitude;
    double longitude;
    double _latitude;
    double _longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_intent);
        CityName = null;

        Intent intent = getIntent();
            CityName = intent.getStringExtra("CityName");
            text = findViewById(R.id.text);
        if(CityName != null) {

            try {
                httpRequest("http://api.openweathermap.org/data/2.5/weather?q="
                        + CityName + "&units=metric&appid=487d1a090747a05af531d66f0db789c8\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            latitude = intent.getDoubleExtra("latitude",_latitude);
            longitude = intent.getDoubleExtra("longitude",_longitude);
            Log.d("debug",Double.toString(longitude));
            try{
                httpRequest("http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&units=metric&appid=487d1a090747a05af531d66f0db789c8");
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

       public void httpRequest(String url) throws IOException {
           OkHttpClient client = new OkHttpClient();
           Request request = new Request.Builder().url(url).build();
           client.newCall(request).enqueue(new Callback() {
               @Override
               public void onFailure(@NotNull Call call, @NotNull IOException e) {
                   e.printStackTrace();
               }

               @Override
               public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                   final String jsonStr = response.body().string();
                   Log.d("Hoge","jsonStr="+jsonStr);

                   try {
                       JSONObject json = new JSONObject(jsonStr);
                       final StringBuilder weather = new StringBuilder();
                       if(CityName == null)
                            weather.append(json.getString("name"));
                       else if(CityName.equals("Ebetsu"))
                           weather.append("札幌");
                       else if(CityName.equals("Tokyo"))
                           weather.append("東京");
                       else
                           weather.append("那覇");
                       weather.append("\n");
                       weather.append("天気: ");
                       String W = json.getJSONArray("weather").getJSONObject(0).getString("main");
                       if(W.equals("Clear")) {
                            weather.append("晴れ\n");
                       }else if(W.equals("Rain")){
                           weather.append("雨\n");
                       }else if(W.equals("Snow")){
                           weather.append("雪\n");
                       }else if(W.equals("Clouds")){
                           weather.append("曇り\n");
                       }else{
                           weather.append(W+"\n");
                       }
                       String t = json.getJSONObject("main").getString("temp");
                       t = t.substring(0,t.length()-1);
                       if(t.substring(t.length()-1).equals("."))
                           t = t.substring(0,t.length()-1);
                       weather.append("気温："+t+"℃"+"\n");
                       weather.append("風圧: "+json.getJSONObject("wind").getString("speed")+"m");
                       icon = json.getJSONArray("weather").getJSONObject(0).getString("icon");
                       ImageView image = findViewById(R.id.image);
                       String iconUrl = "http://openweathermap.org/img/wn/"+icon+"@2x.png";
                       ImageGetTask task = new ImageGetTask(image);
                       task.execute(iconUrl);


                       Handler mainHandler = new Handler(Looper.getMainLooper());
                       mainHandler.post(new Runnable() {
                           @Override
                           public void run() {
                               text.setText(weather.toString());
                           }
                       });
                   }catch (Exception e) {
                       //Log.e("Hoge", e.getMessage());
                       e.printStackTrace();
                   }
               }
           });

       }
           class ImageGetTask extends AsyncTask<String,Void,Bitmap> {
               private ImageView image;

               public ImageGetTask(ImageView _image) {
                   image = _image;
               }
               @Override
               protected Bitmap doInBackground(String... params) {
                   Bitmap image;
                   try {
                       URL imageUrl = new URL(params[0]);
                       InputStream imageIs;
                       imageIs = imageUrl.openStream();
                       image = BitmapFactory.decodeStream(imageIs);
                       return image;
                   } catch (MalformedURLException e) {
                       return null;
                   } catch (IOException e) {
                       return null;
                   }
               }
               @Override
               protected void onPostExecute(Bitmap result) {
                   image.setImageBitmap(result);
               }
           }




}
