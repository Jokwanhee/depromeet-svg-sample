package com.jkh.svgsample

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

object Repository {
    private lateinit var apiService: APIService

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val intercept = httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY // to check the log
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(intercept)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://svgshare.com") // No base url needed in this project, cz we will download svg image from url
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        apiService = retrofit.create(APIService::class.java)
    }

    fun downloadFileFromServer(url: String): LiveData<ResponseBody> {
        val responseBodyLD = MutableLiveData<ResponseBody>()
        val apiObject = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Some","response found null")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    responseBodyLD.value = response.body()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        apiService.downloadFileWithDynamicUrlAsync(
            url
        ).enqueue(apiObject)
        return responseBodyLD
    }
}

interface APIService {
    @Streaming
    @GET
    fun downloadFileWithDynamicUrlAsync(
        @Url fileUrl: String
    ): Call<ResponseBody>
}

fun getHTMLBody(svgString: String) = "<!DOCTYPE HTML>\n" +
        "<html>\n" +
        "\n" +
        "<head>\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=10\">\n" +
        "    <style>\n" +
        "        body {\n" +
        "            text-align: center;\n" +
        "        }\n" +
        "    </style>\n" +
        "</head>\n" +
        "\n" +
        "<body>\n" +
        "    <div id=\"div\" class=\"container\">\n" +
        "\t\n" +
        svgString +
        "\n" +
        "    <script src=\"index.js\"></script>\n" +
        "</body>\n" +
        "\n" +
        "</html>"