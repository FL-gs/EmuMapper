package dev.emumapper.app.core.app

import android.content.Context
import dev.emumapper.app.BuildConfig
import dev.emumapper.app.PairingApplication
import dev.emumapper.app.core.domain.controllers.DefaultControllerAssignmentService
import dev.emumapper.app.core.domain.controllers.InternalControllerClassifier
import dev.emumapper.app.core.domain.controllers.DefaultControllerScanner
import dev.emumapper.app.core.input.mapping.ControllerMappingResolver
import dev.emumapper.app.core.pairing.DefaultVisibleControllersResolver
import dev.emumapper.app.core.pairing.write.DefaultWritePolicy
import dev.emumapper.app.core.pairing.write.IniConfigWriter
import dev.emumapper.app.core.pairing.PairingEngine
import dev.emumapper.app.core.update.AppUpdateChecker
import dev.emumapper.app.core.update.UpdateManager
import dev.emumapper.app.data.settings.AppSettingsRepository
import dev.emumapper.app.data.settings.datastore.settingsDataStore

class AppGraph(context: Context) {
    private val appContext = context.applicationContext

    val settingsRepository = AppSettingsRepository(appContext.settingsDataStore)

    private val appUpdateChecker = AppUpdateChecker()

    val updateManager = UpdateManager(
        appUpdateChecker = appUpdateChecker,
        currentVersionName = BuildConfig.VERSION_NAME
    )

    private val controllerScanner = DefaultControllerScanner()
    private val controllerClassifier = InternalControllerClassifier()
    private val visibleControllersResolver = DefaultVisibleControllersResolver()
    private val controllerMappingResolver = ControllerMappingResolver()
    private val writePolicy = DefaultWritePolicy()
    private val configWriter = IniConfigWriter()

    private val controllerAssignmentService = DefaultControllerAssignmentService(
        scanner = controllerScanner,
        classifier = controllerClassifier,
        resolver = visibleControllersResolver
    )

    val pairingEngine = PairingEngine(
        context = appContext,
        settingsRepository = settingsRepository,
        controllerAssignmentService = controllerAssignmentService,
        controllerMappingResolver = controllerMappingResolver,
        writePolicy = writePolicy,
        configWriter = configWriter
    )
}

val Context.appGraph: AppGraph
    get() = (applicationContext as PairingApplication).appGraph
