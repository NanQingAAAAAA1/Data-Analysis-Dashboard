package cn.moonshot.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Message {
    private String role;
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public static Map<String, Object> toMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("role", message.role);
        map.put("content", message.content);
        return map;
    }

    public static List<Map<String, Object>> toMapList(List<Message> messages) {
        return messages.stream()
                .map(Message::toMap)
                .collect(Collectors.toList());
    }
}