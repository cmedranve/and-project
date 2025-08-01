package pe.com.scotiabank.blpm.android.client.newdashboard.products;

import android.os.Parcel;
import android.os.Parcelable;

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardColor;

public class NewProductModel implements Parcelable {
    private long id;
    private String cardId;
    private String name;
    private String amount;
    private int amountColor;
    private int defaultColor;
    private boolean amountHidden;
    private boolean principal;

    private boolean firstTime;
    private boolean amountLoaded;

    private String group;

    private String productType;
    private String subProductType;
    private String masked;
    private String customerProductNumber;

    private boolean clickable;
    private boolean flagWarning;
    private String currencyPlusAmount;
    private boolean isInactive;
    private String color;
    private String statusProductType;
    private String expirationDateDescription;
    private AtmCardColor cardColor;
    private boolean isAvailable;

    public NewProductModel() {
        //Do nothing
    }

    private NewProductModel(Parcel in) {
        id = in.readLong();
        cardId = in.readString();
        name = in.readString();
        amount = in.readString();
        amountColor = in.readInt();
        defaultColor = in.readInt();
        amountHidden = in.readByte() != 0;
        principal = in.readByte() != 0;
        firstTime = in.readByte() != 0;
        amountLoaded = in.readByte() != 0;
        group = in.readString();
        productType = in.readString();
        subProductType = in.readString();
        masked = in.readString();
        customerProductNumber = in.readString();
        clickable = in.readByte() != 0;
        flagWarning = in.readByte() != 0;
        currencyPlusAmount = in.readString();
        isInactive = in.readByte() != 0;
        color = in.readString();
        statusProductType = in.readString();
        expirationDateDescription = in.readString();
        cardColor = AtmCardColor.getCardColorByName(in.readString());
        isAvailable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(cardId);
        dest.writeString(name);
        dest.writeString(amount);
        dest.writeInt(amountColor);
        dest.writeInt(defaultColor);
        dest.writeByte((byte) (amountHidden ? 1 : 0));
        dest.writeByte((byte) (principal ? 1 : 0));
        dest.writeByte((byte) (firstTime ? 1 : 0));
        dest.writeByte((byte) (amountLoaded ? 1 : 0));
        dest.writeString(group);
        dest.writeString(productType);
        dest.writeString(subProductType);
        dest.writeString(masked);
        dest.writeString(customerProductNumber);
        dest.writeByte((byte) (clickable ? 1 : 0));
        dest.writeByte((byte) (flagWarning ? 1 : 0));
        dest.writeString(currencyPlusAmount);
        dest.writeByte((byte) (isInactive ? 1 : 0));
        dest.writeString(color);
        dest.writeString(statusProductType);
        dest.writeString(expirationDateDescription);
        dest.writeString(cardColor == null ? AtmCardColor.RED.getNameFromNetworkCall() : cardColor.getNameFromNetworkCall());
        dest.writeByte((byte) (isAvailable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewProductModel> CREATOR = new Creator<NewProductModel>() {
        @Override
        public NewProductModel createFromParcel(Parcel in) {
            return new NewProductModel(in);
        }

        @Override
        public NewProductModel[] newArray(int size) {
            return new NewProductModel[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getAmountColor() {
        return amountColor;
    }

    public void setAmountColor(int amountColor) {
        this.amountColor = amountColor;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public boolean isAmountHidden() {
        return amountHidden;
    }

    public void setAmountHidden(boolean amountHidden) {
        this.amountHidden = amountHidden;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public boolean isAmountLoaded() {
        return amountLoaded;
    }

    public void setAmountLoaded(boolean amountLoaded) {
        this.amountLoaded = amountLoaded;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSubProductType() {
        return subProductType;
    }

    public void setSubProductType(String subProductType) {
        this.subProductType = subProductType;
    }

    public String getMasked() {
        return masked;
    }

    public void setMasked(String masked) {
        this.masked = masked;
    }

    public String getCustomerProductNumber() {
        return customerProductNumber;
    }

    public void setCustomerProductNumber(String customerProductNumber) {
        this.customerProductNumber = customerProductNumber;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isFlagWarning() {
        return flagWarning;
    }

    public void setFlagWarning(boolean flagWarning) {
        this.flagWarning = flagWarning;
    }

    public String getCurrencyPlusAmount() {
        return currencyPlusAmount;
    }

    public void setCurrencyPlusAmount(String currencyPlusAmount) {
        this.currencyPlusAmount = currencyPlusAmount;
    }

    public boolean isInactive() {
        return isInactive;
    }

    public void setInactive(boolean inactive) {
        isInactive = inactive;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStatusProductType() {
        return statusProductType;
    }

    public void setStatusProductType(String statusProductType) {
        this.statusProductType = statusProductType;
    }

    public String getExpirationDateDescription() {
        return expirationDateDescription;
    }

    public void setExpirationDateDescription(String expirationDateDescription) {
        this.expirationDateDescription = expirationDateDescription;
    }

    public AtmCardColor getCardColor() {
        return cardColor;
    }

    public void setCardColor(AtmCardColor cardColor) {
        this.cardColor = cardColor;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}