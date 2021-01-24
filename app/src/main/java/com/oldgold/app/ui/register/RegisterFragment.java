package com.oldgold.app.ui.register;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.oldgold.app.R;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    private EditText email, password;
    private TextView name, lastName;
    private Button btnSignUp;
    private FirebaseAuth firebaseAuth;
    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle saveInstanceState) {
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        textView = root.findViewById(R.id.register_header);
        registerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        setUp(root);
        return root;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUp(View root) {

        firebaseAuth = FirebaseAuth.getInstance();
        name = root.findViewById(R.id.name);
        lastName = root.findViewById(R.id.last_name);
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        btnSignUp = root.findViewById(R.id.register_button);
        btnSignUp.setOnClickListener(v -> {
            root.findViewById(R.id.register_progress_bar).setVisibility(View.VISIBLE);
            Map<TextView, String> datas = new HashMap<>();
            datas.put(name, name.getText().toString());
            datas.put(lastName , lastName.getText().toString());
            datas.put(email, email.getText().toString());
            datas.put(password, password.getText().toString());
            AtomicReference<Boolean> errors = new AtomicReference<>(false);
            datas.forEach((key, val) -> {
                if(val.isEmpty()) {
                    key.setError("field is mandatory");
                    key.requestFocus();
                    errors.set(true);
                }
            });
            if (errors.get()) {
                Toast.makeText(getActivity(), "Fields must not be empty", Toast.LENGTH_SHORT).show();
                root.findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
            }
            else {
                root.findViewById(R.id.register_progress_bar).setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(datas.get(email), datas.get(password)).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Navigation.findNavController(root).navigate(R.id.nav_login);
                        }else {
                            root.findViewById(R.id.register_progress_bar).setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Fields must not be empty", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
            }
        });

    }

}
