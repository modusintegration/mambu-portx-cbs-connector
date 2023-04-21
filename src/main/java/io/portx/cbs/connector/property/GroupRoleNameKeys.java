package io.portx.cbs.connector.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupRoleNameKeys {

    @Value("${groupRoles.Treasurer.groupRoleNameKey}")
    private String treasurer;
    @Value("${groupRoles.President.groupRoleNameKey}")
    private String president;
    @Value("${groupRoles.Secretary.groupRoleNameKey}")
    private String secretary;
    @Value("${groupRoles.Assigner.groupRoleNameKey}")
    private String assigner;

    public String getAssigner() {
        return assigner;
    }

    public String getPresident() {
        return president;
    }

    public String getSecretary() {
        return secretary;
    }

    public String getTreasurer() {
        return treasurer;
    }

    public String getKey(String groupRoleName) {
        switch (groupRoleName) {
            case "Treasurer":
                return getTreasurer();
            case "President":
                return getPresident();
            case "Secretary":
                return getSecretary();
            case "Assigner":
                return getAssigner();
            default:
                throw new IllegalArgumentException("Invalid group role name: " + groupRoleName);
        }
    }
}
