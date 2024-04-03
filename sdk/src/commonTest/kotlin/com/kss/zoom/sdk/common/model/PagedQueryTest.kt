package com.kss.zoom.sdk.common.model

import com.kss.zoom.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class PagedQueryTest {

    @Test
    fun shouldAcceptValidConstructor() {
        val query = PagedQuery(
            pageNumber = 1,
            pageSize = 10,
            nextPageToken = "token"
        )
        assertEquals(1, query.pageNumber)
        assertEquals(10, query.pageSize)
        assertEquals("token", query.nextPageToken)
    }

    @Test
    fun pageNumberMustBeGreaterZero() {
        val exception = assertThrows(IllegalArgumentException::class) {
            pagedQuery(pageNumber = 0)
        }
        assertEquals("Page number must be greater than 0.", exception.message)
    }

    @Test
    fun pageSizeMustBeGreaterZero() {
        val exception = assertThrows(IllegalArgumentException::class) {
            pagedQuery(pageSize = 0)
        }
        assertEquals("Page size must be between 1 and 1000.", exception.message)
    }

    @Test
    fun pageSizeMustBeLessThanMaxPageSize() {
        val exception = assertThrows(IllegalArgumentException::class) {
            pagedQuery(pageSize = 1001)
        }
        assertEquals("Page size must be between 1 and 1000.", exception.message)
    }

    @Test
    fun nextPageTokenMustNotBeBlankIfPresent() {
        val exception = assertThrows(IllegalArgumentException::class) {
            pagedQuery(nextPageToken = " ")
        }
        assertEquals("Next page token must not be blank.", exception.message)
    }

    private fun pagedQuery(
        pageNumber: Int = 1,
        pageSize: Int = 10,
        nextPageToken: String? = null
    ) = PagedQuery(
        pageNumber = pageNumber,
        pageSize = pageSize,
        nextPageToken = nextPageToken
    )
}