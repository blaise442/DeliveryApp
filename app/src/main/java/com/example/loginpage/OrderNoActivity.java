package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderNoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Prevents NetworkOnMainThreadException
        // Networking operations should not be done on the main thread
        // This is a temporary fix
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_no);

        final TextView vendor = findViewById(R.id.vendorLabel);
        final EditText orderNo = findViewById(R.id.orderNoTextField);
        final Button verify = findViewById(R.id.schSubmitButton);
        final Button cancel = findViewById(R.id.schHomeButton);
        final TextView error = findViewById(R.id.errorLabel);
        String ven;
        //https://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data
        //used to get vendor data from VendorSelectActivity
        try{
            UserHistoryData currentUsrData = (UserHistoryData) getIntent().getSerializableExtra("data");

            //first vendor should appear as soon as order page is open
            String[] vendorarr = currentUsrData.getVendor();
            int index = 0;
            vendor.setText(vendorarr[index]);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultSet result = null;
                try {
                    String orderNum = orderNo.getText().toString() ;
                    Connection conn = null;
                    Class.forName("com.mysql.jdbc.Driver");

                    conn = DriverManager.getConnection(Helper.conString, Helper.user, Helper.password); //created a god user for this work


                    PreparedStatement statement = (PreparedStatement) conn.prepareStatement("SELECT orderNo FROM ordernumbers WHERE orderNo = "+ orderNum);
                    result = statement.executeQuery();


                }catch(Exception e){
                    e.printStackTrace();
                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderNoActivity.this, SchedulerActivity.class);
                startActivity(intent);
            }
        });
    }
}