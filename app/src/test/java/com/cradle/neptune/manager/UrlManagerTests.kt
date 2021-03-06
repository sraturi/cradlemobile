package com.cradle.neptune.manager

import com.cradle.neptune.model.Settings
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class UrlManagerTests {

    @Test
    fun base_portIsNull_constructUrlWithoutPort() {
        val mockSettings: Settings = mock()
        whenever(mockSettings.networkUseHttps)
            .thenReturn(true)
        whenever(mockSettings.networkHostname)
            .thenReturn("sample.domain.com")
        whenever(mockSettings.networkPort)
            .thenReturn(null)

        val url = UrlManager(mockSettings)

        assertEquals("https://sample.domain.com/api", url.base)
    }

    @Test
    fun base_portIsBlank_constructUrlWithoutPort() {
        val mockSettings: Settings = mock()
        whenever(mockSettings.networkUseHttps)
            .thenReturn(true)
        whenever(mockSettings.networkHostname)
            .thenReturn("sample.domain.com")
        whenever(mockSettings.networkPort)
            .thenReturn("")

        val url = UrlManager(mockSettings)

        assertEquals("https://sample.domain.com/api", url.base)
    }

    @Test
    fun base_portIsNotNull_constructUrlWithPort() {
        val mockSettings: Settings = mock()
        whenever(mockSettings.networkUseHttps)
            .thenReturn(false)
        whenever(mockSettings.networkHostname)
            .thenReturn("sample.domain.com")
        whenever(mockSettings.networkPort)
            .thenReturn("8080")

        val url = UrlManager(mockSettings)

        assertEquals("http://sample.domain.com:8080/api", url.base)
    }
}
