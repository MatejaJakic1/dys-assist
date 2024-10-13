package com.example.sldapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.example.sldapp.ADHD.AdhdActivity
import com.example.sldapp.Authentication.SignInActivity
import com.example.sldapp.Dyscalculia.DyscalculiaActivity
import com.example.sldapp.Dysgraphia.DysgraphiaActivity
import com.example.sldapp.Dyslexia.DyslexiaActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.button_log_out).setOnClickListener {
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.button_main_dyslexia_begin).setOnClickListener {
            val intent = Intent(this, DyslexiaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<Button>(R.id.button_main_dyscalculia_begin).setOnClickListener {
            val intent = Intent(this, DyscalculiaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<Button>(R.id.button_main_dysgraphia_begin).setOnClickListener {
            val intent = Intent(this, DysgraphiaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<Button>(R.id.button_main_adhd_begin).setOnClickListener {
            val intent = Intent(this, AdhdActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }
    }
}