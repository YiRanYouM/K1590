package com.example.k1590;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddActivity extends AppCompatActivity {
    private EditText edt_name, edt_order;
    private Spinner sp_type;
    private Button bt_ok;
    private String user_name;
    private int index = 0;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        user_name = getIntent().getStringExtra("name");

        helper = new DBHelper(this,"eventDataBase", null, 1);

        edt_name = findViewById(R.id.edit_name);
        edt_order = findViewById(R.id.edit_order);
        bt_ok = findViewById(R.id.bt_add_ok);
        sp_type = findViewById(R.id.sp_type);

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edt_name.getText().toString();
                String order = edt_order.getText().toString();
                int size = helper.getCount();
                helper.addThing(size+1+"", user_name, name, order, index+"");
                finish();
            }
        });
    }
}
