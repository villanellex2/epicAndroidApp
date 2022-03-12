package ru.edubinskaya.epics.app.channelaccess;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.STRING;
import gov.aps.jca.event.MonitorListener;
import ru.edubinskaya.epics.app.model.DeviceField;

public class BasicExample {

    private android.content.Context appContext;

    public BasicExample(android.content.Context androidContext){
        appContext = androidContext;
    }
    /**
     * JCA context.
     */
    private Context context = null;
    private List<Channel> channels = new ArrayList<>();
    private List<Monitor> monitors = new ArrayList<>();

    /**
     * Initialize JCA context.
     *
     * @throws CAException throws on any failure.
     */
    private void initialize() throws CAException {

        // Get the JCALibrary instance.
        JCALibrary jca = JCALibrary.getInstance();

        // Create a context with default configuration values.
        context = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);

        // Display basic information about the context.
        System.out.println(context.getVersion().getVersionString());
        context.printInfo();
        System.out.println();
    }

    /**
     * Destroy JCA context.
     */
    public void destroy() {
        try {
            context.flushIO();
            if (context != null)
                context.destroy();
            for (Monitor monitor : monitors) {
                monitor.clear();
            }
            for (Channel channel : channels) {
                channel.destroy();
            }
        } catch (Throwable ignored) {
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void execute(List<DeviceField> fields, String deviceName) {
        new AsyncTask<Object, Object, DBR>() {
            @Override
            protected DBR doInBackground(Object[] objects) {
                // initialize context
                DBR result = null;
                try {
                    initialize();

                    for (DeviceField channelName : fields) {
                        Channel channel = context.createChannel(deviceName + ":" + channelName.getFieldName());
                        channels.add(channel);
                    }

                    channels.add(context.createChannel(deviceName + ":" + fields.get(fields.size()-1).getFieldName()));
                    // Send the request and wait 5.0 seconds for the channel to connect
                    // to the PV.
                    context.pendIO(5.0);
                    for (int i = 0; i <= fields.size(); ++i) {

                        Channel channel = channels.get(i);
                        context.pendIO(10.0);
                        GetListenerImpl listener = new GetListenerImpl();
                        channel.get(listener);
                        synchronized (listener) {
                            // flush & get event back
                            context.flushIO();
                            // wait for response...
                            listener.wait(30000);
                        }

                        if (listener.getStatus() == CAStatus.NORMAL)
                            listener.getValue().printInfo();
                        else
                            System.err.println("Get error: " + listener.getStatus());

                        // Create a monitor
                        Monitor monitor = channel.addMonitor(Monitor.VALUE, new MonitorListenerImpl(fields.get(i), appContext));
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                return result;
            }
        }.execute();
    }
}

