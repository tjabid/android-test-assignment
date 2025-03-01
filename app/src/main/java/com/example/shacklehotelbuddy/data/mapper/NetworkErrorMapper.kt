package com.example.shacklehotelbuddy.data.mapper

import com.example.shacklehotelbuddy.data.mapper.HttpStatusCode.CODE_FORBIDDEN_REQUEST
import com.example.shacklehotelbuddy.data.remote.model.NetworkError
import com.example.shacklehotelbuddy.data.mapper.HttpStatusCode.CODE_SERVER_INTERNAL_ERROR
import com.example.shacklehotelbuddy.data.mapper.HttpStatusCode.CODE_SERVER_MAINTENANCE
import com.example.shacklehotelbuddy.data.mapper.HttpStatusCode.CODE_SERVER_UNAVAILABLE
import com.example.shacklehotelbuddy.data.mapper.HttpStatusCode.CODE_UNAUTHORIZED
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import com.example.shacklehotelbuddy.data.mapper.HttpStatusCode.CODE_INVALID_REQUEST
import com.example.shacklehotelbuddy.data.mapper.HttpStatusCode.CODE_SERVER_NOT_FOUND
import timber.log.Timber
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorMapper {

    fun <T> toErrorCause(
        response: Response<T>? = null,
        throwable: Throwable? = null
    ): NetworkError = when {
        throwable?.isConnectionException == true -> NetworkError.Connection
        throwable?.isConnectionTimeoutException == true -> NetworkError.ConnectionTimeout
        response?.isCodeUnauthorized == true -> NetworkError.Unauthorized
        response?.isCodeServerInternalError == true -> NetworkError.ServerInternalError
        response?.isCodeServerUnavailable == true -> NetworkError.ServerTemporaryUnavailable
        response?.isCodeServerMaintenance == true -> NetworkError.ServerMaintenance
        response?.isInvalidRequest == true -> NetworkError.InvalidRequest
        response?.isForbiddenRequest == true -> NetworkError.Authentication
        response?.errorBody() != null -> NetworkError.OperationCode(
            response.errorBody()!!.operationCode, response.code()
        )
        else -> NetworkError.Unknown(
            response?.toErrorString()
                ?: throwable?.toErrorString()
                ?: ""
        )
    }

    fun toErrorCause(throwable: Throwable): NetworkError = when {
        throwable.isConnectionException -> NetworkError.Connection
        throwable.isConnectionTimeoutException -> NetworkError.ConnectionTimeout
        else -> NetworkError.Unknown(throwable.message ?: throwable.toErrorString())
    }

    private fun Throwable.toErrorString() =
        when (this) {
            is HttpException -> "${code()}: $message"
            else -> localizedMessage ?: ""
        }

    private fun Response<*>.toErrorString() = "${code()} : ${message()}"

    private val Throwable.isConnectionException: Boolean
        get() = this is ConnectException ||
                this is UnknownHostException ||
                this is NoRouteToHostException ||
                this.cause?.isConnectionException ?: false

    private val Throwable.isConnectionTimeoutException: Boolean
        get() = this is SocketTimeoutException || this is InterruptedIOException

    private val Response<*>.isCodeUnauthorized
        get() = code() == CODE_UNAUTHORIZED

    private val Response<*>.isCodeServerInternalError
        get() = code() == CODE_SERVER_INTERNAL_ERROR

    private val Response<*>.isCodeServerUnavailable
        get() = code() == CODE_SERVER_UNAVAILABLE || code() == CODE_SERVER_NOT_FOUND

    private val Response<*>.isCodeServerMaintenance
        get() = code() == CODE_SERVER_MAINTENANCE

    private val Response<*>.isInvalidRequest
        get() = code() == CODE_INVALID_REQUEST

    private val Response<*>.isForbiddenRequest
        get() = code() == CODE_FORBIDDEN_REQUEST
}

val ResponseBody.operationCode: String
    get() = try {
        JSONObject(string()).getString("status")
    } catch (e: Exception) {
        Timber.e(e)
        ""
    }

object HttpStatusCode {
    const val CODE_INVALID_REQUEST = 400
    const val CODE_UNAUTHORIZED = 401
    const val CODE_FORBIDDEN_REQUEST = 403
    const val CODE_SERVER_NOT_FOUND = 404
    const val CODE_SERVER_INTERNAL_ERROR = 500
    const val CODE_SERVER_UNAVAILABLE = 502
    const val CODE_SERVER_MAINTENANCE = 503
}