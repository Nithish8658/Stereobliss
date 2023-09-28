/* */

package com.groza.Stereobliss.utils;

import android.os.AsyncTask;

import androidx.core.util.Pair;

import com.groza.Stereobliss.models.GenericModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FilterTask<T extends GenericModel> extends AsyncTask<String, Void, Pair<List<T>, String>> {

    public interface SuccessCallback<T> {
        void onSuccess(final Pair<List<T>, String> result);
    }

    public interface FailureCallback {
        void onFailure();
    }

    public interface Filter<T> {
        boolean matchesFilter(final T elem, final String filterString);
    }

    private final Filter<T> mFilter;

    private final SuccessCallback<T> mSuccessCallback;

    private final FailureCallback mFailureCallback;

    private final WeakReference<List<T>> mModelDataRef;

    private final ReentrantReadWriteLock.ReadLock mReadLock;

    public FilterTask(final List<T> modelData, final ReentrantReadWriteLock.ReadLock readLock, final Filter<T> filter, final SuccessCallback<T> successCallback, final FailureCallback failureCallback) {
        mModelDataRef = new WeakReference<>(modelData);
        mFilter = filter;
        mSuccessCallback = successCallback;
        mFailureCallback = failureCallback;
        mReadLock = readLock;
    }

    @Override
    protected Pair<List<T>, String> doInBackground(String... lists) {
        List<T> resultList = new ArrayList<>();

        String filterString = lists[0];
        mReadLock.lock();
        for (T elem : mModelDataRef.get()) {
            // Check if task was cancelled from the outside.
            if (isCancelled()) {
                resultList.clear();
                mReadLock.unlock();
                return new Pair<>(resultList, filterString);
            }
            if (mFilter.matchesFilter(elem, filterString)) {
                resultList.add(elem);
            }
        }

        mReadLock.unlock();
        return new Pair<>(resultList, filterString);
    }

    public final AsyncTask<String, Void, Pair<List<T>, String>> execute(String filterString) {
        return super.execute(filterString);
    }

    @Override
    protected void onPostExecute(Pair<List<T>, String> result) {
        if (!isCancelled()) {
            mSuccessCallback.onSuccess(result);
        } else {
            mFailureCallback.onFailure();
        }
    }

    @Override
    protected void onCancelled() {
        mFailureCallback.onFailure();
    }
}
