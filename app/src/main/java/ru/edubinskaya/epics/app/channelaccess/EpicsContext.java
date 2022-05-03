package ru.edubinskaya.epics.app.channelaccess;

import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

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
