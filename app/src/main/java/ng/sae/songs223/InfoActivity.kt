package ng.sae.songs223

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ng.sae.songs223.ui.theme.Songs223Theme
import java.io.File

class InfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dFile = File(getExternalFilesDir(""), "")
        val songPath = dFile.absolutePath.removePrefix("/storage/emulated/0")
        setContent {
            Songs223Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "To Add songs: Connect to Laptop and copy songs with .l8f format to"
                        )
                        Text(
                            songPath, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
