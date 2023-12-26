package com.utkangul.falci.internalClasses.dataClasses

data class ResetPasswordDataClass(
    var code: String?,
    var password: String?
)

val resetPasswordDataClass = ResetPasswordDataClass(
    code =  null,
    password = null
)
