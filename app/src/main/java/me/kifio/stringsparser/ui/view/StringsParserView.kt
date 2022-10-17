package me.kifio.stringsparser.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.kifio.stringsparser.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StringParserView(viewModel: StringsParserViewModel) {
    Column {
        Text(
            text = "Simple app, which read file from asset and filter lines with regular expression.",
            fontSize = 16.sp
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.filterMaskState,
            onValueChange = { viewModel.filterMaskState = it },
            label = { Text("RegEx") },
            trailingIcon = {
                IconButton(
                    onClick = { viewModel.filterText() },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_done),
                        contentDescription = "Filter with regular expression"
                    )
                }
            }
        )

        when (viewModel.uiState.loading) {
            true -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(48.dp)
                    )
                }
            }

            false -> {
                LazyColumn {
                    when (val count = viewModel.uiState.stringsToDisplayOnScreen.size) {
                        0 -> item { Text(text = "Nothing found") }
                        else -> items(count) { index ->
                            Text(text = viewModel.uiState.stringsToDisplayOnScreen[index])
                        }
                    }
                }
            }
        }
    }
}