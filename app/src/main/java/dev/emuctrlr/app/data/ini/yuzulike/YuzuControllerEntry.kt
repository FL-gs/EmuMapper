package dev.emuctrlr.app.data.ini.yuzulike

import dev.emuctrlr.app.core.input.ControllerInfo

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