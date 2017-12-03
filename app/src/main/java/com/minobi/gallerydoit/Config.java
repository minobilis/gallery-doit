package com.minobi.gallerydoit;

public interface Config {

    interface NETWORK {
        String BASE_URL = "http://api.doitserver.in.ua";

        interface HEADER {
            String AUTH_TOKEN = "token";
        }

        interface ENDPOINT {
            String SIGN_IN = "/login";
            String SIGN_UP = "/create";
            String ADD_IMAGE = "/image";
            String GET_ALL_IMAGES = "/all";
            String GET_GIF = "/gif";
        }

        interface QUERY {
            String USER_NAME = "username";
            String EMAIL = "email";
            String PASSWORD = "password";
            String AVATAR = "avatar";
            String IMAGE_DESCRIPTION = "description";
            String IMAGE_HASH_TAG = "hashtag";
            String IMAGE_LATITUDE = "latitude";
            String IMAGE_longitude = "longitude";
        }

    }

    interface USER {
        String AUTH_TOKEN = "auth_token";
        String USER_NAME = "user_name";
        String USER_EMAIL = "user_email";
        String USER_PASSWORD = "user_password";
        String USER_AVATAR_URL = "user_avatar_url";
        String USER_LOGGED_IN = "user_logger_in";
    }
}
