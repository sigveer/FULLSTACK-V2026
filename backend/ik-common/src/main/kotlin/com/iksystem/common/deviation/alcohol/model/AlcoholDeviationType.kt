package com.iksystem.common.deviation.alcohol.model

/**
 * Types of alcohol-related deviations. Each type has an associated penalty-point value
 * that is applied when the report source is SJENKEKONTROLL or POLITIRAPPORT.
 */
enum class AlcoholDeviationType(val penaltyPoints: Int) {
    // 8 points
    SKJENKING_MINDREAARIGE(8),
    BRUDD_BISTANDSPLIKT(8),
    UFORSVARLIG_DRIFT(8),
    HINDRING_KONTROLL(8),

    // 4 points
    SKJENKING_APENBART_BERUSET(4),
    BRUDD_SJENKETIDER(4),
    BRENNEVIN_18_19(4),

    // 2 points
    BERUSET_PERSON_I_LOKALET(2),
    MANGLER_IK_SYSTEM(2),
    MANGLER_STYRER_STEDFORTREDER(2),
    NARKOTIKA(2),

    // 1 point
    ALKOHOLFRI_ALTERNATIV_MANGLER(1),
    MEDBRAKT_ALKOHOL(1),
    REKLAMEBRUDD(1),
    VILKAARSBRUDD(1),
}
