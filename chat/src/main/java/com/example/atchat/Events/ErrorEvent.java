package com.example.atchat.Events;

import com.example.atchat.ErrorType;

public class ErrorEvent {

    public ErrorType errorType;

    public ErrorEvent(ErrorType errorType) {
        this.errorType = errorType;
    }
}
