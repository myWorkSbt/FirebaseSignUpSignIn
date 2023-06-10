package com.example.fbtask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fbtask.databinding.ActivityDashboardBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;

public class Dashboard extends AppCompatActivity {

    private ActivityDashboardBinding dashboardBinding;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private StorageReference uploader;
    public Uri filePath;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(dashboardBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        dashboardBinding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Toast.makeText(Dashboard.this, " Successfully Signed Out. ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Dashboard.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        dialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        dashboardBinding.btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Dashboard.this, " New Post Clicked   ", Toast.LENGTH_SHORT).show();
                Dexter.withActivity(Dashboard.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                dashboardBinding.showImagOuterInterface.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "selectimage"), 1);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });
        dashboardBinding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadToCloudFirebase();
            }
        });

        dashboardBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dashboardBinding.showImagOuterInterface.setVisibility(View.GONE);
            }
        });
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("posts");
        FirebaseRecyclerOptions<postsdetail> options=
                 new FirebaseRecyclerOptions.Builder<postsdetail>()
                         .setQuery(databaseReference, postsdetail.class)
                                 .build();
        dashboardBinding.allItemLays.setLayoutManager(new LinearLayoutManager(this));
        FirebaseBlogListAdapter listAdapter= new FirebaseBlogListAdapter(options);
        dashboardBinding.allItemLays.setAdapter(listAdapter);
        //   Getting data from Firebase



    }

    private void uploadToCloudFirebase() {


        FirebaseStorage storage = FirebaseStorage.getInstance();
        String blog_image = "image_" + SystemClock.currentThreadTimeMillis();
        uploader = storage.getReference().child("image").child(blog_image);
        uploader.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Toast.makeText(Dashboard.this, " Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                        uploadBlogDataToFirebase();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        dialog.setTitle(" File Uploader ");
                        dialog.setMessage(" Uploading " + (int) percent + "%");
                        dialog.show();
                    }
                });
        dashboardBinding.showImagOuterInterface.setVisibility(View.GONE);

    }

    private void uploadBlogDataToFirebase() {
        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
//
                postsdetail postsdetail = new postsdetail("" + sharedPreferences.getString("user_name", "user_01"),uri, 0);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fbtask-5838c-default-rtdb.firebaseio.com/");
                reference.child("blogs").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long nofAvailableBlog = Long.parseLong(0+"");
                        String user_name = sharedPreferences.getString("user_name", "user_01");
                        for(DataSnapshot blog_users:snapshot.getChildren())
                        {
                            if(blog_users.getKey()== user_name){
                                nofAvailableBlog =blog_users.getChildrenCount();
                                databaseReference.child(user_name).child(nofAvailableBlog+"").setValue(postsdetail);
                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText( Dashboard.this, " Getting some error in Registration .  "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                reference.child(sharedPreferences.getString("user_name", "user_01")).setValue(postsdetail);
                Toast.makeText(Dashboard.this, " Value Inserted.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            filePath = data.getData();
            try{
                InputStream inputStream= getContentResolver().openInputStream(filePath);
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                dashboardBinding.showImg.setImageBitmap(bitmap);
            }
            catch (Exception ex){
                Toast.makeText(this, " "+ ex.getMessage(), Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}