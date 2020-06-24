package com.nullexcom.qrscanner

import com.google.zxing.Result

interface Callback {
    fun apply(result: Result)
}