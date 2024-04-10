package ng.sae.songs223

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ng.sae.songs223.ui.theme.Songs223Theme
import java.io.File

class FolderListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Songs223Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val folder = intent.getStringExtra("folder")
                    if (folder != null) {
                        Column(
                            modifier = Modifier.padding(10.dp)

                        ) {
                            TopBar()
                            FolderList(folder)
                        }

                    }
                }
            }
        }
    }

}

fun getFolderList(context: Context, folder: String): ArrayList<String> {
    val dFile = File(context.getExternalFilesDir(""), "$folder/")
    val rootFiles = dFile.listFiles()

    var songList = ArrayList<String>()
    for (rFile in rootFiles) {
        if (!rFile.isDirectory && rFile.name != "cover.jpg" && rFile.name != "Cover.jpg") {
            songList.add(rFile.name)
        }
    }
    return songList
}

@Composable
fun FolderList(folder: String) {
    val context = LocalContext.current
    val songList = getFolderList(context, folder)
    Column {
        Text(folder, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally)
            .padding(10.dp))
        FolderListView(songList, folder, context)
    }
}



// on below line we are creating grid view function for loading our grid view.
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun FolderListView(songList: ArrayList<String>, folder: String, context: Context) {

    val coverFile = getCover(LocalContext.current, folder)
    Column() {
        AsyncImage(
            model = coverFile,
            contentDescription = "Album Art",
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(bottom=20.dp)
        )

        LazyVerticalGrid(
            // on below line we are setting the
            // column count for our grid view.
            columns = GridCells.Fixed(1),
            // on below line we are adding padding
            // from all sides to our grid view.
        ) {
            items(songList.size) {
                Card(
//                    border = BorderStroke(1.dp, Color.Gray),
                    // inside our grid view on below line we are
                    // adding on click for each item of our grid view.
                    onClick = {
                        val intent1 = Intent(context, PlayerActivity::class.java)
                        intent1.putExtra("folder", folder)
                        intent1.putExtra("song", songList[it])
                        intent1.putExtra("resume", "false")
                        context.startActivity(intent1)
                    },

                    // on below line we are adding padding from our all sides.
                    modifier = Modifier.padding(0.dp),

                    // on below line we are adding elevation for the card.
                    elevation = 0.dp

                ) {
                    val songName = songList[it]
                    val songFile = File(context.getExternalFilesDir(""), "$folder/$songName")
                    val duration = getVideoLength(songFile, context)

                    Row {
                        Text(songList[it],  modifier = Modifier.padding(top = 10.dp))
                        Text(
                            intToDisplaySeconds(duration),
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(top = 10.dp)
                                .fillMaxWidth()
                        )
                    }
                    if (it != 0 && it != songList.size) {
                        Divider(color = Color.Gray, modifier = Modifier.fillMaxHeight()
                            .padding(bottom = 0.dp)
                        )
                    }
                }
            }
        }

    }

}
