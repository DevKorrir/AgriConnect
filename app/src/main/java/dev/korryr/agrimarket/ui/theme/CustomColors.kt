package dev.korryr.agrimarket.ui.theme

// Extension values for custom colors not covered by Material3 ColorScheme
object AgriCustomColors {
    // Market-specific colors
    val priceUp = ProfitGreen
    val priceDown = LossRed
    val priceStable = StablePrice

    // Status indicators
    val success = SuccessGreen
    val warning = WarningAmber
    val info = InfoBlue

    // Feature-specific colors
    val water = SkyBlue
    val soil = FertileEarthBrown
    val crops = LightLeafGreen
    val harvest = HarvestOrange
    val premium = SeedPurple

    // UI element specific colors
    val disabledContent = DisabledGray
    val buttonHighlight = RipeCornYellow
    val cardBorder = SecondaryTextLight.copy(alpha = 0.2f)
    val chartLine = TealGreen
    val weatherIcon = SkyBlue
    val marketplaceIcon = HarvestOrange
    val educationIcon = SeedPurple
    val communityIcon = LightLeafGreen
    val profileIcon = FertileEarthBrown
}