package com.example.backend.Controller;

import com.example.backend.Config.auth.PrincipalDetails;
import com.example.backend.Dto.CharacterCollectionDto;
import com.example.backend.Dto.CharacterDetailDto;
import com.example.backend.Dto.CollectionCheckRequestDto;
import com.example.backend.Service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    //페이지 로딩 하면 18 종류의 캐릭터 정보 로드
    //emotion, level, imageUrl, isAcquired
    @GetMapping
    public ResponseEntity<List<CharacterCollectionDto>> getCollection(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<CharacterCollectionDto> collection = collectionService.getCollectionForUser(principalDetails.getUser());
        return ResponseEntity.ok(collection);
    }

    //캐릭터 상세 페이지 정보 조회
    @GetMapping("/{emotion}")
    public ResponseEntity<CharacterDetailDto> getCharacterDetail(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable String emotion) {

        CharacterDetailDto characterDetailDto = collectionService.getCharacterDetailForUser(principalDetails.getUser(), emotion);
        return ResponseEntity.ok(characterDetailDto);
    }

    //new 마커를 해제 하는 api
    @PostMapping("/checked")
    public ResponseEntity<Void> checkCharacter(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody CollectionCheckRequestDto requestDto) {
        collectionService.checkCharacter(principalDetails.getUser(), requestDto.getCollectionId());
        return ResponseEntity.noContent().build();
    }



}
