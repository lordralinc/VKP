package dev.idm.vkp.idmapi

import dev.idm.vkp.idmapi.models.AuthToken
import dev.idm.vkp.idmapi.models.Command
import dev.idm.vkp.idmapi.models.User
import dev.idm.vkp.idmapi.models.Verified
import dev.idm.vkp.idmapi.requests.GetTokenByVKToken
import dev.idm.vkp.idmapi.requests.GetUserById
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface IdmApiService {
    @GET("users/getVerified/")
    fun getVerified(): Observable<Verified>

    @GET("special/getCommands/")
    fun getCommands(): Observable<Command>

    @POST("auth/getTokenByVKToken/")
    fun getTokenByVKToken(@Body() body: GetTokenByVKToken): Observable<AuthToken>

    @POST("users/getById/")
    fun getUserById(@Body body: GetUserById, @Header("Authorization") token: String): Observable<User>


    companion object Factory {
        fun create(): IdmApiService {
            val interceptor = run {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                }
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://irisduty.ru/api/")
                .build()

            return retrofit.create(IdmApiService::class.java);
        }
    }
}