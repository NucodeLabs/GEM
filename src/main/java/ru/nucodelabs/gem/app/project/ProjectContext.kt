package ru.nucodelabs.gem.app.project

import javax.inject.Inject

/**
 * Доступ к текущему проекту, над которым ведется работа
 */
class ProjectContext<T> @Inject constructor(
    val project: Project<T>
)