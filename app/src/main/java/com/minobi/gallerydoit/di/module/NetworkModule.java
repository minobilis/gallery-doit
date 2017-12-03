package com.minobi.gallerydoit.di.module;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.minobi.gallerydoit.Config;
import com.minobi.gallerydoit.data.AddImageResponse;
import com.minobi.gallerydoit.data.GetAllImagesResponse;
import com.minobi.gallerydoit.data.GetGifResponse;
import com.minobi.gallerydoit.data.SignInResponse;
import com.minobi.gallerydoit.data.SignUpResponse;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Maybe;
import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

@Module 
public class NetworkModule {
    @Singleton
    @Provides
    DoItRestClient provideDoItRestClient (Retrofit retrofit) {
        return retrofit.create(DoItRestClient.class);
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Config.NETWORK.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public interface DoItRestClient {
        @Multipart
        @POST(Config.NETWORK.ENDPOINT.SIGN_IN)
        Maybe<Response<SignInResponse>> signIn(
                @Part(Config.NETWORK.QUERY.EMAIL) String email,
                @Part(Config.NETWORK.QUERY.PASSWORD) String password);

        @Multipart
        @POST(Config.NETWORK.ENDPOINT.SIGN_UP)
        Maybe<Response<SignUpResponse>> signUp(
                @Part(Config.NETWORK.QUERY.USER_NAME) String username,
                @Part(Config.NETWORK.QUERY.EMAIL) String email,
                @Part(Config.NETWORK.QUERY.PASSWORD) String password,
                @Part MultipartBody.Part image);

        @Multipart
        @POST(Config.NETWORK.ENDPOINT.ADD_IMAGE)
        Maybe<Response<AddImageResponse>> uploadImage(
                @Header(Config.NETWORK.HEADER.AUTH_TOKEN) String token,
                @Part(Config.NETWORK.QUERY.IMAGE_DESCRIPTION) String imageDescription,
                @Part(Config.NETWORK.QUERY.IMAGE_HASH_TAG) String imageHashTag,
                @Part(Config.NETWORK.QUERY.IMAGE_longitude) float imageLongitude,
                @Part(Config.NETWORK.QUERY.IMAGE_LATITUDE) float imageLatitude,
                @Part MultipartBody.Part image);

        @GET(Config.NETWORK.ENDPOINT.GET_ALL_IMAGES)
        Maybe<Response<GetAllImagesResponse>> getImages(
                @Header(Config.NETWORK.HEADER.AUTH_TOKEN) String token);

        @GET(Config.NETWORK.ENDPOINT.GET_GIF)
        Maybe<Response<GetGifResponse>> getGif(
                @Header(Config.NETWORK.HEADER.AUTH_TOKEN) String token);
    }
} 