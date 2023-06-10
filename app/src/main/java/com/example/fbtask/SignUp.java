package com.example.fbtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fbtask.databinding.ActivitySignUpBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    public ActivitySignUpBinding signUpBinding;
    private FirebaseAuth firebaseAuth;
    public DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fbtask-5838c-default-rtdb.firebaseio.com/");
        signUpBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signUpBinding.etName.getText().toString();
                String user_email = signUpBinding.etUserId.getText().toString();
                String password = signUpBinding.etPassword.getText().toString();
                if (name.isEmpty()) {
                    signUpBinding.etName.setError(" Mandatory");
                    signUpBinding.etName.requestFocus();
                } else if (user_email.isEmpty()) {
                    signUpBinding.etUserId.requestFocus();
                    signUpBinding.etUserId.setError("Mandatory");
                } else if (password.length() < 8) {
                    signUpBinding.etPassword.requestFocus();
                    signUpBinding.etPassword.setError("Mandatory! min 8 characters allowed ");
                } else if (!signUpBinding.etConfirmPassword.getText().toString().equals(password)) {
                    signUpBinding.etConfirmPassword.requestFocus();
                    signUpBinding.etConfirmPassword.setError("Password! Mismatched ");
                } else {

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(name)) {
                                Toast.makeText(SignUp.this, "", Toast.LENGTH_SHORT).show();
                            } else {
                                databaseReference.child("users").child(name).child("user_name").setValue(name);
                                databaseReference.child("users").child(name).child("user_email").setValue(user_email);
                                databaseReference.child("users").child(name).child("password").setValue(password);

                                firebaseAuth.createUserWithEmailAndPassword(user_email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(SignUp.this, " User Registered Successfully . ", Toast.LENGTH_SHORT).show();
                                        SharedPreferences preferences=getSharedPreferences("user_details", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor= preferences.edit();
                                        editor.putString("user_name", name);
                                        editor.putString("email", user_email);
                                        editor.apply();
                                        editor.commit();
                                        Intent intent = new Intent(SignUp.this, Dashboard.class);
                                        intent.putExtra("user_name", name);
//                                       intent.putExtra("")
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SignUp.this, " Getting some error in Registration .  "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        signUpBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
                finish();
            }
        });

    }
}