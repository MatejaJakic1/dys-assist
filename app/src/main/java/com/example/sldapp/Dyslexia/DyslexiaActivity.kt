package com.example.sldapp.Dyslexia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import com.example.sldapp.R
import com.example.sldapp.Dyslexia.FlashCardInit.DECK_SIZE
import com.example.sldapp.MainActivity


class DyslexiaActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dyslexia)

        val fifteenCardsRB = findViewById<RadioButton>(R.id.radio_button_cards_fifteen)
        val twentyCardsRB = findViewById<RadioButton>(R.id.radio_button_cards_twenty)

        findViewById<Button>(R.id.button_dyslexia_begin).setOnClickListener {
            val intent = Intent(this, FlashcardActivity::class.java)
            if(twentyCardsRB.isChecked){
                intent.putExtra(DECK_SIZE, 20)
            }else if(fifteenCardsRB.isChecked){
                intent.putExtra(DECK_SIZE, 15)
            }else{
                intent.putExtra(DECK_SIZE, 10)
            }
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<Button>(R.id.button_dyslexia_back).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
    }
}