package io.portx.cbs.connector.mapper;

public class Constants {
    static final String RAW_GROUP_MEMBERS = "rawGroupMembers";
    static final String ARRAY_BUFFER = "ArrayBuffer\\((.*)\\)";
    static final String GROUP_MEMBERS_REPLACEMENT = "{\"groupMembers\": [$1]}";
    static final String GROUP_MEMBERS = "groupMembers";
    static final String CLIENT_KEY = "clientKey";
    static final String ROLES = "roles";
    static final String PARTY_ID = "partyId";
    static final String PARTY_RELATION_TYPE = "partyRelationType";
    static final String GROUP_ROLE_NAME_KEY = "groupRoleNameKey";
    static final String RELATED_PARTIES = "relatedParties";
    public static final String RAW_RELATED_PARTIES = "rawRelatedParties";
    public static final String RELATED_PARTIES_REPLACEMENT = "{\"relatedParties\": [$1]}";
}
