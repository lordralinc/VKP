package dev.idm.vkp.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.fragment.Command;
import dev.idm.vkp.mvp.presenter.base.RxSupportPresenter;
import dev.idm.vkp.mvp.view.IChatCommandsView;
import dev.idm.vkp.util.Utils;


public class ChatCommandsPresenter extends RxSupportPresenter<IChatCommandsView> {
    private boolean refreshing;
    ArrayList<Command> commands;
    List<Command> found;
    private String query = "";

    public ChatCommandsPresenter(@Nullable Bundle savedInstanceState) {
        super(savedInstanceState);
        commands = new ArrayList<>();

        commands.add(new Command("инфо", "инфо ", "Покажет информацию о пользователе"));
        commands.add(new Command("Информация о дежурном", "дежинфо ", "Покажет информацию о дежурном"));
        commands.add(new Command("Чат", "чат ", "Информация о чате"));
        commands.add(new Command("Помощь", "помощь ", "Покажет помощь"));
        commands.add(new Command("Пинг", "пинг ", "Деж ответит Понг/Конг/Пау/На месте. Если указан параметр «подробно», деж отобразит время обработки и ответа. Полезно для проверки работоспособности и задержки"));
        commands.add(new Command("Ачивки", "ачивки ", "Просмотр ачивок пользователя"));
        commands.add(new Command("Проверка токенов", "токены ", "Проверит токены на валидность"));
        commands.add(new Command("Благодарности", "благодарности ", "Спасибо всем тем, кто помог IDM"));
        commands.add(new Command("Информация о сообщении", "смсинфо ", "Покажет информацию о сообщении. Полезно разработчикам"));
        commands.add(new Command("Ник пользователя", "ник\n", "Изменит имя пользователя в профиле IDM"));
        commands.add(new Command("Описание пользователя", "!описание\n ", "Изменит описание пользователя в профиле IDM"));
        commands.add(new Command("Изменение имени VK", "имявк ", "Изменит имя и фамилию"));
        commands.add(new Command("Обновление профиля IDM", "обновить ", "Обновит информацию о пользователе и дежурных"));
        commands.add(new Command("Статистика", "моястата ", "Показывает статистику использования дежурного за последние 3 дня"));


        commands.add(new Command("шаблон", "шаб ", "вызывает шаблон"));
        commands.add(new Command("добавить шаблон", "+шаб ", "создает новый шаблон"));
        commands.add(new Command("удалить шаблон", "-шаб ", "удаляет шаблон"));
        commands.add(new Command("редактировать шаблон", "~шаб ", "редактирует шаблон"));

        found = new ArrayList<>();
        found.addAll(commands);
    }

    public void updateCriteria() {

        found.clear();

        if (query == null ||  query.isEmpty()){
            found.addAll(commands);
        } else {
            for (Command i: commands){
                if (i == null) {
                    continue;
                }
                if (i.name.toLowerCase().contains(query.toLowerCase())){
                    found.add(i);
                }
            }
        }
        callView(IChatCommandsView::notifyDataSetChanged);
    }

    public void fireQuery(String q) {
        if (Utils.isEmpty(q))
            query = null;
        else {
            query = q;
        }
        updateCriteria();
    }


    public void setLoadingNow(boolean loadingNow) {
        refreshing = loadingNow;
        resolveRefreshing();
    }

    @Override
    public void onGuiCreated(@NonNull IChatCommandsView view) {
        super.onGuiCreated(view);
        view.displayData(found);
    }

    private void resolveRefreshing() {
        if (isGuiResumed()) {
            getView().displayRefreshing(refreshing);
        }
    }

    public void fireCommandClick(Command command){
        getView().openCommand(command);
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshing();
    }

}
