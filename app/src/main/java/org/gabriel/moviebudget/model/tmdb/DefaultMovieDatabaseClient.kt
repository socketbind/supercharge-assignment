package org.gabriel.moviebudget.model.tmdb

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.gabriel.moviebudget.injection.TmdbApiKey
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import rx.schedulers.Schedulers
import javax.inject.Inject


class DefaultMovieDatabaseClient @Inject constructor(@TmdbApiKey val apiKey: String) : TheMovieDatabaseClient {

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(client)
            .build()

    private val service: TheMovieDatabaseService = retrofit.create(TheMovieDatabaseService::class.java)

    override fun searchMovie(query: String, language: String?, page: Int?, includeAdult: Boolean?, region: String?, year: Int?, primaryReleaseYear: Int?)
            = service.searchMovies(apiKey, query, language, page, includeAdult, region, year, primaryReleaseYear)
            .subscribeOn(Schedulers.io())

    override fun movieDetails(id: String, language: String?, appendToResponse: String?)
            = service.movieDetails(apiKey, id, language, appendToResponse)
            .subscribeOn(Schedulers.io())
}