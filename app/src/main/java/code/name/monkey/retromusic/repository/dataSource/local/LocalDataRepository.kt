package code.name.monkey.retromusic.repository.dataSource.local

import code.name.monkey.retromusic.model.Contributor

interface LocalDataRepository {
    fun contributors(): List<Contributor>
}