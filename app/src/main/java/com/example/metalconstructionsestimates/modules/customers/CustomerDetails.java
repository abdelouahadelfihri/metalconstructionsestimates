package com.example.metalconstructionsestimates.modules.customers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.android.material.textfield.TextInputEditText;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.customviews.UpdateDeleteButtons;

public class CustomerDetails extends AppCompatActivity {
    Customer customer;
    DBAdapter adapter;
    UpdateDeleteButtons updateDeleteButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        Toolbar toolbar = findViewById(R.id.toolbar_customer_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String customerIdExtra = getIntent().getStringExtra("customerIdExtra");
        Integer customerId = Integer.parseInt(customerIdExtra);
        adapter = new DBAdapter(getApplicationContext());
        customer = adapter.getCustomerById(customerId);

        TextInputEditText customerIdTextInputEditText = findViewById(R.id.editText_customer_id_details);
        TextInputEditText customerNameTextInputEditText = findViewById(R.id.editText_customer_name_details);
        TextInputEditText customerEmailTextInputEditText = findViewById(R.id.editText_customer_email_details);
        TextInputEditText customerPhoneTextInputEditText = findViewById(R.id.editText_customer_phone_details);
        TextInputEditText customerMobileTextInputEditText = findViewById(R.id.editText_customer_mobile_details);
        TextInputEditText customerFaxTextInputEditText = findViewById(R.id.editText_customer_fax_details);
        TextInputEditText customerAddressTextInputEditText = findViewById(R.id.editText_customer_address_details);

        customerIdTextInputEditText.setText(customerId.toString());
        customerNameTextInputEditText.setText(customer.getName());
        customerEmailTextInputEditText.setText(customer.getEmail());
        customerPhoneTextInputEditText.setText(customer.getTelephone());
        customerMobileTextInputEditText.setText(customer.getMobile());
        customerFaxTextInputEditText.setText(customer.getFax());
        customerAddressTextInputEditText.setText(customer.getAddress());

        updateDeleteButtons = findViewById(R.id.customers_details_update_delete_buttons);
        Button deleteCustomer = updateDeleteButtons.getDeleteButton();
        Button updateCustomer = updateDeleteButtons.getUpdateButton();

        deleteCustomer.setOnClickListener(view -> {
            AlertDialog.Builder alertDelete = new AlertDialog.Builder(CustomerDetails.this);
            alertDelete.setTitle("Delete Confirmation");
            alertDelete.setMessage("Do you really want to delete the customer ?");
            alertDelete.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                // continue with delete
                customer = new Customer();
                TextInputEditText customerIdTextInputEditText12 = (TextInputEditText) findViewById(R.id.editText_customer_id_details);
                adapter.deleteCustomer(Integer.parseInt(customerIdTextInputEditText12.getText().toString()));
                Toast deletesuccess = Toast.makeText(getApplicationContext(), "Suppression du pièce métallique a été effectuée avec succés", Toast.LENGTH_LONG);
                deletesuccess.show();
                if(adapter.retrieveCustomers().isEmpty()){
                    adapter.setSeqCustomers();
                }
            });
            alertDelete.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // close dialog
                    dialog.cancel();
                }
            });
            alertDelete.show();
        });

        updateCustomer.setOnClickListener(view -> {
            AlertDialog.Builder alertUpdate = new AlertDialog.Builder(CustomerDetails.this);
            alertUpdate.setTitle("Update Confirmation");
            alertUpdate.setMessage("Do you really want to update customer ?");
            alertUpdate.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                // continue with delete
                customer = new Customer();

                if (!customerIdTextInputEditText.getText().toString().isEmpty()) {
                    customer.setId(Integer.parseInt(customerIdTextInputEditText.getText().toString()));
                } else {
                    customer.setId(Integer.valueOf(""));
                }

                if (!customerNameTextInputEditText.getText().toString().isEmpty()) {
                    customer.setName(customerNameTextInputEditText.getText().toString());
                } else {
                    customer.setName("");
                }

                if (!customerEmailTextInputEditText.getText().toString().isEmpty()) {
                    customer.setEmail(customerEmailTextInputEditText.getText().toString());
                } else {
                    customer.setEmail("");
                }

                if (!customerPhoneTextInputEditText.getText().toString().isEmpty()) {
                    customer.setTelephone(customerPhoneTextInputEditText.getText().toString());
                } else {
                    customer.setTelephone("");
                }

                if (!customerMobileTextInputEditText.getText().toString().isEmpty()) {
                    customer.setMobile(customerMobileTextInputEditText.getText().toString());
                } else {
                    customer.setMobile("");
                }

                if (!customerFaxTextInputEditText.getText().toString().isEmpty()) {
                    customer.setFax(customerFaxTextInputEditText.getText().toString());
                } else {
                    customer.setFax("");
                }

                if (!customerAddressTextInputEditText.getText().toString().isEmpty()) {
                    customer.setAddress(customerAddressTextInputEditText.getText().toString());
                } else {
                    customer.setAddress("");
                }

                adapter.updateCustomer(customer);
                Toast updateSuccessToast = Toast.makeText(getApplicationContext(), "Modification du client a été effectué avec succés", Toast.LENGTH_LONG);
                updateSuccessToast.show();
            });
            alertUpdate.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // close dialog
                    dialog.cancel();
                }
            });
            alertUpdate.show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();  // or use NavUtils.navigateUpFromSameTask(this);
        return true;
    }
}