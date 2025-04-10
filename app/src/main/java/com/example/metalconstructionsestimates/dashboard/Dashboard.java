package com.example.metalconstructionsestimates.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.customviews.dashboard.DashboardDatabaseEntitiesTotals;
import com.example.metalconstructionsestimates.customviews.dashboard.DashboardEstimatesTotal;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.widget.TextView;
import java.util.Objects;


public class Dashboard extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    DashboardDatabaseEntitiesTotals dashboardDatabaseEntitiesTotals;
    DashboardEstimatesTotal dashboardEstimatesTotal;
    String customersCount, steelsCount, estimatesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
        toolbar.setBackgroundColor(Color.parseColor("#4F5EB1"));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new DashboardPagerAdapter(this));
        dashboardDatabaseEntitiesTotals = findViewById(R.id.dashboard_database_entities_totals);
        dashboardEstimatesTotal = findViewById(R.id.dashboard_estimates_totals);
        TextView allEstimatesTotalTextView = dashboardEstimatesTotal.getTextViewAllEstimatesTotal();
        TextView customersCountTextView = dashboardDatabaseEntitiesTotals.getTextViewCustomersCount();
        TextView estimatesCountTextView = dashboardDatabaseEntitiesTotals.getTextViewEstimatesCount();
        TextView steelsCountTextView = dashboardDatabaseEntitiesTotals.getTextViewSteelsCount();
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        String allEstimatesTotal = "All Estimates Total:";
        allEstimatesTotal += String.valueOf(dbAdapter.getEstimatesTotal());
        allEstimatesTotalTextView.setText(allEstimatesTotal);

        // Set counts for each category
        setCounts(dbAdapter, customersCountTextView, estimatesCountTextView, steelsCountTextView);

        tabLayout = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(getTabTitle(position))).attach();
    }

    private void setCounts(DBAdapter dbAdapter, TextView customersCountTextView, TextView estimatesCountTextView, TextView steelsCountTextView) {
        if (dbAdapter.getCustomersCount() == 0) {
            customersCountTextView.setText("Customers Count: 0 Customers");
        } else {
            customersCount = "Customers Count:";
            customersCount += String.valueOf(dbAdapter.getCustomersCount());
            customersCount += dbAdapter.getCustomersCount() == 1 ? " Customer" : " Customers";
            customersCountTextView.setText(customersCount);
        }

        if (dbAdapter.getEstimatesCount() == 0) {
            estimatesCountTextView.setText("Estimates Count: 0 Estimates");
        } else {
            estimatesCount = "Estimates Count:";
            estimatesCount += String.valueOf(dbAdapter.getEstimatesCount());
            estimatesCount += dbAdapter.getEstimatesCount() == 1 ? " Estimate" : " Estimates";
            estimatesCountTextView.setText(estimatesCount);
        }

        if (dbAdapter.getSteelsCount() == 0) {
            steelsCountTextView.setText("Steels Count: 0 Steels");
        } else {
            steelsCount = "Steels Count:";
            steelsCount += String.valueOf(dbAdapter.getSteelsCount());
            steelsCount += dbAdapter.getSteelsCount() == 1 ? " Steel" : " Steels";
            steelsCountTextView.setText(steelsCount);
        }
    }

    private String getTabTitle(int position) {
        // You can return tab titles based on position if needed
        switch (position) {
            case 0:
                return "Daily Estimates";
            case 1:
                return "Weekly Estimates";
            case 2:
                return "Monthly Estimates";
            case 3:
                return "Yearly Estimates";
            default:
                return null;
        }
    }
}