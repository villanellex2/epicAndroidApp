package ru.edubinskaya.epics.app.channelaccess;

import static ru.edubinskaya.epics.app.view.SecondFragmentKt.PV_NAME_DEVICE_FIELD;
import static ru.edubinskaya.epics.app.view.SecondFragmentKt.PV_TYPE_DEVICE_TYPE;
import static ru.edubinskaya.epics.app.view.SecondFragmentKt.PV_VALUE_CHANGED;
import static ru.edubinskaya.epics.app.view.SecondFragmentKt.PV_VALUE_DEVICE_FIELD;

import android.content.Context;
import android.content.Intent;

import gov.aps.jca.CAStatus;
import gov.aps.jca.dbr.DOUBLE;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;
import ru.edubinskaya.epics.app.model.DeviceField;

/**
 * Implementation of monitor listener.
 */
class MonitorListenerImpl implements MonitorListener {

    DeviceField mField;
    Context mContext;

    public MonitorListenerImpl(DeviceField field, Context context) {
        mContext = context;
        mField = field;
    }

    /**
     * @see MonitorListener#monitorChanged(gov.aps.jca.event.MonitorEvent)
     */
    public void monitorChanged(MonitorEvent event) {
        if (event.getStatus() == CAStatus.NORMAL) {
            Intent intent = new Intent();
            intent.setAction(PV_VALUE_CHANGED);
            switch (mField.getFieldType()) {
                case DOUBLE_VALUE:
                case BOOLEAN_VALUE:
                    intent.putExtra(PV_VALUE_DEVICE_FIELD, ((DOUBLE) event.getDBR()).getDoubleValue()[0]);
                    break;
                case BOOLEAN_SET:
                    break;
            }
            intent.putExtra(PV_NAME_DEVICE_FIELD, mField.getFieldName());
            intent.putExtra(PV_TYPE_DEVICE_TYPE, mField.getFieldType().ordinal());
            mContext.sendBroadcast(intent);
        }
    }
}
