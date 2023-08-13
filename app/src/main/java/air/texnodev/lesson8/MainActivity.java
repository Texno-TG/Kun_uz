package air.texnodev.lesson8;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import air.texnodev.lesson8.service.File_Service;

public class MainActivity extends AppCompatActivity {
    org.jsoup.nodes.Document documents;
    private RequestQueue requestQueue;
    private static final String TAG = "MyActivity";
    public ArrayList<String> arrayList1 = new ArrayList<String>();
    ImageView imageView;
    TextView textView, text_eyes,text_time, discreb, counter;
    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.title_img);
        textView = findViewById(R.id.text_title);
        counter = findViewById(R.id.counter);
        listKunuz();
        initJson();

        ConstraintLayout con = findViewById(R.id.main_layout);
        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeRight() {
                Animation animationUtils = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale);
                con.startAnimation(animationUtils);


                animationUtils.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int intPage = Integer.parseInt(counter.getText().toString());
                        backPage((intPage - 1));
                        Animation animationUtils2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scals);
                        con.startAnimation(animationUtils2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            public void onSwipeLeft() {
                Animation animationUtils = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale);
                con.startAnimation(animationUtils);


                animationUtils.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int intPage = Integer.parseInt(counter.getText().toString());
                        nextPage((intPage + 1));
                        Animation animationUtils2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scals);
                        con.startAnimation(animationUtils2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        });

    }
    private void nextPage(int i){
        File_Service fileService = new File_Service();
        String s = fileService.readFromFile(this, "list.json");
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<String> sArrayList = gson.fromJson(s, type);

        Log.d(TAG, "NextPage  " + sArrayList.size() + "  --->  " + i);
        if (sArrayList.size() > i){

            showUI(sArrayList.get(i));
            counter.setText(String.valueOf(i));
        }else {
            Toast.makeText(this, "Boshqa sahifa mavjud emas!", Toast.LENGTH_SHORT).show();
        }

    }
    private void backPage(int i){

        File_Service fileService = new File_Service();
        String s = fileService.readFromFile(this, "list.json");
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<String> sArrayList = gson.fromJson(s, type);

        Log.d(TAG, "BackPage  " + sArrayList.size() + "  --->  " + i);
        if (-1 < i){

            showUI(sArrayList.get(i));
            counter.setText(String.valueOf(i));
        }else {
            Toast.makeText(this, "Boshqa sahifa mavjud emas!", Toast.LENGTH_SHORT).show();
        }

    }

    private void setImageView(String s){
        Glide.with(this).load(s).into((ImageView) findViewById(R.id.title_img));
        imageView.setVisibility(View.VISIBLE);

    }
    public void showUI(String link){
        File_Service fileService = new File_Service();
        StringBuffer buffer = new StringBuffer();

        new Thread(() -> {
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(link).get();
                Log.d(TAG, "TITLE  " + document.title());
                runOnUiThread(() -> textView.setText(document.title()));

                Elements img = document.select("img[src*=storage.kun.uz/source/]");
                Log.d(TAG, "Img  " + img.first().attr("src"));
                runOnUiThread(() -> setImageView(img.first().attr("src")));
                runOnUiThread(() -> {
                    text_time = findViewById(R.id.text_times);
                    text_time.setText(document.select(".date").first().ownText());
                    text_eyes = findViewById(R.id.text_eyes);
                    text_eyes.setText(document.select(".view").first().ownText());
                    discreb = findViewById(R.id.discreption);
                    discreb.setText(document.select(".single-content").first().text());

                });

                Log.d(TAG, "Date " + document.select(".date").first().ownText());
                Log.d(TAG, "View " + document.select(".view").first().ownText());
                Log.d(TAG, "All " + document.select(".single-content").first().text());

            } catch (Exception e) {
                Log.d(TAG, "Exception " + e.getMessage());

            }

        }).start();


    }


    public void initJson(){
        File_Service fileService = new File_Service();
        String s = fileService.readFromFile(this, "list.json");
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<String> sArrayList = gson.fromJson(s, type);
        counter.setText(String.valueOf(0));
        showUI(sArrayList.get(0));

    }

    public void listKunuz(){
        File_Service fileService = new File_Service();
        requestQueue = Volley.newRequestQueue(this);

        String url = "https://kun.uz/rss";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    //Log.d(TAG, response);

                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                    try {

                        DocumentBuilder builder = builderFactory.newDocumentBuilder();
                        Document document = builder.parse(new InputSource(new StringReader(response)));
                        document.getDocumentElement().normalize();

                        NodeList nodeList = document.getElementsByTagName("item");
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Node node = nodeList.item(i);

                            if (node.hasChildNodes()){

                                Element element = (Element) node;
                                String name = element.getElementsByTagName("link").item(0).getTextContent();
                                arrayList1.add(name);
                            }
                        }
                        String mydata = new Gson().toJson(arrayList1);
                        fileService.writeToFile(mydata, "list.json", this);
                        Log.d(TAG, "Array 1 " + fileService.readFromFile(this, "list.json"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, e.getMessage());
                    }

                }, error -> Log.d(TAG, error.getMessage()));

        requestQueue.add(stringRequest);
    }
}