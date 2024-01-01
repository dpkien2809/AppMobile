package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileproject.api.CustomRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Login extends AppCompatActivity {
    Spinner spinner;
    public static final String[] languages = {"Choose Language", "English", "Vietnamese"};
    EditText username_edt;
    EditText password_edt;
    Button login_btn,back_btn;
    WebView login_webview,dashboard_webview;
    TextView forgot_password_textview, signup_textview;
    Handler ui_handle = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username_edt = findViewById(R.id.edt_username);
        password_edt = findViewById(R.id.edt_password);
        back_btn = findViewById(R.id.btn_back);
        login_webview = findViewById(R.id.webview_login);
        username_edt.setText("user");
        password_edt.setText("123");
        forgot_password_textview = findViewById(R.id.txtview_forgot_password);
        signup_textview = findViewById(R.id.txtview_sign_up);
        login_webview.getSettings().setJavaScriptEnabled(true);
        login_webview.getSettings().setLoadsImagesAutomatically(true);
        login_webview.getSettings().setAllowContentAccess(true);

        login_webview.getSettings().setUseWideViewPort(true);
        login_webview.getSettings().setLoadWithOverviewMode(true);
        login_webview.getSettings().setDomStorageEnabled(true);
        login_webview.setHorizontalScrollBarEnabled(false);
        login_webview.getSettings().setDatabaseEnabled(true);
        login_webview.setVerticalScrollBarEnabled(false);
        login_webview.getSettings().setBuiltInZoomControls(true);
        login_webview.getSettings().setDisplayZoomControls(false);
        login_webview.getSettings().setAllowFileAccess(true);
        login_webview.setScrollbarFadingEnabled(false);
        login_webview.setWebViewClient(new WebViewClient());
        login_webview.setInitialScale(1);

        forgot_password_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgot_password = new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(forgot_password);
            }
        });

        signup_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(getApplicationContext(), Register.class);
                startActivity(signup);
            }
        });






        login_webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("webview","shouldOverrideUrlLoading called");
                Log.d("webview",request.getUrl().toString());
                view.loadUrl(request.getUrl().toString());
                return false;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("webview","onPageFinished called");
                Log.d("webview",url);

                if(url.contains("auth?client_id")){
                    String auto_fill_placeholder = "%s.value=\"%s\";";
                    view.evaluateJavascript(
                            "let username = document.getElementById(\"username\"); "+
                            "let password = document.getElementById(\"password\"); "+
                            String.format(auto_fill_placeholder,"username",username_edt.getText().toString())+
                            String.format(auto_fill_placeholder,"password",password_edt.getText().toString())+
                            "let elements = document.getElementsByTagName(\"*\"); "+
                            "elements[31].click() ;"

                    ,null);
                }
                else if(url.contains("authenticate?session_code")){
                    Toast.makeText(getApplicationContext(), R.string.notice1,Toast.LENGTH_SHORT).show();
                    Intent dashboard = new Intent(getApplicationContext(),Dashboard.class);
                    dashboard.putExtra("username",username_edt.getText().toString());
                    dashboard.putExtra("password",password_edt.getText().toString());
                    startActivity(dashboard);



                    login_webview.getSettings().setJavaScriptEnabled(false);

                }else if(url.contains("authenticate?execution")){
                    Toast.makeText(getApplicationContext(), R.string.notice2,Toast.LENGTH_SHORT).show();
                    login_webview.getSettings().setJavaScriptEnabled(false);
                }

            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        login_btn = findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username_edt.getText().toString().isEmpty() || password_edt.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.notice3,Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Map<String,String> parameter = new HashMap<>();
                    parameter.put("client_id","openremote");
                    parameter.put("username",username_edt.getText().toString());
                    parameter.put("password",password_edt.getText().toString());
                    parameter.put("grant_type","password");
                    Thread login_Thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CustomRequest request = new CustomRequest(
                                        "https://uiot.ixxc.dev/auth/realms/master/protocol/openid-connect/token",
                                        "POST",
                                        null,
                                        parameter
                                );
                                Map<String,String> response = request.sendRequest();
                                String token = response.get("access_token");
                                Intent dashboard = new Intent(getApplicationContext(),Dashboard.class);
                                if (token==null){
                                    ui_handle.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                                dashboard.putExtra("access_token",token);
                                startActivity(dashboard);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });
                    login_Thread.start();


                }
            }
        });
        spinner = findViewById(R.id.spinner_attribute);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if(selectedLang.equals("English")){
                    setLocal(Login.this, "en");
                    finish();
                    startActivity(getIntent());
                }else if(selectedLang.equals("Vietnamese")){
                    setLocal(Login.this, "hi");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
    }
}