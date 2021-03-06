package com.ssafy.chat.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ssafy.chat.model.ChatMessage;
import com.ssafy.chat.model.Message;
import com.ssafy.chat.service.ChatService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@CrossOrigin(origins = { "*" }, maxAge = 6000)
public class ChatController {

	@Autowired
	private ChatService chatService;
	
	@Autowired
	private final SimpMessageSendingOperations messagingTemplate;

	@MessageMapping("/chat/message/{roomId}")
	@SendTo("/sub/chat/room/{roomId}")
	public ChatMessage message(@RequestBody ChatMessage message,@DestinationVariable String roomId) {
		

		message.setCreateDate(new Date());
		message.setRoomId(roomId);
		message.setContent(chatService.convertMessage(message.getContent()));
		System.out.println("###################");
		System.out.println(message);
		chatService.saveMessage(message);
//		messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
		return message;
		
	}


}