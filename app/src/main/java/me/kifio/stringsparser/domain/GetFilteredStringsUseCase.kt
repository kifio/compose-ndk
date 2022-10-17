package me.kifio.stringsparser.domain

import android.content.res.AssetManager
import me.kifio.stringsparser.data.LocalRepository

class GetFilteredStringsUseCase(
    private val repo: LocalRepository,
    private val assetManager: AssetManager
) {

    fun invoke(assetFilename: String, mask: String): List<String> {
        return repo.getFilteredStrings(assetFilename, mask, assetManager)
    }
}