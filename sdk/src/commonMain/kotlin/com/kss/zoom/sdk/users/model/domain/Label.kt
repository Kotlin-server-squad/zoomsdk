package com.kss.zoom.sdk.users.model.domain

enum class Label(val value: String) {
    MOBILE("Mobile"), OFFICE("Office"), HOME("Home"), FAX("Fax");

    companion object {
        fun fromString(value: String): Label {
            return entries.find { it.value == value } ?: throw IllegalArgumentException("Unknown value: $value")
        }
    }
}
