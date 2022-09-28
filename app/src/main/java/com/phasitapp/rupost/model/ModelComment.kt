package com.phasitapp.rupost.model

import java.io.Serializable

class ModelComment (
    var id: String? = null,
    var uid: String? = null,
    var username: String? = null,
    var profile: String? = null,
    var message: String? = null,
    var createDate: String? = null,
): Serializable