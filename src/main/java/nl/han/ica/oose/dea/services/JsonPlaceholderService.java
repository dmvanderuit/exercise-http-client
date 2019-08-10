package nl.han.ica.oose.dea.services;

import nl.han.ica.oose.dea.services.dtos.TodoDto;
import nl.han.ica.oose.dea.services.mappers.TodoMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class JsonPlaceholderService {

    private static final String URL_TODOS = "https://jsonplaceholder.typicode.com/todos";

    private HttpClient client;
    private TodoMapper todoMapper;

    public JsonPlaceholderService() {
        client = HttpClient.newHttpClient();
        todoMapper = new TodoMapper();
    }

    public void getTodos() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL_TODOS))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(
                response -> {
                    System.out.println(response.body());
                });
    }

    public void getTodosWithCallback(Consumer<String> callback) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL_TODOS))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(
                response -> {
                    var body = response.body();
                    callback.accept(body);
                });
    }

    public void createNewTodoItemOnServer(Consumer<String> callback) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(URL_TODOS))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(
                response -> {
                    var json = response.body();
                    TodoDto[] todoDtos = todoMapper.mapToJava(json);
                    var length = todoDtos.length;
                    var newTodo = createNewTodoItem(length + 1);

                    sendPost(newTodo, callback);
                });
    }

    private void sendPost(TodoDto todoDto, Consumer<String> callback) {
        var todoJson = todoMapper.mapToJson(todoDto);
        var request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(todoJson))
                .uri(URI.create(URL_TODOS))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(
                response -> {
                    callback.accept(response.body());
                });
    }

    private TodoDto createNewTodoItem(int id) {
        var todo = new TodoDto();
        todo.setId(id);
        todo.setCompleted(false);
        todo.setTitle("Finish the DEA Exercise");
        todo.setUserId(2);
        return todo;
    }


}
