package me.kifio.stringsparser.domain

import me.kifio.stringsparser.data.LocalRepository

class CancelFiltrationStringsUseCase(
    private val repo: LocalRepository,
) {
    fun invoke() = repo.cancel()
}