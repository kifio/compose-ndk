package me.kifio.stringsparser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.kifio.stringsparser.ui.theme.StringsParserTheme
import me.kifio.stringsparser.ui.view.StringParserView
import me.kifio.stringsparser.ui.view.StringsParserViewModel

class MainActivity : ComponentActivity() {

    companion object {
        init {
            System.loadLibrary("stringsparser");
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StringsParserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StringParserView(viewModel(factory = StringsParserViewModel.Factory))
                }
            }
        }
    }
}