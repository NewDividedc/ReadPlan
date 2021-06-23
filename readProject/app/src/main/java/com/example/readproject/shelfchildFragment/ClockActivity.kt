package com.example.readproject.shelfchildFragment

import android.app.AlertDialog
import android.content.*
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.readproject.R
import com.example.readproject.util.TimeFormatUtil
import com.example.readproject.widget.RippleWrapper
import com.example.readproject.widget.TickProgressBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.clock.*
import java.util.concurrent.TimeUnit


class ClockActivity : AppCompatActivity() {

    private var mApplication: TickApplication? = null
    private var mBtnStart: Button? = null
    private var mBtnPause: Button? = null
    private var mBtnResume: Button? = null
    private var mBtnStop: Button? = null
    private var mBtnSkip: Button? = null
    private var mTextCountDown: TextView? = null
    private var mTextTimeTile: TextView? = null
    private var mProgressBar: TickProgressBar? = null
    private var mRippleWrapper: RippleWrapper? = null
    private var mLastClickTime: Long = 0
    private var bookID: Int = 0
    private var bookName: String? = null
    private var book_readTime: Long = 0

    companion object{
        fun newIntent(context: Context) : Intent {
            val intent = Intent(context,ClockActivity::class.java)
            return intent
            //context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clock)

        bookName = intent.getStringExtra("bookName")
        bookID = intent.getIntExtra("bookID",0)
        book_readTime = intent.getLongExtra("readTime",0)
        //Log.d("ClockActivity",bookName+bookID+" "+TimeUnit.MILLISECONDS.toMinutes(book_readTime))
        //amount_durations.text = resources.getString(R.string.amount_durations,TimeUnit.MILLISECONDS.toMinutes(book_readTime))
        //getSharedPreferences("data",Context.MODE_PRIVATE).edit().putLong("pref_key_amount_durations",book_readTime)
        //初始化setting
        val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
        editor.putLong("pref_key_amount_durations", book_readTime)
        editor.apply()
        title_bookName.text = bookName

        mApplication = application as TickApplication

        mBtnStart = findViewById(R.id.btn_start) as Button
        mBtnPause = findViewById(R.id.btn_pause) as Button
        mBtnResume = findViewById(R.id.btn_resume) as Button
        mBtnStop = findViewById(R.id.btn_stop) as Button
        mBtnSkip = findViewById(R.id.btn_skip) as Button
        mTextCountDown = findViewById(R.id.text_count_down) as TextView
        mTextTimeTile = findViewById(R.id.text_time_title) as TextView
        mProgressBar = findViewById(R.id.tick_progress_bar) as TickProgressBar
        mRippleWrapper = findViewById(R.id.ripple_wrapper) as RippleWrapper
        initActions()
    }

    private fun initActions() {
        updateButtons()
        mBtnStart?.setOnClickListener(View.OnClickListener {
            Log.d("ClockActivity","Start")
            val i = TickService.newIntent(applicationContext,bookID)
            i.action = TickService.ACTION_START

            //startService(i)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                startForegroundService(i);
            } else {
                startService(i);
            }
            mApplication?.start()
            updateButtons()
            updateTitle()
            updateRipple()
        })
        mBtnPause?.setOnClickListener(View.OnClickListener {
            Log.d("ClockActivity","Pause")
            val i = TickService.newIntent(applicationContext,bookID)
            i.action = TickService.ACTION_PAUSE
            i.putExtra("time_left", mTextCountDown?.getText() as String)
            //startService(i)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                startForegroundService(i);
            } else {
                startService(i);
            }
            mApplication?.pause()
            updateButtons()
            updateRipple()
        })
        mBtnResume?.setOnClickListener(View.OnClickListener {
            Log.d("ClockActivity","Resume")
            val i = TickService.newIntent(applicationContext,bookID)
            i.action = TickService.ACTION_RESUME
            //startService(i)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                startForegroundService(i);
            } else {
                startService(i);
            }
            mApplication?.resume()
            updateButtons()
            updateRipple()
        })
        mBtnStop?.setOnClickListener(View.OnClickListener {
            Log.d("ClockActivity","Stop")
            val stopChoice = arrayOf("放弃本次阅读(不记录时长)", "提前完成阅读并记录时长")
            val stop_choice_i : Int
            AlertDialog.Builder(this)
                    .setTitle("提示")
//                    .setMessage("确定要放弃当前阅读，并返回主界面吗？")
                    .setItems(stopChoice, DialogInterface.OnClickListener { dialogInterface, i ->
                        Log.d("mBtnStop",stopChoice[i])
                        when(i){
                            0 ->{
                                val i = TickService.newIntent(applicationContext,bookID)
                                i.action = TickService.ACTION_STOP
                                //startService(i)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    //android8.0以上通过startForegroundService启动service
                                    startForegroundService(i);
                                } else {
                                    startService(i);
                                }
                                mApplication?.stop()
                                reload()
                                val stopIntent = Intent()
                                setResult(RESULT_OK, stopIntent)
                                finish()
                            }
                            1 ->{
                                val editText = EditText(this)
                                AlertDialog.Builder(this)
                                    .setTitle("请输入本次阅读页数")
                                    .setView(editText)
                                    .setPositiveButton("确定",DialogInterface.OnClickListener{ dialog: DialogInterface?, which: Int ->
                                        val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                                        editor.putInt("readPages",Integer.parseInt(editText.text.toString()))
                                        Log.d("mBtnStop",editText.text.toString())
                                        editor.apply()
                                    })
                                    .show()
                                var iiii = mApplication!!.scene
                                Log.d("mBtnStop",iiii.toString())
                                val i = TickService.newIntent(applicationContext,bookID)
                                i.action = TickService.ACTION_FINISH
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    //android8.0以上通过startForegroundService启动service
                                    startForegroundService(i);
                                } else {
                                    startService(i);
                                }
                                iiii = mApplication!!.scene
                                Log.d("mBtnStop",iiii.toString())

                                iiii = mApplication!!.scene
                                Log.d("mBtnStop",iiii.toString())
                                reload()
                                mApplication?.finish()
                                Log.d("AfterAlertDialog","123123123123")
                            }
                        }
                        Log.d("AfterWhen","123123123123")
                    })
                    .setNegativeButton("取消", null)
                    .show()
            Log.d("AfterAlertDialog","123123123123")
        })
        mBtnSkip?.setOnClickListener(View.OnClickListener {
            Log.d("ClockActivity","Skip")
            val i = TickService.newIntent(applicationContext,bookID)
            i.action = TickService.ACTION_STOP
            //startService(i)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android8.0以上通过startForegroundService启动service
                startForegroundService(i);
            } else {
                startService(i);
            }
            mApplication?.skip()
            reload()
        })
        mRippleWrapper?.setOnClickListener(View.OnClickListener { view ->
            val clickTime = System.currentTimeMillis()
            if (clickTime - mLastClickTime < 500) {
                val isSoundOn = getSharedPreferences()
                    ?.getBoolean("pref_key_tick_sound", true)

                // 修改 SharedPreferences
                val editor = PreferenceManager
                    .getDefaultSharedPreferences(applicationContext).edit()
                if (isSoundOn != null) {
                    editor.putBoolean("pref_key_tick_sound", false)
                    val i = TickService.newIntent(applicationContext,bookID)
                    i.action = TickService.ACTION_TICK_SOUND_OFF
                    //startService(i)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //android8.0以上通过startForegroundService启动service
                        startForegroundService(i);
                    } else {
                        startService(i);
                    }
                    Snackbar.make(
                        view, resources.getString(R.string.toast_tick_sound_off),
                        Snackbar.LENGTH_SHORT
                    ).setAction("Action", null).show()
                } else {
                    editor.putBoolean("pref_key_tick_sound", true)
                    val i = TickService.newIntent(applicationContext,bookID)
                    i.action = TickService.ACTION_TICK_SOUND_ON
                    //startService(i)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //android8.0以上通过startForegroundService启动service
                        startForegroundService(i);
                    } else {
                        startService(i);
                    }
                    Snackbar.make(
                        view, resources.getString(R.string.toast_tick_sound_on),
                        Snackbar.LENGTH_SHORT
                    ).setAction("Action", null).show()
                }
                try {
                    editor.apply()
                } catch (unused: AbstractMethodError) {
                    editor.commit()
                }
                updateRipple()
            }
            mLastClickTime = clickTime
        })
    }

    override fun onStart() {
        super.onStart()
        reload()
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(TickService.ACTION_COUNTDOWN_TIMER)
        registerReceiver(mIntentReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mIntentReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun reload() {
        mApplication!!.reload()
        mProgressBar!!.setMaxProgress(mApplication!!.millisInTotal / 1000)
        mProgressBar!!.setProgress(mApplication!!.millisUntilFinished / 1000)
        updateText(mApplication!!.millisUntilFinished)
        updateTitle()
        updateButtons()
        updateScene()
        updateRipple()
        updateAmount()
        if (getSharedPreferences()?.getBoolean("pref_key_screen_on", false) != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun updateText(millisUntilFinished: Long) {
        mTextCountDown?.text= TimeFormatUtil.formatTime(millisUntilFinished)
    }

    private fun updateTitle() {
        if (mApplication!!.state === TickApplication.STATE_FINISH) {
            val title: String
            title = if (mApplication!!.scene === TickApplication.SCENE_WORK) {
                resources.getString(R.string.scene_title_work)
            } else {
                resources.getString(R.string.scene_title_break)
            }
            mTextTimeTile!!.text = title
            mTextTimeTile!!.visibility = View.VISIBLE
            mTextCountDown!!.visibility = View.GONE
        } else {
            mTextTimeTile!!.visibility = View.GONE
            mTextCountDown!!.visibility = View.VISIBLE
        }
    }

    private fun updateButtons() {
        val state = mApplication!!.state
        val scene = mApplication!!.scene
        Log.d("ClockActivityState", "updateButtons"+ state.toString())
        Log.d("ClockActivityScene","updateButtons"+ scene.toString())
        val isPomodoroMode = getSharedPreferences()
            ?.getBoolean("pref_key_pomodoro_mode", false)
        Log.d("pref_key_pomodoro_mode",isPomodoroMode.toString())

        // 在番茄模式下不能暂停定时器
        mBtnStart!!.visibility =
            if (state == TickApplication.STATE_WAIT || state == TickApplication.STATE_FINISH) View.VISIBLE else View.GONE
        if (isPomodoroMode == true) {
            mBtnPause!!.visibility = View.GONE
            mBtnResume!!.visibility = View.GONE
        } else {
            mBtnPause!!.visibility =
                if (state == TickApplication.STATE_RUNNING) View.VISIBLE else View.GONE
            mBtnResume!!.visibility =
                if (state == TickApplication.STATE_PAUSE) View.VISIBLE else View.GONE
        }
        if (scene == TickApplication.SCENE_WORK) {
            mBtnSkip!!.visibility = View.GONE
            if (isPomodoroMode == true) {
                mBtnStop!!.visibility = if (!(state == TickApplication.STATE_WAIT || state == TickApplication.STATE_FINISH)) View.VISIBLE else View.GONE
            } else {
                mBtnStop!!.visibility =
                    if (state == TickApplication.STATE_PAUSE) View.VISIBLE else View.GONE
            }
        } else {
            mBtnStop!!.visibility = View.GONE
            if (isPomodoroMode == true) {
                mBtnSkip!!.visibility = if (!(state == TickApplication.STATE_WAIT ||
                            state == TickApplication.STATE_FINISH)
                ) View.VISIBLE else View.GONE
            } else {
                mBtnSkip!!.visibility =
                    if (state == TickApplication.STATE_PAUSE) View.VISIBLE else View.GONE
            }
        }
    }

    fun updateScene() {
        val scene = mApplication!!.scene
        Log.d("updateScene",scene.toString())
        val workLength = getSharedPreferences()
            ?.getInt("pref_key_work_length", TickApplication.DEFAULT_WORK_LENGTH)
        val shortBreak = getSharedPreferences()
            ?.getInt("pref_key_short_break", TickApplication.DEFAULT_SHORT_BREAK)
        val longBreak = getSharedPreferences()
            ?.getInt("pref_key_long_break", TickApplication.DEFAULT_LONG_BREAK)
        (findViewById<TextView>(R.id.stage_work_value)).text =
            resources.getString(R.string.stage_time_unit, workLength)
        (findViewById<TextView>(R.id.stage_short_break_value)).text =
            resources.getString(R.string.stage_time_unit, shortBreak)
        (findViewById<TextView>(R.id.stage_long_break_value)).text =
            resources.getString(R.string.stage_time_unit, longBreak)

        findViewById<LinearLayout>(R.id.stage_work).alpha = if (scene == TickApplication.SCENE_WORK) 0.9f else 0.5f
        findViewById<LinearLayout>(R.id.stage_short_break).alpha = if (scene == TickApplication.SCENE_SHORT_BREAK) 0.9f else 0.5f
        findViewById<LinearLayout>(R.id.stage_long_break).alpha = if (scene == TickApplication.SCENE_LONG_BREAK) 0.9f else 0.5f
    }

    private fun updateRipple() {
        val isPlayOn =
            getSharedPreferences()?.getBoolean("pref_key_tick_sound", true)
        if (isPlayOn != null) {
            if (mApplication!!.state === TickApplication.STATE_RUNNING) {
                mRippleWrapper!!.start()
                return
            }
        }
        mRippleWrapper!!.stop()
    }

    private fun updateAmount() {
        var amount = getSharedPreferences()?.getLong("pref_key_amount_durations", 0)
        Log.d("updateAmount", amount.toString())
        val textView = findViewById(R.id.amount_durations) as TextView
        if (amount != null){
            textView.text = resources.getString(R.string.amount_durations, TimeUnit.MILLISECONDS.toMinutes(amount))
        }
    }

    private val mIntentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == TickService.ACTION_COUNTDOWN_TIMER) {
                val requestAction =
                    intent.getStringExtra(TickService.REQUEST_ACTION)
                when (requestAction) {
                    TickService.ACTION_TICK -> {
                        val millisUntilFinished = intent.getLongExtra(
                            TickService.MILLIS_UNTIL_FINISHED, 0
                        )
                        mProgressBar!!.setProgress(millisUntilFinished / 1000)
                        updateText(millisUntilFinished)
                    }
                    TickService.ACTION_FINISH,TickService.ACTION_AUTO_START -> reload()
                }
            }
        }
    }

    private fun getSharedPreferences(): SharedPreferences? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreferences
    }
    private fun exitApp() {
        stopService(TickService.newIntent(applicationContext,bookID))
        mApplication!!.exit()
        finish()
    }

}
