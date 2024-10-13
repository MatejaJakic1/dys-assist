package com.example.sldapp.Dyscalculia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import com.example.sldapp.R
import com.example.sldapp.Dyscalculia.QuestionGenerator.QUESTION_NUMBER
import com.example.sldapp.MainActivity


class DyscalculiaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dyscalculia)

        val fifteenCardsRB = findViewById<RadioButton>(R.id.radio_button_dyscalculia_fifteen)
        val twentyCardsRB = findViewById<RadioButton>(R.id.radio_button_dyscalculia_twenty)
        findViewById<Button>(R.id.button_dyscalculia_back).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()

        }
        findViewById<Button>(R.id.button_dyscalculia_begin).setOnClickListener {
            val intent = Intent(this, QuizQuestionsActivity::class.java)
            if(twentyCardsRB.isChecked){
                intent.putExtra(QUESTION_NUMBER, 20)
            }else if(fifteenCardsRB.isChecked){
                intent.putExtra(QUESTION_NUMBER, 15)
            }else{
                intent.putExtra(QUESTION_NUMBER, 10)
            }
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
    }
}