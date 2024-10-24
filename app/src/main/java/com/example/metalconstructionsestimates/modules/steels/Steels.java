package com.example.metalconstructionsestimates.modules.steels;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.SteelsListAdapter;
import com.example.metalconstructionsestimates.databinding.ActivitySteelsBinding;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Steel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class Steels extends AppCompatActivity {
    Intent intent;
    SteelsListAdapter steelsListAdapter;
    TextInputEditText steel_id,steel_type,steel_weight,steel_unit;
    FloatingActionButton addSteel, searchSteels, clearSearchSteelForm;
    Spinner geometric_shape;
    ActivitySteelsBinding activitySteelsBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySteelsBinding = ActivitySteelsBinding.inflate(getLayoutInflater());
        setContentView(activitySteelsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar_steels);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        geometric_shape = findViewById(R.id.spinner_steel_geometric_shape_steels);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.geometric_shapes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        geometric_shape.setAdapter(adapter);
        geometric_shape.setSelection(0);
        geometric_shape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                TextInputEditText unitTextInputEditText = findViewById(R.id.editText_steel_unit_steels);
                switch(item.toString()){
                    case "Profile":
                        unitTextInputEditText.setText("Meter");
                        break;
                    case "Surface":
                        unitTextInputEditText.setText("Square Meter");
                        break;
                    case "Volume":
                        unitTextInputEditText.setText("Cubic Meter");
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<Steel> steelsList = db.retrieveSteels();
        RecyclerView recyclerViewSteels = findViewById(R.id.recycler_view_steels);
        if (steelsList.isEmpty()) {
            activitySteelsBinding.recyclerViewSteels.setVisibility(View.GONE);
            activitySteelsBinding.emptyView.setVisibility(View.VISIBLE);
            activitySteelsBinding.emptyView.setText(R.string.noSteels);
        } else {
            activitySteelsBinding.recyclerViewSteels.setVisibility(View.VISIBLE);
            activitySteelsBinding.emptyView.setVisibility(View.GONE);
            steelsListAdapter = new SteelsListAdapter(this, steelsList);
            recyclerViewSteels.setHasFixedSize(true);
            recyclerViewSteels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerViewSteels.setAdapter(steelsListAdapter);
        }



        searchSteels = findViewById(R.id.fab_search_steels);

        searchSteels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                steel_id = (TextInputEditText) findViewById(R.id.editText_steel_id_steels);
                steel_type = (TextInputEditText) findViewById(R.id.editText_steel_type_steels);
                geometric_shape = (Spinner) findViewById(R.id.spinner_steel_geometric_shape_steels);
                steel_weight = (TextInputEditText) findViewById(R.id.editText_steel_weight_steels);
                steel_unit = (TextInputEditText) findViewById(R.id.editText_steel_unit_steels);
                Steel steel = new Steel();
                if (!steel_id.getText().toString().isEmpty()) {
                    steel.setId(Integer.parseInt(steel_id.getText().toString()));
                } else {
                    steel.setId(null);
                }
                if (!steel_type.getText().toString().isEmpty()) {
                    steel.setType(steel_type.getText().toString());
                } else {
                    steel.setType(null);
                }
                if (!geometric_shape.getSelectedItem().toString().isEmpty() && (!geometric_shape.getSelectedItem().toString().equals("Family"))) {
                    steel.setGeometricShape(geometric_shape.getSelectedItem().toString());
                } else {
                    steel.setGeometricShape(null);
                }
                if (!steel_weight.getText().toString().isEmpty()) {
                    steel.setWeight(Float.parseFloat(steel_weight.getText().toString()));
                } else {
                    steel.setWeight(null);
                }

                if (!steel_unit.getText().toString().isEmpty()) {
                    steel.setUnit(steel_unit.getText().toString());
                } else {
                    steel.setUnit(null);
                }

                RecyclerView recyclerViewSteels = (RecyclerView) findViewById(R.id.recycler_view_steels);
                DBAdapter db = new DBAdapter(getApplicationContext());
                ArrayList<Steel> steelsList = db.searchSteels(steel);
                steelsListAdapter = new SteelsListAdapter(Steels.this, steelsList);
                if (steelsList.isEmpty()) {
                    recyclerViewSteels.setVisibility(View.GONE);
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                    recyclerViewSteels.setVisibility(View.VISIBLE);
                    recyclerViewSteels.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewSteels.setAdapter(steelsListAdapter);
                }
            }
        });

        addSteel = findViewById(R.id.fab_add_steel);

        addSteel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Steels.this, AddSteel.class);
                startActivity(intent);
            }
        });

        clearSearchSteelForm = findViewById(R.id.fab_clear_steel_form);

        clearSearchSteelForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter db;

                ArrayList<Steel> steelsList;
                SteelsListAdapter steelsListAdapter;
                steel_id = (TextInputEditText) findViewById(R.id.editText_steel_id_steels);
                steel_type = (TextInputEditText) findViewById(R.id.editText_steel_type_steels);
                geometric_shape = (Spinner) findViewById(R.id.spinner_steel_geometric_shape_steels);
                steel_weight = (TextInputEditText) findViewById(R.id.editText_steel_weight_steels);
                steel_unit = (TextInputEditText) findViewById(R.id.editText_steel_unit_steels);
                steel_id.getText().clear();
                steel_type.getText().clear();
                geometric_shape.setSelection(0);
                steel_weight.getText().clear();
                steel_unit.getText().clear();

                db = new DBAdapter(getApplicationContext());

                steelsList = db.retrieveSteels();

                RecyclerView recyclerViewSteels = findViewById(R.id.recycler_view_steels);

                if (steelsList.isEmpty()) {

                    recyclerViewSteels.setVisibility(View.GONE);
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);

                } else {

                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                    recyclerViewSteels.setVisibility(View.VISIBLE);
                    steelsListAdapter = new SteelsListAdapter(Steels.this, steelsList);
                    recyclerViewSteels.setAdapter(steelsListAdapter);

                }
            }
        });
    }
}