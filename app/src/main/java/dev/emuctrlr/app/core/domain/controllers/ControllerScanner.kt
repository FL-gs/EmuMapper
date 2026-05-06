package dev.emuctrlr.app.core.domain.controllers

import dev.emuctrlr.app.core.input.ControllerInfo

interface ControllerScanner {
    fun scan(): List<ControllerInfo>
}