package com.example.metalconstructionsestimates.modules.estimates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.EstimateLinesListAdapter;
import com.example.metalconstructionsestimates.databinding.ActivityEstimateDetailsBinding;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.modules.estimateslines.AddEstimateLine;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class EstimateDetails extends AppCompatActivity {

    Integer estimateId;
    Integer customerId;

    String formattedTotalExcludingTax;
    String formattedTotalAfterDiscount;
    String formattedTotalAllTaxIncluded;
    private static final String DEFAULT_AMOUNT_PAID = "0.0";

    Estimate estimate;

    DBAdapter dbAdapter;
    TextView expirationDate,issueDate;
    String expirationDateValue = "", issueDateValue = "";

    private DatePickerDialog.OnDateSetListener expirationDateSetListner,issueDateSetListener;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    ActivityEstimateDetailsBinding activityEstimateDetailsBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEstimateDetailsBinding = ActivityEstimateDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityEstimateDetailsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String estimateIdExtra = getIntent().getStringExtra("estimateIdExtra");
        assert estimateIdExtra != null;
        estimateId = Integer.parseInt(estimateIdExtra);
        dbAdapter = new DBAdapter(getApplicationContext());
        estimate = dbAdapter.getEstimateById(estimateId);

        issueDate = findViewById(R.id.issueDateValue);
        expirationDate = findViewById(R.id.expirationDateValue);
        TextInputEditText totalExclTaxEditText = findViewById(R.id.totalExclTaxEditText);
        TextInputEditText discountEditText = findViewById(R.id.discountEditText);
        TextInputEditText amountPaidEditText = findViewById(R.id.amountPaidEditText_estimate_details);
        TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
        TextInputEditText locationEditText = findViewById(R.id.locationEditText_estimate_details);
        AtomicReference<TextInputEditText> customerIdEditText = new AtomicReference<>(findViewById(R.id.customerIdEditText));
        Button selectCustomerButton = findViewById(R.id.selectCustomerButton);
        TextInputEditText vatEditText = findViewById(R.id.vatEditText);
        TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);
        TextInputEditText estimateIdEditText = findViewById(R.id.estimateIdEditText_estimate_details);

        String amountPaid = Objects.requireNonNull(amountPaidEditText.getText()).toString();
        Float allTaxIncludedTotal = dbAdapter.getEstimateById(estimateId).getAllTaxIncludedTotal();
        if (!amountPaid.isEmpty()) {
            Float amountPaidFloat = Float.parseFloat(amountPaid);
            if (amountPaidFloat > allTaxIncludedTotal) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_amount_paid_greater), Toast.LENGTH_LONG).show();
                amountPaidEditText.setText(DEFAULT_AMOUNT_PAID);
            }
        }

        amountPaidEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String amountPaid = Objects.requireNonNull(amountPaidEditText.getText()).toString();
                Float allTaxIncludedTotal = dbAdapter.getEstimateById(estimateId).getAllTaxIncludedTotal();
                if (!amountPaid.isEmpty()) {
                    Float amountPaidFloat = Float.parseFloat(amountPaid);
                    if (amountPaidFloat > allTaxIncludedTotal) {
                        Toast.makeText(getApplicationContext(), "Amount paid cannot be greater than total", Toast.LENGTH_LONG).show();
                        amountPaidEditText.setText("0.0");
                    }
                }
            }
        });
        Button newEstimateLineButton = findViewById(R.id.newEstimateLineButton);
        Button updateEstimateButton = findViewById(R.id.updateButton_estimate_details);
        Button refreshEstimateLinesListButton = findViewById(R.id.refreshEstimateLinesListButton);
        Button deleteEstimateButton = findViewById(R.id.deleteEstimateButton);

        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<EstimateLine> estimateLinesList = db.searchEstimateLines(estimateId);
        TextView noEstimateLinesTextView = findViewById(R.id.noEstimateLinesTextView);

        ConstraintLayout.LayoutParams recyclerParams = (ConstraintLayout.LayoutParams)
                activityEstimateDetailsBinding.estimateLinesRecyclerView.getLayoutParams();

        ConstraintLayout.LayoutParams noLinesParams = (ConstraintLayout.LayoutParams)
                noEstimateLinesTextView.getLayoutParams();

        ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams)
                activityEstimateDetailsBinding.newEstimateLineButton.getLayoutParams();

        if (!estimateLinesList.isEmpty()) {
            // Show RecyclerView, hide "no lines" TextView
            noEstimateLinesTextView.setVisibility(View.GONE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.VISIBLE);

            activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            EstimateLinesListAdapter adapter = new EstimateLinesListAdapter(this, estimateLinesList);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setAdapter(adapter);

            // recyclerView constraints: Top to vatLayout
            recyclerParams.topToBottom = R.id.vatLayout;
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutParams(recyclerParams);

            // button constraints: Top to recyclerView
            buttonParams.topToBottom = R.id.estimateLinesRecyclerView;
            activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);

        } else {
            // Show "no lines" TextView, hide RecyclerView
            noEstimateLinesTextView.setVisibility(View.VISIBLE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.GONE);

            // noLinesTextView constraints: Top to vatLayout
            noLinesParams.topToBottom = R.id.vatLayout;
            noEstimateLinesTextView.setLayoutParams(noLinesParams);

            // button constraints: Top to noEstimateLinesTextView
            buttonParams.topToBottom = R.id.noEstimateLinesTextView;
            activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);
        }


        if(!estimate.getExpirationDate().isEmpty()){
            expirationDate.setText(estimate.getExpirationDate());
        }
        else{
            expirationDate.setText("");
        }

        if(!estimate.getIssueDate().isEmpty()){
            issueDate.setText(estimate.getIssueDate());
        }
        else{
            issueDate.setText("");
        }

        estimateIdEditText.setText(String.format(estimate.getId().toString()));
        locationEditText.setText(estimate.getDoneIn());
        customerId = estimate.getCustomer();

        if(estimate.getCustomer() == null){
            customerIdEditText.get().setText("");
        }
        else{
            customerIdEditText.get().setText(String.format(estimate.getCustomer().toString()));
        }


        if(estimate.getExcludingTaxTotal() == null){
            totalExclTaxEditText.setText("");
        }
        else{
            formattedTotalExcludingTax = new BigDecimal(estimate.getExcludingTaxTotal()).toPlainString();
            totalExclTaxEditText.setText(formattedTotalExcludingTax);
        }

        totalExclTaxEditText.setEnabled(false);

        if(estimate.getDiscount() == null){
            discountEditText.setText("");
        }
        else{
            discountEditText.setText(String.format(estimate.getDiscount().toString()));
        }

        if(estimate.getExcludingTaxTotalAfterDiscount() == null){
            totalAfterDiscountEditText.setText("");
        }
        else{
            formattedTotalAfterDiscount = BigDecimal.valueOf(estimate.getExcludingTaxTotalAfterDiscount()).toPlainString();
            totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
        }

        totalAfterDiscountEditText.setEnabled(false);

        if(estimate.getVat() == null){
            vatEditText.setText("");
        }
        else{
            vatEditText.setText(String.format(estimate.getVat().toString()));
        }

        if(estimate.getAllTaxIncludedTotal() == null){
            totalAllTaxIncludedEditText.setText("");
        }
        else{
            formattedTotalAllTaxIncluded = BigDecimal.valueOf(estimate.getAllTaxIncludedTotal()).toPlainString();
            totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);
        }

        totalAllTaxIncludedEditText.setEnabled(false);

        if(estimate.getAmountPaid() == null){
            amountPaidEditText.setText("");
        }
        else{
            amountPaidEditText.setText(String.format(estimate.getAmountPaid().toString()));
        }

        selectCustomerButton.setOnClickListener(v -> startActivityForResult());

        AtomicInteger selectedCustomerId = new AtomicInteger(-1);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                String customerIdExtraResult = extras.getString("customerIdExtraResult");
                                assert customerIdExtraResult != null;
                                selectedCustomerId.set(Integer.parseInt(customerIdExtraResult));
                                customerIdEditText.set(findViewById(R.id.customerIdEditText));
                                String customerName = dbAdapter.getCustomerById(selectedCustomerId.get()).getName();
                                customerIdEditText.get().setText(customerName);
                            }
                        }
                    }
                }
        );

        newEstimateLineButton.setOnClickListener(view -> {
            Intent intent = new Intent(EstimateDetails.this, AddEstimateLine.class);
            intent.putExtra("estimateIdExtra", estimateId.toString());
            startActivity(intent);
        });

        deleteEstimateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(EstimateDetails.this);
                alertDelete.setTitle("Confirmation de suppression");
                alertDelete.setMessage("Voulez-vous vraiment supprimer le devis?");
                alertDelete.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        TextInputEditText estimateIdTextInputEditText = findViewById(R.id.estimateIdEditText_estimate_details);
                        dbAdapter.deleteEstimate(Integer.parseInt(estimateIdTextInputEditText.getText().toString()));
                        Toast deleteSuccessToast = Toast.makeText(getApplicationContext(), "Suppression du devis a été effectuée avec succés", Toast.LENGTH_LONG);
                        deleteSuccessToast.show();

                        if(dbAdapter.retrieveEstimates().isEmpty()){
                            dbAdapter.setSeqEstimates();
                        }

                        Intent intent = new Intent(EstimateDetails.this, Estimates.class);
                        startActivity(intent);
                    }
                });
                alertDelete.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alertDelete.show();
            }
        });

        updateEstimateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertUpdate = new AlertDialog.Builder(EstimateDetails.this);
                alertUpdate.setTitle("Update Confirmation");
                alertUpdate.setMessage("Do you really want to update the estimate ?");
                alertUpdate.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        estimate = new Estimate();
                        TextView issueDate = findViewById(R.id.issueDateValue);
                        TextView expirationDate = findViewById(R.id.expirationDateValue);
                        TextInputEditText discountEditText = findViewById(R.id.discountEditText);
                        TextInputEditText estimateIdEditText = findViewById(R.id.estimateIdEditText_estimate_details);
                        TextInputEditText amountPaidEditText = findViewById(R.id.amountPaidEditText_estimate_details);
                        TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                        TextInputEditText locationEditText = findViewById(R.id.locationEditText_estimate_details);
                        TextInputEditText customerIdEditText = findViewById(R.id.customerIdEditText);
                        TextInputEditText vatEditText = findViewById(R.id.vatEditText);
                        TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);

                        estimate.setId(Integer.parseInt(estimateIdEditText.getText().toString()));

                        if (!locationEditText.getText().toString().isEmpty()) {
                            estimate.setDoneIn(locationEditText.getText().toString());
                        } else {
                            estimate.setDoneIn("");
                        }

                        if (!issueDate.getText().toString().isEmpty()) {
                            estimate.setIssueDate(issueDate.getText().toString());
                        } else {
                            estimate.setIssueDate("");
                        }

                        if (!expirationDate.getText().toString().isEmpty()) {
                            estimate.setExpirationDate(expirationDate.getText().toString());
                        } else {
                            estimate.setExpirationDate("");
                        }

                        Float totalExcludingTax = 0.0f;
                        totalExcludingTax = dbAdapter.getEstimateExcludingTaxTotal(estimateId);
                        estimate.setExcludingTaxTotal(totalExcludingTax);

                        if (!discountEditText.getText().toString().isEmpty()) {
                            estimate.setDiscount(Float.parseFloat(discountEditText.getText().toString()));
                        } else {
                            estimate.setDiscount(null);
                        }

                        if (!totalAfterDiscountEditText.getText().toString().isEmpty()) {
                            estimate.setExcludingTaxTotalAfterDiscount(Float.parseFloat(totalAfterDiscountEditText.getText().toString()));
                        } else {
                            estimate.setExcludingTaxTotalAfterDiscount(null);
                        }

                        if (!vatEditText.getText().toString().isEmpty()) {
                            estimate.setVat(Float.parseFloat(vatEditText.getText().toString()));
                        } else {
                            estimate.setVat(null);
                        }

                        if (!totalAllTaxIncludedEditText.getText().toString().isEmpty()) {
                            estimate.setAllTaxIncludedTotal(Float.parseFloat(totalAllTaxIncludedEditText.getText().toString()));
                        } else {
                            estimate.setAllTaxIncludedTotal(null);
                        }

                        if (!amountPaidEditText.getText().toString().isEmpty()) {
                            estimate.setAmountPaid(Float.parseFloat(amountPaidEditText.getText().toString()));
                        } else {
                            estimate.setAmountPaid(null);
                        }

                        if(dbAdapter.getCustomerById(Integer.parseInt(Objects.requireNonNull(customerIdEditText.getText()).toString())) == null){
                            Toast.makeText(getApplicationContext(), "Customer does not exist", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            if (!Objects.requireNonNull(customerIdEditText.getText()).toString().isEmpty()) {
                                estimate.setCustomer(Integer.parseInt(customerIdEditText.getText().toString()));
                            } else {
                                estimate.setCustomer(null);
                            }
                        }
                        dbAdapter.updateEstimate(estimate);
                        Toast updateSuccessToast = Toast.makeText(getApplicationContext(), "Estimate has been successfully updated", Toast.LENGTH_LONG);
                        updateSuccessToast.show();
                        Intent intent = new Intent(EstimateDetails.this, Estimates.class);
                        startActivity(intent);
                    }
                });

                alertUpdate.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alertUpdate.show();
            }
        });

        refreshEstimateLinesListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView issueDate = findViewById(R.id.issueDateValue);
                TextView expirationDate = findViewById(R.id.expirationDateValue);
                TextInputEditText totalExclTaxEditText = findViewById(R.id.totalExclTaxEditText);
                TextInputEditText discountEditText = findViewById(R.id.discountEditText);
                TextInputEditText estimateIdEditText = findViewById(R.id.estimateIdEditText_estimate_details);
                TextInputEditText amountPaidEditText = findViewById(R.id.amountPaidEditText_estimate_details);
                TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText locationEditText = findViewById(R.id.locationEditText_estimate_details);
                TextInputEditText vatEditText = findViewById(R.id.vatEditText);
                TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);
                estimate = dbAdapter.getEstimateById(Integer.parseInt(estimateIdEditText.getText().toString()));
                locationEditText.setText(estimate.getDoneIn());
                issueDate.setText(estimate.getIssueDate());
                expirationDate.setText(estimate.getExpirationDate());
                formattedTotalExcludingTax = new BigDecimal(estimate.getExcludingTaxTotal()).toPlainString();
                totalExclTaxEditText.setText(formattedTotalExcludingTax);
                discountEditText.setText(String.format(estimate.getDiscount().toString()));
                formattedTotalAfterDiscount = new BigDecimal(estimate.getExcludingTaxTotalAfterDiscount()).toPlainString();
                totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
                vatEditText.setText(String.format(estimate.getVat().toString()));
                formattedTotalAllTaxIncluded = new BigDecimal(estimate.getAllTaxIncludedTotal()).toPlainString();
                totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);
                amountPaidEditText.setText(String.format(estimate.getAmountPaid().toString()));
                ArrayList<EstimateLine> estimateLinesList = db.searchEstimateLines(Integer.parseInt(estimateIdEditText.getText().toString()));

                ConstraintLayout.LayoutParams recyclerParams = (ConstraintLayout.LayoutParams)
                        activityEstimateDetailsBinding.estimateLinesRecyclerView.getLayoutParams();

                ConstraintLayout.LayoutParams noLinesParams = (ConstraintLayout.LayoutParams)
                        noEstimateLinesTextView.getLayoutParams();

                ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams)
                        activityEstimateDetailsBinding.newEstimateLineButton.getLayoutParams();

                if (!estimateLinesList.isEmpty()) {
                    // Show RecyclerView, hide "no lines" TextView
                    noEstimateLinesTextView.setVisibility(View.GONE);
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.VISIBLE);

                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    EstimateLinesListAdapter adapter = new EstimateLinesListAdapter(EstimateDetails.this, estimateLinesList);
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setAdapter(adapter);

                    // recyclerView constraints: Top to vatLayout
                    recyclerParams.topToBottom = R.id.vatLayout;
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutParams(recyclerParams);

                    // button constraints: Top to recyclerView
                    buttonParams.topToBottom = R.id.estimateLinesRecyclerView;
                    activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);

                } else {
                    // Show "no lines" TextView, hide RecyclerView
                    noEstimateLinesTextView.setVisibility(View.VISIBLE);
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.GONE);

                    // noLinesTextView constraints: Top to vatLayout
                    noLinesParams.topToBottom = R.id.vatLayout;
                    noEstimateLinesTextView.setLayoutParams(noLinesParams);

                    // button constraints: Top to noEstimateLinesTextView
                    buttonParams.topToBottom = R.id.noEstimateLinesTextView;
                    activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);
                }

            }
        });

        discountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalExclTaxEditText = findViewById(R.id.totalExclTaxEditText);
                TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText vatEditText = findViewById(R.id.vatEditText);
                TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);

                String discount = s.toString();
                Float totalExcludingTax, totalExcludingTaxAfterDiscount, vat, totalAllTaxIncluded;
                totalExcludingTax = Float.parseFloat(Objects.requireNonNull(totalExclTaxEditText.getText()).toString());
                vat = Float.parseFloat(Objects.requireNonNull(vatEditText.getText()).toString());
                if (!discount.isEmpty()) {
                    totalExcludingTaxAfterDiscount = totalExcludingTax - totalExcludingTax * Float.parseFloat(discount) / 100;
                    formattedTotalAfterDiscount = new BigDecimal(totalExcludingTaxAfterDiscount).toPlainString();
                    totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
                    if (!vat.toString().isEmpty()) {
                        totalAllTaxIncluded = totalExcludingTaxAfterDiscount + totalExcludingTaxAfterDiscount * vat / 100;
                    } else {
                        totalAllTaxIncluded = totalExcludingTaxAfterDiscount;
                    }

                    formattedTotalAllTaxIncluded = new BigDecimal(totalAllTaxIncluded).toPlainString();
                    totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);


                } else {
                    totalAfterDiscountEditText.setText(String.format(Locale.getDefault(), "%s", totalExcludingTax));
                    if (!vat.toString().isEmpty()) {
                        totalAllTaxIncluded = totalExcludingTax + totalExcludingTax * vat / 100;
                    } else {
                        totalAllTaxIncluded = totalExcludingTax;
                    }
                    formattedTotalAllTaxIncluded = new BigDecimal(totalAllTaxIncluded).toPlainString();
                    totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);
                }
            }
        });

        vatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalExcludingTaxAfterDiscountTextInputEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText totalAllTaxIncludedTextInputEditText = findViewById(R.id.totalInclTaxEditText);
                String vat = s.toString();
                Float totalExcludingTaxAfterDiscount, totalAllTaxIncluded;
                totalExcludingTaxAfterDiscount = Float.parseFloat(totalExcludingTaxAfterDiscountTextInputEditText.getText().toString());
                if (!vat.isEmpty()) {
                    totalAllTaxIncluded = totalExcludingTaxAfterDiscount + totalExcludingTaxAfterDiscount * Float.parseFloat(vat) / 100;
                } else {
                    totalAllTaxIncluded = totalExcludingTaxAfterDiscount;
                }

                formattedTotalAllTaxIncluded = new BigDecimal(totalAllTaxIncluded).toPlainString();
                totalAllTaxIncludedTextInputEditText.setText(formattedTotalAllTaxIncluded);
            }
        });

        issueDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    issueDateSetListener,
                    year, month, day
            );
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    expirationDateSetListner,
                    year, month, day
            );
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateSetListner = (picker, year, month, day) -> {
            month = month + 1;
            expirationDateValue = year + "-" + month + "-" + day;
            expirationDate.setText(expirationDateValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

            Date expirationDate = null;

            try {
                expirationDate = sdf.parse(expirationDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date issueDate = null;

            if(!issueDateValue.isEmpty()){
                try {
                    issueDate = sdf.parse(issueDateValue);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                long diffInMillis = expirationDate.getTime() - issueDate.getTime();
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);
                if(daysBetween <= 0){
                    Toast.makeText(getApplicationContext(), "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    TextView expirationDateTextView = (TextView) findViewById(R.id.expirationDateValue);
                    expirationDateTextView.setText(R.string.expirationDate);
                    expirationDateValue = "";
                }
            }
        };

        issueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            issueDateValue = year + "-" + month + "-" + day;
            issueDate.setText(issueDateValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

            Date issueDate = null;

            try {
                issueDate = sdf.parse(issueDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date expirationDate = null;

            if(!expirationDateValue.isEmpty()){
                try {
                    expirationDate = sdf.parse(expirationDateValue);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                long diffInMillis = expirationDate.getTime() - issueDate.getTime();
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);
                if(daysBetween <= 0){
                    Toast.makeText(getApplicationContext(), "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    TextView issueDateTextView = (TextView) findViewById(R.id.issueDateValue);
                    issueDateTextView.setText(R.string.issueDate);
                    issueDateValue = "";
                }
            }
        };

    }
    public void startActivityForResult() {
        Intent intent = new Intent(EstimateDetails.this, Customers.class);
        activityResultLauncher.launch(intent);
    }
}