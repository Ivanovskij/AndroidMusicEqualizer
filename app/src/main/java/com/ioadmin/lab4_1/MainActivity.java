package com.ioadmin.lab4_1;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
            implements View.OnClickListener {

    private ListView lvPlaylists;

    private ArrayList<File> playlistsFromSD;

    private String[] itemsPlaylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initPlaylistsAndItems();
        } catch (Exception ex) {
            toastMsg(ex.getMessage());
            return;
        }

        initListView();
    }

    private void initPlaylistsAndItems() throws Exception {
        playlistsFromSD = findAllPlaylists(Config.HOME_DIRECTORY);
        if (playlistsFromSD == null) {
            throw new Exception("Playlists not found");
        }

        itemsPlaylist = new String[playlistsFromSD.size()];
        for (int i = 0; i < playlistsFromSD.size(); i++) {
            itemsPlaylist[i] = playlistsFromSD.get(i).getName();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            initPlaylistsAndItems();
            initListView();
        } catch (Exception ex) {
            toastMsg(ex.getMessage().toString());
            return;
        }

    }

    private ArrayList<File> findAllPlaylists(File home_directory)
            throws Exception {
        File[] playlists = home_directory.listFiles();

        if (playlists == null) {
            return null;
        }

        ArrayList<File> plists = new ArrayList<>();
        for (File playlist : playlists) {
            if (playlist.isDirectory()) {
                plists.add(playlist);
            }
        }
        return plists;
    }

    private void initListView() {
        lvPlaylists = (ListView) findViewById(R.id.lvPlayLists);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.list_song,
                R.id.txtSong,
                itemsPlaylist
        );
        lvPlaylists.setAdapter(adp);

        lvPlaylists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                startActivity(new Intent(getApplicationContext(),
                        InsidePlaylistActivity.class)
                        .putExtra("name_playlist", itemsPlaylist[pos].toString())
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.createPlaylist :
            {
                Intent createPl = new Intent(MainActivity.this, CreatePlaylist.class);
                startActivity(createPl);
                break;
            }
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {

    }
}
