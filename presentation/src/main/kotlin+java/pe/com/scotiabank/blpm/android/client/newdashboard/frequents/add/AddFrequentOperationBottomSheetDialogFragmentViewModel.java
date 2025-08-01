package pe.com.scotiabank.blpm.android.client.newdashboard.frequents.add;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import pe.com.scotiabank.blpm.android.client.R;

public class AddFrequentOperationBottomSheetDialogFragmentViewModel extends AndroidViewModel {

    private final MutableLiveData<String> description;
    private final MutableLiveData<Boolean> dismiss;
    private final MutableLiveData<Boolean> frequent;
    private final MutableLiveData<Boolean> sendNegativeAnalytics;
    private final MutableLiveData<Boolean> sendPositiveAnalytics;
    private final Application application;

    public AddFrequentOperationBottomSheetDialogFragmentViewModel(@NonNull Application application) {
        super(application);
        this.dismiss = new MutableLiveData<>();
        this.frequent = new MutableLiveData<>();
        this.description = new MutableLiveData<>();
        this.sendNegativeAnalytics = new MutableLiveData<>();
        this.sendPositiveAnalytics = new MutableLiveData<>();

        this.application = application;
    }

    public void setDescription(String name) {
        this.description.setValue(application.getBaseContext().getString(R.string.my_list_add_message, name));
    }

    public void setDismiss(Boolean dismiss) {
        this.dismiss.setValue(dismiss);
    }

    public void setFrequent(Boolean frequent) {
        this.frequent.setValue(frequent);
    }

    public void setSendNegativeAnalytics(Boolean send) {
        this.sendNegativeAnalytics.setValue(send);
    }

    public void setSendPositiveAnalytics(Boolean send) {
        this.sendPositiveAnalytics.setValue(send);
    }

    public LiveData<String> getDescription() {
        return description;
    }

    public LiveData<Boolean> getDismiss() {
        return dismiss;
    }

    public LiveData<Boolean> getFrequent() {
        return frequent;
    }

    public LiveData<Boolean> getSendNegativeAnalytics() {
        return sendNegativeAnalytics;
    }

    public LiveData<Boolean> getSendPositiveAnalytics() {
        return sendPositiveAnalytics;
    }

    public void onNegativeClick() {
        setFrequent(false);
        setDismiss(true);
        setSendNegativeAnalytics(true);
    }

    public void onPositiveClick() {
        setFrequent(true);
        setDismiss(true);
        setSendPositiveAnalytics(true);
    }

}