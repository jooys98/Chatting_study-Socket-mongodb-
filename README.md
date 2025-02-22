![스크린샷 2025-02-22 오후 2 12 14](https://github.com/user-attachments/assets/70e9c713-5cb9-4dd7-a3dc-77ad0ad1eb45)


# Chatting Process

0. 대화 상대를 선택하면 채팅룸이 만들어지는 api 호출 (createChatRoom) +  구독 주소를 삭별하는 roomId 생성됨 
1. stomp 헤더의 로그인인 유저의 토큰을 담아 서버에 전송
2. 서버는 stomp 헤더에 담긴 토큰으로 유저 인증 처리 후 web socket 연결 허용
3. 유저와 대화 상대는  해당 채팅룸을 구독하는 상태가 됨
4. 유저가 메세지를 보내면 stomp 프로토콜의 body 에 메세지가 담겨서 서버( /topic/chat/message/{roomId}" 주소 )에 전달됨 ( STOMP <Message>)
5. 서버에 온 메세지는  roomId 에 맞는 chatRoom 이 있는지 조회 후 도큐먼트로 변환 하여 mongo db 에 roomId와 함께 저장
6.  roomId에 해당하는 chatMessage 들을 List 화 하여 클라이언트에 전달( http <GET> )

