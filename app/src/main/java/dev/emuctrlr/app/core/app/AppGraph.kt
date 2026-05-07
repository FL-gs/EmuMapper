package dev.emuctrlr.app.core.app

import android.content.Context
import dev.emuctrlr.app.PairingApplication
import dev.emuctrlr.app.core.domain.controllers.DefaultControllerAssignmentService
import dev.emuctrlr.app.core.domain.controllers.InternalControllerClassifier
import dev.emuctrlr.app.core.domain.controllers.DefaultControllerScanner
import dev.emuctrlr.app.core.input.mapping.ControllerMappingResolver
import dev.emuctrlr.app.core.pairing.DefaultVisibleControllersResolver
import dev.emuctrlr.app.core.pairing.write.DefaultWritePolicy
import dev.emuctrlr.app.core.pairing.write.IniConfigWriter
import dev.emuctrlr.app.core.pairing.PairingEngine
import dev.emuctrlr.app.data.settings.AppSettingsRepository
import dev.emuctrlr.app.data.settings.datastore.settingsDataStore

class AppGraph(context: Context) {
    private val appContext = context.applicationContext

    val settingsRepository = AppSettingsRepository(appContext.settingsDataStore)

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
