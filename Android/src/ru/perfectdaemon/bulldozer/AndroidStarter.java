package ru.perfectdaemon.bulldozer;

import android.app.Activity;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.*;

public class AndroidStarter extends AndroidApplication
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        //cfg.useWakelock = true;
        cfg.useGL20 = true;
        initialize(new BulldozerGame(), cfg);
    }
}
