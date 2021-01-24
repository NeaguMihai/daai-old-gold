package com.oldgold.app.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<String> data;

    public RegisterViewModel() {
        data = new MutableLiveData<>();
        data.setValue("Login view");
    }

    public LiveData<String> getText() {
        return  data;
    }
}
