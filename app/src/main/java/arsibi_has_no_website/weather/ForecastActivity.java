package arsibi_has_no_website.weather;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForecastActivity extends AppCompatActivity {
    static List<ListItem> resList = new ArrayList<>();
    CustomAdapter myAdapter;
    ListView lv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        getSupportActionBar().hide();
        lv=(ListView)findViewById(R.id.forecastlist);
        myAdapter = new CustomAdapter(getApplicationContext(),R.layout.item,resList);
        lv.setAdapter(myAdapter);
        Intent intent =getIntent();
        String input = intent.getStringExtra("name");
        AsyncLoader myLoader = new AsyncLoader();
        String url="http://api.openweathermap.org/data/2.5/forecast?"+"APPID="+MainActivity.API_KEY+"&q="+input;
        resList.clear();
        myLoader.execute(url);

    }
    class AsyncLoader extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                JSONObject json = new JSONObject(processedInput(connection.getInputStream()));
                Log.d("MOO",json.toString());
                getandPrintOutput(json);

            }
            catch (MalformedURLException e){Log.d("MOO",e.toString());}
            catch (IOException e){Log.d("MOO",e.toString());}
            catch (JSONException e){Log.d("MOO",e.toString());}
            catch (Exception e){Log.d("MOO",e.toString());}
            return null;
        }
        public void getandPrintOutput(JSONObject json) throws JSONException{
            JSONArray jsonarr=json.getJSONArray("list");
            for(int i=4;i<jsonarr.length();i+=8) {
                JSONObject json1=jsonarr.getJSONObject(i);
                getAndSetValue("dt_txt", json1, "Date", "", null , 0);
                json1= json1.getJSONObject("main");
                getAndSetValue("temp", json1, "Temperature", " C", "-", 273.15);
                getAndSetValue("pressure", json1, "Pressure", " bar", "*", 0.001);
                getAndSetValue("humidity", json1, "Humidity", " %", null, 0);
                getAndSetValue("temp_min", json1, "Temp(Min)", " C", "-", 273.15);
                getAndSetValue("temp_max", json1, "Temp(Max)", " C", "-", 273.15);
                addItem("","");
            }
        }
        public void getAndSetValue(String s, JSONObject json, String Title, String extension , @Nullable String operand , double change) throws JSONException{
            final String id = Title;
            String value = json.getString(s);
            if(operand!=null){
                double v=Double.parseDouble(value);
                if(operand.equals("*")){
                    v*=change;
                }
                if(operand.equals("-")){
                    v-=change;
                }
                value=String.format(Locale.US,"%.2f",v);
            }
            final String finalValue=value+extension;
            addItem(id,finalValue);
        }
        public void addItem(String id1 ,String finalValue1){
            final String id=id1;
            final String finalValue = finalValue1;
            runOnUiThread(new Runnable() {
                @Override
                public void  run() {
                    resList.add(new ListItem(id,finalValue));
                    myAdapter.notifyDataSetChanged();
                }
            });
        }
        public String processedInput(InputStream in){
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder("");
                String line;
                while((line=reader.readLine())!=null){
                    builder.append(line).append('\n');
                }
                return builder.toString();
            }catch (IOException e){}
            return null;
        }
    }
}
