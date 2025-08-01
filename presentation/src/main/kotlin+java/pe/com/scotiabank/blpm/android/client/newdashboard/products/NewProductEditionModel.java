package pe.com.scotiabank.blpm.android.client.newdashboard.products;

public class NewProductEditionModel {
    private boolean edit;
    private boolean allMyAccount;
    private boolean hideAmount;

    private boolean checked;

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isAllMyAccount() {
        return allMyAccount;
    }

    public void setAllMyAccount(boolean allMyAccount) {
        this.allMyAccount = allMyAccount;
    }

    public boolean isHideAmount() {
        return hideAmount;
    }

    public void setHideAmount(boolean hideAmount) {
        this.hideAmount = hideAmount;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}