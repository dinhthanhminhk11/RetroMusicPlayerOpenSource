package code.name.monkey.retromusic

import androidx.room.Room
import code.name.monkey.retromusic.auto.AutoMusicProvider
import code.name.monkey.retromusic.cast.RetroWebServer
import code.name.monkey.retromusic.db.MIGRATION_23_24
import code.name.monkey.retromusic.db.RetroDatabase
import code.name.monkey.retromusic.fragments.LibraryViewModel
import code.name.monkey.retromusic.fragments.albums.AlbumDetailsViewModel
import code.name.monkey.retromusic.fragments.artists.ArtistDetailsViewModel
import code.name.monkey.retromusic.fragments.genres.GenreDetailsViewModel
import code.name.monkey.retromusic.fragments.playlists.PlaylistDetailsViewModel
import code.name.monkey.retromusic.model.Genre
import code.name.monkey.retromusic.network.provideDefaultCache
import code.name.monkey.retromusic.network.provideLastFmRest
import code.name.monkey.retromusic.network.provideLastFmRetrofit
import code.name.monkey.retromusic.network.provideOkHttp
import code.name.monkey.retromusic.repository.*
import code.name.monkey.retromusic.repository.dataSource.local.AlbumLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.ArtistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.GenreLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.LastAddedLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.LocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.PlaylistLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.RoomLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.SongLocalDataRepository
import code.name.monkey.retromusic.repository.dataSource.local.TopPlayedLocalDataRepository
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealAlbumLocalDataRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealArtistLocalDataRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealGenreLocalDataRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealLastAddedRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealLocalDataRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealPlaylistLocalDataRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealRoomLocalDataRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealSearchRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealSongLocalDataRepositoryImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.local.RealTopPlayedLocalDataRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    factory {
        provideDefaultCache()
    }
    factory {
        provideOkHttp(get(), get())
    }
    single {
        provideLastFmRetrofit(get())
    }
    single {
        provideLastFmRest(get())
    }
}

private val roomModule = module {
    single {
        Room.databaseBuilder(androidContext(), RetroDatabase::class.java, "playlist.db")
            .addMigrations(MIGRATION_23_24)
            .build()
    }

    factory {
        get<RetroDatabase>().playlistDao()
    }

    factory {
        get<RetroDatabase>().playCountDao()
    }

    factory {
        get<RetroDatabase>().historyDao()
    }

    single {
        RealRoomLocalDataRepositoryImpl(get(), get(), get())
    } bind RoomLocalDataRepository::class
}
private val autoModule = module {
    single {
        AutoMusicProvider(
            androidContext(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
private val mainModule = module {
    single {
        androidContext().contentResolver
    }
    single {
        RetroWebServer(get())
    }
}
private val dataModule = module {
    single {
        RealRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    } bind Repository::class

    single {
        RealSongLocalDataRepositoryImpl(get())
    } bind SongLocalDataRepository::class

    single {
        RealGenreLocalDataRepositoryImpl(get(), get())
    } bind GenreLocalDataRepository::class

    single {
        RealAlbumLocalDataRepositoryImpl(get())
    } bind AlbumLocalDataRepository::class

    single {
        RealArtistLocalDataRepositoryImpl(get(), get())
    } bind ArtistLocalDataRepository::class

    single {
        RealPlaylistLocalDataRepositoryImpl(get())
    } bind PlaylistLocalDataRepository::class

    single {
        RealTopPlayedLocalDataRepositoryImpl(get(), get(), get(), get())
    } bind TopPlayedLocalDataRepository::class

    single {
        RealLastAddedRepositoryImpl(
            get(),
            get(),
            get()
        )
    } bind LastAddedLocalDataRepository::class

    single {
        RealSearchRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single {
        RealLocalDataRepositoryImpl(get())
    } bind LocalDataRepository::class
}

private val viewModules = module {

    viewModel {
        LibraryViewModel(get())
    }

    viewModel { (albumId: Long) ->
        AlbumDetailsViewModel(
            get(),
            albumId
        )
    }

    viewModel { (artistId: Long?, artistName: String?) ->
        ArtistDetailsViewModel(
            get(),
            artistId,
            artistName
        )
    }

    viewModel { (playlistId: Long) ->
        PlaylistDetailsViewModel(
            get(),
            playlistId
        )
    }

    viewModel { (genre: Genre) ->
        GenreDetailsViewModel(
            get(),
            genre
        )
    }
}

val appModules = listOf(mainModule, dataModule, autoModule, viewModules, networkModule, roomModule)