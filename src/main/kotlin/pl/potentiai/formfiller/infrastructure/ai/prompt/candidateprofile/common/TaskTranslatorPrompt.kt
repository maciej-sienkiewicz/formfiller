package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class TasksTranslatorPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): Extractor<Tasks>(openAI, Tasks::class, timeableWrapper, "taskTranslations") {

    override val PROMPT = """
Przetłumacz przesłaną listę na dwa języki: na angielski i niemiecki. Odpowiedź w formacie json.

{
    "translated": [
        {
            "original": String,
            "experienceTaskDe": String,
            "experienceTaskEng": String
        }
    ]
}


Zasady:
1. Nie dodawaj żadnych dodatkowych informacji, opisów ani znaków poza oczekiwanym JSON.
    """.trimIndent()
}

data class Tasks(val translated: List<Translated>)

data class Translated(val original: String, val experienceTaskDe: String, val experienceTaskEng: String)
