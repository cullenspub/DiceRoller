package com.cjd.learning.diceroller

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope(){

    lateinit var diceImage: ImageView
    lateinit var diceRollAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.text = resources.getString(R.string.roll_button_label)
        rollButton.setOnClickListener {
            displayRandomDie()
        }

        diceImage = findViewById(R.id.dice_image)
        diceImage.setBackgroundResource(R.drawable.animated_dice_oneshot)
        diceRollAnimation = diceImage.background as AnimationDrawable
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel() // cancel is extension on CoroutineScope
    }

    private fun displayRandomDie() {
        // Stop the animation if button is pressed before roll was completed
        if (diceRollAnimation.isRunning) diceRollAnimation.stop()

        // Remove the image resource so that the animated background will be visible
        diceImage.setImageResource(0)

        // Get a random dice side for the roll
        val imageResource = when (Random.nextInt(1,7)) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        // Coroutine to start animation, wait for a period of time, stop the animation and
        // make the image drawable display the result of the roll
        launch {
            var rolledDice = ResourcesCompat.getDrawable(resources,imageResource, null)!!
            diceRollAnimation.start()
            wait(1100L)
            diceRollAnimation.stop()
            diceImage.setImageDrawable(rolledDice)
        }
    }

    suspend fun wait(duration: Long) {
        withContext(Dispatchers.Main) {
            delay(duration)
        }
    }

    private fun rollDice() {
        GlobalScope.launch() {
            diceRollAnimation.start()
            val delayTime = (diceRollAnimation.getDuration(0) * diceRollAnimation.numberOfFrames).toLong()
            delay(delayTime)
            diceRollAnimation.stop()
        }
    }
}


