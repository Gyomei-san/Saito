package ani.saito.connections.anilist

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ani.saito.logError
import ani.saito.settings.saving.PrefManager
import ani.saito.settings.saving.PrefName
import ani.saito.startMainActivity
import ani.saito.themes.ThemeManager

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager(this).applyTheme()
        val data: Uri? = intent?.data
        try {
            Anilist.token =
                Regex("""(?<=access_token=).+(?=&token_type)""").find(data.toString())!!.value
            PrefManager.setVal(PrefName.AnilistToken, Anilist.token ?: "")
        } catch (e: Exception) {
            logError(e)
        }
        startMainActivity(this)
    }
}
