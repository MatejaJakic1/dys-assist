package com.example.sldapp.Dyslexia

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.example.sldapp.R
import com.example.sldapp.Dyslexia.FlashCardInit.DECK_SIZE
import com.example.sldapp.Dyslexia.FlashCardInit.KNOWN_CARDS_COUNT
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.ArrayList

class FlashcardActivity : AppCompatActivity() {

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var frontAnimation : AnimatorSet
    private lateinit var backAnimation : AnimatorSet
    private lateinit var frontNoAnimation : AnimatorSet
    private lateinit var backNoAnimation : AnimatorSet
    private lateinit var mediaPlayerRight : MediaPlayer
    private lateinit var mediaPlayerWord: MediaPlayer
    private lateinit var mediaPlayerSentence : MediaPlayer
    private lateinit var mediaPlayerWrong : MediaPlayer

    private var mFlashCardList : ArrayList<FlashCard>? = null
    private var mCurrentFlashCardPos: Int = 1
    private var mCurrentDeckLength : Int = 0
    private var maxDeckLength : Int = 0
    private var mKnownWordsCount : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        val scale : Float = applicationContext.resources.displayMetrics.density
        val cardFront = findViewById<ConstraintLayout>(R.id.constraint_flashcard_front)
        val cardBack = findViewById<LinearLayout>(R.id.linear_flashcard_back)
        val secondIntent = Intent(this, FlashCardResultActivity::class.java)

        fireBaseInit()
        animationInit()
        mFlashCardList = FlashCardInit.getFlashCards(applicationContext)
        maxDeckLength = intent.getIntExtra(DECK_SIZE, 0)
        setFlashCards()

        findViewById<FloatingActionButton>(R.id.button_flashcard_check_right).setOnClickListener{
            mediaPlayerSentence.stop()
            mediaPlayerRight = MediaPlayer.create(this, R.raw.audio_clapping_shorter)
            mediaPlayerRight.start()
                if(mCurrentDeckLength == maxDeckLength) {
                    val result = (mKnownWordsCount.toFloat() / maxDeckLength.toFloat()) * 100
                    secondIntent.putExtra(KNOWN_CARDS_COUNT, result.toInt())
                    val flashCardID = database.push().key
                    if (flashCardID != null) {
                        database.child(flashCardID).child("score").setValue(result.toInt())
                    }
                    startActivity(secondIntent)
                    overridePendingTransition(0, 0)
                    finish()
                } else {
                mKnownWordsCount++
                setFlashCards()
                cardBack.visibility = View.GONE
                frontNoAnimation.setTarget(cardBack)
                backNoAnimation.setTarget(cardFront)
                backNoAnimation.start()
                frontNoAnimation.start()
            }
        }

        findViewById<FloatingActionButton>(R.id.button_flashcard_check_wrong).setOnClickListener{
            mediaPlayerSentence.stop()
            mediaPlayerWrong = MediaPlayer.create(this, R.raw.audio_wrong_answer1)
            mediaPlayerWrong.start()
            if(mCurrentDeckLength == maxDeckLength) {
                val result = (mKnownWordsCount.toFloat() / maxDeckLength.toFloat()) * 100
                val flashCardID = database.push().key
                if (flashCardID != null) {
                    database.child(flashCardID).child("score").setValue(result.toInt())
                }
                secondIntent.putExtra(KNOWN_CARDS_COUNT, result.toInt())
                startActivity(secondIntent)
                overridePendingTransition(0, 0)
                finish()
            } else {
                setFlashCards()
                cardBack.visibility = View.GONE
                frontNoAnimation.setTarget(cardBack)
                backNoAnimation.setTarget(cardFront)
                backNoAnimation.start()
                frontNoAnimation.start()
            }
        }

        cardBack.visibility = View.GONE
        cardFront.cameraDistance = 8000 * scale
        cardBack.cameraDistance = 8000 * scale

        findViewById<Button>(R.id.button_flashcard_front).setOnClickListener {
            mediaPlayerWord.stop()
            cardBack.visibility = View.VISIBLE
            frontAnimation.setTarget(cardFront)
            backAnimation.setTarget(cardBack)
            frontAnimation.start()
            backAnimation.start()
            mediaPlayerSentence.start()
        }

        findViewById<ImageButton>(R.id.image_button_flashcard_sound).setOnClickListener {
            mediaPlayerSentence.start()
        }
    }

    private fun setFlashCards(){
        mCurrentDeckLength++
        mCurrentFlashCardPos = (1..12).random()
        val flashcard = mFlashCardList!![mCurrentFlashCardPos-1]
        val flashCardWord = findViewById<TextView>(R.id.text_flashcard_front)
        val flashCardImage = findViewById<ImageView>(R.id.image_flashcard_back)
        val flashCardSentence = findViewById<TextView>(R.id.text_flashcard_back)
        mediaPlayerWord = MediaPlayer.create(this, flashcard.ttsWord)
        mediaPlayerSentence = MediaPlayer.create(this, flashcard.ttsSentence)
        if(flashcard.backSentenceFP.equals(flashcard.frontWord, true)){
            flashCardSentence.text = HtmlCompat.fromHtml("<b>"+ flashcard.backSentenceFP + "</b>" + flashcard.backSentenceSP, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }else{
            flashCardSentence.text = HtmlCompat.fromHtml( flashcard.backSentenceFP +"<b>"+ flashcard.backSentenceSP+"</b>" + flashcard.backSentenceTP, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        flashCardImage.setImageResource(flashcard.backImage)
        flashCardWord.text = flashcard.frontWord
        mediaPlayerWord.start()
    }

    private fun animationInit(){
        frontNoAnimation = AnimatorInflater.loadAnimator(applicationContext,
            R.animator.no_animation_front
        ) as AnimatorSet
        backNoAnimation = AnimatorInflater.loadAnimator(applicationContext,
            R.animator.no_animation_back
        ) as AnimatorSet
        frontAnimation = AnimatorInflater.loadAnimator(applicationContext,
            R.animator.animation_card_front
        ) as AnimatorSet
        backAnimation = AnimatorInflater.loadAnimator(applicationContext,
            R.animator.animation_card_back
        ) as AnimatorSet
    }

    private fun fireBaseInit(){
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            val user = firebaseAuth.currentUser!!.uid
            database = FirebaseDatabase.getInstance("https://sld-project-default-rtdb.europe-west1.firebasedatabase.app//").getReference("User").child(user).child("Dyslexia")
        }
    }
}