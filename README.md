# redis test case

* ## Проект собран c использованием maven (мультимодульный проект), Spring boot
* ## в проекте есть parent-module, и 3 подмодуля:
  * druggy - модуль с rest- контроллером, который привязан к end-point'у /send. Тоmcat на дефолтном 8080 порту. Этот модуль обрабатывает запрос и возвращает ответ вида Future<String> для ассинхронности. Сервис с основной бизнес логикой формирует DTO (Request) , массив байтов со случайными данными, кладет его в редис и отправляет сообщение с UUID на topic "send" для второго сервиса предварительно создав CompletableFuture и положив его в ConcurentHashMap поле. Сам сервис подписан на другой топик и ждет на нем DTO с сформированной подписью для проверки. При получении DTO (Response) сервис проверяет есть в его мапе ключ,забирает данные из redis после чего проводит процедуру проверки по результатам который в Rest-контроллер комплитится future.Объкт из redis удаляется. В случае успешной проверки пользователю возвращается reqquest: " key is correct". В случае какой-либо ошибки, информация о ней логируется, и кидается ResponseException, который mapped на HTTP 400 error rest - контроллера.
  * drauggy2 - второй модуль. Tomcat на 9090 порту. Этот модуль подписан на topic "send". При получении сообщения с UUID, он проверяет есть ли в redis такой объект, берет данные из redis, формирует подпись по приватном ключу и формирует DTO (Response с полями signature и UUID), который отправляет на topic "received", на который подписан первый сервис.
  * DTO - два класс SignatureRequest и SignatureResponse, который подключается через maven как <dependancy> в первый и второй модули.
* ## Пара приватный-публичный ключ сформирована заранее. Каждый ключ хранится в своем модуле в виде Base64.encoded строки. Блок генерации пары закооментирован и оставлен во втором модуле.
* ## все константы лежат в application.properties
* ## самое главное: оно работает)
