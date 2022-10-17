package me.kifio.stringsparser.data

import android.content.res.AssetManager
import java.util.regex.Pattern

class LocalRepository {

    private var isCancelled: Boolean = false

    fun getFilteredStringsKotlin(file: String, mask: String, assetManager: AssetManager): List<String> {
        isCancelled = false
        val pattern = Pattern.compile(mask)
        return assetManager.open(file).bufferedReader().useLines { lines ->
            lines
                .takeWhile { !isCancelled }
                .filter { line -> pattern.matcher(line).matches() }.toList()
        }
    }

    fun cancel() {
        isCancelled = true
        cancelCurrentOperation()
    }

    external fun getFilteredStrings(file: String, mask: String, assetManager: AssetManager): ArrayList<String>

    external fun cancelCurrentOperation()

}