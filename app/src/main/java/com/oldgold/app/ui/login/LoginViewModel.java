package com.oldgold.app.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> data;

    public LoginViewModel() {
        data = new MutableLiveData<>();
        data.setValue("Login");
    }

    public LiveData<String> getText() {
        return  data;
    }
}
