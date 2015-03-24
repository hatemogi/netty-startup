# 채팅 서버 프로토콜

> 예제와 실습을 위한 간단한 채팅 프로토콜

## 메시지 형식

* 서버와 클라이언트는 UTF-8 텍스트로 ```메시지```를 주고 받는다.
* ```메시지```는 줄바꿈 문자(\n)로 끝난다. (메시지 중간에는 \n가 없다)
* ```메시지```는 ```명령어절```과 (```텍스트```)로 구성되며, ```텍스트```는 있을 수도 있고 없을 수도 있다. 둘의 사이에는 공백문자(\s)가 있다.
* ```명령어절```은 ```명령어```와 ```대화명```으로 구성되며, ```대화명```이 있는 경우에는 ```명령어```뒤에 콜론(:)이 오고 ```대화명```이 따라온다. ```대화명```이 덧붙지 않는 ```명령어```에는 콜론(:)도 붙이지 않는다.

```
message := command-part, "\s", text, "\n" ;
command-part := command
                        | command, ":", nickname ;
command := "HAVE" | "HELO" | "QUIT" | "PING" | "PONG"| "LEFT" | "JOIN" | "SEND" | "FROM" ;
nickname := (일반 UTF8 문자열, 공백/콜론등 제외)
text := ("\n"을 제외한 UTF-8 문자열)
```

## 서버가 보내는 명령어 (8가지)

### PONG

### HAVE:nick

### HELO:nick

### FROM:nick msg

### NICK:prevnick newnick

### JOIN:nick

### LEFT:nick

### BYE:nick

## 클라이언트가 보내는 명령어(4가지)

### PING

### SEND msg

### NICK newnick

### QUIT


## 클라이언트와 서버의 대화 흐름

