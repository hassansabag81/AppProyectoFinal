package com.example.appproyectofinal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> text;

    public HomeViewModel() {
        text = new MutableLiveData<>();
        text.setValue("Texto inicial del ViewModel");
    }

    public LiveData<String> getText() {
        return text;
    }
}