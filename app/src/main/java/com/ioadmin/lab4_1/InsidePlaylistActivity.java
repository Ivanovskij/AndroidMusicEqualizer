package com.ioadmin.lab4_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class InsidePlaylistActivity extends AppCompatActivity {

    private ListView lvSongByPlaylist;

    private ArrayList<File> songFromPlaylist;
    private String[] itemsMusic;

    private String name_playlist;
    private String name_song_by_del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_playlist);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name_playlist = bundle.getString("name_playlist");

        try {
            initPlaylistsAndItems();
        } catch (Exception ex) {
            toastMsg(ex.getMessage().toString());
            return;
        }

        initListView();
        registerForContextMenu(lvSongByPlaylist);
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

    private void initPlaylistsAndItems() throws Exception {
        songFromPlaylist = findSongByPlaylist();
        if (songFromPlaylist == null) {
            throw new Exception("Playlists not found");
        }

        itemsMusic = new String[songFromPlaylist.size()];
        for (int i = 0; i < songFromPlaylist.size(); i++) {
            itemsMusic[i] = songFromPlaylist.get(i).getName();
        }
    }

    private ArrayList<File> findSongByPlaylist() throws Exception {
        ArrayList<File> songsByPlaylist = new ArrayList<>();

        File dir_byPlaylist = new File(Config.HOME_DIRECTORY
                + File.separator + name_playlist);
        File[] songs = dir_byPlaylist.listFiles();

        for (File song : songs) {
            if (isMp3(song) || isM4a(song) || isWav(song)) {
                songsByPlaylist.add(song);
            }
        }

        return songsByPlaylist;
    }

    private boolean isWav(File file) {
        return file.getName().endsWith(".wav");
    }

    private boolean isMp3(File file) {
        return file.getName().endsWith(".mp3");
    }

    private boolean isM4a(File file) {
        return file.getName().endsWith(".m4a");
    }

    private void initListView() {
        lvSongByPlaylist = (ListView) findViewById(R.id.lvSongByPlaylist);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.list_song,
                R.id.txtSong,
                itemsMusic
        );
        lvSongByPlaylist.setAdapter(adp);
        lvSongByPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),
                        PlayActivity.class)
                        .putExtra("musicPos", position)
                        .putExtra("songList", songFromPlaylist)
                );
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvSongByPlaylist) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            name_song_by_del = lvSongByPlaylist.getItemAtPosition(acmi.position).toString();

            menu.add(100, 200, 100, "delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 200) {
            for (File song : songFromPlaylist) {
                String song_name = song.getName();
                if (song_name.equals(name_song_by_del)) {
                    try {
                        deleteSong(song);
                        initPlaylistsAndItems();
                        initListView();
                    } catch (Exception e) {
                        toastMsg(e.getMessage().toString());
                    }
                }
            }
        }
        return  true;
    }

    private void deleteSong(File song) throws IOException {
        if (!song.delete()) {
            throw new IOException("Song not delete from playlist");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_songs, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.allSongs :
            {
                startActivity(new Intent(getApplicationContext(),
                        AllSongsActivity.class)
                        .putExtra("name_playlist", name_playlist)
                );
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
}
