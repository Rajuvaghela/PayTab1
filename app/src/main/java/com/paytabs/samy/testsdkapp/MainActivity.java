package com.paytabs.samy.testsdkapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import paytabs.project.PayTabActivity;

public class MainActivity extends AppCompatActivity {

    EditText etToken,etTokenPassword,etMerchantEmail,etMerchantSecret,etAmount,etCustomerEmail,
            etTokenEmail;
    Button btnPay;

    CheckBox ckTookenization,ckExistingCustomer;
    LinearLayout lnTookenization;

    Spinner spCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etToken = (EditText)findViewById(R.id.etToken);
        etTokenPassword = (EditText)findViewById(R.id.etTokenPassword);
        etMerchantEmail = (EditText)findViewById(R.id.etEmail);
        etMerchantSecret = (EditText)findViewById(R.id.etSecret);
        etAmount = (EditText)findViewById(R.id.etAmount);
        etCustomerEmail = (EditText)findViewById(R.id.etCustomerEmail);
        etTokenEmail = (EditText)findViewById(R.id.etTokenEmail);

        ckTookenization = (CheckBox)findViewById(R.id.ckTookenization);
        ckExistingCustomer = (CheckBox)findViewById(R.id.ckExistingCustomer);

        lnTookenization = (LinearLayout)findViewById(R.id.lnToken);
        spCurrency = (Spinner)findViewById(R.id.spCurrency);

        btnPay = (Button)findViewById(R.id.btnPay);


    }

    @Override
    public void onResume(){
        super.onResume();

        if(ckTookenization.isChecked()){
            useTookenization(ckTookenization);
        }
    }

    public void useTookenization(View view){
        if(ckTookenization.isChecked()){
            ckExistingCustomer.setVisibility(View.VISIBLE);
        }else{
            ckExistingCustomer.setVisibility(View.GONE);
        }
    }

    public void existingCustomer(View view){
        if(ckExistingCustomer.isChecked()){
            lnTookenization.setVisibility(View.VISIBLE);
        }else {
            lnTookenization.setVisibility(View.GONE);
        }
    }

    public void Pay(View view){
        if(etMerchantEmail.getText().toString().isEmpty() || etMerchantSecret.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Please Provide your email and secret key",Toast.LENGTH_LONG).show();
            return;
        }

        if(!isEmailValid(etMerchantEmail.getText().toString())){
            Toast.makeText(this,"Please Provide valid merchant Email address",Toast.LENGTH_LONG).show();
            return;
        }


        String amount = etAmount.getText().toString();
        if(amount.isEmpty() || Float.parseFloat(amount) <=0){
            Toast.makeText(this,"Please Provide valid amount",Toast.LENGTH_LONG).show();
            return;
        }

        if(!isEmailValid(etCustomerEmail.getText().toString())){
            Toast.makeText(this,"Please Provide customer Email address",Toast.LENGTH_LONG).show();
            return;
        }




        String token = etToken.getText().toString();
        String tokenPassword = etTokenPassword.getText().toString();

        Intent in = new Intent(getApplicationContext(), PayTabActivity.class);
        in.putExtra("pt_merchant_email", "rajuvg.lujayninfoways@gmail.com"); //this a demo account for testing the sdk
        in.putExtra("pt_secret_key", "M1mtq6zgsLJepYJvZKRty6hDstd8yNZlOmPespvVae9AdS5dCXGb4YOwr2E0PDlE0aSszooKDxlQpr42pLdtJ2Zb5DbUhxyiHrLh");//Add your Secret Key Here
        in.putExtra("pt_transaction_title", "Rashid test_SDK");
//		in.putExtra("pt_amount", amountstr);
        in.putExtra("pt_amount", etAmount.getText().toString());
        in.putExtra("pt_timeout_in_seconds", "300");
        in.putExtra("pt_shared_prefs_name", "rashid_shared");
        in.putExtra("pt_currency_code", spCurrency.getSelectedItem().toString());
        in.putExtra("pt_customer_phone_number", "009733");
        in.putExtra("pt_customer_email", etCustomerEmail.getText().toString());
        in.putExtra("pt_order_id", "1234567");
        in.putExtra("pt_product_name", "Samsung Galaxy S6");

        //Billing Address
        in.putExtra("pt_address_billing", "Flat 1,Building 123, Road 2345");
        in.putExtra("pt_city_billing", "Dubai");
        in.putExtra("pt_state_billing", "3510");
        in.putExtra("pt_country_billing", "BHR");
        in.putExtra("pt_postal_code_billing", "00973"); //Put Country Phone code if Postal code not available '00973'
        //Shipping Address
        in.putExtra("pt_address_shipping", "Flat 1,Building 123, Road 2345");
        in.putExtra("pt_city_shipping", "Juffair");
        in.putExtra("pt_state_shipping", "Manama");
        in.putExtra("pt_country_shipping", "BHR");
        in.putExtra("pt_postal_code_shipping", "00973"); //Put Country Phone code if Postal code not available '00973'

        if (ckTookenization.isChecked()) {
            in.putExtra("pt_is_tokenization", "TRUE");
        }

        if(ckExistingCustomer.isChecked()){
            String customerTokenEmail = etCustomerEmail.getText().toString();
            if(token.isEmpty() || tokenPassword.isEmpty() || !isEmailValid(customerTokenEmail)){
                Toast.makeText(this,"Please do at least one tokenization transaction without checking 'is Existing customer option'!",Toast.LENGTH_LONG).show();
                return;
            }

            in.putExtra("pt_is_tokenization", "TRUE");
            in.putExtra("pt_is_existing_customer", "yess");
            in.putExtra("pt_customer_email", customerTokenEmail);
            in.putExtra("pt_customer_password", tokenPassword);
            in.putExtra("pt_pt_token", token);
        }else{
            in.putExtra("pt_is_existing_customer", "no");
        }
        int requestCode = 0;
        startActivityForResult(in, requestCode);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences shared_prefs = getSharedPreferences("rashid_shared", MODE_PRIVATE);
        String pt_response_code = shared_prefs.getString("pt_response_code", "DEFAULT");
        String pt_transaction_id = shared_prefs.getString("pt_transaction_id", "DEFAULTtext");
        //If its not tokenisation transaction, the following three tokenisation variable will return as empty
        String pt_token = shared_prefs.getString("pt_token", "DefaultTokenTxt");
        String pt_token_customer_password = shared_prefs.getString("pt_token_customer_password", "DefaultTokenTxt");
        String pt_token_customer_email = shared_prefs.getString("pt_token_customer_email", "DefaultTokenTxt");

        etToken.setText(pt_token);
        etTokenPassword.setText(pt_token_customer_password);
        etTokenEmail.setText(pt_token_customer_email);

//        Toast.makeText(MainActivity.this, "PayTabs Response Code : " + pt_response_code, Toast.LENGTH_LONG).show();
//        Toast.makeText(MainActivity.this, "Paytabs transaction ID after payment : " + pt_transaction_id, Toast.LENGTH_LONG).show();
//
//        Toast.makeText(MainActivity.this, "pt_token : " + pt_token, Toast.LENGTH_LONG).show();
//        Toast.makeText(MainActivity.this, "pt_token_customer_password : " + pt_token_customer_password, Toast.LENGTH_LONG).show();
//        Toast.makeText(MainActivity.this, "pt_token_customer_email : " + pt_token_customer_email, Toast.LENGTH_LONG).show();

    }


}
