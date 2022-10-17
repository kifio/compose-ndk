package me.kifio.stringsparser.ui.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.*
import me.kifio.stringsparser.data.LocalRepository
import me.kifio.stringsparser.domain.CancelFiltrationStringsUseCase
import me.kifio.stringsparser.domain.GetFilteredStringsUseCase

data class StringParserUiState(
    val stringsToDisplayOnScreen: List<String> = emptyList(),
    val loading: Boolean = false
)

class StringsParserViewModel(
    private val getFilteredStringsUseCase: GetFilteredStringsUseCase,
    private val cancelFiltrationStringsUseCase: CancelFiltrationStringsUseCase,
) : ViewModel() {

    companion object {

        private const val ASSET_FILE_NAME = "hp_1.txt"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T = with(checkNotNull(extras[APPLICATION_KEY])) {
                val repository = LocalRepository()
                StringsParserViewModel(
                    GetFilteredStringsUseCase(repository, assets),
                    CancelFiltrationStringsUseCase(repository)
                ) as T
            }
        }
    }

    var uiState by mutableStateOf(StringParserUiState(loading = true))
        private set

    var filterMaskState by mutableStateOf(".*")

    private var job: Job? = null

    init { filterText() }

    fun filterText() {
        viewModelScope.launch { job?.cancelAndJoin() }

        job = viewModelScope.launch(Dispatchers.Default) {
            cancelFiltrationStringsUseCase.invoke()

            try {

                withContext(Dispatchers.Main) {
                    uiState =
                        StringParserUiState(stringsToDisplayOnScreen = emptyList(), loading = true)
                }

                val lines: List<String> =
                    getFilteredStringsUseCase.invoke(ASSET_FILE_NAME, filterMaskState)

                withContext(Dispatchers.Main) {
                    uiState = StringParserUiState(stringsToDisplayOnScreen = lines, loading = false)
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
                throw e
            }
        }
    }
}