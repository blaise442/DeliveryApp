package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VendorSelectActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Prevents NetworkOnMainThreadException
        // Networking operations should not be done on the main thread
        // This is a temporary fix
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_select);
        final Spinner venSelection = findViewById(R.id.filterDropDown);
        final Button search = findViewById(R.id.schSubmitButton);
        final EditText searchField = findViewById(R.id.searchTextField);
        final LinearLayout resultLayout = findViewById(R.id.results);
        final Button add = findViewById(R.id.addButton);
        final TextView selected = findViewById(R.id.vendorsSelected);
        final Button cancel = findViewById(R.id.cancelButton);
        final Button submit = findViewById(R.id.schHomeButton);
        UserHistoryData currentUsrData = new UserHistoryData();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vendorSelection, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        venSelection.setAdapter(adapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ResultSet result = null;
                resultLayout.removeAllViews();
                if(venSelection.getSelectedItem().toString().equals("All Vendors")){
                    try {
                        String name = searchField.getText().toString() ;
                        Connection conn = null;
                        Class.forName("com.mysql.jdbc.Driver");

                        conn = DriverManager.getConnection(Helper.conString, Helper.user, Helper.password); //created a god user for this work


                        PreparedStatement statement = (PreparedStatement) conn.prepareStatement("SELECT vendorID, name, location FROM vendors WHERE LOWER(name) LIKE LOWER('%" + name + "%')");
                        result = statement.executeQuery();
                        if (!result.isBeforeFirst()) {
                            TextView fail = new TextView(VendorSelectActivity.this);
                            fail.setText("Vendors not found");
                            resultLayout.addView(fail);
                        }
                        else {
                            while(result.next()){
                                CheckBox vendor = new CheckBox(VendorSelectActivity.this);
                                String boxString = result.getString("name") + " in " + result.getString("location");
                                vendor.setTag(result.getString("vendorID"));
                                vendor.setText(boxString);
                                resultLayout.addView(vendor);
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    ArrayList<UserHistoryData> historyList = UserHistoryData.readHistory(VendorSelectActivity.this);
                    //https://stackoverflow.com/questions/15422428/iterator-over-hashmap-in-java
                    Map<Integer, String> hash = new HashMap<>();
                    if(historyList == null){
                        TextView fail = new TextView(VendorSelectActivity.this);
                        fail.setText("Past Vendors not found");
                        resultLayout.addView(fail);
                    }else{
                        for(int i = 0; i < historyList.size(); i++){
                            UserHistoryData del = historyList.get(i);
                            String[] venarr = del.getVendor();
                            int[] venIDarr = del.getVendorId();
                            for(int j = 0; j < venarr.length; j++){
                                if(venarr[j] != null){
                                    if(venarr[j].contains(searchField.getText().toString())){
                                        hash.put(venIDarr[j], venarr[j]);
                                    }
                                }
                            }

                        }
                        for(Integer key : hash.keySet()){
                            CheckBox vendor = new CheckBox(VendorSelectActivity.this);
                            vendor.setTag(key);
                            vendor.setText(hash.get(key));
                            resultLayout.addView(vendor);
                        }
                    }
                }
            }

        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //https://stackoverflow.com/questions/4809834/how-to-iterate-through-a-views-elements
                    for (int i = 0; i < resultLayout.getChildCount(); i++) {
                        CheckBox cb = (CheckBox) resultLayout.getChildAt(i);
                        if (cb.isChecked()) {
                            String s = cb.getText().toString();
                            String ss = s.substring(0, s.indexOf(" in "));
                            int tag = Integer.parseInt(String.valueOf(cb.getTag()));
                            currentUsrData.setVendor(ss, tag);
                            selected.append(ss + "\n");
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorSelectActivity.this, OrderNoActivity.class);
                intent.putExtra("data", currentUsrData);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorSelectActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}