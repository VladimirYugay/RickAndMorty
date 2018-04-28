package vladimir.yandex.database;

import java.util.List;

import vladimir.yandex.entity.Result;

public interface IDatabaseHandler {
    public void addCharacter(Result result);
    public Result getCharacter(int id);
    public List<Result> getAllCharacters();
    public int getCharactersCount();
    public void deleteAll();
}
