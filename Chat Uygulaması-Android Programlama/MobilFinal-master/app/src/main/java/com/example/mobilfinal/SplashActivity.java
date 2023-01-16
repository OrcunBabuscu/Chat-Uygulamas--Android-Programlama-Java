package com.example.mobilfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btn_login,btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        btn_login= findViewById(R.id.buttonLogin);
        btn_register=findViewById(R.id.buttonRegister);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            //eğer kullanıcı giriş yapmış ise
            Toast.makeText(this, "Otomatik Giriş...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            //kullanıcıyı mainactivitye yönlendiriyoruz
        }
    }
}