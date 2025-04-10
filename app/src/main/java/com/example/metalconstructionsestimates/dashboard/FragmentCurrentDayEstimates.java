package com.example.metalconstructionsestimates.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.databinding.FragmentCurrentDayEstimatesBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentCurrentDayEstimates extends Fragment {
    FragmentCurrentDayEstimatesBinding fragmentCurrentDayEstimatesBinding;
    Locale moroccoLocale = new Locale("ar", "MA");
    NumberFormat numberFormat = NumberFormat.getNumberInstance(moroccoLocale);
    String currentDayEstimatesTotal;
    String currentDayEstimatesCount;
    public FragmentCurrentDayEstimates() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentCurrentDayEstimatesBinding = FragmentCurrentDayEstimatesBinding.inflate(inflater,container,false);

        DBAdapter dbAdapter = new DBAdapter(getContext());
        if(dbAdapter.getCurrentDayEstimatesCount() == 0)
            fragmentCurrentDayEstimatesBinding.currentDayEstimatesCount.getTextViewCurrentDayEstimatesCount().setText(R.string.noDailyEstimates);
        else{
            if(dbAdapter.getCurrentDayEstimatesCount() == 1){
                currentDayEstimatesCount= "Daily Number of Estimates : 1";
            }
            else{
                currentDayEstimatesCount = "Daily Number of Estimates : " + dbAdapter.getCurrentDayEstimatesCount();
            }
            fragmentCurrentDayEstimatesBinding.currentDayEstimatesCount.getTextViewCurrentDayEstimatesCount().setText(currentDayEstimatesCount);
        }

        if(dbAdapter.getCurrentDayEstimatesTotal() == 0.0f){
            fragmentCurrentDayEstimatesBinding.currentDayEstimatesTotal.getTextViewCurrentDayEstimatesTotal().setText("Daily Total of Estimates : 0 DH");
        }
        else{
            currentDayEstimatesTotal = "Daily Total of Estimates :";
            currentDayEstimatesTotal += String.valueOf(dbAdapter.getCurrentDayEstimatesTotal());
            currentDayEstimatesTotal += " DH";
            fragmentCurrentDayEstimatesBinding.currentDayEstimatesTotal.getTextViewCurrentDayEstimatesTotal().setText(currentDayEstimatesTotal);
        }

        ArrayList<Estimate> currentDayEstimatesList = dbAdapter.getCurrentDayEstimates();



        if (currentDayEstimatesList.isEmpty()){
            fragmentCurrentDayEstimatesBinding.recyclerViewCurrentDayEstimates.setVisibility(View.GONE);
            fragmentCurrentDayEstimatesBinding.noEstimatesInCurrentDayTextView.setVisibility(View.VISIBLE);
        } else {
            fragmentCurrentDayEstimatesBinding.recyclerViewCurrentDayEstimates.setVisibility(View.VISIBLE);
            fragmentCurrentDayEstimatesBinding.noEstimatesInCurrentDayTextView.setVisibility(View.GONE);
            EstimatesListAdapter estimateAdapter = new EstimatesListAdapter(getContext(), currentDayEstimatesList);
            fragmentCurrentDayEstimatesBinding.recyclerViewCurrentDayEstimates.setAdapter(estimateAdapter);
            fragmentCurrentDayEstimatesBinding.recyclerViewCurrentDayEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return fragmentCurrentDayEstimatesBinding.getRoot();
    }
}