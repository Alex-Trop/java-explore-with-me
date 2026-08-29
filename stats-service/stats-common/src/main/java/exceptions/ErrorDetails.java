package exceptions;

public class ErrorDetails {
    public static final String APP_ERROR = "Необходимо указать идентификатор сервиса";
    public static final String APP_NOT_FOUND_ERROR = "Идентификатор сервиса указан некорректно";
    public static final String URI_ERROR = "Необходимо указать URI запроса";
    public static final String IP_ERROR = "Необходимо указать IP пользователя, отправившего запрос";
    public static final String TIME_ERROR = "Необходимо указать время создания запроса";
    public static final String TIME_FORMAT_ERROR = "Некорректный формат времени";
    public static final String HIT_DUPLICATE_ERROR = "Сведения о запросе уже внесены";
}
