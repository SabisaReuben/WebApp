package co.ciao.bluclub.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class LauncherInputs extends BaseObservable {
    private String countryUrl;
    private String country;

    private String emailAddress;

    public LauncherInputs() {
        this.country = this.countryUrl = this.emailAddress = "";
    }

    @Bindable
    public String getCountry() {
        return country;
    }

    @Bindable
    public String getCountryUrl() {
        return countryUrl;
    }

    @Bindable
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setCountryUrl(String countryUrl) {
        this.countryUrl = countryUrl;
        notifyPropertyChanged(BR.countryUrl);

    }

    public void setCountry(String country) {
        this.country = country;
        notifyPropertyChanged(BR.country);
    }

    public void setEmailAddress(String emailAddress) {
        if (!this.emailAddress.equals(emailAddress)) {
            this.emailAddress = emailAddress;
            notifyPropertyChanged(BR.emailAddress);
        }
    }
}
