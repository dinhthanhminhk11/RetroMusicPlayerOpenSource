package code.name.monkey.retromusic

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import cat.ereza.customactivityoncrash.config.CaocConfig
import code.name.monkey.appthemehelper.ThemeStore
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.activities.ErrorActivity
import code.name.monkey.retromusic.activities.MainActivity
import code.name.monkey.retromusic.appshortcuts.DynamicShortcutManager
import code.name.monkey.retromusic.billing.BillingManager
import code.name.monkey.retromusic.encryption.AESUtil
import code.name.monkey.retromusic.encryption.RESTUtil
import code.name.monkey.retromusic.encryption.Secret
import code.name.monkey.retromusic.helper.WallpaperAccentManager
import code.name.monkey.retromusic.model.response.LoginResponse
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.io.InputStream
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class App : Application() {

    lateinit var billingManager: BillingManager
    private val wallpaperAccentManager = WallpaperAccentManager(this)
    private lateinit var serverSecret: Secret

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
        // default theme
        if (!ThemeStore.isConfigured(this, 3)) {
            ThemeStore.editTheme(this)
                .accentColorRes(code.name.monkey.appthemehelper.R.color.md_deep_orange_A400)
                .coloredNavigationBar(true)
                .commit()
        }
        wallpaperAccentManager.init()

        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(this).initDynamicShortcuts()

        billingManager = BillingManager(this)

        // setting Error activity
        CaocConfig.Builder.create().errorActivity(ErrorActivity::class.java)
            .restartActivity(MainActivity::class.java).apply()

        // Set Default values for now playing preferences
        // This will reduce startup time for now playing settings fragment as Preference listener of AbsSlidingMusicPanelActivity won't be called
        PreferenceManager.setDefaultValues(this, R.xml.pref_now_playing_screen, false)

        var serverPublicKey: PublicKey? = null
        try {
            val pemString = "-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2tlHxvCIVkAGrJMFQVn/\n" +
                    "TGUAiw6bKNWecxnTpKAYy//HjnhOVVDBqctWPYMfSiY8pGLLWWfxXduXXr3MOGtd\n" +
                    "YmyAxyGtbB2lyjzAtQe6uNdORwuhpQzx54UyhU5ggOyvBtDxL3HtwREWWTco0r0h\n" +
                    "w9XyRFXt4yQAI/QMW1edl1vkUglGK2GFIA3OM5DMGESGFCgeaiJxQa0GCu9WyJYJ\n" +
                    "/1GBzGGCSNJetaW+FluM8zH0vbWjTs+D/TmSuvrdPqCPXNjRJ7lDO4tfJgkZh1CP\n" +
                    "Ei+w1mnkg/aq9gKne4J2r4iRTLxm0MAUJNTjSRr2exy0YVfCjk6Z4turg3omgzJG\n" +
                    "QQIDAQAB\n" +
                    "-----END PUBLIC KEY-----"

            val publicKey: PublicKey = getPublicKeyFromPEM(pemString)
            serverPublicKey = publicKey
            Log.e("MInh read file", serverPublicKey.toString())
        } catch (e: Exception) {
            Log.e("MInh read file", e.message.toString())
            e.printStackTrace()
        }
        serverSecret = Secret(
            serverPublicKey,
            AESUtil.randomAESBytesEncoded(),
            AESUtil.randomAESBytesEncoded()
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun getPublicKeyFromPEM(pemString: String): PublicKey {
        val publicKeyPEM = pemString
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "")

        val publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM)

        val keySpec = X509EncodedKeySpec(publicKeyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    override fun onTerminate() {
        super.onTerminate()
        billingManager.release()
        wallpaperAccentManager.release()
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        fun isProVersion(): Boolean {
            return BuildConfig.DEBUG || instance?.billingManager!!.isProVersion
        }
    }

   public fun getServerSecret(): Secret = serverSecret
}
