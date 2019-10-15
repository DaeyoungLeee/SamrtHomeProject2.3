package kr.co.aiotlab.samrthomeproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetIP extends AppCompatActivity {

    private EditText edt_ip, edt_cctv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setip);

        Button btn_okay = findViewById(R.id.btn_okay);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        edt_ip = findViewById(R.id.edt_ip);
        edt_cctv = findViewById(R.id.edt_cctv);

        SharedPreferences ip = getSharedPreferences("IP_ADDRESS", MODE_PRIVATE);
        String ip_address = ip.getString("IP", "0");
        String cctv_address = ip.getString("CCTV", "0");

        edt_ip.setText(ip_address);
        edt_cctv.setText(cctv_address);

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences ip = getSharedPreferences("IP_ADDRESS", MODE_PRIVATE);
                SharedPreferences.Editor editor = ip.edit();
                editor.putString("IP", edt_ip.getText().toString());
                editor.putString("CCTV", edt_cctv.getText().toString());
                editor.apply();
                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
