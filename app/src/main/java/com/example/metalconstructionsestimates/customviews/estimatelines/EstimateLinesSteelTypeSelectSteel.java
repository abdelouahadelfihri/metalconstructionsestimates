package com.example.metalconstructionsestimates.customviews.estimatelines;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;


import com.example.metalconstructionsestimates.R;

public class EstimateLinesSteelTypeSelectSteel extends LinearLayout {
    private TextInputEditText editTextSteelType;
    private Button selectSteelButton;

    public TextInputEditText getTextInputEditTextSteelType(){
        return editTextSteelType;
    }

    public Button getSelectSteelButton(){
        return selectSteelButton;
    }

    public EstimateLinesSteelTypeSelectSteel(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_lines_steel_type_select_steel, this, true);
        editTextSteelType = findViewById(R.id.editText_steel_type_textInputEditText);
        selectSteelButton = findViewById(R.id.button_select_steel);
    }
}