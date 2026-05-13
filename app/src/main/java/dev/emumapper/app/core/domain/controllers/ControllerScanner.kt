package dev.emumapper.app.core.domain.controllers

import dev.emumapper.app.core.input.ControllerInfo

interface ControllerScanner {
    fun scan(): List<ControllerInfo>
}