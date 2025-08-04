package ru.nucodelabs.gem.app.project

import jakarta.inject.Inject

/**
 * Доступ к текущему проекту, над которым ведется работа
 */
class ProjectContext<T> @Inject constructor(
    val project: Project<T>
)