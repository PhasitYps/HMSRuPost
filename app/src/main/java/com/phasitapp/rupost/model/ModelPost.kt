package com.phasitapp.rupost.model

class ModelPost (
    var id: String? = null,
    var uid: String? = null,
    var username: String? = null,
    var profile: String? = null,
    var category: String? = null,
    var targetGroup: String? = null,
    var title: String? = null,
    var desciption: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var address: String? = null,
    var viewer: Int? = 0,

    var createDate: String? = null,
    var updateDate: String? = null,
    var images: List<String> = ArrayList()
)