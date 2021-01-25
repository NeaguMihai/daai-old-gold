package com.oldgold.app.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.oldgold.app.MainActivity;
import com.oldgold.app.R;
import com.oldgold.app.domain.database.UserDAO;
import com.oldgold.app.ui.home.HomeFragment;
import com.oldgold.app.ui.home.HomeViewModel;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private UserDAO userDAO;
    private EditText email, password;
    private Button btnSignUp;
    private FragmentTransaction transaction;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle saveInstanceState) {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        final TextView textView = root.findViewById(R.id.login_header);

        transaction = this.getParentFragmentManager().beginTransaction();
        userDAO = new UserDAO(transaction);

        loginViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        if (!userDAO.isUserLogged()) {
            setUp(root, this);
        }
        else {
            transaction.replace(R.id.nav_host_fragment,HomeFragment.class, null).remove(this).commit();
        }
        return root;
    }

    public void setUp(View root, Fragment fragment) {

        email = root.findViewById(R.id.emailField);
        password = root.findViewById(R.id.password_field);
        btnSignUp = root.findViewById(R.id.login_button);

        btnSignUp.setOnClickListener(v -> {
            root.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            if (email.getText().toString().isEmpty()||password.getText().toString().isEmpty())
            {

                Toast.makeText(getActivity(), "Wrong email or password", Toast.LENGTH_SHORT).show();
                root.findViewById(R.id.progressBar).setVisibility(View.GONE);

            }
            else {
                userDAO.loginResult(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                userDAO.getUserData()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                ((MainActivity)getActivity()).updateDrawer(
                                                        documentSnapshot.get("name")+
                                                                " "
                                                                +documentSnapshot.get("lastName"),
                                                        documentSnapshot.get("email").toString());
                                                Toast.makeText(getActivity(), "Welcome, " + documentSnapshot.get("name") + "!", Toast.LENGTH_SHORT).show();

                                                transaction.replace(R.id.nav_host_fragment,HomeFragment.class, null).remove(fragment).commit();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(getActivity(), "There was a server error, please try again in a moment", Toast.LENGTH_SHORT).show();
                                                root.findViewById(R.id.progressBar).setVisibility(View.GONE);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Wrong email or password", Toast.LENGTH_SHORT).show();
                                root.findViewById(R.id.progressBar).setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

}
