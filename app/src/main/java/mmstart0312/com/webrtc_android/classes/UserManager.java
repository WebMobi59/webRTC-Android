package mmstart0312.com.webrtc_android.classes;

import android.app.Application;

public class UserManager extends Application {
    private String phone_number;
    private String first_name;
    private String last_name;
    private String street;
    private String apt;
    private String city;
    private String state;
    private String zip;

    public UserManager() {
        phone_number = "";
        first_name = "";
        last_name = "";
        street = "";
        apt = "";
        city = "";
        state = "";
        zip = "";
    }

    public String getUser_PhoneNumber() {
        return  phone_number;
    }

    public void setUser_PhoneNumber (String number) {
        this.phone_number = number;
    }
}
