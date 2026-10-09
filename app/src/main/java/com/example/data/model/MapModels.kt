package com.example.data.model

import androidx.compose.ui.geometry.Offset

enum class Continent(val displayName: String) {
    EUROPE("Europa"),
    ASIA("Asia"),
    NORTH_AMERICA("Nord America"),
    SOUTH_AMERICA("Sud America"),
    AFRICA("Africa"),
    OCEANIA("Oceania")
}

data class Point(val x: Float, val y: Float)

data class Region(
    val id: String,
    val name: String,
    val defaultColorHex: String,
    // Polygons to draw. Each inner list is a list of normalized coordinates (0.0 to 1.0)
    val points: List<Point>
)

data class Country(
    val id: String, // e.g., "it", "us", "fr"
    val name: String,
    val continent: Continent,
    val regions: List<Region>,
    // Position of this country on the world map canvas (normalized coordinates 0.0 to 1.0)
    val worldMapPoints: List<Point> = emptyList(),
    val flag: String = "🏳️"
)

object MapData {
    // A robust database of countries and their regions
    val countries: List<Country> = listOf(
        // --- EUROPE (45 COUNTRIES) ---
        Country(
            id = "al",
            name = "Albania",
            continent = Continent.EUROPE,
            flag = "🇦🇱",
            worldMapPoints = listOf(Point(0.53f, 0.38f), Point(0.54f, 0.38f), Point(0.54f, 0.39f), Point(0.53f, 0.39f)),
            regions = listOf(
                Region("tirana", "Tirana", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("durazzo", "Durazzo", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("valona", "Valona", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("scutari", "Scutari", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "ad",
            name = "Andorra",
            continent = Continent.EUROPE,
            flag = "🇦🇩",
            worldMapPoints = listOf(Point(0.42f, 0.37f), Point(0.43f, 0.37f), Point(0.43f, 0.38f), Point(0.42f, 0.38f)),
            regions = listOf(
                Region("andorra_vella", "Andorra la Vella", "#FFF176", listOf(Point(0.1f, 0.1f))),
                Region("canillo", "Canillo", "#FFB74D", listOf(Point(0.2f, 0.2f))),
                Region("encamp", "Encamp", "#81C784", listOf(Point(0.3f, 0.3f))),
                Region("massana", "La Massana", "#4FC3F7", listOf(Point(0.4f, 0.4f))),
                Region("ordino", "Ordino", "#BA68C8", listOf(Point(0.5f, 0.5f))),
                Region("sant_julia", "Sant Julià de Lòria", "#FF8A65", listOf(Point(0.6f, 0.6f)))
            )
        ),
        Country(
            id = "at",
            name = "Austria",
            continent = Continent.EUROPE,
            flag = "🇦🇹",
            worldMapPoints = listOf(Point(0.48f, 0.32f), Point(0.51f, 0.32f), Point(0.51f, 0.34f), Point(0.48f, 0.34f)),
            regions = listOf(
                Region("vienna", "Vienna", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("salisburgo", "Salisburgo", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("tirolo", "Tirolo", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("carinzia", "Carinzia", "#4FC3F7", listOf(Point(0.5f, 0.5f))),
                Region("stiria", "Stiria", "#BA68C8", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "be",
            name = "Belgio",
            continent = Continent.EUROPE,
            flag = "🇧🇪",
            worldMapPoints = listOf(Point(0.43f, 0.29f), Point(0.45f, 0.29f), Point(0.45f, 0.31f), Point(0.43f, 0.31f)),
            regions = listOf(
                Region("bruxelles", "Bruxelles", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("fiandre", "Fiandre", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("vallonia", "Vallonia", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "by",
            name = "Bielorussia",
            continent = Continent.EUROPE,
            flag = "🇧🇾",
            worldMapPoints = listOf(Point(0.54f, 0.24f), Point(0.58f, 0.24f), Point(0.58f, 0.27f), Point(0.54f, 0.27f)),
            regions = listOf(
                Region("minsk", "Minsk", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("brest", "Brest", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("grodno", "Grodno", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("gomel", "Gomel", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "ba",
            name = "Bosnia ed Erzegovina",
            continent = Continent.EUROPE,
            flag = "🇧🇦",
            worldMapPoints = listOf(Point(0.51f, 0.36f), Point(0.53f, 0.36f), Point(0.53f, 0.38f), Point(0.51f, 0.38f)),
            regions = listOf(
                Region("sarajevo", "Sarajevo", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("mostar", "Mostar", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("banja_luka", "Banja Luka", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "bg",
            name = "Bulgaria",
            continent = Continent.EUROPE,
            flag = "🇧🇬",
            worldMapPoints = listOf(Point(0.54f, 0.35f), Point(0.57f, 0.35f), Point(0.57f, 0.37f), Point(0.54f, 0.37f)),
            regions = listOf(
                Region("sofia", "Sofia", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("varna", "Varna", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("plovdiv", "Plovdiv", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("burgas", "Burgas", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "va",
            name = "Città del Vaticano",
            continent = Continent.EUROPE,
            flag = "🇻🇦",
            worldMapPoints = listOf(Point(0.491f, 0.371f), Point(0.493f, 0.371f), Point(0.493f, 0.373f), Point(0.491f, 0.373f)),
            regions = listOf(
                Region("piazza_san_pietro", "Piazza San Pietro", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("musei_vaticani", "Musei Vaticani", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("giardini_vaticani", "Giardini Vaticani", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "hr",
            name = "Croazia",
            continent = Continent.EUROPE,
            flag = "🇭🇷",
            worldMapPoints = listOf(Point(0.49f, 0.34f), Point(0.51f, 0.34f), Point(0.51f, 0.36f), Point(0.49f, 0.36f)),
            regions = listOf(
                Region("zagabria", "Zagabria", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("istria", "Istria", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("dalmazia", "Dalmazia", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("dubrovnik", "Dubrovnik", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "dk",
            name = "Danimarca",
            continent = Continent.EUROPE,
            flag = "🇩🇰",
            worldMapPoints = listOf(Point(0.46f, 0.24f), Point(0.48f, 0.24f), Point(0.48f, 0.26f), Point(0.46f, 0.26f)),
            regions = listOf(
                Region("copenaghen", "Copenaghen", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("jutland", "Jutland", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("selandia", "Selandia", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("fionia", "Fionia", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "ee",
            name = "Estonia",
            continent = Continent.EUROPE,
            flag = "🇪🇪",
            worldMapPoints = listOf(Point(0.52f, 0.20f), Point(0.54f, 0.20f), Point(0.54f, 0.22f), Point(0.52f, 0.22f)),
            regions = listOf(
                Region("tallinn", "Tallinn", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("tartu", "Tartu", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("parnu", "Pärnu", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "fi",
            name = "Finlandia",
            continent = Continent.EUROPE,
            flag = "🇫🇮",
            worldMapPoints = listOf(Point(0.51f, 0.14f), Point(0.56f, 0.14f), Point(0.56f, 0.19f), Point(0.51f, 0.19f)),
            regions = listOf(
                Region("helsinki", "Helsinki", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("lapponia_fi", "Lapponia Finlandese", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("tampere", "Tampere", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("turku", "Turku", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "fr",
            name = "Francia",
            continent = Continent.EUROPE,
            flag = "🇫🇷",
            worldMapPoints = listOf(Point(0.44f, 0.32f), Point(0.47f, 0.32f), Point(0.47f, 0.36f), Point(0.43f, 0.36f)),
            regions = listOf(
                Region("hauts_de_france", "Hauts-de-France", "#81C784", listOf(Point(0.45f, 0.05f), Point(0.58f, 0.07f), Point(0.54f, 0.2f), Point(0.42f, 0.17f))),
                Region("normandie", "Normandie", "#AED581", listOf(Point(0.22f, 0.15f), Point(0.41f, 0.16f), Point(0.38f, 0.28f), Point(0.19f, 0.25f))),
                Region("ile_de_france", "Île-de-France", "#E57373", listOf(Point(0.42f, 0.21f), Point(0.53f, 0.21f), Point(0.51f, 0.31f), Point(0.39f, 0.31f))),
                Region("grand_est", "Grand Est", "#FFF176", listOf(Point(0.55f, 0.14f), Point(0.78f, 0.15f), Point(0.72f, 0.34f), Point(0.52f, 0.32f))),
                Region("bretagne", "Bretagne", "#4DB6AC", listOf(Point(0.05f, 0.24f), Point(0.21f, 0.23f), Point(0.18f, 0.38f), Point(0.03f, 0.35f))),
                Region("pays_de_la_loire", "Pays de la Loire", "#4DD0E1", listOf(Point(0.17f, 0.33f), Point(0.34f, 0.32f), Point(0.31f, 0.48f), Point(0.14f, 0.46f))),
                Region("centre_val_de_loire", "Centre-Val de Loire", "#FFB74D", listOf(Point(0.35f, 0.32f), Point(0.51f, 0.32f), Point(0.48f, 0.49f), Point(0.32f, 0.47f))),
                Region("bourgogne", "Bourgogne-Franche-Comté", "#BA68C8", listOf(Point(0.52f, 0.33f), Point(0.74f, 0.35f), Point(0.69f, 0.52f), Point(0.49f, 0.49f))),
                Region("nouvelle_aquitaine", "Nouvelle-Aquitaine", "#9575CD", listOf(Point(0.18f, 0.49f), Point(0.42f, 0.5f), Point(0.38f, 0.78f), Point(0.15f, 0.72f))),
                Region("auvergne", "Auvergne-Rhône-Alpes", "#7986CB", listOf(Point(0.43f, 0.5f), Point(0.68f, 0.52f), Point(0.63f, 0.74f), Point(0.39f, 0.71f))),
                Region("occitanie", "Occitanie", "#64B5F6", listOf(Point(0.36f, 0.73f), Point(0.54f, 0.74f), Point(0.51f, 0.94f), Point(0.32f, 0.91f))),
                Region("paca", "Provence-Alpes-Côte d'Azur", "#FF8A65", listOf(Point(0.56f, 0.73f), Point(0.72f, 0.75f), Point(0.68f, 0.91f), Point(0.53f, 0.88f))),
                Region("corse", "Corse", "#F06292", listOf(Point(0.75f, 0.85f), Point(0.83f, 0.85f), Point(0.81f, 0.98f), Point(0.73f, 0.97f)))
            )
        ),
        Country(
            id = "de",
            name = "Germania",
            continent = Continent.EUROPE,
            flag = "🇩🇪",
            worldMapPoints = listOf(Point(0.46f, 0.28f), Point(0.49f, 0.28f), Point(0.49f, 0.32f), Point(0.46f, 0.32f)),
            regions = listOf(
                Region("baviera", "Baviera", "#FFF176", listOf(Point(0.1f, 0.1f))),
                Region("berlino", "Berlino e Brandeburgo", "#FFB74D", listOf(Point(0.2f, 0.2f))),
                Region("amburgo", "Amburgo e Nord", "#81C784", listOf(Point(0.3f, 0.3f))),
                Region("renania", "Renania Settentrionale-Vestfalia", "#4FC3F7", listOf(Point(0.4f, 0.4f))),
                Region("baden", "Baden-Württemberg", "#BA68C8", listOf(Point(0.5f, 0.5f))),
                Region("sassonia", "Sassonia", "#FF8A65", listOf(Point(0.6f, 0.6f)))
            )
        ),
        Country(
            id = "gr",
            name = "Grecia",
            continent = Continent.EUROPE,
            flag = "🇬🇷",
            worldMapPoints = listOf(Point(0.51f, 0.38f), Point(0.54f, 0.38f), Point(0.54f, 0.41f), Point(0.51f, 0.41f)),
            regions = listOf(
                Region("atene", "Atene e Attica", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("salonicco", "Salonicco", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("creta", "Creta", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("cicladi", "Santorini & Cicladi", "#4FC3F7", listOf(Point(0.5f, 0.5f))),
                Region("ionie", "Corfù & Isole Ionie", "#BA68C8", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "ie",
            name = "Irlanda",
            continent = Continent.EUROPE,
            flag = "🇮🇪",
            worldMapPoints = listOf(Point(0.39f, 0.26f), Point(0.41f, 0.26f), Point(0.41f, 0.29f), Point(0.39f, 0.29f)),
            regions = listOf(
                Region("dublino", "Dublino", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("cork", "Cork", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("galway", "Galway", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("kerry", "Kerry", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "is",
            name = "Islanda",
            continent = Continent.EUROPE,
            flag = "🇮🇸",
            worldMapPoints = listOf(Point(0.38f, 0.16f), Point(0.42f, 0.16f), Point(0.42f, 0.19f), Point(0.38f, 0.19f)),
            regions = listOf(
                Region("reykjavik", "Reykjavík", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("golden_circle", "Golden Circle", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("fiordi_occ", "Fiordi Occidentali", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("snaefellsnes", "Snaefellsnes", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "it",
            name = "Italia",
            continent = Continent.EUROPE,
            flag = "🇮🇹",
            worldMapPoints = listOf(Point(0.48f, 0.35f), Point(0.50f, 0.35f), Point(0.51f, 0.40f), Point(0.49f, 0.42f)),
            regions = listOf(
                Region("valle_daosta", "Valle d'Aosta", "#9CCC65", listOf(Point(0.1f, 0.1f), Point(0.2f, 0.08f), Point(0.22f, 0.15f), Point(0.12f, 0.16f))),
                Region("piemonte", "Piemonte", "#81C784", listOf(Point(0.08f, 0.17f), Point(0.23f, 0.16f), Point(0.25f, 0.32f), Point(0.1f, 0.35f))),
                Region("liguria", "Liguria", "#4DB6AC", listOf(Point(0.12f, 0.36f), Point(0.26f, 0.33f), Point(0.3f, 0.38f), Point(0.15f, 0.4f))),
                Region("lombardia", "Lombardia", "#AED581", listOf(Point(0.24f, 0.08f), Point(0.42f, 0.08f), Point(0.4f, 0.26f), Point(0.24f, 0.25f))),
                Region("trentino", "Trentino-Alto Adige", "#D4E157", listOf(Point(0.43f, 0.05f), Point(0.55f, 0.05f), Point(0.53f, 0.18f), Point(0.41f, 0.16f))),
                Region("veneto", "Veneto", "#FFF176", listOf(Point(0.44f, 0.19f), Point(0.56f, 0.14f), Point(0.58f, 0.28f), Point(0.45f, 0.29f))),
                Region("friuli", "Friuli-Venezia Giulia", "#FFD54F", listOf(Point(0.57f, 0.13f), Point(0.68f, 0.11f), Point(0.66f, 0.22f), Point(0.57f, 0.24f))),
                Region("emilia", "Emilia-Romagna", "#FFB74D", listOf(Point(0.26f, 0.27f), Point(0.52f, 0.3f), Point(0.58f, 0.38f), Point(0.28f, 0.36f))),
                Region("toscana", "Toscana", "#FF8A65", listOf(Point(0.32f, 0.38f), Point(0.48f, 0.38f), Point(0.46f, 0.54f), Point(0.3f, 0.5f))),
                Region("marche", "Marche", "#F06292", listOf(Point(0.49f, 0.39f), Point(0.62f, 0.44f), Point(0.58f, 0.54f), Point(0.47f, 0.49f))),
                Region("umbria", "Umbria", "#BA68C8", listOf(Point(0.45f, 0.5f), Point(0.52f, 0.47f), Point(0.54f, 0.56f), Point(0.46f, 0.56f))),
                Region("lazio", "Lazio", "#9575CD", listOf(Point(0.42f, 0.55f), Point(0.53f, 0.55f), Point(0.56f, 0.68f), Point(0.44f, 0.65f))),
                Region("abruzzo", "Abruzzo", "#7986CB", listOf(Point(0.54f, 0.53f), Point(0.64f, 0.56f), Point(0.61f, 0.64f), Point(0.53f, 0.62f))),
                Region("molise", "Molise", "#64B5F6", listOf(Point(0.61f, 0.61f), Point(0.68f, 0.62f), Point(0.66f, 0.68f), Point(0.59f, 0.67f))),
                Region("campania", "Campania", "#4FC3F7", listOf(Point(0.54f, 0.66f), Point(0.66f, 0.67f), Point(0.7f, 0.76f), Point(0.56f, 0.74f))),
                Region("puglia", "Puglia", "#4DD0E1", listOf(Point(0.67f, 0.64f), Point(0.85f, 0.7f), Point(0.78f, 0.81f), Point(0.69f, 0.72f))),
                Region("basilicata", "Basilicata", "#4DB6AC", listOf(Point(0.67f, 0.73f), Point(0.76f, 0.76f), Point(0.72f, 0.83f), Point(0.65f, 0.8f))),
                Region("calabria", "Calabria", "#81C784", listOf(Point(0.69f, 0.81f), Point(0.75f, 0.84f), Point(0.73f, 0.96f), Point(0.66f, 0.94f))),
                Region("sicilia", "Sicilia", "#FFF176", listOf(Point(0.46f, 0.83f), Point(0.64f, 0.82f), Point(0.58f, 0.96f), Point(0.44f, 0.91f))),
                Region("sardegna", "Sardegna", "#FFB74D", listOf(Point(0.14f, 0.52f), Point(0.24f, 0.52f), Point(0.22f, 0.76f), Point(0.12f, 0.74f)))
            )
        ),
        Country(
            id = "xk",
            name = "Kosovo",
            continent = Continent.EUROPE,
            flag = "🇽🇰",
            worldMapPoints = listOf(Point(0.52f, 0.36f), Point(0.53f, 0.36f), Point(0.53f, 0.37f), Point(0.52f, 0.37f)),
            regions = listOf(
                Region("pristina", "Pristina", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("prizren", "Prizren", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("peja", "Peja", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "lv",
            name = "Lettonia",
            continent = Continent.EUROPE,
            flag = "🇱🇻",
            worldMapPoints = listOf(Point(0.52f, 0.22f), Point(0.54f, 0.22f), Point(0.54f, 0.24f), Point(0.52f, 0.24f)),
            regions = listOf(
                Region("riga", "Riga", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("jurmala", "Jurmala", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("curlandia", "Curlandia", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "li",
            name = "Liechtenstein",
            continent = Continent.EUROPE,
            flag = "🇱🇮",
            worldMapPoints = listOf(Point(0.47f, 0.34f), Point(0.475f, 0.34f), Point(0.475f, 0.345f), Point(0.47f, 0.345f)),
            regions = listOf(
                Region("vaduz", "Vaduz", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("schaan", "Schaan", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("triesen", "Triesen", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "lt",
            name = "Lituania",
            continent = Continent.EUROPE,
            flag = "🇱🇹",
            worldMapPoints = listOf(Point(0.52f, 0.24f), Point(0.54f, 0.24f), Point(0.54f, 0.26f), Point(0.52f, 0.26f)),
            regions = listOf(
                Region("vilnius", "Vilnius", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("kaunas", "Kaunas", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("klaipeda", "Klaipėda", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "lu",
            name = "Lussemburgo",
            continent = Continent.EUROPE,
            flag = "🇱🇺",
            worldMapPoints = listOf(Point(0.44f, 0.31f), Point(0.45f, 0.31f), Point(0.45f, 0.32f), Point(0.44f, 0.32f)),
            regions = listOf(
                Region("lussemburgo_citta", "Lussemburgo Città", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("esch", "Esch-sur-Alzette", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("echternach", "Echternach", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "mk",
            name = "Macedonia del Nord",
            continent = Continent.EUROPE,
            flag = "🇲🇰",
            worldMapPoints = listOf(Point(0.53f, 0.37f), Point(0.54f, 0.37f), Point(0.54f, 0.38f), Point(0.53f, 0.38f)),
            regions = listOf(
                Region("skopje", "Skopje", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("ocrida", "Ocrida", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("bitola", "Bitola", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "mt",
            name = "Malta",
            continent = Continent.EUROPE,
            flag = "🇲🇹",
            worldMapPoints = listOf(Point(0.49f, 0.43f), Point(0.50f, 0.43f), Point(0.50f, 0.44f), Point(0.49f, 0.44f)),
            regions = listOf(
                Region("valletta", "La Valletta", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("sliema", "Sliema & San Giuliano", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("gozo", "Gozo", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("comino", "Comino", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "md",
            name = "Moldavia",
            continent = Continent.EUROPE,
            flag = "🇲🇩",
            worldMapPoints = listOf(Point(0.55f, 0.31f), Point(0.57f, 0.31f), Point(0.57f, 0.33f), Point(0.55f, 0.33f)),
            regions = listOf(
                Region("chisinau", "Chișinău", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("tiraspol", "Tiraspol (Transnistria)", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("balti", "Bălți", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "me",
            name = "Montenegro",
            continent = Continent.EUROPE,
            flag = "🇲🇪",
            worldMapPoints = listOf(Point(0.51f, 0.37f), Point(0.52f, 0.37f), Point(0.52f, 0.38f), Point(0.51f, 0.38f)),
            regions = listOf(
                Region("podgorica", "Podgorica", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("cattaro", "Cattaro (Kotor)", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("budva", "Budva", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "no",
            name = "Norvegia",
            continent = Continent.EUROPE,
            flag = "🇳🇴",
            worldMapPoints = listOf(Point(0.44f, 0.15f), Point(0.48f, 0.15f), Point(0.48f, 0.22f), Point(0.44f, 0.22f)),
            regions = listOf(
                Region("oslo", "Oslo", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("bergen", "Bergen e i Fiordi", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("tromso", "Tromsø e Aurora", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("lofoten", "Isole Lofoten", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "nl",
            name = "Paesi Bassi",
            continent = Continent.EUROPE,
            flag = "🇳🇱",
            worldMapPoints = listOf(Point(0.44f, 0.28f), Point(0.46f, 0.28f), Point(0.46f, 0.30f), Point(0.44f, 0.30f)),
            regions = listOf(
                Region("olanda_nord", "Olanda Settentrionale (Amsterdam)", "#FFF176", listOf(Point(0.1f, 0.1f))),
                Region("olanda_sud", "Olanda Meridionale (Rotterdam)", "#FFB74D", listOf(Point(0.2f, 0.2f))),
                Region("utrecht", "Utrecht", "#81C784", listOf(Point(0.3f, 0.3f))),
                Region("gheldria", "Gheldria", "#4FC3F7", listOf(Point(0.4f, 0.4f))),
                Region("limburgo", "Limburgo", "#BA68C8", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "pl",
            name = "Polonia",
            continent = Continent.EUROPE,
            flag = "🇵🇱",
            worldMapPoints = listOf(Point(0.49f, 0.28f), Point(0.53f, 0.28f), Point(0.53f, 0.31f), Point(0.49f, 0.31f)),
            regions = listOf(
                Region("varsavia", "Varsavia", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("cracovia", "Cracovia", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("danzica", "Danzica", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("breslavia", "Breslavia", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "pt",
            name = "Portogallo",
            continent = Continent.EUROPE,
            flag = "🇵🇹",
            worldMapPoints = listOf(Point(0.38f, 0.37f), Point(0.40f, 0.37f), Point(0.40f, 0.40f), Point(0.38f, 0.40f)),
            regions = listOf(
                Region("lisbona", "Lisbona", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("porto", "Porto", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("algarve", "Algarve", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("madera", "Madera", "#4FC3F7", listOf(Point(0.5f, 0.5f))),
                Region("azzorre", "Azzorre", "#BA68C8", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "mc",
            name = "Principato di Monaco",
            continent = Continent.EUROPE,
            flag = "🇲🇨",
            worldMapPoints = listOf(Point(0.465f, 0.355f), Point(0.47f, 0.355f), Point(0.47f, 0.36f), Point(0.465f, 0.36f)),
            regions = listOf(
                Region("monte_carlo", "Monte Carlo", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("monaco_ville", "Monaco-Ville", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("la_condamine", "La Condamine", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "gb",
            name = "Regno Unito",
            continent = Continent.EUROPE,
            flag = "🇬🇧",
            worldMapPoints = listOf(Point(0.41f, 0.25f), Point(0.45f, 0.25f), Point(0.45f, 0.29f), Point(0.41f, 0.29f)),
            regions = listOf(
                Region("inghilterra", "Inghilterra (Londra)", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("scozia", "Scozia (Edimburgo)", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("galles", "Galles (Cardiff)", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("irlanda_nord", "Irlanda del Nord (Belfast)", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "cz",
            name = "Repubblica Ceca",
            continent = Continent.EUROPE,
            flag = "🇨🇿",
            worldMapPoints = listOf(Point(0.48f, 0.31f), Point(0.51f, 0.31f), Point(0.51f, 0.33f), Point(0.48f, 0.33f)),
            regions = listOf(
                Region("praga", "Praga", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("boemia", "Boemia", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("moravia", "Moravia", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "ro",
            name = "Romania",
            continent = Continent.EUROPE,
            flag = "🇷🇴",
            worldMapPoints = listOf(Point(0.53f, 0.33f), Point(0.56f, 0.33f), Point(0.56f, 0.35f), Point(0.53f, 0.35f)),
            regions = listOf(
                Region("bucarest", "Bucarest", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("transilvania", "Transilvania", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("maramures", "Maramureș", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("costanza", "Costanza", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),

        Country(
            id = "sm",
            name = "San Marino",
            continent = Continent.EUROPE,
            flag = "🇸🇲",
            worldMapPoints = listOf(Point(0.485f, 0.365f), Point(0.49f, 0.365f), Point(0.49f, 0.37f), Point(0.485f, 0.37f)),
            regions = listOf(
                Region("sm_citta", "Città di San Marino", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("borgo", "Borgo Maggiore", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("dogana", "Dogana", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "rs",
            name = "Serbia",
            continent = Continent.EUROPE,
            flag = "🇷🇸",
            worldMapPoints = listOf(Point(0.52f, 0.35f), Point(0.54f, 0.35f), Point(0.54f, 0.37f), Point(0.52f, 0.37f)),
            regions = listOf(
                Region("belgrado", "Belgrado", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("novisad", "Novi Sad", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("nis", "Niš", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "sk",
            name = "Slovacchia",
            continent = Continent.EUROPE,
            flag = "🇸🇰",
            worldMapPoints = listOf(Point(0.51f, 0.32f), Point(0.53f, 0.32f), Point(0.53f, 0.34f), Point(0.51f, 0.34f)),
            regions = listOf(
                Region("bratislava", "Bratislava", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("kosice", "Košice", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("tatra", "Alti Tatra", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "si",
            name = "Slovenia",
            continent = Continent.EUROPE,
            flag = "🇸🇮",
            worldMapPoints = listOf(Point(0.48f, 0.34f), Point(0.49f, 0.34f), Point(0.49f, 0.35f), Point(0.48f, 0.35f)),
            regions = listOf(
                Region("lubiana", "Lubiana", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("bled", "Lago di Bled", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("capodistria", "Capodistria", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "es",
            name = "Spagna",
            continent = Continent.EUROPE,
            flag = "🇪🇸",
            worldMapPoints = listOf(Point(0.41f, 0.36f), Point(0.44f, 0.36f), Point(0.44f, 0.40f), Point(0.40f, 0.40f)),
            regions = listOf(
                Region("galicia", "Galizia", "#4FC3F7", listOf(Point(0.05f, 0.08f), Point(0.22f, 0.08f), Point(0.19f, 0.28f), Point(0.03f, 0.25f))),
                Region("asturias", "Asturie", "#81C784", listOf(Point(0.23f, 0.07f), Point(0.38f, 0.07f), Point(0.36f, 0.18f), Point(0.21f, 0.18f))),
                Region("cantabria", "Cantabria", "#FFF176", listOf(Point(0.39f, 0.07f), Point(0.48f, 0.07f), Point(0.47f, 0.18f), Point(0.37f, 0.18f))),
                Region("pais_vasco", "Paesi Baschi", "#FFB74D", listOf(Point(0.49f, 0.08f), Point(0.58f, 0.09f), Point(0.56f, 0.2f), Point(0.48f, 0.19f))),
                Region("navarra", "Navarra", "#FF8A65", listOf(Point(0.57f, 0.13f), Point(0.66f, 0.14f), Point(0.63f, 0.26f), Point(0.55f, 0.24f))),
                Region("aragon", "Aragona", "#F06292", listOf(Point(0.59f, 0.23f), Point(0.73f, 0.21f), Point(0.69f, 0.48f), Point(0.55f, 0.46f))),
                Region("cataluna", "Catalogna", "#BA68C8", listOf(Point(0.74f, 0.15f), Point(0.95f, 0.18f), Point(0.85f, 0.38f), Point(0.71f, 0.32f))),
                Region("castilla_leon", "Castiglia e León", "#9575CD", listOf(Point(0.18f, 0.23f), Point(0.51f, 0.22f), Point(0.46f, 0.51f), Point(0.14f, 0.48f))),
                Region("madrid", "Comunità di Madrid", "#E57373", listOf(Point(0.44f, 0.46f), Point(0.54f, 0.46f), Point(0.52f, 0.56f), Point(0.42f, 0.55f))),
                Region("extremadura", "Estremadura", "#7986CB", listOf(Point(0.15f, 0.52f), Point(0.33f, 0.53f), Point(0.29f, 0.78f), Point(0.11f, 0.74f))),
                Region("castilla_mancha", "Castiglia-La Mancia", "#64B5F6", listOf(Point(0.34f, 0.52f), Point(0.61f, 0.51f), Point(0.56f, 0.78f), Point(0.31f, 0.76f))),
                Region("valencia", "Comunità Valenciana", "#4DD0E1", listOf(Point(0.62f, 0.45f), Point(0.74f, 0.47f), Point(0.68f, 0.76f), Point(0.58f, 0.71f))),
                Region("andalucia", "Andalusia", "#AED581", listOf(Point(0.18f, 0.77f), Point(0.58f, 0.79f), Point(0.51f, 0.96f), Point(0.13f, 0.92f))),
                Region("murcia", "Murcia", "#FFF176", listOf(Point(0.55f, 0.75f), Point(0.65f, 0.76f), Point(0.61f, 0.86f), Point(0.52f, 0.84f))),
                Region("baleares", "Isole Baleari", "#FFD54F", listOf(Point(0.77f, 0.52f), Point(0.92f, 0.53f), Point(0.89f, 0.65f), Point(0.75f, 0.62f)))
            )
        ),
        Country(
            id = "se",
            name = "Svezia",
            continent = Continent.EUROPE,
            flag = "🇸🇪",
            worldMapPoints = listOf(Point(0.46f, 0.16f), Point(0.49f, 0.16f), Point(0.49f, 0.23f), Point(0.46f, 0.23f)),
            regions = listOf(
                Region("stoccolma", "Stoccolma", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("goteborg", "Göteborg", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("malmo", "Malmö", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("lapponia_se", "Lapponia Svedese", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "ch",
            name = "Svizzera",
            continent = Continent.EUROPE,
            flag = "🇨🇭",
            worldMapPoints = listOf(Point(0.46f, 0.34f), Point(0.48f, 0.34f), Point(0.48f, 0.36f), Point(0.46f, 0.36f)),
            regions = listOf(
                Region("ticino", "Canton Ticino", "#FFF176", listOf(Point(0.1f, 0.1f))),
                Region("grigioni", "Canton Grigioni", "#FFB74D", listOf(Point(0.2f, 0.2f))),
                Region("zurigo", "Canton Zurigo", "#81C784", listOf(Point(0.3f, 0.3f))),
                Region("ginevra", "Canton Ginevra", "#4FC3F7", listOf(Point(0.4f, 0.4f))),
                Region("berna", "Canton Berna", "#BA68C8", listOf(Point(0.5f, 0.5f))),
                Region("vallese", "Canton Vallese", "#FF8A65", listOf(Point(0.6f, 0.6f)))
            )
        ),
        Country(
            id = "ua",
            name = "Ucraina",
            continent = Continent.EUROPE,
            flag = "🇺🇦",
            worldMapPoints = listOf(Point(0.54f, 0.28f), Point(0.58f, 0.28f), Point(0.58f, 0.31f), Point(0.54f, 0.31f)),
            regions = listOf(
                Region("kiev", "Kiev", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("leopoli", "Leopoli", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("odessa", "Odessa", "#81C784", listOf(Point(0.5f, 0.5f))),
                Region("carpazi", "Carpazi", "#4FC3F7", listOf(Point(0.5f, 0.5f)))
            )
        ),
        Country(
            id = "hu",
            name = "Ungheria",
            continent = Continent.EUROPE,
            flag = "🇭🇺",
            worldMapPoints = listOf(Point(0.51f, 0.33f), Point(0.53f, 0.33f), Point(0.53f, 0.35f), Point(0.51f, 0.35f)),
            regions = listOf(
                Region("budapest", "Budapest", "#FFF176", listOf(Point(0.5f, 0.5f))),
                Region("balaton", "Lago Balaton", "#FFB74D", listOf(Point(0.5f, 0.5f))),
                Region("debrecen", "Debrecen", "#81C784", listOf(Point(0.5f, 0.5f)))
            )
        ),

        // --- NORTH AMERICA ---
        Country(
            id = "us",
            name = "Stati Uniti",
            continent = Continent.NORTH_AMERICA,
            flag = "🇺🇸",
            worldMapPoints = listOf(
                Point(0.10f, 0.25f),
                Point(0.26f, 0.25f),
                Point(0.24f, 0.35f),
                Point(0.12f, 0.35f)
            ),
            regions = listOf(
                Region("west", "Stati dell'Ovest", "#4FC3F7", listOf(Point(0.05f, 0.1f), Point(0.35f, 0.1f), Point(0.31f, 0.85f), Point(0.04f, 0.8f))),
                Region("midwest", "Midwest", "#AED581", listOf(Point(0.36f, 0.12f), Point(0.65f, 0.12f), Point(0.61f, 0.55f), Point(0.34f, 0.53f))),
                Region("south", "Stati del Sud", "#FFB74D", listOf(Point(0.33f, 0.56f), Point(0.78f, 0.58f), Point(0.72f, 0.94f), Point(0.3f, 0.91f))),
                Region("northeast", "Nord-est", "#BA68C8", listOf(Point(0.66f, 0.15f), Point(0.95f, 0.16f), Point(0.91f, 0.51f), Point(0.63f, 0.49f)))
            )
        ),
        Country(
            id = "ca",
            name = "Canada",
            continent = Continent.NORTH_AMERICA,
            flag = "🇨🇦",
            worldMapPoints = listOf(
                Point(0.10f, 0.12f),
                Point(0.28f, 0.12f),
                Point(0.26f, 0.22f),
                Point(0.12f, 0.22f)
            ),
            regions = listOf(
                Region("west_ca", "Canada Occidentale", "#81C784", listOf(Point(0.05f, 0.1f), Point(0.48f, 0.1f), Point(0.44f, 0.9f), Point(0.04f, 0.85f))),
                Region("east_ca", "Canada Orientale", "#4DD0E1", listOf(Point(0.49f, 0.12f), Point(0.95f, 0.15f), Point(0.91f, 0.92f), Point(0.46f, 0.89f)))
            )
        ),
        Country(
            id = "mx",
            name = "Messico",
            continent = Continent.NORTH_AMERICA,
            flag = "🇲🇽",
            worldMapPoints = listOf(
                Point(0.12f, 0.36f),
                Point(0.23f, 0.36f),
                Point(0.25f, 0.44f),
                Point(0.14f, 0.43f)
            ),
            regions = listOf(
                Region("mx_cdmx", "Città del Messico (CDMX)", "#FFF176", listOf(Point(0.42f, 0.50f), Point(0.58f, 0.50f), Point(0.58f, 0.65f), Point(0.42f, 0.65f))),
                Region("mx_yucatan", "Yucatán & Riviera Maya (Cancún)", "#4FC3F7", listOf(Point(0.70f, 0.42f), Point(0.96f, 0.42f), Point(0.96f, 0.68f), Point(0.70f, 0.68f))),
                Region("mx_oaxaca", "Oaxaca e Chiapas", "#81C784", listOf(Point(0.46f, 0.66f), Point(0.78f, 0.66f), Point(0.76f, 0.94f), Point(0.44f, 0.94f))),
                Region("mx_baja", "Bassa California (Tijuana, Cabo)", "#FFB74D", listOf(Point(0.04f, 0.06f), Point(0.24f, 0.12f), Point(0.28f, 0.55f), Point(0.12f, 0.52f))),
                Region("mx_jalisco", "Jalisco e Costa Pacifica (Guadalajara, Puerto Vallarta)", "#BA68C8", listOf(Point(0.22f, 0.48f), Point(0.44f, 0.48f), Point(0.42f, 0.72f), Point(0.20f, 0.68f))),
                Region("mx_centro", "Messico Centrale (Puebla, Guanajuato, Querétaro)", "#4DD0E1", listOf(Point(0.35f, 0.35f), Point(0.62f, 0.35f), Point(0.60f, 0.52f), Point(0.33f, 0.52f))),
                Region("mx_nord", "Nord del Messico (Monterrey, Chihuahua)", "#FF8A65", listOf(Point(0.22f, 0.12f), Point(0.68f, 0.14f), Point(0.64f, 0.40f), Point(0.20f, 0.38f)))
            )
        ),
        // --- ASIA ---
        Country(
            id = "af",
            name = "Afghanistan",
            continent = Continent.ASIA,
            flag = "🇦🇫",
            regions = listOf(
                Region("af_kb", "Kabul", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("af_hr", "Herat", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "sa_arabia",
            name = "Arabia Saudita",
            continent = Continent.ASIA,
            flag = "🇸🇦",
            regions = listOf(
                Region("sa_ri", "Riad", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("sa_ge", "Gedda", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "am",
            name = "Armenia",
            continent = Continent.ASIA,
            flag = "🇦🇲",
            regions = listOf(
                Region("am_er", "Erevan", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("am_gy", "Gyumri", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "az",
            name = "Azerbaigian",
            continent = Continent.ASIA,
            flag = "🇦🇿",
            regions = listOf(
                Region("az_ba", "Baku", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("az_ga", "Ganja", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "bh",
            name = "Bahrein",
            continent = Continent.ASIA,
            flag = "🇧🇭",
            regions = listOf(
                Region("bh_ma", "Manama", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("bh_mu", "Al Muharraq", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "bd",
            name = "Bangladesh",
            continent = Continent.ASIA,
            flag = "🇧🇩",
            regions = listOf(
                Region("bd_da", "Dacca", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("bd_ch", "Chittagong", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "bt",
            name = "Bhutan",
            continent = Continent.ASIA,
            flag = "🇧🇹",
            regions = listOf(
                Region("bt_th", "Thimphu", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("bt_pa", "Paro", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "bn",
            name = "Brunei",
            continent = Continent.ASIA,
            flag = "🇧🇳",
            regions = listOf(
                Region("bn_bs", "Bandar Seri Begawan", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("bn_be", "Belait", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "kh",
            name = "Cambogia",
            continent = Continent.ASIA,
            flag = "🇰🇭",
            regions = listOf(
                Region("kh_pp", "Phnom Penh", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("kh_sr", "Siem Reap", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "cn",
            name = "Cina",
            continent = Continent.ASIA,
            flag = "🇨🇳",
            worldMapPoints = listOf(
                Point(0.70f, 0.32f),
                Point(0.78f, 0.32f),
                Point(0.76f, 0.42f),
                Point(0.68f, 0.42f)
            ),
            regions = listOf(
                Region("north_cn", "Cina Settentrionale", "#FFF176", listOf(Point(0.48f, 0.08f), Point(0.95f, 0.1f), Point(0.88f, 0.51f), Point(0.42f, 0.48f))),
                Region("south_cn", "Cina Meridionale", "#AED581", listOf(Point(0.41f, 0.51f), Point(0.92f, 0.53f), Point(0.85f, 0.92f), Point(0.36f, 0.88f))),
                Region("west_cn", "Cina Occidentale (Tibet & Xinjiang)", "#4DD0E1", listOf(Point(0.05f, 0.15f), Point(0.42f, 0.13f), Point(0.38f, 0.88f), Point(0.03f, 0.85f)))
            )
        ),
        Country(
            id = "cy",
            name = "Cipro",
            continent = Continent.ASIA,
            flag = "🇨🇾",
            regions = listOf(
                Region("cy_ni", "Nicosia", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("cy_li", "Limassol", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "kp",
            name = "Corea del Nord",
            continent = Continent.ASIA,
            flag = "🇰🇵",
            regions = listOf(
                Region("kp_py", "Pyongyang", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("kp_ka", "Kaesong", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "kr",
            name = "Corea del Sud",
            continent = Continent.ASIA,
            flag = "🇰🇷",
            regions = listOf(
                Region("kr_se", "Seul", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("kr_bu", "Busan", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ae",
            name = "Emirati Arabi Uniti",
            continent = Continent.ASIA,
            flag = "🇦🇪",
            regions = listOf(
                Region("ae_du", "Dubai", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("ae_ad", "Abu Dhabi", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ph",
            name = "Filippine",
            continent = Continent.ASIA,
            flag = "🇵🇭",
            regions = listOf(
                Region("ph_ma", "Manila", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("ph_ce", "Cebu", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ge",
            name = "Georgia",
            continent = Continent.ASIA,
            flag = "🇬🇪",
            regions = listOf(
                Region("ge_tb", "Tbilisi", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("ge_ba", "Batumi", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "jp",
            name = "Giappone",
            continent = Continent.ASIA,
            flag = "🇯🇵",
            worldMapPoints = listOf(
                Point(0.82f, 0.32f),
                Point(0.85f, 0.32f),
                Point(0.84f, 0.38f),
                Point(0.81f, 0.38f)
            ),
            regions = listOf(
                Region("hokkaido", "Hokkaido", "#FF8A65", listOf(Point(0.65f, 0.05f), Point(0.95f, 0.08f), Point(0.85f, 0.34f), Point(0.58f, 0.28f))),
                Region("tohoku", "Tohoku", "#FFF176", listOf(Point(0.52f, 0.31f), Point(0.78f, 0.33f), Point(0.72f, 0.54f), Point(0.48f, 0.51f))),
                Region("kanto", "Kanto", "#81C784", listOf(Point(0.46f, 0.52f), Point(0.68f, 0.53f), Point(0.64f, 0.74f), Point(0.42f, 0.71f))),
                Region("chubu", "Chubu", "#4FC3F7", listOf(Point(0.25f, 0.48f), Point(0.44f, 0.49f), Point(0.41f, 0.72f), Point(0.22f, 0.69f))),
                Region("kansai", "Kansai", "#BA68C8", listOf(Point(0.18f, 0.71f), Point(0.39f, 0.72f), Point(0.36f, 0.88f), Point(0.15f, 0.85f))),
                Region("chugoku_shikoku_kyushu", "Kyushu, Shikoku & Chugoku", "#FFB74D", listOf(Point(0.05f, 0.75f), Point(0.17f, 0.74f), Point(0.28f, 0.95f), Point(0.02f, 0.92f)))
            )
        ),
        Country(
            id = "jo",
            name = "Giordania",
            continent = Continent.ASIA,
            flag = "🇯🇴",
            regions = listOf(
                Region("jo_am", "Amman", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("jo_pe", "Petra", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "in",
            name = "India",
            continent = Continent.ASIA,
            flag = "🇮🇳",
            regions = listOf(
                Region("in_nd", "Nuova Delhi", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("in_mu", "Mumbai", "#81C784", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("in_ra", "Rajasthan", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "id_country",
            name = "Indonesia",
            continent = Continent.ASIA,
            flag = "🇮🇩",
            regions = listOf(
                Region("id_gi", "Giacarta", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("id_ba", "Bali", "#81C784", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("id_su", "Sumatra", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ir",
            name = "Iran",
            continent = Continent.ASIA,
            flag = "🇮🇷",
            regions = listOf(
                Region("ir_te", "Teheran", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("ir_is", "Isfahan", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "iq",
            name = "Iraq",
            continent = Continent.ASIA,
            flag = "🇮🇶",
            regions = listOf(
                Region("iq_ba", "Baghdad", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("iq_er", "Erbil", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "il",
            name = "Israele",
            continent = Continent.ASIA,
            flag = "🇮🇱",
            regions = listOf(
                Region("il_ge", "Gerusalemme", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("il_ta", "Tel Aviv", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "kz",
            name = "Kazakistan",
            continent = Continent.ASIA,
            flag = "🇰🇿",
            regions = listOf(
                Region("kz_as", "Astana", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("kz_al", "Almaty", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "kg",
            name = "Kirghizistan",
            continent = Continent.ASIA,
            flag = "🇰🇬",
            regions = listOf(
                Region("kg_bi", "Bishkek", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("kg_os", "Osh", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "kw",
            name = "Kuwait",
            continent = Continent.ASIA,
            flag = "🇰🇼",
            regions = listOf(
                Region("kw_ku", "Al Kuwait", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("kw_ja", "Jahra", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "la_country",
            name = "Laos",
            continent = Continent.ASIA,
            flag = "🇱🇦",
            regions = listOf(
                Region("la_vi", "Vientiane", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("la_lp", "Luang Prabang", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "lb",
            name = "Libano",
            continent = Continent.ASIA,
            flag = "🇱🇧",
            regions = listOf(
                Region("lb_be", "Beirut", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("lb_tr", "Tripoli", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "my_country",
            name = "Malesia",
            continent = Continent.ASIA,
            flag = "🇲🇾",
            regions = listOf(
                Region("my_kl", "Kuala Lumpur", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("my_pe", "Penang", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "mv",
            name = "Maldive",
            continent = Continent.ASIA,
            flag = "🇲🇻",
            regions = listOf(
                Region("mv_ma", "Malé", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("mv_ar", "Atollo di Ari", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "mn",
            name = "Mongolia",
            continent = Continent.ASIA,
            flag = "🇲🇳",
            regions = listOf(
                Region("mn_ul", "Ulaanbaatar", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("mn_go", "Deserto del Gobi", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "mm",
            name = "Myanmar",
            continent = Continent.ASIA,
            flag = "🇲🇲",
            regions = listOf(
                Region("mm_ya", "Yangon", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("mm_ba", "Bagan", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "np",
            name = "Nepal",
            continent = Continent.ASIA,
            flag = "🇳🇵",
            regions = listOf(
                Region("np_ka", "Kathmandu", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("np_po", "Pokhara", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "om",
            name = "Oman",
            continent = Continent.ASIA,
            flag = "🇴🇲",
            regions = listOf(
                Region("om_ma", "Mascate", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("om_sa", "Salalah", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "pk",
            name = "Pakistan",
            continent = Continent.ASIA,
            flag = "🇵🇰",
            regions = listOf(
                Region("pk_is", "Islamabad", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("pk_la", "Lahore", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "qa",
            name = "Qatar",
            continent = Continent.ASIA,
            flag = "🇶🇦",
            regions = listOf(
                Region("qa_do", "Doha", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("qa_wa", "Al Wakrah", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ru",
            name = "Russia",
            continent = Continent.ASIA,
            flag = "🇷🇺",
            regions = listOf(
                Region("mosca", "Mosca", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.25f), Point(0f, 0.25f))),
                Region("spb", "San Pietroburgo", "#FFB74D", listOf(Point(0f, 0.25f), Point(1f, 0.25f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("anello_oro", "Anello d'Oro", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 0.75f), Point(0f, 0.75f))),
                Region("siberia", "Siberia", "#4FC3F7", listOf(Point(0f, 0.75f), Point(1f, 0.75f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "sg",
            name = "Singapore",
            continent = Continent.ASIA,
            flag = "🇸🇬",
            regions = listOf(
                Region("sg_mb", "Marina Bay", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("sg_se", "Sentosa", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "sy",
            name = "Siria",
            continent = Continent.ASIA,
            flag = "🇸🇾",
            regions = listOf(
                Region("sy_da", "Damasco", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("sy_al", "Aleppo", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "lk",
            name = "Sri Lanka",
            continent = Continent.ASIA,
            flag = "🇱🇰",
            regions = listOf(
                Region("lk_co", "Colombo", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("lk_ka", "Kandy", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "tj",
            name = "Tagikistan",
            continent = Continent.ASIA,
            flag = "🇹🇯",
            regions = listOf(
                Region("tj_du", "Dushanbe", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("tj_pa", "Pamir", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "tw",
            name = "Taiwan",
            continent = Continent.ASIA,
            flag = "🇹🇼",
            regions = listOf(
                Region("tw_ta", "Taipei", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("tw_ka", "Kaohsiung", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "th",
            name = "Thailandia",
            continent = Continent.ASIA,
            flag = "🇹🇭",
            regions = listOf(
                Region("th_ba", "Bangkok", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("th_ph", "Phuket", "#81C784", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("th_cm", "Chiang Mai", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "tl",
            name = "Timor Est",
            continent = Continent.ASIA,
            flag = "🇹🇱",
            regions = listOf(
                Region("tl_di", "Dili", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("tl_ba", "Baucau", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "tr",
            name = "Turchia",
            continent = Continent.ASIA,
            flag = "🇹🇷",
            regions = listOf(
                Region("tr_is", "Istanbul", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("tr_ca", "Cappadocia", "#81C784", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("tr_ae", "Costa Egea", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "tm",
            name = "Turkmenistan",
            continent = Continent.ASIA,
            flag = "🇹🇲",
            regions = listOf(
                Region("tm_as", "Ashgabat", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("tm_me", "Merv", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "uz",
            name = "Uzbekistan",
            continent = Continent.ASIA,
            flag = "🇺🇿",
            regions = listOf(
                Region("uz_ta", "Tashkent", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("uz_sa", "Samarcanda", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "vn",
            name = "Vietnam",
            continent = Continent.ASIA,
            flag = "🇻🇳",
            regions = listOf(
                Region("vn_ha", "Hanoi", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("vn_hl", "Baia di Ha Long", "#81C784", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("vn_hc", "Ho Chi Minh City", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ye",
            name = "Yemen",
            continent = Continent.ASIA,
            flag = "🇾🇪",
            regions = listOf(
                Region("ye_sa", "Sana'a", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("ye_so", "Socotra", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        // --- SOUTH AMERICA ---
        Country(
            id = "ar",
            name = "Argentina",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇦🇷",
            regions = listOf(
                Region("ar_ba", "Buenos Aires", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("ar_pam", "La Pampa", "#81C784", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("ar_pat", "Patagonia", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "bo",
            name = "Bolivia",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇧🇴",
            regions = listOf(
                Region("bo_lp", "La Paz", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("bo_sc", "Santa Cruz", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "br",
            name = "Brasile",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇧🇷",
            worldMapPoints = listOf(
                Point(0.28f, 0.58f),
                Point(0.38f, 0.58f),
                Point(0.35f, 0.72f),
                Point(0.26f, 0.72f)
            ),
            regions = listOf(
                Region("north_br", "Regione Nord", "#81C784", listOf(Point(0.05f, 0.08f), Point(0.62f, 0.1f), Point(0.54f, 0.52f), Point(0.03f, 0.46f))),
                Region("northeast_br", "Regione Nord-Est", "#FFF176", listOf(Point(0.63f, 0.11f), Point(0.95f, 0.22f), Point(0.85f, 0.58f), Point(0.53f, 0.52f))),
                Region("central_br", "Centro-Ovest & Sud-Est", "#FFB74D", listOf(Point(0.28f, 0.51f), Point(0.78f, 0.53f), Point(0.72f, 0.92f), Point(0.24f, 0.88f))),
                Region("south_br", "Regione Sud", "#64B5F6", listOf(Point(0.38f, 0.85f), Point(0.62f, 0.86f), Point(0.58f, 0.98f), Point(0.34f, 0.96f)))
            )
        ),
        Country(
            id = "cl",
            name = "Cile",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇨🇱",
            regions = listOf(
                Region("cl_at", "Deserto di Atacama", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("cl_st", "Santiago e Centro", "#FFB74D", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("cl_pa", "Patagonia Cilena", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "co",
            name = "Colombia",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇨🇴",
            regions = listOf(
                Region("co_ca", "Costa dei Caraibi", "#4FC3F7", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("co_bo", "Bogotà e Ande", "#FFF176", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("co_am", "Amazzonia", "#81C784", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ec",
            name = "Ecuador",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇪🇨",
            regions = listOf(
                Region("ec_qi", "Quito e Sierra", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("ec_co", "Costa Pacifica", "#81C784", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("ec_ga", "Isole Galápagos", "#4FC3F7", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "gy",
            name = "Guyana",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇬🇾",
            regions = listOf(
                Region("gy_gt", "Georgetown", "#FFB74D", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("gy_in", "Entroterra Selvaggio", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "py",
            name = "Paraguay",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇵🇾",
            regions = listOf(
                Region("py_as", "Asunción", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("py_ch", "Gran Chaco", "#FFB74D", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "pe",
            name = "Perù",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇵🇪",
            regions = listOf(
                Region("pe_li", "Lima e Costa", "#FFB74D", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("pe_cu", "Cusco e Machu Picchu", "#FFF176", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("pe_am", "Amazzonia Peruviana", "#81C784", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "sr",
            name = "Suriname",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇸🇷",
            regions = listOf(
                Region("sr_pa", "Paramaribo", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("sr_in", "Foresta Pluviale", "#81C784", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "tt",
            name = "Trinidad e Tobago",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇹🇹",
            regions = listOf(
                Region("tt_tr", "Trinidad", "#FF8A65", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("tt_to", "Tobago", "#4FC3F7", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "uy",
            name = "Uruguay",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇺🇾",
            regions = listOf(
                Region("uy_mv", "Montevideo", "#FFF176", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.5f), Point(0f, 0.5f))),
                Region("uy_pe", "Punta del Este", "#4FC3F7", listOf(Point(0f, 0.5f), Point(1f, 0.5f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        Country(
            id = "ve",
            name = "Venezuela",
            continent = Continent.SOUTH_AMERICA,
            flag = "🇻🇪",
            regions = listOf(
                Region("ve_ca", "Caracas e Centro", "#FFB74D", listOf(Point(0f, 0f), Point(1f, 0f), Point(1f, 0.33f), Point(0f, 0.33f))),
                Region("ve_sa", "Canaima e Salto Angel", "#4FC3F7", listOf(Point(0f, 0.33f), Point(1f, 0.33f), Point(1f, 0.66f), Point(0f, 0.66f))),
                Region("ve_cv", "Costa Caraibica", "#FFF176", listOf(Point(0f, 0.66f), Point(1f, 0.66f), Point(1f, 1f), Point(0f, 1f)))
            )
        ),
        // --- AFRICA ---
        Country(
            id = "eg",
            name = "Egitto",
            continent = Continent.AFRICA,
            flag = "🇪🇬",
            worldMapPoints = listOf(
                Point(0.51f, 0.45f),
                Point(0.55f, 0.45f),
                Point(0.54f, 0.50f),
                Point(0.50f, 0.50f)
            ),
            regions = listOf(
                Region("lower_eg", "Basso Egitto (Delta e Cairo)", "#4FC3F7", listOf(Point(0.12f, 0.05f), Point(0.88f, 0.05f), Point(0.82f, 0.38f), Point(0.15f, 0.36f))),
                Region("upper_eg", "Alto Egitto (Sud)", "#FFB74D", listOf(Point(0.14f, 0.41f), Point(0.84f, 0.43f), Point(0.78f, 0.95f), Point(0.11f, 0.92f)))
            )
        ),
        Country(
            id = "za",
            name = "Sudafrica",
            continent = Continent.AFRICA,
            flag = "🇿🇦",
            worldMapPoints = listOf(
                Point(0.52f, 0.72f),
                Point(0.56f, 0.72f),
                Point(0.55f, 0.78f),
                Point(0.51f, 0.78f)
            ),
            regions = listOf(
                Region("western_cape", "Western Cape", "#FF8A65", listOf(Point(0.05f, 0.52f), Point(0.42f, 0.55f), Point(0.38f, 0.95f), Point(0.03f, 0.91f))),
                Region("northern_eastern_cape", "Northern & Eastern Cape", "#4DD0E1", listOf(Point(0.12f, 0.12f), Point(0.65f, 0.15f), Point(0.58f, 0.58f), Point(0.08f, 0.51f))),
                Region("inland_za", "Province Interne (Gauteng, Free State, etc.)", "#AED581", listOf(Point(0.48f, 0.16f), Point(0.95f, 0.18f), Point(0.88f, 0.88f), Point(0.44f, 0.82f)))
            )
        ),
        // --- OCEANIA ---
        Country(
            id = "au",
            name = "Australia",
            continent = Continent.OCEANIA,
            flag = "🇦🇺",
            worldMapPoints = listOf(
                Point(0.80f, 0.70f),
                Point(0.92f, 0.70f),
                Point(0.90f, 0.82f),
                Point(0.78f, 0.82f)
            ),
            regions = listOf(
                Region("wa", "Western Australia", "#FFF176", listOf(Point(0.05f, 0.1f), Point(0.42f, 0.1f), Point(0.39f, 0.9f), Point(0.03f, 0.85f))),
                Region("nt", "Northern Territory", "#FFB74D", listOf(Point(0.43f, 0.12f), Point(0.68f, 0.12f), Point(0.65f, 0.48f), Point(0.41f, 0.46f))),
                Region("sa", "South Australia", "#81C784", listOf(Point(0.41f, 0.5f), Point(0.66f, 0.52f), Point(0.63f, 0.92f), Point(0.38f, 0.88f))),
                Region("qld", "Queensland", "#4FC3F7", listOf(Point(0.69f, 0.15f), Point(0.95f, 0.16f), Point(0.88f, 0.54f), Point(0.66f, 0.51f))),
                Region("nsw_vic", "New South Wales & Victoria", "#9575CD", listOf(Point(0.67f, 0.56f), Point(0.92f, 0.58f), Point(0.85f, 0.95f), Point(0.62f, 0.91f))),
                Region("tas", "Tasmania", "#F06292", listOf(Point(0.75f, 0.96f), Point(0.85f, 0.96f), Point(0.83f, 0.99f), Point(0.73f, 0.99f)))
            )
        ),
        Country(
            id = "nz",
            name = "Nuova Zelanda",
            continent = Continent.OCEANIA,
            flag = "🇳🇿",
            worldMapPoints = listOf(
                Point(0.88f, 0.82f),
                Point(0.93f, 0.82f),
                Point(0.93f, 0.88f),
                Point(0.88f, 0.88f)
            ),
            regions = listOf(
                Region("nz_north", "Isola del Nord (Auckland, Wellington)", "#81C784", listOf(Point(0.35f, 0.08f), Point(0.85f, 0.12f), Point(0.75f, 0.50f), Point(0.30f, 0.45f))),
                Region("nz_south", "Isola del Sud (Christchurch, Queenstown)", "#4FC3F7", listOf(Point(0.15f, 0.48f), Point(0.65f, 0.52f), Point(0.55f, 0.92f), Point(0.10f, 0.88f))),
                Region("nz_bay", "Bay of Islands & Northland", "#FFF176", listOf(Point(0.40f, 0.02f), Point(0.70f, 0.04f), Point(0.65f, 0.20f), Point(0.38f, 0.18f))),
                Region("nz_fiordland", "Fiordland & Laghi del Sud", "#FFB74D", listOf(Point(0.05f, 0.70f), Point(0.45f, 0.72f), Point(0.40f, 0.98f), Point(0.04f, 0.95f)))
            )
        ),
        Country(
            id = "pg",
            name = "Papua Nuova Guinea",
            continent = Continent.OCEANIA,
            flag = "🇵🇬",
            worldMapPoints = listOf(
                Point(0.82f, 0.58f),
                Point(0.87f, 0.58f),
                Point(0.87f, 0.63f),
                Point(0.82f, 0.63f)
            ),
            regions = listOf(
                Region("pg_pom", "Port Moresby e Regione Papuana", "#FFF176", listOf(Point(0.35f, 0.55f), Point(0.85f, 0.55f), Point(0.80f, 0.90f), Point(0.30f, 0.90f))),
                Region("pg_high", "Highlands (Altipiani Centrali)", "#81C784", listOf(Point(0.15f, 0.30f), Point(0.60f, 0.30f), Point(0.55f, 0.65f), Point(0.12f, 0.65f))),
                Region("pg_mom", "Regione di Momase (Madang, Wewak)", "#4FC3F7", listOf(Point(0.10f, 0.10f), Point(0.65f, 0.10f), Point(0.60f, 0.38f), Point(0.08f, 0.38f))),
                Region("pg_islands", "Regione delle Isole (Rabaul, Bougainville)", "#FF8A65", listOf(Point(0.65f, 0.15f), Point(0.95f, 0.18f), Point(0.92f, 0.60f), Point(0.62f, 0.58f)))
            )
        ),
        Country(
            id = "fj",
            name = "Figi",
            continent = Continent.OCEANIA,
            flag = "🇫🇯",
            worldMapPoints = listOf(
                Point(0.92f, 0.68f),
                Point(0.95f, 0.68f),
                Point(0.95f, 0.71f),
                Point(0.92f, 0.71f)
            ),
            regions = listOf(
                Region("fj_viti", "Viti Levu (Suva, Nadi)", "#4FC3F7", listOf(Point(0.15f, 0.30f), Point(0.60f, 0.30f), Point(0.55f, 0.80f), Point(0.12f, 0.80f))),
                Region("fj_vanua", "Vanua Levu", "#81C784", listOf(Point(0.45f, 0.10f), Point(0.90f, 0.12f), Point(0.85f, 0.45f), Point(0.40f, 0.42f))),
                Region("fj_mamanuca", "Isole Mamanuca e Yasawa", "#FFF176", listOf(Point(0.05f, 0.15f), Point(0.30f, 0.15f), Point(0.28f, 0.60f), Point(0.04f, 0.60f))),
                Region("fj_islands", "Taveuni e Isole Lau", "#FFB74D", listOf(Point(0.65f, 0.45f), Point(0.95f, 0.48f), Point(0.92f, 0.90f), Point(0.62f, 0.88f)))
            )
        ),
        Country(
            id = "sb",
            name = "Isole Salomone",
            continent = Continent.OCEANIA,
            flag = "🇸🇧",
            worldMapPoints = listOf(
                Point(0.87f, 0.62f),
                Point(0.90f, 0.62f),
                Point(0.90f, 0.65f),
                Point(0.87f, 0.65f)
            ),
            regions = listOf(
                Region("sb_guad", "Guadalcanal (Honiara)", "#81C784", listOf(Point(0.35f, 0.45f), Point(0.75f, 0.45f), Point(0.70f, 0.80f), Point(0.32f, 0.80f))),
                Region("sb_west", "Provincia Occidentale (Gizo)", "#4FC3F7", listOf(Point(0.05f, 0.15f), Point(0.45f, 0.18f), Point(0.40f, 0.50f), Point(0.04f, 0.48f))),
                Region("sb_mal", "Malaita", "#FFF176", listOf(Point(0.55f, 0.20f), Point(0.85f, 0.22f), Point(0.80f, 0.55f), Point(0.52f, 0.52f))),
                Region("sb_isabel", "Isabel, Choiseul e Makira", "#FFB74D", listOf(Point(0.20f, 0.10f), Point(0.60f, 0.12f), Point(0.58f, 0.35f), Point(0.18f, 0.32f)))
            )
        ),
        Country(
            id = "vu",
            name = "Vanuatu",
            continent = Continent.OCEANIA,
            flag = "🇻🇺",
            worldMapPoints = listOf(
                Point(0.89f, 0.66f),
                Point(0.92f, 0.66f),
                Point(0.92f, 0.69f),
                Point(0.89f, 0.69f)
            ),
            regions = listOf(
                Region("vu_efate", "Efate (Port Vila)", "#FFF176", listOf(Point(0.35f, 0.45f), Point(0.75f, 0.45f), Point(0.70f, 0.70f), Point(0.32f, 0.70f))),
                Region("vu_santo", "Espiritu Santo", "#81C784", listOf(Point(0.15f, 0.10f), Point(0.60f, 0.12f), Point(0.55f, 0.42f), Point(0.12f, 0.40f))),
                Region("vu_tanna", "Tanna (Monte Yasur)", "#FF8A65", listOf(Point(0.45f, 0.72f), Point(0.80f, 0.72f), Point(0.75f, 0.95f), Point(0.42f, 0.95f))),
                Region("vu_malekula", "Malekula e Isole Centrali", "#4FC3F7", listOf(Point(0.25f, 0.35f), Point(0.65f, 0.38f), Point(0.60f, 0.60f), Point(0.22f, 0.58f)))
            )
        ),
        Country(
            id = "ki",
            name = "Kiribati",
            continent = Continent.OCEANIA,
            flag = "🇰🇮",
            worldMapPoints = listOf(
                Point(0.94f, 0.55f),
                Point(0.98f, 0.55f),
                Point(0.98f, 0.58f),
                Point(0.94f, 0.58f)
            ),
            regions = listOf(
                Region("ki_gilbert", "Isole Gilbert (Tarawa)", "#4FC3F7", listOf(Point(0.05f, 0.20f), Point(0.45f, 0.20f), Point(0.40f, 0.70f), Point(0.04f, 0.70f))),
                Region("ki_line", "Isole della Linea (Kiritimati)", "#FFF176", listOf(Point(0.55f, 0.20f), Point(0.95f, 0.22f), Point(0.90f, 0.70f), Point(0.52f, 0.68f))),
                Region("ki_phoenix", "Isole della Fenice (Phoenix)", "#81C784", listOf(Point(0.30f, 0.50f), Point(0.70f, 0.52f), Point(0.68f, 0.92f), Point(0.28f, 0.90f)))
            )
        ),
        Country(
            id = "mh",
            name = "Isole Marshall",
            continent = Continent.OCEANIA,
            flag = "🇲🇭",
            worldMapPoints = listOf(
                Point(0.91f, 0.51f),
                Point(0.94f, 0.51f),
                Point(0.94f, 0.54f),
                Point(0.91f, 0.54f)
            ),
            regions = listOf(
                Region("mh_majuro", "Atollo di Majuro", "#FFF176", listOf(Point(0.35f, 0.45f), Point(0.75f, 0.45f), Point(0.70f, 0.75f), Point(0.32f, 0.75f))),
                Region("mh_kwaj", "Atollo di Kwajalein", "#4FC3F7", listOf(Point(0.15f, 0.20f), Point(0.55f, 0.22f), Point(0.50f, 0.50f), Point(0.12f, 0.48f))),
                Region("mh_ratak", "Catena Ratak (Arno, Mili, Wotje)", "#81C784", listOf(Point(0.55f, 0.15f), Point(0.95f, 0.18f), Point(0.90f, 0.65f), Point(0.52f, 0.62f))),
                Region("mh_ralik", "Catena Ralik (Jaluit, Enewetak, Bikini)", "#FFB74D", listOf(Point(0.05f, 0.40f), Point(0.45f, 0.42f), Point(0.42f, 0.88f), Point(0.04f, 0.85f)))
            )
        ),
        Country(
            id = "pw",
            name = "Palau",
            continent = Continent.OCEANIA,
            flag = "🇵🇼",
            worldMapPoints = listOf(
                Point(0.79f, 0.53f),
                Point(0.82f, 0.53f),
                Point(0.82f, 0.56f),
                Point(0.79f, 0.56f)
            ),
            regions = listOf(
                Region("pw_koror", "Koror e Rock Islands", "#4FC3F7", listOf(Point(0.30f, 0.45f), Point(0.75f, 0.45f), Point(0.70f, 0.75f), Point(0.28f, 0.75f))),
                Region("pw_babel", "Babeldaob (Ngerulmud)", "#81C784", listOf(Point(0.30f, 0.10f), Point(0.75f, 0.12f), Point(0.70f, 0.45f), Point(0.28f, 0.42f))),
                Region("pw_peleliu", "Peleliu e Angaur", "#FFF176", listOf(Point(0.25f, 0.70f), Point(0.65f, 0.72f), Point(0.62f, 0.95f), Point(0.22f, 0.92f)))
            )
        ),
        Country(
            id = "fm",
            name = "Stati Federati di Micronesia",
            continent = Continent.OCEANIA,
            flag = "🇫🇲",
            worldMapPoints = listOf(
                Point(0.84f, 0.52f),
                Point(0.89f, 0.52f),
                Point(0.89f, 0.55f),
                Point(0.84f, 0.55f)
            ),
            regions = listOf(
                Region("fm_pohnpei", "Pohnpei (Palikir)", "#81C784", listOf(Point(0.45f, 0.35f), Point(0.80f, 0.35f), Point(0.75f, 0.65f), Point(0.42f, 0.65f))),
                Region("fm_chuuk", "Chuuk (Laguna di Truk)", "#4FC3F7", listOf(Point(0.20f, 0.30f), Point(0.55f, 0.32f), Point(0.50f, 0.60f), Point(0.18f, 0.58f))),
                Region("fm_yap", "Yap", "#FFF176", listOf(Point(0.05f, 0.20f), Point(0.35f, 0.22f), Point(0.32f, 0.52f), Point(0.04f, 0.50f))),
                Region("fm_kosrae", "Kosrae", "#FFB74D", listOf(Point(0.70f, 0.50f), Point(0.95f, 0.52f), Point(0.92f, 0.85f), Point(0.68f, 0.82f)))
            )
        ),
        Country(
            id = "nr",
            name = "Nauru",
            continent = Continent.OCEANIA,
            flag = "🇳🇷",
            worldMapPoints = listOf(
                Point(0.89f, 0.57f),
                Point(0.91f, 0.57f),
                Point(0.91f, 0.59f),
                Point(0.89f, 0.59f)
            ),
            regions = listOf(
                Region("nr_coast", "Distretti Costieri e Yaren", "#4FC3F7", listOf(Point(0.10f, 0.10f), Point(0.90f, 0.10f), Point(0.85f, 0.90f), Point(0.12f, 0.90f))),
                Region("nr_interior", "Laguna Buada e Plateau Centrale", "#81C784", listOf(Point(0.30f, 0.30f), Point(0.70f, 0.30f), Point(0.68f, 0.70f), Point(0.28f, 0.70f)))
            )
        ),
        Country(
            id = "ws",
            name = "Samoa",
            continent = Continent.OCEANIA,
            flag = "🇼🇸",
            worldMapPoints = listOf(
                Point(0.95f, 0.65f),
                Point(0.98f, 0.65f),
                Point(0.98f, 0.68f),
                Point(0.95f, 0.68f)
            ),
            regions = listOf(
                Region("ws_upolu", "Upolu (Apia)", "#81C784", listOf(Point(0.45f, 0.40f), Point(0.95f, 0.42f), Point(0.90f, 0.75f), Point(0.42f, 0.72f))),
                Region("ws_savaii", "Savai'i", "#4FC3F7", listOf(Point(0.05f, 0.20f), Point(0.55f, 0.22f), Point(0.50f, 0.65f), Point(0.04f, 0.62f))),
                Region("ws_islands", "Isole Manono e Apolima", "#FFF176", listOf(Point(0.38f, 0.50f), Point(0.58f, 0.52f), Point(0.56f, 0.70f), Point(0.36f, 0.68f)))
            )
        ),
        Country(
            id = "to",
            name = "Tonga",
            continent = Continent.OCEANIA,
            flag = "🇹🇴",
            worldMapPoints = listOf(
                Point(0.94f, 0.72f),
                Point(0.97f, 0.72f),
                Point(0.97f, 0.75f),
                Point(0.94f, 0.75f)
            ),
            regions = listOf(
                Region("to_tonga", "Tongatapu (Nuku'alofa)", "#FFF176", listOf(Point(0.30f, 0.65f), Point(0.75f, 0.65f), Point(0.70f, 0.92f), Point(0.28f, 0.92f))),
                Region("to_vavau", "Gruppo Vava'u", "#4FC3F7", listOf(Point(0.35f, 0.10f), Point(0.80f, 0.12f), Point(0.75f, 0.38f), Point(0.32f, 0.35f))),
                Region("to_haapai", "Gruppo Ha'apai", "#81C784", listOf(Point(0.32f, 0.38f), Point(0.78f, 0.40f), Point(0.74f, 0.65f), Point(0.30f, 0.62f))),
                Region("to_eua", "'Eua e Niuas", "#FFB74D", listOf(Point(0.45f, 0.75f), Point(0.85f, 0.78f), Point(0.82f, 0.98f), Point(0.42f, 0.95f)))
            )
        ),
        Country(
            id = "tv",
            name = "Tuvalu",
            continent = Continent.OCEANIA,
            flag = "🇹🇻",
            worldMapPoints = listOf(
                Point(0.92f, 0.61f),
                Point(0.95f, 0.61f),
                Point(0.95f, 0.64f),
                Point(0.92f, 0.64f)
            ),
            regions = listOf(
                Region("tv_funa", "Atollo di Funafuti", "#FFF176", listOf(Point(0.35f, 0.45f), Point(0.75f, 0.45f), Point(0.70f, 0.75f), Point(0.32f, 0.75f))),
                Region("tv_north", "Atolli Settentrionali (Nanumea, Niutao, Nanumanga)", "#4FC3F7", listOf(Point(0.10f, 0.10f), Point(0.55f, 0.12f), Point(0.50f, 0.45f), Point(0.08f, 0.42f))),
                Region("tv_south", "Atolli Meridionali (Nui, Vaitupu, Nukufetau, Nukulaelae)", "#81C784", listOf(Point(0.40f, 0.55f), Point(0.85f, 0.58f), Point(0.80f, 0.92f), Point(0.38f, 0.90f)))
            )
        )
    )

    // Simplified polygon representation for continents to draw the Planisfero Map!
    // Each entry represents a continent/large region in the world map (normalized coordinates)
    val worldContinents = listOf(
        WorldShape("Nord America", "#81C784", listOf(Point(0.02f, 0.1f), Point(0.35f, 0.1f), Point(0.32f, 0.42f), Point(0.18f, 0.45f), Point(0.02f, 0.28f))),
        WorldShape("Sud America", "#4DD0E1", listOf(Point(0.22f, 0.46f), Point(0.38f, 0.48f), Point(0.32f, 0.88f), Point(0.25f, 0.92f), Point(0.18f, 0.62f))),
        WorldShape("Europa", "#FFF176", listOf(Point(0.39f, 0.15f), Point(0.62f, 0.16f), Point(0.58f, 0.42f), Point(0.38f, 0.41f))),
        WorldShape("Africa", "#FFB74D", listOf(Point(0.39f, 0.43f), Point(0.58f, 0.44f), Point(0.62f, 0.72f), Point(0.52f, 0.82f), Point(0.42f, 0.74f))),
        WorldShape("Asia", "#AED581", listOf(Point(0.63f, 0.12f), Point(0.95f, 0.15f), Point(0.92f, 0.52f), Point(0.59f, 0.48f))),
        WorldShape("Oceania", "#F06292", listOf(Point(0.76f, 0.64f), Point(0.95f, 0.65f), Point(0.92f, 0.88f), Point(0.74f, 0.84f)))
    )
}

data class WorldShape(
    val name: String,
    val colorHex: String,
    val points: List<Point>
)
