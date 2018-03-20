package com.ioadmin.lab4_1;

import android.os.Environment;

import java.io.File;

/**
 * Created by IOAdmin on 02.10.2017.
 */

public class Config {

    private static final String NAME_HOME_DIRECTORY = "Music";

    public static final File ROOT_DIRECTORY = Environment.getExternalStorageDirectory();
    public static final File HOME_DIRECTORY = new File(
            Environment.getExternalStorageDirectory() + File.separator + NAME_HOME_DIRECTORY
    );
}
