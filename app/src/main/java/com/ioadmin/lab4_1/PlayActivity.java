package com.ioadmin.lab4_1;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener
{

    private static MediaPlayer mediaPlayer;
    private ArrayList<File> songFromSD;
    private int position;
    private Uri uri;

    private SeekBar sbar;
    private Thread updateSeekBar;

    private Button btnPlay,
                btnNext,
                btnPrev;


    /************* EQUALIZER ******************/
    private TextView bass_boost_label = null;
    private SeekBar bass_boost = null;
    private Button flat = null;

    private Equalizer eq = null;
    private BassBoost bb = null;

    private int min_level = 0;
    private int max_level = 100;

    private static final int MAX_SLIDERS = 5; // Must match the XML layout
    private SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    private TextView slider_labels[] = new TextView[MAX_SLIDERS];
    private int num_sliders = 0;
    /************* END EQUALIZER ******************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initButtons();
        initSeekBar();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songFromSD = (ArrayList) bundle.getParcelableArrayList("songList");
        position = bundle.getInt("musicPos", 0);

        uri = Uri.parse(songFromSD.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        updateSeekBar.start();

        initEqualizer();
    }

    private void initSeekBar() {
        sbar = (SeekBar) findViewById(R.id.seekBar);
        updateSeekBar = new Thread() {
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                sbar.setMax(totalDuration);

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        sbar.setProgress(currentPosition);
                    } catch (InterruptedException ex) {
                        toastMsg(ex.getMessage());
                        return;
                    }
                }
            }
        };

        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void initButtons() {
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrev = (Button) findViewById(R.id.btnPrev);

        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
    }

    private void initEqualizer() {
        flat = (Button)findViewById(R.id.flat);
        flat.setOnClickListener(this);

        bass_boost = (SeekBar)findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label = (TextView) findViewById (R.id.bass_boost_label);

        sliders[0] = (SeekBar)findViewById(R.id.slider_1);
        slider_labels[0] = (TextView)findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar)findViewById(R.id.slider_2);
        slider_labels[1] = (TextView)findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar)findViewById(R.id.slider_3);
        slider_labels[2] = (TextView)findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar)findViewById(R.id.slider_4);
        slider_labels[3] = (TextView)findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar)findViewById(R.id.slider_5);
        slider_labels[4] = (TextView)findViewById(R.id.slider_label_5);

        eq = new Equalizer (0, 0);
        if (eq != null)
        {
            eq.setEnabled (true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
                int[] freq_range = eq.getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(this);
                slider_labels[i].setText (formatBandLabel (freq_range));
            }
        }
        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
        {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost (0, 0);
        if (bb != null)
        {
        }
        else
        {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }

        updateUI();
    }


    /*=============================================================================
        formatBandLabel
    =============================================================================*/
    public String formatBandLabel (int[] band)
    {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }


    /*=============================================================================
        milliHzToString
    =============================================================================*/
    public String milliHzToString (int milliHz)
    {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    /*=============================================================================
    updateUI
=============================================================================*/
    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
    }


    /*=============================================================================
        updateSliders
    =============================================================================*/
    public void updateSliders ()
    {
        for (int i = 0; i < num_sliders; i++)
        {
            int level;
            if (eq != null)
                level = eq.getBandLevel ((short)i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        }
    }

    /*=============================================================================
        updateBassBoost
    =============================================================================*/
    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnPlay :
            {
                if (mediaPlayer.isPlaying()) {
                    btnPlay.setText(">");
                    mediaPlayer.pause();
                } else {
                    btnPlay.setText("||");
                    mediaPlayer.start();
                }
                break;
            }
            case R.id.btnNext :
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (++position >= songFromSD.size()) {
                    position = 0;
                }
                uri = Uri.parse(songFromSD.get(position).toString());
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.start();
                break;
            }
            case R.id.btnPrev :
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position - 1 < 0) ? songFromSD.size() - 1 : position - 1;
                uri = Uri.parse(songFromSD.get(position).toString());
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.start();
                break;
            }
        }

        // equalizer
        if (view == (View) flat)
        {
            setFlat();
        }
    }


    /*=============================================================================
        setFlat
    =============================================================================*/
    public void setFlat ()
    {
        if (eq != null)
        {
            for (int i = 0; i < num_sliders; i++)
            {
                eq.setBandLevel ((short)i, (short)0);
            }
        }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI();
    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int level,
                                  boolean fromTouch)
    {
        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0 ? true : false);
            bb.setStrength ((short)level); // Already in the right range 0-1000
        }
        else if (eq != null)
        {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
