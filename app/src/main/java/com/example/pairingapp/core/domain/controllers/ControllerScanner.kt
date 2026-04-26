package com.example.pairingapp.core.domain.controllers

import com.example.pairingapp.core.input.ControllerInfo

interface ControllerScanner {
    fun scan(): List<ControllerInfo>
}