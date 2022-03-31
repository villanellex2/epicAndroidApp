package ru.edubinskaya.epics.app.channelaccess;

import gov.aps.jca.CAStatus;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

/**
 * Implementation of get listener.
 */
public class GetListenerImpl implements GetListener {
    private DBR value = null;
    private CAStatus status = null;

    /**
     * @see GetListener#getCompleted(gov.aps.jca.event# GetEvent )
     */
    public synchronized void getCompleted(GetEvent ev) {
        status = ev.getStatus();
        value = ev.getDBR();

        // notify retrival
        this.notifyAll();
    }

    public CAStatus getStatus() {
        return status;
    }

    public DBR getValue() {
        return value;
    }
}
