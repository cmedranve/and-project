package pe.com.scotiabank.blpm.android.client.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import coil.ComponentRegistry
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import pe.com.scotiabank.blpm.android.client.base.module.DaggerApplicationComponent
import java.lang.ref.WeakReference
import javax.inject.Inject

open class JoyApplication : DaggerApplication(), ImageLoaderFactory {

    @Inject
    lateinit var factoryOfAppModel: FactoryOfAppModel

    lateinit var appModel: AppModel

    override fun onCreate() {
        super.onCreate()
        setUpModel()
    }

    private fun setUpModel() {
        val weakApp: WeakReference<out Application?> = WeakReference(this)
        appModel = factoryOfAppModel.create(weakApp)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication?> {
        return DaggerApplicationComponent.factory().create(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun newImageLoader(): ImageLoader {
        val componentRegistry = ComponentRegistry()
            .newBuilder()
            .add(SvgDecoder.Factory())
            .build()
        return ImageLoader.Builder(this)
            .components(componentRegistry)
            .build()
    }
}
