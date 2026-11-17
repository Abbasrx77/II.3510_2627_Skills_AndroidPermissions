package fr.isep.permdemo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import fr.isep.permdemo.ui.theme.AndroidGreen
import fr.isep.permdemo.ui.theme.CodeBackground
import fr.isep.permdemo.ui.theme.CodeCardShape
import fr.isep.permdemo.ui.theme.CodeComment
import fr.isep.permdemo.ui.theme.CodeFunction
import fr.isep.permdemo.ui.theme.CodeKeyword
import fr.isep.permdemo.ui.theme.CodeString
import fr.isep.permdemo.ui.theme.CodeText
import fr.isep.permdemo.ui.theme.CodeTextStyle
import fr.isep.permdemo.ui.theme.CodeType

enum class CodeLanguage { KOTLIN, XML, PLAIN }

@Composable
fun CodeCard(
    path: String,
    role: String,
    code: String,
    language: CodeLanguage,
    modifier: Modifier = Modifier,
) {
    val highlighted = remember(code, language) { highlight(code, language) }
    Column(
        modifier
            .fillMaxWidth()
            .clip(CodeCardShape)
            .background(CodeBackground)
            .padding(16.dp),
    ) {
        Text(path, style = CodeTextStyle.copy(fontWeight = FontWeight.Bold), color = AndroidGreen)
        Spacer(Modifier.height(4.dp))
        Text(role, style = MaterialTheme.typography.bodySmall, color = CodeComment)
        Spacer(Modifier.height(12.dp))
        Text(
            text = highlighted,
            style = CodeTextStyle,
            color = CodeText,
            softWrap = false,
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        )
    }
}

private val KOTLIN_KEYWORDS = setOf(
    "val", "var", "fun", "if", "else", "when", "return", "class", "object", "private",
    "override", "import", "package", "is", "in", "null", "true", "false", "this", "as",
    "try", "catch", "for", "while", "data", "enum", "const", "suspend", "by", "it",
)

private val KOTLIN_TOKENS = Regex(
    """(?<comment>//.*)|(?<string>"(?:\\.|[^"\\])*")|(?<annotation>@\w+)|(?<word>[A-Za-z_]\w*)"""
)

private val XML_TOKENS = Regex(
    """(?<comment><!--[\s\S]*?-->)|(?<tag></?[\w:.-]+)|(?<attr>[\w:.-]+(?==))|(?<string>"[^"]*")"""
)

fun highlight(code: String, language: CodeLanguage): AnnotatedString = when (language) {
    CodeLanguage.KOTLIN -> colorize(code, KOTLIN_TOKENS) { match ->
        val groups = match.groups
        when {
            groups["comment"] != null -> CodeComment
            groups["string"] != null -> CodeString
            groups["annotation"] != null -> CodeKeyword
            else -> {
                val word = match.value
                var i = match.range.last + 1
                while (i < code.length && code[i] == ' ') i++
                val next = code.getOrNull(i)
                when {
                    word in KOTLIN_KEYWORDS -> CodeKeyword
                    word.first().isLowerCase() && (next == '(' || next == '{') -> CodeFunction
                    word.first().isUpperCase() -> CodeType
                    else -> null
                }
            }
        }
    }
    CodeLanguage.XML -> colorize(code, XML_TOKENS) { match ->
        val groups = match.groups
        when {
            groups["comment"] != null -> CodeComment
            groups["tag"] != null -> CodeKeyword
            groups["attr"] != null -> CodeFunction
            groups["string"] != null -> CodeString
            else -> null
        }
    }
    CodeLanguage.PLAIN -> AnnotatedString(code)
}

private fun colorize(
    code: String,
    tokens: Regex,
    colorOf: (MatchResult) -> androidx.compose.ui.graphics.Color?,
): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    for (match in tokens.findAll(code)) {
        append(code, cursor, match.range.first)
        val color = colorOf(match)
        if (color != null) withStyle(SpanStyle(color = color)) { append(match.value) } else append(match.value)
        cursor = match.range.last + 1
    }
    append(code, cursor, code.length)
}
