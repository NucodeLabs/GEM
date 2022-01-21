# Гайд-лайны по разработке программы

> На данный момент не вся информация актуальна.

## MVVM

В проекте используется только MVVM паттерн(подробнее см. статьи в интернете).

## Как создавать модули

- ### UserControl
    - Должен лежать в `ru.nucodelabs.gem.view.usercontrols`
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
    - **Соглашение имен файлов обязательное.** (почему? см. `ru.nucodelabs.mvvm.VBUserControl.java`).
- ### View
    - Объекты View представляют собой такую же связку, что и объекты UserControl, но следует приписывать суффикс `*View`
      в конце имен.
    - **Соглашение имен файлов тоже обязательное.**
    - По аналогии с UserControl
  ```java
  class MainSplitLayoutView extends VBView<MainViewModel>
  ```
    - Должны наследовать `*View<VM extends ViewModel>`, где параметр типа `VM` - класс используемой ViewModel.
- ### ViewModel
    - Следует приписывать суффикс `*ViewModel` к имени.
    - Объекты ViewModel представляют собой следующее:
  ```java
  class MainViewModel extends ViewModel<VESDataModel>
  ```
    - Должны наследовать `ViewModel<M extends Model>`, где параметр типа `M` - интерфейс, реализуемый используемой
      моделью.
- ### Model
    - Следует приписывать суффикс `*Model` к имени интерфейса модели.
    - Сначала надо написать интерфейс модели вида
    ```java
    interface VESDataModel extends Model
    ```
    - Затем его имплементацию.
    ```java
    class VESDataManager implements VESDataModel
    ```

## Архитектурные моменты:

- UserControl не должен иметь ViewModel.
- UserControl - композиция нескольких UserControl-ов и/или компонентов JavaFX.
- View - целый экран, окно. Содержит внутри себя UserControl-ы и компоненты JavaFX.
- Каждый View имеет обязательную зависимость от ViewModel.
- Одну ViewModel могут использовать несколько View.
- Каждый ViewModel имеет зависимость от Model.
- Допустимы ViewModel, не имеющие зависимости от Model, тогда в конструктор передается `null` и ViewModel выполняет роль
  контролера(см. следующий пункт).
- Стоит отметить, что в теле View(как мы помним, View является контроллером своего FXML-файла) обязан присутствовать
  только конструктор, в котором выполняются биндинги(привязки) к ViewModel. Методы для обработки событий тоже
  реализуются во ViewModel и также назначаются в конструкторе View.
- Model никак не соотносятся с ViewModel, т.е. от одной Model могут зависеть несколько ViewModel.
- Сложные Model состоящие из нескольких подсистем допустимы, для простоты использования в ViewModel. Но подсистемы
  следует разбивать на отдельные классы, как и всегда.
- Каждый ViewModel также имеет доступ к ViewManager, для смены или добавления других View. Поэтому доступ к View не
  нужен, в MVVM паттерне ViewModel не должен иметь зависимость от View.
- Каждый View открывает свою ViewModel (`getViewModel()`), преимущественно для ViewManager. Сделано это для того, чтобы
  можно было передавать один и тот же экземпляр ViewModel в разные View, а также для вызова методов из ViewModel до
  появления View на экране(при переходе из одного View в другой).
- Инжект зависимостей происходит в конструкторах.
- ModelFactory инициализирует модели и раздает их геттерами.
- ViewManager имеет зависимость от ModelFactory.
- ViewManager управляет показом View на экране.
- ViewManager выполняет также функцию ViewModelFactory по причине тесной связанности View с ViewModel.
- В итоге тело программы выглядит следующим образом:

```java
public class GemApplication extends javafx.application.Application {
    @Override
    public void start(Stage stage) {
        ModelFactory modelFactory = new ModelFactory();
        ViewManager viewManager = new ViewManager(modelFactory, stage);

        viewManager.start();
    }
}
```

- Возможно у вас возник вопрос: почему View является собственным контроллером? Сделано это для того, чтобы удобно
  использовать конструкторы, после того как View становится контроллером, можно обращаться к полям `@FXML` прямо в
  конструкторе, и не имплементировать интерфейс `Initializable`.
