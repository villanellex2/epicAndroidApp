package ru.edubinskaya.epics.app.model

class DeviceField constructor(
    val fieldName: String,
    val fieldType: FieldType,
    var fieldValue: Any? = null,
)
