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
import code.name.monkey.retromusic.fragments.login.LoginViewModel
import code.name.monkey.retromusic.fragments.otp.OtpViewModel
import code.name.monkey.retromusic.fragments.playlists.PlaylistDetailsViewModel
import code.name.monkey.retromusic.fragments.register.RegisterViewModel
import code.name.monkey.retromusic.model.Genre
import code.name.monkey.retromusic.network.provideAlbumService
import code.name.monkey.retromusic.network.provideArtistService
import code.name.monkey.retromusic.network.provideDefaultCache
import code.name.monkey.retromusic.network.provideLastFmRest
import code.name.monkey.retromusic.network.provideLastFmRetrofit
import code.name.monkey.retromusic.network.provideUserService
import code.name.monkey.retromusic.network.provideNewApiRetrofit
import code.name.monkey.retromusic.network.provideOkHttp
import code.name.monkey.retromusic.network.provideSongService
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
import code.name.monkey.retromusic.repository.dataSource.network.AlbumRemoteDataSource
import code.name.monkey.retromusic.repository.dataSource.network.ArtistRemoteDataSource
import code.name.monkey.retromusic.repository.dataSource.network.LoginRemoteDataSource
import code.name.monkey.retromusic.repository.dataSource.network.SongRemoteDataSource
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
import code.name.monkey.retromusic.repository.dataSourceImpl.network.AlbumRemoteDataSourceImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.network.ArtistRemoteDataSourceImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.network.LoginRemoteDataSourceImpl
import code.name.monkey.retromusic.repository.dataSourceImpl.network.SongRemoteDataSourceImpl
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

val networkModelNewApi = module {
    single {
        provideNewApiRetrofit(get())
    }
    single {
        provideUserService(get())
    }
    single {
        provideSongService(get())
    }
    single {
        provideAlbumService(get())
    }
    single {
        provideArtistService(get())
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
            get(),
            get(),
            get(),
            get()
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

    single {
        LoginRemoteDataSourceImpl(get())
    } bind LoginRemoteDataSource::class

    single {
        SongRemoteDataSourceImpl(get())
    } bind SongRemoteDataSource::class
    single {
        AlbumRemoteDataSourceImpl(get())
    } bind AlbumRemoteDataSource::class
    single {
        ArtistRemoteDataSourceImpl(get())
    } bind ArtistRemoteDataSource::class
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

    viewModel { (artistId: String?) ->
        ArtistDetailsViewModel(
            get(),
            artistId
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

    viewModel {
        LoginViewModel(
            get()
        )
    }
    viewModel {
        RegisterViewModel(
            get()
        )
    }
    viewModel {
        OtpViewModel(
            get()
        )
    }
}

val appModules = listOf(
    mainModule,
    dataModule,
    autoModule,
    viewModules,
    networkModule,
    roomModule,
    networkModelNewApi
)