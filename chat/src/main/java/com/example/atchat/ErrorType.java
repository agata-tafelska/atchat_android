package com.example.atchat;

import androidx.annotation.StringRes;

public enum ErrorType {
    ALREADY_EXISTS(R.string.username_exists_message),
    UNAUTHENTICATED(R.string.incorrect_credentials_message),
    UNAVAILABLE(R.string.unable_to_connect_message),
    UNKNOWN(R.string.unknown_error_message);

    @StringRes int messageResourceId;

    ErrorType(@StringRes int messageResourceId) {
        this.messageResourceId = messageResourceId;
    }
}
