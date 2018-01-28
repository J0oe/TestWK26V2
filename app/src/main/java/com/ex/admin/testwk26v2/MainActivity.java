package com.ex.admin.testwk26v2;

import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareButton;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    ArrayList<String> author;
    ArrayList<String> title;
    ArrayList<String> description;
    ArrayList<String> url;
    ArrayList<String> urlToImage;
    ArrayList<String> publishedAt;
    EditText editTextForSearch;

    static String totalResult;
    ArrayAdapter<String> adapter;
    ListView listView;
    ListView listViewAuthor;
    ArrayList<String> authorlistView;
    ArrayList<String> authorListViewIndex;
    String nameAuthor;
    ArrayAdapter<String> adapterForAuthor;
    LinearLayout tableForSearch;
    EditText editTextFindFrom;
    EditText editTextFindTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authorlistView = new ArrayList<>();
        authorListViewIndex = new ArrayList<>();
        editTextFindFrom = findViewById(R.id.idFindFrom);
        editTextFindTo = findViewById(R.id.idFindTo);

        authorlistView.add("ABC News");
        authorlistView.add("BBC News");
        authorlistView.add("CNN");
        authorlistView.add("Daily Mail");
        authorlistView.add("Time");


        authorListViewIndex.add("abc-news");
        authorListViewIndex.add("bbc-news");
        authorListViewIndex.add("cnn");
        authorListViewIndex.add("daily-mail");
        authorListViewIndex.add("time");

        tableForSearch = findViewById(R.id.idFindFromTo);

        listView = findViewById(R.id.idListForNews);

        new ParseTask().execute();

        listViewAuthor = findViewById(R.id.idListViewAuthor);
        adapterForAuthor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, authorlistView);
        listViewAuthor.setAdapter(adapterForAuthor);


        listViewAuthor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                nameAuthor = authorListViewIndex.get(position);
                new ParseTaskAuthor().execute();
                listViewAuthor.setVisibility(View.INVISIBLE);

            }
        });
    }


    private class ParseTask extends AsyncTask<Void, Void, String> {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL("https://newsapi.org/v2/everything?q=новости&apiKey=6300fada41b64190a5bbcb2d4aec9d30");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            author = new ArrayList<>();
            title = new ArrayList<>();
            description = new ArrayList<>();
            url = new ArrayList<>();
            urlToImage = new ArrayList<>();
            publishedAt = new ArrayList<>();


            super.onPostExecute(strJson);


            try {
                JSONObject res = new JSONObject(resultJson);
                String check = res.getString("status");
                if (check.equals("ok")) {
                    totalResult = res.getString("totalResults");
                    for (int q = 0; q < Integer.valueOf(totalResult); q++) {
                        JSONObject nameJsonNews = res.getJSONArray("articles").getJSONObject(q);
                        JSONObject source = nameJsonNews.getJSONObject("source");

                        String authorDate = source.getString("name");

                        String titleDate = nameJsonNews.getString("title");
                        String descriptionDate = nameJsonNews.getString("description");
                        String urlDate = nameJsonNews.getString("url");
                        String urlToImageDate = nameJsonNews.getString("urlToImage");
                        String publishedAtDate = nameJsonNews.getString("publishedAt");

                        author.add(authorDate);
                        title.add(titleDate);
                        description.add(descriptionDate);
                        url.add(urlDate);
                        urlToImage.add(urlToImageDate);
                        publishedAt.add(publishedAtDate);
                    }


                }


            } catch (Throwable throwable) {
                Log.d("Error", "Wrong");
            }


            listNews();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NewsFinal.class);

                    intent.putExtra("author", author.get(position));
                    intent.putExtra("title", title.get(position));
                    intent.putExtra("description", description.get(position));
                    intent.putExtra("url", url.get(position));
                    intent.putExtra("urlToImage", urlToImage.get(position));
                    intent.putExtra("publishedAt", publishedAt.get(position));
                    startActivity(intent);
                }

            });


        }


    }


    public void onCLickShowMeAuthor(View view) {
        listViewAuthor = findViewById(R.id.idListViewAuthor);
        listViewAuthor.setVisibility(View.VISIBLE);
    }

    private class ParseTaskAuthor extends AsyncTask<Void, Void, String> {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL("https://newsapi.org/v2/everything?q=" + editTextForSearch.getText().toString() + "&" + "sources=" + nameAuthor + "&apiKey=6300fada41b64190a5bbcb2d4aec9d30");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            author = new ArrayList<>();
            title = new ArrayList<>();
            description = new ArrayList<>();
            url = new ArrayList<>();
            urlToImage = new ArrayList<>();
            publishedAt = new ArrayList<>();


            super.onPostExecute(strJson);


            try {
                JSONObject res = new JSONObject(resultJson);
                String check = res.getString("status");
                if (check.equals("ok")) {
                    totalResult = res.getString("totalResults");
                    for (int q = 0; q < Integer.valueOf(totalResult); q++) {
                        JSONObject nameJsonNews = res.getJSONArray("articles").getJSONObject(q);
                        JSONObject source = nameJsonNews.getJSONObject("source");

                        String authorDate = source.getString("name");

                        String titleDate = nameJsonNews.getString("title");
                        String descriptionDate = nameJsonNews.getString("description");
                        String urlDate = nameJsonNews.getString("url");
                        String urlToImageDate = nameJsonNews.getString("urlToImage");
                        String publishedAtDate = nameJsonNews.getString("publishedAt");

                        author.add(authorDate);
                        title.add(titleDate);
                        description.add(descriptionDate);
                        url.add(urlDate);
                        urlToImage.add(urlToImageDate);
                        publishedAt.add(publishedAtDate);
                    }


                }


            } catch (Throwable throwable) {
                Log.d("Error", "Wrong");
            }


            listNews();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NewsFinal.class);

                    intent.putExtra("author", author.get(position));
                    intent.putExtra("title", title.get(position));
                    intent.putExtra("description", description.get(position));
                    intent.putExtra("url", url.get(position));
                    intent.putExtra("urlToImage", urlToImage.get(position));
                    intent.putExtra("publishedAt", publishedAt.get(position));
                    startActivity(intent);
                }

            });


        }


    }

    private class ParseTaskForSearch extends AsyncTask<Void, Void, String> {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL("https://newsapi.org/v2/everything?q=" + editTextForSearch.getText().toString() + "&apiKey=6300fada41b64190a5bbcb2d4aec9d30");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            author = new ArrayList<>();
            title = new ArrayList<>();
            description = new ArrayList<>();
            url = new ArrayList<>();
            urlToImage = new ArrayList<>();
            publishedAt = new ArrayList<>();


            super.onPostExecute(strJson);


            try {
                JSONObject res = new JSONObject(resultJson);
                String check = res.getString("status");
                if (check.equals("ok")) {
                    totalResult = res.getString("totalResults");
                    for (int q = 0; q < Integer.valueOf(totalResult); q++) {
                        JSONObject nameJsonNews = res.getJSONArray("articles").getJSONObject(q);
                        JSONObject source = nameJsonNews.getJSONObject("source");

                        String authorDate = source.getString("name");

                        String titleDate = nameJsonNews.getString("title");
                        String descriptionDate = nameJsonNews.getString("description");
                        String urlDate = nameJsonNews.getString("url");
                        String urlToImageDate = nameJsonNews.getString("urlToImage");
                        String publishedAtDate = nameJsonNews.getString("publishedAt");

                        author.add(authorDate);
                        title.add(titleDate);
                        description.add(descriptionDate);
                        url.add(urlDate);
                        urlToImage.add(urlToImageDate);
                        publishedAt.add(publishedAtDate);
                    }


                }


            } catch (Throwable throwable) {
                Log.d("Error", "Wrong");
            }


            listNews();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NewsFinal.class);
                    intent.putExtra("author", author.get(position));
                    intent.putExtra("title", title.get(position));
                    intent.putExtra("description", description.get(position));
                    intent.putExtra("url", url.get(position));
                    intent.putExtra("urlToImage", urlToImage.get(position));
                    intent.putExtra("publishedAt", publishedAt.get(position));
                    startActivity(intent);
                }
            });


        }


    }


    private class ParseTaskForSearchFromTo extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";


        @Override
        protected String doInBackground(Void... params) {
            try {
                editTextFindFrom = findViewById(R.id.idFindFrom);
                editTextForSearch = findViewById(R.id.idTextForSearch);
                editTextFindTo = findViewById(R.id.idFindTo);

               URL urlLink = new URL("https://newsapi.org/v2/everything?q=" + editTextForSearch.getText().toString() + "&from=" + editTextFindTo.getText().toString() + "&to=" + editTextFindTo.getText().toString() + "&apiKey=6300fada41b64190a5bbcb2d4aec9d30");

                urlConnection = (HttpURLConnection) urlLink.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            author = new ArrayList<>();
            title = new ArrayList<>();
            description = new ArrayList<>();
            url = new ArrayList<>();
            urlToImage = new ArrayList<>();
            publishedAt = new ArrayList<>();


            super.onPostExecute(strJson);


            try {
                JSONObject res = new JSONObject(resultJson);
                String check = res.getString("status");
                if (check.equals("ok")) {
                    totalResult = res.getString("totalResults");
                    for (int q = 0; q < Integer.valueOf(totalResult); q++) {
                        JSONObject nameJsonNews = res.getJSONArray("articles").getJSONObject(q);
                        JSONObject source = nameJsonNews.getJSONObject("source");

                        String authorDate = source.getString("name");

                        String titleDate = nameJsonNews.getString("title");
                        String descriptionDate = nameJsonNews.getString("description");
                        String urlDate = nameJsonNews.getString("url");
                        String urlToImageDate = nameJsonNews.getString("urlToImage");
                        String publishedAtDate = nameJsonNews.getString("publishedAt");

                        author.add(authorDate);
                        title.add(titleDate);
                        description.add(descriptionDate);
                        url.add(urlDate);
                        urlToImage.add(urlToImageDate);
                        publishedAt.add(publishedAtDate);
                    }


                }


            } catch (Throwable throwable) {
                Log.d("Error", "Wrong");
            }


            listNews();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NewsFinal.class);
                    intent.putExtra("author", author.get(position));
                    intent.putExtra("title", title.get(position));
                    intent.putExtra("description", description.get(position));
                    intent.putExtra("url", url.get(position));
                    intent.putExtra("urlToImage", urlToImage.get(position));
                    intent.putExtra("publishedAt", publishedAt.get(position));
                    startActivity(intent);
                }
            });


        }


    }

    private class ParseTaskForPopular extends AsyncTask<Void, Void, String> {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL("https://newsapi.org/v2/everything?q=" + editTextForSearch.getText().toString() + "&sortBy=popularity&apiKey=6300fada41b64190a5bbcb2d4aec9d30");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            author = new ArrayList<>();
            title = new ArrayList<>();
            description = new ArrayList<>();
            url = new ArrayList<>();
            urlToImage = new ArrayList<>();
            publishedAt = new ArrayList<>();


            super.onPostExecute(strJson);


            try {
                JSONObject res = new JSONObject(resultJson);
                String check = res.getString("status");
                if (check.equals("ok")) {
                    totalResult = res.getString("totalResults");
                    for (int q = 0; q < Integer.valueOf(totalResult); q++) {
                        JSONObject nameJsonNews = res.getJSONArray("articles").getJSONObject(q);
                        JSONObject source = nameJsonNews.getJSONObject("source");

                        String authorDate = source.getString("name");

                        String titleDate = nameJsonNews.getString("title");
                        String descriptionDate = nameJsonNews.getString("description");
                        String urlDate = nameJsonNews.getString("url");
                        String urlToImageDate = nameJsonNews.getString("urlToImage");
                        String publishedAtDate = nameJsonNews.getString("publishedAt");

                        author.add(authorDate);
                        title.add(titleDate);
                        description.add(descriptionDate);
                        url.add(urlDate);
                        urlToImage.add(urlToImageDate);
                        publishedAt.add(publishedAtDate);
                    }


                }


            } catch (Throwable throwable) {
                Log.d("Error", "Wrong");
            }


            listNews();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NewsFinal.class);
                    intent.putExtra("author", author.get(position));
                    intent.putExtra("title", title.get(position));
                    intent.putExtra("description", description.get(position));
                    intent.putExtra("url", url.get(position));
                    intent.putExtra("urlToImage", urlToImage.get(position));
                    intent.putExtra("publishedAt", publishedAt.get(position));
                    startActivity(intent);
                }
            });


        }


    }


    private class ParseTaskForTime extends AsyncTask<Void, Void, String> {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {

                URL url = new URL("https://newsapi.org/v2/everything?q=" + editTextForSearch.getText().toString() + "&sortBy=publishedAt&apiKey=6300fada41b64190a5bbcb2d4aec9d30");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        @Override
        protected void onPostExecute(String strJson) {
            author = new ArrayList<>();
            title = new ArrayList<>();
            description = new ArrayList<>();
            url = new ArrayList<>();
            urlToImage = new ArrayList<>();
            publishedAt = new ArrayList<>();


            super.onPostExecute(strJson);


            try {
                JSONObject res = new JSONObject(resultJson);
                String check = res.getString("status");
                if (check.equals("ok")) {
                    totalResult = res.getString("totalResults");
                    for (int q = 0; q < Integer.valueOf(totalResult); q++) {
                        JSONObject nameJsonNews = res.getJSONArray("articles").getJSONObject(q);
                        JSONObject source = nameJsonNews.getJSONObject("source");

                        String authorDate = source.getString("name");

                        String titleDate = nameJsonNews.getString("title");
                        String descriptionDate = nameJsonNews.getString("description");
                        String urlDate = nameJsonNews.getString("url");
                        String urlToImageDate = nameJsonNews.getString("urlToImage");
                        String publishedAtDate = nameJsonNews.getString("publishedAt");

                        author.add(authorDate);
                        title.add(titleDate);
                        description.add(descriptionDate);
                        url.add(urlDate);
                        urlToImage.add(urlToImageDate);
                        publishedAt.add(publishedAtDate);
                    }


                }


            } catch (Throwable throwable) {
                Log.d("Error", "Wrong");
            }


            listNews();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NewsFinal.class);
                    intent.putExtra("author", author.get(position));
                    intent.putExtra("title", title.get(position));
                    intent.putExtra("description", description.get(position));
                    intent.putExtra("url", url.get(position));
                    intent.putExtra("urlToImage", urlToImage.get(position));
                    intent.putExtra("publishedAt", publishedAt.get(position));
                    startActivity(intent);
                }
            });


        }


    }


    public void listNews() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, title);
        listView.setAdapter(adapter);

    }


    public void onClickPopular(View view) {
        editTextForSearch = findViewById(R.id.idTextForSearch);
        new ParseTaskForPopular().execute();
    }

    public void onClickTime(View view) {
        editTextForSearch = findViewById(R.id.idTextForSearch);
        new ParseTaskForTime().execute();
    }

    public void onClickSearch(View view) {
        editTextForSearch = findViewById(R.id.idTextForSearch);
        new ParseTaskForSearch().execute();
    }


    public void onClickFromTo(View view) {
        tableForSearch.setVisibility(View.VISIBLE);
    }

    public void onCLickShowMeFromTo(View view) {
        new ParseTaskForSearchFromTo().execute();
        tableForSearch.setVisibility(View.INVISIBLE);
    }

    public void onClickNext(View view) {
        startActivity(new Intent(this, NewsSaved.class));
    }
}
