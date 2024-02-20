package code.name.monkey.retromusic.repository.dataSource.local

import code.name.monkey.retromusic.model.Album

interface AlbumLocalDataRepository {
    fun albums(): List<Album>

    fun albums(query: String): List<Album>

    fun album(albumId: Long): Album
}