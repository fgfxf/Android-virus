package com.example.andriodvirusclient;

import android.os.Binder;

class ObjectBinder extends Binder {
    private  Object obj;
    ObjectBinder(Object obj){
        this.obj=obj;
    }
    Object getObj(){
        return this.obj;
    }
}
