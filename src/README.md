# Гайд-лайны по разработке программы

### SectionManager

За бизнес-логику отвечает `SectionManager`, все изменения модели данных проходят в нем.

#### Flow API - Observable

Так же он предоставляет возможность получать уведомления об изменениях, имплементируя
`Flow.Publisher<Section>`.

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

Используется Binding API, чтобы все эти объекты содержали актуальную информацию. Изменения в `ObservableList<Picket>` не
имеют смысла, т.к. он зависит от состояния данных в `StorageManager`, но не наоборот.

Также используется DI framework **Guice**, имеет хорошую документацию на GitHub.

### UserControl

- Должен лежать в `ru.nucodelabs.gem.view.control`
    - Представляет собой связку
        - `SampleControl.java`
        - `SampleControl.fxml`
        - `SampleControl.css`(опционально)
    - `SampleControl.java` и `SampleControl.fxml`+ `SampleControl.css` обязаны лежать в одинаковых пакетах(`*.fxml`
      и `*css` в папке `resources`)
    - `SampleControl.fxml`
        - Корневой узел задается следующий образом(пример для `VBox`):
      ```xml
      <fx:root type="javafx.scene.layout.VBox"> 
      ... 
      </fx:root>
      ```
        - При этом аттрибут `fx:contoller` удаляется, т.к. мы назначаем контроллер позже в
          конструкторе `SampleControl.java`.
    - `SampleControl.java`
        - Должен наследовать абстрактный класс `*UserControl`, где `*` - обозначение корневого узла в
          структуре `SampleControl.fxml`. Например, `VESCurves.fxml`, корневой узел
          `VBox`(`fx:root type="javafx.scene.layout.VBox"`), следовательно
          ```java 
          abstract class VBUserControl extends VBox  
          class VESCurves extends VBUserControl
          ```
            - После вызова базового конструктора (`super();`) объект становится контроллером для своего FXML-файла.
              Поэтому дополнительную настройку элементов можно производить в конструкторе сразу после вызова `super();`(
              см. `MainMenuBar.java`).
    - **Соглашение имен файлов обязательное.** (почему? см. `ru.nucodelabs.gem.view.control.VBUserControl.java`).
  > [Подробнее про реализацию UserControl (docs.oracle.com)](https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm)
