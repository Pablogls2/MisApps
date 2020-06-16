package com.example.rss.ui.acercaDe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AcercaDeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AcercaDeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is acercaDe fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}