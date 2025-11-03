package com.website.common.exception;

public class NotAllowException extends RuntimeException{
    public NotAllowException(){
        super("해킹하려고 하지 마세요.");
    }
}
