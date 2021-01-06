package co.ciao.bluclub.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LauncherViewModel extends ViewModel {
    private MutableLiveData<LauncherInputs> mutableLiveData = new MutableLiveData<>();

    private final LauncherInputs inputs = new LauncherInputs();

    public MutableLiveData<LauncherInputs> getMutableLiveData() {
        mutableLiveData.postValue(inputs);
        return mutableLiveData;
    }

    public void addCountryAndUrl(String country, String countryUrl) {
        inputs.setCountry(country);
        inputs.setCountryUrl(countryUrl);
    }

    public void setEmail(String email) {
        inputs.setEmailAddress(email);
    }
}
