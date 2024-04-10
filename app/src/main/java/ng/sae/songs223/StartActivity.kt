package ng.sae.songs223

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ng.sae.songs223.ui.theme.Songs223Theme
import java.io.File

var globalMediaPlayer: MediaPlayer? = null
var currentPlayingFolder: String? = null
var currentPlayingSong: String? = null

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Songs223Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

fun getCover(context: Context, folder: String): File {
    var dFile = File(context.getExternalFilesDir(""), "$folder/cover.jpg")
    var dFile2 = File(context.getExternalFilesDir(""), "$folder/Cover.jpg")

    var trueCover: File = dFile
    if (! dFile.exists() && dFile2.exists()) {
        trueCover = dFile2
    } else if (! dFile.exists() && ! dFile2.exists()) {
        context.cacheDir.mkdirs()
        val noCoverIS = context.assets.open("no_cover.png")
        val noCoverFile = File(context.cacheDir, "no_cover.png")
        if (!noCoverFile.exists()) {
            noCoverFile.writeBytes(noCoverIS.readBytes())
            Log.d("info", "no cover")

        } else {
            Log.d("info", "no cover")
        }
        trueCover = noCoverFile
    }

    return trueCover
}

@Composable
fun HomeScreen() {

    val context = LocalContext.current
    context.getExternalFilesDir("")?.mkdirs()

    val dFile = File(context.getExternalFilesDir(""), "")
    val rootFiles = dFile.listFiles()
    val folders = ArrayList<String>()

    for (rFile in rootFiles) {
        if (rFile.isDirectory) {
            folders.add(rFile.name)
        }
    }

    Column(
        modifier = Modifier.padding(15.dp),
    ){
        TopBar()
        Spacer(modifier = Modifier.height(20.dp))
        FoldersView(folders = folders, context)
    }
}

@Composable fun TopBar() {
    val mContext = LocalContext.current

    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(start = 20.dp)
    )
    {
        Text(
            "Songs223", color = Color.Gray, style= TextStyle(
            fontSize = 30.sp, fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(top = 5.dp)
        )

        Spacer(modifier = Modifier.width(width=10.dp))
        Button(
            onClick = {
                if (currentPlayingSong != null) {
                    val intent1 = Intent(mContext, PlayerActivity::class.java)
                    intent1.putExtra("folder", currentPlayingFolder)
                    intent1.putExtra("song", currentPlayingSong)
                    intent1.putExtra("resume", "true")
                    mContext.startActivity(intent1)
                }

            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                contentColor = Color.White)

        ) {
            Text("Now Playing")
        }
        Spacer(modifier = Modifier.width(width=10.dp))
        Button(onClick = {
            mContext.startActivity(Intent(mContext, InfoActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                contentColor = Color.White)

        ) {
            Text("Info", color = Color.White)
        }
    }
}

// on below line we are creating grid view function for loading our grid view.
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun FoldersView(folders: ArrayList<String>, context: Context) {
    LazyVerticalGrid(
        // on below line we are setting the
        // column count for our grid view.
        columns = GridCells.Fixed(2),
        // on below line we are adding padding
        // from all sides to our grid view.
//        modifier = Modifier.padding(5.dp)
    ) {
        items(folders.size) {
            Card(
//                border = BorderStroke(1.dp, Color.Gray),
                // inside our grid view on below line we are
                // adding on click for each item of our grid view.
                onClick = {
                    val intent1 = Intent(context, FolderListActivity::class.java)
                    intent1.putExtra("folder", folders[it])
                    context.startActivity(intent1)
                },

                // on below line we are adding padding from our all sides.
                modifier = Modifier.padding(8.dp),

                // on below line we are adding elevation for the card.
                elevation = 0.dp

            ) {
                val coverFile = getCover(LocalContext.current, folders[it])
                Column {
                    AsyncImage(
                        model = coverFile,
                        contentDescription = "Album Art",
                        modifier = Modifier.padding(8.dp)
                        )
                    Text(folders[it], modifier = Modifier.align(Alignment.CenterHorizontally))
                }

            }
        }
    }
}
