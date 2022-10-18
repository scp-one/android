package com.greenknightlabs.scp_001.app.util.shopkeep.enums

import com.greenknightlabs.scp_001.app.util.shopkeep.objects.ProductProperties

enum class TipJarOptions(val rawValue: String) {
    Option1("Slice of Pizza"),
    Option2("Slices of Pizza"),
    Option3("Whole Pizza");

    fun displayName(): String {
        return this.rawValue
    }

    fun productProperties(): ProductProperties {
        return when (this) {
            Option1 -> ProductProperties(
                "com.greenknightlabs.and.scp_one.tip_jar_option_1.101722",
                "$1",
                null,
                null
            )
            Option2 -> ProductProperties(
                "com.greenknightlabs.and.scp_one.tip_jar_option_2.101722",
                "$5",
                null,
                null
            )
            Option3 -> ProductProperties(
                "com.greenknightlabs.and.scp_one.tip_jar_option_3.101722",
                "$10",
                null,
                null
            )
        }
    }
}
