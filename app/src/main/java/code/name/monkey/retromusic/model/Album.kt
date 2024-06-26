package code.name.monkey.retromusic.model

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("idAlbum")
    val id: Long,
    val songs: List<Song>,
    val artistIdString: String,
    val artistImage: String) {
    val title: String
        get() = safeGetFirstSong().albumName

    val artistId: Long
        get() = safeGetFirstSong().artistId

    val artistName: String
        get() = safeGetFirstSong().artistName

    val year: Int
        get() = safeGetFirstSong().year

    val dateModified: Long
        get() = safeGetFirstSong().dateModified

    val songCount: Int
        get() = songs.size

    val albumArtist: String?
        get() = safeGetFirstSong().albumArtist

    fun safeGetFirstSong(): Song {
        return songs.firstOrNull() ?: Song.emptySong
    }

    companion object {
        val empty = Album(-1, emptyList(), "" , "")
    }
}
