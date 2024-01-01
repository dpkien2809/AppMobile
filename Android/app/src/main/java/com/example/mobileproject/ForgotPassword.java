package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
    Button search_btn;
    public static final String[] languages = {"Choose Language", "English", "Vietnamese"};
    Spinner spinner;
    EditText username_edt;
    WebView forgot_password_Webview;


    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        username_edt = findViewById(R.id.edt_username);
        forgot_password_Webview = findViewById(R.id.webview_forgotpassword);


        forgot_password_Webview.setWebViewClient(new WebViewClient(){
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
                    view.evaluateJavascript(
                                    "let elements = document.getElementsByTagName(\"*\"); "+
                                    "elements[37].click() ;"

                            ,null);
                }
                else if(url.contains("reset-credentials?client_id")){
                    view.evaluateJavascript(
                            "let username = document.getElementById(\"username\");"+
                                    String.format("username.value=\"%s\";",username_edt.getText().toString())+
                                    "let elements = document.getElementsByTagName(\"*\");+" +
                                    "elements[29].click();"
                            ,
                            null
                    );

                }else if(url.contains("authenticate?execution=")||url.contains("reset-credentials?execution")){
                    view.evaluateJavascript(
                            "let element = document.getElementById(\"kc-error-message\");"+
                            "(function () {\n" +
                                    "  if (element == null){\n" +
                                    "    return \"null\".toString();\n" +
                                    "  }\n" +
                                    "\telse{\n" +
                                    "    return element.outerText.toString();\n" +
                                    "  } \n" +
                                    "})();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            value = value.trim().replace("\"","");
                            if (Objects.equals(value, "null")) {
                                setContentView(R.layout.otp_layout);
                                Toast.makeText(getApplicationContext(), R.string.notice5, Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                        }
                    });
                    forgot_password_Webview.getSettings().setJavaScriptEnabled(false);
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
                if (selectedLang.equals("English")) {
                    setLocal(ForgotPassword.this, "en");
                    finish();
                    startActivity(getIntent());
                } else if (selectedLang.equals("Vietnamese")) {
                    setLocal(ForgotPassword.this,"hi");
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        search_btn = findViewById(R.id.btn_search);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username_edt.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.notice6,Toast.LENGTH_SHORT).show();
                }
                else {
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookies(null);
                    forgot_password_Webview.getSettings().setJavaScriptEnabled(true);
                    forgot_password_Webview.clearHistory();
                    forgot_password_Webview.clearCache(true);
                    forgot_password_Webview.loadUrl("https://uiot.ixxc.dev/manager/");
                }
            }
        });
    }
}