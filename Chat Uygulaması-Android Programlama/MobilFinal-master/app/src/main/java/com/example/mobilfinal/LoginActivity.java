package com.example.mobilfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //Login Sayfası
    private EditText editTextUserName;
    private EditText editTextUserPassword;
    private Button buttonLogin;
    private TextView txtRegister;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userName;
    private String userPassword;
    //firebase nesnelerimizi oluşturduk

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextUserPassword = (EditText)findViewById(R.id.editTextUserPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        txtRegister = (TextView) findViewById(R.id.txtRegister);
        //oluşturduğumuz tüm nesneelri layouta koyduğumuz widgetlarla bağladık
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser(); // authenticate olan kullaniciyi aliyoruz eger var ise

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login butonuna tıklandığında

                userName = editTextUserName.getText().toString();//yazdığımız login emailini çekiyoruz gettext ile
                userPassword = editTextUserPassword.getText().toString();// yazdığımız passwordu çekiyoruz gettext ile
                if(userName.isEmpty() || userPassword.isEmpty()){// is empty komutuyla verilerin boş olup olmadığını kontrol ediyoruz
                //eğer boş ise
                    Toast.makeText(getApplicationContext(),"Lütfen gerekli alanları doldurunuz!",Toast.LENGTH_SHORT).show();
                    //toast mesajı ile kullanıcıyı uyarıyoruz

                }else{
                    //eğer boş değil ise
                    login();
                    //login fonksiyonunu çağırıyoruz

                }
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    private void login() {
        // mAuth nesnemiz signinwithemailpaswword ile böyle bir kullanıcı var mı yok mu kontrol ediyor hazır bir koddur
        mAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(LoginActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){// dönen sonuç basarılı ise
                            Intent i;
                                i = new Intent(LoginActivity.this, MainActivity.class);

                            startActivity(i);
                            finish();
                            //SellerHomeActivity sayfamıza yönlendirme yapıyoruz


                        }
                        else{
                            // hata mesajını ekrana vuran kod
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}