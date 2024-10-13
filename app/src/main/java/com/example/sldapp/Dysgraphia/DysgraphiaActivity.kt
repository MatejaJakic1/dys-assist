package com.example.sldapp.Dysgraphia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sldapp.R
import com.example.sldapp.MainActivity


class DysgraphiaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dysgraphia)

        findViewById<Button>(R.id.button_dysgraphia_choose_letter).setOnClickListener {
            intent = Intent(this, LetterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<Button>(R.id.button_dysgraphia_choose_word).setOnClickListener {
            intent = Intent(this, WordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<Button>(R.id.button_dysgraphia_home).setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
    }
}