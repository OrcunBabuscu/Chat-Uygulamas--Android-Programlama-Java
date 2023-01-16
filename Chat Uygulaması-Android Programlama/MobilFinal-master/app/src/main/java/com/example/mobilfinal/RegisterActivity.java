package com.example.mobilfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    //kayıt olma sayfası
    EditText editTextUserName,editTextUserPassword;
    Button  buttonRegister;
    FirebaseAuth mAuth;
    TextView txtRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextUserPassword = (EditText)findViewById(R.id.editTextUserPassword);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);
        txtRegister=(TextView)findViewById(R.id.txtRegister);
        //oluşturduğumuz tüm nesneelri layouta koyduğumuz widgetlarla bağladık


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//kayıt ol butonuna tıklandığında
                String email = editTextUserName.getText().toString();
                String password = editTextUserPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()) {
                    //is empty komutuyla verilerin boş olup olmadığını kontrol ediyoruz
                    Toast.makeText(RegisterActivity.this, "Lütfen boş alan bırakmayınız", Toast.LENGTH_SHORT).show();
                    //eğer boş ise toast mesajı ile kullanıcıyı uyarıyoruz
                }
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        //eğer başarılı ise
                        Toast.makeText(RegisterActivity.this, "Başarılı", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        //kullanıcıyı mainactivitye yönlendiriyoruz
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Başarısız ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

    }
}