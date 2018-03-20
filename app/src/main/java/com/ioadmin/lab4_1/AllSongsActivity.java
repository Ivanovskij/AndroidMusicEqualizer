package com.ioadmin.lab4_1;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

public class AllSongsActivity extends AppCompatActivity {

    private ListView lvAllSongs;

    private ArrayList<File> songFromSd;
    private String[] itemsMusic;

    private String name_playlist;
    private String name_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name_playlist = bundle.getString("name_playlist");

        try {
            songFromSd = findSongByRoot(Config.ROOT_DIRECTORY);

            if (songFromSd == null || songFromSd.isEmpty()) {
                toastMsg("Song not found!");
                return;
            }
            songFromSd = deleteDuplicate(songFromSd);

            itemsMusic = new String[songFromSd.size()];
            for (int i = 0; i < songFromSd.size(); i++) {
                itemsMusic[i] = songFromSd.get(i).getName().toString();
            }
        } catch (Exception ex) {
            toastMsg(ex.getMessage());
            return;
        }

        initListView();
        registerForContextMenu(lvAllSongs);
    }

    private ArrayList<File> deleteDuplicate(ArrayList<File> songFromSd) {
        ArrayList<String> tmp = new ArrayList<>();
        ArrayList<File> songNotDup = new ArrayList<>();

        for (File f : songFromSd) {
            String name_song = f.getName();
            if (tmp.indexOf(name_song) == -1) {
                songNotDup.add(f);
                tmp.add(name_song);
            }
        }
        return songNotDup;
    }

    private ArrayList<File> findSongByRoot(File root)
            throws Exception {
        ArrayList<File> songs = new ArrayList<>();

        File[] files = root.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                songs.addAll(findSongByRoot(singleFile));
            } else {
                if (isMp3(singleFile) || isM4a(singleFile) || isWav(singleFile)) {
                        songs.add(singleFile);
                }
            }
        }

        return songs;
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
        lvAllSongs = (ListView) findViewById(R.id.lvAllSongs);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.list_song,
                R.id.txtSong,
                itemsMusic
        );
        lvAllSongs.setAdapter(adp);
        lvAllSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),
                        PlayActivity.class)
                        .putExtra("musicPos", position)
                        .putExtra("songList", songFromSd)
                );
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvAllSongs) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            name_music = lvAllSongs.getItemAtPosition(acmi.position).toString();

            menu.add(100, 100, 100, "add");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 100) {
            for (File song : songFromSd) {
                String song_name = song.getName();
                if (song_name.equals(name_music)) {
                    File dst = new File(Config.HOME_DIRECTORY + File.separator + name_playlist + File.separator + song_name);
                    try {
                        copyFileUsingStream(song, dst);
                    } catch (IOException e) {
                        toastMsg(e.getMessage().toString());
                    }
                }
            }
        }
        return  true;
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
