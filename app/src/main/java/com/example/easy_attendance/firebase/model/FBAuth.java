package com.example.easy_attendance.firebase.model;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easy_attendance.DailyReport;
import com.example.easy_attendance.LoginPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import static android.content.ContentValues.TAG;

public class FBAuth {
    FirebaseAuth mAuth;
    public String userID;
    FirebaseDBUser user;

    public FBAuth() {
        this.mAuth = FirebaseAuth.getInstance();
        user  = new FirebaseDBUser();
    }

    public void registerUserToDB(String orgKey, String ID , String fName, String lName, String email, String password, Boolean isManager,  AppCompatActivity activity){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //TASK SUCCESSFUL
                    Toast.makeText(activity.getApplicationContext(), "Success create new account .",
                            Toast.LENGTH_SHORT).show();
                    String userID = getUserID();
                    user.addUserToDB(userID, orgKey, ID, fName, lName, email ,isManager);
                    Intent intent = new Intent(activity, LoginPage.class);
                    activity.startActivity(intent);
                } else {
                    //TASK ERROR
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(activity.getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    public void validationUser(String email, String password, AppCompatActivity activity) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "signInWithEmail:success");
                            userID = mAuth.getCurrentUser().getUid();
                            Intent loginIntent = new Intent(activity, DailyReport.class);
                            loginIntent.putExtra("userId" , userID);
                            activity.startActivity(loginIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithemail:failure", task.getException());
                            Toast.makeText(activity.getApplicationContext(), "Email or password incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void signOut(){
        mAuth.signOut();
    }

    public String getUserID() {
        String uid = mAuth.getCurrentUser().getUid();

        return uid;

    }

    public String getUserName() {
        String Name = mAuth.getCurrentUser().getDisplayName();

        return Name;

    }

    public void resetPassword(String emailAddress){

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Email sent.");
                        }
                    }
                });

    }

    public boolean isConnected(){
        return mAuth.getCurrentUser() != null;
    }



}