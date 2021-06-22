package com.example.readproject.shelfchildFragment;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.readproject.R;
import com.example.readproject.dao.BookShelfDao;
import com.example.readproject.database.DatabaseHelper;
import com.example.readproject.database.TickDBAdapter;
import com.example.readproject.util.CountDownTimer;
import com.example.readproject.util.Sound;
import com.example.readproject.util.TimeFormatUtil;
import com.example.readproject.util.WakeLockHelper;

import java.util.concurrent.TimeUnit;

public class TickService extends Service implements CountDownTimer.OnCountDownTickListener {
    public static final String ACTION_COUNTDOWN_TIMER =
            "com.example.readproject.COUNTDOWN_TIMER";
    public static final String ACTION_START = "com.example.readproject.ACTION_START";
    public static final String ACTION_PAUSE = "com.example.readproject.ACTION_PAUSE";
    public static final String ACTION_RESUME = "com.example.readproject.ACTION_RESUME";
    public static final String ACTION_STOP = "com.example.readproject.ACTION_STOP";
    public static final String ACTION_TICK = "com.example.readproject.ACTION_TICK";
    public static final String ACTION_FINISH = "com.example.readproject.ACTION_FINISH";
    public static final String ACTION_AUTO_START
            = "com.example.readproject.ACTION_AUTO_START";
    public static final String ACTION_TICK_SOUND_ON =
            "com.example.readproject.ACTION_TICK_SOUND_ON";
    public static final String ACTION_TICK_SOUND_OFF =
            "com.example.readproject.ACTION_TICK_SOUND_OFF";
    public static final String ACTION_POMODORO_MODE_ON =
            "com.example.readproject.ACTION_POMODORO_MODE_OFF";

    public static final String MILLIS_UNTIL_FINISHED = "MILLIS_UNTIL_FINISHED";
    public static final String REQUEST_ACTION = "REQUEST_ACTION";
    public static final int NOTIFICATION_ID = 1;
    public static final String WAKELOCK_ID = "tick_wakelock";
    public static final String CHANNEL_ONE_ID = "com.primedu.cn";
    public static final String CHANNEL_ONE_NAME = "Channel One";
    //public static final NotificationChannel notificationChannel = null;

    private CountDownTimer mTimer;
    private TickApplication mApplication;
    private WakeLockHelper mWakeLockHelper;
    private TickDBAdapter mDBAdapter;
    private Sound mSound;
    private long mID;
    private Integer bookID;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                mWakeLockHelper.release();
            } else if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                mWakeLockHelper.acquire(getApplicationContext());
            }
        }
    };

    public static Intent newIntent(Context context,Integer bookID) {
        Intent intent = new Intent(context,TickService.class);
        intent.putExtra("bookID",bookID);
        return intent;
    }

    public TickService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TickService","onCreate");
        mApplication = (TickApplication)getApplication();
        mWakeLockHelper = new WakeLockHelper(WAKELOCK_ID);
        mDBAdapter = new TickDBAdapter(getApplicationContext());
        mSound = new Sound(getApplicationContext());
        mID = 0;
        bookID = 0;

        mDBAdapter.open();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            bookID = intent.getIntExtra("bookID",0);
            String action = intent.getAction();
            Integer iiii = mApplication.getScene();
            Log.d("TickService","onStartCommand" + iiii.toString());
            switch (action) {
                case ACTION_AUTO_START:
                case ACTION_START:
                    stopTimer();

                    // 自动专注
                    if (action.equals(ACTION_AUTO_START)) {
                        Log.d("TickService","ACTION_AUTO_START");
                        Intent broadcastIntent = new Intent(ACTION_COUNTDOWN_TIMER);
                        broadcastIntent.putExtra(REQUEST_ACTION, ACTION_AUTO_START);
                        sendBroadcast(broadcastIntent);

                        mApplication.start();
                    }

                    long millsInTotal = getMillsInTotal();
                    Log.d("TickServiceMillsInTotal", String.valueOf(millsInTotal));
                    mTimer = new CountDownTimer(millsInTotal);
                    mTimer.setOnChronometerTickListener(this);
                    mTimer.start();

                    mSound.play();

                    NotificationCompat.Builder builder_start = getNotification(getNotificationTitle(),formatTime(millsInTotal));
                    startForeground(NOTIFICATION_ID, builder_start.build());

                    if (mApplication.getScene() == TickApplication.SCENE_WORK) {
                        // 插入数据
                        mDBAdapter.open();
                        mID = mDBAdapter.insert(bookID,mTimer.getStartTime(),mTimer.getMinutesInFuture());
                        mDBAdapter.close();
                    }
                    break;
                case ACTION_PAUSE:
                    if (mTimer != null) {
                        mTimer.pause();

                        String text = getResources().getString(R.string.notification_time_left)
                                + " " + intent.getStringExtra("time_left");

                        NotificationCompat.Builder builder_pause = getNotification(getNotificationTitle(), text);
                        getNotificationManager().notify(NOTIFICATION_ID, builder_pause.build());
                    }

                    mSound.pause();
                    break;
                case ACTION_RESUME:
                    if (mTimer != null) {
                        mTimer.resume();
                    }

                    mSound.resume();
                    break;
                case ACTION_STOP:
                    stopTimer();
                    mSound.stop();
                    break;
                case ACTION_TICK_SOUND_ON:
                    if (mTimer != null && mTimer.isRunning()) {
                        mSound.play();
                    }
                    break;
                case ACTION_TICK_SOUND_OFF:
                    mSound.stop();
                    break;
                case ACTION_POMODORO_MODE_ON:
                    // 如果处于暂停状态，但番茄模式设置为 on ,停止番茄时钟
                    if (mApplication.getState() == TickApplication.STATE_PAUSE) {
                        if (mTimer != null) {
                            mTimer.resume();
                            mSound.resume();
                            mApplication.resume();
                        }
                    }
                    break;
                case ACTION_FINISH:
                    if (mTimer != null){
                        Log.d("TickService","ACTION_FINISH123123123");
                        mTimer.onFinish();
                    }
                    mSound.stop();
                    break;
            }
        }

        return START_STICKY;
    }

    private void stopTimer() {
        if (isNotificationOn()) {
            cancelNotification();
        }
        if (mTimer != null) {
            Log.d("TickService","stopTimer");
            mTimer.cancel();
            mTimer = null;
            stopForeground(true);
        }
        mWakeLockHelper.release();
    }

    private long getMillsInTotal() {
        int minutes = mApplication.getMinutesInTotal();
        return TimeUnit.MINUTES.toMillis(minutes);
    }

    @Override
    public void onCountDownTick(long millisUntilFinished) {
        mApplication.setMillisUntilFinished(millisUntilFinished);

        Intent intent = new Intent(ACTION_COUNTDOWN_TIMER);
        intent.putExtra(MILLIS_UNTIL_FINISHED, millisUntilFinished);
        intent.putExtra(REQUEST_ACTION, ACTION_TICK);
        sendBroadcast(intent);

        NotificationCompat.Builder builder =
                getNotification(getNotificationTitle(), formatTime(millisUntilFinished));
        getNotificationManager().notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onCountDownFinish() {
        Log.d("TickService","onCountDownFinish");
        Integer iiii = mApplication.getScene();
        Log.d("onCountDownFinish",iiii.toString());
        Intent intent = new Intent(ACTION_COUNTDOWN_TIMER);
        intent.putExtra(REQUEST_ACTION, ACTION_FINISH);
        sendBroadcast(intent);

        NotificationCompat.Builder builder;

        Log.d("onCountDownFinish",iiii.toString());

        if (mApplication.getScene() - 1 == TickApplication.SCENE_WORK) {
            Log.d("onCountDownFinish","SCENE_WORK");
            builder = getNotification(
                    getResources().getString(R.string.notification_finish),
                    getResources().getString(R.string.notification_finish_content)
            );
            Log.d("mID", String.valueOf(mID));
            if (mID > 0) {
                // 更新数据
                Log.d("onCountDownFinish","updateData");
                mDBAdapter.open();
                boolean success = mDBAdapter.update(mID);
                mDBAdapter.close();

                if (success) {
                    long amountDurations =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getLong("pref_key_amount_durations", 0);

                    SharedPreferences.Editor editor =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .edit();
                    amountDurations += mTimer.mMillisInFuture;
                    Log.d("TickService","更新amountDurations"+amountDurations);
                    editor.putLong("pref_key_amount_durations", amountDurations);
                    editor.apply();

                    //更新Database
                    Log.d("BookShelfDao",bookID.toString());
                    BookShelfDao bookShelfDao = new BookShelfDao(this);
                    bookShelfDao.updateReadtime(bookID,amountDurations);
                }
            }
        } else {
            builder = getNotification(
                    getResources().getString(R.string.notification_break_finish),
                    getResources().getString(R.string.notification_break_finish_content)
            );
        }

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("pref_key_use_notification", true)) {

            int defaults = Notification.DEFAULT_LIGHTS;

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean("pref_key_notification_sound", true)) {
                // 结束提示音
                Uri uri = Uri.parse(
                        "android.resource://" + getPackageName() + "/" +
                                (mApplication.getScene() == TickApplication.SCENE_WORK ?
                                        R.raw.workend :
                                        R.raw.breakend));

                builder.setSound(uri);
            }

            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getBoolean("pref_key_notification_vibrate", true)) {
                defaults |= Notification.DEFAULT_VIBRATE;
                builder.setVibrate(new long[] {0, 1000});
            }

            builder.setDefaults(defaults);
        }

        builder.setLights(Color.GREEN, 1000, 1000);
        getNotificationManager().notify(NOTIFICATION_ID, builder.build());

        mApplication.finish();
        mSound.stop();

        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("pref_key_infinity_mode", false)) {
            Intent i = TickService.newIntent(getApplicationContext(),bookID);
            i.setAction(ACTION_AUTO_START);

            PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 2000, pi);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        mSound.release();
        unregisterReceiver(mIntentReceiver);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private boolean isNotificationOn() {
        Intent intent = ClockActivity.Companion.newIntent(getApplicationContext());
        PendingIntent pi = PendingIntent
                .getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void cancelNotification() {
        Intent intent = ClockActivity.Companion.newIntent(getApplicationContext());
        PendingIntent pi = PendingIntent
                .getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        pi.cancel();
        getNotificationManager().cancel(NOTIFICATION_ID);
    }

    private NotificationCompat.Builder getNotification(String title, String text) {
        Intent intent = ClockActivity.Companion.newIntent(getApplicationContext());

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_notify);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_logo));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        builder.setContentText(text);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        return builder;
    }

    private String getNotificationTitle() {
        int scene = mApplication.getScene();
        int state = mApplication.getState();
        String title;

        switch (scene) {
            case TickApplication.SCENE_WORK:
                if (state == TickApplication.STATE_PAUSE) {
                    title = getResources().getString(R.string.notification_focus_pause);
                } else {
                    title = getResources().getString(R.string.notification_focus);
                }
                break;
            default:
                if (state == TickApplication.STATE_PAUSE) {
                    title = getResources().getString(R.string.notification_break_pause);
                } else {
                    title = getResources().getString(R.string.notification_break);
                }
                break;
        }

        return title;
    }

    private String formatTime(long millisUntilFinished) {
        return getResources().getString(R.string.notification_time_left)  + " " +
                TimeFormatUtil.formatTime(millisUntilFinished);
    }
}
