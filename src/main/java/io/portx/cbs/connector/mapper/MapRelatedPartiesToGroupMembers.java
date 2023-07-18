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

import static io.portx.cbs.connector.mapper.Constants.*;

public class MapRelatedPartiesToGroupMembers implements Processor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final GroupRoleNameKeys groupRoleNameKeys;

    @Autowired
    public MapRelatedPartiesToGroupMembers(GroupRoleNameKeys groupRoleNameKeys) {
        this.groupRoleNameKeys = groupRoleNameKeys;
    }

    public void process(Exchange exchange) throws JsonProcessingException {
        if(exchange.getProperty(RAW_RELATED_PARTIES) != null && !exchange.getProperty(RAW_RELATED_PARTIES).equals("")) {
            String groupRoles = exchange.getProperty(RAW_RELATED_PARTIES).toString();
            String stringRelatedParties = groupRoles.replaceAll(ARRAY_BUFFER,
                    RELATED_PARTIES_REPLACEMENT);

            JsonNode rootNode = objectMapper.readTree(stringRelatedParties);

            ArrayNode relatedPartiesNode = (ArrayNode) rootNode.get(RELATED_PARTIES);
            ArrayNode groupMembersNode = objectMapper.createArrayNode();
            relatedPartiesNode.forEach(relatedPartyNode -> {
                String clientKey = relatedPartyNode.get(PARTY_ID).asText();
                String groupRoleNameKey = relatedPartyNode.has(PARTY_RELATION_TYPE)
                        ? relatedPartyNode.get(PARTY_RELATION_TYPE).asText() : null;
                ObjectNode groupMemberNode = null;
                for (JsonNode memberNode : groupMembersNode) {
                    if (clientKey.equals(memberNode.get(CLIENT_KEY).asText())) {
                        groupMemberNode = (ObjectNode) memberNode;
                        break;
                    }
                }
                if (groupMemberNode == null) {
                    groupMemberNode = objectMapper.createObjectNode();
                    groupMemberNode.put(CLIENT_KEY, clientKey);
                    groupMemberNode.set(ROLES, objectMapper.createArrayNode());
                    groupMembersNode.add(groupMemberNode);
                }

                if (groupRoleNameKey != null) {
                    ArrayNode rolesNode = (ArrayNode) groupMemberNode.get(ROLES);
                    ObjectNode roleNode = objectMapper.createObjectNode();
                    roleNode.put(GROUP_ROLE_NAME_KEY, groupRoleNameKeys.getKey(groupRoleNameKey));
                    rolesNode.add(roleNode);
                }
            });
            exchange.setProperty(GROUP_MEMBERS, groupMembersNode);
        }
    }

}