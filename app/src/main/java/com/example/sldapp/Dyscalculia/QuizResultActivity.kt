package com.example.sldapp.Dyscalculia

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sldapp.Dyscalculia.QuestionGenerator.QUIZ_RESULT
import com.example.sldapp.R
import com.example.sldapp.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class QuizResultActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var user : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        fireBaseInit()
        val scoreList = ArrayList<Int>()
        val currentScore = findViewById<TextView>(R.id.text_quiz_result_current_score_sample)
        val bestScore = findViewById<TextView>(R.id.text_quiz_result_best_score_sample)

        val quizResult = intent.getIntExtra(QUIZ_RESULT, 0)
        currentScore.text = "$quizResult%"

        database.addValueEventListener(object: ValueEventListener {
         override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for (score in p0.children) {
                        scoreList.add(score.child("score").value.toString().toInt())
                    }
                    val max = scoreList.maxOrNull() ?: 0
                    bestScore.text = "$max%"
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.")
            }
        })

        findViewById<Button>(R.id.button_quiz_result_play_again).setOnClickListener{
            val intent = Intent(this, DyscalculiaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<Button>(R.id.button_quiz_result_home_page).setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
    }

    private fun fireBaseInit(){
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            user = firebaseAuth .currentUser!!.uid
            database = FirebaseDatabase.getInstance("https://sld-project-default-rtdb.europe-west1.firebasedatabase.app//").getReference("User").child(user).child("Dyscalculia")
        }
    }
}