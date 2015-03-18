# Netty 프로그래밍 기초

* 과정명 : Netty 프로그래밍 기초
* 학습시간 : 4시간 (2시~6시)
* 정원 : 20명

## 개요
### 학습목표

JAVA로 고성능 네트워크 클라이언트/서버를 편리하게 개발할 수 있는 프레임워크인 Netty(http://netty.io)의 기본 개념을 이해하고 예제를 통해 연습합니다.

### 학습목차

1. Netty 소개와 특징
2. Netty 전체 구조와 기본 코덱
3. Netty와 함께하는 멀티쓰레딩
4. 따라해보는 Netty 서버 개발

### 학습내용

Netty는 JAVA환경에서 고성능 네트워크 서버를 개발할 수 있는 매우 유용한 프레임워크입니다만, 비동기(asynchronous) 이벤트 드리븐 (event-driven) 처리에 익숙하지 않은 개발자에게는 처음 접근하기 어려운 점이 있습니다. 이 과정에서는 기존 멀티쓰레드와 동기식(synchronous) I/O처리에만 익숙했던 개발자도 차근차근 따라해보며 비동기 I/O처리의 기본 개념을 이해하고, Netty를 본격적으로 활용해 볼 수 있도록 예제 개발을 함께 해보려 합니다.

### 학습자 준비사항

간단한 예제 개발을 함께 진행하므로, 각자의 랩탑에 이하의 소프트웨어를 미리 설치해 오시면 좋습니다.

* IntelliJ CE
* JDK8


## 설명할 것들 모두 나열

* 메모리 모델 (Reference Counted Buffers)
* 쓰레드 모델 (I/O처리는 한 쓰레드가 담당함)
* 주요 클래스 (ChannelPipeline, ChannelHandler, ChannelHandlerContext, EventLoop, EventLoopGroup)
* 네티에서 유닛 테스트
* ChannelHandler.channelReadComplete는 언제 불리는가? 그리고 각각 메소드가 불리는 시점
* [ByteBuf](http://netty.io/4.0/api/index.html?io/netty/buffer/ByteBuf.html) 마지막에 쓴 메소드가 해제하기
* msgpack codec을 만들어볼까?
* ChannelFuture, ChannelPromise 활용하기
* ChannelHandler.Shareable
* 타임아웃 처리 넣기
* AttributeMap
* TLS 사용

## 저작권

MIT licensed

Copyright (C) 2015 Daehyun Kim, http://hatemogi.com
