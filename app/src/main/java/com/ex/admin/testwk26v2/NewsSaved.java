package com.ex.admin.testwk26v2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 27.01.2018.
 */

public class NewsSaved extends AppCompatActivity {
    NewsFinal.DBHelper dbHelper;

    ArrayList<String> author;
    ArrayList<String> title;
    ArrayList<String> description;
    ArrayList<String> url;
    ArrayList<String> urlToImage;
    ArrayList<String> publishedAt;
    ArrayAdapter adapter2;
    ListView listView;
    SQLiteDatabase db;
    Cursor c;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_saved);
        author = new ArrayList<>();
        title = new ArrayList<>();
        description = new ArrayList<>();
        url = new ArrayList<>();
        urlToImage = new ArrayList<>();
        publishedAt = new ArrayList<>();
        listView = findViewById(R.id.idListNewsSaved);
        dbHelper = new NewsFinal.DBHelper(this);


        db = dbHelper.getWritableDatabase();


        db.delete("mytable", "id = ?", null);

        c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {

            int idColIndex = c.getColumnIndex("id");
            int authorIndex = c.getColumnIndex("author");
            int titleIndex = c.getColumnIndex("title");
            int urlIndex = c.getColumnIndex("url");
            int timeIndex = c.getColumnIndex("publishedAt");
            int descriptionIndex = c.getColumnIndex("description");
            int urlToImageIndex = c.getColumnIndex("urlToImage");

            do {
                author.add(c.getString(authorIndex));
                title.add(c.getString(titleIndex));
                description.add(c.getString(descriptionIndex));
                url.add(c.getString(urlIndex));
                publishedAt.add(c.getString(timeIndex));
                urlToImage.add(c.getString(urlToImageIndex));

            } while (c.moveToNext());

        }
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, title);
        listView.setAdapter(adapter2);


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
