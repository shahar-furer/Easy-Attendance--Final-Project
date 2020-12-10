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

public class FBAuth {
    FirebaseAuth auth;

    public FBAuth() {
        this.auth = FirebaseAuth.getInstance();
    }

    public void registerUserToDB(String orgKey, String keyID, String email, String fName, String lName, String password,  AppCompatActivity activity){
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
                        user.addUserToDB(orgKey, userID, email, fName, lName, password);
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
                            Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity.getApplicationContext(), "Email or password incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


//    public void resetPassword(String email, AppCompatActivity activity) {
//        final ProgressDialog progressDialog = new ProgressDialog(activity);
//        progressDialog.setMessage("verifying..");
//        progressDialog.show();
//        mAuth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            progressDialog.dismiss();
//                            Toast.makeText(activity.getApplicationContext(), "Reset password instructions has sent to your email",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            progressDialog.dismiss();
//                            Toast.makeText(activity.getApplicationContext(),
//                                    "Email don't exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(activity.getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}