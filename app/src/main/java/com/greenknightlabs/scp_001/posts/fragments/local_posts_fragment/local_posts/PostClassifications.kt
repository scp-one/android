package com.greenknightlabs.scp_001.posts.fragments.local_posts_fragment.local_posts

import com.greenknightlabs.scp_001.posts.enums.PostStatus
import com.greenknightlabs.scp_001.posts.enums.PostVisibility
import com.greenknightlabs.scp_001.posts.models.Post
import com.greenknightlabs.scp_001.users.models.UserRef

fun getPostClassifications(): Post {
    return Post(
        "2",
        UserRef(false, "123", null),
        "Classifications",
        """
        All anomalous objects, entities, and phenomena requiring Special Containment Procedures are assigned an Object Class. An Object Class is a part of the standard SCP template and serves as a rough indicator for how difficult an object is to contain. In universe, Object Classes are for the purposes of identifying containment needs, research priority, budgeting, and other considerations. An SCP's Object Class is determined by a number of factors, but the most important factors are the difficulty and the purpose of its containment.

        ## Safe
        Safe-class SCPs are anomalies that are easily and safely contained. This is often due to the fact that the Foundation has researched the SCP well enough that containment does not require significant resources or that the anomalies require a specific and conscious activation or trigger. Classifying an SCP as Safe, however, does not mean that handling or activating it does not pose a threat.

        ## Euclid
        Euclid-class SCPs are anomalies that require more resources to contain completely or where containment isn't always reliable. Usually this is because the SCP is insufficiently understood or inherently unpredictable. Euclid is the Object Class with the greatest scope, and it's usually a safe bet that an SCP will be this class if it doesn't easily fall into any of the other standard Object Classes.

        As a note, any SCP that's autonomous, sentient and/or sapient is generally classified as Euclid, due to the inherent unpredictability of an object that can act or think on its own.

        ## Keter
        Keter-class SCPs are anomalies that are exceedingly difficult to contain consistently or reliably, with containment procedures often being extensive and complex. The Foundation often can't contain these SCPs well due to not having a solid understanding of the anomaly, or lacking the technology to properly contain or counter it. A Keter SCP does not mean the SCP is dangerous, just that it is simply very difficult or costly to contain.

        ## Thaumiel
        Thaumiel-class SCPs are anomalies that the Foundation specifically uses to contain other SCPs. Even the mere existence of Thaumiel-class objects is classified at the highest levels of the Foundation and their locations, functions, and current status are known to few Foundation personnel outside of the O5 Council.

        ## Neutralized
        Neutralized SCPs are anomalies that are no longer anomalous, either through having been intentionally or accidentally destroyed, or disabled.

        ## Apollyon
        Apollyon-class SCPs are anomalies that cannot be contained, are expected to breach containment imminently, or some other similar scenario. Such anomalies are usually associated with world-ending threats or a K-Class Scenario of some kind and require a massive effort from the Foundation to deal with.

        ## Archon
        Archon-class SCPs are anomalies that could theoretically be contained but are best left uncontained for some reason. Archon SCPs may be a part of consensus reality that is difficult to fully contain or may have adverse effects if put into containment. These SCPs are not uncontainable—the defining feature of the class is that the Foundation chooses to not put the anomaly into containment.

        [https://scp-wiki.wikidot.com/object-classes](/object-classes)
        """.trimIndent(),
        null,
        PostVisibility.VISIBLE,
        PostStatus.APPROVED,
        null,
        0,
        0,
        null,
        "123",
        "123",
        false
    )
}
