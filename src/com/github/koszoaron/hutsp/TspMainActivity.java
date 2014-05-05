/**
 *  Copyright (C) 2014  Aron Koszo
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.koszoaron.hutsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.github.koszoaron.hutsp.util.AbstractTsp;
import com.github.koszoaron.hutsp.util.CheapestInsertionTsp;
import com.github.koszoaron.hutsp.util.City;
import com.github.koszoaron.hutsp.util.NearestAdditionTsp;
import com.github.koszoaron.hutsp.util.NearestInsertionTsp;
import com.github.koszoaron.hutsp.util.NearestNeighbourTsp;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TspMainActivity extends Activity implements OnMapLoadedCallback, OnClickListener {
    private static final String TAG = TspMainActivity.class.getSimpleName();
    private static final String KEY_AVAILABLE_CITIES = "available_cities";
    private static final String KEY_SELECTED_CITIES = "selected_cities";
    private static final String KEY_RESULT_OF_TSP = "result_of_tsp";
    private static final String KEY_SELECTED_METHOD = "selected_method";
    private static final String KEY_TOTAL_DIST = "total_dist";
    private static final int METHOD_NEAREST_NEIGHBOUR = 0;
    private static final int METHOD_NEAREST_ADDITION = 1;
    private static final int METHOD_NEAREST_INSERTION = 2;
    private static final int METHOD_CHEAPEST_INSERTION = 3;
    
    private GoogleMap map;
    private Spinner spCities;
    private Spinner spMethod;
    private EditText etNumCities;
    private Button btnGenerate;
    private Button btnClearMap;
    private Button btnStart;
    private TextView tvTotalDistance;
    
    private List<City> availableCities;
    private List<City> selectedCities;
    private List<City> result;
    private int selectedMethod = METHOD_NEAREST_NEIGHBOUR;
    
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_tspmain);
        spCities = (Spinner)findViewById(R.id.spCities);
        spMethod = (Spinner)findViewById(R.id.spMethod);
        etNumCities = (EditText)findViewById(R.id.etNumCities);
        btnGenerate = (Button)findViewById(R.id.btnGenerateCities);
        btnClearMap = (Button)findViewById(R.id.btnClearMap);
        btnStart = (Button)findViewById(R.id.btnStart);
        tvTotalDistance = (TextView)findViewById(R.id.tvTotalDistance);
        
        btnGenerate.setOnClickListener(this);
        btnClearMap.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        
        if (savedInstanceState != null) {
            Log.i(TAG, "savedInstanceState found!");
            
            availableCities = (List<City>)savedInstanceState.getSerializable(KEY_AVAILABLE_CITIES);
            selectedCities = (List<City>)savedInstanceState.getSerializable(KEY_SELECTED_CITIES);
            result = (List<City>)savedInstanceState.getSerializable(KEY_RESULT_OF_TSP);
            selectedMethod = savedInstanceState.getInt(KEY_SELECTED_METHOD);
            tvTotalDistance.setText(savedInstanceState.getString(KEY_TOTAL_DIST));
            
            if (availableCities != null) {
                Log.i(TAG, "availableCities restored!");
            }
            if (selectedCities != null) {
                Log.i(TAG, "selectedCities restored!");
            }
            if (result != null) {
                Log.i(TAG, "results are restored!");
            }
        } else {
            Log.i(TAG, "no savedInstanceState, loading defaults");
            selectedCities = new ArrayList<City>();
            availableCities = new ArrayList<City>();
            
            File citiesTxt = new File(Environment.getExternalStorageDirectory() + "/" + "cities.txt");
            if (citiesTxt.exists()) {
                parseCitiesTxt(citiesTxt);
                if (availableCities.size() == 0) {
                    fillDefaultCities();
                }
            } else {
                fillDefaultCities();
            }
        }
        
        List<String> cityNames = new LinkedList<String>();
        for (City c : availableCities) {
            cityNames.add(c.getName());
        }
        Collections.sort(cityNames);
        cityNames.add(0, "<Válassz!>");
        ArrayAdapter<String> citySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cityNames);
        spCities.setAdapter(citySpinnerAdapter);
        spCities.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                City selectedCity = findCityByName((String)parent.getItemAtPosition(pos));
                if (selectedCity != null) {
                    if (!selectedCities.contains(selectedCity)) {
                        Log.d(TAG, "City added: " + selectedCity.getName());
                        selectedCities.add(selectedCity);
                        drawCities();
                    }
                    spCities.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        ArrayAdapter<CharSequence> methodSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.tsp_methods, android.R.layout.simple_spinner_dropdown_item);
        spMethod.setAdapter(methodSpinnerAdapter);
        spMethod.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                selectedMethod = pos;
                Log.i(TAG, "Selected method: " + selectedMethod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spMethod.setSelection(selectedMethod);
        
        Log.d(TAG, "Activity created");
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putSerializable(KEY_AVAILABLE_CITIES, (Serializable)availableCities);
        outState.putSerializable(KEY_SELECTED_CITIES, (Serializable)selectedCities);
        if (result != null) {
            outState.putSerializable(KEY_RESULT_OF_TSP, (Serializable)result);
        }
        outState.putInt(KEY_SELECTED_METHOD, selectedMethod);
        outState.putString(KEY_TOTAL_DIST, tvTotalDistance.getText().toString());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        setupMapIfNeeded();
//        drawCities();
    }
    
    @Override
    public void onMapLoaded() {
        drawCities();
        drawPaths(result);
    }

    @Override
    public void onClick(View v) {
        if (v == btnGenerate) {
            int numCities = Integer.parseInt(etNumCities.getText().toString());
            if (numCities > 0 && numCities < availableCities.size()) {
                if (result != null) {
                    result.clear();
                }
                selectedCities.clear();
                map.clear();
                tvTotalDistance.setText("");
                for (int i = 0; i < numCities; i++) {
                    City tempCity = null;
                    boolean hasCity = false;
                    while (!hasCity) {
                        tempCity = availableCities.get(getRandomInt(availableCities.size()-1));
                        if (!selectedCities.contains(tempCity)) {
                            selectedCities.add(tempCity);
                            hasCity = true;
                        }
                    }
                }
                drawCities();
            } else {
                Toast.makeText(getBaseContext(), R.string.invalid_range, Toast.LENGTH_LONG).show();
            }
        } else if (v == btnClearMap) {
            if (result != null) {
                result.clear();
            }
            selectedCities.clear();
            map.clear();
            tvTotalDistance.setText("");
        } else if (v == btnStart) {
            if (selectedCities.size() > 0) {
                if (result != null) {
                    result.clear();
                    map.clear();
                    drawCities();
                }
                AbstractTsp tspMethod = null;
                switch (selectedMethod) {
                    case METHOD_NEAREST_NEIGHBOUR:
                        tspMethod = new NearestNeighbourTsp(selectedCities);
                        break;
                    case METHOD_NEAREST_ADDITION:
                        tspMethod = new NearestAdditionTsp(selectedCities);
                        break;
                    case METHOD_NEAREST_INSERTION:
                        tspMethod = new NearestInsertionTsp(selectedCities);
                        break;
                    case METHOD_CHEAPEST_INSERTION:
                        tspMethod = new CheapestInsertionTsp(selectedCities);
                        break;
                }
                if (tspMethod != null) {
                   result = tspMethod.doTsp();
                   
                   double totalDistance = 0;
                   for (int i = 0; i < result.size(); i++) {
                       if (i != result.size()-1) {
                           int j = i+1;
                           totalDistance += tspMethod.getDistance(result.get(i), result.get(j));
                       }
                   }
                   tvTotalDistance.setText(getString(R.string.total_distance) + totalDistance);
                   
                   drawPaths(result);
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.please_select_some_cities, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void setupMapIfNeeded() {
        if (map == null) {
            map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setOnMapLoadedCallback(this);
        }
        
        if (map != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(47.180086, 19.503736), 7);
            map.animateCamera(update);
        }
    }
    
    private void drawCities() {
        if (map != null && selectedCities.size() > 0) {
            boolean isFirst = true;
            for (City c : selectedCities) {
                if (isFirst) {
                    isFirst = false;
                    map.addCircle(new CircleOptions().center(c.toLatLng()).radius(500).strokeColor(Color.RED));
                } else {
                    map.addCircle(new CircleOptions().center(c.toLatLng()).radius(500));
                }
            }
        }
    }
    
    private void drawPaths(List<City> cities) {
        if (map != null && cities != null && cities.size() > 0) {
            PolylineOptions lineOpts = new PolylineOptions();
            for (City c : cities) {
                lineOpts.add(c.toLatLng());
            }
            lineOpts.add(cities.get(0).toLatLng());
            lineOpts.width(3);
            lineOpts.color(0xff404040);
            map.addPolyline(lineOpts);
        }
    }

    private City findCityByName(String name) {
        if (name == null) {
            return null;
        }
        
        for (City c : availableCities) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
    
    private int getRandomInt(int max) {
        Random rand = new Random();
        int res = rand.nextInt(max);
        
        return res;
    }
    
    private void fillDefaultCities() {
        availableCities.clear();availableCities.add(new City("Sopron", 47.68489, 16.58305));
        availableCities.add(new City("Győr", 47.684167, 17.634444));
        availableCities.add(new City("Érd", 47.37837, 18.922));
        availableCities.add(new City("Budapest", 47.471944, 19.050278));
        availableCities.add(new City("Cegléd", 47.17433, 19.802));
        availableCities.add(new City("Vác", 47.77518, 19.13102));
        availableCities.add(new City("Salgotarján", 48.08531, 19.78689));
        availableCities.add(new City("Eger", 47.89902, 20.3747));
        availableCities.add(new City("Miskolc", 48.104167, 20.791667));
        availableCities.add(new City("Nyíregyháza", 47.95306, 21.72713));
        availableCities.add(new City("Debrecen", 47.52997, 21.63916));
        availableCities.add(new City("Szolnok", 47.17471, 20.17626));
        availableCities.add(new City("Békéscsaba", 46.679, 21.091));
        availableCities.add(new City("Hódmezővásárhely", 46.43039, 20.31881));
        availableCities.add(new City("Szeged", 46.255, 20.145));
        availableCities.add(new City("Kiskunhalas", 46.431944, 19.488333));
        availableCities.add(new City("Baja", 46.1833, 18.9537));
        availableCities.add(new City("Pécs", 46.071111, 18.233056));
        availableCities.add(new City("Nagykanizsa", 46.45, 16.983333));
        availableCities.add(new City("Zalaegerszeg", 46.839167, 16.851111));
        availableCities.add(new City("Szombathely", 47.23512, 16.62191));
    }
    
    private void parseCitiesTxt(File citiesTxt) {
        Log.d(TAG, "parsing cities.txt: " + citiesTxt.getAbsolutePath());
        availableCities.clear();
        try {
            FileReader fReader = new FileReader(citiesTxt);
            BufferedReader bReader = new BufferedReader(fReader);
            String line = null;
            
            while ((line = bReader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 3) {
                    double lat = Double.parseDouble(tokens[1].trim());
                    double lon = Double.parseDouble(tokens[2].trim());
                    availableCities.add(new City(tokens[0].trim(), lat, lon));
                }
            }
            
            bReader.close();
            fReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
