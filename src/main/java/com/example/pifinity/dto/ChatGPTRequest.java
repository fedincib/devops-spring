package com.example.pifinity.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatGPTRequest {
    private String model;

    private List<Message> messages;

}