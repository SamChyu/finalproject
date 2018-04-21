package com.example.samchyu.afinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {
    private static JSONObject ajson;
    private static JsonObject bjson;
    public static double[][] coordinates;
    public static String[][] namesandtypes;
    public static String[] addresses;

    private Button btn1;
    private Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getJSON();

        btn1 = (Button) findViewById(R.id.updatePOI);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bjson = convert(ajson);
                coordinates = poiCoordinates();
                namesandtypes = poiNameandType();
                addresses = poiaddresses();
            }
        });

        btn2 = (Button) findViewById(R.id.goPOI);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });





    }

    public void getJSON() {
        String url = "https://data.urbanaillinois.us/resource/9p4k-dr6i.json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                "https://data.urbanaillinois.us/resource/9p4k-dr6i.json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        ajson = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        ajson = null;
                    }
                }
        );
    }

    public JsonObject convert(JSONObject object) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject)jsonParser.parse(object.toString());
        return gsonObject;
    }
    public double[][] poiCoordinates() {
        JsonArray abc = bjson.getAsJsonArray();
        double[][] result = new double[abc.size()][3];
        for (int i = 0; i < abc.size(); i++) {
            JsonObject individualresource = abc.get(i).getAsJsonObject();
            String resourcename = individualresource.get("resource_name").getAsString();
            JsonObject resourcelocation = individualresource.get("location_1").getAsJsonObject();
            JsonArray coordinates = resourcelocation.get("coordinates").getAsJsonArray();
            result[i][0] = coordinates.get(0).getAsDouble();
            result[i][1] = coordinates.get(1).getAsDouble();
        }
        return result;
    }

    public String[][] poiNameandType() {
        JsonArray abc = bjson.getAsJsonArray();
        String[][] result = new String[abc.size()][2];
        for (int i = 0; i < abc.size(); i++) {
            JsonObject individualresource = abc.get(i).getAsJsonObject();
            String resourcename = individualresource.get("resource_name").getAsString();
            String resourcetype = individualresource.get("resource_type").getAsString();
            result[i][0] = resourcename;
            result[i][1] = resourcetype;
        }
        return result;
    }

    public String[] poiaddresses() {
        JsonArray abc = bjson.getAsJsonArray();
        String[] result = new String[abc.size()];
        for (int i = 0; i < abc.size(); i++) {
            JsonObject individualresource = abc.get(i).getAsJsonObject();
            String address = individualresource.get("address").getAsString();
            result[i] = address;
        }
        return result;
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        Bundle a = new Bundle();
        a.putSerializable("namesandtypes", namesandtypes);
        Bundle b = new Bundle();
        b.putSerializable("coordinates", coordinates);
        intent.putExtras(a);
        intent.putExtras(b);
        intent.putExtra("addresses", addresses);
        startActivity(intent);
    }



}
