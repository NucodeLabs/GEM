# Гайд-лайны по разработке программы

### Domain Model Data

Данные предметной области представляют record'ы из пакета `ru.nucodelabs.data.ves`, они сериализуемые, имеют аннотации
для jackson.

#### Validation API

Также эти record'ы имеют аннотации Bean Validation API, поэтому валидацию данных в проекте следует делать, используя
объект Validator.

### StorageManager

Это абстракция связывающая работу с файлами с объектами предметной области. В нем поддерживается актуальное, сохраненное
на диске состояние Section.

### HistoryManager

Объект, реализующий логику отмены действий (Undo/Redo), реализован по Memento(Snapshot)-паттерну.

## Controllers

### ObservableDataModule

Предоставляет удобные JavaFX Observable-адаптеры для контроллеров.

- `IntegerProperty (picketIndex)` - индекс текущего отображаемого пикета.
- `ObservableList<Picket>` - список пикетов разреза
- `ObservableObjectValue<Picket>` - пикет соответствующий текущему индексу

Также используется DI framework **Guice**, имеет хорошую документацию на GitHub.