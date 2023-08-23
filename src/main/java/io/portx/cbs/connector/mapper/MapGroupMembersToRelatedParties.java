package io.portx.cbs.connector.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.portx.cbs.connector.property.GroupRoleNameKeys;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static io.portx.cbs.connector.mapper.Constants.*;

public class MapGroupMembersToRelatedParties implements Processor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GroupRoleNameKeys groupRoleNameKeys;
    private final Map<String, String> relatedPartiesMap;

    @Autowired
    public MapGroupMembersToRelatedParties(GroupRoleNameKeys groupRoleNameKeys) {
        this.groupRoleNameKeys = groupRoleNameKeys;
        this.relatedPartiesMap = initRelatedPartiesMap();
    }

    public void process(Exchange exchange) throws JsonProcessingException {

        if(exchange.getProperty(RAW_GROUP_MEMBERS) != null && !exchange.getProperty(RAW_GROUP_MEMBERS).equals("")) {
            String groupRoles = exchange.getProperty(RAW_GROUP_MEMBERS).toString();
            String stringGroupMembers = groupRoles.replaceAll(ARRAY_BUFFER,
                    GROUP_MEMBERS_REPLACEMENT);

            JsonNode jsonNode = objectMapper.readTree(stringGroupMembers);
            ArrayNode relatedParties = objectMapper.createArrayNode();
            jsonNode.get(GROUP_MEMBERS).forEach(groupMember -> {
                String partyId = groupMember.get(CLIENT_KEY).asText();
                ArrayNode roles = (ArrayNode) groupMember.get(ROLES);
                if (roles == null || roles.size() == 0) {
                    ObjectNode party = objectMapper.createObjectNode();
                    party.put(PARTY_ID, partyId);
                    relatedParties.add(party);
                } else {
                    roles.forEach(role -> {
                        ObjectNode party = objectMapper.createObjectNode();
                        party.put(PARTY_ID, partyId);
                        party.put(PARTY_TYPE, PARTY_TYPE_PERSON);
                        party.put(PARTY_RELATION_TYPE, relatedPartiesMap.get(role.get(GROUP_ROLE_NAME_KEY).asText()));
                        relatedParties.add(party);
                    });
                }
            });
            exchange.setProperty(RELATED_PARTIES, relatedParties);
        }
    }

    private Map<String, String> initRelatedPartiesMap() {
        return Map.of(
                groupRoleNameKeys.getTreasurer(), "Treasurer",
                groupRoleNameKeys.getPresident(), "President",
                groupRoleNameKeys.getSecretary(), "Secretary",
                groupRoleNameKeys.getAssigner(), "Assigner");

    }

}
