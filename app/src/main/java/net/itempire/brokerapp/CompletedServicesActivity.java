package net.itempire.brokerapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompletedServicesActivity extends AppCompatActivity {


    RecyclerView pandingRecyclerView;
    List<AdapterGetterSetter> pendingRequestList;
    ProgressDialog loadingDialog;
    String url="";
    TextView no_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_services);

        no_request=(TextView)findViewById(R.id.text_no_pending_requests);
        pandingRecyclerView = (RecyclerView) findViewById(R.id.recycler_accepted_request);
        pandingRecyclerView.setHasFixedSize(true);
        pendingRequestList=new ArrayList<>();
        loadingDialog=new ProgressDialog(CompletedServicesActivity.this);
        loadingDialog.setTitle("Accessing Your Data");
        loadingDialog.setMessage("Loading....");
        loadingDialog.show();


        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url + "request_list_user", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("@#request_list_user", "onResponse: " + response);
                try {
                    JSONArray pendingRequest = new JSONArray(response);
                    Log.e("Eror", "success: " + pendingRequest.length());

                    if (pendingRequest.length() > 0) {
                        no_request.setVisibility(View.GONE);
                        //no_requset.setVisibility(View.GONE);
                        // HashMap<String, String> HashMapdata = new HashMap<String, String>();
                        for (int i = 0; i < pendingRequest.length(); i++) {
                            try {

                                AdapterGetterSetter adapterGetterSetter=new AdapterGetterSetter();
                                JSONObject objList = (JSONObject) pendingRequest.get(i);
                                Log.e("@#objList" + i, "onResponse:" + objList);
                                objList.getString("user_full_name");
                                objList.getString("service_name");
                                objList.getString("vehicle_name");
                                objList.getString("requested_date_time");

                                adapterGetterSetter.setId(i);
                                adapterGetterSetter.setNameServiceProvider(objList.getString("user_full_name"));
                                adapterGetterSetter.setServiceServiceProvider(objList.getString("service_name"));
                                adapterGetterSetter.setLocationServiceProvider(objList.getString("vehicle_name"));
                                adapterGetterSetter.setAmountServiceProvider(objList.getString("requested_date_time"));
                                adapterGetterSetter.setReject_data_time(objList.getString("accepted_date_time"));
                                pendingRequestList.add(adapterGetterSetter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setcustomAdapter(pendingRequestList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                //This code is executed if there is an error.
                Log.e("@#vollyResponseError", "onResponse:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                /*MyData.put("user_id",sharedPreferences.getString("user_id", ""));
                MyData.put("type","1");*/
                return MyData;
            }
        };
        RequestQueue MyRequestQueue = Volley.newRequestQueue(CompletedServicesActivity.this);
        MyRequestQueue.add(MyStringRequest);
    }
    public void setcustomAdapter(List<AdapterGetterSetter> dhistory){
        pandingRecyclerView.setLayoutManager(new LinearLayoutManager(CompletedServicesActivity.this));
        AdapterRecyclerCompleted pandingRequestsAdapter = new AdapterRecyclerCompleted(CompletedServicesActivity.this,dhistory);
        pandingRecyclerView.setAdapter(pandingRequestsAdapter);
    }
}
