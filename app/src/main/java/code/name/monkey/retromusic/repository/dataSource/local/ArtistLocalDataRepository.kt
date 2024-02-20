package code.name.monkey.retromusic.repository.dataSource.local

import code.name.monkey.retromusic.model.Artist

interface ArtistLocalDataRepository {
    fun artists(): List<Artist>

    fun albumArtists(): List<Artist>

    fun albumArtists(query: String): List<Artist>

    fun artists(query: String): List<Artist>

    fun artist(artistId: Long): Artist

    fun albumArtist(artistName: String): Artist
}