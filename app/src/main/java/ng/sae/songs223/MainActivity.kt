package ng.sae.songs223

import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();

        setContentView(R.layout.activity_main2)

        val audioUri = readAudio("test.l8f", this)

        // Declaring and Initializing
        // the MediaPlayer to play audio.mp3
        val mMediaPlayer = MediaPlayer.create(this, audioUri)

        val playButton: Button = findViewById(R.id.play_button)
        playButton.setOnClickListener{
            mMediaPlayer.start()
        }

        val pauseButton: Button = findViewById(R.id.pause_button)
        pauseButton.setOnClickListener{
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
            }
        }

        val frameImg: ImageView = findViewById(R.id.frame_img)
        val playSeconds: TextView = findViewById(R.id.play_seconds)
        val context = this

        val currentFrame = readMobileFrames("test.l8f", context, 100)
        frameImg.setImageURI(currentFrame)
        Log.v("info", "video length: " + getVideoLength("test.l8f", this).toString())
        CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                var seconds =
                    TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.currentPosition.toLong()).toInt()
                var currentFrame = readMobileFrames("test.l8f", context, seconds)
                val bmOptions = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeFile(currentFrame.path, bmOptions)
                CoroutineScope(Dispatchers.Main).launch {
                    playSeconds.text = seconds.toString()
                    frameImg.setImageBitmap(bitmap)
                }
                delay(1000)
            }
        }
    }
}