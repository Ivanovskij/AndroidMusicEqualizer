package com.ioadmin.lab4_1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class CreatePlaylist extends AppCompatActivity {

    private EditText edtNamePl;
    private Button btnCreatePl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        initButtons();
        initEdt();
    }

    private void initButtons() {
        btnCreatePl = (Button) findViewById(R.id.btnCreatePl);

        btnCreatePl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtNamePl.getText().toString();
                if (name.equals("")) {
                    toastMsg("Invalid argument, name empty");
                    return;
                }

                createPlaylist(name);
            }
        });
    }

    private void createPlaylist(String name_plist) {
        File newPlist = new File(Config.HOME_DIRECTORY + File.separator + name_plist);

        if (!newPlist.exists()) {
            if (newPlist.mkdir()) {
                finish();
            } else {
                toastMsg("Cannot create playlist");
            }
        }
    }

    private void initEdt() {
        edtNamePl = (EditText) findViewById(R.id.edtNamePl);
    }

    private void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
