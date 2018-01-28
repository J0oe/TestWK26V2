package com.ex.admin.testwk26v2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;

/**
 * Created by Admin on 27.01.2018.
 */

public class NewsFinal extends AppCompatActivity {
    Bundle bundle;
    DBHelper dbHelper;
    LoginButton loginButton;
    CallbackManager callbackManager;
    String urlToImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_final);

        FacebookSdk.sdkInitialize(getApplicationContext());
        initializator();
        loginFB();


        TextView textTittle = findViewById(R.id.idTittle);
        TextView textDescription = findViewById(R.id.idDescription);
        TextView textUrl = findViewById(R.id.idUrl);
        TextView texttime = findViewById(R.id.idTime);
        TextView textAuthor = findViewById(R.id.idAuthor);


        dbHelper = new DBHelper(this);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            textTittle.setText(bundle.getString("title"));
            textDescription.setText(bundle.getString("description"));
            textAuthor.setText(bundle.getString("author"));
            texttime.setText(bundle.getString("publishedAt"));
            textUrl.setText(bundle.getString("url"));
            urlToImage = bundle.getString("urlToImage");
        }


        new DownloadImageTask((ImageView) findViewById(R.id.idImageUrl))
                .execute(urlToImage);


        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(bundle.getString("url")))
                .build();
        ShareButton shareButton = findViewById(R.id.idShareBut);
        shareButton.setShareContent(content);
    }

    public void onClickSaveNews(View view) {


        String title = null;
        String description = null;
        String author = null;
        String time = null;
        String url = null;
        String urlToImage = null;

        ContentValues cv = new ContentValues();
        if (bundle != null) {
            title = bundle.getString("title");
            description = (bundle.getString("description"));
            author = (bundle.getString("author"));
            time = (bundle.getString("publishedAt"));
            url = (bundle.getString("url"));
            urlToImage = bundle.getString("urlToImage");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put("urlToImage", urlToImage);
        cv.put("author", author);
        cv.put("description", description);
        cv.put("url", url);
        cv.put("publishedAt", time);
        cv.put("title", title);


        db.insert("mytable", null, cv);
        Toast.makeText(this, "добавленно в избранное", Toast.LENGTH_LONG).show();
        db.close();


    }

    static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "author text,"
                    + "description text,"
                    + "urlToImage text,"
                    + "url text,"
                    + "publishedAt text,"
                    + "title text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public void initializator() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);

    }

    private void loginFB() {

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "подключились FB", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onCLickShare(View view) {
        ShareDialog shareDialog = new ShareDialog(this);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(bundle.getString("url")))
                .build();
        shareDialog.show(content);


    }


    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}