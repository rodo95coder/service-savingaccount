package com.nttdata.bootcamp.exceptions;

public class PersonalCreditNotFoundException extends InterruptedException {
    public PersonalCreditNotFoundException( String message ){ super(message); }
}