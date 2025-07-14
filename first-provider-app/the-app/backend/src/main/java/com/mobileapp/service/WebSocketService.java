package com.mobileapp.service;

import com.mobileapp.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendBillUpdate(Bill bill) {
        messagingTemplate.convertAndSend("/topic/bills", bill);
    }

    public void sendBillCountUpdate(long count) {
        messagingTemplate.convertAndSend("/topic/bill-count", count);
    }
} 