# java-explore-with-me
Template repository for ExploreWithMe project.

**Ссылка на pull request:** https://github.com/Alex-Trop/java-explore-with-me/pull/5

## Описание новых эндпоинтов и функциональности:
### 1) Private:
   - **добавление комментариев к *опубликованным* событиям:**
        POST users/{userId}/comments?eventId={eventId} (@RequestBody NewCommentRequest);
   - **просмотр собственных комментариев:**
        GET /users/{userId}/comments
        Список отсортирован по дате создания (от самых свежих до самых старых).   
        Если ничего не найдено, возвращает пустой список.
   - **удаление собственных комментариев:**
        DELETE /users/{userId}/comments/{commentId}
### 2) Public:
   - **получение всех комментариев к *опубликованному* событию (возможен поиск по тексту комментария):**
        GET /events/{eventId}/comments?text={text}
        Параметр text не обязателен, если его нет, возвращает все комментарии к событию.
        Список отсортирован по дате создания (от самых свежих до самых старых).
        Если ничего не найдено, возвращает пустой список.
### 3) Admin:
   - **Удаление комментариев:**
        DELETE admin/comments/{commentId}

2 DTO для сущности Comment (для создания комментария и в качестве возвращаемого объекта).
Новый DTO (EventCommentDto) для сущности Event, т.к. в имеющихся слишком много информации.