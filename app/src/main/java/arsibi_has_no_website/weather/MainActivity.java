package arsibi_has_no_website.weather;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
    public static final String API_KEY="43fe4e2883021fed4f3dbeebd7e46b75";
    static List<ListItem> resultList=new ArrayList<>();
    ListView lv ;
    PlaceAutocompleteFragment inputcity;
    String input="";
    CustomAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        inputcity = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.inputtext);
        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
        inputcity.setFilter(filter);
        inputcity.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                input = place.getName().toString();
            }
            @Override
            public void onError(Status status) {
            }
        });
        lv= (ListView)findViewById(R.id.showoutput);
        myAdapter = new CustomAdapter(getApplicationContext(),R.layout.item,resultList);
        lv.setAdapter(myAdapter);
    }
    public void forecastData(View v){
        if(!input.equals("")) {
            Intent intent = new Intent(this, ForecastActivity.class);
            intent.putExtra("name", input);
            startActivity(intent);
        }
    }
    public void getData(View v){
        resultList.clear();
        String url="http://api.openweathermap.org/data/2.5/weather?"+"APPID="+API_KEY+"&q="+input;
        AsyncLoader myLoader = new AsyncLoader();
        myLoader.execute(url);
    }
    class AsyncLoader extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                JSONObject json = new JSONObject(processedInput(connection.getInputStream()));
                getandPrintOutput(json);
                Log.d("MOO",json.toString());
            }catch (MalformedURLException e){Log.d("MOO",e.toString());}
            catch (IOException e){Log.d("MOO",e.toString());}
            catch (JSONException e){Log.d("MOO",e.toString());}
            return null;
        }
        public void getandPrintOutput(JSONObject json) throws JSONException{
            JSONObject json1=json.getJSONObject("main");
            getAndSetValue("temp",json1,"Temperature"," C","-",273.15);
            getAndSetValue("pressure",json1,"Pressure"," bar","*",0.001);
            getAndSetValue("humidity",json1,"Humidity"," %",null,0);
            getAndSetValue("temp_min",json1,"Temp(Min)"," C","-",273.15);
            getAndSetValue("temp_max",json1,"Temp(Max)"," C","-",273.15);

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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultList.add(new ListItem(id,finalValue));
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
