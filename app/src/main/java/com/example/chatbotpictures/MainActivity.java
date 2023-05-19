package com.example.chatbotpictures;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText editText_query;
    //TextView textView;
    ImageButton imageButtonSend;

    MessagesList messagesList;
    MessagesListAdapter<Message> adapter;

    User us, chatGpt;
    //Key -1 //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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_query= findViewById(R.id.editTextTextPersonName);
        //textView = findViewById(R.id.textdisplay);
        imageButtonSend= findViewById(R.id.imageButton_send);
        messagesList= findViewById(R.id.messagesList);

        ImageLoader imageLoader= new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);

            }
        };
         adapter= new MessagesListAdapter<Message>("1",imageLoader);
        messagesList.setAdapter(adapter);
        us= new User("1","John Wick","");
        chatGpt = new User("2","OpenAI","");
        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message message= new Message("M1",editText_query.getText().toString(),us, Calendar.getInstance().getTime(),null);
               adapter.addToStart(message,true);

               if(editText_query.getText().toString().toLowerCase().startsWith("generate image"))
               {
                   generateImages(editText_query.getText().toString());
               }
               else {
                   performAction(editText_query.getText().toString());
               }



                editText_query.setText("");
            }
        });


    }

    public void performAction(String inputText)
    {
        RequestQueue queue= Volley.newRequestQueue(this);
        String url ="https://api.openai.com/v1/completions";


        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("prompt", inputText);
            jsonObject.put("max_tokens", 1200);
            jsonObject.put("model", "text-davinci-003");

            //Make a request

            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            String answer= null;
                            try {
                                answer= response.getJSONArray("choices").getJSONObject(0).getString("text");
                                Message message= new Message("m2",answer.trim(),chatGpt,Calendar.getInstance().getTime(),null);
                                adapter.addToStart(message,true);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            //textView.setText(answer);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   // textView.setText("That didn't work");

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> map= new HashMap<>();
                    map.put("Content-Type","application/json");
                    map.put("Authorization", "Bearer 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");
                    return map;
                }
            };

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 15;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void generateImages(String inputText)
    {
        RequestQueue queue= Volley.newRequestQueue(this);
        String url ="https://api.openai.com/v1/images/generations";


        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("prompt", inputText);
            jsonObject.put("n", 2);
            jsonObject.put("size", "512x512");

            //Make a request

            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            String answer= null;
                            try {
                                answer= response.getJSONArray("data").getJSONObject(0).getString("url");
                                Log.d("TryImage",answer);
                                Message message= new Message("M2","Image",chatGpt,Calendar.getInstance().getTime(), answer.trim());
                                adapter.addToStart(message,true);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                           // textView.setText(answer);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //textView.setText("That didn't work");

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> map= new HashMap<>();
                    map.put("Content-Type","application/json");
                    map.put("Authorization", "Bearer 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");
                    return map;
                }
            };

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 15;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}