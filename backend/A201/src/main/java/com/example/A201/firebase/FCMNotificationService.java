package com.example.A201.firebase;

import com.example.A201.alarm.domain.constant.Receiver;
import com.example.A201.member.domain.Member;
import com.example.A201.member.domain.Role;
import com.example.A201.member.repository.MemberRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class FCMNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOperations;

    public String sendNotificationByToken(FCMNotificationRequestDto requestDto) {

        if (requestDto.getReceiver() == Receiver.USER) {

            String token = valueOperations.get(String.valueOf(requestDto.getTargetUserId()));
            if (token == null) {
                return "알림 전송 실패";
            }
            Notification notification = Notification.builder()
                    .setTitle(requestDto.getTitle())
                    .setBody(requestDto.getBody())
                    .build();

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();

            try {
                firebaseMessaging.send(message);
                return "알림을 성공적으로 전송했습니다. targetUserId=" + requestDto.getTargetUserId();
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                return "알림 보내기를 실패하였습니다. targetUserId=" + requestDto.getTargetUserId();
            }

        }

        // 관리자 일때
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Member> members = memberRepository.findByRole(Role.ADMIN).stream().filter(
                member -> valueOperations.get(String.valueOf(member.getMemberId())) != null)
                .collect(Collectors.toList());

        List<CompletableFuture<Void>> futures = members.stream()
                .map(member -> CompletableFuture.runAsync(() -> {
                        Notification notification = Notification.builder()
                                .setTitle(requestDto.getTitle())
                                .setBody(requestDto.getBody())
                                .build();

                        Message message = Message.builder()
                                .setToken(valueOperations.get(String.valueOf(member.getMemberId())))
                                .setNotification(notification)
                                .build();
                        try {
                            firebaseMessaging.send(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }, executorService))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        executorService.shutdown();

        return "알림을 보냈습니다.";

    }

    public void updateFirebase(String firebaseToken, Long userId) {
        valueOperations.set(String.valueOf(userId), firebaseToken);
    }
}


