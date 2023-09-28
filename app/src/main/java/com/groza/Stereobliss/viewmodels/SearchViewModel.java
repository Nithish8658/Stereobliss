/*  */

package com.groza.Stereobliss.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {

    private final MutableLiveData<String> mSearchString;

    public SearchViewModel() {
        mSearchString = new MutableLiveData<>();
    }

    public LiveData<String> getSearchString() {
        return mSearchString;
    }

    public void setSearchString(final String searchString) {
        mSearchString.setValue(searchString);
    }

    public void clearSearchString() {
        mSearchString.setValue(null);
    }
}
