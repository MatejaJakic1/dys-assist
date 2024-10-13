package com.example.sldapp.Dysgraphia

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sldapp.Preferences.PrefUtilDysgraphia
import com.example.sldapp.Dyscalculia.QuestionGenerator.LETTER_SENT
import com.example.sldapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class CanvasActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference

    lateinit var letterShown : TextView
    lateinit var firstWordShown : TextView
    lateinit var secondWordShown : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)

        var databaseCheck = false
        fireBaseInit()
        val paintView = findViewById<PaintView>(R.id.paint_view)
        hideLetterOrWords()

        findViewById<Button>(R.id.button_canvas_choose_again).setOnClickListener {
            intent = Intent(this, DysgraphiaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
        findViewById<Button>(R.id.button_canvas_delete_all).setOnClickListener{
            databaseCheck = true
            paintView.onReset()
                database.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot){
                        if(databaseCheck){
                        for(pathSnapshot in p0.children) {
                            pathSnapshot.ref.removeValue()
                          }
                            databaseCheck = false
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        Log.w(ContentValues.TAG, "Failed to read value.")
                    }
                })
        }
        findViewById<Button>(R.id.button_canvas_delete).setOnClickListener{
           paintView.onUndo()
        }
    }

    private fun hideLetterOrWords(){
        letterShown = findViewById(R.id.text_paint_letter)
        firstWordShown = findViewById(R.id.text_paint_word_first)
        secondWordShown = findViewById(R.id.text_paint_word_second)

        if(PrefUtilDysgraphia.getHideLetter(applicationContext)){
            letterShown.visibility = View.GONE

            firstWordShown.text = PrefUtilDysgraphia.getFirstClicked(applicationContext)
            secondWordShown.text = PrefUtilDysgraphia.getSecondClicked(applicationContext)
            if(PrefUtilDysgraphia.getFirstClicked(applicationContext) == "njegov" || PrefUtilDysgraphia.getFirstClicked(applicationContext) == "zašto" || PrefUtilDysgraphia.getFirstClicked(applicationContext) == "prema"){
                firstWordShown.textSize = 120F
            }
             if(PrefUtilDysgraphia.getSecondClicked(applicationContext) == "njegov" || PrefUtilDysgraphia.getSecondClicked(applicationContext) == "zašto" || PrefUtilDysgraphia.getSecondClicked(applicationContext) == "prema"){
                secondWordShown.textSize = 120F
            }
            firstWordShown.visibility = View.VISIBLE
            secondWordShown.visibility = View.VISIBLE
        }else{
            letterShown.visibility = View.VISIBLE
            firstWordShown.visibility = View.GONE
            secondWordShown.visibility = View.GONE
        }
        if(intent.getStringExtra(LETTER_SENT) != null){
            if( intent.getStringExtra(LETTER_SENT) == "Dž dž"){
                letterShown.textSize = 180F
            }
            if(intent.getStringExtra(LETTER_SENT) == "M m" || intent.getStringExtra(LETTER_SENT) == "Nj nj"){
                letterShown.textSize = 230F
            }

            letterShown.text = intent.getStringExtra(LETTER_SENT)
        }
    }

    private fun fireBaseInit(){
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            val user = firebaseAuth.currentUser!!.uid
            database = FirebaseDatabase.getInstance("https://sld-project-default-rtdb.europe-west1.firebasedatabase.app//").getReference("User").child(user).child("Dysgraphia")
        }
    }
}