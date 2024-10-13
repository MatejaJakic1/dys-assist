package com.example.sldapp.ADHD

import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sldapp.MainActivity
import com.example.sldapp.Preferences.PrefUtilAdhd
import com.example.sldapp.Preferences.PrefUtilNotification
import com.example.sldapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

import java.util.*


class AdhdActivity : AppCompatActivity() {

    enum class TimerState{
        Stopped, Paused, Running
    }

    companion object{
        fun setAlarm(context : Context, nowSeconds : Long, secondsRemaining : Long) : Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtilAdhd.setAlarmSetTime(nowSeconds, context)
            return(wakeUpTime)
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.cancel(pendingIntent)
            PrefUtilAdhd.setAlarmSetTime(0, context)
        }
        val nowSeconds : Long
        get() = Calendar.getInstance().timeInMillis / 1000
    }

    private lateinit var timer : CountDownTimer
    private var timerLengthSeconds = 1500L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private var toDoList = mutableListOf<ToDoList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adhd)

        createNotificationChannel()
        fireBaseInit()
        val toDoRecycler = findViewById<RecyclerView>(R.id.recycler_adhd)
        val addButton = findViewById<Button>(R.id.button_adhd_add)
        val startButton = findViewById<FloatingActionButton>(R.id.floating_adhd_start)
        val pauseButton = findViewById<FloatingActionButton>(R.id.floating_adhd_pause)
        val stopButton = findViewById<FloatingActionButton>(R.id.floating_adhd_stop)

        startButton.setOnClickListener{
            startTimer()
            if(getIfTextViewClicked()== 1) {
                timerState = TimerState.Running
                updateButtons()
            }
        }

        pauseButton.setOnClickListener {
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        stopButton.setOnClickListener {
            if(timerState == TimerState.Running){
                timer.cancel()
                onTimerFinished()
            }else{
                onTimerFinished()
            }
        }


        toDoRecycler.layoutManager = LinearLayoutManager(this)
        toDoRecycler.adapter = ToDoRecyclerAdapter(toDoList,choiceListener)

        addButton.setOnClickListener {
            val window = PopupWindow(this)
            val popUpView = layoutInflater.inflate(R.layout.todo_popup, null)
            window.contentView = popUpView
            window.width = (800)
            window.height = (800)
            window.setFocusable(true)
            window.update()
            window.showAsDropDown(addButton)

            val enteredTask = popUpView.findViewById<EditText>(R.id.text_adhd_pop_up)
            val enteredRating = popUpView.findViewById<RatingBar>(R.id.rating_adhd_pop_up)

            popUpView.findViewById<Button>(R.id.button_adhd_pop_up_add).setOnClickListener {
                window.dismiss()
                val toDo = ToDoList(enteredTask.text.toString(), enteredRating.rating.toString())
                toDoList.add(toDo)
               val toDoID = database.push().key
                if (toDoID != null) {
                    database.child(toDoID).setValue(toDo)
                }
            }
        }

        val broadcastState = PrefUtilNotification.getBroadcastCompleted(this)
        if(broadcastState){
            PrefUtilNotification.setBroadcastCompleted(false, this)
            onTimerFinished()
        }

        findViewById<Button>(R.id.button_adhd_back).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }


        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    toDoList.clear()
                    for (todoSnapshot in p0.children) {
                        val toDoFirst = todoSnapshot.getValue(ToDoList::class.java)
                        toDoList.add(toDoFirst!!)
                    }
                    toDoRecycler.adapter = ToDoRecyclerAdapter(toDoList,choiceListener)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        removeAlarm(this)
    }

    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.Running){
            timer.cancel()
            setAlarm(this, nowSeconds, secondsRemaining)
        }

        PrefUtilAdhd.setSecondsRemaining(secondsRemaining, this)
        PrefUtilAdhd.setTimerState(timerState, this)
    }

    private fun initTimer(){
        val progressCountdown = findViewById<MaterialProgressBar>(R.id.progress_adhd_countdown)
        timerState = PrefUtilAdhd.getTimerState(this)
        progressCountdown.max = timerLengthSeconds.toInt()
        secondsRemaining = if(timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtilAdhd.getSecondsRemaining(this)
        else timerLengthSeconds


        val alarmSetTime = PrefUtilAdhd.getAlarmSetTime(this)
        if(alarmSetTime>0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if(secondsRemaining <= 0){
            onTimerFinished()
        }
        else if(timerState == TimerState.Running){
            startTimer()
        }
        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished(){
        val progressCountdown = findViewById<MaterialProgressBar>(R.id.progress_adhd_countdown)
        timerState = TimerState.Stopped
        progressCountdown.max = timerLengthSeconds.toInt()
        var pomodoroValueChecker = false

        if(secondsRemaining <= 0){
            val position = getClickedTextViewName()
            Toast.makeText(this, "Uspješno odrađen Pomodoro za obavezu: " + position, Toast.LENGTH_SHORT).show()
            val mediaPlayer = MediaPlayer.create(this, R.raw.audio_clapping)
            mediaPlayer.start()

            val query : Query = database.orderByChild("toDoName").equalTo(position)
                query.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot){
                        for(toDoSnapshot in p0.children) {
                            val toDoPomodoroDone = toDoSnapshot.getValue(ToDoList::class.java)
                            val newPomodoroState = toDoPomodoroDone!!.pomodoroCount.toFloat() - 1
                            if(!pomodoroValueChecker) {
                                if(newPomodoroState.toInt() == 0){
                                    toDoSnapshot.ref.removeValue().addOnCompleteListener{
                                        toDoList.clear()
                                        pomodoroValueChecker = true
                                    }
                                }else{
                                    toDoSnapshot.ref.child("pomodoroCount")
                                    .setValue(newPomodoroState.toString())
                                    pomodoroValueChecker = true
                                }
                            }
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value.")
                    }
                })
            }

        setIfTextViewClicked(0)
        progressCountdown.progress = 0
        PrefUtilAdhd.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds
        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Stopped
        if(getIfTextViewClicked()== 1)
           {
               timerState = TimerState.Running
               val clickedTextView = getClickedTextViewName()
               if(secondsRemaining == timerLengthSeconds ){
                   Toast.makeText(this, "Uspješno odabrano: " + clickedTextView , Toast.LENGTH_SHORT).show()
               }
               timer = object : CountDownTimer(secondsRemaining * 1000, 1000){
                   override fun onFinish() = onTimerFinished()

                   override fun onTick(millisUntilFinished: Long) {
                       secondsRemaining = millisUntilFinished / 1000
                       updateCountdownUI()
                   }
               }.start()
           } else {
               Toast.makeText(this, "Prvo kliknite na naziv obaveze pa na Play gumb", Toast.LENGTH_SHORT).show()
           }
    }

    private fun updateCountdownUI() {
        val progressCountdown = findViewById<MaterialProgressBar>(R.id.progress_adhd_countdown)
        val timerNumber = findViewById<TextView>(R.id.text_adhd_timer)
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        timerNumber.text = "$minutesUntilFinished:${if(secondsStr.length == 2) secondsStr else "0"+ secondsStr}"
        progressCountdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons(){
        val startButton = findViewById<FloatingActionButton>(R.id.floating_adhd_start)
        val pauseButton = findViewById<FloatingActionButton>(R.id.floating_adhd_pause)
        val stopButton = findViewById<FloatingActionButton>(R.id.floating_adhd_stop)
        when(timerState){
            TimerState.Running -> {
                startButton.isEnabled = false
                pauseButton.isEnabled = true
                stopButton.isEnabled = true
            }
            TimerState.Stopped ->{
                startButton.isEnabled = true
                pauseButton.isEnabled = false
                stopButton.isEnabled = false
            }
            TimerState.Paused -> {
                startButton.isEnabled = true
                pauseButton.isEnabled = false
                stopButton.isEnabled = true
            }
        }
    }
    private fun getClickedTextViewName(): String {
        return PrefUtilAdhd.getClickedTextViewName(this)
    }

    private fun setClickedTextViewName(position : String){
       PrefUtilAdhd.setClickedTextViewName(position, this)
    }

    private fun getIfTextViewClicked() : Int{
        return PrefUtilAdhd.getIfTextViewClicked(this)
    }

    private fun setIfTextViewClicked(clicked : Int){
        PrefUtilAdhd.setIfTextViewClicked(clicked, this)
    }

    val choiceListener = object: TimerToListListener {
        override fun onChoose(toDoName: String, pomodoroCount: String) {
            if(getIfTextViewClicked()== 0){
                setIfTextViewClicked(1)
                setClickedTextViewName(toDoName)
            }
        }
    }

    private fun fireBaseInit(){
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            val user = firebaseAuth.currentUser!!.uid
            database = FirebaseDatabase.getInstance("https://sld-project-default-rtdb.europe-west1.firebasedatabase.app//").getReference("User").child(user).child("Adhd")
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(PrefUtilAdhd.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}