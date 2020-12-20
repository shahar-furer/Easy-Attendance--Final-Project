package com.example.easy_attendance.firebase.model;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easy_attendance.HomePage;
import com.example.easy_attendance.MainActivity;
import com.example.easy_attendance.RegistrationPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

public class FBAuth {
    FirebaseAuth mAuth;
    public String userID;

    public FBAuth() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void registerUserToDB(String orgKey, String keyID , String fName, String lName, String email, String password, Boolean isManager,  AppCompatActivity activity){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //TASK SUCCESSFUL
                    Toast.makeText(activity, "Success create new account .",
                            Toast.LENGTH_SHORT).show();
                    String userID = getUserID();
                    FirebaseDBUser user = new FirebaseDBUser();
                    user.addUserToDB(userID,orgKey, userID, email, fName, lName, password ,isManager);
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                } else {
                    //TASK ERROR
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(activity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnSuccessListener(activity, authResult -> {
//                    Log.d("" + activity, "createUserWithEmail:success");
//                    Toast.makeText(activity, "Success create new account .",
//                            Toast.LENGTH_SHORT).show();
//                    String userID = mAuth.getCurrentUser().getUid();
//                    FirebaseDBUser user = new FirebaseDBUser();
//                    user.addUserToDB(orgKey, userID, email, fName, lName, password ,isManager);
//                    Intent loginIntent=new Intent(activity, MainActivity.class);
//                    activity.startActivity(loginIntent);
//                });

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
                            activity.finish();
                            Intent loginIntent = new Intent(activity, HomePage.class);
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

}