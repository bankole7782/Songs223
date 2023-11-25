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
import java.io.File
import java.util.concurrent.TimeUnit


class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();

        setContentView(R.layout.activity_player)

        val folder = intent.getStringExtra("folder")
        val song = intent.getStringExtra("song")
        val songFile = File(getExternalFilesDir(""), "$folder/$song")
        val audioUri = readAudio(songFile, this)

        val songPath: TextView = findViewById(R.id.song_short_path)
        songPath.text = "$folder/$song"

        // Declaring and Initializing
        // the MediaPlayer to play audio.mp3
        if (globalMediaPlayer == null)  {
            val mMediaPlayer = MediaPlayer.create(this, audioUri)
            globalMediaPlayer = mMediaPlayer
        } else {
            globalMediaPlayer!!.stop()
            globalMediaPlayer!!.release()

            val mMediaPlayer = MediaPlayer.create(this, audioUri)
            globalMediaPlayer = mMediaPlayer
        }

        val playButton: Button = findViewById(R.id.play_button)
        playButton.setOnClickListener{
            globalMediaPlayer!!.start()
        }

        val pauseButton: Button = findViewById(R.id.pause_button)
        pauseButton.setOnClickListener{
            if (globalMediaPlayer!!.isPlaying) {
                globalMediaPlayer!!.pause()
            }
        }

        val frameImg: ImageView = findViewById(R.id.frame_img)
        val playSeconds: TextView = findViewById(R.id.play_seconds)
        val context = this

        val currentFrame = readMobileFrames(songFile, context, 0)
        frameImg.setImageURI(currentFrame)
        Log.v("info", "video length: " + getVideoLength(songFile, this).toString())
        CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                var seconds =
                    TimeUnit.MILLISECONDS.toSeconds(globalMediaPlayer!!.currentPosition.toLong()).toInt()
                var currentFrame = readMobileFrames(songFile, context, seconds)
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