package com.oldgold.app.ui.register;

import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<String> data;


    public RegisterViewModel() {
        data = new MutableLiveData<>();
        data.setValue("Register");
    }

    public LiveData<String> getText() {
        return  data;
    }

}
