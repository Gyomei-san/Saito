package ani.saito.parsers.novel

import ani.saito.parsers.Book
import ani.saito.parsers.NovelInterface
import ani.saito.parsers.NovelParser
import ani.saito.parsers.ShowResponse
import eu.kanade.tachiyomi.network.NetworkHelper
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class NovelAdapter

class DynamicNovelParser(extension: NovelExtension.Installed) : NovelParser() {
    override val volumeRegex =
        Regex("vol\\.? (\\d+(\\.\\d+)?)|volume (\\d+(\\.\\d+)?)", RegexOption.IGNORE_CASE)
    var extension: NovelExtension.Installed
    val client = Injekt.get<NetworkHelper>().requestClient

    init {
        this.extension = extension
    }

    override suspend fun search(query: String): List<ShowResponse> {
        val source = extension.sources.firstOrNull()
        return if (source is NovelInterface) {
            source.search(query, client)
        } else {
            emptyList()
        }
    }

    override suspend fun loadBook(link: String, extra: Map<String, String>?): Book {
        val source = extension.sources.firstOrNull()
        if (source is NovelInterface) {
            return source.loadBook(link, extra, client)
        } else {
            return Book("", "", "", emptyList())
        }
    }

}