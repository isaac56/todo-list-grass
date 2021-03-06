package team9.todo.domain.DTO.History;

import team9.todo.domain.HistoryWithCardTitle;
import team9.todo.domain.enums.CardColumn;
import team9.todo.domain.enums.HistoryAction;

import java.time.LocalDateTime;

public class ResponseDTO {
    private final String cardTitle;
    private final int action;
    private final LocalDateTime date;
    private final Integer from;
    private final Integer to;

    private ResponseDTO(String cardTitle, HistoryAction action, LocalDateTime date, CardColumn from, CardColumn to) {
        this.cardTitle = cardTitle;
        this.action = action.ordinal();
        this.date = date;
        this.from = from != null ? from.ordinal() : null;
        this.to = to != null ? to.ordinal() : null;
    }

    public static ResponseDTO of(HistoryWithCardTitle entity) {
        return new ResponseDTO(entity.getCardTitle(), entity.getAction(), entity.getDate(), entity.getFrom(), entity.getTo());
    }


    public String getCardTitle() {
        return cardTitle;
    }

    public int getAction() {
        return action;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }
}
