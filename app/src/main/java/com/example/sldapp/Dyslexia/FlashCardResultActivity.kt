package com.example.sldapp.Dyslexia

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.sldapp.Dyslexia.FlashCardInit.KNOWN_CARDS_COUNT
import com.example.sldapp.R
import com.example.sldapp.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FlashCardResultActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_result)

        fireBaseInit()
        val scoreList = ArrayList<Int>()
        val currentScore = findViewById<TextView>(R.id.text_flashcard_result_score_percent)
        val bestScore = findViewById<TextView>(R.id.text_flashcard_result_best_score_percent)

        val result = intent.getIntExtra(KNOWN_CARDS_COUNT, 0)
        currentScore.text = " $result%"

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

        findViewById<Button>(R.id.button_flashcard_result_back).setOnClickListener{
            val intent = Intent(this, DyslexiaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
        findViewById<Button>(R.id.button_flashcard_result_home_page).setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
    }

    private fun fireBaseInit(){
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            val user = firebaseAuth.currentUser!!.uid
            database = FirebaseDatabase.getInstance("https://sld-project-default-rtdb.europe-west1.firebasedatabase.app//").getReference("User").child(user).child("Dyslexia")
        }
    }
}