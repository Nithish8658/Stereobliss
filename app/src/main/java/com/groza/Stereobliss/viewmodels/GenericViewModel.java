/* */

package com.groza.Stereobliss.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.groza.Stereobliss.models.GenericModel;

import java.util.List;

public abstract class GenericViewModel<T extends GenericModel> extends AndroidViewModel {

    private final MutableLiveData<List<T>> mData;

    abstract void loadData();

    GenericViewModel(@NonNull final Application application) {
        super(application);

        mData = new MutableLiveData<>();
    }

    public LiveData<List<T>> getData() {
        return mData;
    }

    public void reloadData() {
        loadData();
    }

    public void clearData() {
        mData.setValue(null);
    }

    protected void setData(final List<T> data) {
        mData.setValue(data);
    }
}
