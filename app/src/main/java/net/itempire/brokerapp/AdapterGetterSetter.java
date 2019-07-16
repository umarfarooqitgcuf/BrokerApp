package net.itempire.brokerapp;

public class AdapterGetterSetter
{
    private int id;
    private String  nameServiceProvider;
    private String locationServiceProvider;
    private String serviceServiceProvider;
    private String amountServiceProvider;
    private String reject_data_time;
    private String status;
    private String phone_number;
    private String paid_amount;


    public AdapterGetterSetter() {
        this.id=id;
        this.nameServiceProvider = nameServiceProvider;
        this.locationServiceProvider = locationServiceProvider;
        this.serviceServiceProvider = serviceServiceProvider;
        this.amountServiceProvider = amountServiceProvider;
        this.reject_data_time = reject_data_time;
        this.status = status;
        this.phone_number = phone_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(String paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameServiceProvider() {
        return nameServiceProvider;
    }
    public void setNameServiceProvider(String nameServiceProvider) {
        this.nameServiceProvider = nameServiceProvider;
    }

    public String getLocationServiceProvider() {
        return locationServiceProvider;
    }

    public void setLocationServiceProvider(String locationServiceProvider) {
        this.locationServiceProvider = locationServiceProvider;
    }

    public String getServiceServiceProvider() {
        return serviceServiceProvider;
    }

    public void setServiceServiceProvider(String serviceServiceProvider) {
        this.serviceServiceProvider = serviceServiceProvider;
    }

    public String getAmountServiceProvider() {
        return amountServiceProvider;
    }

    public void setAmountServiceProvider(String amountServiceProvider) {
        this.amountServiceProvider = amountServiceProvider;
    }

    public String getReject_data_time() {
        return reject_data_time;
    }
    public void setReject_data_time(String reject_data_time) {
        this.reject_data_time = reject_data_time;
    }

}