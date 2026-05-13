package dev.emumapper.app.core.update

/**
 * Normalise les noms de version, puis compare deux versions de l'application.
 */

object VersionCompare {

    fun isRemoteVersionNewer(
        currentVersion: String,
        remoteVersion: String
    ): Boolean {
        val currentParts = currentVersion.normalizedVersionParts()
        val remoteParts = remoteVersion.normalizedVersionParts()

        val maxSize = maxOf(currentParts.size, remoteParts.size)

        for (index in 0 until maxSize) {
            val current = currentParts.getOrElse(index) { 0 }
            val remote = remoteParts.getOrElse(index) { 0 }

            if (remote > current) return true
            if (remote < current) return false
        }

        return false
    }

    private fun String.normalizedVersionParts(): List<Int> {
        return trim()
            .removePrefix("v")
            .split(".")
            .mapNotNull { part ->
                part.toIntOrNull()
            }
    }
}