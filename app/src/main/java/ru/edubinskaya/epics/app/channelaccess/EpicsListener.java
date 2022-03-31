package ru.edubinskaya.epics.app.channelaccess;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import ru.edubinskaya.epics.app.json.Field;

public class EpicsListener {

    public static EpicsListener instance = new EpicsListener();
    private EpicsListener(){
        initializeContext(MAX_INITIALIZE_TRY);
    };
    private static final int MAX_INITIALIZE_TRY = 5;
    private static final Context context = initializeContext(MAX_INITIALIZE_TRY);

    private static Context initializeContext(int maxTry) {
        if (maxTry <= 0) return null;
        JCALibrary jca = JCALibrary.getInstance();
        try {
            return jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        } catch (CAException exception) {
            initializeContext(maxTry-1);
        }
        return null;
    }

    public void destroy() {
        if (context == null) return;
        try {
            context.flushIO();
            context.destroy();
        } catch (Throwable ignored) {
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void execute(Field field) {
        if (context == null) initializeContext(MAX_INITIALIZE_TRY);
        if (context == null) return;
        new AsyncTask<Object, Object, DBR>() {
            @Override
            protected DBR doInBackground(Object[] objects) {
                DBR result = null;
                try {
                    Channel channel = context.createChannel(field.getPrefix() + ":" + field.getFieldName());
                    context.pendIO(5.0);

                    GetListenerImpl listener = new GetListenerImpl();
                    channel.get(listener);
                    synchronized (listener) {
                        context.flushIO();
                        listener.wait(7000);
                    }

                    if (listener.getStatus() == CAStatus.NORMAL)
                        listener.getValue().printInfo();
                    else
                        System.err.println("Get error: " + listener.getStatus());

                    field.setChannel(channel);
                    field.setMonitor(channel.addMonitor(Monitor.VALUE, field.getMonitorListener()));
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                return result;
            }
        }.execute();
    }
}
