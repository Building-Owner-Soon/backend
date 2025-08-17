package com.bos.backend.application.mapper

import com.bos.backend.domain.user.entity.Character
import com.bos.backend.domain.user.entity.CharacterAsset
import com.bos.backend.presentation.user.dto.UpdateCharacterDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.springframework.stereotype.Component
import java.net.URI

@Mapper(componentModel = "spring", uses = [CharacterAssetHelper::class])
interface CharacterMapper {
    @Mapping(target = "faceShape", source = "faceShape", qualifiedByName = ["createFaceShapeAsset"])
    @Mapping(target = "hand", source = "hand", qualifiedByName = ["createHandAsset"])
    @Mapping(target = "frontHair", source = "frontHair", qualifiedByName = ["createFrontHairAsset"])
    @Mapping(target = "backHair", source = "backHair", qualifiedByName = ["createBackHairAsset"])
    @Mapping(target = "eyes", source = "eyes", qualifiedByName = ["createEyesAsset"])
    @Mapping(target = "mouth", source = "mouth", qualifiedByName = ["createMouthAsset"])
    fun toCharacter(updateCharacterDTO: UpdateCharacterDTO): Character
}

// TODO: Asset api 머지 후에 수정 예정
@Component
class CharacterAssetHelper {
    @org.mapstruct.Named("createFaceShapeAsset")
    fun createFaceShapeAsset(id: Int): CharacterAsset = createAsset(id, "face-shape")

    @org.mapstruct.Named("createHandAsset")
    fun createHandAsset(id: Int): CharacterAsset = createAsset(id, "hand")

    @org.mapstruct.Named("createFrontHairAsset")
    fun createFrontHairAsset(id: Int): CharacterAsset = createAsset(id, "front-hair")

    @org.mapstruct.Named("createBackHairAsset")
    fun createBackHairAsset(id: Int): CharacterAsset = createAsset(id, "back-hair")

    @org.mapstruct.Named("createEyesAsset")
    fun createEyesAsset(id: Int): CharacterAsset = createAsset(id, "eyes")

    @org.mapstruct.Named("createMouthAsset")
    fun createMouthAsset(id: Int): CharacterAsset = createAsset(id, "mouth")

    private fun createAsset(
        id: Int,
        assetType: String,
    ): CharacterAsset {
        val assetId = "${assetType}_$id"
        val uri = URI.create("https://cdn.example.com/profile/$assetType/$id.svg")
        return CharacterAsset(id = assetId, uri = uri)
    }
}
