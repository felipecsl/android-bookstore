package com.example.android.api;

import com.example.android.model.AutoParcelAdapterFactory;
import com.example.android.model.Book;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

@Module(library = true)
public class ApiClientModule {

    public static final String API_BASE_URL = "http://prolific-interview.herokuapp.com/541a7ea40b049e036123749e";
    private final BookService service;

    public ApiClientModule() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_BASE_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(new GsonConverter(getGson()))
                .build();

        service = restAdapter.create(BookService.class);
    }

    @Provides @Singleton public ApiClientModule provideApiClient() {
        return new ApiClientModule();
    }

    public BookService getService() {
        return service;
    }

    private static Gson getGson() {
        return new GsonBuilder().registerTypeAdapterFactory(new AutoParcelAdapterFactory())
                .create();
    }

    public interface BookService {
        @GET("/books") Observable<List<Book>> listBooks();

        @FormUrlEncoded @POST("/books") Observable<Book> addBook(
                @Field("author") String author,
                @Field("categories") String categories,
                @Field("title") String title,
                @Field("publisher") String publisher,
                @Field("lastCheckedOutBy") String lastCheckedOutBy);

        @GET("/{bookUrl}") Observable<Book> getBook(@Path("bookUrl") String bookUrl);

        @FormUrlEncoded @PUT("/{bookUrl}") Observable<Book> checkoutBook(
                @Path("bookUrl") String bookUrl,
                @Field("lastCheckedOutBy") String lastCheckedOutBy);

        @FormUrlEncoded @PUT("/{bookUrl}") Observable<Book> updateBook(
                @Path("bookUrl") String bookUrl,
                @Field("title") String title,
                @Field("author") String author,
                @Field("categories") String categories,
                @Field("publisher") String publisher);

        @DELETE("/{bookUrl}") Observable<String> deleteBook(@Path("bookUrl") String bookUrl);

        @DELETE("/clean") void deleteAllBooks();
    }
}
