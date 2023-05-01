package kr.co.company.healthapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatbotActivity extends AppCompatActivity {

    private EditText etUserInput;
    private Button btnSend;
    private TextView tvResponse;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        tvResponse = findViewById(R.id.tvResponse);
        etUserInput = findViewById(R.id.etUserInput);
        btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = etUserInput.getText().toString();

                new Thread(new Runnable() {
                    String result = "";
                    @Override
                    public void run() {
                        try {
                            result = chatbotTest(userInput);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        (ChatbotActivity.this).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvResponse.setText(result);
//                                text.setText(data);
                                //adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }
        });




    }

    // chatbotAPI 요청
    private String chatbotTest(String userInput) throws Exception {
        try {
            URL url = new URL("https://chatbot-api.run.goorm.site/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject data = new JSONObject();
            data.put("user_input", userInput);

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data.toString());
            out.flush();
            out.close();

            String temp = "";
            String content = "";
            InputStream responseBody = conn.getInputStream();
            InputStreamReader responseBodyReader =
                    new InputStreamReader(responseBody, "UTF-8");
            BufferedReader br = new BufferedReader( responseBodyReader );
            while ((temp = br.readLine()) != null) {
                content += temp;
            }
            JSONObject responseJson = new JSONObject(content);
            Log.d("chatGPT 응답", responseJson.toString(2));
            br.close();

            return responseJson.toString(2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}