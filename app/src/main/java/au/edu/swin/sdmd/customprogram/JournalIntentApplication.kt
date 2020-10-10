package au.edu.swin.sdmd.customprogram

import android.app.Application

class JournalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        JournalRepository.initialize(this)
    }
}