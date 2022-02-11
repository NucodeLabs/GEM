# Гайд-лайны по разработке программы

## MVVM

В проекте используется только MVVM паттерн(подробнее см. статьи в интернете).

## Как создавать модули

> [Подробнее про реализацию View и UserControl (docs.oracle.com)](https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm)

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
  class MainViewModel extends ViewModel
  ```
    - Должны наследовать `ViewModel`.
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

## Нюансы:

1. UserControl не должен иметь собственный ViewModel.
2. UserControl - композиция нескольких UserControl-ов и/или компонентов JavaFX.
3. View - целый экран, т.е. окно. Содержит внутри себя UserControl-ы и компоненты JavaFX.
4. Каждый View имеет обязательную ссылку на нужный ему ViewModel.
5. Одну ViewModel могут использовать несколько View во избежание дублирования кода.
6. ViewModel могут зависеть от произвольного количества разных моделей(поэтому в абстрактном классе отсутствует
   конструктор).
7. Стоит отметить, что в теле View(как мы помним, View является контроллером своего FXML-файла) обязан присутствовать
   только конструктор, в котором выполняются биндинги(привязки) к ViewModel. Методы для обработки событий тоже
   реализуются во ViewModel и также назначаются в конструкторе View(см. примеры).
8. Model никак не соотносятся с ViewModel, т.е. от одной Model могут зависеть несколько ViewModel.
9. Каждый ViewModel также имеет доступ к ViewManager, для смены или добавления других View. Доступ к View из ViewModel
   не нужен, в MVVM паттерне ViewModel не должен иметь зависимость от View.
10. Каждый View открывает свою ViewModel (`getViewModel()`), преимущественно для ViewManager. Сделано это для того,
    чтобы можно было передавать один и тот же экземпляр ViewModel в разные View, а также для вызова методов из ViewModel
    до появления View на экране(при переходе из одного View в другой).
11. ModelFactory инициализирует модели и раздает их.
12. ViewManager управляет окнами на экране.
13. ViewManager выполняет также управляет объектами ViewModel.
14. Возможно у вас возник вопрос: **почему View является собственным контроллером?** Сделано это для того, чтобы удобно
    использовать конструкторы, после того, как View становится контроллером, можно обращаться к полям `@FXML` прямо в
    конструкторе, и не имплементировать интерфейс `Initializable`.


