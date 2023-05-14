package ru.nucodelabs.gem.app.project

import javax.inject.Inject

class ProjectContext<T> @Inject constructor(
    val project: Project<T>
)