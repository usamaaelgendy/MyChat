package com.usama.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.usama.mychat.Models.StaticInfo;
import com.usama.mychat.Services.IFireBaseAPI;
import com.usama.mychat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ActivityLogin extends AppCompatActivity {

    EditText et_Email, et_Password;
    Button btn_Login;
    public static final String ENDPOINT = "https://mys3chat.firebaseio.com";
    ProgressDialog pd;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_Email = findViewById(R.id.et_Email);
        et_Password = findViewById(R.id.et_Password);

    }

    public void btnLoginClick(View view) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        } else if (et_Email.getText().toString().equals("")) {
            et_Email.setError("Email cannot be empty");

        } else if (et_Password.getText().toString().equals("")) {
            et_Password.setError("Password cannot be empty");
        } else {
            pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
            pd.show();
            email = Tools.encodeString(et_Email.getText().toString());
            LoginTask t = new LoginTask();
            t.execute();
        }
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            IFireBaseAPI api = retrofit.create(IFireBaseAPI.class);
            // Call<String> call = api.getAllUsersAsJsonString();
            Call<String> call = api.getSingleUserByEmail(StaticInfo.UsersURL + "/" + email + ".json");
            try {
                return call.execute().body();
            } catch (Exception e) {
                pd.hide();
                return "null";
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            try {
                if (!jsonString.trim().equals("null")) {
                    JSONObject userObj = new JSONObject(jsonString);
                    String pass = et_Password.getText().toString();
                    if (userObj.getString("Password").equals(pass)) {
                        pd.hide();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("LocalUser", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("Email", email);
                        editor.putString("FirstName", userObj.getString("FirstName"));
                        editor.putString("LastName", userObj.getString("LastName"));
                        editor.commit();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        pd.hide();
                        Toast.makeText(ActivityLogin.this, "Incorecct email or password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    pd.hide();
                    Toast.makeText(ActivityLogin.this, "Incorecct email or password", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                pd.hide();
                e.printStackTrace();
            }
        }
    }
    public void btnSignUpClick(View view) {

        startActivity(new Intent(this, ActivityRegister.class));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}