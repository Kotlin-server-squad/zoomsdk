package com.kss.zoom.test

import com.kss.zoom.module.meetings.model.api.MeetingRequest
import com.kss.zoom.module.meetings.model.api.MeetingResponse
import com.kss.zoom.module.meetings.model.api.UpdateMeetingRequest
import com.kss.zoom.module.users.model.api.CreateUserRequest
import com.kss.zoom.module.users.model.api.UpdateUserRequest
import com.kss.zoom.module.users.model.api.UserResponse
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.assertEquals
import kotlin.test.fail

fun MockRequestHandleScope.respondJson(json: String): HttpResponseData {
    return respond(
        content = ByteReadChannel(json),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    )
}

fun HttpRequestData.assertMethod(method: HttpMethod) {
    assertEquals(
        method,
        this.method,
        "Expected method $method, but was ${this.method}"
    )
}

fun HttpRequestData.assertUrl(url: String) {
    assertEquals(
        url,
        this.url.toString(),
        "Expected URL $url, but was ${this.url}"
    )
}

fun HttpRequestData.assertBasicAuth(clientId: String, clientSecret: String) {
    val authHeader = this.headers[HttpHeaders.Authorization]

    @OptIn(ExperimentalEncodingApi::class)
    val encodedCredentials = Base64.encode("$clientId:$clientSecret".encodeToByteArray())

    assertEquals(
        "Basic $encodedCredentials",
        authHeader,
        "Authorization header should be as expected"
    )
}


fun HttpRequestData.assertBearerAuth(token: String) {
    val authHeader = this.headers[HttpHeaders.Authorization]

    assertEquals(
        "Bearer $token",
        authHeader,
        "Authorization header should be as expected"
    )
}

fun HttpRequestData.assertBodyAsJson(body: String) {
    assertBody(TextContent(body, ContentType.Application.Json))
}

fun HttpRequestData.assertBody(body: OutgoingContent = EmptyContent) {
    if (body is EmptyContent && this.body !is EmptyContent) {
        fail("Expected empty body, but was ${this.body}")
    }
    assertEquals(
        body.contentType,
        this.body.contentType,
        "Expected content type ${body.contentType}, but was ${this.body.contentType}"
    )
    assertEquals(
        body.normalize().contentLength,
        this.body.normalize().contentLength,
        "Expected content length ${body.contentLength}, but was ${this.body.contentLength}"
    )
}

fun HttpRequestData.assertContentType(contentType: ContentType) {
    when (body) {
        is EmptyContent -> {
            assertEquals(
                contentType,
                this.headers[HttpHeaders.ContentType]?.let { ContentType.parse(it) },
                "Expected content type $contentType, but was ${this.headers[HttpHeaders.ContentType]}"
            )
        }

        else -> {
            assertEquals(
                contentType,
                this.body.contentType,
                "Expected content type $contentType, but was ${this.body.contentType}"
            )
        }
    }
}

fun OutgoingContent.normalize(): OutgoingContent {
    return when (this) {
        is TextContent -> {
            TextContent(text.replace(" ", "").replace("\n", ""), contentType)
        }

        else -> this
    }
}

fun MeetingResponse.toJson(): String =
    """
        {
            "id": $id,
            "uuid": "$uuid",
            "topic": "$topic",
            "duration": $duration,
            "host_id": "$hostId",
            "host_email": "$hostEmail",
            "status": "$status",
            "start_time": "$startTime",
            "created_at": "$createdAt",
            "timezone": "$timezone",
            "start_url": "$startUrl",
            "join_url": "$joinUrl",
            "password": "$password"
        }
    """.trimIndent()

fun MeetingRequest.toJson(): String =
    """
        {
            "topic": "$topic",
            "type": $type,
            "start_time": "$startTime",
            "duration": $duration,
            "timezone": "$timezone"
        }
    """.trimIndent()

fun UpdateMeetingRequest.toJson(): String =
    """
        {
            "topic": "$topic",
            "start_time": "$startTime",
            "duration": $duration,
            "timezone": "$timezone"
        }
    """.trimIndent()

fun CreateUserRequest.toJson(): String =
    """
        {
            "action": "$action",
            "user_info": {
                "email": "${userInfo.email}",
                "first_name": "${userInfo.firstName}",
                "last_name": "${userInfo.lastName}",
                "display_name": "${userInfo.displayName}",
                "type": ${userInfo.type}
            }
        }
    """.trimIndent()

fun UpdateUserRequest.toJson(): String =
    """
        {
            "company": ${company.toNullableString()},
            "department": ${department.toNullableString()},
            "first_name": ${firstName.toNullableString()},
            "last_name": ${lastName.toNullableString()},
            "job_title": ${jobTitle.toNullableString()},
            "language": ${language.toNullableString()},
            "phone_numbers": ${phoneNumbers.joinToString(prefix = "[", postfix = "]")},
            "pmi": $personalMeetingId
        }
    """.trimIndent()

fun UserResponse.toJson(): String =
    """
        {
            "first_name": "$firstName",
            "last_name": "$lastName",
            "id": "$id",
            "type": $type,
            "email": ${email.toNullableString()},
            "company": ${company.toNullableString()}
        }
    """.trimIndent()

fun String?.toNullableString(): String? = this?.let { "\"$it\"" }
