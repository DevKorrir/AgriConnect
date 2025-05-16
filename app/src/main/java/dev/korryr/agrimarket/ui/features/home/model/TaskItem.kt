package dev.korryr.agrimarket.ui.features.home.model

data class TaskItem(
    val name: String,
    val dueDate: String,
    val isUrgent: Boolean
)