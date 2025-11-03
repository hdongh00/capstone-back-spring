package com.website.common.exception;

public class InvalidEnumValueException extends RuntimeException{
    public InvalidEnumValueException(String filed, String value, String allowed){
        super("잘못된 "+filed+ " 값입니다: "+value+" (허용 값): "+allowed+")");
    }
}
