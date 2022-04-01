package ru.edubinskaya.epics.app.channelaccess;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import ru.edubinskaya.epics.app.json.fields.Field;

public class EpicsContext {

    public static final Context context = initializeContext();

    public static Context initializeContext() {
        JCALibrary jca = JCALibrary.getInstance();
        try {
            return jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        } catch (CAException exception) { }
        return null;
    }
}
