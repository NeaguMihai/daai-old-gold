package com.oldgold.app.domain.database;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.oldgold.app.R;
import com.oldgold.app.ui.home.HomeFragment;
import com.oldgold.app.ui.register.RegisterFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDAO {

    private final FirebaseFirestore db;
    private final FirebaseAuth fa;
    private final FragmentTransaction transaction;


    public UserDAO(FragmentTransaction transaction) {
        this.db = Database.getInstance().getConnection();
        this.fa = FirebaseAuth.getInstance();
        this.transaction = transaction;
    }

    public void registerRequest(Map<String, String> datas,View root, Fragment fragment) {

        fa.createUserWithEmailAndPassword(datas.get("email"), datas.get("password")).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginResult(datas.get("email"), datas.get("password"))
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    FirebaseUser myUser = fa.getCurrentUser();
                                    datas.remove("password");
                                    db
                                            .collection("users")
                                            .document(myUser.getUid())
                                            .set(datas).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            transaction.replace(R.id.nav_host_fragment, HomeFragment.class, null).remove(fragment).commit();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    root.findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
                                                    Toast.makeText(fragment.getActivity(), "Error while registering, try again", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    root.findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
                                    Toast.makeText(fragment.getActivity(), "Error while registering, try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    root.findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
                    Toast.makeText(fragment.getActivity(), "Fields must not be empty", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    //TODO il vom folosi noi candva
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateRequest(Map<String, String> datas, View root, Integer location, Fragment fragment, String uid) {

        Map<String, Object> wrap = datas.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        datas.remove("password");
        db
                .collection("users")
                .document(uid)
                .update(wrap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Navigation.findNavController(root).navigate(location);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        root.findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
                        Toast.makeText(fragment.getActivity(), "Cannot complete the registration", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public Task<AuthResult> loginResult(String email, String password) {

        return fa.signInWithEmailAndPassword(email, password);
    }

    public Task<DocumentSnapshot> getUserData() {
        FirebaseUser myUser = fa.getCurrentUser();
        return db
                .collection("users")
                .document(myUser.getUid())
                .get();
    }

    public Boolean isUserLogged(){

        return fa.getCurrentUser() != null;
    }
}
