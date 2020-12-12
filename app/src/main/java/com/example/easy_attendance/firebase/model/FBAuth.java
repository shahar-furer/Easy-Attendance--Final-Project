package com.example.easy_attendance.firebase.model;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easy_attendance.HomePage;
import com.example.easy_attendance.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class FBAuth {
    FirebaseAuth auth;

    public FBAuth() {
        this.auth = FirebaseAuth.getInstance();
    }

    public void registerUserToDB(String orgKey, String keyID, String email, String fName, String lName, String password, Boolean isManager,  AppCompatActivity activity){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(activity, new OnSuccessListener<AuthResult>(){

                    @Override
                    public void onSuccess(@NonNull AuthResult authResult) {
                        Log.d("" + activity, "createUserWithEmail:success");
                        Toast.makeText(activity, "Authentication completed.",
                                Toast.LENGTH_SHORT).show();
                        FirebaseDBUser user = new FirebaseDBUser();
                        FirebaseDBTable table = new FirebaseDBTable();
                        String userID = auth.getCurrentUser().getUid();
                        user.addUserToDB(orgKey, userID, email, fName, lName, password ,isManager);
                        Intent loginIntent=new Intent(activity, MainActivity.class);
                        activity.startActivity(loginIntent);
                    }

                });

    }


    public void validationUser(String email, String password, AppCompatActivity activity) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "signInWithEmail:success");
                            Intent loginIntent = new Intent(activity, HomePage.class);
                            activity.startActivity(loginIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithemail:failure", task.getException());
                            Toast.makeText(activity.getApplicationContext(), "Email or password incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}