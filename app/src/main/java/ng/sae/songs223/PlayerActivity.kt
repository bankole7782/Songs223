package ng.sae.songs223

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.io.File
import java.util.concurrent.TimeUnit


class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();

        setContentView(R.layout.activity_player)

        val context = this
        val folder = intent.getStringExtra("folder")
        var song = intent.getStringExtra("song")
        val resume = intent.getStringExtra("resume")
        val songFile = File(getExternalFilesDir(""), "$folder/$song")
        val audioUri = readAudio(songFile, this)

        // update display
        val folderText: TextView = findViewById(R.id.folder_name)
        folderText.text = folder
        val songText: TextView = findViewById(R.id.song_name)
        songText.text = song


        currentPlayingFolder = folder
        currentPlayingSong = song

        // Declaring and Initializing
        // the MediaPlayer to play audio.mp3
        if (globalMediaPlayer == null)  {
            val mMediaPlayer = MediaPlayer.create(this, audioUri)
            globalMediaPlayer = mMediaPlayer
        } else if (resume != "true") {
            globalMediaPlayer!!.stop()
            globalMediaPlayer!!.release()

            val mMediaPlayer = MediaPlayer.create(this, audioUri)
            globalMediaPlayer = mMediaPlayer
        }


        globalMediaPlayer?.setOnCompletionListener(OnCompletionListener { mPlayer ->
            val songList = getFolderList(context, folder!!)
            val songIndex = songList.indexOf(song)

            if (songIndex != songList.size-1) {
                currentPlayingSong = songList[songIndex+1]
                song = songList[songIndex+1]
                val songFile = File(getExternalFilesDir(""), "$folder/$song")
                val audioUri = readAudio(songFile, this)

                // update display
                val folderText: TextView = findViewById(R.id.folder_name)
                folderText.text = folder
                val songText: TextView = findViewById(R.id.song_name)
                songText.text = song

                mPlayer.reset()
                mPlayer.setDataSource(context, audioUri)
                mPlayer.prepare()
                mPlayer.start()

            }
        })

        val playButton: Button = findViewById(R.id.play_button)

        if (resume != "true") {
            globalMediaPlayer!!.start()
        }
        playButton.setBackgroundResource(R.drawable.pause)

        playButton.setOnClickListener{
            if (! globalMediaPlayer!!.isPlaying) {
                globalMediaPlayer!!.start()
                playButton.setBackgroundResource(R.drawable.pause)
            } else {
                globalMediaPlayer!!.pause()
                playButton.setBackgroundResource(R.drawable.play)
            }
        }

        val nextButton: Button = findViewById(R.id.next_button)
        nextButton.setOnClickListener{
            val songList = getFolderList(context, folder!!)
            val songIndex = songList.indexOf(song)

            if (songIndex != songList.size-1) {
                currentPlayingSong = songList[songIndex+1]
                song = songList[songIndex+1]
                val songFile = File(getExternalFilesDir(""), "$folder/$song")
                val audioUri = readAudio(songFile, this)

                // update display
                val folderText: TextView = findViewById(R.id.folder_name)
                folderText.text = folder
                val songText: TextView = findViewById(R.id.song_name)
                songText.text = song

                globalMediaPlayer!!.reset()
                globalMediaPlayer!!.setDataSource(context, audioUri)
                globalMediaPlayer!!.prepare()
                globalMediaPlayer!!.start()
            }
        }

        val prevButton: Button = findViewById(R.id.prev_button)
        prevButton.setOnClickListener{
            val songList = getFolderList(context, folder!!)
            val songIndex = songList.indexOf(song)

            if (songIndex != 0) {
                currentPlayingSong = songList[songIndex-1]
                song = songList[songIndex-1]
                val songFile = File(getExternalFilesDir(""), "$folder/$song")
                val audioUri = readAudio(songFile, this)

                // update display
                val folderText: TextView = findViewById(R.id.folder_name)
                folderText.text = folder
                val songText: TextView = findViewById(R.id.song_name)
                songText.text = song

                globalMediaPlayer!!.reset()
                globalMediaPlayer!!.setDataSource(context, audioUri)
                globalMediaPlayer!!.prepare()
                globalMediaPlayer!!.start()
            }
        }

        val frameImg: ImageView = findViewById(R.id.frame_img)
        val playSeconds: TextView = findViewById(R.id.play_seconds)

        var currentFrame = readMobileFrames(songFile, context, 0)
        if (resume == "true") {
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(globalMediaPlayer!!.currentPosition.toLong()).toInt()
            currentFrame = readMobileFrames(songFile, context, seconds)
        }
        frameImg.setImageURI(currentFrame)
        CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(globalMediaPlayer!!.currentPosition.toLong()).toInt()
                var currentFrame = readMobileFrames(songFile, context, seconds)
                val bmOptions = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeFile(currentFrame.path, bmOptions)
                CoroutineScope(Dispatchers.Main).launch {
                    playSeconds.text = intToDisplaySeconds(seconds)
                    frameImg.setImageBitmap(bitmap)
                }
                delay(1000)
            }
        }
    }
}

fun intToDisplaySeconds(seconds: Int): String {
    val minutes = seconds / 60
    val seconds = seconds % 60
    var secondsStr = seconds.toString()
    if (secondsStr.length == 1) {
        secondsStr = "0$secondsStr"
    }
    return "$minutes:$secondsStr"
}

