package dev.emumapper.app.core.update

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/*
 * Queries GitHub to fetch the latest app release.
 *
 * Reads the release tag and URL, then returns an update
 * if it is newer than the installed version.
 */
class AppUpdateChecker(
    private val owner: String = "FL-gs",
    private val repo: String = "EmuMapper"
) {

    suspend fun checkForUpdate(
        currentVersionName: String
    ): AppUpdateInfo? = withContext(Dispatchers.IO) {
        val url = URL("https://api.github.com/repos/$owner/$repo/releases/latest")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("User-Agent", "EmuMapper-UpdateChecker")

            val responseCode = connection.responseCode

            if (responseCode !in 200..299) {
                return@withContext null
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)

            val tagName = json.optString("tag_name")
            val releaseUrl = json.optString("html_url")

            if (tagName.isBlank() || releaseUrl.isBlank()) {
                return@withContext null
            }

            val remoteVersionName = tagName
                .removePrefix("v")
                .removePrefix("V")

            val hasUpdate = VersionCompare.isRemoteVersionNewer(
                currentVersion = currentVersionName,
                remoteVersion = remoteVersionName
            )

            if (!hasUpdate) {
                return@withContext null
            }

            AppUpdateInfo(
                versionName = remoteVersionName,
                releaseUrl = releaseUrl
            )
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}