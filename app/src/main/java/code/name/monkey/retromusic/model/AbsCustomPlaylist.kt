package code.name.monkey.retromusic.model

import code.name.monkey.retromusic.repository.dataSource.local.LastAddedLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.SongLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.TopPlayedLocalDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class AbsCustomPlaylist(
    id: Long,
    name: String
) : Playlist(id, name), KoinComponent {

    abstract fun songs(): List<Song>

    protected val songLocalRepository by inject<SongLocalDataRepository>()

    protected val topPlayedLocalDataRepository by inject<TopPlayedLocalDataRepository>()

    protected val lastAddedLocalDataRepository by inject<LastAddedLocalDataRepository>()
}