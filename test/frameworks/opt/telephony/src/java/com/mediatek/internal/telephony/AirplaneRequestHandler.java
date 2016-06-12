package com.mediatek.internal.telephony;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.telephony.Rlog;
import android.net.ConnectivityManager;

import com.android.internal.telephony.CommandsInterface.RadioState;
import com.android.internal.telephony.PhoneBase;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PhoneProxy;

import com.mediatek.internal.telephony.cdma.CdmaFeatureOptionUtils;
import com.mediatek.internal.telephony.ltedc.svlte.SvlteModeController;
import com.mediatek.internal.telephony.ltedc.svlte.SvltePhoneProxy;

import java.util.concurrent.atomic.AtomicBoolean;
/**
 * This class fix the bug turn on/off flightmode frenquently.
 */
public class AirplaneRequestHandler extends Handler {
    private static final String LOG_TAG = "AirplaneRequestHandler";
    private Context mContext;
    private Boolean mPendingAirplaneModeRequest;
    private int mPhoneCount;
    private boolean mNeedIgnoreMessageForChangeDone;
    private boolean mForceSwitch;
    private static final int EVENT_LTE_RADIO_CHANGE_FOR_OFF = 100;
    private static final int EVENT_CDMA_RADIO_CHANGE_FOR_OFF = 101;
    private static final int EVENT_GSM_RADIO_CHANGE_FOR_OFF = 102;
    private static final int EVENT_LTE_RADIO_CHANGE_FOR_AVALIABLE = 103;
    private static final int EVENT_CDMA_RADIO_CHANGE_FOR_AVALIABLE = 104;
    private static final int EVENT_GSM_RADIO_CHANGE_FOR_AVALIABLE = 105;
    private static final String INTENT_ACTION_AIRPLANE_CHANGE_DONE =
            "com.mediatek.intent.action.AIRPLANE_CHANGE_DONE";
    private static final String EXTRA_AIRPLANE_MODE = "airplaneMode";

    private boolean mNeedIgnoreMessageForWait;
    private static final int EVENT_WAIT_LTE_RADIO_CHANGE_FOR_AVALIABLE = 200;
    private static final int EVENT_WAIT_CDMA_RADIO_CHANGE_FOR_AVALIABLE = 201;
    private static final int EVENT_WAIT_GSM_RADIO_CHANGE_FOR_AVALIABLE = 202;

    private static AtomicBoolean sInSwitching = new AtomicBoolean(false);

    protected boolean allowSwitching() {
        if (sInSwitching.get()  && !mForceSwitch) {
            return false;
        }
        return true;
    }

    protected void pendingAirplaneModeRequest(boolean enabled) {
        log("pendingAirplaneModeRequest, enabled = " + enabled);
        mPendingAirplaneModeRequest = new Boolean(enabled);
    }

    /**
     * The Airplane mode change request handler.
     *
     * @param context the context
     * @param phoneCount the phone count.
     */
    public AirplaneRequestHandler(Context context, int phoneCount) {
        mContext = context;
        mPhoneCount = phoneCount;
    }

    protected void monitorAirplaneChangeDone(boolean power) {
        mNeedIgnoreMessageForChangeDone = false;
        log("monitorAirplaneChangeDone, power = " + power
            + " mNeedIgnoreMessageForChangeDone = " + mNeedIgnoreMessageForChangeDone);
        sInSwitching.set(true);
        int phoneId = 0;
        for (int i = 0; i < mPhoneCount; i++) {
            phoneId = i;
            if (power) {
                if (CdmaFeatureOptionUtils.isCdmaLteDcSupport()) {
                    if (phoneId == SvlteModeController.getActiveSvlteModeSlotId()) {
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_LTE_RADIO_CHANGE_FOR_AVALIABLE, null);
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getNLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_CDMA_RADIO_CHANGE_FOR_AVALIABLE, null);
                    } else {
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_GSM_RADIO_CHANGE_FOR_AVALIABLE, null);
                    }
                } else {
                    ((PhoneBase) (((PhoneProxy) (PhoneFactory.getPhone(phoneId))).getActivePhone())
                            ).mCi.registerForRadioStateChanged(this,
                            EVENT_GSM_RADIO_CHANGE_FOR_AVALIABLE , null);
                }
            } else {
                if (CdmaFeatureOptionUtils.isCdmaLteDcSupport()) {
                    if (phoneId == SvlteModeController.getActiveSvlteModeSlotId()) {
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_LTE_RADIO_CHANGE_FOR_OFF, null);
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getNLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_CDMA_RADIO_CHANGE_FOR_OFF, null);
                    } else {
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_GSM_RADIO_CHANGE_FOR_OFF, null);
                    }
                } else {
                    ((PhoneBase) (((PhoneProxy) (PhoneFactory.getPhone(phoneId))).getActivePhone())
                            ).mCi.registerForRadioStateChanged(this, EVENT_GSM_RADIO_CHANGE_FOR_OFF
                            , null);
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        ///M: Add for wifi only device. @{
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        boolean isWifiOnly = !cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE);
        ///  @}
        switch (msg.what) {
        case EVENT_CDMA_RADIO_CHANGE_FOR_OFF:
        case EVENT_LTE_RADIO_CHANGE_FOR_OFF:
        case EVENT_GSM_RADIO_CHANGE_FOR_OFF:
            if (!mNeedIgnoreMessageForChangeDone) {
                if (msg.what == EVENT_CDMA_RADIO_CHANGE_FOR_OFF) {
                    log("handle EVENT_CDMA_RADIO_CHANGE_FOR_OFF");
                } else if (msg.what == EVENT_LTE_RADIO_CHANGE_FOR_OFF) {
                    log("handle EVENT_LTE_RADIO_CHANGE_FOR_OFF");
                } else if (msg.what == EVENT_GSM_RADIO_CHANGE_FOR_OFF) {
                    log("handle EVENT_GSM_RADIO_CHANGE_FOR_OFF");
                }
                for (int i = 0; i < mPhoneCount; i++) {
                    int phoneId = i;
                    ///M: Add for wifi only device, don't judge radio off. @{
                    if (isWifiOnly) {
                        log("wifi-only, don't judge radio off");
                        break;
                    }
                    ///  @}
                    if (!isRadioOff(phoneId)) {
                        log("radio state change, radio not off, phoneId = "
                                + phoneId);
                        return;
                    }
                }
                log("All radio off");
                sInSwitching.set(false);
                unMonitorAirplaneChangeDone(true);
                checkPendingRequest();
            }
            break;
        case EVENT_LTE_RADIO_CHANGE_FOR_AVALIABLE:
        case EVENT_CDMA_RADIO_CHANGE_FOR_AVALIABLE:
        case EVENT_GSM_RADIO_CHANGE_FOR_AVALIABLE:
            if (!mNeedIgnoreMessageForChangeDone) {
                if (msg.what == EVENT_LTE_RADIO_CHANGE_FOR_AVALIABLE) {
                    log("handle EVENT_LTE_RADIO_CHANGE_FOR_AVALIABLE");
                } else if (msg.what == EVENT_CDMA_RADIO_CHANGE_FOR_AVALIABLE) {
                    log("handle EVENT_CDMA_RADIO_CHANGE_FOR_AVALIABLE");
                } else if (msg.what == EVENT_GSM_RADIO_CHANGE_FOR_AVALIABLE) {
                    log("handle EVENT_GSM_RADIO_CHANGE_FOR_AVALIABLE");
                }
                for (int i = 0; i < mPhoneCount; i++) {
                    int phoneId = i;
                    ///M: Add for wifi only device, don't judge radio avaliable. @{
                    if (isWifiOnly) {
                        log("wifi-only, don't judge radio avaliable");
                        break;
                    }
                    ///  @}
                    if (!isRadioAvaliable(phoneId)) {
                        log("radio state change, radio not avaliable, phoneId = "
                                + phoneId);
                        return;
                    }
                }
                log("All radio avaliable");
                sInSwitching.set(false);
                unMonitorAirplaneChangeDone(false);
                checkPendingRequest();
            }
            break;
        case EVENT_WAIT_LTE_RADIO_CHANGE_FOR_AVALIABLE:
        case EVENT_WAIT_CDMA_RADIO_CHANGE_FOR_AVALIABLE:
        case EVENT_WAIT_GSM_RADIO_CHANGE_FOR_AVALIABLE:
            if (!mNeedIgnoreMessageForWait) {
                if (msg.what == EVENT_WAIT_LTE_RADIO_CHANGE_FOR_AVALIABLE) {
                    log("handle EVENT_WAIT_LTE_RADIO_CHANGE_FOR_AVALIABLE");
                } else if (msg.what == EVENT_WAIT_CDMA_RADIO_CHANGE_FOR_AVALIABLE) {
                    log("handle EVENT_WAIT_CDMA_RADIO_CHANGE_FOR_AVALIABLE");
                } else if (msg.what == EVENT_WAIT_GSM_RADIO_CHANGE_FOR_AVALIABLE) {
                    log("handle EVENT_WAIT_GSM_RADIO_CHANGE_FOR_AVALIABLE");
                }
                if (!isRadioAvaliable()) {
                    return;
                }
                log("All radio avaliable");
                unWaitRadioAvaliable();
                sInSwitching.set(false);
                checkPendingRequest();
            }
            break;
          default:
            break;
        }
    }

    private boolean isRadioAvaliable(int phoneId) {
        if (phoneId == SvlteModeController.getActiveSvlteModeSlotId()) {
            log("phoneId = " + phoneId + " , in svlte mode "
                    + " , lte radio state = "
                    + ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getLtePhone().mCi.getRadioState()
                    + " , cdma radio state = "
                    + ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getNLtePhone().mCi.getRadioState());
            return ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                    .getLtePhone().mCi.getRadioState() != RadioState.RADIO_UNAVAILABLE
                    && ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getNLtePhone().mCi.getRadioState() != RadioState.RADIO_UNAVAILABLE;
        } else {
            if (CdmaFeatureOptionUtils.isCdmaLteDcSupport()) {
                log("phoneId = " + phoneId + " , in csfb mode, lte radio state = "
                        + ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.getRadioState());
                return ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                        .getLtePhone().mCi.getRadioState() != RadioState.RADIO_UNAVAILABLE;
            } else {
                log("phoneId = " + phoneId);
                return ((PhoneBase) (((PhoneProxy) (PhoneFactory.getPhone(phoneId))).
                            getActivePhone())).mCi.getRadioState() != RadioState.RADIO_UNAVAILABLE;
            }
        }
    }

    private boolean isRadioOff(int phoneId) {
        if (phoneId == SvlteModeController.getActiveSvlteModeSlotId()) {
            log("phoneId = " + phoneId + " , in svlte mode "
                    + " , lte radio state = "
                    + ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getLtePhone().mCi.getRadioState()
                    + " , cdma radio state = "
                    + ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getNLtePhone().mCi.getRadioState());
            return ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                    .getLtePhone().mCi.getRadioState() == RadioState.RADIO_OFF
                    && ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getNLtePhone().mCi.getRadioState() == RadioState.RADIO_OFF;
        } else {
            if (CdmaFeatureOptionUtils.isCdmaLteDcSupport()) {
                log("phoneId = " + phoneId + ", in csfb mode, lte radio state = "
                        + ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.getRadioState());
                return ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                        .getLtePhone().mCi.getRadioState() == RadioState.RADIO_OFF;
            } else {
                log("phoneId = " + phoneId);
                return ((PhoneBase) (((PhoneProxy) (PhoneFactory.getPhone(phoneId))).
                            getActivePhone())).mCi.getRadioState() == RadioState.RADIO_OFF;
            }
        }
    }

    private void checkPendingRequest() {
        log("checkPendingRequest, mPendingAirplaneModeRequest = " + mPendingAirplaneModeRequest);
        if (mPendingAirplaneModeRequest != null) {
            Boolean pendingAirplaneModeRequest = mPendingAirplaneModeRequest;
            mPendingAirplaneModeRequest = null;
            RadioManager.getInstance().notifyAirplaneModeChange(
                    pendingAirplaneModeRequest.booleanValue());
        }
    }

    protected void unMonitorAirplaneChangeDone(boolean airplaneMode) {
        mNeedIgnoreMessageForChangeDone = true;
        Intent intent = new Intent(INTENT_ACTION_AIRPLANE_CHANGE_DONE);
        intent.putExtra(EXTRA_AIRPLANE_MODE, airplaneMode);
        mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        int phoneId = 0;
        for (int i = 0; i < mPhoneCount; i++) {
            phoneId = i;
            if (phoneId == SvlteModeController.getActiveSvlteModeSlotId()) {
                ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                        .getLtePhone().mCi.unregisterForRadioStateChanged(this);
                ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                        .getNLtePhone().mCi
                        .unregisterForRadioStateChanged(this);
                log("unMonitorAirplaneChangeDone, for svlte phone,  phoneId = " + phoneId);
            } else {
                if (CdmaFeatureOptionUtils.isCdmaLteDcSupport()) {
                    ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getLtePhone().mCi.unregisterForRadioStateChanged(this);
                    log("unMonitorAirplaneChangeDone, for csfb phone,  phoneId = " + phoneId);
                } else {
                    ((PhoneBase) (((PhoneProxy) (PhoneFactory.getPhone(phoneId))).getActivePhone())
                            ).mCi.unregisterForRadioStateChanged(this);
                    log("unMonitorAirplaneChangeDone, for gsm phone,  phoneId = " + phoneId);
                }
            }

        }
    }

    /**
     * Set Whether force allow airplane mode change.
     * @return true or false
     */
    public void setForceSwitch(boolean forceSwitch) {
        mForceSwitch = forceSwitch;
        log("setForceSwitch, forceSwitch =" + forceSwitch);
    }

    private static void log(String s) {
        Rlog.d(LOG_TAG, "[RadioManager] " + s);
    }

    protected boolean waitRadioAvaliable(boolean enabled) {
        ///M: Add for wifi only device. @{
        final ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        boolean isWifiOnly = !cm.isNetworkSupported(ConnectivityManager.TYPE_MOBILE);
        ///  @}
        final boolean wait = (!isWifiOnly) && CdmaFeatureOptionUtils.isCdmaLteDcSupport()
                && !isRadioAvaliable();
        log("waitRadioAvaliable, enabled=" + enabled + ", wait=" + wait
                + ", isWifiOnly=" + isWifiOnly);
        if (wait) {
            // pending
            pendingAirplaneModeRequest(enabled);

            // wait for radio avaliable
            mNeedIgnoreMessageForWait = false;
            sInSwitching.set(true);

            // register for radiostate changed
            int phoneId = 0;
            for (int i = 0; i < mPhoneCount; i++) {
                phoneId = i;
                if (CdmaFeatureOptionUtils.isCdmaLteDcSupport()) {
                    if (phoneId == SvlteModeController.getActiveSvlteModeSlotId()) {
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_WAIT_LTE_RADIO_CHANGE_FOR_AVALIABLE, null);
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getNLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_WAIT_CDMA_RADIO_CHANGE_FOR_AVALIABLE, null);
                    } else {
                        ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                                .getLtePhone().mCi.registerForRadioStateChanged(this,
                                EVENT_WAIT_GSM_RADIO_CHANGE_FOR_AVALIABLE, null);
                    }
                } else {
                    PhoneBase mPhone = (PhoneBase) (((PhoneProxy) (PhoneFactory.getPhone(phoneId)))
                            .getActivePhone());
                    mPhone.mCi.registerForRadioStateChanged(this,
                            EVENT_WAIT_GSM_RADIO_CHANGE_FOR_AVALIABLE, null);
                }
            }
        }

        return wait;
    }

    private final void unWaitRadioAvaliable() {
        mNeedIgnoreMessageForWait = true;
        int phoneId = 0;
        for (int i = 0; i < mPhoneCount; i++) {
            phoneId = i;
            if (CdmaFeatureOptionUtils.isCdmaLteDcSupport()) {
                if (phoneId == SvlteModeController.getActiveSvlteModeSlotId()) {
                    ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getLtePhone().mCi.unregisterForRadioStateChanged(this);
                    ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getNLtePhone().mCi
                            .unregisterForRadioStateChanged(this);
                    log("unWaitRadioAvaliable, for svlte phone,  phoneId = " + phoneId);
                } else {
                    ((SvltePhoneProxy) PhoneFactory.getPhone(phoneId))
                            .getLtePhone().mCi.unregisterForRadioStateChanged(this);
                    log("unWaitRadioAvaliable, for csfb phone,  phoneId = " + phoneId);
                }
            } else {
                PhoneBase mPhone = (PhoneBase) (((PhoneProxy) (PhoneFactory.getPhone(phoneId)))
                        .getActivePhone());
                mPhone.mCi.unregisterForRadioStateChanged(this);
                log("unWaitRadioAvaliable, for csfb phone,  phoneId = " + phoneId);
            }
        }
    }

    private final boolean isRadioAvaliable() {
        boolean isRadioAvaliable = true;
        for (int i = 0; i < mPhoneCount; i++) {
            int phoneId = i;
            if (!isRadioAvaliable(phoneId)) {
                isRadioAvaliable = false;
                break;
            }
        }
        return isRadioAvaliable;
    }
}