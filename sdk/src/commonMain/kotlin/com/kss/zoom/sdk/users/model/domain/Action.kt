package com.kss.zoom.sdk.users.model.domain

enum class Action(val value: String) {
    CREATE("create"), // The user receives an email from Zoom containing a confirmation link. The user must then use the link to activate their Zoom account. The user can then set or change their password.
    SSO_CREATE("ssoCreate"), // This action is for Enterprise customers with a managed domain. autoCreate creates an email login type for users.
    AUTO_CREATE("autoCreate"), // Users created with this action do not have passwords and will not have the ability to log into the Zoom web portal or the Zoom client. These users can still host and join meetings using the start_url and join_url respectively. To use this option, you must contact the Integrated Software Vendor (ISV) sales team.
    CUST_CREATE("custCreate"), // This action is provided for the enabled “Pre-provisioning SSO User” option. A user created this way has no password. If it is not a Basic user, a personal vanity URL with the username (no domain) of the provisioning email is generated. If the username or PMI is invalid or occupied, it uses a random number or random personal vanity URL.
}