package com.example.andriodvirusclient;

import android.os.Binder;

class StringBinder extends Binder {
    private String string;
    StringBinder(String string){
        this.string=string;
    }
    String getString(){
        return this.string;
    }

}
