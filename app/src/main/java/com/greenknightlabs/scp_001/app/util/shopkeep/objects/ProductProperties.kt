package com.greenknightlabs.scp_001.app.util.shopkeep.objects

import com.greenknightlabs.scp_001.users.enums.UserEntitlements

open class ProductProperties(
    val id: String,
    val price: String,
    val unlockedByEntitlement: UserEntitlements?,
    val previewComponentId: Int?
)
