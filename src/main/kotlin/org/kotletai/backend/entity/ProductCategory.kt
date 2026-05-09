package org.kotletai.backend.entity

enum class ProductCategory(
    val maximaName: String,
) {
    DAIRY_AND_EGGS("Pieno gaminiai ir kiaušiniai"),
    MEAT("Mėsa ir mėsos gaminiai"),
    FISH("Žuvis ir žuvies produktai"),
    FRUITS_AND_VEGETABLES("Vaisiai ir daržovės"),
    GROCERIES("Bakalėja"),
    SWEETS("Saldumynai"),
    CONFECTIONERY("Konditerija"),
    BEVERAGES("Gėrimai"),
    COFFEE_TEA("Kava, kakava, arbata"),
    FROZEN("Šaldytas maistas"),
    CANNED("Konservuotas maistas"),
    PREPARED_FOODS("Kulinarija"),
    BABY("Kūdikių ir vaikų prekės"),
    COSMETICS("Kosmetika ir higiena"),
    HOUSEHOLD_CHEMICALS("Buitinė chemija"),
    INDUSTRIAL("Pramonės prekės"),
    PLANTS("Augalai ir jų priežiūros prekės"),
    OTHER(""),
    ;

    companion object {
        fun fromMaxima(name: String): ProductCategory = entries.firstOrNull { it.maximaName == name } ?: OTHER
    }
}
