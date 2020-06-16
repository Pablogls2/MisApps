package com.example.rss.ui.rss;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RssViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RssViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is rss fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}