package org.kotletai.backend.service

import com.google.genai.Client
import com.google.genai.errors.ServerException
import com.google.genai.types.Content
import org.kotletai.backend.model.ShoppingListFromRecipe
import org.springframework.stereotype.Service
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import com.google.genai.types.Tool
import com.google.genai.types.UrlContext
import jakarta.annotation.PostConstruct
import kotlinx.serialization.json.Json
import org.kotletai.backend.exception.NotFoundException
import org.kotletai.backend.model.GeminiShoppingListItem
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import java.math.BigDecimal

@Service
@ConditionalOnProperty(value = ["shopping-list-from-recipe-generator"], havingValue = "gemini", matchIfMissing = false)
class ShoppingListFromRecipeGeneratorGeminiService : ShoppingListFromRecipeGenerator {
    private val prompt =
        """
        You are a recipe ingredient extractor. A recipe URL will be provided
        immediately after this message. Fetch the recipe at that URL and
        extract every ingredient needed.
        
        Return ONLY a valid JSON array. No markdown, no code fences, no commentary.
        
        Output format — a JSON array of objects with exactly two fields:
        - "name": string. The ingredient name followed by the unit in parentheses.
                  Use "(ml)" for liquids, "(g)" for solids by weight, or no unit
                  in parentheses for whole countable items.
        - "quantity": integer. The numeric amount.
        
        Language rule:
        - Detect the language of the recipe and return all ingredient names in
          that same language. If the recipe is in Lithuanian, return Lithuanian
          names ("pienas", "citrina"). If the recipe is in English, return English
          names ("milk", "lemon"). Same for any other language.
        - Use the singular form in that language ("citrina", not "citrinos";
          "lemon", not "lemons").
        - The unit suffixes "(ml)" and "(g)" stay as-is regardless of language —
          do not translate them.
        
        Conversion rules:
        - Convert all volume measurements to milliliters (ml):
          1 cup = 240 ml, 1 tablespoon = 15 ml, 1 teaspoon = 5 ml,
          1 fl oz = 30 ml, 1 pint = 473 ml, 1 quart = 946 ml.
        - Convert all weight measurements to grams (g):
          1 oz = 28 g, 1 lb = 454 g, 1 kg = 1000 g.
        - For countable items (eggs, lemons, onions, garlic cloves), use the
          whole number with no unit in the name.
        - For vague amounts ("a pinch", "to taste", "a splash"), use a sensible
          default: pinch = 1 g, splash = 5 ml, "to taste" = 1 g.
        - Always round to the nearest integer. Never return decimals.
        - If a range is given (e.g. "2-3 cloves"), use the higher number.
        - Combine duplicate ingredients into a single entry with summed quantity.
        
        Examples (English recipe):
        - "500 ml of milk" → {"name": "milk (ml)", "quantity": 500}
        - "2 lemons" → {"name": "lemon", "quantity": 2}
        - "1 cup of flour" → {"name": "flour (g)", "quantity": 120}
        - "1 tablespoon olive oil" → {"name": "olive oil (ml)", "quantity": 15}
        - "3 cloves garlic" → {"name": "garlic clove", "quantity": 3}
        - "a pinch of salt" → {"name": "salt (g)", "quantity": 1}
        
        Examples (Lithuanian recipe):
        - "500 ml pieno" → {"name": "pienas (ml)", "quantity": 500}
        - "2 citrinos" → {"name": "citrina", "quantity": 2}
        - "1 puodelis miltų" → {"name": "miltai (g)", "quantity": 120}
        - "žiupsnelis druskos" → {"name": "druska (g)", "quantity": 1}
        
        If the URL cannot be accessed or contains no recipe, return [], otherwise return the array of ingredients.
        
        RETURN ONLY THE JSON ARRAY, NOTHING ELSE, DON'T RETURN YOUR THOUGHT PROCESS
        DON'T INCLUDE CODE BLOCKS IN THE RESPONSE, ONLY THE RAW DATA
        
        The recipe URL is:
        """.trimIndent()

    private val model = "gemini-2.5-flash"
    private val config = GenerateContentConfig.builder()
        .tools(listOf(
            Tool.builder().urlContext(UrlContext.builder().build()).build()
        ))
        .build()

    private val client = Client()

    override fun generateShoppingListFromRecipe(recipeUrl: String): ShoppingListFromRecipe {
        try {
            val response = client.models.generateContent(
                model,
                Content.builder()
                    .parts(listOf(
                        Part.fromText(prompt),
                        Part.fromText(recipeUrl),
                    ))
                    .build(),
                config
            )

            println(response.text())
            val items: List<GeminiShoppingListItem> = response.text()?.let { Json.decodeFromString(it) } ?: emptyList()
            return ShoppingListFromRecipe(items)
        } catch (e: ServerException) {
            e.message?.contains("high demand")?.let { throw NotFoundException("Model unavailable due to high demand") }
            return ShoppingListFromRecipe(listOf())
        }
    }
}
