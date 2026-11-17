package fr.isep.permdemo.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import fr.isep.permdemo.permissions.PermissionType

val Background = Color(0xFFF1F3F8)
val SurfaceWhite = Color(0xFFFBFCFE)
val Ink = Color(0xFF0F1A2B)
val TextSecondary = Color(0xFF4B5567)
val Border = Color(0xFFD3D9E3)
val AndroidGreen = Color(0xFF3DDC84)

val GrantedContainer = Color(0xFFC8F2D8)

val CodeBackground = Color(0xFF17233A)
val CodeText = Color(0xFFE2E8F2)
val CodeKeyword = Color(0xFFFF9E64)
val CodeString = Color(0xFFC3E88D)
val CodeFunction = Color(0xFF82AAFF)
val CodeType = Color(0xFF3DDC84)
val CodeComment = Color(0xFF8A96AB)

@Immutable
data class TypeColors(val container: Color, val strong: Color)

val PermissionType.colors: TypeColors
    get() = when (this) {
        PermissionType.NORMAL -> TypeColors(Color(0xFFD6E3FF), Color(0xFF1A4FBF))
        PermissionType.SIGNATURE -> TypeColors(Color(0xFFE6DEFF), Color(0xFF5B3CC4))
        PermissionType.DANGEROUS -> TypeColors(Color(0xFFFFDAD2), Color(0xFFA8330A))
        PermissionType.SPECIAL -> TypeColors(Color(0xFFFFE9A8), Color(0xFF7A5400))
        PermissionType.NONE -> TypeColors(Color(0xFFC8F2D8), Color(0xFF1A6B42))
    }
