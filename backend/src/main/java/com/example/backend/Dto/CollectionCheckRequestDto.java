package com.example.backend.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//new 마커를 제거하기 위한 스위치로 사용
//"기쁨"의 레벨 3을 확인했다는 것과 같이 고유 id를 알려줘야 함
public class CollectionCheckRequestDto {
    private Long collectionId; //character_collection db의 id
}
