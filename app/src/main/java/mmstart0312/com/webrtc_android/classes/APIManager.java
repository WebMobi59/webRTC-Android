package mmstart0312.com.webrtc_android.classes;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIManager {

    public interface APISuccessListener{
        void onFailure(String error);
        void onSuccess(Response response);
    }

    private static APIManager ourInstance = new APIManager();

    public static APIManager getInstance() {
        return ourInstance;
    }

    private Context context;

    private static final int KEY_REQUEST_REGISTER = 381;
    private static final int KEY_REQUEST_CONFIRM = 382;
    private static final int KEY_REGISTER_DEVICETOKEN_WITHTENANT = 383;
    private static final int KEY_REGISTER_DEVICETOKEN_WITHPREQUALTENANT = 384;
    private static final int KEY_GET_USERINFO = 385;

    private static final String URL_BASE = "http://ec2-52-24-49-20.us-west-2.compute.amazonaws.com:2017/";
    private static final String URL_LOGIN_PHONE = "tenant-authorization"; // type:POST login with phone number
    private static final String URL_ConfirmCode = "tenant-authorization/"; // type: PUT confirm mobile number with activation code
    private static final String URL_Register_DeviceToken_WithTenant = "tenants/"; //type: PUT register the device token if user is in tenant
    private static final String URL_Register_DeviceToekn_WithPrequalTenant = "prequal-tenants/"; //type: PUT register the device token if user is not in tenant
    private static final String URL_Get_User_Information_WithTenant = "tenant-location?phone="; //type: GET
    private static final String URL_Get_User_Information_WithPrequalTenant = "prequal-tenants/"; //type: GET
    private static final String URL_LOCATION = "location/";             // type:GET
    private static final String URL_DEFAULT_ITEMS = "defaultMasksOrEffects/";                   // type:GET

    private OkHttpClient client;

    public APIManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MINUTES)
                .readTimeout(1000, TimeUnit.MINUTES)
                .writeTimeout(1000, TimeUnit.MINUTES)
                .build();
    }

    public void loginPhone(Editable phone, final APISuccessListener listener){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", phone);
            this.requestParam(jsonObject, URL_BASE+URL_LOGIN_PHONE, KEY_REQUEST_REGISTER, listener);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void confirmCode(String phone, String activationCode, final APISuccessListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("activationCode", activationCode);
            String url = URL_BASE + URL_ConfirmCode + phone;
            this.putRequestAPICall(jsonObject, url, KEY_REQUEST_CONFIRM, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void registerDeviceToken(String phone, String deviceToken, boolean flag, final APISuccessListener listener) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceToken", deviceToken);
            String url = URL_BASE;
            if (flag) {
                url += URL_Register_DeviceToken_WithTenant;
            } else {
                url += URL_Register_DeviceToekn_WithPrequalTenant;
            }

            url += phone;

            if (flag) {
                this.putRequestAPICall(jsonObject, url, KEY_REGISTER_DEVICETOKEN_WITHTENANT, listener);
            } else {
                this.putRequestAPICall(jsonObject, url, KEY_REGISTER_DEVICETOKEN_WITHPREQUALTENANT, listener);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo(String phone, boolean flag, final APISuccessListener listener){
        String url = URL_BASE;
        url += (flag)?URL_Get_User_Information_WithTenant:URL_Get_User_Information_WithPrequalTenant;
        url += phone;
        this.getRequestAPICall(url, KEY_GET_USERINFO, listener);
    }

    //PUT
    public void putRequestAPICall(JSONObject params, String url, final int key, final APISuccessListener listener) {
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("content-type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                switch (key) {
                    case KEY_REQUEST_CONFIRM:
                    {
                        try {
                            listener.onSuccess(response);
                        } catch (Exception e){
                            e.printStackTrace();
                            listener.onFailure(e.getLocalizedMessage());
                        }
                    }
                    case KEY_REGISTER_DEVICETOKEN_WITHTENANT:
                    {
                        try {
                            listener.onSuccess(response);
                        } catch (Exception e){
                            e.printStackTrace();
                            listener.onFailure(e.getLocalizedMessage());
                        }
                    }
                    case KEY_REGISTER_DEVICETOKEN_WITHPREQUALTENANT:
                    {
                        try {
                            listener.onSuccess(response);
                        } catch (Exception e){
                            e.printStackTrace();
                            listener.onFailure(e.getLocalizedMessage());
                        }
                    }
                    default:
                        break;
                }
            }
        });
    }

    //POST
    public void requestParam(JSONObject params, String url, final int key, final APISuccessListener listener){
        try {
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, params.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFailure(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    switch (key){
                        case KEY_REQUEST_REGISTER:
                        {
                            try {
                                listener.onSuccess(response);
                            }catch(Exception e){
                                e.printStackTrace();
                                listener.onFailure("API is not working properly.");
                            }
                        }
                        default:
                        break;
                    }
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //GET
    public void getRequestAPICall(String url, final int key, final APISuccessListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onSuccess(response);
            }
        });
    }

    public void getAddress(JSONArray location, final APISuccessListener listener){
        try {
            int lat = location.getInt(0);
            int lng = location.getInt(1);
            String url = URL_BASE+URL_LOCATION+lat+","+lng;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFailure(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccess(response);
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getDefaultItems(String type, final APISuccessListener listener){
        String url = URL_BASE+URL_DEFAULT_ITEMS+type;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onSuccess(response);
            }
        });
    }

     public void setContext(Context c){
        this.context = c;
    }

     public Context getContext(){
        return this.context;
    }
}
