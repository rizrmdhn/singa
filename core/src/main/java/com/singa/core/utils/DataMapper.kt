package com.singa.core.utils

import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.singa.core.data.source.remote.response.ArticlesItem
import com.singa.core.data.source.remote.response.GetConversationListItem
import com.singa.core.data.source.remote.response.GetConversationNode
import com.singa.core.data.source.remote.response.GetMeResponse
import com.singa.core.data.source.remote.response.GetStaticTranslationDetailResponse
import com.singa.core.data.source.remote.response.GetStaticTranslationList
import com.singa.core.data.source.remote.response.LoginResponse
import com.singa.core.data.source.remote.response.SchemaError
import com.singa.core.data.source.remote.response.UpdateTokenResponse
import com.singa.core.data.source.remote.response.UpdateUserResponse
import com.singa.core.domain.model.Articles
import com.singa.core.domain.model.Conversation
import com.singa.core.domain.model.ConversationNode
import com.singa.core.domain.model.FaceLandmarker
import com.singa.core.domain.model.HandLandmarker
import com.singa.core.domain.model.Landmark
import com.singa.core.domain.model.NormalizedLandmark
import com.singa.core.domain.model.PoseLandmarker
import com.singa.core.domain.model.RefreshToken
import com.singa.core.domain.model.StaticTranscriptsItem
import com.singa.core.domain.model.StaticTranslation
import com.singa.core.domain.model.StaticTranslationDetail
import com.singa.core.domain.model.Token
import com.singa.core.domain.model.User
import com.singa.core.domain.model.ValidationErrorSchema


object DataMapper {
    fun mapResponseValidationErrorToModel(
        errors: List<SchemaError>
    ) = errors.map {
        ValidationErrorSchema(
            field = it.field,
            message = it.message,
            rule = it.rule
        )
    }

    fun mapLoginResponseToModel(
        data: LoginResponse
    ) = Token(
        type = data.type,
        accessToken = data.token,
        refreshToken = data.refreshToken
    )

    fun mapUserResponseToModel(
        data: GetMeResponse
    ) = User(
        id = data.id,
        name = data.name,
        email = data.email,
        avatar = data.avatarUrl,
        isSignUser = data.isSignUser,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt
    )

    fun mapRefreshTokenResponseToModel(
        data: UpdateTokenResponse
    ) = RefreshToken(
        type = data.type,
        token = data.token,
    )

    fun mapUpdateUserResponseToModel(
        data: UpdateUserResponse
    ) = User(
        id = data.id,
        name = data.name,
        email = data.email,
        avatar = data.avatarUrl,
        isSignUser = data.isSignUser,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt
    )

    fun mapConversationResponseToModel(data: List<GetConversationListItem>) = data.map {
        Conversation(
            id = it.id,
            title = it.title,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt
        )
    }

    fun mapConversationCreateResponseToModel(data: GetConversationListItem) = Conversation(
        id = data.id,
        title = data.title,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt
    )

    fun mapConversationNodeResponseToModel(data: List<GetConversationNode>) = data.map {
        ConversationNode(
            id = it.id,
            conversationTranslationId = it.conversationTranslationId,
            type = it.type,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            video = it.video,
            userId = it.userId,
            transcripts = it.transcripts,
            videoUrl = it.videoUrl
        )
    }

    fun mapStaticTranslationResponseToModel(data: List<GetStaticTranslationList>) = data.map {
        StaticTranslation(
            id = it.id,
            title = it.title,
            videoUrl = it.videoUrl,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt
        )
    }

    fun mapFaceLandmarkResponseToModel(data: FaceLandmarkerResult) = FaceLandmarker(
        timestampMs = data.timestampMs(),
        faceLandmarks = data.faceLandmarks().map { landmarks ->
            landmarks.map { normalizedLandmark ->
                NormalizedLandmark(
                    x = normalizedLandmark.x(),
                    y = normalizedLandmark.y(),
                    z = normalizedLandmark.z(),
                    visibility = normalizedLandmark.visibility(),
                    presence = normalizedLandmark.presence()
                )
            }
        }
    )

    fun mapPoseLandmarkResponseToModel(data: List<PoseLandmarkerResult>) = data.map {
        PoseLandmarker(
            timestampMs = it.timestampMs(),
            landmarks = it.landmarks().map { landmarks ->
                landmarks.map { normalizedLandmark ->
                    NormalizedLandmark(
                        x = normalizedLandmark.x(),
                        y = normalizedLandmark.y(),
                        z = normalizedLandmark.z(),
                        visibility = normalizedLandmark.visibility(),
                        presence = normalizedLandmark.presence()
                    )
                }
            }
        )
    }

    fun mapHandLandmarkResponseToModel(data: List<HandLandmarkerResult>) = data.map {
        HandLandmarker(
            timestampMs = it.timestampMs(),
            landmarks = it.landmarks().map { landmarks ->
                landmarks.map { normalizedLandmark ->
                    NormalizedLandmark(
                        x = normalizedLandmark.x(),
                        y = normalizedLandmark.y(),
                        z = normalizedLandmark.z(),
                        visibility = normalizedLandmark.visibility(),
                        presence = normalizedLandmark.presence()
                    )
                }
            },
            worldLandmarks = it.worldLandmarks().map { landmarks ->
                landmarks.map { landmark ->
                    Landmark(
                        x = landmark.x(),
                        y = landmark.y(),
                        z = landmark.z(),
                        visibility = landmark.visibility(),
                        presence = landmark.presence()
                    )
                }
            }
        )
    }

    fun mapStaticTranslationDetailResponseToModel(data: GetStaticTranslationDetailResponse) =
        StaticTranslationDetail(
            id = data.id,
            title = data.title,
            videoUrl = data.videoUrl,
            createdAt = data.createdAt,
            transcripts = data.transcripts.map {
                StaticTranscriptsItem(
                    id = it.id,
                    userId = it.userId,
                    staticTranslationId = it.staticTranslationId,
                    text = it.text,
                    timestamp = it.timestamp,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            },
            updatedAt = data.updatedAt
        )

    fun mapArticlesResponseToModel(data: List<ArticlesItem>) = data.map {
        Articles(
            createdAt = it.createdAt,
            imageUrl = it.imageUrl,
            description = it.description,
            id = it.id,
            title = it.title,
            updatedAt = it.updatedAt
        )
    }
}