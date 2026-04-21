package com.kma.lamphoun.roomapp.ui.theme

import androidx.compose.ui.graphics.Color

// === Stitch Design Tokens — "The Architectural Concierge" ===
// Primary Teal (#006b5f)
val Primary = Color(0xFF006B5F)
val PrimaryContainer = Color(0xFF38A596)
val OnPrimary = Color(0xFFFFFFFF)
val OnPrimaryContainer = Color(0xFF00342E)
val PrimaryFixed = Color(0xFF8DF5E4)
val PrimaryFixedDim = Color(0xFF70D8C8)

// Secondary Teal (#006a63)
val Secondary = Color(0xFF006A63)
val SecondaryContainer = Color(0xFF8BF1E6)
val OnSecondary = Color(0xFFFFFFFF)
val OnSecondaryContainer = Color(0xFF006F67)

// Tertiary — Warm Orange/Terracotta (urgent alerts only)
val Tertiary = Color(0xFF97472A)
val TertiaryContainer = Color(0xFFDA7C5A)
val OnTertiary = Color(0xFFFFFFFF)
val OnTertiaryContainer = Color(0xFF571800)

// Error
val Error = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)
val OnError = Color(0xFFFFFFFF)

// Background & Surface hierarchy
val Background = Color(0xFFF7FAFA)           // surface
val SurfaceContainerLow = Color(0xFFF1F4F4)  // page background
val SurfaceContainerLowest = Color(0xFFFFFFFF) // cards / content
val SurfaceContainer = Color(0xFFEBEEEE)     // sidebar / nav regions
val SurfaceContainerHigh = Color(0xFFE6E9E9)
val SurfaceContainerHighest = Color(0xFFE0E3E3)
val SurfaceDim = Color(0xFFD7DBDB)

// Keep legacy alias for backward compat
val Surface = SurfaceContainerLowest
val SurfaceVariant = SurfaceContainerHighest

val OnBackground = Color(0xFF181C1D)
val OnSurface = Color(0xFF181C1D)
val OnSurfaceVariant = Color(0xFF3D4947)
val Outline = Color(0xFF6D7A77)
val OutlineVariant = Color(0xFFBCC9C6)

// Gradient colors (Signature Gradient: 135deg primary → primary_container)
val GradientStart = Color(0xFF006B5F)
val GradientEnd = Color(0xFF38A596)

// Status colors — using Stitch tonal palette
val StatusAvailable = Color(0xFF006B5F)   // primary teal = available/paid
val StatusOccupied = Color(0xFF97472A)    // tertiary = occupied/warning
val StatusMaintenance = Color(0xFF6A1B9A) // purple = maintenance
val StatusPaid = Color(0xFF006B5F)
val StatusUnpaid = Color(0xFF97472A)
val StatusOverdue = Color(0xFFBA1A1A)

