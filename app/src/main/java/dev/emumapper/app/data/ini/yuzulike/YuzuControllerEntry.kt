package dev.emumapper.app.data.ini.yuzulike

import dev.emumapper.app.core.input.ControllerInfo

data class YuzuControllerEntry(
    val port: Int,
    val guid: String,
    val display: String
)

fun ControllerInfo.yuzuControllerEntryOrNull(): YuzuControllerEntry? {
    val port = yuzuPort ?: return null

    val guid = "%016x%016x".format(
        productId,
        vendorId
    )

    return YuzuControllerEntry(
        port = port,
        guid = guid,
        display = "$name $port"
    )
}