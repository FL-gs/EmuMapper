package com.example.pairingapp.core.app

import android.content.Context
import com.example.pairingapp.PairingApplication
import com.example.pairingapp.core.domain.controllers.DefaultControllerAssignmentService
import com.example.pairingapp.core.domain.controllers.InternalControllerClassifier
import com.example.pairingapp.core.domain.controllers.DefaultControllerScanner
import com.example.pairingapp.core.pairing.DefaultVisibleControllersResolver
import com.example.pairingapp.core.pairing.write.DefaultWritePolicy
import com.example.pairingapp.core.pairing.write.IniConfigWriter
import com.example.pairingapp.core.pairing.PairingEngine
import com.example.pairingapp.data.settings.AppSettingsRepository
import com.example.pairingapp.data.settings.datastore.settingsDataStore

class AppGraph(context: Context) {
    private val appContext = context.applicationContext

    val settingsRepository = AppSettingsRepository(appContext.settingsDataStore)

    private val controllerScanner = DefaultControllerScanner()
    private val controllerClassifier = InternalControllerClassifier()
    private val visibleControllersResolver = DefaultVisibleControllersResolver()
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
        writePolicy = writePolicy,
        configWriter = configWriter
    )
}

val Context.appGraph: AppGraph
    get() = (applicationContext as PairingApplication).appGraph