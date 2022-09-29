package com.phasitapp.rupost.model

import java.io.Serializable

class ModelComment (
    var id: String? = null,
    var uid: String? = null,
    var username: String? = null,
    var profile: String? = null,
    var message: String? = null,
    var createDate: String? = null,

    //more
    var countLike: Int = 0,
    var currentUserLike: Boolean = false,
    var postId: String? = null

): Serializable