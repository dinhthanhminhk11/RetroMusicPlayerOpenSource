package code.name.monkey.retromusic.encryption

class Key {
    companion object {
        init {
            System.loadLibrary("retromusic")
        }
    }

    external fun stringFromJNI(): String
}