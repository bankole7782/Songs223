package ng.sae.songs223

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ng.sae.songs223.ui.theme.Songs223Theme
import java.io.File

class InfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Songs223Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    InfoScreen()
                }
            }
        }
    }
}

@Composable
fun InfoScreen() {
    // Creating an annotated string
    val mAnnotatedLinkString = buildAnnotatedString {
        append("To create an .l8f song. Please use the program Lyrics818 which can be gotten from ")

        pushStringAnnotation(tag = "saeng", annotation = "https://sae.ng/lyrics818")
        withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
            append("sae.ng")
        }
        pop()
    }

    // UriHandler parse and opens URI inside
    // AnnotatedString Item in Browse
    val mUriHandler = LocalUriHandler.current

    val dFile = File(LocalContext.current.getExternalFilesDir(""), "")
    val songPath = dFile.absolutePath.removePrefix("/storage/emulated/0")

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            "To Add songs: Connect to Laptop and copy songs with .l8f format in a folder to ",
            fontSize = 20.sp,
        )
        Text(
            songPath, fontWeight = FontWeight.Bold, fontSize = 20.sp
        )
        Text("")
        ClickableText(
            text = mAnnotatedLinkString,
            onClick = {
                mAnnotatedLinkString
                    .getStringAnnotations("saeng", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        mUriHandler.openUri(stringAnnotation.item)
                    }
            },
            style = TextStyle(fontSize = 20.sp)
        )
    }
}