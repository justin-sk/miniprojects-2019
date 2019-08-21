package com.wootecobook.turkey.friend.service;

import com.wootecobook.turkey.friend.domain.FriendAskRepository;
import com.wootecobook.turkey.friend.service.dto.FriendAskCreate;
import com.wootecobook.turkey.friend.service.dto.FriendAskResponse;
import com.wootecobook.turkey.friend.service.exception.FriendAskFailException;
import com.wootecobook.turkey.user.domain.UserRepository;
import com.wootecobook.turkey.user.service.UserService;
import com.wootecobook.turkey.user.service.dto.UserRequest;
import com.wootecobook.turkey.user.service.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
class FriendAskServiceTest {

    private static final String SENDER_NAME = "sender";
    private static final String SENDER_EMAIL = "sender@abc.abc";
    private static final String RECEIVER_NAME = "receiver";
    private static final String RECEIVER_EMAIL = "receiver@abc.abc";
    private static final String VALID_PASSWORD = "P@ssw0rd";

    private FriendAskService friendAskService;
    private UserService userService;

    private Long senderId;
    private Long receiverId;

    @Autowired
    public FriendAskServiceTest(UserRepository userRepository, FriendAskRepository friendAskRepository) {
        this.userService = new UserService(userRepository);
        this.friendAskService = new FriendAskService(friendAskRepository, userService);
    }

    @BeforeEach
    void setUp() {
        UserRequest senderRequest = UserRequest.builder()
                .email(SENDER_EMAIL)
                .name(SENDER_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserRequest receiverRequest = UserRequest.builder()
                .email(RECEIVER_EMAIL)
                .name(RECEIVER_NAME)
                .password(VALID_PASSWORD)
                .build();

        UserResponse senderResponse = userService.save(senderRequest);
        UserResponse receiverResponse = userService.save(receiverRequest);

        senderId = senderResponse.getId();
        receiverId = receiverResponse.getId();
    }

    @Test
    void 친구_요청_생성() {
        //given
        FriendAskCreate friendAskCreate = FriendAskCreate.builder()
                .receiverId(receiverId)
                .build();

        //when
        FriendAskResponse friendAskResponse = friendAskService.save(senderId, friendAskCreate);

        //then
        assertThat(friendAskResponse.getSenderId()).isEqualTo(senderId);
        assertThat(friendAskResponse.getReceiverId()).isEqualTo(receiverId);
    }

    @Test
    void 친구_요청_조회() {
        //given
        FriendAskCreate friendAskCreate = FriendAskCreate.builder()
                .receiverId(receiverId)
                .build();
        FriendAskResponse friendAskResponse = friendAskService.save(senderId, friendAskCreate);

        //when
        List<FriendAskResponse> friendAskRespons = friendAskService.findAllByReceiverId(receiverId);

        //then
        FriendAskResponse sender = friendAskRespons.get(0);
        assertThat(friendAskRespons.size()).isEqualTo(1);
        assertThat(sender.getFriendAskId()).isEqualTo(friendAskResponse.getFriendAskId());
        assertThat(sender.getSenderName()).isEqualTo(SENDER_NAME);
        assertThat(sender.getSenderId()).isEqualTo(senderId);
    }

    @Test
    void 이미_친구_요청을_보낸_경우() {
        //given
        FriendAskCreate friendAskCreate = FriendAskCreate.builder()
                .receiverId(receiverId)
                .build();

        //when
        friendAskService.save(senderId, friendAskCreate);

        //then
        assertThrows(FriendAskFailException.class, () -> friendAskService.save(senderId, friendAskCreate));
    }
}