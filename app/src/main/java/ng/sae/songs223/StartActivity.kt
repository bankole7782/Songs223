package ng.sae.songs223

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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
        topBar()
        FoldersView(folders = folders, context)
    }
}

@Composable fun topBar() {
    val mContext = LocalContext.current

    Row {
        Text("Songs223", color = Color.Gray, style= TextStyle(
            fontSize = 24.sp
        ))
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
            }
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
//                border = BorderStroke(2.dp, Color.Gray),
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
                elevation = 6.dp

            ) {
                Text(folders[it], modifier = Modifier.padding(10.dp))
            }
        }
    }
}
