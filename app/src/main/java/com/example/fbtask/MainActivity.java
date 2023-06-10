package com.example.fbtask;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fbtask.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private FirebaseAuth firebaseAuth;
    private String inp_email;
    private String user_name;
    private FirebaseUser fUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        firebaseAuth= FirebaseAuth.getInstance();
        fUser=firebaseAuth.getCurrentUser();
        mainBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inp_email=mainBinding.etUserId.getText().toString();
                String inp_password=mainBinding.etPassword.getText().toString();
                if(inp_email.isEmpty()){
                    mainBinding.etUserId.setError(" Mandatory Field.");
                    mainBinding.etUserId.requestFocus();
                }
                else if(inp_password.isEmpty() || inp_password.length()<8 ){
                    mainBinding.etPassword.setError(" Mandatory ");
                    mainBinding.etPassword.requestFocus();
                }
                else {

                    firebaseAuth.signInWithEmailAndPassword(inp_email, inp_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                SharedPreferences preferences=getSharedPreferences("user_details",MODE_PRIVATE);
                                SharedPreferences.Editor editor= preferences.edit();
                                editor.putString("email",inp_email);

                                fetchingUsernameFormFirebasewithEmailId();
                                editor.putString("user_name", user_name);
                                editor.apply();
                                editor.commit();
                                Toast.makeText(MainActivity.this, " You are successfully Logged In. ", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(MainActivity.this, Dashboard.class);
                                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "User not Registered . Getting Exception :"+task.getException()+" .", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mainBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
                finish();
            }
        });
    }

    private void fetchingUsernameFormFirebasewithEmailId() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot usersSnapshot: snapshot.getChildren()){
                    String user_name_uploads=usersSnapshot.getKey();
                    for(DataSnapshot snapshot1:usersSnapshot.getChildren()){
                        if(snapshot1.getValue() == inp_email){
                            user_name= user_name_uploads;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, " Failed to get user name."+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled:  "+error.getMessage());
            }

        });
    }


}