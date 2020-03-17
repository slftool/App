package me.neocode.slftool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    JSONObject jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        final Button regenerate = findViewById(R.id.regenerate);
        final EditText letter = findViewById(R.id.letter);
        final TextView view = findViewById(R.id.textView);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL link = new URL("https://slftool.github.io/data.json");
                    BufferedReader in = new BufferedReader(new InputStreamReader(link.openStream()));
                    String line;
                    String all = "";

                    while ((line = in.readLine()) != null) {
                        all = all + line;
                    }
                    jsonData = new JSONObject(all);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            regenerate.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                            regenerate.setVisibility(View.VISIBLE);
                            letter.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Datenbank erfolgreich geladen!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Beim laden der Datenbank ist ein Fehler aufgetreten! kein Internet?", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();

        regenerate.setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                String letterName = letter.getText().toString();
                letterName = letterName.toLowerCase();
                if ("abcdefghijklmnopqrstuvwxyz".contains(letterName)) {
                    try {
                        hideKeyboard();

                        JSONArray stadt = jsonData.getJSONObject(letterName).getJSONArray("stadt");
                        JSONArray land = jsonData.getJSONObject(letterName).getJSONArray("land");
                        JSONArray fluss = jsonData.getJSONObject(letterName).getJSONArray("fluss");
                        JSONArray name = jsonData.getJSONObject(letterName).getJSONArray("name");
                        JSONArray beruf = jsonData.getJSONObject(letterName).getJSONArray("beruf");
                        JSONArray tier = jsonData.getJSONObject(letterName).getJSONArray("tier");
                        JSONArray marke = jsonData.getJSONObject(letterName).getJSONArray("marke");
                        JSONArray pflanze = jsonData.getJSONObject(letterName).getJSONArray("pflanze");

                        view.setText("Stadt: " + stadt.getString(getRandomNumberInRange(0, stadt.length())) +
                                "\nLand: " + land.getString(getRandomNumberInRange(0, land.length())) +
                                "\nFluss: " + fluss.getString(getRandomNumberInRange(0, fluss.length())) +
                                "\nName: " + name.getString(getRandomNumberInRange(0, name.length())) +
                                "\nBeruf: " + beruf.getString(getRandomNumberInRange(0, beruf.length())) +
                                "\nTier: " + tier.getString(getRandomNumberInRange(0, tier.length()))+
                                "\nMarke: " + marke.getString(getRandomNumberInRange(0, marke.length()))+
                                "\nPflanze: " + pflanze.getString(getRandomNumberInRange(0, pflanze.length())));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void hideKeyboard() {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min)) + min;
    }
}