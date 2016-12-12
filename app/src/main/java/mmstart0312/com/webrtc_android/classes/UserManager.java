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

    public String getUser_Firstname() {
        return  first_name;
    }

    public void setUser_Firstname (String fname) {
        this.first_name = fname;
    }

    public String getUser_Lastname() {
        return  last_name;
    }

    public void setUser_Lastname (String lname) {
        this.last_name = lname;
    }

    public String getUser_Street() {
        return  street;
    }

    public void setUser_Street (String st) {
        this.street = st;
    }

    public String getUser_Apt() {
        return  apt;
    }

    public void setUser_Apt (String user_apt) {
        this.apt = user_apt;
    }

    public String getUser_City() {
        return  city;
    }

    public void setUser_City (String user_city) {
        this.city = user_city;
    }

    public String getUser_State() {
        return  state;
    }

    public void setUser_State (String user_state) {
        this.street = user_state;
    }

    public String getUser_Zip() {
        return zip;
    }

    public void setUser_Zip(String user_zip) {
        this.zip = user_zip;
    }
}
