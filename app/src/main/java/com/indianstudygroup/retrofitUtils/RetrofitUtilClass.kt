package com.indianstudygroup.retrofitUtils

import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitUtilClass {
    companion object {
        private lateinit var retrofit: Retrofit
        private lateinit var retrofit2: Retrofit
        val API_BASE_URL = "https://indian-study-group.onrender.com/"
        val PINCODE_BASE_URL = "http://www.postalpincode.in/api/"

        fun getRetrofit(): Retrofit {

            if (!::retrofit.isInitialized) {

                val authInterceptor = Interceptor { chain ->
                    val user = FirebaseAuth.getInstance().currentUser
                    val request = chain.request().newBuilder()
                    user?.let {
                        // Add the UID as a header to the request
                        request.addHeader("uid", "")
                    }
                    chain.proceed(request.build())
                }

                retrofit = Retrofit.Builder().baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(
                        OkHttpClient.Builder().connectTimeout(180, TimeUnit.SECONDS)
                            .readTimeout(180, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS)
//                            .addInterceptor(authInterceptor) // Add the auth interceptor
                            .addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            }).build()
                    ).build()

                return retrofit
            }
            return retrofit
        }


        fun getRetrofitPincode(): Retrofit {

            if (!::retrofit2.isInitialized) {
                retrofit2 = Retrofit.Builder().baseUrl(PINCODE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(
                        OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                            .addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            }).build()
                    ).build()
                return retrofit2
            }
            return retrofit2
        }
    }
}
